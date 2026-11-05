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
* The "Visit a Village" quest uses the `#minecraft:village` structure tag. The registry dump doesn't include tags, so it's verified only by FTB Quests loading without error.
* Some item quests name one wood variant (oak drawers). The descriptions say so.

## Upstream issues worked around (re-check when updating)
* Serene Seasons 26.1.2.0.7: infinite recursion in its client `Biome#hasPrecipitation` mixin crashes singleplayer. Fixed by a one-call-site redirect in homestead-core (`SereneSeasonsRecursionFixMixin`).
* Spoiled 12.1.0: the spoil-recipe cache is filled only on the client. Fixed in homestead-core (`SpoiledCompat`).
* Wilder Wild 4.2.9: intermittent registry-load crash alongside Terralith. Removed from the pack.

## Not verified in the cloud (needs your PC)
Real-GPU rendering, shaders, Distant Horizons, sound (ambience, Sound Physics), keybinding conflicts, mouse/keyboard feel, multiplayer, long-session memory use and performance, Physics Mod Pro, Create Fly.

## Remaining work (next sessions)
1. Run `docs/test-checklist.md` locally and feed back the results.
2. Tune energy draw and generation, and spoilage times, from play-testing.
3. Better appliance models (cabinet-style fridge and range), and a rotor renderer for the turbine.
4. Possibly an energy task for town power using FTB's `tech_reborn_energy` task type.
5. Re-evaluate Serene Seasons and Spoiled when newer 26.1.x builds ship; drop the workarounds if they're fixed upstream.
