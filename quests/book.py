# Jules' Homestead quest book — authored content (compiled by tools/build_quests.py).
# Task kinds: item (auto, optional consume), advancement (auto), observation (auto: look at a block/tag),
# biome / structure (auto: visit), kill (auto), tech_reborn_energy (auto: cable into a Task Screen), checkmark (manual). Manual building milestones are labelled.
# Rewards are deliberately modest and never hand out a machine from a later tier.

BOOK_TITLE = "Jules' Homestead"


def Item(id, count=1, consume=False, title=None):
    return {"kind": "item", "id": id, "count": count, "consume": consume, "title": title}


def Adv(id, title=None):
    return {"kind": "advancement", "id": id, "title": title}


def Look(id, observe="block", title=None):
    return {"kind": "observation", "observe": observe, "id": id, "title": title}


def Biome(id, title=None):
    return {"kind": "biome", "id": id, "title": title}


def Structure(id, title=None):
    return {"kind": "structure", "id": id, "title": title}


def Energy(value, max_input=2000, title=None):
    """Tech Reborn Energy (E) fed by cable into an FTB Quests Task Screen bound to this task (consumed)."""
    return {"kind": "tech_reborn_energy", "value": value, "max_input": max_input, "title": title}


def Check(title=None):
    return {"kind": "checkmark", "title": title}


def R(id, count=1):
    return {"kind": "item", "id": id, "count": count}


def XP(n):
    return {"kind": "xp", "xp": n}


def Q(key, title, x, y, tasks, rewards=(), deps=(), desc=(), subtitle=None, icon=None, preset=None, shape=None,
      size=None, optional=False, manual=False, any_dep=False, repeat=0):
    return {"key": key, "title": title, "x": x, "y": y, "tasks": list(tasks), "rewards": list(rewards), "deps": list(deps),
            "desc": list(desc), "subtitle": subtitle, "icon": icon, "preset": preset, "shape": shape, "size": size,
            "optional": optional, "manual": manual, "any_dep": any_dep, "repeat": repeat}


DAY = 1200  # repeat cooldown in real seconds (~one in-game day)

