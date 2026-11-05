package dev.jules.homestead.mixin;

import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Upstream bug fix for Serene Seasons 26.1.2.0.7 (Fabric). Its client mixin MixinBiomeClient#onHasPrecipitation
 * replaces Biome#hasPrecipitation with SeasonHooks#hasPrecipitationSeasonal, which ends by calling
 * Biome#hasPrecipitation again — infinite recursion (StackOverflowError) in any client JVM once a level is loaded,
 * i.e. every singleplayer world. Reproduced by tools/client_smoke_test.py (integrated server crash on join).
 *
 * The intended value of that final call is the biome's own (vanilla) precipitation flag, so we redirect exactly that
 * one call site to read ClimateSettings directly. Dedicated servers were never affected (the mixin is client-only).
 * Applied only when Serene Seasons is present (CompatMixinPlugin).
 */
@Mixin(targets = "sereneseasons.season.SeasonHooks", remap = false)
public abstract class SereneSeasonsRecursionFixMixin {
	@Redirect(method = "hasPrecipitationSeasonal",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/biome/Biome;hasPrecipitation()Z"))
	private static boolean homesteadcore$vanillaPrecipitation(Biome biome) {
		return biome.climateSettings.hasPrecipitation();
	}
}
