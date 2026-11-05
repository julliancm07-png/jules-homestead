# Known Limitations & Remaining Work

Stated plainly. "Awaiting local verification" means it was not possible to test in the cloud (no GPU, no audio, no real player input).

## Weather
* Weather is **global** (vanilla model). A front affects the whole dimension; regional differences come only from biome precipitation (snow vs rain vs none). There is no moving storm cell.
* **Shelter** is a heuristic (a roof per the heightmap, plus one upwind raycast), not an airflow simulation. Cave mouths and big overhangs count as shelter if they block the upwind ray.
* Destructive weather (block breaking, crop damage) is **not implemented**. The config switches exist only so the conservative defaults are explicit.
* Wind affects turbines, particles and slowdown. It doesn't push entities or boats.
* Blizzard fog is a client mixin. Shader packs may replace fog rendering. **Awaiting local verification** (with and without shaders).
* Forecast severity is deliberately fuzzy (±1 level about 40% of the time).

## Power & appliances
* **No Create ↔ electricity conversion.** No verified Fabric 26.1.2 converter exists. Create (optional) is mechanical only.
* The wind turbine has no rotor animation (a static block model). The appliances use simple generated textures, not detailed models.
* Water wheel output counts flowing faces, not flow speed.
* Cooking-pot progress during an outage follows Farmer's Delight's own rule (it decays once the cooktop's 5-second residual heat runs out). Ingredients are never lost.
* Hoppers can pull from any range slot, inputs included.
* Energy values are tuned on paper against Energized Power's defaults. Balance **awaits play-testing**.

## Food
* Spoilage comes from **Spoiled 12.1.0, an alpha build** (the newest for 26.1.2). Its dedicated-server recipe cache bug is worked around by homestead-core. Watch for other issues.
* Only Farmer's Delight, Ube's Delight and Rustic Delight crops have season tags. Crops from other mods fall back to Serene Seasons' defaults.
* The Let's Do suite (Bakery, Vinery, …), Handcrafted and Supplementaries have no 26.1.2 Fabric builds, so the food and furniture breadth is narrower than on 1.21.1 packs.

## Residents & town
* MCA chores can hurt livestock (hunting). MCA has no setting to restrict this, so the quests warn about it.
* I didn't test whether MCA residents can damage player builds; I found no config option that limits it. **Awaiting local verification.**
* No autonomous "player-like" AI companions are included. AI-Player (shasankp000) exists for 26.1, but it needs a locally running LLM (e.g. Ollama) and is experimental; it was not tested.
* MCA AI chat is off. Enabling it sends conversations to an external endpoint.

## Quests
* 19 of 104 quests are manual building milestones (FTB Quests can't judge architecture).
* "Power for the Town" needs an FTB **Task Screen** fed by cable. The task type and the screen's energy input exist in the FTB Quests Fabric build (checked in its code), and the quest loads on the server. Actually filling it with energy is **awaiting local verification**.
* The "Visit a Village" quest uses the `#minecraft:village` structure tag. The registry dump doesn't include tags, so it's verified only by FTB Quests loading without error.
* Some item quests name one wood variant (oak drawers). The descriptions say so.

## Upstream issues worked around (re-check when updating)
* Serene Seasons 26.1.2.0.7: infinite recursion in its client `Biome#hasPrecipitation` mixin crashes singleplayer. Fixed by a one-call-site redirect in homestead-core (`SereneSeasonsRecursionFixMixin`).
* Spoiled 12.1.0: the spoil-recipe cache is filled only on the client. Fixed in homestead-core (`SpoiledCompat`).
* Wilder Wild 4.2.9: intermittent registry-load crash alongside Terralith. Removed from the pack.

## Visuals
* Dramatic Skys on Modrinth is the free "Demo" edition. It loads and renders, but the full sky set is a paid Patreon edition. Shaders replace its sky.
* EMF logs that it ignores a few block-entity models (chest, bed, banner, shulker box) after repeated rebuilds, so Fresh Animations Objects won't animate those. Cosmetic only.
* The title panorama is a morning village captured with software rendering. A golden-hour or winter capture could replace it later (`tools/client_smoke_test.py --scenario showcase --update-panorama`, then `tools/gen_resources.py`).
* **Spatial GUI** (3D inventory panels):
  * JEI's item list and a few small mod buttons stay flat on screen around the panel.
  * The mod is in early development (its author says so), so an unusual modded screen may misbehave: add it to *Disabled Screens* in its config.
  * Not yet tried with shaders on a real GPU.
* The walnut quest-book theme and Jade theme are new designs. I checked them in screenshots, not yet in long play.
* **1.1 menus:** Homestead GUI is a public alpha. Its tooltip sprites use file names 26.1 rejects, so item tooltips
  keep Legendary Tooltips' look. Its own container screens are unfinished, so Recolourful Containers stays on top for
  every container. It also logs a harmless error about an optional "Wynncraft" hotbar variant.
* **1.1 animations** are judged from mod pages and a crash-free client run; a screenshot can't show motion. Check
  them in play (`docs/test-checklist.md` §7). Fancy World Animations and A Good Place mostly cover vanilla blocks;
  modded doors (Macaw's) may still snap open.
* **Spawn Animations** covers vanilla hostile mobs. Its optional compatibility data pack for modded mobs isn't
  included (unclear licence), so modded mobs still appear normally.
* **C2ME** for 26.1.2 is a beta build. If you ever see world-generation errors or a corrupted chunk, remove it first
  (and tell me).

## Not verified in the cloud (needs your PC)
Real-GPU rendering, shaders, Distant Horizons, sound (ambience, Sound Physics), keybinding conflicts, mouse/keyboard feel, multiplayer, long-session memory use and performance, Physics Mod Pro, Create Fly.

## Remaining work (next sessions)
1. Run `docs/test-checklist.md` locally and feed back the results.
2. Tune energy draw and generation, and spoilage times, from play-testing.
3. Better appliance models (cabinet-style fridge and range), and a rotor renderer for the turbine.
4. ~~Energy task for town power~~ — done in 1.0.0 ("Power for the Town": 500,000 E into a Task Screen).
5. Re-evaluate Serene Seasons and Spoiled when newer 26.1.x builds ship; drop the workarounds if they're fixed upstream.