CHAPTERS = [
    # ------------------------------------------------------------------ 1
    {"key": "01_founding_camp", "title": "Founding Camp", "icon": "minecraft:campfire",
     "subtitle": "Make it through the first nights, then start thinking about the seasons.",
     "quests": [
         Q("welcome", "Welcome, Jules", 0, 0, [Check("Read this, then tick")], [R("minecraft:bread", 6)], preset="info",
           icon="sereneseasons:calendar", subtitle="How this homestead works",
           desc=["This world rewards preparation. Four seasons turn the year, and every few days a severe front rolls in: a storm, or, where it is cold enough to snow, a blizzard.",
                 "",
                 "You start with nothing. Build a camp, then a home and fields. Later you'll add a workshop, renewable power and a modern kitchen. In time, residents will settle and your camp will grow into a town.",
                 "",
                 "Pin a quest (the pin icon) to keep it on your HUD tracker. Quests marked [Manual] are building milestones you tick off yourself."]),
         Q("timber", "Timber", 2, -1, [Item("minecraft:crafting_table"), Item("minecraft:stick", 8)], [R("minecraft:apple", 3)], deps=["welcome"],
           desc=["Any wood will do. Chop the base of a trunk with an axe and the whole tree comes down (Falling Tree).", "Hold sneak while chopping to take a single log."]),
         Q("storage", "A Place for Things", 4, -1, [Item("minecraft:chest")], [R("minecraft:torch", 8)], deps=["timber"],
           desc=["Chests keep things safe, but food in them slowly spoils.", "A barrel is a cool cellar: food in it spoils at half the rate."]),
         Q("fire", "First Fire", 2, 1, [Item("minecraft:campfire")], [R("minecraft:cooked_cod", 3)], deps=["welcome"],
           desc=["A campfire cooks food and keeps you warm. Light warms you too: stand where it's well lit when you feel the cold.", "Add a log to a lit campfire to get the Warmth effect for a while."]),
         Q("warmth", "Feeling the Cold", 4, 1, [Adv("frostiful:adventure/warm_by_light", "Warm up by a light source")], [XP(30)], deps=["fire"],
           desc=["Your body temperature shows next to the hotbar. Cold biomes, winter, wet clothes and night all pull it down.",
                 "Shivering burns hunger. Freezing hurts. Light, fire, fur clothing and a roof all help."]),
         Q("bed", "A Bed for the Night", 6, 0, [Adv("minecraft:adventure/sleep_in_bed", "Sleep in a bed")], [R("minecraft:white_wool", 4)], deps=["storage", "warmth"]),
         Q("stone", "Stone Age", 6, -2, [Adv("minecraft:story/mine_stone", "Mine stone"), Item("minecraft:furnace")], [R("minecraft:coal", 8)], deps=["storage"]),
         Q("iron", "Iron and Embers", 8, -2, [Adv("minecraft:story/smelt_iron", "Smelt iron"), Item("minecraft:iron_ingot", 12)], [XP(50)], deps=["stone"],
           desc=["Iron unlocks better tools, buckets and the Farmer's Delight cooking pot."]),
         Q("knife", "A Proper Knife", 8, 0, [Adv("farmersdelight:main/craft_knife", "Craft a knife")], [R("farmersdelight:rope", 8)], deps=["stone"],
           desc=["Knives harvest straw from tall grass and wheat, and cut ingredients on a cutting board."]),
         Q("seasons", "Reading the Seasons", 6, 2, [Item("sereneseasons:calendar")], [R("minecraft:pumpkin_seeds", 4)], deps=["bed"],
           desc=["The season shows on your HUD. Each season has three sub-seasons of six days.",
                 "Crops only grow in their seasons; seed tooltips show which. Winter snows over temperate land, and that's when blizzards strike."]),
         Q("water", "Water on the Go", 8, 2, [Item("scorchful:water_skin")], [XP(30)], deps=["seasons"], optional=True,
           desc=["In hot places you sweat and need to drink. A water skin carries several drinks. Rain or a swim also cools you down."]),
         Q("camp", "The Camp Is Made", 10, 0, [Check("Tick when your camp has fire, bed, storage and a roof")], [R("minecraft:emerald", 3), XP(80)],
           deps=["iron", "knife", "seasons"], preset="goal", icon="minecraft:campfire",
           desc=["You have fire, food, iron and a bed. Next, a home with fields around it."]),
     ]},
    # ------------------------------------------------------------------ 2
    {"key": "02_fields_and_kitchen", "title": "Fields & Kitchen", "icon": "farmersdelight:cooking_pot",
     "subtitle": "Crops, livestock and real cooking.",
     "quests": [
         Q("plant", "Breaking Ground", 0, 0, [Adv("minecraft:husbandry/plant_seed", "Plant a seed")], [R("minecraft:bone_meal", 8)], deps=["01_founding_camp/camp"]),
         Q("wild", "Wild Harvest", 2, -1, [Adv("farmersdelight:main/get_fd_seed", "Find a wild crop")], [XP(30)], deps=["plant"],
           desc=["Wild cabbages, tomatoes, onions, rice and more grow around the world. Break them for seeds.",
                 "Ube's Delight and Rustic Delight add garlic, ginger, ube, peppers, coffee and cotton."]),
         Q("cabbage", "Cool-Season Greens", 4, -2, [Item("farmersdelight:cabbage", 8)], [R("farmersdelight:straw", 8)], deps=["wild"],
           desc=["Cabbages are hardy: they grow in spring, autumn and even winter."]),
         Q("tomato", "Summer Tomatoes", 4, -1, [Item("farmersdelight:tomato", 8)], [R("farmersdelight:rope", 8)], deps=["wild"],
           desc=["Tomatoes need summer. Let them climb a rope for a bigger harvest."]),
         Q("onion", "Onions and Garlic", 4, 0, [Item("farmersdelight:onion", 8)], [XP(30)], deps=["wild"],
           desc=["Onions grow in spring and autumn. Garlic from Ube's Delight overwinters."]),
         Q("rice", "Paddies", 4, 1, [Adv("farmersdelight:main/plant_rice", "Plant rice in water")], [XP(30)], deps=["wild"],
           desc=["Rice grows in shallow water in spring and summer."]),
         Q("soil", "Rich Soil", 6, -2, [Adv("farmersdelight:main/get_rich_soil", "Make rich soil")], [R("minecraft:bone_meal", 16)], deps=["cabbage"],
           desc=["Organic compost slowly turns into rich soil. It speeds up crops and grows mushroom colonies."]),
         Q("board", "The Cutting Board", 6, -1, [Adv("farmersdelight:main/use_cutting_board", "Use a cutting board")], [XP(30)], deps=["tomato"]),
         Q("skillet", "Skillet Supper", 6, 0, [Adv("farmersdelight:main/use_skillet", "Cook in a skillet")], [XP(30)], deps=["onion"],
           desc=["A skillet works on any heat source: campfire, Farmer's Delight stove, magma, or later an electric range."]),
         Q("pot", "The Cooking Pot", 6, 1, [Adv("farmersdelight:main/place_cooking_pot", "Place a cooking pot")], [R("minecraft:bowl", 8)], deps=["rice"],
           desc=["Meals come from the cooking pot. It needs heat underneath: a campfire or a lit stove for now."]),
         Q("stew", "Something Hearty", 8, 0, [Item("farmersdelight:vegetable_soup"), Item("farmersdelight:beef_stew")], [XP(60)], deps=["skillet", "pot", "board"],
           desc=["Proper meals give Farmer's Delight's Nourishment and Comfort effects, which carry you through long days."]),
         Q("herd", "Feeding the Herd", 2, 2, [Adv("minecraft:husbandry/breed_an_animal", "Breed two animals"), Item("animal_feeding_trough:feeding_trough")], [R("minecraft:wheat", 16)], deps=["plant"],
           desc=["Fill a feeding trough and animals will feed themselves when they're ready to breed."]),
         Q("honey", "Honey and Hives", 4, 3, [Adv("minecraft:husbandry/safely_harvest_honey", "Harvest honey safely")], [R("minecraft:glass_bottle", 4)], deps=["herd"], optional=True,
           desc=["Honey never spoils. Friends & Foes adds beekeeper villagers."]),
         Q("fish", "Fresh Catch", 6, 3, [Adv("minecraft:husbandry/fishy_business", "Catch a fish")], [R("farmersdelight:rope", 4)], deps=["herd"], optional=True),
         Q("nourish", "A Balanced Table", 10, 0, [Adv("farmersdelight:main/eat_nourishing_food", "Eat a nourishing meal"), Item("farmersdelight:shepherds_pie")], [R("minecraft:emerald", 4), XP(100)],
           deps=["stew"], preset="goal", icon="farmersdelight:shepherds_pie",
           desc=["Your fields feed you now. Soon they will feed a town."]),
     ]},
    # ------------------------------------------------------------------ 3
    {"key": "03_weather_wise", "tags": ["weather"], "title": "Weather Wise", "icon": "homesteadcore:barometer",
     "subtitle": "Storms and blizzards: forecasts, clothing, shelter and warmth.",
     "quests": [
         Q("fronts", "About Severe Fronts", 0, 0, [Check("Got it")], [XP(20)], deps=["01_founding_camp/camp"], preset="info", icon="minecraft:lightning_rod",
           desc=["Every few in-game days a severe front arrives. It brings rain and thunder, plus strong, gusting wind.",
                 "Where it snows (cold biomes, or anywhere temperate in winter) the front becomes a blizzard. Out in the open you lose heat fast, you walk slower and you can barely see.",
                 "",
                 "Shelter: a roof over your head (glass counts) AND a wall on the windward side within about 8 blocks. A doorway facing into the wind is not shelter.",
                 "Fronts never break your blocks. They can pile up to two layers of snow on open ground."]),
         Q("barometer", "Read the Barometer", 2, 0, [Item("homesteadcore:barometer")], [R("minecraft:copper_ingot", 4)], deps=["fronts"],
           desc=["Use a barometer to read the forecast: when the next front arrives, what kind it is and how strong (the strength is an estimate).",
                 "Placed barometers give a redstone signal while a front is active or less than two minutes away. Use it to close shutters or start heaters automatically.",
                 "Warnings are also announced a day ahead and again just before arrival."]),
         Q("fur", "Dress for Winter", 4, -1, [Adv("frostiful:adventure/craft_any_fur_armor", "Craft any fur armour")], [R("minecraft:leather", 4)], deps=["barometer"],
           desc=["Wolves, polar bears and ocelots drop fur tufts. Fur armour gives frost resistance, which also reduces blizzard cold."]),
         Q("feed_fire", "Keep the Fire Fed", 4, 1, [Adv("frostiful:adventure/add_log_to_campfire", "Add a log to a campfire")], [R("minecraft:charcoal", 8)], deps=["barometer"]),
         Q("witness", "Here It Comes", 6, 0, [Adv("homesteadcore:weather/witness_front", "Experience a front")], [XP(50)], deps=["fur", "feed_fire"],
           desc=["Wait for a forecast front. Watch the wind, the rain or snow, and how your temperature behaves inside and out."]),
         Q("caught", "Caught Out", 8, -1, [Adv("homesteadcore:weather/caught_in_blizzard", "Be exposed in a blizzard")], [R("minecraft:cooked_beef", 4)], deps=["witness"], optional=True,
           desc=["It happens to everyone. Get under a roof, out of the wind, and next to light or fire."]),
         Q("shelter", "Safe and Warm", 8, 1, [Adv("homesteadcore:weather/safe_and_warm", "Stay sheltered through a blizzard")], [R("minecraft:emerald", 3), XP(80)], deps=["witness"],
           desc=["Spend a full minute sheltered while a blizzard rages at your location."]),
         Q("cellar", "Root Cellar", 2, 2, [Item("minecraft:barrel", 4)], [R("farmersdelight:potato_crate")], deps=["barometer"], manual=False,
           desc=["Barrels keep food at half the spoilage rate. Golden foods, honey and dried kelp never spoil.",
                 "Later, a powered refrigerator stops spoilage completely."]),
         Q("greenhouse", "A Greenhouse", 4, 3, [Check("Tick when crops grow under glass in the wrong season")], [R("minecraft:glass", 16)], deps=["cellar"], manual=True,
           desc=["Crops under a glass roof keep growing out of season. Glass also counts as a roof for blizzard shelter."]),
         Q("summer", "Summer Heat", 6, 3, [Item("scorchful:sun_hat")], [R("scorchful:water_skin")], deps=["cellar"], optional=True,
           desc=["Deserts and summer days can overheat you. A sun hat and plenty of water help. Desert storms bring sandstorms."]),
         Q("skates", "Ice Skates", 10, -1, [Adv("frostiful:adventure/obtain_ice_skates", "Obtain ice skates")], [XP(40)], deps=["caught"], optional=True),
         Q("prepared", "Prepared for Winter", 10, 1, [Check("Tick when you have fur clothing, a stocked cellar, firewood and a sheltered home")], [R("minecraft:emerald", 5), XP(120)],
           deps=["shelter", "greenhouse"], preset="goal", icon="frostiful:fur_chestplate"),
     ]},
    # ------------------------------------------------------------------ 4
    {"key": "04_homes_and_services", "title": "Homes & Services", "icon": "minecraft:oak_door",
     "subtitle": "From camp to homestead: a real house, a workshop and the first services.",
     "quests": [
         Q("house", "A Proper House", 0, 0, [Check()], [R("minecraft:glass_pane", 16), R("minecraft:lantern", 2)], deps=["01_founding_camp/camp"], manual=True, icon="minecraft:oak_door",
           desc=["Walls, a roof, a door you can close, windows, light, a bed and storage.",
                 "Macaw's Doors, Windows, Roofs, Furniture and Lights give you plenty to build with. Rechiseled adds decorative block patterns."]),
         Q("furnish", "Making It Home", 2, -1, [Check()], [R("minecraft:white_wool", 8)], deps=["house"], manual=True, icon="mcwfurnitures:oak_chair",
           desc=["Tables, chairs, a wardrobe, a rug. Comfort is the point of a homestead.",
                 "Kitchen cabinets and drawers from Storage Delight and Macaw's Furniture actually store things."]),
         Q("paths", "Paths and Lamps", 2, 1, [Check()], [R("minecraft:torch", 16)], deps=["house"], manual=True, icon="minecraft:lantern",
           desc=["Link your buildings with paths (Macaw's Paths & Pavings) and light them so mobs don't spawn between your doors."]),
         Q("tools", "Builder's Tools", 4, -2, [Item("effortlessbuilding:randomizer_tool")], [XP(40)], deps=["furnish"], optional=True,
           desc=["Effortless Building can mirror, repeat and draw lines of blocks. The randomizer mixes materials for natural-looking walls."]),
         Q("drawers", "A Tidy Pantry", 4, 0, [Item("storagedrawers:oak_full_drawers_4")], [XP(30)], deps=["furnish"],
           desc=["Drawers hold large amounts of one item each: grain, flour, seeds. (The quest asks for oak 2x2 drawers, but any wood works the same.)"]),
         Q("workshop", "The Workshop", 4, 2, [Check()], [R("minecraft:iron_ingot", 8)], deps=["paths"], manual=True, icon="minecraft:smithing_table",
           desc=["A separate building for crafting, smelting and, soon, machines. Leave space for power equipment and run cable inside the walls."]),
         Q("backpack", "Packed for the Road", 6, -1, [Item("travelersbackpack:standard")], [R("minecraft:bread", 8)], deps=["drawers"], optional=True),
         Q("waystone", "Waystone", 6, 1, [Look("#waystones:waystones", observe="block_tag", title="Look at a waystone you placed")], [XP(60)], deps=["workshop"],
           desc=["Put a waystone at your homestead and you can always find your way back. Build more at distant villages and outposts."]),
         Q("homestead", "A Homestead", 8, 0, [Check()], [R("minecraft:emerald", 5), XP(120)], deps=["waystone", "drawers"], manual=True, preset="goal", icon="minecraft:bell",
           desc=["House, fields, barn, cellar, workshop. A place worth defending from the weather. Tick when it feels like a homestead."]),
     ]},
    # ------------------------------------------------------------------ 5
    {"key": "05_workshop_and_power", "tags": ["power"], "title": "Workshop & Power", "icon": "energizedpower:solar_panel_1",
     "subtitle": "Renewable electricity: solar, water and wind, batteries and hidden wiring.",
     "quests": [
         Q("power_info", "How Power Works Here", 0, 0, [Check("Got it")], [XP(20)], deps=["04_homes_and_services/workshop"], preset="info", icon="energizedpower:energy_analyzer",
           desc=["Every machine in this pack uses one kind of energy, measured in E (Team Reborn Energy). Energized Power provides solar panels, batteries, cables and machines. Homestead Core adds wind, water and household appliances.",
                 "",
                 "Solar follows daylight and loses most of its output under rain, thunder and at night.",
                 "A water wheel only turns in FLOWING water: 3 E/t for each flowing side. Still water and ice give nothing.",
                 "A wind turbine needs open sky and space around it, and does best high up. It generates from 3 m/s of wind, reaches full output (12 E/t) at 12 m/s, and shuts down above 25 m/s to protect itself.",
                 "",
                 "Batteries carry you through nights and storms. Appliances accept power from every side, so you can hide cables behind walls and under floors."]),
         Q("tin", "Tin Country", 2, -1, [Item("energizedpower:raw_tin", 16)], [R("minecraft:torch", 16)], deps=["power_info"],
           desc=["Tin ore generates underground like copper. Smelt it into tin ingots."]),
         Q("hammer", "Hammer Time", 2, 1, [Adv("energizedpower:main/basics/hammer", "Craft a hammer")], [XP(30)], deps=["power_info"],
           desc=["Hammer ingots into plates at the crafting table (the hammer takes damage)."]),
         Q("plates", "Plates and Wire", 4, 0, [Item("energizedpower:iron_plate", 8), Item("energizedpower:copper_wire", 8)], [XP(40)], deps=["tin", "hammer"]),
         Q("circuit", "Basic Circuits", 6, -1, [Item("energizedpower:basic_circuit", 3)], [XP(40)], deps=["plates"]),
         Q("frame", "Machine Frame", 6, 1, [Item("energizedpower:basic_machine_frame")], [XP(40)], deps=["plates"]),
         Q("solar", "Sunlight", 8, -2, [Item("energizedpower:solar_panel_1")], [XP(40)], deps=["circuit"],
           desc=["Small but free. Several Tier I panels add up."]),
         Q("wheel", "Running Water", 8, 0, [Item("homesteadcore:water_wheel")], [XP(40)], deps=["plates"],
           desc=["Place it beside a stream or a water channel with real flow. Sneak-use it to see how many sides are getting flow."]),
         Q("coal", "Coal Engine", 8, 2, [Item("energizedpower:coal_engine")], [XP(40)], deps=["frame"],
           desc=["Burns furnace fuel. A dependable backup when it's dark and calm."]),
         Q("spark", "First Spark", 10, 0, [Item("energizedpower:battery_box")], [R("energizedpower:tin_cable", 16)], deps=["solar", "wheel", "coal"], any_dep=True,
           desc=["A battery box stores 65,536 E. Connect your generator to it, then the battery to your machines."]),
         Q("cables", "Hidden Wiring", 12, -1, [Item("energizedpower:copper_cable", 16)], [XP(40)], deps=["spark"],
           desc=["Tin cable carries 128 E/t and copper 1,024 E/t. Run cables through a one-block cavity inside a wall or under the floor and cover them; appliances take power from their back and bottom."]),
         Q("turbine", "Wind Turbine", 12, 1, [Item("homesteadcore:wind_turbine")], [XP(60)], deps=["spark"],
           desc=["Put it on a hill or a tower with plenty of air around it. Sneak-use it to read wind speed, output and exposure.",
                 "Storms raise the wind, which offsets their effect on solar. In extreme gusts the turbine shuts down, so pair it with batteries."]),
         Q("analyzer", "Measure Twice", 14, -1, [Item("energizedpower:energy_analyzer")], [XP(30)], deps=["cables"], optional=True),
         Q("solar2", "More Sun", 14, 1, [Item("energizedpower:solar_panel_2", 2)], [XP(60)], deps=["turbine"]),
         Q("grid", "Off the Grid", 16, 0, [Item("energizedpower:battery_box", 3), Check("Tick when the lights stay on through a night and a storm")],
           [R("minecraft:emerald", 6), XP(150)], deps=["solar2", "cables"], preset="goal", icon="energizedpower:battery_box",
           desc=["A winter night with a fridge, a heater and a few lamps needs roughly 170,000 E. That's about three battery boxes charged by day.",
                 "Mixing generators is the reliable answer: sun by day, wind in storms, water all year until it freezes."]),
     ]},
    # ------------------------------------------------------------------ 6
    {"key": "06_modern_kitchen", "tags": ["power"], "title": "The Modern Kitchen", "icon": "homesteadcore:electric_range",
     "subtitle": "Appliances that use electricity, and what happens when the power goes out.",
     "quests": [
         Q("lamps", "Electric Light", 0, 0, [Item("energizedpower:powered_lamp", 4)], [XP(30)], deps=["05_workshop_and_power/spark"],
           desc=["Powered lamps draw 1 E/t each. Candles, lanterns and Macaw's lamps keep working without power."]),
         Q("fridge", "Refrigerator", 2, -1, [Item("homesteadcore:refrigerator")], [R("minecraft:cooked_salmon", 4)], deps=["lamps"],
           desc=["Draws 1 E/t. While powered, food inside does not spoil at all.",
                 "During an outage it stays cold for 5 minutes, then the food starts spoiling at the normal rate. Nothing vanishes; food only turns rotten the usual way.",
                 "Sneak-use it to check whether it's cold, still holding the cold, or warm."]),
         Q("range", "Electric Range", 2, 1, [Item("homesteadcore:electric_range")], [R("farmersdelight:tomato_sauce")], deps=["lamps"],
           desc=["The cooktop heats a Farmer's Delight pot or skillet placed on top. The oven cooks up to four foods at twice smoker speed. It draws 6 E/t while working.",
                 "Outage: oven progress pauses and resumes when power returns. The cooktop holds heat for 5 seconds; after that a cooking pot's progress slowly drops back, but its ingredients are never lost."]),
         Q("electric_meal", "Dinner on Electric", 4, 1, [Item("farmersdelight:pasta_with_meatballs")], [XP(60)], deps=["range"],
           desc=["Cook a meal in a pot on the electric range. No fuel, no smoke."]),
         Q("heater", "Space Heater", 4, -1, [Item("homesteadcore:space_heater")], [R("minecraft:white_carpet", 4)], deps=["fridge"],
           desc=["Draws 8 E/t while it runs. It switches on when a player is within 7 blocks, or whenever it gets a redstone signal; a barometer works well for that.",
                 "It lights the room and warms everyone nearby back towards comfortable, but never makes you overheat. It's the biggest power user in the house, so size your batteries for winter nights."]),
         Q("furnace", "Powered Furnace", 6, 0, [Item("energizedpower:powered_furnace")], [XP(40)], deps=["electric_meal"]),
         Q("kitchen", "Fitted Kitchen", 6, -2, [Check()], [R("minecraft:emerald", 3)], deps=["heater"], manual=True, icon="mcwfurnitures:oak_kitchen_cabinet",
           desc=["Counters, cabinets and a sink around your range and fridge. Hide the wiring behind the cabinets."]),
         Q("outage", "Outage Drill", 6, 2, [Check("Tick after you've let the batteries run flat once and recovered")], [XP(60)], deps=["furnace"], preset="info", icon="minecraft:redstone_torch",
           desc=["What a blackout looks like:",
                 "Fridge: cold for 5 more minutes, then food spoils normally.",
                 "Range: oven pauses; cooktop cools after 5 seconds.",
                 "Heater and lamps: off.",
                 "Machines: they stop and keep their contents.",
                 "Keep a campfire, a smoker and a barrel cellar as backups."]),
         Q("feast", "Sunday Dinner", 8, 0, [Item("farmersdelight:roast_chicken_block"), Item("farmersdelight:honey_glazed_ham_block"), Item("farmersdelight:shepherds_pie_block")],
           [R("minecraft:emerald", 8), XP(200)], deps=["kitchen", "outage"], preset="goal", icon="farmersdelight:roast_chicken_block",
           desc=["Three feasts, placed on the table, from a kitchen that runs on sun, wind and water."]),
     ]},
    # ------------------------------------------------------------------ 7
    {"key": "07_neighbours_and_market", "title": "Neighbours & Market", "icon": "farmingforblockheads:market",
     "subtitle": "Residents arrive. Trade, take on requests, and earn your place.",
     "quests": [
         Q("residents", "About Residents", 0, 0, [Check("Got it")], [XP(20)], deps=["04_homes_and_services/homestead"], preset="info", icon="mca:blueprint",
           desc=["Villagers here are people. They have names, moods, families and professions, and they remember how you treat them.",
                 "Your homestead can become a village: once you have at least 3 recognised buildings and villagers living in them, it counts as one.",
                 "A Blueprint shows which buildings the village recognises and what each building type needs.",
                 "",
                 "Chores: you can ask a resident to farm, chop, fish or hunt. Hunting can kill nearby animals, including your livestock, so only assign it far from your pens.",
                 "Optional AI conversations are off, and nothing in the pack needs an online service."]),
         Q("blueprint", "A Blueprint for a Town", 2, 0, [Item("mca:blueprint")], [R("minecraft:paper", 8)], deps=["residents"]),
         Q("friend", "Making Friends", 4, -1, [Adv("mca:friend", "Befriend a resident")], [R("mca:bouquet")], deps=["blueprint"],
           desc=["Chat, give gifts that suit their personality, and say hello every day."]),
         Q("first_home", "A Home for a Resident", 4, 1, [Check()], [R("minecraft:emerald", 3)], deps=["blueprint"], manual=True, icon="minecraft:red_bed",
           desc=["Build a small house with a bed, a door and light. Check the Blueprint until it shows up as a recognised building."]),
         Q("market", "Market Day", 6, -1, [Item("farmingforblockheads:market")], [R("minecraft:emerald", 4)], deps=["friend"],
           desc=["The market sells seeds, saplings and animals for emeralds. It's a good way to get crops that don't grow wild near you."]),
         Q("bin", "Shipping Bin", 6, 1, [Item("farmingforblockheads:shipping_bin")], [XP(40)], deps=["first_home"],
           desc=["Put surplus produce in the bin to sell it. Fields that feed the town also earn money."]),
         Q("merchant", "Merchant", 8, 0, [Adv("mca:merchant", "Reach the Merchant rank")], [R("minecraft:emerald", 6), XP(100)], deps=["market", "bin"],
           desc=["Your standing rises with the village's buildings and how well its people like you."]),
         Q("req_bread", "Request: Bread for the Bakery", 10, -2, [Item("minecraft:bread", 32, consume=True)], [R("minecraft:emerald", 4)], deps=["merchant"], repeat=DAY, icon="minecraft:bread",
           desc=["The village is short of bread. Deliver 32 loaves. (Repeatable once a day.)"]),
         Q("req_fuel", "Request: Winter Fuel", 10, -1, [Item("minecraft:charcoal", 32, consume=True)], [R("minecraft:emerald", 4)], deps=["merchant"], repeat=DAY, icon="minecraft:charcoal",
           desc=["Cold is coming and the hearths need feeding. Deliver 32 charcoal. (Repeatable once a day.)"]),
         Q("req_crates", "Request: Vegetable Crates", 10, 0, [Item("farmersdelight:cabbage_crate", 2, consume=True), Item("farmersdelight:carrot_crate", 2, consume=True)], [R("minecraft:emerald", 6)], deps=["merchant"], repeat=DAY, icon="farmersdelight:cabbage_crate"),
         Q("req_wool", "Request: Wool for Blankets", 10, 1, [Item("minecraft:white_wool", 16, consume=True)], [R("minecraft:emerald", 3)], deps=["merchant"], repeat=DAY, icon="minecraft:white_wool"),
         Q("req_soup", "Request: Soup Kitchen", 10, 2, [Item("farmersdelight:vegetable_soup", 6, consume=True)], [R("minecraft:emerald", 5)], deps=["merchant"], repeat=DAY, icon="farmersdelight:vegetable_soup",
           desc=["Hot soup for residents caught out in the last blizzard."]),
         Q("chores", "Many Hands", 12, 0, [Adv("mca:chores", "Assign a chore")], [XP(60)], deps=["merchant"], optional=True,
           desc=["Residents can farm or chop wood for you. Remember that hunting means livestock."]),
     ]},
    # ------------------------------------------------------------------ 8
    {"key": "08_established_community", "title": "Established Community", "icon": "minecraft:bell",
     "subtitle": "Town projects and a place that runs itself.",
     "quests": [
         Q("noble", "Noble", 0, 0, [Adv("mca:noble", "Reach the Noble rank")], [R("minecraft:emerald", 8)], deps=["07_neighbours_and_market/merchant"]),
         Q("bakery", "Town Bakery", 2, -2, [Check()], [R("farmersdelight:wheat_dough", 8)], deps=["noble"], manual=True, icon="minecraft:bread",
           desc=["A bakery building with ovens and storage. An electric range makes a fine bakery oven."]),
         Q("inn", "The Inn", 2, 0, [Check()], [R("minecraft:emerald", 5)], deps=["noble"], manual=True, icon="minecraft:cake",
           desc=["A recognised inn brings visitors and new residents."]),
         Q("hall", "Town Hall", 2, 2, [Check()], [R("minecraft:emerald", 5)], deps=["noble"], manual=True, icon="minecraft:bell",
           desc=["A place to gather. Put a barometer outside so everyone sees the forecast."]),
         Q("wedding", "Wedding Bells", 4, -2, [Adv("mca:marriage", "Get married")], [XP(100)], deps=["inn"], optional=True),
         Q("mayor", "Mayor", 4, 0, [Adv("mca:mayor", "Become Mayor")], [R("minecraft:emerald", 12), XP(200)], deps=["bakery", "inn", "hall"]),
         Q("town_power", "Power for the Town", 6, -1,
           [Item("energizedpower:advanced_battery_box"), Item("ftbquests:screen_1", title="Craft a Task Screen (stone around a book)"),
            Energy(500_000, 2000, "Feed 500,000 E from the town grid into the Task Screen")],
           [XP(150), R("energizedpower:copper_cable", 32)], deps=["mayor"],
           desc=["An Advanced Battery Box stores 8.4 million E, enough for a street of homes. Wire the whole town and every winter night stays lit.",
                 "",
                 "Prove the grid can carry the load: place a Task Screen, right-click it and choose this quest's energy task, then run a cable from your batteries into it. The screen uses up the energy as it fills (up to 2,000 E per tick)."]),
         Q("stockpile", "Winter Stockpile", 6, 1, [Item("farmersdelight:potato_crate", 4), Item("farmersdelight:carrot_crate", 4), Item("farmersdelight:rice_bale", 4), Item("farmersdelight:onion_crate", 4)],
           [R("minecraft:emerald", 8)], deps=["mayor"],
           desc=["Full crates in the cellar before the first snow. The town eats well all winter."]),
         Q("waystones", "Roads Between Towns", 8, -1, [Check("Tick when three settlements are linked by waystones")], [XP(100)], deps=["town_power"], manual=True, icon="waystones:andesite_waystone"),
         Q("monarch", "A Town Worth Keeping", 8, 1, [Adv("mca:monarch", "Reach the highest rank")], [R("minecraft:emerald", 20), XP(400)], deps=["stockpile"],
           preset="goal", icon="minecraft:golden_helmet",
           desc=["What started as a campfire is now a town. Its residents have homes, work, power and a full larder for winter."]),
     ]},
    # ------------------------------------------------------------------ 9
    {"key": "09_horizons", "title": "Horizons", "icon": "naturescompass:naturescompass",
     "subtitle": "Exploration and long-term goals.",
     "quests": [
         Q("compass", "Nature's Compass", 0, 0, [Item("naturescompass:naturescompass")], [R("minecraft:bread", 8)], deps=["01_founding_camp/camp"],
           desc=["Finds biomes, handy when you need a crop that only grows wild in one place."]),
         Q("village", "Visit a Village", 2, -1, [Structure("#minecraft:village", "Find any village")], [R("minecraft:emerald", 3)], deps=["compass"],
           desc=["Vanilla, Terralith and Towns & Towers villages are good places to trade and gather ideas. Your own town is built, not found."]),
         Q("mountains", "Into the Highlands", 2, 1, [Biome("terralith:alpine_highlands", "Visit Alpine Highlands")], [XP(60)], deps=["compass"],
           desc=["High ground gets more wind: a good spot for a turbine, if you can get power home."]),
         Q("cold", "Where Winter Lives", 4, 1, [Biome("minecraft:snowy_plains", "Visit Snowy Plains")], [R("minecraft:cooked_beef", 4)], deps=["mountains"],
           desc=["It snows here all year, so every front is a blizzard. Wear fur and bring food."]),
         Q("castle", "The Frostologer's Castle", 6, 1, [Adv("frostiful:adventure/find_frostologer_castle", "Find a Frostologer castle")], [XP(150)], deps=["cold"], optional=True),
         Q("mineshaft", "Deep Shafts", 4, -1, [Structure("bettermineshafts:mineshaft_oak", "Find an oak mineshaft")], [R("minecraft:torch", 32)], deps=["village"], optional=True),
         Q("diamonds", "Diamonds", 6, -1, [Adv("minecraft:story/mine_diamond", "Mine a diamond")], [XP(100)], deps=["village"]),
         Q("nether", "Beyond the Portal", 8, -1, [Adv("minecraft:story/enter_the_nether", "Enter the Nether")], [XP(150)], deps=["diamonds"], optional=True),
         Q("adventuring", "Adventuring Time", 8, 1, [Adv("minecraft:adventure/adventuring_time", "Visit every vanilla biome")], [R("minecraft:emerald", 16), XP(500)], deps=["castle", "diamonds"],
           any_dep=True, preset="goal", icon="minecraft:filled_map",
           desc=["A very long-term goal. Do it a little at a time, between seasons."]),
     ]},
]
