package dev.jules.homestead.registry;

import dev.jules.homestead.HomesteadCore;
import dev.jules.homestead.menu.OvenMenu;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;

public final class ModMenus {
	public static final MenuType<OvenMenu> OVEN = Registry.register(BuiltInRegistries.MENU, HomesteadCore.id("oven"), new MenuType<>(OvenMenu::new, FeatureFlags.VANILLA_SET));

	private ModMenus() {
	}

	public static void init() {
	}
}
