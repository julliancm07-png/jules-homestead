package dev.jules.homestead.client;

import dev.jules.homestead.HomesteadCore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;

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
 * screen inventory|close | look &lt;yaw&gt; &lt;pitch&gt; | use (right-click the block under the crosshair) |
 * probe &lt;dx&gt; &lt;dy&gt; &lt;dz&gt; (log the CLIENT's block state relative to the player) | pack on|off &lt;pack id&gt; |
 * quit | # comment
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
			case "screen" -> {
				if ("inventory".equals(arg) && mc.player != null) {
					mc.setScreen(new InventoryScreen(mc.player));
				} else if (mc.player != null && mc.player.containerMenu != mc.player.inventoryMenu) {
					mc.player.closeContainer(); // also tells the server the container menu closed
				} else {
					mc.setScreen(null);
				}
			}
			case "use" -> {
				if (mc.player != null && mc.gameMode != null && mc.hitResult instanceof BlockHitResult hit) {
					HomesteadCore.LOG.warn("[dev] use {}: {}", hit.getBlockPos(), mc.gameMode.useItemOn(mc.player, InteractionHand.MAIN_HAND, hit));
				} else {
					HomesteadCore.LOG.warn("[dev] use: no block under the crosshair");
				}
			}
			case "look" -> {
				if (mc.player != null) {
					String[] r = arg.split("\\s+");
					mc.player.setYRot(Float.parseFloat(r[0]));
					mc.player.setXRot(Float.parseFloat(r[1]));
				}
			}
			case "probe" -> {
				if (mc.player != null && mc.level != null) {
					String[] r = arg.split("\\s+");
					BlockPos pos = mc.player.blockPosition().offset(Integer.parseInt(r[0]), Integer.parseInt(r[1]), Integer.parseInt(r[2]));
					HomesteadCore.LOG.warn("[dev] probe {} (client): {}", pos, mc.level.getBlockState(pos));
				}
			}
			case "pack" -> {
				String[] r = arg.split("\\s+", 2);
				var repo = mc.getResourcePackRepository();
				boolean changed = "off".equals(r[0]) ? repo.removePack(r[1]) : repo.addPack(r[1]);
				HomesteadCore.LOG.warn("[dev] pack {} {}: changed={}", r[0], r[1], changed);
				if (changed) {
					mc.options.updateResourcePacks(repo);
				}
			}
			case "quit" -> mc.stop();
			default -> HomesteadCore.LOG.warn("[dev] unknown showcase step: {}", step);
		}
		HomesteadCore.LOG.warn("[dev] step done: {}", step);
		STEPS.pollFirst();
	}
}
