#!/usr/bin/env python3
"""Resolve pack/manifest.toml into pack/mods.lock.json.

* Modrinth entries: exact version for the pack's Minecraft version + loader, chosen as the pinned
  `version` (version_number) or the newest release (falling back to beta/alpha, recorded).
  Required dependencies are resolved recursively and added with `required_by`.
* ftb-maven entries: FTB's official maven (maven.ftb.dev); sha1/sha512 from the published checksum files.
* local entries: built from custom-mods/<slug> by tools/package.py.

Only official sources are contacted: api.modrinth.com and maven.ftb.dev.
Usage: python3 -I tools/resolve.py [--refresh]   (--refresh ignores existing pins in the lockfile)
"""
import datetime
import json
import pathlib
import sys
import time
import tomllib
import urllib.error
import urllib.parse
import urllib.request

ROOT = pathlib.Path(__file__).resolve().parent.parent
MANIFEST = ROOT / "pack" / "manifest.toml"
LOCK = ROOT / "pack" / "mods.lock.json"
API = "https://api.modrinth.com/v2"
FTB_MAVEN = "https://maven.ftb.dev/releases"
UA = {"User-Agent": "jules-homestead-modpack-resolver/0.1 (github.com/julliancm07-png/jules-homestead)"}
TYPE_RANK = {"release": 0, "beta": 1, "alpha": 2}
LOADERS = {"mod": ["fabric"], "resourcepack": ["minecraft"], "shader": ["iris"]}


def http(url, method="GET"):
    for attempt in range(6):
        try:
            req = urllib.request.Request(url, headers=UA, method=method)
            with urllib.request.urlopen(req, timeout=90) as r:
                return r.read() if method == "GET" else dict(r.headers)
        except urllib.error.HTTPError as e:
            if e.code == 404:
                return None
            if e.code == 429:
                time.sleep(15)
                continue
            if attempt == 5:
                raise
        except (urllib.error.URLError, TimeoutError):
            if attempt == 5:
                raise
        time.sleep(2 ** attempt)


def api(path, **params):
    q = ("?" + urllib.parse.urlencode({k: json.dumps(v) if isinstance(v, list) else v for k, v in params.items()})) if params else ""
    data = http(API + path + q)
    return None if data is None else json.loads(data)


