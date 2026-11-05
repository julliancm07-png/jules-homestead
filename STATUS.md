# Status (2026-11-05)

Legend: **R** researched · **I** implemented · **C** compiled · **T** runtime-tested in the cloud · **L** awaiting local verification on Jules' PC.
"T" names exactly what the test showed; cloud tests use a headless server and a software-rendered client (Mesa llvmpipe under Xvfb, no sound, no player input).

## Platform & pack
| Item | State | Evidence |
|---|---|---|
| Platform choice (26.1.2 / Loader 0.20.1 / Java 25) | R | research/version-decision.md |
| Pinned lockfile (125 files, IDs + hashes + licenses) | T | `tools/resolve.py`; hashes re-verified by `validate.py` and `mrpack_install.py` |
| Jar-level dependency resolution (client-all, client-core, server) | T | `tools/validate.py`: 0 errors |
| Dedicated server boots + generates world (90 server mods) | T | `tools/server_test.py`: 3/3 clean boots, ~40 s |
| `.mrpack` builds; launcher-style install verifies every file | T | `tools/package.py`, `tools/mrpack_install.py` (117 client files + overrides) |
| **Full client** (238 mods incl. nested) launches, renders, joins singleplayer, 90 s without crash | T | `tools/client_smoke_test.py --scenario normal` → PASS; `docs/screenshots/client-smoke-normal.png` (world rendered behind MCA's first-join screen) |
| Prism import (real launcher UI, optional-mod dialog, untrusted-URL prompt) | L | Prism behaviour verified from source only |
| Real GPU rendering, shaders, Distant Horizons, sound, keybind conflicts, FPS | L | docs/test-checklist.md |

## homestead-core (custom mod)
| Feature | State | Evidence / what it establishes |
|---|---|---|
| Severe fronts: schedule, envelope, saved state, vanilla flags, stop/ramp-down | T | GameTest `frontLifecycleWindAndPersistence`; console commands in the full server pack |
| Forecast + warnings, barometer reading, commands | T (server console) / L (in-game UI) | `/homestead weather status/forecast/start/stop` in the full pack |
| Shelter heuristic (heightmap roof incl. glass + upwind wall) | T | GameTest `shelterHeuristic` |
| Blizzard cold via Thermoo source (frost-resistance reduction) | T (registration + maths) / L (felt in play) | GameTest `thermooSourcesRegistered` |
| Slowdown, snow build-up | I/C · L | code paths run during console-started fronts; no assertion yet |
| Blizzard fog + snow particles (client) | T (runs under software GL) / L (looks right) | see the blizzard smoke test below |
| Wind turbine output under storm wind | T | GameTest (turbine generated energy during a front) |
| Water wheel needs flowing water | T | GameTest `waterWheelNeedsFlowingWater` |
| Refrigerator preserves only while powered (real Spoiled) | T | GameTest `refrigeratorPreservesOnlyWhilePowered` (chest control spoils; warm fridge spoils; powered fridge doesn't) |
| Electric range: FD heat-source LIT gate, oven pause on outage, residual heat | T | GameTest `electricRangeOvenAndCooktop`; FD tag gating verified in FD source |
| Space heater power + demand + light + warm radius | T | GameTest `spaceHeaterRunsOnPowerAndDemand` |
| Weather advancements for quests | I/C · L | awarded by the server tick; not yet asserted |
| Spoiled dedicated-server cache workaround | T | GameTest control chest spoils only with the fix |
| Serene Seasons singleplayer recursion fix | T | Client smoke test crashed without it (StackOverflowError) and passes with it |

## Content
| Item | State | Evidence |
|---|---|---|
| Configs (seasons, temperature, spoilage, MCA, homestead) | T (load) / L (balance) | parsed by mods in the server boot; `check_content.py` |
| Pack data module (seasonal crop tags, EP controller removal) | T | registry dump: EP weather/time controller recipes absent |
| Quest book: 9 chapters, 104 quests, 85 auto-detected | T (load + references) / L (play-through) | FTB log "Loaded 1 chapter groups, 9 chapters, 104 quests" (server and integrated server); `check_content.py` 0 errors |
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
| Immersion mods (Subtle Effects, Ambient Environment, Wakes, Camera Overhaul tuned, Legendary Tooltips, RRLS, Overflowing Bars, Nuit) | T (load) / L (feel) | 259 mods loaded, showcase PASS |
| Optional shaders: Complementary Reimagined, Photon, Mellow, MakeUp UF | R · L | need a real GPU |
| IPN auto-refill icons removed from slots | I · L | config `AutoRefillSettings` (format verified in libIPN source) |

## Removed / blocked
* Wilder Wild: removed (registry-load race with Terralith). See docs/compatibility.md.
* CurseForge API and site are blocked by this environment's egress proxy (HTTP 403). CurseForge facts came from WebFetch and the CFWidget mirror (marked secondary).

## Finish-up plan (next session, in order)
1. **Spacial GUI** (Jules likes its look): identify the Modrinth project, check its 26.1.2 build, licence and requirements, compare it with Recolourful Containers (pick one or order them), add it, then check the `showcase` inventory screenshot.
2. Re-run the `showcase` scenario: confirm the IPN slot icons are gone and Camera Overhaul's tuned config applies.
3. Close the I/C gaps: GameTests for slowdown, snow build-up and the weather advancements.
4. Optional quest: town power via FTB's `tech_reborn_energy` task.
5. Release pass: bump to 1.0.0, run the full pipeline (resolve → validate → build_local → quests → check_content → package → mrpack_install → server_test → client normal/blizzard/showcase/title), make counts consistent across README/STATUS (mods, files), and finish test-checklist entries for visuals and shaders.
6. Delivery: get the `.mrpack` to Jules (a GitHub release asset or a committed `dist/` file; ask first).
