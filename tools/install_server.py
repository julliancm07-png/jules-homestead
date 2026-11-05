#!/usr/bin/env python3
"""Install a Jules' Homestead dedicated server into <dir> from the pinned lockfile.

Downloads the official Fabric server launcher (meta.fabricmc.net) and every server-capable, non-optional mod from its
official URL with SHA-512 verification, adds the pack's local modules, and copies configs + quests.
Requires Java 25 to run the server afterwards:  java -Xmx6G -jar fabric-server-launch.jar nogui
Usage: python3 -I tools/install_server.py <dir> [--accept-eula]
"""
import json, pathlib, shutil, sys, urllib.request

sys.path.insert(0, str(pathlib.Path(__file__).resolve().parent))
from fabricmeta import read_mod_jsons  # noqa: E402
import validate  # noqa: E402

ROOT = pathlib.Path(__file__).resolve().parent.parent


def main():
    if len(sys.argv) < 2:
        raise SystemExit(__doc__)
    out = pathlib.Path(sys.argv[1])
    lock = json.loads((ROOT / "pack" / "mods.lock.json").read_text())
    mc, loader = lock["pack"]["minecraft"], lock["pack"]["loader_version"]
    (out / "mods").mkdir(parents=True, exist_ok=True)
    url = f"https://meta.fabricmc.net/v2/versions/loader/{mc}/{loader}/1.1.2/server/jar"
    (out / "fabric-server-launch.jar").write_bytes(urllib.request.urlopen(urllib.request.Request(url, headers=validate.UA), timeout=300).read())
    n = 0
    for e in lock["files"]:
        if e["kind"] != "mod" or e.get("optional"):
            continue
        data = validate.local_jar(e) if e["source"] == "local" else validate.fetch(e)
        if data is None:
            raise SystemExit(f"{e['slug']} not built; run tools/build_local.py first")
        if not validate.server_capable(e, read_mod_jsons(data)):
            continue
        (out / "mods" / (e["filename"] or f"{e['slug']}.jar")).write_bytes(data)
        n += 1
    shutil.copytree(ROOT / "configs", out / "config", dirs_exist_ok=True)
    if "--accept-eula" in sys.argv:
        (out / "eula.txt").write_text("eula=true\n")
    print(f"installed {n} server mods into {out}. Start with Java 25: java -Xmx6G -jar fabric-server-launch.jar nogui")


if __name__ == "__main__":
    main()
