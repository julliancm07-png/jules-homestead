# Residents, NPCs & Autonomous Characters (research 2026-11-05)

| Option | 26.1.2 Fabric | Verdict |
|---|---|---|
| **MCA Reborn** 8.1.11 | ✓ (GPL-3.0) | **Included.** Villagers become residents with names, moods, families, professions and chores. Villages are detected from player-built buildings (Blueprint); a village needs `minimumBuildingsToBeConsideredAVillage = 3`. Ranks (Merchant → Noble → Mayor → Monarch) are advancements, so quests detect town growth automatically. Taxes (`taxesFactor`, `taxSeason`). |
| Easy NPC 7.18.0 (+ FTB Quests compat) | ✓ (MIT) | **Included** for hand-placed NPCs (mentor/mayor) whose dialogue can open or complete quests. |
| MineColonies | ✗ (NeoForge only) | Not possible on Fabric. |
| Guard Villagers / Villager Recruits | ✗ for 26.1.2 Fabric | n/a |
| AI-Player (shasankp000) 1.0.6.1 | ✓ (GPL-2.0+) | **Not included.** An LLM-driven player-like bot; needs a separately running model (Ollama etc.), and the author describes it as work in progress. Fails the requirement that the base pack needs no paid API or separately running model; untested here. |

## MCA settings changed (configs/mca.json)
* `allowGrimReaper=false`, `innSpawnsCultists=false`, `enableInfection=false`: keeps the tone grounded (no boss or horror elements).
* `enableVillagerChatAI=false` (the default, kept): conversations would otherwise go to `api.conczin.net`.

## Risks investigated (from MCA's config fields, decompiled 26.1.2 jar)
* **Livestock:** the hunting chore targets animals; there's no config option to exempt pens. The quests and docs warn about it.
* **Building damage / taking supplies:** no MCA config field restricts this, and I didn't observe it in tests, so I make no promise either way. On the local checklist.
* **Load:** `trackVillagerPositionEveryNTicks=200` (default); villager pathfinding distance 80. Large towns should be checked for TPS locally.
