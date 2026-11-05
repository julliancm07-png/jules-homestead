#!/usr/bin/env python3
"""Build the pack's local modules.

* custom-mods/homestead-core -> Gradle `build` (compiles, runs GameTests) unless --skip-tests (then `jar`).
* datapacks/homestead-pack-data -> deterministic zip -> datapacks/homestead-pack-data/build/libs/homestead-pack-data-<ver>.jar
Usage: python3 -I tools/build_local.py [--skip-tests] [--cloud-mirror]
"""
import json, os, pathlib, subprocess, sys, zipfile

ROOT = pathlib.Path(__file__).resolve().parent.parent


def build_core(skip_tests, mirror):
    core = ROOT / "custom-mods" / "homestead-core"
    cmd = ["./gradlew", "--no-daemon", "jar" if skip_tests else "build"]
    if mirror:
        cmd[1:1] = ["--init-script", str(ROOT / "tools" / "ci" / "central-mirror.init.gradle")]
    env = dict(os.environ)
    # Java 25 is required. HOMESTEAD_JAVA_HOME overrides; /opt/jdk25 is the cloud-session install.
    if env.get("HOMESTEAD_JAVA_HOME"):
        env["JAVA_HOME"] = env["HOMESTEAD_JAVA_HOME"]
    elif pathlib.Path("/opt/jdk25").exists():
        env["JAVA_HOME"] = "/opt/jdk25"
    print("+", " ".join(cmd), flush=True)
    subprocess.run(cmd, cwd=core, env=env, check=True)


def build_pack_data():
    src = ROOT / "datapacks" / "homestead-pack-data"
    meta = json.loads((src / "fabric.mod.json").read_text())
    out = src / "build" / "libs" / f"homestead-pack-data-{meta['version']}.jar"
    out.parent.mkdir(parents=True, exist_ok=True)
    files = sorted(p for p in src.rglob("*") if p.is_file() and "build" not in p.relative_to(src).parts)
    with zipfile.ZipFile(out, "w", zipfile.ZIP_DEFLATED) as z:
        for p in files:
            info = zipfile.ZipInfo(str(p.relative_to(src)), date_time=(2026, 1, 1, 0, 0, 0))
            info.compress_type = zipfile.ZIP_DEFLATED
            z.writestr(info, p.read_bytes())
        lic = ROOT / "custom-mods" / "homestead-core" / "LICENSE"
        z.writestr(zipfile.ZipInfo("LICENSE_homestead-pack-data", date_time=(2026, 1, 1, 0, 0, 0)), lic.read_bytes())
    print("built", out.relative_to(ROOT), f"({len(files)} files)")


if __name__ == "__main__":
    build_pack_data()
    if "--data-only" not in sys.argv:
        build_core("--skip-tests" in sys.argv, "--cloud-mirror" in sys.argv)
