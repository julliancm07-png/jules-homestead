#!/usr/bin/env python3
"""Generate docs/mod-inventory.md from pack/mods.lock.json + build/reports/validation.json (jar mod ids).

Per entry: project + exact version IDs, MC/loader compatibility, dependencies, client/server sides, download
source + hashes, purpose, license / distribution notes.
"""
import json
import pathlib
import sys

sys.path.insert(0, str(pathlib.Path(__file__).resolve().parent))
import validate  # noqa: E402
from fabricmeta import read_mod_jsons  # noqa: E402

ROOT = pathlib.Path(__file__).resolve().parent.parent


def main():
    lock = json.loads((ROOT / "pack" / "mods.lock.json").read_text())
    by_pid = {e["project_id"]: e["slug"] for e in lock["files"] if e.get("project_id")}
    lines = [
        "# Mod Inventory (generated)",
        "",
        f"Generated from `pack/mods.lock.json` (resolved {lock['resolved_at']}). Do not edit by hand: run `python3 -I tools/inventory.py`.",
        "",
        f"Target: Minecraft {lock['pack']['minecraft']}, Fabric Loader {lock['pack']['loader_version']}. "
        f"{sum(1 for e in lock['files'] if e['kind']=='mod')} mods, "
        f"{sum(1 for e in lock['files'] if e['kind']=='resourcepack')} resource packs, {sum(1 for e in lock['files'] if e['kind']=='shader')} shader packs.",
        "",
        "Side legend: **C** client, **S** dedicated server; ✓ required, ○ optional (mrpack), ✗ not installed there.",
        "Distribution: Modrinth-hosted files are referenced by Modrinth CDN URL; FTB files by FTB's official maven URL; only the pack's own MIT modules are bundled.",
        "",
    ]
    groups = {}
    for e in lock["files"]:
        groups.setdefault(e.get("group") or "library", []).append(e)
    order = ["core", "performance", "weather", "food", "build", "power", "town", "quests", "world", "qol", "visual", "visual-optional", "optional-create", "library"]
    for g in sorted(groups, key=lambda g: order.index(g) if g in order else 99):
        lines += [f"## {g}", "", "| Mod | Version (Modrinth version ID) | MC / loaders | Sides C/S | License | Purpose / notes | Requires |", "|---|---|---|---|---|---|---|"]
        for e in sorted(groups[g], key=lambda e: e["slug"]):
            if e["source"] == "local":
                data = validate.local_jar(e)
                meta = read_mod_jsons(data)[0][0] if data else {}
                ver = f"{meta.get('version', '?')} (local build)"
                mcl, sides, lic = "26.1.2 / fabric", "✓/✓", "MIT (bundled)"
                req = ", ".join(k for k in (meta.get("depends") or {}) if k not in ("minecraft", "java", "fabricloader"))
                link = f"`{e['slug']}` ({e['path']})"
            else:
                metas = []
                if e["kind"] == "mod":
                    try:
                        metas = read_mod_jsons(validate.fetch(e))
                    except Exception:
                        metas = []
                server = "✗" if e["kind"] != "mod" or not validate.server_capable(e, metas) else ("○" if e.get("optional") else "✓")
                client = "○" if e.get("optional") or e["kind"] == "shader" else "✓"
                sides = f"{client}/{server}"
                vid = e.get("version_id") or "FTB maven"
                ver = f"{e['version_number']} (`{vid}`)" + (f" **{e['version_type']}**" if e.get("version_type") not in (None, "release") else "")
                gv = [v for v in e.get("game_versions", []) if v.startswith("26.1")] or ["26.1.2"]
                mcl = f"{', '.join(gv)} / {', '.join(e.get('loaders', ['fabric']))}"
                lic = e["license"].replace("LicenseRef-", "")
                if e["source"] == "ftb-maven":
                    lic += " (referenced from maven.ftb.dev, not re-hosted)"
                req = ", ".join(sorted({by_pid.get(d["project_id"], d["project_id"]) for d in e.get("dependencies", []) if d["dependency_type"] == "required" and d.get("project_id")}))
                link = f"[{e['title']}]({e['page']})"
            purpose = (e.get("purpose") or "").replace("|", "/")
            if e.get("required_by") and not e.get("explicit"):
                purpose = f"Dependency of {', '.join(e['required_by'][:4])}{'…' if len(e['required_by']) > 4 else ''}"
            lines.append(f"| {link} | {ver} | {mcl} | {sides} | {lic} | {purpose} | {req} |")
        lines.append("")
    lines += ["## Hashes", "", "SHA-1 / SHA-512 for every downloadable file are in `pack/mods.lock.json` (`hashes`) and in the mrpack index.", ""]
    (ROOT / "docs" / "mod-inventory.md").write_text("\n".join(lines))
    print("wrote docs/mod-inventory.md")


if __name__ == "__main__":
    main()
