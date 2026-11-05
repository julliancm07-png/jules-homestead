package dev.jules.homestead.mixin;

import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.EnvironmentAttributeLayer;
import net.minecraft.world.attribute.EnvironmentAttributeSystem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Upstream bug fix for Immersive Storms 2.0.0+26.1.x-beta.1..beta.3 (bundled in Scorchful 0.18.3): bright daytime sky
 * and horizon fog at night.
 *
 * Immersive Storms adds one positional layer per attribute at the TAIL of EnvironmentAttributeSystem#addDefaultLayers,
 * i.e. after the timeline layers that darken the sky at night. For spatially interpolated attributes (sky colour, fog
 * colour, ...) that layer returns {@code biomeWeights.applyAttributeLayer(attribute, result)} — a copy of vanilla's biome
 * layer — so the biome's daytime "set" value overwrites the night multiplier. Verified in the cloud client: at time
 * 18000 the sky stayed bright blue (clouds and terrain dark) with and without Dramatic Skys.
 *
 * Its own client-side probe already feeds the clear-weather biome values into vanilla's biome layer (getClearWeatherMap)
 * and applies rain/thunder values in EnvironmentAttributeProbe#getValue, so that branch adds nothing but the clobbering.
 * We wrap only layers created by Immersive Storms classes and skip exactly that branch; the server-side branch (no
 * interpolator: clear/rain/thunder entries per biome) is untouched. Applied only when Immersive Storms is loaded.
 */
@Mixin(EnvironmentAttributeSystem.Builder.class)
public abstract class ImmersiveStormsNightSkyFixMixin {
	private static final String STORMS_PACKAGE = "com.thedeathlycow.immersive.storms.";

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Inject(method = "addPositionalLayer", at = @At("HEAD"), cancellable = true)
	private void homesteadcore$skipStormBiomeReapply(EnvironmentAttribute attribute, EnvironmentAttributeLayer.Positional layer,
			CallbackInfoReturnable<EnvironmentAttributeSystem.Builder> cir) {
		if (layer == null || !layer.getClass().getName().startsWith(STORMS_PACKAGE)) {
			return;
		}
		EnvironmentAttributeLayer.Positional wrapped = (baseValue, pos, biomeInterpolator) ->
				biomeInterpolator != null && attribute.isSpatiallyInterpolated()
						? baseValue
						: layer.applyPositional(baseValue, pos, biomeInterpolator);
		// the wrapper is our class, so this call passes straight through
		cir.setReturnValue(((EnvironmentAttributeSystem.Builder) (Object) this).addPositionalLayer(attribute, wrapped));
	}
}
