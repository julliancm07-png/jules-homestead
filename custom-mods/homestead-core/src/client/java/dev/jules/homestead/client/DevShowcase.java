package dev.jules.homestead.client;

import dev.jules.homestead.HomesteadCore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.server.IntegratedServer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Development-only automation used by tools/client_smoke_test.py to capture screenshots / the title panorama in a
 * headless (Xvfb) client. Inert unless the JVM is started with -Dhomesteadcore.dev.script=&lt;file&gt;.
 *
 * Script lines: waitworld | wait N | server &lt;command&gt; | screenshot &lt;name&gt; | panorama | hud on|off |
 * screen inventory|close | look &lt;yaw&gt; &lt;pitch&gt; | quit | # comment
 */
public final class DevShowcase {
	private static final Deque<String> STEPS = new ArrayDeque<>();
	private static int waitTicks;
	private static boolean active;

	private DevShowcase() {
	}

	public static void init() {
		String file = System.getProperty("homesteadcore.dev.script");
		if (file == null) {
			return;
		}
		try {
			for (String line : Files.readAllLines(Path.of(file))) {
				line = line.strip();
				if (!line.isEmpty() && !line.startsWith("#")) {
					STEPS.add(line);
				}
			}
			active = true;
			HomesteadCore.LOG.warn("[dev] showcase script loaded: {} steps from {}", STEPS.size(), file);
		} catch (IOException e) {
			HomesteadCore.LOG.error("[dev] cannot read showcase script {}", file, e);
		}
	}

	public static void tick(Minecraft mc) {
		if (!active || STEPS.isEmpty()) {
			return;
		}
		if (waitTicks > 0) {
			waitTicks--;
			return;
		}
		String step = STEPS.peekFirst();
		String[] p = step.split("\\s+", 2);
		String arg = p.length > 1 ? p[1] : "";
		switch (p[0]) {
			case "waitworld" -> {
				if (mc.level == null || mc.player == null) {
					return; // keep waiting, step stays queued
				}
			}
			case "wait" -> waitTicks = Integer.parseInt(arg);
			case "server" -> {
				IntegratedServer server = mc.getSingleplayerServer();
				if (server != null) {
					server.execute(() -> server.getCommands().performPrefixedCommand(server.createCommandSourceStack(), arg));
				}
			}
			case "screenshot" -> Screenshot.grab(mc.gameDirectory, arg + ".png", mc.getMainRenderTarget(), 1,
					c -> HomesteadCore.LOG.warn("[dev] screenshot {}: {}", arg, c.getString()));
			case "panorama" -> HomesteadCore.LOG.warn("[dev] panorama: {}", mc.grabPanoramixScreenshot(mc.gameDirectory).getString());
			case "hud" -> mc.options.hideGui = !"on".equals(arg);
			case "screen" -> mc.setScreen("inventory".equals(arg) && mc.player != null ? new InventoryScreen(mc.player) : null);
			case "look" -> {
				if (mc.player != null) {
					String[] r = arg.split("\\s+");
					mc.player.setYRot(Float.parseFloat(r[0]));
					mc.player.setXRot(Float.parseFloat(r[1]));
				}
			}
			case "quit" -> mc.stop();
			default -> HomesteadCore.LOG.warn("[dev] unknown showcase step: {}", step);
		}
		HomesteadCore.LOG.warn("[dev] step done: {}", step);
		STEPS.pollFirst();
	}
}
