package dev.jules.homestead.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import dev.jules.homestead.HomesteadCore;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * JSON config at config/homesteadcore.json. Missing fields fall back to defaults; the file is rewritten with all
 * fields on load so new options appear automatically.
 */
public final class HomesteadConfig {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	private static HomesteadConfig instance = new HomesteadConfig();

	public Weather weather = new Weather();
	public Power power = new Power();
	public Appliances appliances = new Appliances();
	public Client client = new Client();

	public static final class Weather {
		/** Master switch for severe fronts (storms/blizzards). Vanilla weather is untouched when false. */
		public boolean enableSevereWeather = true;
		/** Gap between fronts, in ticks (24000 = one in-game day). */
		public int minGapTicks = 48_000;
		public int maxGapTicks = 120_000;
		/** First front of a new world is never sooner than this (lets players settle in). */
		public int firstFrontDelayTicks = 72_000;
		public int minDurationTicks = 6_000;
		public int maxDurationTicks = 14_000;
		/** Probability that a front is a blizzard, per Serene Seasons season. Used when Serene Seasons is present. */
		public double blizzardChanceSpring = 0.15;
		public double blizzardChanceSummer = 0.0;
		public double blizzardChanceAutumn = 0.30;
		public double blizzardChanceWinter = 0.85;
		/** Used when no seasons mod is installed. */
		public double blizzardChanceNoSeasons = 0.35;
		/** Relative weights of severity 1, 2, 3. */
		public double[] severityWeights = {0.5, 0.35, 0.15};
		/** Forecast warnings are broadcast this many ticks before a front (largest first). */
		public int[] warningLeadTicks = {24_000, 2_400};
		public boolean broadcastWarnings = true;
		/** Extra Thermoo temperature points lost per tick while exposed in a blizzard at full intensity, by severity. */
		public int[] blizzardColdPerTick = {1, 2, 3};
		/** Movement-speed multiplier loss at full intensity while exposed (0.25 = -25%). */
		public double blizzardSlowdown = 0.25;
		public double stormSlowdown = 0.10;
		/** Horizontal distance (blocks) checked upwind for a wall when deciding shelter. */
		public int shelterWindCheckBlocks = 8;
		/** Snow-layer accumulation on exposed natural ground near players during blizzards. */
		public boolean blizzardSnowAccumulation = true;
		public int snowAccumulationMaxLayers = 2;
		public int snowAccumulationRadius = 24;
		public int snowAccumulationAttemptsPerSecond = 6;
		/** Destructive effects. Not implemented beyond these switches; kept false (conservative by design). */
		public boolean stormBreaksBlocks = false;
		public boolean stormDamagesCrops = false;
	}

	public static final class Power {
		public long windTurbineRatedOutput = 12;
		public double windCutInMs = 3.0;
		public double windRatedMs = 12.0;
		public double windCutOutMs = 25.0;
		public long windTurbineBuffer = 4_000;
		public long waterWheelOutputPerFace = 3;
		public long waterWheelBuffer = 2_000;
		/** Max E/t a generator pushes into each adjacent energy storage. */
		public long generatorPushPerTick = 64;
	}

	public static final class Appliances {
		public long refrigeratorDraw = 1;
		public long refrigeratorBuffer = 2_000;
		/** Ticks a refrigerator stays cold after losing power. */
		public int refrigeratorColdRetentionTicks = 6_000;
		public long rangeDraw = 6;
		public long rangeBuffer = 4_000;
		/** Oven speed multiplier relative to the recipe's cooking time. */
		public double ovenSpeed = 2.0;
		/** Ticks the cooktop stays lit after power is lost (residual heat). */
		public int rangeResidualHeatTicks = 100;
		public long heaterDraw = 8;
		public long heaterBuffer = 4_000;
		public int heaterRadius = 7;
		/** Thermoo temperature points added per tick to entities within radius of a running heater. */
		public int heaterWarmthPerTick = 4;
	}

	public static final class Client {
		/** 0..1 — how strongly blizzard exposure shortens fog distance. */
		public double blizzardFogStrength = 1.0;
		public boolean blizzardParticles = true;
		public boolean showForecastToasts = true;
	}

	public static HomesteadConfig get() {
		return instance;
	}

	public static void load() {
		Path path = FabricLoader.getInstance().getConfigDir().resolve(HomesteadCore.MOD_ID + ".json");
		HomesteadConfig cfg = new HomesteadConfig();
		if (Files.exists(path)) {
			try {
				HomesteadConfig read = GSON.fromJson(Files.readString(path), HomesteadConfig.class);
				if (read != null) {
					cfg = read.withDefaults();
				}
			} catch (IOException | JsonParseException e) {
				HomesteadCore.LOG.error("Could not read {}, using defaults: {}", path, e.toString());
			}
		}
		instance = cfg;
		try {
			Files.createDirectories(path.getParent());
			Files.writeString(path, GSON.toJson(cfg));
		} catch (IOException e) {
			HomesteadCore.LOG.warn("Could not write {}: {}", path, e.toString());
		}
	}

	/** Gson leaves absent nested objects null; replace them with defaults. */
	private HomesteadConfig withDefaults() {
		if (weather == null) weather = new Weather();
		if (power == null) power = new Power();
		if (appliances == null) appliances = new Appliances();
		if (client == null) client = new Client();
		if (weather.severityWeights == null || weather.severityWeights.length != 3) weather.severityWeights = new Weather().severityWeights;
		if (weather.blizzardColdPerTick == null || weather.blizzardColdPerTick.length != 3) weather.blizzardColdPerTick = new Weather().blizzardColdPerTick;
		if (weather.warningLeadTicks == null) weather.warningLeadTicks = new Weather().warningLeadTicks;
		if (weather.maxGapTicks < weather.minGapTicks) weather.maxGapTicks = weather.minGapTicks;
		if (weather.maxDurationTicks < weather.minDurationTicks) weather.maxDurationTicks = weather.minDurationTicks;
		return this;
	}
}
