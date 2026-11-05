package dev.jules.homestead.weather;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import dev.jules.homestead.block.BarometerBlock;
import dev.jules.homestead.compat.ThermooCompat;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

/** /homestead weather status|forecast|start|stop|schedule — see docs/weather-design.md. */
public final class WeatherCommands {
	private WeatherCommands() {
	}

	public static void register(CommandDispatcher<CommandSourceStack> d) {
		d.register(Commands.literal("homestead")
				.then(Commands.literal("weather")
						.then(Commands.literal("status").executes(WeatherCommands::status))
						.then(Commands.literal("forecast").executes(c -> {
							c.getSource().sendSuccess(() -> BarometerBlock.reading(c.getSource().getServer().overworld()), false);
							return 1;
						}))
						.then(Commands.literal("start").requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
								.then(kind("storm", Front.Kind.STORM))
								.then(kind("blizzard", Front.Kind.BLIZZARD)))
						.then(Commands.literal("stop").requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS)).executes(c -> {
							boolean ok = WeatherManager.stopNow(c.getSource().getServer());
							c.getSource().sendSuccess(() -> Component.translatable(ok ? "homesteadcore.cmd.stopping" : "homesteadcore.cmd.no_front"), true);
							return ok ? 1 : 0;
						}))
						.then(Commands.literal("schedule").requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
								.then(Commands.argument("seconds", IntegerArgumentType.integer(1, 864_000)).executes(c -> {
									MinecraftServer s = c.getSource().getServer();
									WeatherSavedData data = WeatherManager.data(s);
									long now = s.overworld().getGameTime();
									if (data.next == null) {
										WeatherManager.scheduleNext(s, data, now, true);
									}
									data.next = data.next.withStart(now + IntegerArgumentType.getInteger(c, "seconds") * 20L);
									data.warnedMask = 0;
									data.setDirty();
									c.getSource().sendSuccess(() -> WeatherManager.forecastLine(data.next, now), true);
									return 1;
								})))));
	}

	private static com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> kind(String name, Front.Kind kind) {
		return Commands.literal(name)
				.executes(c -> start(c, kind, 300, 2))
				.then(Commands.argument("seconds", IntegerArgumentType.integer(10, 7200))
						.executes(c -> start(c, kind, IntegerArgumentType.getInteger(c, "seconds"), 2))
						.then(Commands.argument("severity", IntegerArgumentType.integer(1, 3))
								.executes(c -> start(c, kind, IntegerArgumentType.getInteger(c, "seconds"), IntegerArgumentType.getInteger(c, "severity")))));
	}

	private static int start(CommandContext<CommandSourceStack> c, Front.Kind kind, int seconds, int severity) {
		Front f = WeatherManager.startNow(c.getSource().getServer(), kind, seconds * 20, severity);
		c.getSource().sendSuccess(() -> Component.translatable("homesteadcore.cmd.started", kind.getSerializedName(), severity, seconds), true);
		return 1;
	}

	private static int status(CommandContext<CommandSourceStack> c) {
		MinecraftServer s = c.getSource().getServer();
		WeatherSavedData d = WeatherManager.data(s);
		long now = s.overworld().getGameTime();
		StringBuilder sb = new StringBuilder();
		if (d.current != null) {
			sb.append(String.format("Front: %s sev %d, intensity %.2f, %s left", d.current.kind().getSerializedName(), d.current.severity(),
					d.current.intensityAt(now), WeatherManager.formatDuration(d.current.endTick() - now)));
		} else {
			sb.append("Front: none");
		}
		sb.append(String.format(" | wind %.1f m/s from %.0f°", WeatherManager.windSpeed(s, now, s.overworld().getSeaLevel() + 8, s.overworld().getSeaLevel()), d.windDirection));
		if (d.next != null) {
			sb.append(String.format(" | next: %s sev %d in %s", d.next.kind().getSerializedName(), d.next.severity(), WeatherManager.formatDuration(d.next.startTick() - now)));
		}
		ServerPlayer p = c.getSource().getPlayer();
		if (p != null) {
			WeatherManager.PlayerWeather st = WeatherManager.playerState(p.getUUID());
			sb.append(String.format(" | you: %s, precipitation %s", st.exposed() ? "EXPOSED" : "sheltered", st.precipitation().getSerializedName()));
			if (FabricLoader.getInstance().isModLoaded("thermoo")) {
				sb.append(String.format(", blizzard cold %d/s", ThermooCompat.blizzardChange(st)));
			}
		}
		String msg = sb.toString();
		c.getSource().sendSuccess(() -> Component.literal(msg), false);
		return 1;
	}
}
