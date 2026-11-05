package dev.jules.homestead.compat;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.level.Level;

/**
 * Season lookup. Serene Seasons is optional: its classes are only touched from {@link SereneSeasonsBridge}, which is
 * never loaded unless the mod is present.
 */
public final class SeasonCompat {
	public enum Season { SPRING, SUMMER, AUTUMN, WINTER, NONE }

	private static final boolean SERENE = FabricLoader.getInstance().isModLoaded("sereneseasons");

	private SeasonCompat() {
	}

	public static boolean hasSeasons() {
		return SERENE;
	}

	public static Season season(Level level) {
		if (!SERENE) {
			return Season.NONE;
		}
		try {
			return SereneSeasonsBridge.season(level);
		} catch (LinkageError | RuntimeException e) {
			return Season.NONE;
		}
	}

	static final class SereneSeasonsBridge {
		static Season season(Level level) {
			return switch (sereneseasons.api.season.SeasonHelper.getSeasonState(level).getSeason()) {
				case SPRING -> Season.SPRING;
				case SUMMER -> Season.SUMMER;
				case AUTUMN -> Season.AUTUMN;
				case WINTER -> Season.WINTER;
			};
		}
	}
}
