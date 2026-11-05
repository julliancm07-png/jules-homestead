#!/usr/bin/env python3
"""Compile quests/book.py into FTB Quests 26.1.2.9 JSON5 files.

Format verified against the decompiled FTB Quests 26.1.2.9 loader (BaseQuestFile.readDataFull):
  config/ftbquests/quests/data.json5, chapter_groups.json5, chapters/<file>.json5, lang/en_us/*.json5
Text (titles/subtitles/descriptions) lives in lang files keyed "<type>.<16-hex-id>.<key>".
IDs are deterministic 63-bit values derived from stable keys, so rebuilding never reshuffles player progress.

Usage: python3 -I tools/build_quests.py      -> writes configs/ftbquests/quests/
"""
import hashlib
import json
import pathlib
import runpy
import shutil
import sys

ROOT = pathlib.Path(__file__).resolve().parent.parent
OUT = ROOT / "configs" / "ftbquests" / "quests"
BOOK = ROOT / "quests" / "book.py"


def qid(key):
    v = int.from_bytes(hashlib.sha256(("jules-homestead:" + key).encode()).digest()[:8], "big") & 0x7FFF_FFFF_FFFF_FFFF
    return f"{max(v, 2):016X}"


def item(id_, count=1):
    return {"id": id_, "count": count}


def task_json(t, key):
    kind = t["kind"]
    j = {"id": qid(key), "type": kind}
    if kind == "item":
        j["item"] = item(t["id"])
        j["count"] = t.get("count", 1)
        if t.get("consume"):
            j["consume_items"] = True
    elif kind == "advancement":
        j["advancement"] = t["id"]
        j["criterion"] = t.get("criterion") or ""  # mandatory in 26.1.2.9 (orElseThrow), "" = whole advancement
    elif kind == "observation":
        j["observation_type"] = t["observe"]
        j["to_observe"] = t["id"]
        j["timer"] = t.get("timer", 0)
    elif kind in ("biome", "structure"):
        j[kind] = t["id"]
    elif kind == "kill":
        j["entity"] = t["id"]
        j["value"] = t.get("count", 1)
    elif kind == "checkmark":
        pass
    else:
        raise SystemExit(f"unknown task kind {kind}")
    return j


def reward_json(r, key):
    kind = r["kind"]
    j = {"id": qid(key), "type": kind}
    if kind == "item":
        j["item"] = item(r["id"])
        j["count"] = r.get("count", 1)
    elif kind == "xp":
        j["xp"] = r["xp"]
    else:
        raise SystemExit(f"unknown reward kind {kind}")
    return j


