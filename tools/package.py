#!/usr/bin/env python3
"""Package Jules' Homestead.

Outputs (build/dist/):
  JulesHomestead-<ver>.mrpack   Prism/Modrinth-importable pack. Mods are referenced by official download URLs with
                                SHA-1/SHA-512 (Modrinth CDN; FTB's official maven for FTB Quests/Library/Teams/XMod,
                                which Prism confirms once as "untrusted source"). Only our own MIT modules
                                (homestead-core, homestead-pack-data) and configs are bundled in overrides/.
  JulesHomestead-<ver>-server-files.json   server file list (same pins) for tools/install_server.py

mrpack env mapping:
  client: "optional" for manifest optional entries (Create Fly, Distant Horizons, shaders), else "required".
  server: "unsupported" for client-only mods (validate.server_capable == False), resource packs and shaders.
Usage: python3 -I tools/package.py [--skip-build]
"""
import hashlib
import json
import pathlib
import sys
import tomllib
import zipfile

sys.path.insert(0, str(pathlib.Path(__file__).resolve().parent))
from fabricmeta import read_mod_jsons  # noqa: E402
import validate  # noqa: E402

ROOT = pathlib.Path(__file__).resolve().parent.parent
DIST = ROOT / "build" / "dist"
ALLOWED_HOSTS = ("https://cdn.modrinth.com/", "https://maven.ftb.dev/")
FIXED_DATE = (2026, 1, 1, 0, 0, 0)

OPTIONS_TXT = """version:4671
renderDistance:10
simulationDistance:8
guiScale:3
fov:0.0
maxFps:120
graphicsMode:1
particles:0
entityShadows:true
biomeBlendRadius:2
resourcePacks:["vanilla","fabric","file/FreshAnimations_v1.11.2_26.1+.zip","file/Better-Leaves-9.6.zip"]
narrator:0
tutorialStep:none
skipMultiplayerWarning:true
onboardAccessibility:false
"""


def zinfo(name):
    i = zipfile.ZipInfo(name, date_time=FIXED_DATE)
    i.compress_type = zipfile.ZIP_DEFLATED
    i.external_attr = 0o644 << 16
    return i


def env_for(e, metas):
    client = "optional" if e.get("optional") else "required"
    if e["kind"] != "mod":
        return {"client": "optional" if e["kind"] == "shader" or e.get("optional") else "required", "server": "unsupported"}
    server = "unsupported" if not validate.server_capable(e, metas) else ("optional" if e.get("optional") else "required")
    return {"client": client, "server": server}


def main():
    man = tomllib.loads((ROOT / "pack" / "manifest.toml").read_text())
    lock = json.loads((ROOT / "pack" / "mods.lock.json").read_text())
    pack = man["pack"]
    DIST.mkdir(parents=True, exist_ok=True)
    out = DIST / f"JulesHomestead-{pack['version']}.mrpack"
    files, server_files, local = [], [], []
    for e in lock["files"]:
        folder = {"mod": "mods", "resourcepack": "resourcepacks", "shader": "shaderpacks"}[e["kind"]]
        if e["source"] == "local":
            data = validate.local_jar(e)
            if data is None:
                raise SystemExit(f"{e['slug']} not built; run tools/build_local.py")
            meta = read_mod_jsons(data)[0][0]
            local.append((f"overrides/mods/{e['slug']}-{meta['version']}.jar", data))
            continue
        data = validate.fetch(e)
        metas = read_mod_jsons(data) if e["kind"] == "mod" else []
        if not e["url"].startswith(ALLOWED_HOSTS):
            raise SystemExit(f"{e['slug']}: download host not allowed: {e['url']}")
        entry = {"path": f"{folder}/{e['filename']}", "hashes": {"sha1": e["hashes"]["sha1"], "sha512": e["hashes"]["sha512"]},
                 "env": env_for(e, metas), "downloads": [e["url"]], "fileSize": len(data)}
        files.append(entry)
        if e["kind"] == "mod" and entry["env"]["server"] == "required":
            server_files.append(entry)
    index = {
        "formatVersion": 1, "game": "minecraft", "versionId": pack["version"], "name": pack["name"], "summary": pack["summary"],
        "files": sorted(files, key=lambda f: f["path"]),
        "dependencies": {"minecraft": pack["minecraft"], "fabric-loader": pack["loader_version"]},
    }
    with zipfile.ZipFile(out, "w") as z:
        z.writestr(zinfo("modrinth.index.json"), json.dumps(index, indent=1))
        for path, data in local:
            z.writestr(zinfo(path), data)
        for f in sorted((ROOT / "configs").rglob("*")):
            if f.is_file():
                z.writestr(zinfo("overrides/config/" + f.relative_to(ROOT / "configs").as_posix()), f.read_bytes())
        z.writestr(zinfo("overrides/options.txt"), OPTIONS_TXT)
        for doc in ("README.md", "docs/INSTALL.md", "CHANGELOG.md"):
            p = ROOT / doc
            if p.exists():
                z.writestr(zinfo("overrides/homestead-docs/" + p.name), p.read_bytes())
    srv = DIST / f"JulesHomestead-{pack['version']}-server-files.json"
    srv.write_text(json.dumps({"minecraft": pack["minecraft"], "fabric-loader": pack["loader_version"], "files": server_files,
                               "local": [p.split("/", 1)[1] for p, _ in local]}, indent=1))
    digest = hashlib.sha256(out.read_bytes()).hexdigest()
    print(f"{out.relative_to(ROOT)}: {len(files)} downloads ({sum(1 for f in files if f['path'].startswith('mods/'))} mods), "
          f"{len(local)} bundled local mods, size {out.stat().st_size // 1024} KiB, sha256 {digest}")
    print(f"{srv.relative_to(ROOT)}: {len(server_files)} server mods + {len(local)} local")


if __name__ == "__main__":
    main()
