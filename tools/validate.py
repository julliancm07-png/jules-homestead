#!/usr/bin/env python3
"""Validate the pinned pack.

Checks
 1. Lockfile integrity: every manifest entry resolved; downloads match recorded sha512 (jar cache in build/.cache/jars).
 2. Jar-level dependency resolution: evaluates every fabric.mod.json `depends` and `breaks` (including nested
    jar-in-jar mods and `provides`) against the *client* set and, separately, the *dedicated-server* set
    (client-only mods removed), with minecraft/java/fabricloader built-ins.
 3. Duplicate mod ids across top-level jars; environment ("client"-only jars) vs. assigned side.
 4. Pack content: JSON syntax of configs/ and datapacks/, tag/recipe references, quest SNBT references
    (delegated to tools/check_content.py when present).
Writes build/reports/validation.json and exits non-zero on errors.
"""
import hashlib
import json
import pathlib
import subprocess
import sys
import tomllib
import urllib.request

sys.path.insert(0, str(pathlib.Path(__file__).resolve().parent))
from fabricmeta import predicate_matches, read_mod_jsons, Version  # noqa: E402

ROOT = pathlib.Path(__file__).resolve().parent.parent
LOCK = ROOT / "pack" / "mods.lock.json"
CACHE = ROOT / "build" / ".cache" / "jars"
REPORT = ROOT / "build" / "reports" / "validation.json"
UA = {"User-Agent": "jules-homestead-modpack-validator/0.1"}
BUILTINS = {"minecraft": "26.1.2", "java": "25", "fabricloader": "0.20.1", "fabric-loader": "0.20.1", "mixinextras": "0.5.0"}


RESOURCE_PACK_FORMAT = 84  # Minecraft 26.1.2 (see tools/gen_resources.py PACK_FORMAT)


def _fmt_major(v):
    return v[0] if isinstance(v, list) else int(v)


def pack_format_range(zip_bytes):
    """(min, max) major resource-pack format a zip declares in pack.mcmeta, or None."""
    import io
    import zipfile
    try:
        with zipfile.ZipFile(io.BytesIO(zip_bytes)) as z:
            name = next((n for n in z.namelist() if n.rstrip("/").split("/")[-1] == "pack.mcmeta" and n.count("/") <= 1), None)
            if name is None:
                return None
            pack = json.loads(z.read(name).decode("utf-8-sig"))["pack"]
    except Exception:
        return None
    if "min_format" in pack or "max_format" in pack:
        lo = _fmt_major(pack.get("min_format", pack.get("pack_format", 0)))
        hi = _fmt_major(pack.get("max_format", pack.get("pack_format", 10**6)))
        return lo, hi
    # Legacy fields only (no min_format/max_format): 26.1 ignores a legacy range above format 64, so such packs are
    # "no longer compatible" (seen in the client log: Shelviety, pack_format 15 + supported_formats 1..99, was removed).
    LEGACY_MAX = 64
    sf = pack.get("supported_formats")
    if isinstance(sf, int):
        lo, hi = sf, sf
    elif isinstance(sf, list) and len(sf) == 2:
        lo, hi = int(sf[0]), int(sf[1])
    elif isinstance(sf, dict):
        lo, hi = int(sf.get("min_inclusive", 0)), int(sf.get("max_inclusive", 10**6))
    else:
        pf = pack.get("pack_format")
        if pf is None:
            return None
        lo, hi = int(pf), int(pf)
    return lo, min(hi, LEGACY_MAX)


def fetch(entry):
    CACHE.mkdir(parents=True, exist_ok=True)
    p = CACHE / f"{entry['hashes']['sha1']}-{entry['filename']}"
    if not p.exists():
        req = urllib.request.Request(entry["url"], headers=UA)
        with urllib.request.urlopen(req, timeout=300) as r:
            data = r.read()
        p.write_bytes(data)
    data = p.read_bytes()
    if hashlib.sha512(data).hexdigest() != entry["hashes"]["sha512"]:
        raise SystemExit(f"HASH MISMATCH {entry['slug']} {entry['url']}")
    return data


def local_jar(entry):
    libs = ROOT / entry["path"] / "build" / "libs"
    # newest build wins (a stale jar from an older version number must never be packaged)
    jars = sorted((j for j in libs.glob("*.jar") if not j.name.endswith("-sources.jar")), key=lambda j: j.stat().st_mtime) if libs.exists() else []
    return jars[-1].read_bytes() if jars else None


def server_capable(entry, metas):
    if entry.get("side_override") == "client":
        return False
    if entry.get("side_override") in ("server", "both"):
        return True
    if entry["server_side"] == "unsupported":
        return False
    if metas and metas[0][0].get("environment") == "client":
        return False
    return True


