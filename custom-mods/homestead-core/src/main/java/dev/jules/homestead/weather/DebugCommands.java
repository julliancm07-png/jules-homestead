package dev.jules.homestead.weather;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import dev.jules.homestead.HomesteadCore;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * /homestead debug dump — writes registry IDs (items, blocks, biomes, structures, entity types, advancements, recipes,
 * mob effects) to homestead-registry-dump.json in the server directory. Used by tools/check_content.py to validate
 * quest and datapack references against the real modded registries. Op level 2.
 */
public final class DebugCommands {
	private DebugCommands() {
	}

	public static void register(CommandDispatcher<CommandSourceStack> d) {
		d.register(Commands.literal("homestead").then(Commands.literal("debug")
				.requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
				.then(Commands.literal("dump").executes(c -> {
					Path out = dump(c.getSource().getServer());
					c.getSource().sendSuccess(() -> Component.literal("Registry dump written to " + out.toAbsolutePath()), false);
					return 1;
				}))));
	}

	public static Path dump(MinecraftServer server) {
		RegistryAccess ra = server.registryAccess();
		JsonObject root = new JsonObject();
		add(root, "item", ra, Registries.ITEM);
		add(root, "block", ra, Registries.BLOCK);
		add(root, "entity_type", ra, Registries.ENTITY_TYPE);
		add(root, "biome", ra, Registries.BIOME);
		add(root, "structure", ra, Registries.STRUCTURE);
		add(root, "mob_effect", ra, Registries.MOB_EFFECT);
		add(root, "fluid", ra, Registries.FLUID);
		JsonArray adv = new JsonArray();
		server.getAdvancements().getAllAdvancements().stream().map(h -> h.id().toString()).sorted().forEach(adv::add);
		root.add("advancement", adv);
		JsonArray rec = new JsonArray();
		server.overworld().recipeAccess().getRecipes().stream().map(h -> h.id().identifier().toString()).sorted().forEach(rec::add);
		root.add("recipe", rec);
		Path out = server.getServerDirectory().resolve("homestead-registry-dump.json");
		try {
			Files.writeString(out, new GsonBuilder().setPrettyPrinting().create().toJson(root));
		} catch (IOException e) {
			HomesteadCore.LOG.error("dump failed", e);
		}
		return out;
	}

	private static <T> void add(JsonObject root, String name, RegistryAccess ra, ResourceKey<? extends Registry<T>> key) {
		JsonArray arr = new JsonArray();
		ra.lookupOrThrow(key).keySet().stream().map(Object::toString).sorted().forEach(arr::add);
		root.add(name, arr);
	}
}
