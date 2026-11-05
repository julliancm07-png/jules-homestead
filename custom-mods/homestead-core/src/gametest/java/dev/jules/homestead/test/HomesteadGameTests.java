package dev.jules.homestead.test;

import com.github.thedeathlycow.thermoo.api.core.v2.registry.ThermooRegistries;
import com.mojang.authlib.GameProfile;
import com.github.thedeathlycow.thermoo.api.core.v2.source.TemperatureSource;
import com.mojang.serialization.DataResult;
import dev.jules.homestead.block.entity.ElectricRangeBlockEntity;
import dev.jules.homestead.block.entity.RefrigeratorBlockEntity;
import dev.jules.homestead.block.entity.SpaceHeaterBlockEntity;
import dev.jules.homestead.block.entity.WaterWheelBlockEntity;
import dev.jules.homestead.block.entity.WindTurbineBlockEntity;
import dev.jules.homestead.compat.ThermooCompat;
import dev.jules.homestead.config.HomesteadConfig;
import dev.jules.homestead.registry.ModBlocks;
import dev.jules.homestead.weather.Exposure;
import dev.jules.homestead.weather.Front;
import dev.jules.homestead.weather.WeatherManager;
import dev.jules.homestead.weather.WeatherSavedData;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import io.netty.channel.embedded.EmbeddedChannel;
import team.reborn.energy.api.EnergyStorage;

import java.util.UUID;

/**
 * Runtime tests executed on a real dedicated-server GameTest instance with Thermoo, Cardinal Components, Spoiled and
 * Forge Config API Port loaded (see build.gradle). Each test documents what it establishes.
 */
public class HomesteadGameTests {
	private static void floor(GameTestHelper h, int size) {
		for (int x = 0; x < size; x++) {
			for (int z = 0; z < size; z++) {
				h.setBlock(x, 0, z, Blocks.STONE);
			}
		}
	}

	private static long insert(GameTestHelper h, BlockPos rel, long amount) {
		EnergyStorage s = EnergyStorage.SIDED.find(h.getLevel(), h.absolutePos(rel), Direction.UP);
		h.assertTrue(s != null, "machine exposes a TR Energy storage on its top face");
		try (Transaction tx = Transaction.openOuter()) {
			long n = s.insert(amount, tx);
			tx.commit();
			return n;
		}
	}

	/**
	 * Establishes: a commanded front sets vanilla rain+thunder, intensity ramps above 0, storm wind drives a turbine
	 * with open sky to generate TR Energy, the saved data round-trips through its codec, and stop ramps the front
	 * down to completion and clears vanilla weather.
	 */
	@GameTest(maxTicks = 1200)
	public void frontLifecycleWindAndPersistence(GameTestHelper h) {
		MinecraftServer server = h.getLevel().getServer();
		floor(h, 5);
		BlockPos turbine = new BlockPos(2, 1, 2);
		h.setBlock(turbine, ModBlocks.WIND_TURBINE);
		WeatherManager.startNow(server, Front.Kind.STORM, 400, 1);
		h.assertTrue(server.getWeatherData().isRaining() && server.getWeatherData().isThundering(), "front sets vanilla rain + thunder");
		h.runAfterDelay(120, () -> {
			WeatherSavedData d = WeatherManager.data(server);
			long now = server.overworld().getGameTime();
			h.assertTrue(d.current() != null, "front is active");
			float i = d.current().intensityAt(now);
			h.assertTrue(i > 0.5f && i <= 0.6f + 1e-4, "severity-1 plateau intensity is 0.6 (was " + i + ")");
			double wind = WeatherManager.windSpeed(server, now, h.absolutePos(turbine).getY(), server.overworld().getSeaLevel());
			h.assertTrue(wind > HomesteadConfig.get().power.windCutInMs, "storm wind above cut-in (was " + wind + ")");
			WindTurbineBlockEntity be = h.getBlockEntity(turbine, WindTurbineBlockEntity.class);
			h.assertTrue(be.storedEnergy() > 0, "turbine generated energy during the storm (stored " + be.storedEnergy() + ")");
			DataResult<Tag> enc = WeatherSavedData.CODEC.encodeStart(NbtOps.INSTANCE, d);
			WeatherSavedData back = WeatherSavedData.CODEC.parse(NbtOps.INSTANCE, enc.getOrThrow()).getOrThrow();
			h.assertValueEqual(back.current(), d.current(), "codec round-trip of current front");
			h.assertTrue(WeatherManager.stopNow(server), "stop accepted");
			h.succeedWhen(() -> {
				h.assertTrue(WeatherManager.data(server).current() == null, "front ended after ramp-down");
				h.assertTrue(!server.getWeatherData().isThundering(), "vanilla thunder cleared");
			});
		});
	}

