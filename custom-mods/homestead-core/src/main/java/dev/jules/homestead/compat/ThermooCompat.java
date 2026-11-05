package dev.jules.homestead.compat;

import com.github.thedeathlycow.thermoo.api.core.v2.TemperatureAware;
import com.github.thedeathlycow.thermoo.api.core.v2.event.LivingEntityTemperatureTickEvents;
import com.github.thedeathlycow.thermoo.api.core.v2.registry.ThermooRegistries;
import com.github.thedeathlycow.thermoo.api.core.v2.source.TemperatureSource;
import dev.jules.homestead.HomesteadCore;
import dev.jules.homestead.block.entity.SpaceHeaterBlockEntity;
import dev.jules.homestead.config.HomesteadConfig;
import dev.jules.homestead.weather.WeatherManager;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

/**
 * Thermoo integration. Loaded only when Thermoo is present (see HomesteadCore). Two pack-defined temperature sources
 * (data/homesteadcore/thermoo/temperature_source/*.json, tick_interval 20) carry our effects so that Thermoo applies
 * its normal resistance reductions (e.g. Frostiful fur armour) to blizzard cold.
 */
public final class ThermooCompat {
	public static final ResourceKey<TemperatureSource> BLIZZARD = ResourceKey.create(ThermooRegistries.TEMPERATURE_SOURCE, HomesteadCore.id("blizzard"));
	public static final ResourceKey<TemperatureSource> SPACE_HEATER = ResourceKey.create(ThermooRegistries.TEMPERATURE_SOURCE, HomesteadCore.id("space_heater"));
	/** Must match tick_interval in the data files. */
	public static final int INTERVAL = 20;

	private ThermooCompat() {
	}

	public static void init() {
		var blizzard = LivingEntityTemperatureTickEvents.getTemperatureChange(BLIZZARD);
		blizzard.register(blizzard.defaultPhaseId(), ctx -> {
			LivingEntity e = ctx.affected();
			if (!(e instanceof Player p) || p.isCreative() || p.isSpectator()) {
				return 0;
			}
			return blizzardChange(WeatherManager.playerState(p.getUUID()));
		});
		var heater = LivingEntityTemperatureTickEvents.getTemperatureChange(SPACE_HEATER);
		heater.register(heater.defaultPhaseId(), ctx -> {
			LivingEntity e = ctx.affected();
			int temp = ((TemperatureAware) e).thermoo$getTemperature();
			if (temp >= 0) {
				return 0; // heaters only bring you back to neutral; they never cause overheating
			}
			if (!SpaceHeaterBlockEntity.isWarmedByHeater(ctx.level(), e.position())) {
				return 0;
			}
			int warmth = HomesteadConfig.get().appliances.heaterWarmthPerTick * INTERVAL;
			return Math.min(warmth, -temp);
		});
		HomesteadCore.LOG.info("Thermoo integration active (blizzard exposure, space heater warmth)");
	}

	/** Temperature change for one interval: negative while exposed in a blizzard, scaled by intensity and severity. */
	public static int blizzardChange(WeatherManager.PlayerWeather s) {
		if (!s.blizzardExposure()) {
			return 0;
		}
		int[] perTick = HomesteadConfig.get().weather.blizzardColdPerTick;
		int base = perTick[Math.max(1, Math.min(3, s.severity())) - 1];
		return -Math.round(base * INTERVAL * s.intensity());
	}
}
