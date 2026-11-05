package dev.jules.homestead.weather;

import dev.jules.homestead.HomesteadCore;
import dev.jules.homestead.compat.SeasonCompat;
import dev.jules.homestead.config.HomesteadConfig;
import dev.jules.homestead.network.WeatherPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SnowLayerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.levelgen.Heightmap;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Server-side owner of severe fronts. All state lives in {@link WeatherSavedData}; this class is stateless glue. */
public final class WeatherManager {
	public static final Identifier SLOWDOWN_ID = HomesteadCore.id("weather_slowdown");
	public static final TagKey<Block> NO_SNOW_ACCUMULATION = TagKey.create(Registries.BLOCK, HomesteadCore.id("no_snow_accumulation"));

	/** Per-player exposure snapshot, refreshed every 10 ticks; read by the Thermoo integration and the client sync. */
	public record PlayerWeather(boolean exposed, Biome.Precipitation precipitation, float intensity, int severity, Front.Kind kind) {
		public static final PlayerWeather NONE = new PlayerWeather(false, Biome.Precipitation.NONE, 0f, 0, null);

		public boolean blizzardExposure() {
			return exposed && intensity > 0 && precipitation == Biome.Precipitation.SNOW;
		}

		public boolean stormExposure() {
			return exposed && intensity > 0 && precipitation == Biome.Precipitation.RAIN;
		}
	}

	private static final Map<UUID, PlayerWeather> PLAYER_STATE = new HashMap<>();

	private WeatherManager() {
	}

	public static WeatherSavedData data(MinecraftServer server) {
		return server.overworld().getDataStorage().computeIfAbsent(WeatherSavedData.TYPE);
	}

	public static PlayerWeather playerState(UUID id) {
		return PLAYER_STATE.getOrDefault(id, PlayerWeather.NONE);
	}

	// ------------------------------------------------------------------ server tick

	public static void tick(MinecraftServer server) {
		ServerLevel level = server.overworld();
		long now = level.getGameTime();
		WeatherSavedData d = data(server);
		HomesteadConfig.Weather cfg = HomesteadConfig.get().weather;

		if (now % 200 == 0) {
			updateWind(d, level.getRandom());
		}
		if (!cfg.enableSevereWeather) {
			if (d.current != null) {
				endFront(server, d, true);
			}
			clearAllPlayers(server);
			return;
		}
		boolean weatherAdvances = level.getGameRules().get(GameRules.ADVANCE_WEATHER);

		if (d.current != null && now >= d.current.endTick()) {
			endFront(server, d, false);
		}
		if (d.next == null && weatherAdvances) {
			scheduleNext(server, d, now, false);
		}
		if (d.current == null && d.next != null && now >= d.next.startTick()) {
			if (weatherAdvances) {
				startFront(server, d, d.next);
			} else {
				d.next = d.next.withStart(now + 1200); // frozen weather cycle: keep postponing
				d.setDirty();
			}
		}
		if (d.current == null && d.next != null && cfg.broadcastWarnings) {
			issueWarnings(server, d, now);
		}
		if (d.current != null && now % 100 == 0) {
			int remaining = (int) Math.max(20, d.current.endTick() - now);
			server.setWeatherParameters(0, remaining, true, true);
		}
		if (now % 10 == 0) {
			tickPlayers(server, level, d, now);
		}
		if (d.current != null && now % 20 == 0 && cfg.blizzardSnowAccumulation) {
			for (ServerPlayer p : level.players()) {
				accumulateSnow(level, p, d.current.intensityAt(now));
			}
		}
	}

	// ------------------------------------------------------------------ scheduling

	public static Front scheduleNext(MinecraftServer server, WeatherSavedData d, long now, boolean firstAllowedSoon) {
		HomesteadConfig.Weather cfg = HomesteadConfig.get().weather;
		RandomSource r = server.overworld().getRandom();
		long gap = cfg.minGapTicks + (long) (r.nextDouble() * (cfg.maxGapTicks - cfg.minGapTicks));
		if (!firstAllowedSoon && now < cfg.firstFrontDelayTicks) {
			gap = Math.max(gap, cfg.firstFrontDelayTicks - now);
		}
		long start = now + gap;
		Front.Kind kind = r.nextDouble() < blizzardChance(server.overworld(), cfg) ? Front.Kind.BLIZZARD : Front.Kind.STORM;
		d.next = randomFront(r, cfg, kind, start, -1);
		d.warnedMask = 0;
		d.setDirty();
		return d.next;
	}

