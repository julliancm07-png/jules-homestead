# Changelog

## 1.1.0 — 2026-11-05 (polish release for Jules' local testing)
Requests: more mods and resource packs that improve the experience, optimization, nicer themed menus, smooth
animations and immersive nights. Research: `research/2026-11-05-polish-1.1-candidates.md`.

### Home, farm and world (+45 mods, +10 resource packs)
- **Home and decor:** Adorn (sofas, chairs, tables, kitchen counters and cupboards), Beautify (blinds, trellises,
  hanging pots, lamps), Macaw's Holidays (winter and Halloween decorations), Reconnectible Chains, diagonal fences,
  walls and windows, Bl4cks Sit (sit on stairs and slabs), Visual Workbench, Comforts (sleeping bags and hammocks).
- **Living world:** This Rocks (pebbles, sticks, pinecones, shells), Fish of Thieves, Boids, Friends & Foes beekeeper
  hut and flower cows, Simple Snowy Fix (snow on leaves), Trample No More.
- **Exploration:** the rest of the YUNG's set (bridges, extras, temples, witch huts, monuments, fortresses) and
  Explorer's Compass.
- **Ambience:** Particular (fireflies, waterfall mist), Soft Imprints (footprints in snow), Cool Rain, Drip Sounds,
  Sounds, Traveler's Titles, Advancement Plaques.
- **Info and QoL:** Jade Addons, Just Enough Breeding, Universal Graves, YUNG's Menu Tweaks, FastQuit, spark.
- **Resource packs:** Fast Better Grass, Connected Paths, Fancy Beds, Fluffy Carpets, Ray's 3D Ladders, Just
  Expressions, Fresh Seeds, Better Flame Particles, Torches Reimagined, Renewed Bookshelf.

### Optimization
- C2ME, Ksyxis, Let Me Despawn and Particle Core, plus BadOptimizations, ScalableLux, Clumps, Structure
  Layout Optimizer, Neruina, Debugify and RSLS. Dedicated-server boot in the cloud test: spawn preparation
  **20.9 s → 10.0 s**, whole boot 70 s → 58 s.
- `docs/INSTALL.md`: new "If the game runs slowly" section (settings to try, spark profiler, optional ZGC flag).
- ModernFix-mVUS and LightspeedRE were tried and removed: both disturbed start-up and crashed the integrated server
  ("Registry is not frozen yet!").

### Smooth animations
- Fancy World Animations (doors, gates, trapdoors, levers, lecterns and chests move; lanterns and chains sway),
  Smooth Swapping (items glide between slots), Smooth Scroll, Chunks Fade In, A Good Place (placed blocks settle),
  ItemPhysic Lite (dropped items lie flat), Countered's Smooth Steps, NEA EMF Compat.

### Immersive nights
- Spawn Animations (hostile mobs dig out of the ground), Fresh Animations Emissive (glowing mob eyes).
- Shader night presets, seeded only if you have no settings for that shader yet: Complementary aurora (full moon or
  snowy biomes), night nebulae and moon-phase lighting; Photon aurora and Milky Way galaxy.

### Menus
- Homestead GUI: oak-plank buttons, a stone hotbar, slate-and-gold toasts and advancement frames. Recolourful
  Containers still draws every container screen.

### Quests (110, +6)
- New: Bedroll, Pond Life, Flower Cows, Put Your Feet Up, Festive Season, Lost Places. Fitted Kitchen is now detected
  automatically (Adorn advancements). 92 quests complete automatically, 18 are tick-boxes.

### Fixes
- **Night sky** (also wrong in 1.0.0): the sky and horizon fog stayed bright daytime blue at night. Cause: Immersive
  Storms (inside Scorchful) re-applied each biome's daytime colour after Minecraft's day/night timeline. homestead-core
  1.1.0 skips that duplicate step (`ImmersiveStormsNightSkyFixMixin`); verified in the client at midnight.
- **Start-up loading screen:** RRLS now only hides reloads you trigger in game (`config/rrls.toml`), so the first load
  keeps the normal loading screen and a world can't open before its textures are ready (the cause of a white progress
  bar in screenshots).
- Homestead Resources restores the "Inventory" label on container screens (Homestead GUI blanks it).

### homestead-core 1.1.0
- `ImmersiveStormsNightSkyFixMixin` (see Fixes); applied only when Immersive Storms is installed.
- Dev harness: `probe` (log the client's block state) and `pack on|off` steps; `client_smoke_test.py --script`.
- GameTests: 10/10.

### Packaging and tooling
- **YOSBR:** default options ship as `config/yosbr/options.txt` and are copied only when you have no `options.txt`, so
  updating never overwrites your settings.
- Built-in packs that a mod enables by default are now kept in the shipped resource-pack list (A Good Place).
- `validate.py` checks every resource pack's declared format against 26.1.2 (84). It caught Shelviety (replaced by
  Renewed Bookshelf) and New Glowing Ores (replaced by Emissive Glowing Ores, still shipped off).
- Showcase screenshots: a lantern-lit evening room (`showcase-decor.png`) and the village at night
  (`showcase-village-night.png`).

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
