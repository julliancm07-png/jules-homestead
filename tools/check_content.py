#!/usr/bin/env python3
"""Validate pack content against the real modded registries (research/data/registry-dump-26.1.2.json,
produced by `/homestead debug dump` on the headless server).

Checks: every quest task/reward/icon item, advancement, biome, structure and observed block exists; quest IDs are
unique; dependencies resolve and are acyclic; every lang key points at a real object; datapack/mod recipe item
references and required tag entries exist; config files parse (JSON / TOML).
Exit code 1 on any error.
"""
import json
import pathlib
import sys
import tomllib

ROOT = pathlib.Path(__file__).resolve().parent.parent
DUMP = ROOT / "research" / "data" / "registry-dump-26.1.2.json"
QUESTS = ROOT / "configs" / "ftbquests" / "quests"
errors, warnings = [], []


def load_dump():
    if not DUMP.exists():
        print("WARN registry dump missing; run tools/server_test.py --cmd 'homestead debug dump'")
        return None
    d = json.loads(DUMP.read_text())
    return {k: set(v) for k, v in d.items()}


def check_quests(reg):
    ids = {}
    deps = {}
    lang = {}
    for f in (QUESTS / "lang").rglob("*.json5"):
        lang.update(json.loads(f.read_text()))
    for f in sorted((QUESTS / "chapters").glob("*.json5")):
        ch = json.loads(f.read_text())
        for obj, kind in [(ch, "chapter")] + [(q, "quest") for q in ch["quests"]]:
            if obj["id"] in ids:
                errors.append(f"duplicate id {obj['id']} ({kind} in {f.name})")
            ids[obj["id"]] = kind
            if "icon" in obj and reg and obj["icon"]["id"] not in reg["item"]:
                errors.append(f"{f.name}: icon item {obj['icon']['id']} not registered")
        for q in ch["quests"]:
            deps[q["id"]] = q.get("dependencies", [])
            title = lang.get(f"quest.{q['id']}.title", "?")
            for t in q["tasks"]:
                ids[t["id"]] = "task"
                ty = t["type"]
                if not reg:
                    continue
                if ty == "item" and t["item"]["id"] not in reg["item"]:
                    errors.append(f"[{title}] task item {t['item']['id']} not registered")
                elif ty == "advancement" and t["advancement"] not in reg["advancement"]:
                    errors.append(f"[{title}] advancement {t['advancement']} not found")
                elif ty == "biome" and not t["biome"].startswith("#") and t["biome"] not in reg["biome"]:
                    errors.append(f"[{title}] biome {t['biome']} not registered")
                elif ty == "structure":
                    s = t["structure"]
                    if s.startswith("#"):
                        warnings.append(f"[{title}] structure tag {s} not verifiable from dump (tags not dumped)")
                    elif s not in reg["structure"]:
                        errors.append(f"[{title}] structure {s} not registered")
                elif ty == "observation" and t.get("observation_type") == "block" and t["to_observe"] not in reg["block"]:
                    errors.append(f"[{title}] observed block {t['to_observe']} not registered")
            for r in q.get("rewards", []):
                ids[r["id"]] = "reward"
                if reg and r["type"] == "item" and r["item"]["id"] not in reg["item"]:
                    errors.append(f"[{title}] reward item {r['item']['id']} not registered")
    for q, ds in deps.items():
        for d in ds:
            if ids.get(d) != "quest":
                errors.append(f"quest {q} depends on unknown quest {d}")
    # cycle check
    state = {}

    def visit(n, stack):
        if state.get(n) == 1:
            errors.append(f"dependency cycle through {n}")
            return
        if state.get(n) == 2:
            return
        state[n] = 1
        for d in deps.get(n, []):
            visit(d, stack + [n])
        state[n] = 2
    for n in deps:
        visit(n, [])
    for key in lang:
        parts = key.split(".")
        if parts[0] in ("quest", "chapter", "task") and parts[1] not in ids:
            errors.append(f"lang key {key} has no object")
    for q in deps:
        if f"quest.{q}.title" not in lang:
            errors.append(f"quest {q} has no title")
    print(f"quests: {sum(1 for v in ids.values() if v == 'quest')} quests, {sum(1 for v in ids.values() if v == 'task')} tasks checked")


def walk_refs(o):
    if isinstance(o, dict):
        for k, v in o.items():
            if k in ("id", "item") and isinstance(v, str):
                yield v
            elif k == "key" and isinstance(v, dict):
                for vv in v.values():
                    if isinstance(vv, str):
                        yield vv
            else:
                yield from walk_refs(v)
    elif isinstance(o, list):
        for v in o:
            yield from walk_refs(v)


def check_data(reg):
    roots = [ROOT / "datapacks" / "homestead-pack-data" / "data", ROOT / "custom-mods" / "homestead-core" / "src" / "main" / "resources" / "data"]
    n = 0
    for root in roots:
        for f in root.rglob("*.json"):
            try:
                o = json.loads(f.read_text())
            except json.JSONDecodeError as e:
                errors.append(f"{f.relative_to(ROOT)}: invalid JSON {e}")
                continue
            n += 1
            rel = f.relative_to(root).as_posix()
            disabled = any(c.get("condition") == "fabric:false" for c in o.get("fabric:load_conditions", []))
            if not reg or disabled:
                continue
            if "/recipe/" in "/" + rel:
                for ref in walk_refs({k: v for k, v in o.items() if k != "fabric:load_conditions"}):
                    if ref.startswith("#"):
                        continue
                    if ref not in reg["item"]:
                        errors.append(f"{f.relative_to(ROOT)}: recipe references unknown item {ref}")
            if "/tags/" in "/" + rel and "/tags/block/" in "/" + rel:
                for v in o.get("values", []):
                    ident, required = (v, True) if isinstance(v, str) else (v["id"], v.get("required", True))
                    if ident.startswith("#"):
                        continue
                    if ident not in reg["block"]:
                        (errors if required else warnings).append(f"{f.relative_to(ROOT)}: block {ident} not registered" + ("" if required else " (optional entry)"))
            if "/tags/item/" in "/" + rel:
                for v in o.get("values", []):
                    ident, required = (v, True) if isinstance(v, str) else (v["id"], v.get("required", True))
                    if not ident.startswith("#") and ident not in reg["item"]:
                        (errors if required else warnings).append(f"{f.relative_to(ROOT)}: item {ident} not registered" + ("" if required else " (optional entry)"))
    print(f"data: {n} JSON files checked")


def check_configs():
    n = 0
    for f in (ROOT / "configs").rglob("*"):
        if not f.is_file():
            continue
        n += 1
        try:
            if f.suffix == ".toml":
                tomllib.loads(f.read_text())
            elif f.suffix == ".json":
                json.loads(f.read_text())
            elif f.suffix == ".json5" and "ftbquests" in f.parts:
                json.loads(f.read_text())  # generated as strict JSON (valid JSON5)
        except Exception as e:
            errors.append(f"{f.relative_to(ROOT)}: parse error {e}")
    print(f"configs: {n} files parsed")


if __name__ == "__main__":
    reg = load_dump()
    check_quests(reg)
    check_data(reg)
    check_configs()
    for w in warnings:
        print("WARN ", w)
    for e in errors:
        print("ERROR", e)
    print(f"content: {len(errors)} errors, {len(warnings)} warnings")
    sys.exit(1 if errors else 0)