class Resolver:
    def __init__(self, pack, old_lock):
        self.mc = pack["minecraft"]
        self.loader = pack["loader"]
        self.entries = {}          # project_id -> entry
        self.slug_to_pid = {}
        self.old = {e.get("project_id"): e for e in old_lock.get("files", []) if e.get("project_id")}
        self.warnings = []
        self.project_cache = {}

    def project(self, ident):
        if ident not in self.project_cache:
            p = api(f"/project/{ident}")
            if p is None:
                raise SystemExit(f"Modrinth project not found: {ident}")
            self.project_cache[ident] = p
            self.project_cache[p["id"]] = p
        return self.project_cache[ident]

    def pick_version(self, p, kind, pin=None, version_id=None):
        if version_id:
            v = api(f"/version/{version_id}")
            if v and (self.mc in v["game_versions"]):
                return v
            self.warnings.append(f"{p['slug']}: dependency pinned version {version_id} does not list {self.mc}; picking newest compatible")
        loaders = LOADERS[kind]
        vers = api(f"/project/{p['id']}/version", loaders=loaders, game_versions=[self.mc]) or []
        if pin:
            hits = [v for v in vers if v["version_number"] == pin]
            if not hits:
                raise SystemExit(f"{p['slug']}: pinned version {pin} not found for {self.mc}/{loaders}")
            return hits[0]
        if not vers:
            raise SystemExit(f"{p['slug']}: no {loaders} version for Minecraft {self.mc}")
        vers.sort(key=lambda v: (TYPE_RANK.get(v["version_type"], 3), v["date_published"]), reverse=False)
        best_rank = min(TYPE_RANK.get(v["version_type"], 3) for v in vers)
        cands = [v for v in vers if TYPE_RANK.get(v["version_type"], 3) == best_rank]
        v = max(cands, key=lambda v: v["date_published"])
        if v["version_type"] != "release":
            self.warnings.append(f"{p['slug']}: newest available build for {self.mc} is {v['version_type']} ({v['version_number']})")
        return v

    def add_modrinth(self, slug, kind="mod", meta=None, required_by=None, version_id=None):
        p = self.project(slug)
        pid = p["id"]
        if pid in self.entries:
            if required_by and required_by not in self.entries[pid]["required_by"]:
                self.entries[pid]["required_by"].append(required_by)
            if meta:  # explicit manifest entry overrides an earlier dependency-only entry
                self.entries[pid].update({k: v for k, v in meta.items() if k in ("group", "purpose", "optional")})
                if "side" in meta:
                    self.entries[pid]["side_override"] = meta["side"]
                self.entries[pid]["explicit"] = True
            return
        meta = meta or {}
        pin = meta.get("version")
        if not pin and pid in self.old and not REFRESH:
            pin = self.old[pid]["version_number"]
        try:
            v = self.pick_version(p, kind, pin=pin, version_id=version_id)
        except SystemExit:
            if pin and not meta.get("version"):  # stale lock pin; fall back to fresh resolution
                v = self.pick_version(p, kind, version_id=version_id)
            else:
                raise
        f = next((f for f in v["files"] if f["primary"]), v["files"][0])
        entry = {
            "slug": p["slug"], "title": p["title"], "kind": kind, "source": "modrinth",
            "project_id": pid, "version_id": v["id"], "version_number": v["version_number"],
            "version_type": v["version_type"], "date_published": v["date_published"],
            "game_versions": v["game_versions"], "loaders": v["loaders"],
            "filename": f["filename"], "url": f["url"], "size": f["size"],
            "hashes": {"sha1": f["hashes"]["sha1"], "sha512": f["hashes"]["sha512"]},
            "client_side": p["client_side"], "server_side": p["server_side"],
            "license": p["license"]["id"], "license_url": p["license"].get("url"),
            "source_url": p.get("source_url"), "issues_url": p.get("issues_url"),
            "page": f"https://modrinth.com/{p['project_type']}/{p['slug']}",
            "dependencies": [d for d in v["dependencies"] if d["dependency_type"] in ("required", "optional", "incompatible", "embedded")],
            "group": meta.get("group", "library"), "purpose": meta.get("purpose", "Dependency."),
            "optional": bool(meta.get("optional", False)), "side_override": meta.get("side"),
            "explicit": bool(meta), "required_by": [required_by] if required_by else [],
        }
        self.entries[pid] = entry
        for d in v["dependencies"]:
            if d["dependency_type"] == "required" and d.get("project_id"):
                try:
                    self.add_modrinth(d["project_id"], kind="mod", required_by=p["slug"], version_id=d.get("version_id"))
                except SystemExit as e:
                    # Modrinth dependency metadata is sometimes wrong (e.g. a NeoForge-only library listed on a Fabric
                    # build). Record it; tools/validate.py checks the real fabric.mod.json requirements of every jar.
                    self.warnings.append(f"{p['slug']}: declared required dependency unresolvable ({e}); verify via jar metadata")

    def add_ftb(self, m):
        group, artifact = m["artifact"].split(":")
        ver = m["version"]
        base = f"{FTB_MAVEN}/{group.replace('.', '/')}/{artifact}/{ver}/{artifact}-{ver}.jar"
        sha1 = http(base + ".sha1").decode().split()[0]
        sha512 = http(base + ".sha512").decode().split()[0]
        size = int(http(base, method="HEAD").get("Content-Length", 0))
        self.entries["ftb:" + artifact] = {
            "slug": m["slug"], "title": artifact, "kind": "mod", "source": "ftb-maven",
            "project_id": None, "version_id": None, "version_number": ver, "version_type": "release",
            "filename": f"{artifact}-{ver}.jar", "url": base, "size": size,
            "hashes": {"sha1": sha1, "sha512": sha512},
            "client_side": "required", "server_side": "required",
            "license": "LicenseRef-All-Rights-Reserved", "license_url": None,
            "source_url": "https://github.com/FTBTeam", "page": f"https://www.curseforge.com/minecraft/mc-mods/{artifact}",
            "dependencies": [], "group": m.get("group"), "purpose": m.get("purpose"), "optional": False,
            "side_override": m.get("side"), "explicit": True, "required_by": [],
            "distribution": "Referenced from FTB's official maven; not re-hosted (All Rights Reserved).",
        }

    def add_local(self, m):
        self.entries["local:" + m["slug"]] = {
            "slug": m["slug"], "title": m["slug"], "kind": "mod", "source": "local", "project_id": None,
            "version_id": None, "version_number": None, "filename": None, "url": None, "hashes": {},
            "client_side": "required", "server_side": "required", "license": "MIT",
            "group": m.get("group"), "purpose": m.get("purpose"), "optional": False,
            "side_override": m.get("side"), "explicit": True, "required_by": [], "dependencies": [],
            "path": m.get("path", f"custom-mods/{m['slug']}"),
        }


REFRESH = "--refresh" in sys.argv


def main():
    man = tomllib.loads(MANIFEST.read_text())
    old = json.loads(LOCK.read_text()) if LOCK.exists() else {}
    r = Resolver(man["pack"], old)
    for kind, key in (("mod", "mod"), ("resourcepack", "resourcepack"), ("shader", "shader")):
        for m in man.get(key, []):
            src = m.get("source", "modrinth")
            print(f"resolving {kind}:{m['slug']} ({src})", flush=True)
            if src == "modrinth":
                r.add_modrinth(m["slug"], kind=kind, meta=m)
            elif src == "ftb-maven":
                r.add_ftb(m)
            elif src == "local":
                r.add_local(m)
    files = sorted(r.entries.values(), key=lambda e: (e["kind"], e["slug"]))
    out = {
        "pack": man["pack"], "resolved_at": datetime.datetime.now(datetime.timezone.utc).isoformat(timespec="seconds"),
        "sources": [API, FTB_MAVEN], "warnings": r.warnings, "files": files,
    }
    LOCK.write_text(json.dumps(out, indent=1, ensure_ascii=False) + "\n")
    print(f"\n{len(files)} files ({sum(1 for f in files if not f['explicit'])} dependencies). Warnings:")
    for w in r.warnings:
        print("  -", w)


if __name__ == "__main__":
    main()
