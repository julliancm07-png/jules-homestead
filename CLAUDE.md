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
- `build/` (gitignored) — packaged outputs. `dist/` (committed) — the released `.mrpack` + server file list + SHA256SUMS; refresh it only after a full release pass.

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
python3 -I tools/build_quests.py                                 # quests/book.py -> configs/ftbquests/quests (JSON5)
python3 -I tools/check_content.py                                # quest/recipe/tag refs vs research/data/registry-dump-26.1.2.json
python3 -I tools/package.py                                      # build/dist/JulesHomestead-<ver>.mrpack (+ server file list)
python3 -I tools/server_test.py [--cmd "homestead debug dump"]   # headless dedicated-server boot with server-side mods
python3 -I tools/client_smoke_test.py [--scenario blizzard]      # full client: Xvfb + Mesa llvmpipe + portablemc
python3 -I tools/inventory.py                                    # regenerate docs/mod-inventory.md
python3 -I tools/gen_resources.py                                # resourcepacks/homestead-resources (title, splashes, themes)
python3 -I tools/client_smoke_test.py --scenario showcase|title  # dev harness screenshots (+ --update-panorama to replace the title panorama)
```

## Gotchas learned (2026-11-05)
- Modrinth metadata is not authoritative: always run validate.py (jar fabric.mod.json). Found: GlitchCore (SS), Fusion server-side (Rechiseled).
- Modrinth maven: pin by VERSION ID, not version number (number is ambiguous across loaders).
- repo.maven.apache.org returns 429 in the cloud proxy -> use `--init-script tools/ci/central-mirror.init.gradle`.
- FTB Quests 26.1.2.9 uses JSON5 + lang files; `advancement` tasks REQUIRE `criterion` (may be ""). Validate by booting the server and reading "Loaded ... quests".
- `canSeeSky` = sky light (lags block changes, passes through glass). Use heightmaps for shelter.
- GameTest areas are encased in barriers (use `skyAccess = true`; keep rays inside the area).
- Upstream bugs patched in homestead-core: Serene Seasons client recursion (singleplayer crash), Spoiled server recipe cache.
- Visual work: verify with the showcase/title scenarios and READ the screenshots (docs/screenshots/).
- Dev harness: -Dhomesteadcore.dev.script=<file> (tools/showcase.script, tools/title.script); inert otherwise.
- Wilder Wild removed (FrozenLib CME during parallel registry load with Terralith).
- Serene Seasons force-clears thunder in seasons with thunder range -1 -> winter thunder window set in configs.
- GameTests that touch GLOBAL state (weather fronts) need their own test environment (`data/homesteadcore/test_environment/*.json`,
  `@GameTest(environment=...)`): batches run sequentially, tests within a batch run concurrently.
- `Level#getBiome` is fuzzed across 4-block biome cells: when a test uses /fillbiome, fill a one-cell margin around the area.
- Vanilla `makeMockServerPlayerInLevel` is always CREATIVE; tests needing survival build their own ServerPlayer (see HomesteadGameTests).
- Spatial GUI (3D inventory panels) is client-only and shipped with mod defaults; quest book/menus stay flat (allScreens=false).
- Showcase runs no longer overwrite the committed title panorama unless `--update-panorama` is passed.
- Local module jars: validate.local_jar picks the newest build in build/libs; bump versions in manifest, gradle.properties and pack-data fabric.mod.json together.
- Shipped options.txt lists resource packs explicitly, which drops mods' DEFAULT_ENABLED built-in packs: add them to
  `package.BUILTIN_DEFAULT_PACKS` (scan jars for `resourcepacks/<name>/` + activation type when adding mods).
- YOSBR copies ANY file under `config/yosbr/` to the game dir if missing: options.txt and the shader presets
  (`shaderpacks/<zip>.txt`, `package.SHADER_PRESETS`; option names must match the shader's `#define`s).
- 26.1 drops resource packs that only declare legacy formats (`pack_format`/`supported_formats` ≤ 64): validate.py warns.
- One sky owner: Dramatic Skys (Nuit) draws stars/moon; don't add star/sky mods. Shaders draw their own sky.
- `client_smoke_test.py --skip-install` reuses the old instance: never use it after changing the mod set.
Keep STATUS.md and CHANGELOG.md current at the end of every work session.
