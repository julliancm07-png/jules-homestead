# Power Plan

## Shared API & units
* **API:** Team Reborn Energy `team.reborn.energy.api.EnergyStorage` (v5.0.0), queried through Fabric's block API lookup `EnergyStorage.SIDED`. Bundled by Energized Power and FTB Quests; homestead-core compiles against the same artifact (`teamreborn:energy:5.0.0`).
* **Unit:** `E` (TR energy units). Energized Power displays the same quantity; all numbers below are E or E/t (per game tick, 20 ticks = 1 s).
* **Not electricity:** Create rotation (optional Create Fly module). No verified rotation→TR Energy converter exists for 26.1.2 Fabric, so Create stays mechanical-only.

## Generation

| Source | Mod | Rule (verified in source/jar) | Output |
|---|---|---|---|
| Solar Panel I–IV | Energized Power | `4×(skyLight − skyDarken)/60 × peak`. `skyDarken` rises with night, rain and thunder → storms really cut output. Needs open sky. | peak 2 / 16 / 128 / 2048 E/t |
| Coal Engine | Energized Power | Burns furnace fuel. | thermal backup |
| Heat Generator / Thermal Generator | Energized Power | Lava/heat based. | mid-game |
| Lightning Generator | Energized Power | Lightning strikes on the block — storms are an *opportunity*. | burst |
| **Wind Turbine** | homestead-core | Wind speed v from the weather model. Cut-in 3 m/s, rated 12 m/s, **cut-out 25 m/s** (feathers in extreme gusts). Power ∝ (v/12)³ below rated. Height factor (y/(sea+16))^0.14, clamped 0.6–1.25. Obstruction: share of air blocks in a 5×3×5 box around the rotor. Needs open sky. | rated **12 E/t** |
| **Water Wheel** | homestead-core | Counts horizontally adjacent **flowing** water (non-source with non-zero flow). Still water and ice give nothing — winter freezing reduces hydro. | **3 E/t per flowing face**, max 12 |

Simplified rules, plainly: the wind turbine uses a cube law and constant shear exponent; the water wheel counts flow, not flow rate.

## Storage & distribution
* **Battery Box** (EP): 65,536 E, 2,048 E/t. **Advanced Battery Box**: 8,388,608 E.
* **Cables** (EP): Tin 128 E/t → Copper 1,024 → Gold 16,384 … (EP config defaults).
* **Concealed wiring:** homestead-core appliances accept energy from **every side, including back and bottom**, so cables can run inside a one-block wall cavity or under floorboards and be covered with any block. No visible cable needs to enter a furnished room.

## Consumers (household appliances)

| Appliance | Draw | Powered | Unpowered / outage |
|---|---|---|---|
| **Refrigerator** (27 slots) | 1 E/t continuous | Contents do **not** spoil (Spoiled). | **Cold retention**: stays cold 6,000 ticks (5 min) after power loss. After that, contents spoil at the normal Spoiled rate. Items are never deleted except by Spoiled's normal spoil-to-rotten conversion. Without Spoiled installed it is plain storage. |
| **Electric Range** (cooktop + 4-slot oven) | 6 E/t while cooking or heating cookware | Cooktop is `LIT` → Farmer's Delight heat source (pot/skillet). Oven cooks `minecraft:smoking` recipes at 2× vanilla speed. | Oven progress **pauses** (never regresses). Cooktop keeps residual heat for 100 ticks, then unlit; Farmer's Delight then decays pot progress (FD's own behaviour) but never loses ingredients. |
| **Space Heater** | 8 E/t while running | Light 15 + extra Thermoo warmth within 7 blocks. Runs when an entity is near and it is cold, or always with a redstone signal. | Off: no light, no warmth. |
| **Powered Lamp** (EP) | 1 E/t | Light. | Off. |
| Powered Furnace (EP) | 2 E/t | 2× speed smelting. | EP behaviour. |

## Sizing example (first winter)
Fridge 1 + range 6 (≈25% duty → 1.5 avg) + heater 8 (night) + 4 lamps 4 → night load ≈ 14.5 E/t × 12,000 ticks ≈ **174,000 E**, i.e. ~3 Battery Boxes, recharged by day from ~4× Solar II (16 E/t peak) or one turbine on a windy ridge. Storms cut solar but raise wind — mixed generation is the intended answer.

## Progression
Camp (no power) → Coal Engine or Solar I + Battery Box (first lamp, fridge) → Wind Turbine / Water Wheel + Solar II (range, heater) → Solar III/Advanced Battery Box (whole town).
