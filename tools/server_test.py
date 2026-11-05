#!/usr/bin/env python3
"""Headless dedicated-server boot test.

Builds build/server-test/ with the official Fabric server launcher (meta.fabricmc.net), the *server-capable*
non-optional mods from pack/mods.lock.json (client-only mods excluded), homestead-core, and pack overrides
(config/ + datapacks/). Starts the server, waits for "Done (", optionally runs console commands, then stops it.

What a pass establishes: every server-side mod loads together, mixins apply, registries/datapacks/worldgen
initialise and a fresh world's spawn area generates on 26.1.2 without crash. It does NOT test client rendering,
GUIs, or gameplay feel.

Usage: python3 -I tools/server_test.py [--keep-world] [--cmd "homestead weather status"] ... [--timeout 900]
"""
import argparse
import json
import os
import pathlib
import re
import shutil
import subprocess
import sys
import threading
import time
import urllib.request

sys.path.insert(0, str(pathlib.Path(__file__).resolve().parent))
from fabricmeta import read_mod_jsons  # noqa: E402
import validate  # noqa: E402

ROOT = pathlib.Path(__file__).resolve().parent.parent
OUT = ROOT / "build" / "server-test"
JAVA = os.environ.get("JAVA_HOME", "/opt/jdk25") + "/bin/java"
MC, LOADER, INSTALLER = "26.1.2", "0.20.1", "1.1.2"


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--keep-world", action="store_true")
    ap.add_argument("--cmd", action="append", default=[])
    ap.add_argument("--timeout", type=int, default=900)
    ap.add_argument("--settle", type=int, default=20, help="seconds to keep running after Done")
    a = ap.parse_args()

    lock = json.loads((ROOT / "pack" / "mods.lock.json").read_text())
    OUT.mkdir(parents=True, exist_ok=True)
    launcher = OUT / "fabric-server-launch.jar"
    if not launcher.exists():
        url = f"https://meta.fabricmc.net/v2/versions/loader/{MC}/{LOADER}/{INSTALLER}/server/jar"
        launcher.write_bytes(urllib.request.urlopen(urllib.request.Request(url, headers=validate.UA), timeout=300).read())
    mods = OUT / "mods"
    shutil.rmtree(mods, ignore_errors=True)
    mods.mkdir()
    included, skipped = [], []
    for e in lock["files"]:
        if e["kind"] != "mod" or e.get("optional"):
            continue
        data = validate.local_jar(e) if e["source"] == "local" else validate.fetch(e)
        metas = read_mod_jsons(data) if data else []
        if not validate.server_capable(e, metas):
            skipped.append(e["slug"])
            continue
        name = e["filename"] or f"{e['slug']}.jar"
        (mods / name).write_bytes(data)
        included.append(e["slug"])
    for sub in ("config",):
        src = ROOT / "configs"
        if src.exists():
            shutil.rmtree(OUT / sub, ignore_errors=True)
            shutil.copytree(src, OUT / sub)
    if not a.keep_world:
        shutil.rmtree(OUT / "world", ignore_errors=True)
    if (ROOT / "datapacks").exists():
        dp = OUT / "world" / "datapacks"
        dp.mkdir(parents=True, exist_ok=True)
        for d in (ROOT / "datapacks").iterdir():
            if d.is_dir():
                shutil.rmtree(dp / d.name, ignore_errors=True)
                shutil.copytree(d, dp / d.name)
    (OUT / "eula.txt").write_text("eula=true\n")
    (OUT / "server.properties").write_text("online-mode=false\nserver-port=25599\nspawn-protection=0\nmotd=Jules Homestead server test\nlevel-seed=julesHomestead\nview-distance=6\nsimulation-distance=6\nsync-chunk-writes=false\n")
    print(f"server set: {len(included)} mods; skipped client-only: {', '.join(skipped)}", flush=True)

    log_lines, done = [], threading.Event()
    p = subprocess.Popen([JAVA, "-Xmx6G", "-jar", launcher.name, "nogui"], cwd=OUT, stdin=subprocess.PIPE,
                         stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True, bufsize=1)

    def reader():
        for line in p.stdout:
            log_lines.append(line)
            if re.search(r"Done \([\d.,]+s\)!", line):
                done.set()
    t = threading.Thread(target=reader, daemon=True)
    t.start()
    start = time.time()
    while not done.is_set() and p.poll() is None and time.time() - start < a.timeout:
        time.sleep(1)
    ok = done.is_set()
    boot = time.time() - start
    if ok:
        for c in a.cmd:
            p.stdin.write(c + "\n")
            p.stdin.flush()
            time.sleep(3)
        time.sleep(a.settle)
        p.stdin.write("stop\n")
        p.stdin.flush()
    try:
        p.wait(timeout=180)
    except subprocess.TimeoutExpired:
        p.kill()
    t.join(timeout=5)
    log = "".join(log_lines)
    (OUT / "server-test.log").write_text(log)
    errors = [l for l in log_lines if re.search(r"/ERROR\]|Exception|Mixin apply failed|Caused by", l)]
    print(f"\nresult: {'BOOTED' if ok else 'FAILED TO BOOT'} in {boot:.0f}s, exit={p.returncode}, {len(errors)} error/exception lines")
    for l in errors[:60]:
        print("  ", l.rstrip()[:300])
    print(f"full log: {(OUT / 'server-test.log').relative_to(ROOT)}")
    sys.exit(0 if ok else 1)


if __name__ == "__main__":
    main()
