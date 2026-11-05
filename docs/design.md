# Jules' Homestead — Gameplay Design

## Experience pillars
1. **Home** — build and furnish a comfortable house; shelter matters because weather matters.
2. **Food** — crops, livestock and cooking feed the household *and* the town (requests, trades, taxes).
3. **Seasons & storms** — winter and severe fronts are real, forecastable challenges that reward preparation.
4. **Power** — renewable electricity runs household appliances; batteries carry you through nights and storms.
5. **Town** — residents arrive, work, marry and pay taxes in a town *you* built from nothing.
6. **Guidance** — a hand-authored quest book teaches each system once, then sets meaningful projects.

Tone: grounded, welcoming, vanilla+. Not a fantasy RPG, horror or punishing death simulator. Destruction is conservative and configurable.

## System ownership (one owner per concern)

| Concern | Owner | Notes |
|---|---|---|
| Seasons, crop fertility, seasonal biome temperature | **Serene Seasons** | Season length configured in `configs/`. |
| Body temperature (cold) | **Thermoo** (API) + **Frostiful** (cold content) | Fur/wool armour, warmth from light, wet-cold multiplier. |
| Body temperature (heat) + thirst in heat | **Scorchful** | Thirst only bites in hot conditions — a decision, not a chore. |
| Seasons → temperature bridge | **thermoo-patches** (bundled Serene Seasons patch) | Verified in jar contents (5.0.0). |
| Ordinary rain/snow/thunder | **Vanilla** (+Serene Seasons' seasonal precipitation) | |
| Severe fronts (storms, blizzards), wind, forecasts | **homestead-core** | See `weather-design.md`. Drives vanilla weather flags during an event. |
| Fog during snowy/sandy thunderstorms (client) | **Immersive Storms** (bundled in Scorchful) | Client-only visuals keyed to vanilla thunder. |
| Food spoilage | **Spoiled** | Slow defaults; powered fridges preserve. |
| Electricity API & units | **Team Reborn Energy** (`E`) | Shared by Energized Power, homestead-core, FTB Quests energy tasks. |
| Generation, storage, cables, machines | **Energized Power** + homestead-core wind/hydro | Tech Reborn deliberately excluded (duplicate tin, cables, machines). |
| Cooking backbone | **Farmer's Delight Refabricated** (+More Delight, Storage Delight) | |
| Household appliances | **homestead-core** (range, fridge, heater) + EP Powered Lamp | |
| Residents, families, building types, taxes | **MCA Reborn** | AI chat disabled by default. |
| Quests | **FTB Quests** | Content authored in `quests/`. |
| Recipe viewer | **JEI** (only) | |
| Terrain | **Terralith + Tectonic** (+ Lithostitched) | One overworld shaper pair; no other biome-placement mods. |

## Town progression (quest chapters follow this)
1. **Founding Camp** — shelter before the first night, campfire cooking, a bed, a chest. Learn temperature (Frostiful HUD) and seasons (SeasonHUD).
2. **First Homes & Fields** — a proper house (manual milestone), tilled fields, a first livestock pen, Farmer's Delight cooking pot & skillet over a campfire/stove.
3. **Weather Wise** — barometer, fur/wool clothing, firewood/heat, cellar storage; ride out a first storm or blizzard.
4. **Workshop & Power** — tin & copper, Energized Power basics, solar panel, wind turbine or water wheel, Battery Box, cables. First electric appliance.
5. **The Modern Kitchen** — Electric Range, Refrigerator, Powered Lamps, Space Heater; meals for the household.
6. **Bakery & Market** — residents arrive (MCA), market stalls (Farming for Blockheads market), town requests (deliver food/goods for emeralds & rewards).
7. **Established Community** — named buildings MCA recognises (house, bakery, blacksmith, inn…), taxes, waystones network, long winter stockpile.
8. **Horizons** — exploration: villages/towns (Towns & Towers), dungeons (YUNG's), biomes; long-term goals.

Early survival never requires electricity: campfires, the Farmer's Delight stove, vanilla furnaces/smokers, cellars and fires all keep working. Electricity *removes chores* (fuel, spoilage, cold rooms) rather than gating survival.

## Economy
* Currency is vanilla **emeralds**. Sources: villager/MCA trades, MCA taxes, town-request quests, selling produce.
* Sinks: Farming for Blockheads market (seeds/saplings/animals), MCA services, upgrade materials.
* Quest rewards are modest: ingredients, emeralds, a few convenience items. Rewards never hand out machines from a later tier.
