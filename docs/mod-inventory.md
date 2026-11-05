# Mod Inventory (generated)

Generated from `pack/mods.lock.json` (resolved 2026-11-05T04:43:33+00:00). Do not edit by hand: run `python3 -I tools/inventory.py`.

Target: Minecraft 26.1.2, Fabric Loader 0.20.1. 137 mods, 17 resource packs, 4 shader packs.

Side legend: **C** client, **S** dedicated server; ✓ required, ○ optional (mrpack), ✗ not installed there.
Distribution: Modrinth-hosted files are referenced by Modrinth CDN URL; FTB files by FTB's official maven URL; only the pack's own MIT modules are bundled.

## core

| Mod | Version (Modrinth version ID) | MC / loaders | Sides C/S | License | Purpose / notes | Requires |
|---|---|---|---|---|---|---|
| [Fabric API](https://modrinth.com/mod/fabric-api) | 0.155.3+26.1.2 (`3dM0X6ou`) | 26.1, 26.1.1, 26.1.2 / fabric | ✓/✓ | Apache-2.0 | Fabric API. |  |
| `homestead-pack-data` (datapacks/homestead-pack-data) | 1.0.0 (local build) | 26.1.2 / fabric | ✓/✓ | MIT (bundled) | Custom data module: Serene Seasons tags for Farmer's Delight/Ube's/Rustic crops, removes EP weather & time controllers. |  |
| [Mod Menu](https://modrinth.com/mod/modmenu) | 18.0.2 (`ZJ6YTrMY`) | 26.1, 26.1.1, 26.1.2 / fabric, quilt | ✓/✗ | MIT | In-game mod list and config entry points. | fabric-api, placeholder-api |

## performance

| Mod | Version (Modrinth version ID) | MC / loaders | Sides C/S | License | Purpose / notes | Requires |
|---|---|---|---|---|---|---|
| [Dynamic FPS](https://modrinth.com/mod/dynamic-fps) | 3.11.7 (`HV67Kt1R`) | 26.1, 26.1.1, 26.1.2 / fabric, quilt | ✓/✗ | MIT | Throttle FPS when unfocused/idle. | fabric-api |
| [Entity Culling](https://modrinth.com/mod/entityculling) | 1.11.3 (`VhQ51u7a`) | 26.1, 26.1.1, 26.1.2 / fabric | ✓/✗ | tr7zw-Protective-License | Skip rendering hidden (block-)entities — important with furnished houses. | fabric-api |
| [FerriteCore](https://modrinth.com/mod/ferrite-core) | 9.0.0-fabric (`d5ddUdiB`) | 26.1, 26.1.1, 26.1.2 / fabric | ✓/✓ | MIT | Memory reduction. |  |
| [ImmediatelyFast](https://modrinth.com/mod/immediatelyfast) | 1.15.3+26.1-fabric (`xOA9hbpf`) | 26.1, 26.1.1, 26.1.2 / fabric, quilt | ✓/✗ | LGPL-3.0-or-later | Immediate-mode rendering speedups (HUD, text, entities). |  |
| [Krypton](https://modrinth.com/mod/krypton) | 0.3.0 (`kYAGItyj`) | 26.1, 26.1.1, 26.1.2 / fabric | ✓/✓ | LGPL-3.0-only | Network stack optimisation. |  |
| [Lithium](https://modrinth.com/mod/lithium) | mc26.1.2-0.24.7-fabric (`Oqq8TOAV`) | 26.1, 26.1.1, 26.1.2 / fabric, quilt | ✓/✓ | LGPL-3.0-only | Server/tick optimisation. |  |
| [More Culling](https://modrinth.com/mod/moreculling) | 1.7.4 (`hS47fcKO`) | 26.1, 26.1.1, 26.1.2 / fabric, quilt | ✓/✗ | GPL-3.0-only | Extra block culling (leaves etc.). | cloth-config |
| [Reese's Sodium Options](https://modrinth.com/mod/reeses-sodium-options) | mc26.1.2-2.2.4+fabric (`CqmgNDX3`) | 26.1.2 / fabric | ✓/✗ | MIT | Readable tabbed video settings. | sodium |
| [Sodium](https://modrinth.com/mod/sodium) | mc26.1.2-0.9.2-fabric (`tZQ3jqnf`) | 26.1.2 / fabric | ✓/✗ | Polyform-Shield-1.0.0 | Renderer (client baseline). |  |
| [Sodium Extra](https://modrinth.com/mod/sodium-extra) | mc26.1.2-0.9.5+fabric (`nP6HUWD1`) | 26.1.2 / fabric | ✓/✗ | LGPL-3.0-only | Extra Sodium video options (fog, particles, animations). | sodium |

## weather

| Mod | Version (Modrinth version ID) | MC / loaders | Sides C/S | License | Purpose / notes | Requires |
|---|---|---|---|---|---|---|
| [Frostiful](https://modrinth.com/mod/frostiful) | 3.0.2+26.1.x (`RLZkcX2U`) | 26.1, 26.1.1, 26.1.2 / fabric, quilt | ✓/✓ | LGPL-3.0-only | Cold content: freezing, fur armour, warmth from light/fire. (26.1.x build is labelled beta upstream.) | cardinal-components-api, fabric-api, thermoo, yacl |
| [Scorchful](https://modrinth.com/mod/scorchful) | 0.18.3+26.1.x (`auMRMxeg`) **beta** | 26.1, 26.1.1, 26.1.2 / fabric, quilt | ✓/✓ | LGPL-3.0-only | Heat + thirst in hot conditions; bundles Immersive Storms (snow/sand fog in thunderstorms, client). | cardinal-components-api, fabric-api, thermoo, yacl |
| [SeasonHud-Fabric](https://modrinth.com/mod/seasonhud-fabric) | 26.1.2-2.0.11 (`dtsgTLCg`) | 26.1, 26.1.1, 26.1.2 / fabric | ✓/✓ | MIT | Season indicator on the HUD/minimap. | forge-config-api-port |
| [Serene Seasons](https://modrinth.com/mod/serene-seasons) | 26.1.2.0.7 (`kxB8vG5W`) **beta** | 26.1.2 / fabric | ✓/✓ | All-Rights-Reserved | OWNER of seasons: seasonal biome temperature, crop fertility, winter snow. | fabric-api |
| [Snow Under Trees](https://modrinth.com/mod/snow-under-trees) | v1.6.1 (`ubJYO77l`) | 26.1, 26.1.1, 26.1.2 / fabric, quilt | ✓/✓ | MIT | Snow settles under trees in snowy biomes / winter (supports Serene Seasons and Terralith). | fabric-api, forge-config-api-port |
| [Thermoo](https://modrinth.com/mod/thermoo) | 10.1.5 (`BeNHdIXt`) | 26.1, 26.1.1, 26.1.2 / fabric, quilt | ✓/✓ | LGPL-3.0-only | OWNER of body temperature (API). homestead-core feeds blizzard cold and heater warmth through it. | cardinal-components-api, fabric-api |
| [Thermoo Patches](https://modrinth.com/mod/thermoo-patches) | 5.0.0 (`HG3aPqWx`) | 26.1, 26.1.1, 26.1.2 / fabric, quilt | ✓/✓ | LGPL-3.0-only | Verified Thermoo<->Serene Seasons bridge (bundled serene-seasons patch). | fabric-api, thermoo, yacl |

## food

| Mod | Version (Modrinth version ID) | MC / loaders | Sides C/S | License | Purpose / notes | Requires |
|---|---|---|---|---|---|---|
| [Animal Feeding Trough](https://modrinth.com/mod/animal_feeding_trough) | 1.2.1+26.1.2 (`HwX7awp8`) | 26.1.2 / fabric | ✓/✓ | MIT | Livestock feeding troughs (animal husbandry automation without industry). |  |
| [AppleSkin](https://modrinth.com/mod/appleskin) | 3.0.10+mc26.1.2 (`zLlqqiTA`) | 26.1, 26.1.1, 26.1.2 / fabric | ✓/✓ | Unlicense | Hunger/saturation preview. | fabric-api |
| [Crate Delight](https://modrinth.com/mod/crate-delight) | 26.07.01-26.1-fabric (`BN65jCGU`) | 26.1, 26.1.1, 26.1.2 / fabric | ✓/✓ | MIT | Crop crates for compact produce storage. |  |
| [Crops Love Rain](https://modrinth.com/mod/crops-love-rain) | 3.2.2+26.3-fabric (`2wR3dP9w`) | 26.1, 26.1.1, 26.1.2 / fabric, quilt | ✓/✓ | MIT | Crops grow faster in rain — weather matters to farms. | fabric-api, forge-config-api-port |
| [Farmer's Delight Refabricated](https://modrinth.com/mod/farmers-delight-refabricated) | 26.1-3.6.30 (`3s3vr72P`) | 26.1, 26.1.1, 26.1.2 / fabric | ✓/✓ | MIT | Cooking backbone: cooking pot, skillet, cutting board, stove, meals, rice/tomato/cabbage/onion. | fabric-api |
| [Farming for Blockheads](https://modrinth.com/mod/farming-for-blockheads) | 26.1.2.6+fabric-26.1.2 (`5GOBem5e`) | 26.1.2 / fabric | ✓/✓ | All-Rights-Reserved | Market (buy seeds/saplings/animals), shipping bin (sell produce), sprinklers (irrigation), feeding trough. | balm, fabric-api |
| [More Delight (for Farmer's Delight)](https://modrinth.com/mod/more-delight) | 26.05.26-26.1-fabric (`q01zzzeZ`) | 26.1, 26.1.1, 26.1.2 / fabric | ✓/✓ | MIT | Extra Farmer's Delight meals and ingredients. | delight-lib, farmers-delight-refabricated |
| [RightClickHarvest](https://modrinth.com/mod/rightclickharvest) | 4.6.2+26.1.x (`spaSLGmu`) | 26.1, 26.1.1, 26.1.2 / fabric, quilt | ✓/✓ | MIT | Right-click to harvest and replant crops. | fabric-api, jamlib |
| [Rustic Delight](https://modrinth.com/mod/rustic-delight) | 1.7.0 (`kVLLFl6q`) | 26.1, 26.1.1, 26.1.2 / fabric | ✓/✓ | MIT | Bell peppers, coffee, cotton; Farmer's Delight style dishes. | cloth-config, fabric-api, farmers-delight-refabricated |
| [Spoiled](https://modrinth.com/mod/spoiled) | 12.1.0 (`bEOjyZyc`) **alpha** | 26.1.2 / fabric | ✓/✓ | MIT | OWNER of food spoilage. Slow defaults; homestead-core fridge preserves while powered. | fabric-api, forge-config-api-port |
| [Storage Delight](https://modrinth.com/mod/storage-delight) | 26.07.01-26.1-fabric (`JXJ3B666`) | 26.1, 26.1.1, 26.1.2 / fabric | ✓/✓ | MIT | Kitchen cabinets/drawers matching Farmer's Delight. |  |
| [Ube's Delight](https://modrinth.com/mod/ubes-delight) | 26.1.2-0.4.15 (`YKJCSGVJ`) | 26.1, 26.1.1, 26.1.2 / fabric | ✓/✓ | MIT | Additional crops (ube, garlic, ginger, lemongrass) and dishes using Farmer's Delight tags. | fabric-api, farmers-delight-refabricated |

## build

| Mod | Version (Modrinth version ID) | MC / loaders | Sides C/S | License | Purpose / notes | Requires |
|---|---|---|---|---|---|---|
| [Effortless Building](https://modrinth.com/mod/effortless-building) | 4.3 (`tUZmcsh6`) | 26.1.2 / fabric | ✓/✓ | LGPL-3.0-only | Mirror/array/line building tools. |  |
| [Gardener's Dream](https://modrinth.com/mod/gardeners-dream) | 2.6+mod (`jCeXp97b`) | 26.1, 26.1.1, 26.1.2 / fabric, forge, neoforge, quilt | ✓/✓ | All-Rights-Reserved | Place any plant in flower pots and decorated pots (cozy decorating). |  |
| [Macaw's Bridges](https://modrinth.com/mod/macaws-bridges) | 3.1.2 (`QLLyZd4W`) | 26.1, 26.1.1, 26.1.2 / fabric | ✓/✓ | All-Rights-Reserved | Bridges. |  |
| [Macaw's Doors](https://modrinth.com/mod/macaws-doors) | 1.1.5 (`RG8zptcU`) | 26.1, 26.1.1, 26.1.2 / fabric | ✓/✓ | MIT | Doors. | fabric-api |
| [Macaw's Fences and Walls](https://modrinth.com/mod/macaws-fences-and-walls) | 1.2.1 (`oWA108EY`) | 26.1, 26.1.1, 26.1.2 / fabric | ✓/✓ | MIT | Fences and walls. |  |
| [Macaw's Furniture](https://modrinth.com/mod/macaws-furniture) | 3.4.2 (`RZnGeGr0`) | 26.1, 26.1.1, 26.1.2 / fabric | ✓/✓ | All-Rights-Reserved | Primary furniture set (kitchens, bedrooms, offices). | fabric-api |
| [Macaw's Lights and Lamps](https://modrinth.com/mod/macaws-lights-and-lamps) | 1.1.5 (`3pQc7GPB`) | 26.1, 26.1.1, 26.1.2 / fabric | ✓/✓ | All-Rights-Reserved | Decorative (unpowered) lamps — early lighting alternative. | fabric-api |
| [Macaw's Paintings](https://modrinth.com/mod/macaws-paintings) | 1.1.0 (`GOQxUGTV`) | 26.1, 26.1.1, 26.1.2 / fabric | ✓/✓ | All-Rights-Reserved | Paintings. | fabric-api |
| [Macaw's Paths and Pavings](https://modrinth.com/mod/macaws-paths-and-pavings) | 1.1.1 (`Lhx6kseh`) | 26.1, 26.1.1, 26.1.2 / fabric | ✓/✓ | MIT | Paths and pavings for town streets. | fabric-api |
| [Macaw's Roofs](https://modrinth.com/mod/macaws-roofs) | 2.3.2 (`nOoMKDXX`) | 26.1, 26.1.1, 26.1.2 / fabric | ✓/✓ | All-Rights-Reserved | Roofs. | fabric-api |
| [Macaw's Stairs](https://modrinth.com/mod/macaws-stairs) | 1.0.2 (`jvxqO78F`) | 26.1, 26.1.1, 26.1.2 / fabric | ✓/✓ | All-Rights-Reserved | Stairs and balconies. |  |
| [Macaw's Trapdoors](https://modrinth.com/mod/macaws-trapdoors) | 1.1.5 (`OjCybdsq`) | 26.1, 26.1.1, 26.1.2 / fabric | ✓/✓ | MIT | Trapdoors. |  |
| [Macaw's Windows](https://modrinth.com/mod/macaws-windows) | 2.4.2 (`YAGJmlsb`) | 26.1, 26.1.1, 26.1.2 / fabric | ✓/✓ | All-Rights-Reserved | Windows, curtains, blinds. | fabric-api |
| [Rechiseled](https://modrinth.com/mod/rechiseled) | 1.2.6-fabric-mc26.1 (`MSyzZAAt`) | 26.1, 26.1.1, 26.1.2 / fabric, quilt | ✓/✓ | All-Rights-Reserved | Chisel block variants (single chisel system). | fabric-api, fusion-connected-textures, supermartijn642s-config-lib, supermartijn642s-core-lib |

## power

| Mod | Version (Modrinth version ID) | MC / loaders | Sides C/S | License | Purpose / notes | Requires |
|---|---|---|---|---|---|---|
| [Energized Power](https://modrinth.com/mod/energized-power) | 3.0.7+26.1.x-fabric (`8athtrg0`) | 26.1, 26.1.1, 26.1.2 / fabric | ✓/✓ | MIT | Electrical backbone on TR Energy: solar I-IV, coal engine, battery boxes, cables, powered lamp/furnace, crusher. | fabric-api |
| `homestead-core` (custom-mods/homestead-core) | 1.0.0 (local build) | 26.1.2 / fabric | ✓/✓ | MIT (bundled) | Custom: severe fronts (storm/blizzard), shelter/exposure, forecast, wind turbine, water wheel, refrigerator, electric range, space heater. | fabric-api |

## town

| Mod | Version (Modrinth version ID) | MC / loaders | Sides C/S | License | Purpose / notes | Requires |
|---|---|---|---|---|---|---|
| [Easy NPC](https://modrinth.com/mod/easy-npc) | 7.18.0 (`ykWif2B1`) | 26.1.2 / fabric, quilt | ✓/✓ | MIT | Hand-placed town NPCs (mentor/mayor) with dialog and trades. | easy-npc-config-ui, easy-npc-core |
| [EasyNPC x FTB Quests Compat](https://modrinth.com/mod/easynpc-x-ftb-quests-compat) | 26.1.2+1.0.2 (`WuxX4yOP`) | 26.1.2 / fabric | ✓/✓ | MIT | Lets Easy NPC dialog actions open/complete FTB quests. |  |
| [MCA Reborn](https://modrinth.com/mod/minecraft-comes-alive-reborn) | 8.1.11+26.1.2 (`Kzv4GU7X`) | 26.1.2 / fabric | ✓/✓ | GPL-3.0-only | Residents with families, moods, professions, building detection, taxes. AI chat disabled by default. | fabric-api |

## quests

| Mod | Version (Modrinth version ID) | MC / loaders | Sides C/S | License | Purpose / notes | Requires |
|---|---|---|---|---|---|---|
| [Better Advancements](https://modrinth.com/mod/better-advancements) | 0.6.0.76 (`EJXDbJy9`) | 26.1.2 / fabric | ✓/✗ | Dont-Be-a-Jerk | Readable advancement screen. |  |
| [ftb-library-fabric](https://www.curseforge.com/minecraft/mc-mods/ftb-library-fabric) | 26.1.2.11 (`FTB maven`) | 26.1.2 / fabric | ✓/✓ | All-Rights-Reserved (referenced from maven.ftb.dev, not re-hosted) | FTB Quests dependency (official FTB maven, All Rights Reserved — referenced, not re-hosted). |  |
| [ftb-quests-fabric](https://www.curseforge.com/minecraft/mc-mods/ftb-quests-fabric) | 26.1.2.9 (`FTB maven`) | 26.1.2 / fabric | ✓/✓ | All-Rights-Reserved (referenced from maven.ftb.dev, not re-hosted) | Quest book UI, pinned-quest HUD tracker, rewards. Content authored in quests/. |  |
| [ftb-teams-fabric](https://www.curseforge.com/minecraft/mc-mods/ftb-teams-fabric) | 26.1.2.5 (`FTB maven`) | 26.1.2 / fabric | ✓/✓ | All-Rights-Reserved (referenced from maven.ftb.dev, not re-hosted) | FTB Quests dependency. |  |
| [ftb-xmod-compat-fabric](https://www.curseforge.com/minecraft/mc-mods/ftb-xmod-compat-fabric) | 26.1.2.5 (`FTB maven`) | 26.1.2 / fabric | ✓/✓ | All-Rights-Reserved (referenced from maven.ftb.dev, not re-hosted) | FTB <-> JEI integration (quest bookmarks/recipes). |  |

## world

| Mod | Version (Modrinth version ID) | MC / loaders | Sides C/S | License | Purpose / notes | Requires |
|---|---|---|---|---|---|---|
| [Explorify](https://modrinth.com/mod/explorify) | v1.6.5 (`CuBdAr31`) | 26.1, 26.1.1, 26.1.2 / fabric, forge, neoforge | ✓/✓ | All-Rights-Reserved | Vanilla-friendly small structures (camps, ruins, houses) that make travel interesting. |  |
| [Friends&Foes (Fabric/Quilt)](https://modrinth.com/mod/friends-and-foes) | fabric-5.0.4+mc26.1.2 (`MReCzJrI`) | 26.1, 26.1.1, 26.1.2 / fabric, quilt | ✓/✓ | CC-BY-NC-ND-4.0 | Vanilla-style mobs (moobloom, beekeeper villagers, copper golem helpers). | fabric-api, resourceful-lib |
| [Nature's Compass](https://modrinth.com/mod/natures-compass) | 26.1-2.5.1-fabric (`AlyHZXyH`) | 26.1, 26.1.1, 26.1.2 / fabric | ✓/✓ | CC-BY-NC-SA-4.0 | Find biomes for specific crops/resources. |  |
| [Remove Stardust Labs Intro Message](https://modrinth.com/mod/remove-terralith-intro-message) | 1.2.2 (`BHr8OLOk`) | 26.1, 26.1.1, 26.1.2 / fabric, forge, neoforge, quilt | ✓/✓ | MIT | Removes the Stardust Labs intro chat message on world join (cleaner first impression). |  |
| [Structory](https://modrinth.com/mod/structory) | 1.3.17 (`TUbwu7eG`) | 26.1, 26.1.1, 26.1.2 / fabric, forge, neoforge, quilt | ✓/✓ | Stardust-Labs-License | Atmospheric ruins and small structures with light lore (Stardust Labs, designed alongside Terralith). |  |
| [Structory: Towers](https://modrinth.com/mod/structory-towers) | 1.0.17 (`ziO4YIv1`) | 26.1, 26.1.1, 26.1.2 / fabric, forge, neoforge, quilt | ✓/✓ | Stardust-Labs-License | Biome-themed towers to discover (Structory add-on). |  |
| [Tectonic](https://modrinth.com/mod/tectonic) | 3.0.28-fabric-26.1 (`5v9Cz8zS`) | 26.1, 26.1.1, 26.1.2 / fabric | ✓/✓ | MIT | Terrain shape (mountains, rivers) — designed to pair with Terralith. | fabric-api, lithostitched |
| [Terralith](https://modrinth.com/mod/terralith) | 2.6.2 (`FCzSjHeG`) | 26.1, 26.1.1, 26.1.2 / fabric | ✓/✓ | Stardust-Labs-License | OWNER of overworld biomes (vanilla-block biomes, lush forests, rivers). | lithostitched |
| [Towns and Towers](https://modrinth.com/mod/towns-and-towers) | 1.13.11 (`eN3WLQ3P`) | 26.1, 26.1.1, 26.1.2 / fabric, neoforge, quilt | ✓/✓ | CC-BY-NC-SA-4.0 | Varied villages/outposts worth exploring (not a finished town to move into). | cristel-lib |
| [YUNG's Better Dungeons](https://modrinth.com/mod/yungs-better-dungeons) | 26.1.2-Fabric-6.1.1 (`ZAy29qXU`) | 26.1.1, 26.1.2 / fabric | ✓/✓ | LGPL-3.0-only | Exploration. | cloth-config, fabric-api, yungs-api |
| [YUNG's Better Mineshafts](https://modrinth.com/mod/yungs-better-mineshafts) | 26.1.2-Fabric-6.1.1 (`PCdPUNQs`) | 26.1.1, 26.1.2 / fabric | ✓/✓ | LGPL-3.0-only | Exploration. | cloth-config, fabric-api, yungs-api |
| [YUNG's Better Strongholds](https://modrinth.com/mod/yungs-better-strongholds) | 26.1.2-Fabric-6.1.1 (`aYR5vkpj`) | 26.1.1, 26.1.2 / fabric | ✓/✓ | LGPL-3.0-only | Exploration. | cloth-config, fabric-api, yungs-api |

## qol

| Mod | Version (Modrinth version ID) | MC / loaders | Sides C/S | License | Purpose / notes | Requires |
|---|---|---|---|---|---|---|
| [Carry On](https://modrinth.com/mod/carry-on) | 2.10.0 (`ISFd6ie5`) | 26.1, 26.1.1, 26.1.2 / fabric | ✓/✓ | LGPL-3.0-only | Pick up chests/animals while moving house. |  |
| [Controlling](https://modrinth.com/mod/controlling) | 26.1.2.7 (`NyXJNDB4`) | 26.1.2 / fabric | ✓/✗ | MIT | Searchable keybinds + conflict view. | fabric-api, searchables |
| [Enchantment Descriptions](https://modrinth.com/mod/enchantment-descriptions) | 26.1.2.6 (`AJzO4pYf`) | 26.1, 26.1.1, 26.1.2 / fabric | ✓/✗ | LGPL-2.1-only | Enchantment tooltips. | fabric-api, prickle |
| [FallingTree](https://modrinth.com/mod/fallingtree) | 26.1.2-26.1.2.5 (`87iHk1lF`) | 26.1.2 / fabric, forge, neoforge | ✓/✓ | LGPL-3.0-only | Fell whole trees (less chore, configurable). |  |
| [Inventory Profiles Next](https://modrinth.com/mod/inventory-profiles-next) | fabric-26.1-2.3.8 (`lOBWkkzv`) | 26.1, 26.1.1, 26.1.2 / fabric | ✓/✗ | AGPL-3.0-or-later | Inventory sorting. | fabric-api, fabric-language-kotlin, libipn |
| [Jade 🔍](https://modrinth.com/mod/jade) | 26.1.9+fabric (`fW2xykin`) | 26.1, 26.1.1, 26.1.2 / fabric, quilt | ✓/✓ | CC-BY-NC-SA-4.0 | Block/entity tooltip (shows appliance power state). | fabric-api |
| [Just Enough Items (JEI)](https://modrinth.com/mod/jei) | 29.47.0.110 (`T8wS82jn`) | 26.1, 26.1.1, 26.1.2 / fabric | ✓/✓ | MIT | THE single recipe viewer. | mezzconfig |
| [Mouse Tweaks](https://modrinth.com/mod/mouse-tweaks) | 26.1-2.31-fabric (`EBIKCzuP`) | 26.1, 26.1.1, 26.1.2 / fabric | ✓/✗ | BSD-3-Clause | Mouse inventory handling. | fabric-api |
| [Shulker Box Tooltip](https://modrinth.com/mod/shulkerboxtooltip) | 5.4.3+26.1.1-fabric (`cCQDtoFu`) | 26.1.1, 26.1.2 / fabric | ✓/✓ | MIT | Container previews. |  |
| [Storage Drawers](https://modrinth.com/mod/storagedrawers) | 26.1.0.2+fabric (`rvCDTyMh`) | 26.1, 26.1.1, 26.1.2 / fabric | ✓/✓ | MIT | Pantry/workshop drawers. | fabric-api |
| [Tom's Simple Storage Mod](https://modrinth.com/mod/toms-storage) | 26.1-2.10.3-fabric (`9VRUBvKA`) | 26.1, 26.1.1, 26.1.2 / fabric | ✓/✓ | MIT | Simple storage terminal (late). |  |
| [Traveler's Backpack](https://modrinth.com/mod/travelersbackpack) | 26.1.2-11.2.12 (`nkLEWUqE`) | 26.1.1, 26.1.2 / fabric | ✓/✓ | LGPL-3.0-only | Backpacks for expeditions. | fabric-api, forge-config-api-port |
| [Waystones](https://modrinth.com/mod/waystones) | 26.1.2.17+fabric-26.1.2 (`gRvVm0oS`) | 26.1.2 / fabric | ✓/✓ | All-Rights-Reserved | Town-to-outpost travel network (craftable mid-game). | balm, fabric-api, shogi |
| [Xaero's Minimap](https://modrinth.com/mod/xaeros-minimap) | fabric-26.1.2-26.6.2 (`DVSdniw4`) | 26.1, 26.1.1, 26.1.2 / fabric, quilt | ✓/✓ | All-Rights-Reserved | Minimap. | fabric-api |
| [Xaero's World Map](https://modrinth.com/mod/xaeros-world-map) | fabric-26.1.2-1.47.2 (`ZpUBkoFm`) | 26.1, 26.1.1, 26.1.2 / fabric, quilt | ✓/✓ | All-Rights-Reserved | World map. | fabric-api |
| [Zoomify (Zoom)](https://modrinth.com/mod/zoomify) | 2.17.0+mc26.1.2 (`gwKcDOSy`) | 26.1, 26.1.1, 26.1.2 / fabric, neoforge | ✓/✗ | LGPL-3.0-only | Zoom key. | 1vrSzlao, fabric-api, fabric-language-kotlin, yacl |

## visual

| Mod | Version (Modrinth version ID) | MC / loaders | Sides C/S | License | Purpose / notes | Requires |
|---|---|---|---|---|---|---|
| [3D Skin Layers](https://modrinth.com/mod/3dskinlayers) | 1.11.4 (`hIowUmVK`) | 26.1, 26.1.1, 26.1.2 / fabric | ✓/✗ | tr7zw-Protective-License | 3D skin layers. | fabric-api |
| [Ambient Environment](https://modrinth.com/mod/ambient-environment) | 26.1.2.1 (`ZhJAYDw4`) | 26.1.2 / fabric | ✓/✗ | MIT | Subtle colour variation in grass, leaves and water (Bedrock-style), so landscapes feel less flat. | fabric-api |
| [AmbientSounds](https://modrinth.com/mod/ambientsounds) | 6.3.6 (`DKPRMIAu`) | 26.1.2 / fabric | ✓/✗ | LGPL-3.0-only | Biome/time/weather ambience. | creativecore, fabric-api |
| [Camera Overhaul](https://modrinth.com/mod/cameraoverhaul) | 2.1.2-fabric+mc.26.1-26.1.2 (`RVjlBM9t`) | 26.1, 26.1.1, 26.1.2 / fabric, quilt | ✓/✗ | GPL-3.0-or-later | Gentle camera roll/sway for first-person immersion (tuned down in configs; can be disabled). | cloth-config |
| [Continuity](https://modrinth.com/mod/continuity) | 3.0.1-beta.2+26.1 (`l1mM6Ov7`) | 26.1, 26.1.1, 26.1.2 / fabric, quilt | ✓/✗ | LGPL-3.0-only | Connected textures (glass in houses/greenhouses). | fabric-api |
| [[EMF] Entity Model Features](https://modrinth.com/mod/entity-model-features) | 3.3.12-fabric-26.1 (`zh2AVI88`) | 26.1, 26.1.1, 26.1.2 / fabric, quilt | ✓/✗ | LGPL-3.0-only | Required by Fresh Animations. | entitytexturefeatures |
| [[ETF] Entity Texture Features](https://modrinth.com/mod/entitytexturefeatures) | 7.2.7-fabric-26.1 (`JxWjtiQZ`) | 26.1, 26.1.1, 26.1.2 / fabric, quilt | ✓/✗ | LGPL-3.0-only | Required by Fresh Animations. |  |
| [Falling Leaves](https://modrinth.com/mod/fallingleaves) | 2.0.7+26.1 (`JYRSvzQW`) | 26.1, 26.1.1, 26.1.2 / fabric | ✓/✗ | MIT | Leaf particles. | cloth-config, fabric-api |
| [First-person Model](https://modrinth.com/mod/first-person-model) | 2.7.3 (`J7gA0RdJ`) | 26.1, 26.1.1, 26.1.2 / fabric | ✓/✗ | MIT | See your body in first person. | fabric-api, not-enough-animations |
| [LambDynamicLights - Dynamic Lights](https://modrinth.com/mod/lambdynamiclights) | 4.11.2+26.1.2 (`USlEyPPR`) | 26.1, 26.1.1, 26.1.2 / fabric, neoforge, quilt | ✓/✗ | Lambda-License | Held-torch dynamic light. | fabric-api |
| [Legendary Tooltips](https://modrinth.com/mod/legendary-tooltips) | 1.6.1.1 (`f02TIUcN`) | 26.1, 26.1.1, 26.1.2 / fabric | ✓/✗ | CC-BY-NC-ND-4.0 | Framed, readable item tooltips with item previews (pleasant screens). | fabric-api, iceberg, prism-lib |
| [Not Enough Animations](https://modrinth.com/mod/not-enough-animations) | 1.12.7 (`1q6LdvOS`) | 26.1, 26.1.1, 26.1.2 / fabric | ✓/✗ | tr7zw-Protective-License | First/third-person animation polish. | fabric-api |
| [Nuit](https://modrinth.com/mod/nuit) | mc26.1.2-1.0.0-beta.3+fabric (`i1Y5SmCe`) | 26.1, 26.1.1, 26.1.2 / fabric | ✓/✗ | MIT | Custom skybox support (formerly FabricSkyboxes) for the Dramatic Skys resource pack. | fabric-api |
| [Overflowing Bars](https://modrinth.com/mod/overflowing-bars) | 26.1.0 (`IxKzb0Hv`) | 26.1, 26.1.1, 26.1.2 / fabric | ✓/✗ | MPL-2.0 | Compact health/armour rows (thermoo-patches ships a compatibility patch for it). | fabric-api, forge-config-api-port, puzzles-lib |
| [Particle Rain](https://modrinth.com/mod/particle-rain) | v4.0.3+26.1-fabric (`3urZjvK3`) | 26.1.2, 26.1.1, 26.1 / fabric | ✓/✗ | MIT | Particle rain/snow visuals (client). Compatible with Immersive Storms per its docs. | fabric-api |
| [Presence Footsteps](https://modrinth.com/mod/presence-footsteps) | 1.13.3+26.1 (`wpMVyDNf`) | 26.1, 26.1.1, 26.1.1-rc-1, 26.1.2, 26.1.2-rc-1 / fabric, quilt | ✓/✗ | Polyform-Shield-1.0 | Footstep sounds. | fabric-api |
| [Remove Reloading Screen](https://modrinth.com/mod/rrls) | 5.2.6+mc26.1 (`J8aRjeWe`) **beta** | 26.1, 26.1.1, 26.1.2 / fabric, neoforge | ✓/✗ | OSL-3.0 | Resource reloads happen in the background instead of a blocking full-screen overlay. |  |
| [Sound Physics Remastered](https://modrinth.com/mod/sound-physics-remastered) | fabric-1.5.2+26.1.2 (`Acwz0QbP`) **beta** | 26.1, 26.1.1, 26.1.2 / fabric, quilt | ✓/✓ | GPL-3.0-only | Reverb/occlusion — storms sound muffled indoors. |  |
| [Spatial GUI](https://modrinth.com/mod/spatial-gui) | 1.10.1-fabric+26.1.x (`MF4iKnLw`) | 26.1.2, 26.1.1, 26.1 / fabric | ✓/✗ | MIT | Inventory and container screens drawn as a 3D panel in the world (camera swings to third person, player faces the panel). Client-only; needs YACL + Mod Menu (already in the pack). | fabric-api, modmenu, yacl |
| [Subtle Effects](https://modrinth.com/mod/subtle-effects) | 1.15.1 (`SaXeAq2M`) | 26.1.2 / fabric | ✓/✓ | All-Rights-Reserved | Ambient detail: fire sparks, better smoke, snoring Z's, dust clouds, glowing dust; every effect toggleable. Replaces Visuality. | fabric-api, fzzy-config |
| [Wakes](https://modrinth.com/mod/wakes) | 0.6.0+26.1 (`p2RaO0z1`) **beta** | 26.1, 26.1.1, 26.1.2 / fabric | ✓/✗ | GPL-3.0-only | Boat and swimming wakes/splashes on water. | fabric-api |

## visual-optional

| Mod | Version (Modrinth version ID) | MC / loaders | Sides C/S | License | Purpose / notes | Requires |
|---|---|---|---|---|---|---|
| [Distant Horizons](https://modrinth.com/mod/distanthorizons) | 3.3.3-26.1.2 (`QHxoccsN`) | 26.1, 26.1.1, 26.1.2 / fabric, neoforge | ○/○ | LGPL-3.0-only | Optional long-distance terrain LOD. Off by default (performance cost). |  |
| [Iris Shaders](https://modrinth.com/mod/iris) | 1.11.4+26.1-fabric (`sZbVsl2Q`) | 26.1, 26.1.1, 26.1.2 / fabric | ✓/✗ | LGPL-3.0-only | Shader loader. Installed but shaders OFF by default (optional preset). | sodium |

## optional-create

| Mod | Version (Modrinth version ID) | MC / loaders | Sides C/S | License | Purpose / notes | Requires |
|---|---|---|---|---|---|---|
| [Create Fly](https://modrinth.com/mod/create-fly) | 26.1.2-6.0.9-4 (`wV5IQLKg`) | 26.1.2 / fabric | ○/○ | CC0-1.0 | OPTIONAL Create (community Fabric port). Mechanical only; not electricity. Off unless chosen at import. |  |

## library

| Mod | Version (Modrinth version ID) | MC / loaders | Sides C/S | License | Purpose / notes | Requires |
|---|---|---|---|---|---|---|
| [Animated Items](https://modrinth.com/resourcepack/animated-items) | 1.6.3 (`OwMX7WV9`) | 26.1, 26.1.1, 26.1.2 / minecraft | ✓/✗ | CC-BY-NC-4.0 | Subtle item texture animations. |  |
| [Balm](https://modrinth.com/mod/balm) | 26.1.2.15+fabric-26.1.2 (`GsjBKPJs`) | 26.1.2 / fabric | ✓/✓ | All-Rights-Reserved | Dependency of farming-for-blockheads, waystones | fabric-api |
| [Better Lanterns](https://modrinth.com/resourcepack/better-lanterns) | 1.3.2-mc26.1 (`wfM00H4G`) | 26.1, 26.1.1, 26.1.2 / minecraft | ✓/✗ | All-Rights-Reserved | 3D lanterns and chains. |  |
| [Motschen's Better Leaves](https://modrinth.com/resourcepack/better-leaves) | 9.6 (`w4ReEpuG`) | 26.1, 26.1.1, 26.1.2 / minecraft | ✓/✗ | MIT | Bushier vanilla leaves. |  |
| [Cardinal Components API](https://modrinth.com/mod/cardinal-components-api) | 8.0.1 (`d78LiKJ8`) | 26.1, 26.1.1, 26.1.2 / fabric, quilt | ✓/✓ | MIT | Dependency of frostiful, scorchful, thermoo |  |
| [Clean Connected Glass](https://modrinth.com/resourcepack/clean-connected-glass) | 0.0.2 (`gN2DnGei`) | 26.1, 26.1.1, 26.1.2 / minecraft | ✓/✗ | CC-BY-NC-SA-4.0 | Connected glass without streaks (via Continuity) — windows and greenhouses. |  |
| [Cloth Config API](https://modrinth.com/mod/cloth-config) | 26.1.154+fabric (`GFM8zh9J`) | 26.1, 26.1.1, 26.1.2 / fabric | ✓/✓ | LGPL-3.0-only | Dependency of cameraoverhaul, cristel-lib, fallingleaves, moreculling… |  |
| [Complementary Shaders - Reimagined](https://modrinth.com/shader/complementary-reimagined) | r5.9.3 (`Bqen1mJX`) | 26.1, 26.1.1, 26.1.2 / iris, optifine | ○/✗ | Custom | Optional 'Cinematic' preset shader (vanilla-faithful). Off by default. |  |
| [CreativeCore](https://modrinth.com/mod/creativecore) | 2.14.16 (`atlpYjof`) | 26.1.2 / fabric | ✓/✓ | LGPL-3.0-only | Dependency of ambientsounds | fabric-api |
| [Cristel Lib](https://modrinth.com/mod/cristel-lib) | fabric-26.1.2-3.1.13 (`JDTs3eQM`) | 26.1, 26.1.1, 26.1.2 / fabric, quilt | ✓/✓ | CC-BY-NC-ND-4.0 | Dependency of towns-and-towers | cloth-config, fabric-api |
| [Delight Lib](https://modrinth.com/mod/delight-lib) | 26.05.18-26.1-fabric (`tPYOmX52`) | 26.1, 26.1.1, 26.1.2 / fabric | ✓/✓ | MIT | Dependency of more-delight | farmers-delight-refabricated |
| [Dramatic Skys](https://modrinth.com/resourcepack/dramatic-skys) | 1.5.3.36.6 (`4MZQy9nc`) | 26.1, 26.1.1, 26.1.2 / minecraft | ✓/✗ | All-Rights-Reserved | HD skies with realistic clouds (needs Nuit). Baseline without shaders. |  |
| [Easy NPC: Config UI](https://modrinth.com/mod/easy-npc-config-ui) | 7.18.0 (`u3httvO2`) | 26.1.2 / fabric, quilt | ✓/✓ | MIT | Dependency of easy-npc | easy-npc-core |
| [Easy NPC: Core](https://modrinth.com/mod/easy-npc-core) | 7.18.0 (`uB3Xp6jk`) | 26.1.2 / fabric, quilt | ✓/✓ | MIT | Dependency of easy-npc, easy-npc-config-ui |  |
| [Even Better Enchants](https://modrinth.com/resourcepack/even-better-enchants) | 3-1.21.5 (`scd0ojNG`) | 26.1, 26.1.1, 26.1.2 / minecraft | ✓/✗ | Apache-2.0 | Distinct enchanted book textures (vanilla item models, no CIT mod needed on 26.1). |  |
| [Fabric Language Kotlin](https://modrinth.com/mod/fabric-language-kotlin) | 1.14.2+kotlin.2.4.21 (`SKW1BTL3`) | 26.1, 26.1.1, 26.1.2 / fabric | ✓/✓ | Apache-2.0 | Dependency of fzzy-config, inventory-profiles-next, libipn, zoomify |  |
| [(Bee's) Fancy Crops](https://modrinth.com/resourcepack/fancy-crops) | 1.3 (`ZJEBZjg6`) | 26.1, 26.1.1, 26.1.2 / minecraft | ✓/✗ | All-Rights-Reserved | Prettier crops; built to work with Farmer's Delight. |  |
| [Forge Config API Port](https://modrinth.com/mod/forge-config-api-port) | 26.1.5 (`jUe0ucoE`) | 26.1, 26.1.1, 26.1.2 / fabric | ✓/✓ | MPL-2.0 | Dependency of crops-love-rain, overflowing-bars, puzzles-lib, seasonhud-fabric… | fabric-api |
| [Fresh Animations](https://modrinth.com/resourcepack/fresh-animations) | 1.11.2 (`xfE3qfr5`) | 26.1, 26.1.1, 26.1.2 / minecraft | ✓/✗ | See-Terms-of-Use-in-Description | Vanilla-style mob animations. |  |
| [Fresh Animations: Details](https://modrinth.com/resourcepack/fresh-animations-details) | 2.4.0 (`XM8VyQLk`) | 26.1, 26.1.1, 26.1.2 / minecraft | ✓/✗ | All-Rights-Reserved | Official Fresh Animations extension: extra mob details. |  |
| [Fresh Animations: Objects](https://modrinth.com/resourcepack/fresh-animations-objects) | 2.1.2 (`AIGgXNdl`) | 26.1, 26.1.1, 26.1.2 / minecraft | ✓/✗ | All-Rights-Reserved | Official Fresh Animations extension: animates non-mob entities. |  |
| [Fresh Flower Pots](https://modrinth.com/resourcepack/fresh-flower-pots) | 1.5.4 (`YEM2pUsf`) | 26.1, 26.1.1, 26.1.2 / minecraft | ✓/✗ | All-Rights-Reserved | Detailed flower pots (pairs with Gardener's Dream). |  |
| [Fusion (Connected Textures)](https://modrinth.com/mod/fusion-connected-textures) | 1.3.16-fabric-mc26.1 (`oAEjsOze`) | 26.1, 26.1.1, 26.1.2 / fabric, quilt | ✓/✓ | All-Rights-Reserved | Connected-texture library required by Rechiseled's jar on BOTH sides (Modrinth marks it client-only). | fabric-api |
| [Fzzy Config](https://modrinth.com/mod/fzzy-config) | 0.7.7+26.1 (`Ugxie86a`) | 26.1, 26.1.1, 26.1.2 / fabric | ✓/✓ | TDL-M | Dependency of subtle-effects | fabric-api, fabric-language-kotlin |
| [GlitchCore](https://modrinth.com/mod/glitchcore) | 26.1.2.0.2 (`WNtSATXw`) **beta** | 26.1.2 / fabric | ✓/✓ | All-Rights-Reserved | Required by Serene Seasons' jar (not declared in Modrinth metadata; found by tools/validate.py). | fabric-api |
| [Iceberg](https://modrinth.com/mod/iceberg) | 1.4.1.2 (`UujfCDsB`) | 26.1, 26.1.1, 26.1.2 / fabric | ✓/✓ | CC-BY-NC-ND-4.0 | Dependency of legendary-tooltips | fabric-api |
| [JamLib](https://modrinth.com/mod/jamlib) | 2.3.1+26.1.x (`eeYK2UkC`) | 26.1, 26.1.1, 26.1.2 / fabric, quilt | ✓/✓ | MIT | Dependency of rightclickharvest | fabric-api |
| [libIPN](https://modrinth.com/mod/libipn) | fabric-26.1-6.9.0 (`94nbGWNs`) | 26.1, 26.1.1, 26.1.2 / fabric | ✓/✗ | AGPL-3.0-or-later | Dependency of inventory-profiles-next | fabric-api, fabric-language-kotlin |
| [Lithostitched](https://modrinth.com/mod/lithostitched) | 1.8.1-fabric-26.1 (`o908EP07`) | 26.1, 26.1.1, 26.1.2 / fabric | ✓/✓ | MIT | Dependency of tectonic, terralith | fabric-api |
| [Low On Fire](https://modrinth.com/resourcepack/low-on-fire) | 26.3 (`yQdcUfnr`) | 26.1, 26.1.1, 26.1.2 / minecraft | ✓/✗ | All-Rights-Reserved | Lower first-person fire overlay. |  |
| [MakeUp - Ultra Fast](https://modrinth.com/shader/makeup-ultra-fast-shaders) | 9.6 (`t4YRxQug`) | 26.1, 26.1.1, 26.1.2 / iris, optifine | ○/✗ | LGPL-3.0-or-later | Optional lightweight shader for lower-end GPUs. |  |
| [Mellow](https://modrinth.com/shader/mellow) | 3.5 (`DmXr7SxM`) | 26.1, 26.1.1, 26.1.2 / iris, optifine | ○/✗ | MIT | Optional painterly, cozy and light shader (MIT). |  |
| [MezzConfig](https://modrinth.com/mod/mezzconfig) | 0.6.10 (`JpwQcdyO`) **beta** | 26.1.2 / fabric | ✓/✓ | MIT | Dependency of jei | fabric-api |
| [New Glowing Ores](https://modrinth.com/resourcepack/new-glowing-ores) | 2.0-1.21-border (`BG46xXmQ`) | 26.1, 26.1.1, 26.1.2 / minecraft | ✓/✗ | CC-BY-NC-SA-4.0 | Optional: glowing ore outlines (accessibility); shipped but off. |  |
| [Photon Shaders](https://modrinth.com/shader/photon-shader) | v1.4 (`BnGklH5y`) | 26.1, 26.1.1, 26.1.2 / iris, optifine | ○/✗ |  | Optional high-end semi-realistic shader (cinematic, strong GPU). |  |
| [Text Placeholder API](https://modrinth.com/mod/placeholder-api) | 3.0.0+26.1 (`b3IPAHgB`) | 26.1, 26.1.1, 26.1.2 / fabric | ✓/✓ | LGPL-3.0-only | Dependency of modmenu |  |
| [Prickle](https://modrinth.com/mod/prickle) | 26.1.2.6 (`Sr3cVd3n`) | 26.1, 26.1.1, 26.1.2 / fabric | ✓/✓ | LGPL-2.1-only | Dependency of enchantment-descriptions | fabric-api |
| [Prism](https://modrinth.com/mod/prism-lib) | 1.1.1 (`uxupbyjR`) | 26.1, 26.1.1, 26.1.2 / fabric | ✓/✗ | CC-BY-NC-ND-4.0 | Dependency of legendary-tooltips | fabric-api |
| [Puzzles Lib](https://modrinth.com/mod/puzzles-lib) | 26.1.16 (`cPeqCZKe`) | 26.1, 26.1.1, 26.1.2 / fabric | ✓/✓ | MPL-2.0 | Dependency of overflowing-bars | fabric-api, forge-config-api-port |
| [Recolourful Containers GUI + HUD](https://modrinth.com/resourcepack/recolourful-containers-gui) | 3.1.4 (`aQ7jLjk8`) | 26.1, 26.1.1, 26.1.2 / minecraft | ✓/✗ | All-Rights-Reserved | Warm, themed container screens (wooden crafting table, stone anvil, …); vanilla+ mode without OptiGUI. |  |
| [Resourceful Lib](https://modrinth.com/mod/resourceful-lib) | 4.0.1 (`sOEhLVuw`) | 26.1, 26.1.1, 26.1.2 / fabric | ✓/✓ | MIT | Dependency of friends-and-foes | fabric-api |
| [Searchables](https://modrinth.com/mod/searchables) | 1.0.2 (`g7OQkxmL`) | 26.1.2 / fabric | ✓/✗ | MIT | Dependency of controlling | fabric-api |
| [Shogi](https://modrinth.com/mod/shogi) | 26.1.2.12+fabric-26.1.2 (`u9XumgCL`) | 26.1.2 / fabric | ✓/✓ | All-Rights-Reserved | Dependency of waystones | fabric-api |
| [Simple Grass Flowers](https://modrinth.com/resourcepack/simple-grass-flowers) | 2.0.0 (`Cd4Xa7Sr`) | 26.1, 26.1.1, 26.1.2 / minecraft | ✓/✗ | All-Rights-Reserved | Vanilla-friendly flowers, clovers and pebbles on grass. |  |
| [SuperMartijn642's Config Lib](https://modrinth.com/mod/supermartijn642s-config-lib) | 1.1.8a-fabric-mc26.1 (`aX2tCZWA`) | 26.1, 26.1.1, 26.1.2 / fabric, quilt | ✓/✓ | All-Rights-Reserved | Dependency of rechiseled | fabric-api |
| [SuperMartijn642's Core Lib](https://modrinth.com/mod/supermartijn642s-core-lib) | 1.1.26b-fabric-mc26.1 (`t3hsg8GL`) | 26.1, 26.1.1, 26.1.2 / fabric, quilt | ✓/✓ | All-Rights-Reserved | Dependency of rechiseled | fabric-api |
| [Theone's Eating Animation Pack](https://modrinth.com/resourcepack/theones-eating-animation-pack) | 1.0 (`laDCvp3K`) | 26.1, 26.1.1, 26.1.2 / minecraft | ✓/✗ | All-Rights-Reserved | Eating/drinking animations for food (no mod needed). |  |
| [Visual: Armor Trims](https://modrinth.com/resourcepack/visual-armor-trims) | 4.2 (`Qt949MJE`) | 26.1, 26.1.1, 26.1.2 / minecraft | ✓/✗ | CC-BY-SA-4.0 | Armour trims visible on inventory icons. |  |
| [YetAnotherConfigLib (YACL)](https://modrinth.com/mod/yacl) | 3.9.7+26.1-fabric (`IODousKT`) | 26.1, 26.1.1, 26.1.2 / fabric | ✓/✓ | LGPL-3.0-or-later | Dependency of frostiful, scorchful, spatial-gui, thermoo-patches… | fabric-api |
| [YUNG's API](https://modrinth.com/mod/yungs-api) | 26.1.2-Fabric-6.1.3 (`lmLenYfY`) | 26.1.1, 26.1.2 / fabric | ✓/✓ | LGPL-3.0-only | Dependency of yungs-better-dungeons, yungs-better-mineshafts, yungs-better-strongholds | fabric-api |

## Hashes

SHA-1 / SHA-512 for every downloadable file are in `pack/mods.lock.json` (`hashes`) and in the mrpack index.
