package dev.jules.homestead.client.mixin;

import dev.jules.homestead.client.ClientWeather;
import dev.jules.homestead.config.HomesteadConfig;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.FogRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Blizzard visibility. Narrow injection at the RETURN of FogRenderer#setupFog: only when the local player is exposed
 * in a blizzard (server-computed), pull the environmental fog in towards ~12 blocks and whiten its colour.
 * Underwater/lava/blindness fog are already decided by vanilla before this point and we only ever shorten distances.
 */
@Mixin(FogRenderer.class)
public abstract class FogRendererMixin {
	@Inject(method = "setupFog", at = @At("RETURN"))
	private void homesteadcore$blizzardFog(Camera camera, int renderDistanceInChunks, DeltaTracker deltaTracker, float darkenWorldAmount,
										   ClientLevel level, CallbackInfoReturnable<FogData> cir) {
		float blend = ClientWeather.fogBlend() * (float) HomesteadConfig.get().client.blizzardFogStrength;
		if (blend <= 0.001f) {
			return;
		}
		FogData fog = cir.getReturnValue();
		float targetStart = 0f;
		float targetEnd = 12f + (1f - blend) * 40f;
		fog.environmentalStart = Math.min(fog.environmentalStart, lerp(blend, fog.environmentalStart, targetStart));
		fog.environmentalEnd = Math.min(fog.environmentalEnd, lerp(blend, fog.environmentalEnd, targetEnd));
		fog.skyEnd = Math.min(fog.skyEnd, lerp(blend, fog.skyEnd, targetEnd));
		fog.cloudEnd = Math.min(fog.cloudEnd, lerp(blend, fog.cloudEnd, targetEnd));
		float w = 0.85f * blend;
		fog.color.set(lerp(w, fog.color.x(), 0.86f), lerp(w, fog.color.y(), 0.89f), lerp(w, fog.color.z(), 0.93f), fog.color.w());
	}

	private static float lerp(float t, float a, float b) {
		return a + (b - a) * t;
	}
}
