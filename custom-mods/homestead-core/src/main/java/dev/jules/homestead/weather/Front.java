package dev.jules.homestead.weather;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.StringRepresentable;

/**
 * A severe weather front. Times are server game-time ticks. {@code forecastSeverity} is what barometers report
 * (may differ from the true severity by one level).
 */
public record Front(Kind kind, long startTick, long endTick, int severity, int forecastSeverity, float windDirection) {
	public static final Codec<Front> CODEC = RecordCodecBuilder.create(i -> i.group(
			Kind.CODEC.fieldOf("kind").forGetter(Front::kind),
			Codec.LONG.fieldOf("start").forGetter(Front::startTick),
			Codec.LONG.fieldOf("end").forGetter(Front::endTick),
			Codec.intRange(1, 3).fieldOf("severity").forGetter(Front::severity),
			Codec.intRange(1, 3).fieldOf("forecast_severity").forGetter(Front::forecastSeverity),
			Codec.FLOAT.fieldOf("wind_direction").forGetter(Front::windDirection)
	).apply(i, Front::new));

	public enum Kind implements StringRepresentable {
		STORM("storm"),
		BLIZZARD("blizzard");

		public static final Codec<Kind> CODEC = StringRepresentable.fromEnum(Kind::values);
		private final String name;

		Kind(String name) {
			this.name = name;
		}

		@Override
		public String getSerializedName() {
			return name;
		}
	}

	public long duration() {
		return Math.max(1, endTick - startTick);
	}

	public boolean isActiveAt(long gameTime) {
		return gameTime >= startTick && gameTime < endTick;
	}

	/** Envelope 0..1: linear ramp-up over the first 15%, plateau, ramp-down over the last 20%. */
	public float envelopeAt(long gameTime) {
		if (!isActiveAt(gameTime)) {
			return 0f;
		}
		double p = (gameTime - startTick) / (double) duration();
		double v = p < 0.15 ? p / 0.15 : p > 0.80 ? (1.0 - p) / 0.20 : 1.0;
		return (float) Math.max(0.0, Math.min(1.0, v));
	}

	/** Severity scaling applied to the envelope: severity 1 = 0.6, 2 = 0.8, 3 = 1.0. */
	public float intensityAt(long gameTime) {
		return envelopeAt(gameTime) * (0.4f + 0.2f * severity);
	}

	public Front withEnd(long newEnd) {
		return new Front(kind, startTick, newEnd, severity, forecastSeverity, windDirection);
	}

	public Front withStart(long newStart) {
		long d = duration();
		return new Front(kind, newStart, newStart + d, severity, forecastSeverity, windDirection);
	}
}