	static double blizzardChance(ServerLevel level, HomesteadConfig.Weather cfg) {
		return switch (SeasonCompat.season(level)) {
			case SPRING -> cfg.blizzardChanceSpring;
			case SUMMER -> cfg.blizzardChanceSummer;
			case AUTUMN -> cfg.blizzardChanceAutumn;
			case WINTER -> cfg.blizzardChanceWinter;
			case NONE -> cfg.blizzardChanceNoSeasons;
		};
	}

	static Front randomFront(RandomSource r, HomesteadConfig.Weather cfg, Front.Kind kind, long start, int forcedSeverity) {
		long dur = cfg.minDurationTicks + (long) (r.nextDouble() * (cfg.maxDurationTicks - cfg.minDurationTicks));
		int sev = forcedSeverity > 0 ? forcedSeverity : pickSeverity(r, cfg.severityWeights);
		int forecast = sev;
		if (r.nextDouble() < 0.4) {
			forecast = Math.max(1, Math.min(3, sev + (r.nextBoolean() ? 1 : -1)));
		}
		return new Front(kind, start, start + dur, sev, forecast, r.nextFloat() * 360f);
	}

	static int pickSeverity(RandomSource r, double[] w) {
		double total = w[0] + w[1] + w[2];
		double x = r.nextDouble() * total;
		return x < w[0] ? 1 : x < w[0] + w[1] ? 2 : 3;
	}

	// ------------------------------------------------------------------ start / stop (also used by commands)

	public static Front startNow(MinecraftServer server, Front.Kind kind, int durationTicks, int severity) {
		WeatherSavedData d = data(server);
		long now = server.overworld().getGameTime();
		Front f = randomFront(server.overworld().getRandom(), HomesteadConfig.get().weather, kind, now, severity);
		f = new Front(kind, now, now + durationTicks, f.severity(), f.severity(), f.windDirection());
		if (d.current != null) {
			d.current = null;
		}
		startFront(server, d, f);
		return f;
	}

	public static boolean stopNow(MinecraftServer server) {
		WeatherSavedData d = data(server);
		if (d.current == null) {
			return false;
		}
		long now = server.overworld().getGameTime();
		// Ramp down quickly rather than snapping off: end ~30 s from now, compressing the envelope tail.
		long end = Math.min(d.current.endTick(), now + 600);
		Front shortened = new Front(d.current.kind(), now - (long) ((end - now) * 4.0), end, d.current.severity(), d.current.forecastSeverity(), d.current.windDirection());
		d.current = shortened;
		d.setDirty();
		return true;
	}

	static void startFront(MinecraftServer server, WeatherSavedData d, Front f) {
		d.current = f;
		if (d.next != null && d.next.startTick() <= f.endTick()) {
			d.next = null;
		}
		d.windDirection = f.windDirection();
		d.setDirty();
		server.setWeatherParameters(0, (int) f.duration(), true, true);
		broadcast(server, Component.translatable("homesteadcore.weather.started." + f.kind().getSerializedName(),
				severityName(f.severity())).withStyle(ChatFormatting.GOLD));
		HomesteadCore.LOG.info("Severe front started: {} severity {} for {} ticks", f.kind(), f.severity(), f.duration());
	}

	static void endFront(MinecraftServer server, WeatherSavedData d, boolean silent) {
		Front f = d.current;
		d.current = null;
		d.setDirty();
		RandomSource r = server.overworld().getRandom();
		server.setWeatherParameters(6_000 + r.nextInt(12_000), 0, false, false);
		clearAllPlayers(server);
		if (!silent && f != null) {
			broadcast(server, Component.translatable("homesteadcore.weather.ended." + f.kind().getSerializedName()).withStyle(ChatFormatting.GRAY));
		}
	}

	private static void issueWarnings(MinecraftServer server, WeatherSavedData d, long now) {
		int[] leads = HomesteadConfig.get().weather.warningLeadTicks;
		for (int i = 0; i < leads.length && i < 30; i++) {
			int bit = 1 << i;
			long remaining = d.next.startTick() - now;
			if ((d.warnedMask & bit) == 0 && remaining <= leads[i] && remaining > 0) {
				d.warnedMask |= bit;
				d.setDirty();
				// Only the most imminent applicable warning is shown if several thresholds pass at once.
				if (i + 1 < leads.length && remaining <= leads[i + 1]) {
					continue;
				}
				broadcast(server, forecastLine(d.next, now).copy().withStyle(ChatFormatting.YELLOW));
			}
		}
	}