def check_set(name, entries, metas_by_slug):
    errors, warnings = [], []
    provided = dict(BUILTINS)
    owners = {}
    for e in entries:
        for meta, path in metas_by_slug.get(e["slug"], []):
            ids = [meta["id"]] + list(meta.get("provides", []) or [])
            for i in ids:
                ver = meta.get("version", "0")
                if i not in provided or Version(ver).cmp(Version(provided[i])) > 0:
                    provided[i] = ver
                if path == "<root>":
                    owners.setdefault(i, []).append(e["slug"])
    for i, slugs in owners.items():
        if len(slugs) > 1:
            errors.append(f"[{name}] duplicate top-level mod id '{i}' in {slugs}")
    for e in entries:
        for meta, path in metas_by_slug.get(e["slug"], []):
            env = meta.get("environment", "*")
            if name == "server" and env == "client":
                continue  # nested client-only libs are skipped by the loader on servers
            for dep, pred in (meta.get("depends") or {}).items():
                if dep not in provided:
                    errors.append(f"[{name}] {e['slug']} ({meta['id']}{'' if path == '<root>' else ' in ' + path}) requires '{dep}' {pred} — missing")
                elif not predicate_matches(pred, provided[dep]):
                    errors.append(f"[{name}] {e['slug']} ({meta['id']}) requires {dep} {pred}, have {provided[dep]}")
            for dep, pred in (meta.get("breaks") or {}).items():
                if dep in provided and dep not in BUILTINS and predicate_matches(pred, provided[dep]):
                    errors.append(f"[{name}] {e['slug']} ({meta['id']}) BREAKS with {dep} {pred} (have {provided[dep]})")
    return errors, warnings, provided


def main():
    lock = json.loads(LOCK.read_text())
    man = tomllib.loads((ROOT / "pack" / "manifest.toml").read_text())
    errors, warnings = [], list(lock.get("warnings", []))
    slugs = {e["slug"] for e in lock["files"]}
    for key in ("mod", "resourcepack", "shader"):
        for m in man.get(key, []):
            if m["slug"] not in slugs:
                errors.append(f"manifest entry {m['slug']} missing from lockfile — run tools/resolve.py")
    metas, mods = {}, [e for e in lock["files"] if e["kind"] == "mod"]
    for e in mods:
        data = local_jar(e) if e["source"] == "local" else fetch(e)
        if data is None:
            errors.append(f"local mod {e['slug']} not built (run gradle build in {e['path']})")
            continue
        metas[e["slug"]] = read_mod_jsons(data)
        if not metas[e["slug"]]:
            errors.append(f"{e['slug']}: no fabric.mod.json in jar")
    for e in lock["files"]:
        if e["kind"] != "mod" and e["source"] == "modrinth":
            data = fetch(e)
            if e["kind"] == "resourcepack":
                rng = pack_format_range(data)
                if rng is None:
                    warnings.append(f"{e['slug']}: no readable pack.mcmeta")
                elif not (rng[0] <= RESOURCE_PACK_FORMAT <= rng[1]):
                    warnings.append(f"{e['slug']}: declares resource pack formats {rng[0]}..{rng[1]}, game uses {RESOURCE_PACK_FORMAT}"
                                    " — 26.1 treats it as incompatible and drops it from the enabled packs")
    # Optional mods (e.g. Create Fly, Distant Horizons) must not be required by anything else.
    core = [e for e in mods if not e.get("optional")]
    for setname, entries in (("client-all", mods), ("client-core", core),
                             ("server", [e for e in core if server_capable(e, metas.get(e["slug"]))])):
        er, wa, _ = check_set(setname.split("-")[0] if setname == "server" else setname, entries, metas)
        errors += er
        warnings += wa
    for e in mods:
        if e["source"] == "modrinth" and e["client_side"] == "unsupported":
            warnings.append(f"{e['slug']}: Modrinth marks client 'unsupported' — still installed on clients (needed by the singleplayer integrated server)")
    content = ROOT / "tools" / "check_content.py"
    if content.exists():
        r = subprocess.run([sys.executable, "-I", str(content)], capture_output=True, text=True)
        print(r.stdout, end="")
        if r.returncode != 0:
            errors.append("content checks failed (see output above)")
    REPORT.parent.mkdir(parents=True, exist_ok=True)
    REPORT.write_text(json.dumps({"errors": errors, "warnings": warnings,
                                  "mods": {s: [m[0]["id"] + "@" + str(m[0].get("version")) for m in v] for s, v in metas.items()}}, indent=1))
    for w in warnings:
        print("WARN ", w)
    for x in errors:
        print("ERROR", x)
    print(f"\n{len(mods)} mod jars checked; {len(errors)} errors, {len(warnings)} warnings. Report: {REPORT.relative_to(ROOT)}")
    sys.exit(1 if errors else 0)


if __name__ == "__main__":
    main()
