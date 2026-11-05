package dev.jules.homestead.client;

import dev.jules.homestead.network.WeatherPayload;

/** Latest server weather snapshot for the local player, smoothed for rendering. */
public final class ClientWeather {
	private static WeatherPayload latest;
	private static float fogBlend;

	private ClientWeather() {
	}

	public static void accept(WeatherPayload p) {
		latest = p;
	}

	public static WeatherPayload latest() {
		return latest;
	}

	public static void reset() {
		latest = null;
		fogBlend = 0f;
	}

	/** Called every client tick: eases the blizzard fog in/out over ~2 seconds so shelter transitions are smooth. */
	public static void tick() {
		float target = latest != null && latest.blizzardExposure() ? latest.intensity() : 0f;
		fogBlend += (target - fogBlend) * 0.05f;
		if (Math.abs(target - fogBlend) < 0.002f) {
			fogBlend = target;
		}
	}

	public static float fogBlend() {
		return fogBlend;
	}
}
