# Changelog

## 1.0.0 — 2026-11-05 (release candidate for Jules' local testing)
### Screens
- **Spatial GUI** 1.10.1 (client-only, MIT; Jules' pick): inventory and container screens open as a 3D panel beside the
  character (third-person, mod defaults). It runs alongside Recolourful Containers, which supplies the panel textures.
  The quest book, menus and dialogues stay flat. Verified in the showcase client run:
  - the inventory
  - a crafting table
  - the Electric Oven
- Electric Oven screen restyled in the pack's walnut and cream palette. The player inventory below stays vanilla.
- Confirmed in the client: the IPN auto-refill slot icons are gone, and Camera Overhaul applies the tuned config.

### Quests
- "Power for the Town" now also asks you to feed 500,000 E from the town grid into an FTB Task Screen, using FTB Quests'
  Tech Reborn energy task (up to 2,000 E per tick, consumed). Reward: 32 copper cable.

### homestead-core 1.0.0
- 2 new GameTests (10 in total):
  - storm slowdown and shelter
  - blizzard slowdown
  - snow build-up rules and live accumulation
  - the witness_front, caught_in_blizzard and safe_and_warm advancements (the last needs 60 s of shelter)

  Each front test runs in its own test environment, so global fronts never overlap.
- Dev harness: a `use` step (right-click) and proper container close, for container screenshots.

### Tooling
- `resolve.py` sorts dependency lists, so re-resolving no longer reorders the lockfile.
- `validate.local_jar` picks the newest local build, so a stale jar from an old version is never packaged.
- `client_smoke_test.py --update-panorama`: the showcase run only replaces the title panorama when asked.

### Counts
- 135 third-party mods (108 chosen + 27 dependencies)
- 2 local modules
- 17 resource packs (16 on by default)
- 4 optional shaders
- 260 mods loaded in the client, including nested libraries
- 96 + 2 mods on a dedicated server
- 104 quests (85 auto-detected)

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

### Screens & immersion (same day, user request)
- New original resource pack "Homestead Resources": in-game panorama of a pack village for the title screen,
  "Homestead Edition" banner, homestead splash texts, walnut FTB Quests theme (frost/ember chapter variants),
  matching walnut Jade theme; calm dark loading screen.
- Resource packs: Fresh Animations Details/Objects, Fancy Crops, Simple Grass Flowers, Better Lanterns, Fresh Flower
  Pots, Clean Connected Glass, Animated Items, TheOne's Eating Animation, Even Better Enchants, Visual Armor Trims,
  Low On Fire, Recolourful Containers, Dramatic Skys; New Glowing Ores shipped off.
- Data packs: Structory, Structory Towers, Explorify, Gardener's Dream, Remove Terralith Intro Message; Snow Under Trees.
- Mods: Subtle Effects (replaces Visuality), Ambient Environment, Wakes, Camera Overhaul (tuned down), Legendary
  Tooltips, RRLS, Overflowing Bars, Nuit. Optional shaders: Photon, Mellow.
- homestead-core: dev-only showcase harness (-Dhomesteadcore.dev.script) for automated screenshots/panorama.
- IPN auto-refill slot icons disabled; IPN first-run popup skipped.

### Removed during testing
- Wilder Wild (intermittent registry-load crash with Terralith on 26.1 parallel loading).
