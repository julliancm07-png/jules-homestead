package dev.jules.homestead.block.entity;

import dev.jules.homestead.config.HomesteadConfig;
import dev.jules.homestead.registry.ModBlockEntities;
import dev.jules.homestead.weather.WeatherManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

/**
 * Wind turbine. Output (documented in docs/power-plan.md):
 * 0 below cut-in or at/above cut-out (feathered); rated × (v³ − vin³)/(vr³ − vin³) between cut-in and rated; rated
 * above rated speed. Multiplied by an obstruction factor (share of air in a 5×3×5 box around the rotor) and requires
 * open sky. Wind speed v comes from {@link WeatherManager#windSpeed}, which includes the height factor.
 */
public class WindTurbineBlockEntity extends PoweredBlockEntity {
	private double remainder;
	private double currentOutput;
	private double lastWind;
	private float obstruction = -1f;

	public WindTurbineBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.WIND_TURBINE, pos, state, HomesteadConfig.get().power.windTurbineBuffer, 0, Long.MAX_VALUE);
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, WindTurbineBlockEntity be) {
		if (!(level instanceof ServerLevel sl)) {
			return;
		}
		HomesteadConfig.Power cfg = HomesteadConfig.get().power;
		long now = sl.getGameTime();
		if (be.obstruction < 0 || (now + pos.asLong()) % 200 == 0) {
			be.obstruction = obstructionFactor(sl, pos);
		}
		if (now % 20 == 0) {
			boolean windy = sl.dimensionType().hasSkyLight() && !sl.dimensionType().hasCeiling() && sl.canSeeSky(pos.above());
			be.lastWind = windy ? WeatherManager.windSpeed(sl.getServer(), now, pos.getY(), sl.getSeaLevel()) : 0.0;
			be.currentOutput = powerFor(be.lastWind, cfg) * be.obstruction;
			boolean active = be.currentOutput > 0;
			if (state.hasProperty(BlockStateProperties.POWERED) && state.getValue(BlockStateProperties.POWERED) != active) {
				sl.setBlock(pos, state.setValue(BlockStateProperties.POWERED, active), 3);
			}
		}
		be.remainder += be.currentOutput;
		long whole = (long) be.remainder;
		be.remainder -= whole;
		be.generate(whole);
		be.pushToNeighbors(sl, pos, cfg.generatorPushPerTick);
	}

	public static double powerFor(double v, HomesteadConfig.Power cfg) {
		if (v < cfg.windCutInMs || v >= cfg.windCutOutMs) {
			return 0.0;
		}
		if (v >= cfg.windRatedMs) {
			return cfg.windTurbineRatedOutput;
		}
		double in3 = Math.pow(cfg.windCutInMs, 3);
		double frac = (Math.pow(v, 3) - in3) / (Math.pow(cfg.windRatedMs, 3) - in3);
		return cfg.windTurbineRatedOutput * Math.max(0.0, Math.min(1.0, frac));
	}

	static float obstructionFactor(ServerLevel level, BlockPos pos) {
		int air = 0;
		int total = 0;
		for (int dx = -2; dx <= 2; dx++) {
			for (int dy = 0; dy <= 2; dy++) {
				for (int dz = -2; dz <= 2; dz++) {
					if (dx == 0 && dz == 0 && dy == 0) {
						continue;
					}
					total++;
					if (level.getBlockState(pos.offset(dx, dy, dz)).isAir()) {
						air++;
					}
				}
			}
		}
		return total == 0 ? 0f : air / (float) total;
	}

	public double currentOutput() {
		return currentOutput;
	}

	@Override
	public Component statusLine() {
		return Component.translatable("homesteadcore.status.wind_turbine",
				String.format("%.1f", lastWind), String.format("%.1f", currentOutput), Math.round(obstruction * 100), storedEnergy(), energyCapacity());
	}
}
