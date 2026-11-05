# Compatibility Record

Everything here was established in this repository's cloud environment on 2026-11-05, unless marked otherwise.

## Whole-pack checks performed
| Check | Tool | Result |
|---|---|---|
| Every manifest entry resolves to an exact 26.1.2 Fabric file | `tools/resolve.py` | 125 files (incl. 24 transitive deps) |
| Jar-level `depends`/`breaks` incl. nested JiJ + `provides`, three sets (all client, core client, dedicated server) | `tools/validate.py` | 0 errors |
| Download hashes (SHA-1 + SHA-512) | `tools/validate.py`, `tools/mrpack_install.py` | all match |
| Dedicated server boots, generates a world | `tools/server_test.py` | 90-mod server set, ~40 s boot, 3/3 boots clean |
| Quest file loads in real FTB Quests | `tools/server_test.py` log | "Loaded 1 chapter groups, 9 chapters, 104 quests", no errors |
| Quest/recipe/tag references exist in real registries | `tools/check_content.py` | 0 errors |
| homestead-core GameTests (Thermoo + Spoiled loaded) | `./gradlew build` | 8/8 pass |
| Full client (238 mods incl. nested) under Xvfb + Mesa llvmpipe, join singleplayer world | `tools/client_smoke_test.py` | see STATUS.md for the latest result |

## Problems found and resolved
| Problem | Evidence | Resolution |
|---|---|---|
| **Wilder Wild 4.2.9 (FrozenLib)**: intermittent `ConcurrentModificationException` in `FrozenBiomeParameters.findBorderParameters` while 26.1's parallel registry loader decodes Terralith's `terralith:all_skylands` parameter list → "Failed to load registries", world cannot load | `build/server-test/server-test.log` (1 of 4 boots) | **Removed** Wilder Wild. Terralith + Tectonic remain the terrain owners. |
| **Serene Seasons 26.1.2.0.7**: client mixin `MixinBiomeClient#onHasPrecipitation` → `SeasonHooks#hasPrecipitationSeasonal` → `Biome#hasPrecipitation` → … infinite recursion (`StackOverflowError`) in every client JVM once a level exists, i.e. **singleplayer crashes** shortly after joining. Dedicated servers unaffected (client-only mixin). | `tools/client_smoke_test.py`: integrated-server crash report 1 s after join | homestead-core `SereneSeasonsRecursionFixMixin`: `@Redirect` of that single call site to the biome's own `ClimateSettings.hasPrecipitation` (the intended value). Applied only if Serene Seasons is loaded. |
| **Serene Seasons** forces `thundering=false` in sub-seasons whose thunder range is `-1` (winter defaults) when `change_weather_frequency=true` → would cancel homestead-core fronts in winter | `RandomUpdateHandler` (decompiled) | Winter sub-seasons get a rare but valid thunder window (96 000–240 000 ticks) in `configs/sereneseasons/seasons.toml`. |
| **Spoiled 12.1.0** fills its spoil-recipe cache only from a *client* recipe-sync event → nothing spoils on a dedicated server | `SpoiledFabricClient` (decompiled); GameTest control chest failed | homestead-core rebuilds the cache server-side on start/reload (`SpoiledCompat.refreshServerRecipeCache`). GameTest confirms chests now spoil. |
| Serene Seasons' jar requires **GlitchCore**; Modrinth metadata doesn't declare it | validator | Added `glitchcore` explicitly. |
| Rechiseled requires **Fusion** on servers; Modrinth marks Fusion client-only | validator | `fusion-connected-textures` forced to both sides. |
| Zoomify's Modrinth metadata lists NeoForge-only `kotlin-lang-forge` | resolver | Ignored; jar metadata check passes. |
| Modrinth maven resolves bare version numbers ambiguously across loaders | NeoForge Spoiled jar picked at compile time | homestead-core build pins integration deps by **version ID**. |
| Energized Power Weather Controller / Time Controller would bypass seasons and fronts | design | Recipes disabled in `homestead-pack-data`. |
| Frostiful freezing wind destroys torches; falling icicles; Scorchful sand piles / Breezes in blizzards | config review | Disabled (conservative destruction policy). |

## Ownership (no duplicate systems)
See `docs/design.md`. Single owners for seasons (Serene Seasons), body temperature (Thermoo + Frostiful/Scorchful), severe weather (homestead-core), spoilage (Spoiled), energy API (TR Energy), recipe viewer (JEI), terrain (Terralith + Tectonic), quests (FTB Quests). Homeostatic, Tough As Nails, Fabric Seasons, Tech Reborn, EMI/REI, Wilder Wild deliberately absent.

## Client-side mixins that touch the same area
* homestead-core `FogRendererMixin` (RETURN of `FogRenderer.setupFog`, only while exposed in a blizzard) and Immersive Storms (bundled in Scorchful) both shorten fog. Ours only ever *reduces* distances, so they compose; with shaders, Iris shader packs may draw their own fog. **Awaiting local verification.**
* Sodium / Iris / Particle Rain / Immersive Storms co-existence is the upstream-supported combination per their docs; not verifiable without a GPU.

## Not verified here (needs your PC)
Client rendering with a real GPU, shaders, keybinding conflicts in the Controls screen (use *Controlling*'s conflict filter), audio mods, Physics Mod Pro, Create Fly with this pack, Distant Horizons.
