#!/usr/bin/env python3
"""Install an .mrpack into a directory the way a launcher does (download, SHA-512 verify, extract overrides).

Used to (a) verify the mrpack is self-consistent and every URL resolves to the pinned bytes, and (b) prepare an
instance for tools/client_smoke_test.py. Honours env.client: "required" always, "optional" only with --with-optional.
Usage: python3 -I tools/mrpack_install.py build/dist/JulesHomestead-0.1.0.mrpack <instance-dir> [--with-optional]
"""
import hashlib
import json
import pathlib
import shutil
import sys
import urllib.request
import zipfile

CACHE = pathlib.Path(__file__).resolve().parent.parent / "build" / ".cache" / "jars"
UA = {"User-Agent": "jules-homestead-mrpack-install-test/0.1"}


def main():
    mrpack, dest = pathlib.Path(sys.argv[1]), pathlib.Path(sys.argv[2])
    with_optional = "--with-optional" in sys.argv
    z = zipfile.ZipFile(mrpack)
    index = json.loads(z.read("modrinth.index.json"))
    assert index["formatVersion"] == 1 and index["game"] == "minecraft"
    if dest.exists():
        shutil.rmtree(dest)
    dest.mkdir(parents=True)
    n = skipped = 0
    for f in index["files"]:
        env = f.get("env", {}).get("client", "required")
        if env == "unsupported" or (env == "optional" and not with_optional):
            skipped += 1
            continue
        target = dest / f["path"]
        assert ".." not in pathlib.PurePosixPath(f["path"]).parts, f["path"]
        target.parent.mkdir(parents=True, exist_ok=True)
        cached = CACHE / f"{f['hashes']['sha1']}-{pathlib.PurePosixPath(f['path']).name}"
        data = cached.read_bytes() if cached.exists() else urllib.request.urlopen(urllib.request.Request(f["downloads"][0], headers=UA), timeout=300).read()
        if hashlib.sha512(data).hexdigest() != f["hashes"]["sha512"] or hashlib.sha1(data).hexdigest() != f["hashes"]["sha1"]:
            raise SystemExit(f"hash mismatch for {f['path']}")
        if len(data) != f["fileSize"]:
            raise SystemExit(f"size mismatch for {f['path']}")
        target.write_bytes(data)
        n += 1
    ov = 0
    for name in z.namelist():
        if name.startswith("overrides/") and not name.endswith("/"):
            t = dest / name[len("overrides/"):]
            t.parent.mkdir(parents=True, exist_ok=True)
            t.write_bytes(z.read(name))
            ov += 1
    print(f"installed {n} files ({skipped} skipped by env), {ov} override files -> {dest}")
    print(f"dependencies: {index['dependencies']}")


if __name__ == "__main__":
    main()
