# Status (2026-11-05, version 1.0.0)

Legend: **R** researched · **I** implemented · **C** compiled · **T** runtime-tested in the cloud · **L** awaiting local verification on Jules' PC.
"T" names exactly what the test showed; cloud tests use a headless server and a software-rendered client (Mesa llvmpipe under Xvfb, no sound, no player input).

## Platform & pack
| Item | State | Evidence |
|---|---|---|
| Platform choice (26.1.2 / Loader 0.20.1 / Java 25) | R | research/version-decision.md |
| Pinned lockfile (158 files: 135 third-party mods, 2 local, 17 resource packs, 4 shaders; IDs + hashes + licences) | T | `tools/resolve.py`; hashes re-verified by `validate.py` and `mrpack_install.py` |
| Jar-level dependency resolution (client-all, client-core, server) | T | `tools/validate.py`: 0 errors |
| Dedicated server boots + generates world (96 server mods + 2 local) | T | `tools/server_test.py`: 3/3 clean boots, ~40 s |
| `.mrpack` builds; launcher-style install verifies every file | T | `tools/package.py`, `tools/mrpack_install.py` (156 downloads + 2 bundled local mods + overrides) |
| **Full client** (260 mods incl. nested libraries) launches, renders, joins singleplayer, 90 s without crash | T | `tools/client_smoke_test.py --scenario normal` → PASS; `docs/screenshots/client-smoke-normal.png` (world rendered behind MCA's first-join screen) |
| Prism import (real launcher UI, optional-mod dialog, untrusted-URL prompt) | L | Prism behaviour verified from source only |
| Real GPU rendering, shaders, Distant Horizons, sound, keybind conflicts, FPS | L | docs/test-checklist.md |

## homestead-core (custom mod)
| Feature | State | Evidence / what it establishes |
|---|---|---|
| Severe fronts: schedule, envelope, saved state, vanilla flags, stop/ramp-down | T | GameTest `frontLifecycleWindAndPersistence`; console commands in the full server pack |
| Forecast + warnings, barometer reading, commands | T (server console) / L (in-game UI) | `/homestead weather status/forecast/start/stop` in the full pack |
| Shelter heuristic (heightmap roof incl. glass + upwind wall) | T | GameTest `shelterHeuristic` |
| Blizzard cold via Thermoo source (frost-resistance reduction) | T (registration + maths) / L (felt in play) | GameTest `thermooSourcesRegistered` |
| Slowdown (storm and blizzard), shelter removes it | T | GameTests `stormSlowdownAndShelter`, `blizzardSlowdownSnowAndAdvancements` (survival test player; slowdown = config × intensity) |
| Snow build-up (cap, farmland/roof exclusions, live accumulation around an exposed player) | T | GameTest `blizzardSlowdownSnowAndAdvancements` |
| Blizzard fog + snow particles (client) | T (runs under software GL) / L (looks right) | see the blizzard smoke test below |
| Wind turbine output under storm wind | T | GameTest (turbine generated energy during a front) |
| Water wheel needs flowing water | T | GameTest `waterWheelNeedsFlowingWater` |
| Refrigerator preserves only while powered (real Spoiled) | T | GameTest `refrigeratorPreservesOnlyWhilePowered` (chest control spoils; warm fridge spoils; powered fridge doesn't) |
| Electric range: FD heat-source LIT gate, oven pause on outage, residual heat | T | GameTest `electricRangeOvenAndCooktop`; FD tag gating verified in FD source |
| Space heater power + demand + light + warm radius | T | GameTest `spaceHeaterRunsOnPowerAndDemand` |
| Weather advancements for quests (witness_front, caught_in_blizzard, safe_and_warm after 60 s sheltered) | T | the two weather-exposure GameTests (10/10 GameTests pass in 10 consecutive runs, also under load) |
| Electric Oven screen (walnut/cream appliance area) | T (renders) | `showcase-range.png` |
| Spoiled dedicated-server cache workaround | T | GameTest control chest spoils only with the fix |
| Serene Seasons singleplayer recursion fix | T | Client smoke test crashed without it (StackOverflowError) and passes with it |

## Content
| Item | State | Evidence |
|---|---|---|
| Configs (seasons, temperature, spoilage, MCA, homestead) | T (load) / L (balance) | parsed by mods in the server boot; `check_content.py` |
| Pack data module (seasonal crop tags, EP controller removal) | T | registry dump: EP weather/time controller recipes absent |
| Quest book: 9 chapters, 104 quests, 85 auto-detected (incl. town-power energy task) | T (load + references) / L (play-through; filling the Task Screen with energy) | FTB log "Loaded 1 chapter groups, 9 chapters, 104 quests" (server and integrated server); `check_content.py` 0 errors |
| Residents (MCA), market, requests | T (load) / L | |
| Create (optional, Create Fly) | R · L | dependency-checked only; excluded from the cloud boots |

## Screens & immersion (user request 2026-11-05) — see docs/visuals-and-immersion.md
| Item | State | Evidence |
|---|---|---|
| Homestead Resources pack (panorama of our world, Homestead Edition banner, splashes) | T | `docs/screenshots/title-screen.png` (client title scenario PASS) |
| Walnut FTB Quests theme (+ frost/ember chapter variants) | T (renders) / L (readability at your GUI scale) | `docs/screenshots/showcase-questbook.png` |
| Walnut Jade theme as default | T | `docs/screenshots/showcase-jade.png` |
| 16 curated resource packs active (incl. Recolourful Containers, Dramatic Skys) | T (all loaded) / L (look on real GPU) | client log "Reloading ResourceManager" lists every pack |
| Structure data packs (Structory, Towers, Explorify), Gardener's Dream, Snow Under Trees | T (load, server + client) / L (exploration feel) | server boot + client showcase |
| Immersion mods (Subtle Effects, Ambient Environment, Wakes, Camera Overhaul tuned, Legendary Tooltips, RRLS, Overflowing Bars, Nuit) | T (load) / L (feel) | 260 mods loaded, showcase PASS; Camera Overhaul re-saved our tuned values unchanged |
| **Spatial GUI** (3D inventory/container panels; Jules' pick) | T (renders with Recolourful, JEI, our oven screen) / L (feel, shaders) | `showcase-inventory.png`, `showcase-crafting.png`, `showcase-range.png`; quest book stays flat |
| Optional shaders: Complementary Reimagined, Photon, Mellow, MakeUp UF | R · L | need a real GPU |
| IPN auto-refill icons removed from slots | T | showcase screenshots show no slot icons |

## Removed / blocked
* Wilder Wild: removed (registry-load race with Terralith). See docs/compatibility.md.
* CurseForge API and site are blocked by this environment's egress proxy (HTTP 403). CurseForge facts came from WebFetch and the CFWidget mirror (marked secondary).

## 1.0.0 release pass (2026-11-05)
Pipeline run on the final build:
1. `build_local`: GameTests 10/10.
2. `resolve`: 158 files.
3. `validate`: 137 mod jars, 0 errors.
4. `build_quests`: 104 quests.
5. `check_content`: 0 errors.
6. `package`: `JulesHomestead-1.0.0.mrpack`, 156 downloads + 2 local, 6726 KiB.
7. `server_test`: booted in 39 s; FTB loaded 104 quests.
8. Client scenarios **normal / blizzard / showcase / title: all PASS**, 260 mods. After the oven-screen restyle, the showcase was re-run and passed again.

Delivered: `dist/JulesHomestead-1.0.0.mrpack` (committed; SHA-256 in `dist/SHA256SUMS`).

## Next (after Jules' local test)
1. Run `docs/test-checklist.md` on the real PC and feed back the results.
2. Balance pass (energy, spoilage, cold) from play-testing.
3. Appliance models and the turbine rotor (see known-limitations).