	/** Establishes the shelter rule: open sky = exposed; roof without upwind wall = exposed; roof + upwind wall = sheltered. */
	@GameTest(skyAccess = true)
	public void shelterHeuristic(GameTestHelper h) {
		ServerLevel level = h.getLevel();
		floor(h, 7);
		ArmorStand stand = h.spawn(EntityType.ARMOR_STAND, new Vec3(3.5, 1, 3.5));
		h.assertTrue(Exposure.isExposed(level, stand, 0f, 8), "open sky is exposed");
		h.setBlock(3, 4, 3, Blocks.GLASS);
		h.assertTrue(!Exposure.openToSky(level, h.absolutePos(new BlockPos(3, 2, 3))), "glass counts as a roof (greenhouses shelter)");
		h.setBlock(3, 4, 3, Blocks.AIR);
		for (int x = 0; x < 7; x++) {
			for (int z = 0; z < 7; z++) {
				h.setBlock(x, 4, z, Blocks.OAK_PLANKS);
			}
		}
		h.assertTrue(!Exposure.openToSky(level, h.absolutePos(new BlockPos(3, 2, 3))), "roof detected immediately via heightmap");
		// Wind from north (0°): upwind ray points to -Z. Walls only on the south side => still exposed.
		for (int x = 0; x < 7; x++) {
			for (int y = 1; y < 4; y++) {
				h.setBlock(x, y, 6, Blocks.OAK_PLANKS);
			}
		}
		// GameTest areas are walled with barriers, so keep the upwind ray (3 blocks) inside the 7x7 test area.
		h.assertTrue(Exposure.isExposed(level, stand, 0f, 3), "roof with open windward side is exposed");
		for (int x = 0; x < 7; x++) {
			for (int y = 1; y < 4; y++) {
				h.setBlock(x, y, 0, Blocks.OAK_PLANKS);
			}
		}
		h.assertTrue(!Exposure.isExposed(level, stand, 0f, 3), "roof + windward wall is sheltered");
		h.assertTrue(Exposure.isExposed(level, stand, 180f, 2), "upwind wall beyond the check distance does not shelter");
		h.succeed();
	}

