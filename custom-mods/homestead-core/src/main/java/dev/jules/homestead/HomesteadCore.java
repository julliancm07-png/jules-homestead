package dev.jules.homestead;

import dev.jules.homestead.block.entity.SpaceHeaterBlockEntity;
import dev.jules.homestead.compat.SpoiledCompat;
import dev.jules.homestead.config.HomesteadConfig;
import dev.jules.homestead.network.WeatherPayload;
import dev.jules.homestead.registry.ModBlockEntities;
import dev.jules.homestead.registry.ModBlocks;
import dev.jules.homestead.registry.ModMenus;
import dev.jules.homestead.weather.WeatherCommands;
import dev.jules.homestead.weather.WeatherManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class HomesteadCore implements ModInitializer {
	public static final String MOD_ID = "homesteadcore";
	public static final Logger LOG = LoggerFactory.getLogger(MOD_ID);

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}

	@Override
	public void onInitialize() {
		HomesteadConfig.load();
		ModBlocks.init();
		ModBlockEntities.init();
		ModMenus.init();
		PayloadTypeRegistry.clientboundPlay().register(WeatherPayload.TYPE, WeatherPayload.CODEC);

		ServerTickEvents.END_SERVER_TICK.register(WeatherManager::tick);
		ServerLifecycleEvents.SERVER_STOPPED.register(s -> SpaceHeaterBlockEntity.clearAll());
		ServerLifecycleEvents.SERVER_STARTED.register(SpoiledCompat::refreshServerRecipeCache);
		ServerLifecycleEvents.END_DATA_PACK_RELOAD.register((server, rm, success) -> {
			if (success) {
				SpoiledCompat.refreshServerRecipeCache(server);
			}
		});
		ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> WeatherManager.forget(handler.getPlayer().getUUID()));
		CommandRegistrationCallback.EVENT.register((dispatcher, ctx, selection) -> WeatherCommands.register(dispatcher));
		CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register(out -> {
			for (Item item : ModBlocks.ITEMS) {
				out.accept(new ItemStack(item), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
			}
		});

		if (FabricLoader.getInstance().isModLoaded("thermoo")) {
			dev.jules.homestead.compat.ThermooCompat.init();
		} else {
			LOG.info("Thermoo not present: blizzard cold and heater warmth disabled (slowdown, fog and snow still active)");
		}
		LOG.info("Homestead Core initialised");
	}
}
