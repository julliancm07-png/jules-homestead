package dev.jules.homestead.client;

import dev.jules.homestead.client.screen.OvenScreen;
import dev.jules.homestead.config.HomesteadConfig;
import dev.jules.homestead.network.WeatherPayload;
import dev.jules.homestead.registry.ModMenus;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;

public final class HomesteadCoreClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(WeatherPayload.TYPE, (payload, ctx) -> ClientWeather.accept(payload));
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> ClientWeather.reset());
		ClientTickEvents.END_CLIENT_TICK.register(HomesteadCoreClient::tick);
		MenuScreens.register(ModMenus.OVEN, OvenScreen::new);
	}

	private static void tick(Minecraft mc) {
		ClientWeather.tick();
		if (mc.level == null || mc.player == null || mc.isPaused() || !HomesteadConfig.get().client.blizzardParticles) {
			return;
		}
		WeatherPayload w = ClientWeather.latest();
		if (w == null || !w.blizzardExposure()) {
			return;
		}
		spawnBlizzardSnow(mc, mc.player, w);
	}

	/** Wind-driven snow around an exposed player; count scales with intensity (max ~12 particles/tick). */
	private static void spawnBlizzardSnow(Minecraft mc, Player p, WeatherPayload w) {
		RandomSource r = p.getRandom();
		int n = Math.round(12 * w.intensity());
		double rad = Math.toRadians(w.windDirection());
		// Wind blows FROM windDirection, so particles travel the opposite way.
		double speed = Math.min(1.2, w.windSpeed() / 20.0);
		double vx = -Math.sin(rad) * speed;
		double vz = Math.cos(rad) * speed;
		for (int i = 0; i < n; i++) {
			double x = p.getX() + (r.nextDouble() - 0.5) * 16 - vx * 6;
			double y = p.getEyeY() + (r.nextDouble() - 0.3) * 6;
			double z = p.getZ() + (r.nextDouble() - 0.5) * 16 - vz * 6;
			mc.level.addParticle(ParticleTypes.SNOWFLAKE, x, y, z, vx, -0.05 - r.nextDouble() * 0.05, vz);
		}
	}
}
