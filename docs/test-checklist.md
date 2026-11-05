# Local Test Checklist (Jules' PC)

The cloud tests cover loading, data, quests and server/singleplayer start-up (see `STATUS.md`). This list covers what needs **your PC**: real GPU, sound, input, feel and performance. Commands need cheats enabled (single player: *Open to LAN → Allow Cheats*, or create the test world with cheats on).

Use a **separate test world**. Tick each box; if something fails, note what you saw and copy `logs/latest.log` (plus any `crash-reports/*.txt`).

## 0. Start-up
- [ ] Import the mrpack in Prism and select Java 25. The game reaches the title screen.
- [ ] Mod count shows in Mod Menu. *Controls* → Controlling's conflict filter: write down any conflicts.
- [ ] Create a world: terrain looks like Terralith + Tectonic, with no missing textures (magenta/black).
- [ ] The quest book opens: 9 chapters, readable text, pinned quests show on the HUD tracker.

## 1. Weather & shelter
- [ ] `/homestead weather forecast` and a placed **Barometer** report the next front.
- [ ] `/homestead weather start storm 120 2`: rain and thunder start, wind is audible, and `/homestead weather status` shows intensity and wind.
- [ ] Go to snow (`/locate biome minecraft:snowy_plains`, then teleport) and run `/homestead weather start blizzard 180 3`:
  - [ ] Outside: fog closes in to about 12–50 blocks, snow streams sideways, and walking is noticeably slower.
  - [ ] Your temperature drops faster than Frostiful alone would (compare with `/homestead weather stop`).
  - [ ] Under a roof, with a wall on the windward side: fog lifts, speed is normal and the extra cold stops. `/homestead weather status` says "sheltered".
  - [ ] A glass roof also counts as shelter. A doorway facing into the wind does not.
  - [ ] Fur armour visibly slows the cooling.
  - [ ] Open ground gets at most 2 snow layers; farmland, paths and crops never get covered.
- [ ] After `/homestead weather stop` the front ramps down over about 30 s and the skies clear.
- [ ] You get the warnings a day ahead and about 2 minutes before a scheduled front (use `/homestead weather schedule 150` to test).
- [ ] With shaders on, note whether the blizzard fog still shows (shader packs may override fog).

## 2. Cooking
- [ ] A Farmer's Delight cooking pot on a campfire, and on an FD stove, makes a meal.
- [ ] An **Electric Range** with power and a pot on top shows a lit cooktop and the pot cooks. With power off, the cooktop stays lit about 5 s, then goes out; the pot keeps its ingredients.
- [ ] Oven: put in 2 raw beef with power → cooked beef. Break the cable mid-cook: progress holds, and continues when you reconnect.

## 3. Generation & batteries
- [ ] Solar Panel I at noon in clear weather vs. during a storm: output clearly drops (EP GUI or Jade).
- [ ] **Water Wheel** beside flowing water: sneak-use shows ≥1 flowing face and E/t > 0. Beside still water only: 0.
- [ ] **Wind Turbine** on a hill with open sky: sneak-use shows wind m/s and output. Box it in with blocks and exposure drops. During a severity-3 front it can shut down above 25 m/s.
- [ ] A Battery Box charges by day and runs a lamp through the night.
- [ ] Cables hidden inside a wall still power an appliance from the back or bottom.

## 4. Outages & refrigeration
- [ ] A powered **Refrigerator** shows "cold (powered)" on sneak-use. Food inside shows no spoilage tooltip progress over a few in-game days.
- [ ] Cut its power: it shows "no power — staying cold for N seconds" for about 5 minutes, then "WARM". After that, food spoils like it does in a chest.
- [ ] Barrel food spoils about half as fast as chest food; golden carrots, honey and dried kelp never spoil.
- [ ] The **Space Heater** turns on with a player nearby and power. The room is lit and your temperature recovers. With no power, it's off.

## 5. Residents & town
- [ ] Villagers are MCA residents: you can chat, give gifts, and see moods.
- [ ] The MCA Blueprint shows your homestead and the buildings it recognises (house with bed).
- [ ] No Grim Reaper or cultist spawns, and no zombie infection.
- [ ] A test chore (farming) works. Hunting near livestock: note whether pen animals are killed (expected — the docs warn about it).
- [ ] Market buy and Shipping Bin sell both work. The "Request: Bread" quest takes the bread and pays emeralds, then shows a cooldown.

## 6. Quests
- [ ] A few auto-detected quests: craft a crafting table; place a cooking pot; "Here It Comes" during a front; MCA "Merchant" when you reach the rank.
- [ ] Manual quests show "[Manual]" and a tick box.
- [ ] Rewards claim correctly, and none of them hand out machines.

## 7. Screens & visuals
- [ ] Title screen: the village panorama turns slowly, with the "Homestead Edition" banner and a homestead splash.
- [ ] **Spatial GUI**: press E. The inventory appears as a panel beside your character in third person, and clicking and dragging items works normally.
  - [ ] A chest, a crafting table and the Electric Oven open the same way.
  - [ ] The quest book and the pause menu stay flat.
  - [ ] Try *Mod Menu → Spatial GUI → View Mode → First Person* and keep whichever you prefer.
- [ ] JEI: the item list next to the panel is still usable (search, click for recipes).
- [ ] The quest book (walnut) and Jade tooltips (walnut) are readable at your GUI scale.
- [ ] Shaders: switch between Complementary Reimagined, Photon, Mellow and MakeUp Ultra Fast (*Video Settings → Shader Packs*). For each one:
  - [ ] no black screen or crash;
  - [ ] Spatial GUI panels are still readable;
  - [ ] note the FPS.
- [ ] Without shaders, Dramatic Skys shows clouds and a sunset.

## 8. Performance
- [ ] Baseline preset (`docs/presets.md`): record FPS at the homestead during clear weather and in a severity-3 blizzard.
- [ ] Memory: F3 shows heap use under about 80% of the allocation after 10 minutes.
- [ ] Optional: Complementary Reimagined (cinematic) and Distant Horizons: record FPS and stability.
- [ ] Optional: Physics Mod Pro (your file). Start-up, a blizzard and breaking blocks all still work.