	// ------------------------------------------------------------------ wind

	static void updateWind(WeatherSavedData d, RandomSource r) {
		if (Math.abs(d.windBase - d.windTarget) < 0.3f) {
			d.windTarget = 2f + r.nextFloat() * 6f;
		}
		d.windBase += Math.signum(d.windTarget - d.windBase) * Math.min(0.4f, Math.abs(d.windTarget - d.windBase));
		if (d.current == null) {
			d.windDirection = (d.windDirection + (r.nextFloat() - 0.5f) * 20f + 360f) % 360f;
		}
		d.setDirty();
	}

	/** Wind speed in m/s at a world height. Same formula on every call within a tick (deterministic gusts). */
	public static double windSpeed(MinecraftServer server, long now, int y, int seaLevel) {
		WeatherSavedData d = data(server);
		double v = d.windBase;
		if (d.current != null) {
			float i = d.current.intensityAt(now);
			double gust = Math.sin(now / 37.0) * 0.6 + Math.sin(now / 11.0) * 0.4;
			v += i * (8.0 + 6.0 * d.current.severity()) + gust * 3.0 * i;
		}
		double heightFactor = Math.pow(Math.max(1.0, y - seaLevel + 16) / 16.0, 0.14);
		heightFactor = Math.max(0.6, Math.min(1.25, heightFactor));
		return Math.max(0.0, v * heightFactor);
	}

	// ------------------------------------------------------------------ players

	private static void tickPlayers(MinecraftServer server, ServerLevel overworld, WeatherSavedData d, long now) {
		HomesteadConfig.Weather cfg = HomesteadConfig.get().weather;
		for (ServerPlayer p : server.getPlayerList().getPlayers()) {
			PlayerWeather state = PlayerWeather.NONE;
			if (d.current != null && p.level() == overworld && !p.isSpectator()) {
				float intensity = d.current.intensityAt(now);
				Biome.Precipitation precip = Exposure.precipitationAt(overworld, p.blockPosition());
				boolean exposed = intensity > 0 && precip != Biome.Precipitation.NONE
						&& Exposure.isExposed(overworld, p, d.windDirection, cfg.shelterWindCheckBlocks);
				state = new PlayerWeather(exposed, precip, intensity, d.current.severity(), d.current.kind());
			}
			PLAYER_STATE.put(p.getUUID(), state);
			applySlowdown(p, state, cfg);
			awardWeatherAdvancements(server, p, state);
			if (ServerPlayNetworking.canSend(p, WeatherPayload.TYPE)) {
				ServerPlayNetworking.send(p, WeatherPayload.of(d, state, windSpeed(server, now, p.getBlockY(), overworld.getSeaLevel())));
			}
		}
	}

	/** Ticks (in 10-tick steps) each player has spent sheltered during an active blizzard. */
	private static final Map<UUID, Integer> SHELTERED_STEPS = new HashMap<>();

	/**
	 * Quest hooks: grants homesteadcore:weather/* advancements (criterion "award", trigger minecraft:impossible).
	 * witness_front — experienced any front with precipitation; caught_in_blizzard — exposed in a blizzard;
	 * safe_and_warm — 60 s sheltered while a blizzard is active at your position.
	 */
	private static void awardWeatherAdvancements(MinecraftServer server, ServerPlayer p, PlayerWeather s) {
		if (s.intensity() <= 0 || s.precipitation() == Biome.Precipitation.NONE) {
			SHELTERED_STEPS.remove(p.getUUID());
			return;
		}
		award(server, p, "weather/witness_front");
		if (s.precipitation() == Biome.Precipitation.SNOW) {
			if (s.exposed()) {
				award(server, p, "weather/caught_in_blizzard");
				SHELTERED_STEPS.remove(p.getUUID());
			} else {
				int steps = SHELTERED_STEPS.merge(p.getUUID(), 1, Integer::sum);
				if (steps >= 120) {
					award(server, p, "weather/safe_and_warm");
				}
			}
		}
	}

	private static void award(MinecraftServer server, ServerPlayer p, String path) {
		var holder = server.getAdvancements().get(HomesteadCore.id(path));
		if (holder != null && !p.getAdvancements().getOrStartProgress(holder).isDone()) {
			p.getAdvancements().award(holder, "award");
		}
	}

