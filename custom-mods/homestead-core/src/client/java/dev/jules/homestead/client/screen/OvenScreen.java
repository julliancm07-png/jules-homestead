package dev.jules.homestead.client.screen;

import dev.jules.homestead.menu.OvenMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

/**
 * Oven GUI drawn with plain fills (no texture asset needed). The appliance area uses the pack's walnut + cream palette
 * (matching the quest book, Jade theme and Recolourful Containers); the player inventory below keeps the vanilla look.
 */
public class OvenScreen extends AbstractContainerScreen<OvenMenu> {
	private static final int PANEL = 0xFFC6C6C6;
	private static final int BEVEL_LIGHT = 0xFFFFFFFF;
	private static final int BEVEL_DARK = 0xFF555555;
	private static final int DARK = 0xFF373737;
	private static final int LIGHT = 0xFFFFFFFF;
	private static final int SLOT = 0xFF8B8B8B;
	// appliance area
	private static final int WALNUT = 0xFF5B3A1E;
	private static final int WALNUT_LIGHT = 0xFF8A5E36;
	private static final int ENAMEL = 0xFFF1E7D0;
	private static final int ENAMEL_SLOT = 0xFFC9B99A;
	private static final int ENAMEL_SHADOW = 0xFF6E5B43;
	private static final int ENAMEL_LIGHT = 0xFFFFF8E8;
	private static final int TRACK = 0xFF4A3524;
	private static final int HEAT = 0xFFE08A2E;
	private static final int HEAT_GLOW = 0xFFF2B35E;
	private static final int CHARGE = 0xFFD9C230;
	private static final int AREA_TOP = 14;
	private static final int AREA_BOTTOM = 72;

	public OvenScreen(OvenMenu menu, Inventory inventory, Component title) {
		super(menu, inventory, title, 176, 166);
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor g, int mouseX, int mouseY, float a) {
		super.extractBackground(g, mouseX, mouseY, a);
		int x = leftPos;
		int y = topPos;
		// vanilla-style panel with a bevel
		g.fill(x, y, x + imageWidth, y + imageHeight, PANEL);
		g.outline(x, y, imageWidth, imageHeight, DARK);
		g.fill(x + 1, y + 1, x + imageWidth - 2, y + 2, BEVEL_LIGHT);
		g.fill(x + 1, y + 1, x + 2, y + imageHeight - 2, BEVEL_LIGHT);
		g.fill(x + 2, y + imageHeight - 2, x + imageWidth - 1, y + imageHeight - 1, BEVEL_DARK);
		g.fill(x + imageWidth - 2, y + 2, x + imageWidth - 1, y + imageHeight - 1, BEVEL_DARK);
		// appliance area: walnut frame around cream enamel
		int ax0 = x + 7;
		int ax1 = x + imageWidth - 7;
		int ay0 = y + AREA_TOP;
		int ay1 = y + AREA_BOTTOM;
		g.fill(ax0, ay0, ax1, ay1, WALNUT);
		g.fill(ax0 + 1, ay0 + 1, ax1 - 1, ay0 + 2, WALNUT_LIGHT);
		g.fill(ax0 + 2, ay0 + 2, ax1 - 2, ay1 - 2, ENAMEL);
		for (Slot s : menu.slots) {
			int sx = x + s.x - 1;
			int sy = y + s.y - 1;
			boolean appliance = s.y < AREA_BOTTOM;
			g.fill(sx, sy, sx + 18, sy + 18, appliance ? ENAMEL_SLOT : SLOT);
			g.fill(sx, sy, sx + 17, sy + 1, appliance ? ENAMEL_SHADOW : DARK);
			g.fill(sx, sy, sx + 1, sy + 17, appliance ? ENAMEL_SHADOW : DARK);
			g.fill(sx + 1, sy + 17, sx + 18, sy + 18, appliance ? ENAMEL_LIGHT : LIGHT);
			g.fill(sx + 17, sy + 1, sx + 18, sy + 18, appliance ? ENAMEL_LIGHT : LIGHT);
		}
		// per-slot cooking progress: a glowing element under each input
		for (int i = 0; i < 4; i++) {
			int bx = x + 53 + i * 18;
			int by = y + 40;
			g.fill(bx, by, bx + 16, by + 6, TRACK);
			int w = Math.round(16 * menu.progress(i));
			if (w > 0) {
				g.fill(bx, by, bx + w, by + 6, HEAT);
				g.fill(bx, by, bx + w, by + 1, HEAT_GLOW);
			}
		}
		// energy gauge
		int ex = x + 158;
		int ey = y + 18;
		int eh = 50;
		g.fill(ex, ey, ex + 8, ey + eh, TRACK);
		int filled = (int) Math.round((eh - 2) * (menu.energy() / (double) menu.capacity()));
		g.fill(ex + 1, ey + eh - 1 - filled, ex + 7, ey + eh - 1, CHARGE);
	}

	@Override
	public void extractContents(GuiGraphicsExtractor g, int mouseX, int mouseY, float a) {
		super.extractContents(g, mouseX, mouseY, a);
		int ex = leftPos + 158;
		int ey = topPos + 18;
		if (mouseX >= ex && mouseX < ex + 8 && mouseY >= ey && mouseY < ey + 50) {
			g.setTooltipForNextFrame(font, Component.translatable("homesteadcore.gui.energy", menu.energy(), menu.capacity()), mouseX, mouseY);
		}
	}
}
