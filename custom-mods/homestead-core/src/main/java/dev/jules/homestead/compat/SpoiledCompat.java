package dev.jules.homestead.compat;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;

/**
 * Narrow adapter to Spoiled (optional). homestead-core refrigerators keep their items in an internal container while
 * the block entity itself does NOT implement {@link Container}, so Spoiled's world tick never touches them. When a
 * fridge has been without power longer than its cold-retention window, it calls {@link #spoilTick} which mirrors
 * Spoiled's own per-container loop (same public helpers, same rate) — food then spoils exactly as in a chest.
 */
public final class SpoiledCompat {
	private static final boolean PRESENT = FabricLoader.getInstance().isModLoaded("spoiled");

	private SpoiledCompat() {
	}

	public static boolean present() {
		return PRESENT;
	}

	/**
	 * Upstream workaround (Spoiled 12.1.0 Fabric): Spoiled fills its static spoil-recipe cache
	 * ({@code Constants.SPOIL_RECIPES}) only from a CLIENT recipe-sync event, so on a dedicated server nothing ever
	 * spoils. We rebuild the same cache from the server's recipe manager on server start and after datapack reloads.
	 * Same contents as the client path, so it is harmless in singleplayer.
	 */
	public static void refreshServerRecipeCache(net.minecraft.server.MinecraftServer server) {
		if (!PRESENT) {
			return;
		}
		try {
			int n = Bridge.refresh(server);
			dev.jules.homestead.HomesteadCore.LOG.info("Spoiled server recipe cache rebuilt: {} spoil recipes", n);
		} catch (LinkageError | RuntimeException e) {
			dev.jules.homestead.HomesteadCore.LOG.warn("Could not rebuild Spoiled recipe cache: {}", e.toString());
		}
	}

	/** Spoiled's tick period in ticks (config "spoilRate"), or -1 when Spoiled is absent. */
	public static long spoilRate() {
		if (!PRESENT) {
			return -1;
		}
		try {
			return Bridge.spoilRate();
		} catch (LinkageError | RuntimeException e) {
			return -1;
		}
	}

	/** Advance spoilage of every item in {@code container} by one Spoiled step. Returns the number of stacks updated. */
	public static int spoilTick(ServerLevel level, Container container) {
		if (!PRESENT) {
			return 0;
		}
		try {
			return Bridge.spoilTick(level, container);
		} catch (LinkageError | RuntimeException e) {
			return 0;
		}
	}

	static final class Bridge {
		static int refresh(net.minecraft.server.MinecraftServer server) {
			var map = net.minecraft.world.item.crafting.RecipeMap.create(server.overworld().recipeAccess().getRecipes());
			var recipes = map.byType(com.mrbysco.spoiled.registration.SpoiledRecipes.SPOIL_RECIPE_TYPE.get());
			synchronized (com.mrbysco.spoiled.Constants.SPOIL_RECIPES) {
				com.mrbysco.spoiled.Constants.SPOIL_RECIPES.clear();
				com.mrbysco.spoiled.Constants.SPOIL_RECIPES.addAll(recipes);
			}
			return recipes.size();
		}

		static long spoilRate() {
			return Math.max(1, com.mrbysco.spoiled.config.SpoiledConfigCache.spoilRate);
		}

		static int spoilTick(ServerLevel level, Container container) {
			int updated = 0;
			for (int slot = 0; slot < container.getContainerSize(); slot++) {
				var stack = container.getItem(slot);
				if (stack == null || stack.isEmpty()) {
					continue;
				}
				var holder = com.mrbysco.spoiled.util.SpoilHelper.getSpoilRecipe(level, stack);
				if (holder == null) {
					continue;
				}
				var recipe = holder.value();
				com.mrbysco.spoiled.util.SpoilHelper.updateSpoilingStack(stack, recipe);
				updated++;
				if (com.mrbysco.spoiled.util.SpoilHelper.isSpoiled(stack)) {
					com.mrbysco.spoiled.handler.SpoilHandler.spoilItemInContainer(container, slot, stack, recipe, level.registryAccess());
				}
			}
			if (updated > 0) {
				container.setChanged();
			}
			return updated;
		}
	}
}