def build():
    book = runpy.run_path(str(BOOK))
    chapters = book["CHAPTERS"]
    if OUT.exists():
        shutil.rmtree(OUT)
    (OUT / "chapters").mkdir(parents=True)
    (OUT / "lang" / "en_us" / "chapters").mkdir(parents=True)

    data = {
        "version": 13,
        "default_reward_team": False,
        "default_consume_items": False,
        "default_autoclaim_rewards": "disabled",
        "default_quest_shape": "circle",
        "default_quest_disable_jei": False,
        "emergency_items_cooldown": 300,
        "drop_loot_crates": False,
        "disable_gui": False,
        "grid_scale": 0.5,
        "pause_game": False,
        "lock_message": "",
        "progression_mode": "linear",
        "detection_delay": 20,
        "show_lock_icons": True,
        "drop_book_on_death": False,
        "hide_excluded_quests": False,
        "fallback_locale": "en_us",
        "verify_on_load": True,
        "icon": item("homesteadcore:barometer"),
    }
    (OUT / "data.json5").write_text(json.dumps(data, indent=2) + "\n")
    (OUT / "chapter_groups.json5").write_text(json.dumps({"chapter_groups": []}, indent=2) + "\n")

    file_lang = {"file.0000000000000001.title": book["BOOK_TITLE"]}
    all_quest_keys = {}
    stats = {"chapters": 0, "quests": 0, "tasks": 0, "rewards": 0, "manual": 0, "auto": 0}
    for order, ch in enumerate(chapters):
        for q in ch["quests"]:
            all_quest_keys[f"{ch['key']}/{q['key']}"] = qid(f"quest/{ch['key']}/{q['key']}")
    for order, ch in enumerate(chapters):
        cid = qid(f"chapter/{ch['key']}")
        cj = {"id": cid, "group": "", "order_index": order, "filename": ch["key"], "icon": item(ch["icon"]),
              "default_quest_shape": ch.get("shape", ""), "default_hide_dependency_lines": False,
              "quests": [], "quest_links": [], "images": []}
        clang = {f"chapter.{cid}.title": ch["title"]}
        if ch.get("subtitle"):
            clang[f"chapter.{cid}.chapter_subtitle"] = ch["subtitle"] if isinstance(ch["subtitle"], list) else [ch["subtitle"]]
        for q in ch["quests"]:
            qkey = f"{ch['key']}/{q['key']}"
            qj = {"id": all_quest_keys[qkey], "x": float(q["x"]), "y": float(q["y"])}
            if q.get("icon"):
                qj["icon"] = item(q["icon"])
            if q.get("preset"):
                qj["preset"] = q["preset"]
            if q.get("shape"):
                qj["shape"] = q["shape"]
            if q.get("size"):
                qj["size"] = float(q["size"])
            if q.get("optional"):
                qj["optional"] = True
            if q.get("repeat"):
                qj["can_repeat"] = True
                qj["repeat_cooldown"] = int(q["repeat"])
            deps = []
            for d in q.get("deps", []):
                full = d if "/" in d else f"{ch['key']}/{d}"
                if full not in all_quest_keys:
                    raise SystemExit(f"{qkey}: unknown dependency {d}")
                deps.append(all_quest_keys[full])
            if deps:
                qj["dependencies"] = deps
            if q.get("any_dep"):
                qj["dependency_requirement"] = "one_completed"
            qj["tasks"] = [task_json(t, f"task/{qkey}/{i}") for i, t in enumerate(q["tasks"])]
            qj["rewards"] = [reward_json(r, f"reward/{qkey}/{i}") for i, r in enumerate(q.get("rewards", []))]
            cj["quests"].append(qj)
            qid_ = qj["id"]
            title = ("[Manual] " if q.get("manual") else "") + q["title"]
            clang[f"quest.{qid_}.title"] = title
            if q.get("subtitle"):
                clang[f"quest.{qid_}.quest_subtitle"] = q["subtitle"]
            desc = list(q.get("desc", []))
            if q.get("manual"):
                desc += ["", "This is a building milestone. FTB Quests cannot reliably judge architecture, so tick the checkbox yourself when you are happy with it."]
            if desc:
                clang[f"quest.{qid_}.quest_desc"] = desc
            for i, t in enumerate(q["tasks"]):
                if t.get("title"):
                    clang[f"task.{qid(f'task/{qkey}/{i}')}.title"] = t["title"]
            stats["quests"] += 1
            stats["tasks"] += len(q["tasks"])
            stats["rewards"] += len(q.get("rewards", []))
            if all(t["kind"] == "checkmark" for t in q["tasks"]):
                stats["manual"] += 1
            else:
                stats["auto"] += 1
        stats["chapters"] += 1
        (OUT / "chapters" / f"{ch['key']}.json5").write_text(json.dumps(cj, indent=2, ensure_ascii=False) + "\n")
        (OUT / "lang" / "en_us" / "chapters" / f"{ch['key']}.json5").write_text(json.dumps(clang, indent=2, ensure_ascii=False) + "\n")
    (OUT / "lang" / "en_us" / "file.json5").write_text(json.dumps(file_lang, indent=2) + "\n")
    print(f"quests built: {stats}")
    return stats


if __name__ == "__main__":
    build()