	private static void applySlowdown(ServerPlayer p, PlayerWeather s, HomesteadConfig.Weather cfg) {
		AttributeInstance speed = p.getAttribute(Attributes.MOVEMENT_SPEED);
		if (speed == null) {
			return;
		}
		double amount = 0;
		if (!p.isPassenger() && !p.isCreative() && !p.isSpectator()) {
			if (s.blizzardExposure()) {
				amount = -cfg.blizzardSlowdown * s.intensity();
			} else if (s.stormExposure()) {
				amount = -cfg.stormSlowdown * s.intensity();
			}
		}
		if (amount < -0.001) {
			speed.addOrUpdateTransientModifier(new AttributeModifier(SLOWDOWN_ID, amount, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
		} else {
			speed.removeModifier(SLOWDOWN_ID);
		}
	}

	private static void clearAllPlayers(MinecraftServer server) {
		for (ServerPlayer p : server.getPlayerList().getPlayers()) {
			PLAYER_STATE.put(p.getUUID(), PlayerWeather.NONE);
			AttributeInstance speed = p.getAttribute(Attributes.MOVEMENT_SPEED);
			if (speed != null) {
				speed.removeModifier(SLOWDOWN_ID);
			}
			if (ServerPlayNetworking.canSend(p, WeatherPayload.TYPE)) {
				ServerPlayNetworking.send(p, WeatherPayload.of(data(server), PlayerWeather.NONE, 0));
			}
		}
	}

	public static void forget(UUID id) {
		PLAYER_STATE.remove(id);
		SHELTERED_STEPS.remove(id);
	}

	// ------------------------------------------------------------------ snow accumulation (only world change)

	static void accumulateSnow(ServerLevel level, ServerPlayer p, float intensity) {
		HomesteadConfig.Weather cfg = HomesteadConfig.get().weather;
		if (intensity <= 0.2f || p.level() != level) {
			return;
		}
		RandomSource r = level.getRandom();
		int radius = cfg.snowAccumulationRadius;
		int attempts = Math.round(cfg.snowAccumulationAttemptsPerSecond * intensity);
		for (int i = 0; i < attempts; i++) {
			int x = p.getBlockX() + r.nextInt(radius * 2 + 1) - radius;
			int z = p.getBlockZ() + r.nextInt(radius * 2 + 1) - radius;
			if (!level.hasChunk(x >> 4, z >> 4)) {
				continue;
			}
			BlockPos pos = new BlockPos(x, level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z), z);
			tryAddSnowLayer(level, pos, cfg.snowAccumulationMaxLayers);
		}
	}

	/** Adds one snow layer at {@code pos} if it is snowing there and the spot is natural, open ground. */
	public static boolean tryAddSnowLayer(ServerLevel level, BlockPos pos, int maxLayers) {
		if (Exposure.precipitationAt(level, pos) != Biome.Precipitation.SNOW || !Exposure.openToSky(level, pos)) {
			return false;
		}
		BlockState at = level.getBlockState(pos);
		BlockState below = level.getBlockState(pos.below());
		if (below.is(NO_SNOW_ACCUMULATION) || at.is(NO_SNOW_ACCUMULATION)) {
			return false;
		}
		if (at.is(Blocks.SNOW)) {
			int layers = at.getValue(SnowLayerBlock.LAYERS);
			if (layers < maxLayers) {
				level.setBlockAndUpdate(pos, at.setValue(SnowLayerBlock.LAYERS, layers + 1));
				return true;
			}
			return false;
		}
		BlockState snow = Blocks.SNOW.defaultBlockState();
		if (at.isAir() && snow.canSurvive(level, pos)) {
			level.setBlockAndUpdate(pos, snow);
			return true;
		}
		return false;
	}

	// ------------------------------------------------------------------ text

	public static Component severityName(int sev) {
		return Component.translatable("homesteadcore.weather.severity." + Math.max(1, Math.min(3, sev)));
	}

	public static Component forecastLine(Front f, long now) {
		long ticks = Math.max(0, f.startTick() - now);
		return Component.translatable("homesteadcore.weather.forecast." + f.kind().getSerializedName(),
				severityName(f.forecastSeverity()), formatDuration(ticks));
	}

	public static String formatDuration(long ticks) {
		if (ticks >= 24_000) {
			return String.format("%.1f days", ticks / 24_000.0);
		}
		long minutes = Math.round(ticks / 1200.0);
		return minutes <= 1 ? "about a minute" : minutes + " minutes";
	}

	private static void broadcast(MinecraftServer server, Component msg) {
		server.getPlayerList().broadcastSystemMessage(msg, false);
	}
}
