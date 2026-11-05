package dev.jules.homestead.weather;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.jules.homestead.HomesteadCore;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.Optional;

/**
 * Persistent weather state, stored once per server on the overworld ({@code data/homesteadcore_weather.dat}).
 * A null DataFixTypes is legal: Fabric API's SavedDataStorageMixin skips datafixing for it.
 */
public final class WeatherSavedData extends SavedData {
	public static final Codec<WeatherSavedData> CODEC = RecordCodecBuilder.create(i -> i.group(
			Front.CODEC.optionalFieldOf("current").forGetter(d -> Optional.ofNullable(d.current)),
			Front.CODEC.optionalFieldOf("next").forGetter(d -> Optional.ofNullable(d.next)),
			Codec.INT.optionalFieldOf("warned_mask", 0).forGetter(d -> d.warnedMask),
			Codec.FLOAT.optionalFieldOf("wind_direction", 270f).forGetter(d -> d.windDirection),
			Codec.FLOAT.optionalFieldOf("wind_base", 4f).forGetter(d -> d.windBase),
			Codec.FLOAT.optionalFieldOf("wind_target", 4f).forGetter(d -> d.windTarget)
	).apply(i, WeatherSavedData::new));

	public static final SavedDataType<WeatherSavedData> TYPE =
			new SavedDataType<>(HomesteadCore.id("weather"), WeatherSavedData::new, CODEC, null);

	Front current;
	Front next;
	int warnedMask;
	float windDirection;
	float windBase;
	float windTarget;

	public WeatherSavedData() {
		this(Optional.empty(), Optional.empty(), 0, 270f, 4f, 4f);
	}

	private WeatherSavedData(Optional<Front> current, Optional<Front> next, int warnedMask, float windDirection, float windBase, float windTarget) {
		this.current = current.orElse(null);
		this.next = next.orElse(null);
		this.warnedMask = warnedMask;
		this.windDirection = windDirection;
		this.windBase = windBase;
		this.windTarget = windTarget;
	}

	public Front current() {
		return current;
	}

	public Front next() {
		return next;
	}

	public float windDirection() {
		return windDirection;
	}

	public float windBase() {
		return windBase;
	}
}
