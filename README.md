# Jules' Homestead

A Fabric modpack for **Minecraft 26.1.2**. Found a homestead, then grow it into a town. Seasons turn the year and severe fronts bring storms and **blizzards** that really matter. **Renewable electricity** runs household appliances, residents move in, and a hand-written quest book guides the way.

* **Platform:** Minecraft 26.1.2 · Fabric Loader 0.20.1 · Fabric API 0.155.3 · Java 25 ([why](research/version-decision.md))
* **Mods:** 119 pinned mods (`docs/mod-inventory.md`), plus the custom **homestead-core** mod and a pack data module
* **Quests:** 9 chapters, 104 quests (85 auto-detected, 19 labelled manual building milestones)
* **Install:** [docs/INSTALL.md](docs/INSTALL.md) (Windows + Prism Launcher, `.mrpack`)
* **Status:** [STATUS.md](STATUS.md) · **Limitations:** [docs/known-limitations.md](docs/known-limitations.md) · **Local test plan:** [docs/test-checklist.md](docs/test-checklist.md)

## What makes it a homestead pack
| System | How |
|---|---|
| Seasons | Serene Seasons: crop fertility (Farmer's Delight / Ube's / Rustic crops tagged by season), winter snow |
| Temperature | Thermoo + Frostiful (cold, fur, warmth from light) + Scorchful (heat, thirst in the heat) |
| Severe weather | **homestead-core**: storm and blizzard fronts with saved state, forecasts and warnings, wind, shelter, exposure cold, slowdown, fog, snow build-up. No block damage. |
| Food | Farmer's Delight + More/Ube's/Rustic/Storage/Crate Delight, Farming for Blockheads market & shipping bin, Spoiled spoilage (cellar barrels, powered fridges) |
| Power | Energized Power (solar I–IV, batteries, cables, machines) + homestead-core wind turbine and water wheel, all on Team Reborn Energy |
| Appliances | homestead-core **refrigerator** (stops spoilage while powered, cold retention on outage), **electric range** (Farmer's Delight heat source + oven that pauses on outage), **space heater**, **barometer**; EP powered lamps |
| Town | MCA Reborn residents, building detection, ranks and taxes; Easy NPC; waystones |
| World | Terralith + Tectonic, Towns & Towers, YUNG's structures, Friends & Foes |
| Quality of life | JEI (only recipe viewer), Jade, Xaero's maps, backpacks, drawers, Tom's storage, Controlling, Mod Menu |
| Atmosphere | AmbientSounds, Sound Physics, Particle Rain, Immersive Storms, Falling Leaves, Fresh Animations, Better Leaves; shaders optional |

## Repository layout
See [CLAUDE.md](CLAUDE.md). In short: `research/` (decisions and evidence), `docs/` (design, power plan, weather design, install, tests), `pack/` (manifest + lockfile), `configs/` (pack configs + FTB quests), `datapacks/` (pack data module), `quests/book.py` (quest source), `custom-mods/homestead-core/` (the custom mod), `tools/` (build and validation).

## Building
Requires Java 25 and Python 3.11+.
```bash
python3 -I tools/resolve.py            # refresh lockfile pins from manifest (Modrinth + FTB maven)
python3 -I tools/build_local.py        # homestead-core (compile + GameTests) and the pack data module
python3 -I tools/build_quests.py       # quests/book.py -> configs/ftbquests/quests
python3 -I tools/validate.py           # jar-level dependency check + content checks
python3 -I tools/package.py            # build/dist/JulesHomestead-<ver>.mrpack
python3 -I tools/server_test.py        # headless dedicated-server boot
python3 -I tools/client_smoke_test.py  # full client under Xvfb + software GL (needs Xvfb, portablemc)
```

## Licences
Pack code, configs, quests and homestead-core: MIT. Third-party mods are **not** redistributed; the `.mrpack` references their official download URLs (Modrinth CDN, FTB maven) with hashes. Each mod's licence is listed in `docs/mod-inventory.md`.
