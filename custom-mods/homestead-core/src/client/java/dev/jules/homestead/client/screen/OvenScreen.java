package dev.jules.homestead.client.screen;

import dev.jules.homestead.menu.OvenMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

/** Oven GUI drawn with plain fills (no texture asset needed): slots, per-slot progress bars and an energy gauge. */
public class OvenScreen extends AbstractContainerScreen<OvenMenu> {
	private static final int PANEL = 0xFFC6C6C6;
	private static final int DARK = 0xFF373737;
	private static final int LIGHT = 0xFFFFFFFF;
	private static final int SLOT = 0xFF8B8B8B;

	public OvenScreen(OvenMenu menu, Inventory inventory, Component title) {
		super(menu, inventory, title, 176, 166);
	}

	@Override
	public void extractBackground(GuiGraphicsExtractor g, int mouseX, int mouseY, float a) {
		super.extractBackground(g, mouseX, mouseY, a);
		int x = leftPos;
		int y = topPos;
		g.fill(x, y, x + imageWidth, y + imageHeight, PANEL);
		g.outline(x, y, imageWidth, imageHeight, DARK);
		for (Slot s : menu.slots) {
			int sx = x + s.x - 1;
			int sy = y + s.y - 1;
			g.fill(sx, sy, sx + 18, sy + 18, SLOT);
			g.fill(sx, sy, sx + 17, sy + 1, DARK);
			g.fill(sx, sy, sx + 1, sy + 17, DARK);
			g.fill(sx + 1, sy + 17, sx + 18, sy + 18, LIGHT);
			g.fill(sx + 17, sy + 1, sx + 18, sy + 18, LIGHT);
		}
		for (int i = 0; i < 4; i++) {
			int bx = x + 53 + i * 18;
			int by = y + 40;
			g.fill(bx, by, bx + 16, by + 6, DARK);
			int w = Math.round(16 * menu.progress(i));
			g.fill(bx, by, bx + w, by + 6, 0xFFE08A2E);
		}
		int ex = x + 160;
		int ey = y + 18;
		int eh = 52;
		g.fill(ex, ey, ex + 8, ey + eh, DARK);
		int filled = (int) Math.round(eh * (menu.energy() / (double) menu.capacity()));
		g.fill(ex + 1, ey + eh - filled, ex + 7, ey + eh, 0xFFD9C230);
	}

	@Override
	public void extractContents(GuiGraphicsExtractor g, int mouseX, int mouseY, float a) {
		super.extractContents(g, mouseX, mouseY, a);
		int ex = leftPos + 160;
		int ey = topPos + 18;
		if (mouseX >= ex && mouseX < ex + 8 && mouseY >= ey && mouseY < ey + 52) {
			g.setTooltipForNextFrame(font, Component.translatable("homesteadcore.gui.energy", menu.energy(), menu.capacity()), mouseX, mouseY);
		}
	}
}