	/**
	 * Establishes (with real Spoiled loaded): a vanilla chest's food starts spoiling (control); a powered fridge's
	 * food does not; an unpowered fridge stays cold for the retention window, then its food starts spoiling.
	 */
	@GameTest(maxTicks = 1200) // Spoiled starts spoiling a container on its own schedule; leave headroom
	public void refrigeratorPreservesOnlyWhilePowered(GameTestHelper h) {
		HomesteadConfig.get().appliances.refrigeratorColdRetentionTicks = 60;
		floor(h, 6);
		BlockPos powered = new BlockPos(1, 1, 1);
		BlockPos unpowered = new BlockPos(3, 1, 1);
		BlockPos chest = new BlockPos(5, 1, 1);
		h.setBlock(powered, ModBlocks.REFRIGERATOR);
		h.setBlock(unpowered, ModBlocks.REFRIGERATOR);
		h.setBlock(chest, Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.NORTH));
		RefrigeratorBlockEntity a = h.getBlockEntity(powered, RefrigeratorBlockEntity.class);
		RefrigeratorBlockEntity b = h.getBlockEntity(unpowered, RefrigeratorBlockEntity.class);
		a.items().setItem(0, new ItemStack(Items.COOKED_BEEF, 4));
		b.items().setItem(0, new ItemStack(Items.COOKED_BEEF, 4));
		h.getBlockEntity(chest, ChestBlockEntity.class).setItem(0, new ItemStack(Items.COOKED_BEEF, 4));
		h.getBlockEntity(powered, RefrigeratorBlockEntity.class).setStoredEnergy(2_000);
		insert(h, unpowered, 20); // ~20 ticks of power, then outage begins
		h.assertTrue(com.mrbysco.spoiled.util.SpoilHelper.getSpoilRecipe(h.getLevel(), new ItemStack(Items.COOKED_BEEF)) != null,
				"Spoiled has a spoil recipe for cooked beef (rate " + com.mrbysco.spoiled.config.SpoiledConfigCache.spoilRate + ")");
		h.runAfterDelay(30, () -> h.assertTrue(b.isCold(), "unpowered fridge still cold inside retention window"));
		h.succeedWhen(() -> {
			ItemStack inChest = h.getBlockEntity(chest, ChestBlockEntity.class).getItem(0);
			h.assertTrue(com.mrbysco.spoiled.util.SpoilHelper.isSpoiling(inChest), "control: food in a chest spoils (Spoiled active)");
			h.assertTrue(!b.isCold(), "unpowered fridge warmed up after retention");
			h.assertTrue(com.mrbysco.spoiled.util.SpoilHelper.isSpoiling(b.items().getItem(0)), "food in the warm fridge spoils");
			h.assertTrue(a.isCold(), "powered fridge stays cold");
			h.assertTrue(!com.mrbysco.spoiled.util.SpoilHelper.isSpoiling(a.items().getItem(0)), "food in the powered fridge never started spoiling");
			h.assertTrue(h.getBlockState(powered).getValue(BlockStateProperties.POWERED), "powered fridge shows POWERED");
		});
	}

	/**
	 * Establishes: the oven cooks a smoking recipe only while powered, progress PAUSES on outage (no regression), and
	 * the cooktop is LIT (Farmer's Delight heat-source gate) only while powered with cookware above, plus residual heat.
	 */
	@GameTest(maxTicks = 600)
	public void electricRangeOvenAndCooktop(GameTestHelper h) {
		floor(h, 3);
		BlockPos pos = new BlockPos(1, 1, 1);
		h.setBlock(pos, ModBlocks.ELECTRIC_RANGE);
		ElectricRangeBlockEntity be = h.getBlockEntity(pos, ElectricRangeBlockEntity.class);
		be.items().setItem(0, new ItemStack(Items.BEEF, 2));
		h.runAfterDelay(10, () -> {
			h.assertValueEqual(be.progress(0), 0.0, "no progress without power");
			h.assertTrue(!h.getBlockState(pos).getValue(BlockStateProperties.LIT), "unlit without power");
			insert(h, pos, 120); // 120 E at 6 E/t = 20 ticks of cooking
			h.setBlock(pos.above(), Blocks.CAULDRON); // stand-in cookware
		});
		h.runAfterDelay(15, () -> h.assertTrue(h.getBlockState(pos).getValue(BlockStateProperties.LIT), "cooktop LIT while powered with cookware"));
		h.runAfterDelay(45, () -> {
			double p = be.progress(0);
			h.assertTrue(p > 0, "oven made progress while powered");
			h.assertTrue(be.storedEnergy() < 6, "energy exhausted");
			h.runAfterDelay(40, () -> h.assertValueEqual(be.progress(0), p, "progress paused (not regressed) during outage"));
		});
		h.runAfterDelay(200, () -> {
			h.assertTrue(!h.getBlockState(pos).getValue(BlockStateProperties.LIT), "cooktop unlit after residual heat expired");
			be.setStoredEnergy(be.energyCapacity());
		});
		h.succeedWhen(() -> {
			ItemStack out = be.items().getItem(ElectricRangeBlockEntity.OVEN_SLOTS);
			h.assertTrue(out.is(Items.COOKED_BEEF) && out.getCount() == 2, "both beef cooked once power returned (" + out + ")");
		});
	}

	/** Establishes: flowing water beside the wheel generates energy; still (source) water alone does not. */
	@GameTest(maxTicks = 200)
	public void waterWheelNeedsFlowingWater(GameTestHelper h) {
		floor(h, 8);
		BlockPos flowing = new BlockPos(1, 1, 1);
		BlockPos still = new BlockPos(1, 1, 6);
		h.setBlock(flowing, ModBlocks.WATER_WHEEL);
		h.setBlock(still, ModBlocks.WATER_WHEEL);
		// Source two blocks away on a stone floor: water flows into (2,1,1) towards the wheel.
		h.setBlock(3, 1, 1, Blocks.WATER);
		// Still water: a source block hemmed in on every other side.
		h.setBlock(0, 1, 6, Blocks.STONE);
		h.setBlock(0, 1, 5, Blocks.STONE);
		h.setBlock(0, 1, 7, Blocks.STONE);
		h.setBlock(1, 1, 5, Blocks.STONE);
		h.setBlock(1, 1, 7, Blocks.STONE);
		h.setBlock(2, 1, 6, Blocks.STONE);
		h.setBlock(0, 2, 6, Blocks.STONE);
		h.setBlock(0, 1, 6, Blocks.WATER);
		h.succeedWhen(() -> {
			WaterWheelBlockEntity f = h.getBlockEntity(flowing, WaterWheelBlockEntity.class);
			WaterWheelBlockEntity s = h.getBlockEntity(still, WaterWheelBlockEntity.class);
			h.assertTrue(f.flowingFaces() >= 1 && f.storedEnergy() > 0, "flowing water powers the wheel (faces " + f.flowingFaces() + ")");
			h.assertTrue(s.flowingFaces() == 0 && s.storedEnergy() == 0, "still water does not (faces " + s.flowingFaces() + ")");
		});
	}

	/** Establishes: heater runs only with power + demand, emits light 15 while running and registers its warm radius. */
	@GameTest(maxTicks = 200)
	public void spaceHeaterRunsOnPowerAndDemand(GameTestHelper h) {
		floor(h, 5);
		BlockPos pos = new BlockPos(2, 1, 2);
		h.setBlock(pos, ModBlocks.SPACE_HEATER);
		h.setBlock(pos.east(), Blocks.REDSTONE_BLOCK);
		h.runAfterDelay(25, () -> h.assertTrue(!h.getBlockState(pos).getValue(BlockStateProperties.LIT), "no power => off"));
		h.runAfterDelay(26, () -> {
			insert(h, pos, 8); // API path works...
			h.getBlockEntity(pos, SpaceHeaterBlockEntity.class).setStoredEnergy(4_000); // ...then fill for the rest of the test
		});
		h.succeedWhen(() -> {
			h.assertTrue(h.getBlockState(pos).getValue(BlockStateProperties.LIT), "powered + redstone demand => running");
			h.assertTrue(h.getLevel().getBrightness(LightLayer.BLOCK, h.absolutePos(pos.north())) >= 14, "emits light (Frostiful warmth-from-light)");
			h.assertTrue(SpaceHeaterBlockEntity.isWarmedByHeater(h.getLevel(), h.absolutePos(pos.north(3)).getCenter()), "warm radius registered");
		});
	}

	/** Establishes: our Thermoo temperature sources load from data with tick_interval 20, and the exposure maths. */
	@GameTest
	public void thermooSourcesRegistered(GameTestHelper h) {
		var reg = h.getLevel().registryAccess().lookupOrThrow(ThermooRegistries.TEMPERATURE_SOURCE);
		TemperatureSource blizzard = reg.getValueOrThrow(ThermooCompat.BLIZZARD);
		TemperatureSource heater = reg.getValueOrThrow(ThermooCompat.SPACE_HEATER);
		h.assertValueEqual(blizzard.tickInterval(), ThermooCompat.INTERVAL, "blizzard source interval");
		h.assertValueEqual(heater.tickInterval(), ThermooCompat.INTERVAL, "heater source interval");
		h.assertTrue(blizzard.reduction().isPresent(), "blizzard cold is reduced by frost resistance");
		var exposed = new WeatherManager.PlayerWeather(true, Biome.Precipitation.SNOW, 1f, 3, Front.Kind.BLIZZARD);
		var sheltered = new WeatherManager.PlayerWeather(false, Biome.Precipitation.SNOW, 1f, 3, Front.Kind.BLIZZARD);
		var rain = new WeatherManager.PlayerWeather(true, Biome.Precipitation.RAIN, 1f, 3, Front.Kind.STORM);
		h.assertValueEqual(ThermooCompat.blizzardChange(exposed), -3 * 20, "severe blizzard cold per second");
		h.assertValueEqual(ThermooCompat.blizzardChange(sheltered), 0, "sheltered => no blizzard cold");
		h.assertValueEqual(ThermooCompat.blizzardChange(rain), 0, "rain storm => no blizzard cold");
		h.succeed();
	}

	// ------------------------------------------------------------------ weather-exposure helpers

	/** Survival test player (vanilla's mock player is always creative, which our slowdown deliberately ignores). */
	private static ServerPlayer survivalPlayer(GameTestHelper h, Vec3 rel) {
		MinecraftServer server = h.getLevel().getServer();
		CommonListenerCookie cookie = CommonListenerCookie.createInitial(new GameProfile(UUID.randomUUID(), "weather-test"), false);
		ServerPlayer p = new ServerPlayer(server, h.getLevel(), cookie.gameProfile(), cookie.clientInformation());
		Connection connection = new Connection(PacketFlow.SERVERBOUND);
		new EmbeddedChannel(connection);
		server.getPlayerList().placeNewPlayer(connection, p, cookie);
		p.setGameMode(GameType.SURVIVAL);
		p.setInvulnerable(true); // Thermoo cold must not kill the test player during the blizzard
		p.snapTo(h.absoluteVec(rel));
		return p;
	}

	/**
	 * Runs /fillbiome over the test area plus one 4-block biome cell of margin and returns the old biome. The margin
	 * matters: Level#getBiome uses the fuzzed (zoomed) biome lookup, so blocks near an edge can read the neighbouring cell.
	 */
	private static String fillBiome(GameTestHelper h, int size, String biome) {
		BlockPos a = h.absolutePos(new BlockPos(-4, -4, -4));
		BlockPos b = h.absolutePos(new BlockPos(size + 3, 12, size + 3));
		String old = h.getLevel().getBiome(h.absolutePos(new BlockPos(size / 2, 1, size / 2))).getRegisteredName();
		MinecraftServer server = h.getLevel().getServer();
		server.getCommands().performPrefixedCommand(server.createCommandSourceStack().withSuppressedOutput(),
				String.format("fillbiome %d %d %d %d %d %d %s", Math.min(a.getX(), b.getX()), Math.min(a.getY(), b.getY()), Math.min(a.getZ(), b.getZ()),
						Math.max(a.getX(), b.getX()), Math.max(a.getY(), b.getY()), Math.max(a.getZ(), b.getZ()), biome));
		return old;
	}

	/** 5x5 hut (outer x/z 1..5, walls y 1..3, roof y 4) around a player standing at (3, 1, 3): sheltered from any wind. */
	private static void hut(GameTestHelper h) {
		for (int x = 1; x <= 5; x++) {
			for (int z = 1; z <= 5; z++) {
				h.setBlock(x, 4, z, Blocks.OAK_PLANKS);
				if (x == 1 || x == 5 || z == 1 || z == 5) {
					for (int y = 1; y <= 3; y++) {
						h.setBlock(x, y, z, Blocks.OAK_PLANKS);
					}
				}
			}
		}
	}

	private static boolean hasAdvancement(ServerPlayer p, String path) {
		var holder = p.level().getServer().getAdvancements().get(dev.jules.homestead.HomesteadCore.id(path));
		return holder != null && p.getAdvancements().getOrStartProgress(holder).isDone();
	}

	private static double slowdown(ServerPlayer p) {
		AttributeModifier m = p.getAttribute(Attributes.MOVEMENT_SPEED).getModifier(WeatherManager.SLOWDOWN_ID);
		return m == null ? 0 : m.amount();
	}

	/**
	 * Establishes (own batch, so no other test's front interferes): an exposed survival player in a rain storm is slowed
	 * by exactly stormSlowdown × intensity and earns weather/witness_front but not caught_in_blizzard; shelter
	 * (roof + walls) removes the slowdown; ending the front clears it and vanilla thunder.
	 */
	@GameTest(environment = "homesteadcore:storm_front", skyAccess = true, maxTicks = 1200)
	public void stormSlowdownAndShelter(GameTestHelper h) {
		MinecraftServer server = h.getLevel().getServer();
		floor(h, 7);
		String oldBiome = fillBiome(h, 7, "minecraft:plains");
		ServerPlayer p = survivalPlayer(h, new Vec3(3.5, 1, 3.5));
		h.assertValueEqual(Exposure.precipitationAt(h.getLevel(), p.blockPosition()), Biome.Precipitation.RAIN, "precondition: it rains here");
		WeatherManager.startNow(server, Front.Kind.STORM, 2000, 2);
		h.startSequence()
				.thenIdle(40)
				.thenExecute(() -> {
					WeatherManager.PlayerWeather st = WeatherManager.playerState(p.getUUID());
					h.assertTrue(st.stormExposure(), "player in the open counts as storm-exposed (" + st + ")");
					double expected = -HomesteadConfig.get().weather.stormSlowdown * st.intensity();
					h.assertTrue(expected < 0 && Math.abs(slowdown(p) - expected) < 1e-6, "storm slowdown = stormSlowdown x intensity (" + slowdown(p) + " vs " + expected + ")");
					h.assertTrue(hasAdvancement(p, "weather/witness_front"), "witness_front awarded");
					h.assertTrue(!hasAdvancement(p, "weather/caught_in_blizzard"), "rain storm does not award caught_in_blizzard");
					hut(h);
				})
				.thenIdle(20)
				.thenExecute(() -> {
					h.assertTrue(!WeatherManager.playerState(p.getUUID()).exposed(), "inside the hut the player is sheltered");
					h.assertValueEqual(slowdown(p), 0.0, "shelter removes the slowdown");
					h.assertTrue(WeatherManager.stopNow(server), "stop accepted");
				})
				.thenWaitUntil(() -> h.assertTrue(WeatherManager.data(server).current() == null, "front ended"))
				.thenIdle(20)
				.thenExecute(() -> {
					h.assertValueEqual(slowdown(p), 0.0, "no slowdown after the front");
					h.assertTrue(!server.getWeatherData().isThundering(), "vanilla thunder cleared");
					server.getPlayerList().remove(p);
					fillBiome(h, 7, oldBiome);
				})
				.thenSucceed();
	}

	/**
	 * Establishes (own batch): snow build-up rules (open natural ground gains layers up to the cap; no snow under a
	 * roof or on no_snow_accumulation blocks such as farmland and hay), live blizzard accumulation around an exposed player,
	 * blizzard slowdown = blizzardSlowdown × intensity, the caught_in_blizzard advancement, and safe_and_warm after
	 * 60 s sheltered during the blizzard.
	 */
	@GameTest(environment = "homesteadcore:blizzard_front", skyAccess = true, maxTicks = 3000)
	public void blizzardSlowdownSnowAndAdvancements(GameTestHelper h) {
		MinecraftServer server = h.getLevel().getServer();
		ServerLevel level = h.getLevel();
		HomesteadConfig.Weather cfg = HomesteadConfig.get().weather;
		int oldRadius = cfg.snowAccumulationRadius;
		int oldAttempts = cfg.snowAccumulationAttemptsPerSecond;
		// Vanilla snowfall also lays single snow layers (on full blocks such as hay). Switch it off for this test so every
		// layer observed below comes from homestead-core's build-up.
		var rules = level.getGameRules();
		int oldVanillaSnow = rules.get(GameRules.MAX_SNOW_ACCUMULATION_HEIGHT);
		rules.set(GameRules.MAX_SNOW_ACCUMULATION_HEIGHT, 0, server);
		floor(h, 7);
		String oldBiome = fillBiome(h, 7, "minecraft:snowy_plains");
		h.assertValueEqual(Exposure.precipitationAt(level, h.absolutePos(new BlockPos(3, 1, 3))), Biome.Precipitation.SNOW, "precondition: it snows here");

		// Deterministic rules first (no front needed).
		BlockPos open = new BlockPos(0, 1, 0);
		int cap = cfg.snowAccumulationMaxLayers;
		for (int i = 1; i <= cap; i++) {
			BlockPos abs = h.absolutePos(open);
			h.assertTrue(WeatherManager.tryAddSnowLayer(level, abs, cap), "layer " + i + " added on open stone (precip "
					+ Exposure.precipitationAt(level, abs) + ", open " + Exposure.openToSky(level, abs) + ", at " + level.getBlockState(abs)
					+ ", below " + level.getBlockState(abs.below()) + ", survive " + Blocks.SNOW.defaultBlockState().canSurvive(level, abs) + ")");
		}
		h.assertValueEqual(h.getBlockState(open).getValue(SnowLayerBlock.LAYERS), cap, "layers stop at the configured cap");
		h.assertTrue(!WeatherManager.tryAddSnowLayer(level, h.absolutePos(open), cap), "no layer beyond the cap");
		h.setBlock(6, 0, 0, Blocks.FARMLAND);
		h.assertTrue(!WeatherManager.tryAddSnowLayer(level, h.absolutePos(new BlockPos(6, 1, 0)), cap), "farmland never gets snow (no_snow_accumulation)");
		h.setBlock(0, 3, 6, Blocks.OAK_PLANKS);
		h.assertTrue(!WeatherManager.tryAddSnowLayer(level, h.absolutePos(new BlockPos(0, 1, 6)), cap), "no snow under a roof");
		h.setBlock(open, Blocks.AIR);
		h.setBlock(0, 3, 6, Blocks.AIR);
		// Control row along z = 6 for the live accumulation check: hay bales (no_snow_accumulation). Not farmland: dry
		// farmland turns back into dirt on random ticks, and snow then (correctly) settles on the dirt.
		for (int x = 0; x < 7; x++) {
			h.setBlock(x, 0, 6, Blocks.HAY_BLOCK);
		}

		// Live blizzard: keep accumulation inside the 7x7 test area and make it frequent enough to observe.
		cfg.snowAccumulationRadius = 3;
		cfg.snowAccumulationAttemptsPerSecond = 40;
		ServerPlayer p = survivalPlayer(h, new Vec3(3.5, 1, 3.5));
		long[] shelteredAt = {0};
		WeatherManager.startNow(server, Front.Kind.BLIZZARD, 3000, 3);
		// Snow only builds up above intensity 0.2 (reached ~90 ticks into the 450-tick ramp), in passes every 20 ticks:
		// by tick 200 several passes have run at ~0.3-0.45 intensity, so the area check below is not timing-sensitive.
		h.startSequence()
				.thenIdle(200)
				.thenExecute(() -> {
					WeatherManager.PlayerWeather st = WeatherManager.playerState(p.getUUID());
					h.assertTrue(st.blizzardExposure(), "player in the open counts as blizzard-exposed (" + st + ")");
					double expected = -cfg.blizzardSlowdown * st.intensity();
					h.assertTrue(expected < 0 && Math.abs(slowdown(p) - expected) < 1e-6, "blizzard slowdown = blizzardSlowdown x intensity (" + slowdown(p) + " vs " + expected + ")");
					h.assertTrue(hasAdvancement(p, "weather/witness_front"), "witness_front awarded");
					h.assertTrue(hasAdvancement(p, "weather/caught_in_blizzard"), "caught_in_blizzard awarded");
					h.assertTrue(!hasAdvancement(p, "weather/safe_and_warm"), "safe_and_warm not yet");
					int snowy = 0;
					for (int x = 0; x < 7; x++) {
						for (int z = 0; z < 6; z++) {
							if (h.getBlockState(new BlockPos(x, 1, z)).is(Blocks.SNOW)) {
								snowy++;
							}
						}
						h.assertTrue(!h.getBlockState(new BlockPos(x, 1, 6)).is(Blocks.SNOW), "no snow on the hay-bale row at x=" + x);
					}
					h.assertTrue(snowy > 0, "snow accumulated on open ground around the player (" + snowy + " columns)");
					hut(h);
					shelteredAt[0] = level.getGameTime();
				})
				.thenIdle(20)
				.thenExecute(() -> {
					h.assertTrue(!WeatherManager.playerState(p.getUUID()).exposed(), "inside the hut the player is sheltered");
					h.assertValueEqual(slowdown(p), 0.0, "shelter removes the blizzard slowdown");
				})
				// 120 sheltered 10-tick steps = 60 s
				.thenWaitUntil(() -> h.assertTrue(hasAdvancement(p, "weather/safe_and_warm"), "safe_and_warm after 60 s sheltered"))
				.thenExecute(() -> {
					long waited = level.getGameTime() - shelteredAt[0];
					h.assertTrue(waited >= 1180, "safe_and_warm needed ~60 s of shelter, not less (" + waited + " ticks)");
				})
				.thenExecute(() -> h.assertTrue(WeatherManager.stopNow(server), "stop accepted"))
				.thenWaitUntil(() -> h.assertTrue(WeatherManager.data(server).current() == null, "front ended"))
				.thenExecute(() -> {
					cfg.snowAccumulationRadius = oldRadius;
					cfg.snowAccumulationAttemptsPerSecond = oldAttempts;
					rules.set(GameRules.MAX_SNOW_ACCUMULATION_HEIGHT, oldVanillaSnow, server);
					server.getPlayerList().remove(p);
					fillBiome(h, 7, oldBiome);
				})
				.thenSucceed();
	}
}
