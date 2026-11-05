# Jules' Homestead — Project Brief for Future Sessions

Custom Fabric modpack: start a survival world, found a homestead, grow it into a town. Seasons, storms and blizzards
matter; renewable electricity powers real household appliances; residents (MCA Reborn) and a hand-written FTB Quests
book guide progression. Grounded, cozy, vanilla+ — not fantasy RPG/horror/hardcore. User: Jules (Windows + Prism Launcher).

## Platform (decided 2026-11-05, see research/version-decision.md)
Minecraft **26.1.2** · Fabric Loader **0.20.1** · Fabric API **0.155.3+26.1.2** · Java **25** · Gradle 9.8.2 · Loom 1.19.3.
Create is OPTIONAL via Create Fly (user decision). Never trade core systems for Create.

## Layout
- `research/` — dated research notes, version decision, reference-pack study, raw probe JSON (`research/data/`).
- `docs/` — design (`design.md`), weather (`weather-design.md`), power (`power-plan.md`), install, test checklist, limitations.
- `pack/manifest.toml` — human-edited mod list (slug + intent). `pack/mods.lock.json` — generated exact pins (never hand-edit).
- `configs/` — pack config overrides (copied to `overrides/config`). `datapacks/` — pack datapack (global, via overrides).
- `quests/` — FTB Quests SNBT content (copied to `overrides/config/ftbquests/quests`).
- `custom-mods/homestead-core/` — our Fabric mod (weather fronts, shelter, wind/hydro generators, appliances).
- `tools/` — probe, resolver, validators, packager. `tools/ci/central-mirror.init.gradle` for cloud 429s only.
- `build/` (gitignored) — packaged outputs.

## Ground rules
- Every pinned mod must come from Modrinth API or an official maven (FTB → maven.ftb.dev). Record IDs, hashes, side, license.
- Do not invent APIs. Verify signatures against decompiled 26.1.2 sources (Loom `genSources`) or third-party jars (Vineflower).
- One owner per system (see docs/design.md table). Don't add a second seasons/temperature/weather/recipe-viewer mod.
- Mark features honestly: researched / implemented / compiled / runtime-tested / awaiting local verification (STATUS.md).
- Never bundle All-Rights-Reserved jars in overrides. No secrets, no user worlds, no Physics Mod Pro (user-supplied only).

## Commands
```
export JAVA_HOME=/opt/jdk25 PATH=/opt/jdk25/bin:$PATH          # cloud: Temurin 25 installed here
python3 -I tools/resolve.py                                      # manifest.toml -> mods.lock.json (Modrinth/FTB maven)
python3 -I tools/validate.py                                     # lockfile, deps, sides, configs, datapack, quests
(cd custom-mods/homestead-core && ./gradlew build)               # add --init-script ../../tools/ci/central-mirror.init.gradle in cloud
python3 -I tools/package.py                                      # build/JulesHomestead-<ver>.mrpack + server pack
python3 -I tools/server_test.py                                  # headless dedicated-server boot with server-side mods
```
Keep STATUS.md and CHANGELOG.md current at the end of every work session.
