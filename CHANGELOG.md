# Changelog

## 0.1.0 — 2026-11-05 (first build)
### Platform
- Minecraft 26.1.2, Fabric Loader 0.20.1, Fabric API 0.155.3+26.1.2, Java 25 (decision record: research/version-decision.md).
- 119 pinned mods from Modrinth + FTB's official maven; Create (Create Fly) and Distant Horizons optional.

### homestead-core 0.1.0 (custom mod, MIT)
- Severe fronts: storm and blizzard events with saved state, season-weighted scheduling, intensity envelope, wind model, forecasts and warnings, vanilla rain/thunder coordination, commands.
- Shelter (roof per the heightmap, glass counts, plus an upwind wall), blizzard exposure cold through a Thermoo temperature source, movement slowdown, client fog and wind-driven snow, capped snow build-up.
- Wind turbine (cut-in/rated/cut-out, height, obstruction) and water wheel (flowing water) on Team Reborn Energy.
- Refrigerator (no spoilage while powered, 5-minute cold retention, Spoiled adapter), electric range (Farmer's Delight heat source + 4-slot oven that pauses on outage), space heater (light + Thermoo warmth), barometer (forecast + redstone warning).
- Weather advancements for quest detection; `/homestead debug dump` registry dump.
- Upstream workarounds: Serene Seasons singleplayer recursion crash; Spoiled dedicated-server recipe cache.
- 8 GameTests.

### Content
- Quest book: 9 chapters, 104 quests (85 auto-detected, 19 manual milestones).
- Configs: Serene Seasons, Frostiful, Scorchful, Spoiled, MCA, homestead-core. Pack data module: seasonal crop tags, Energized Power weather/time controllers disabled.

### Removed during testing
- Wilder Wild (intermittent registry-load crash with Terralith on 26.1 parallel loading).
