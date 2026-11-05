package dev.jules.homestead.menu;

import dev.jules.homestead.block.entity.ElectricRangeBlockEntity;
import dev.jules.homestead.registry.ModMenus;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/** Oven menu: 4 input slots (top row), 4 output slots (bottom row), player inventory. */
public class OvenMenu extends AbstractContainerMenu {
	public static final int SLOTS = ElectricRangeBlockEntity.OVEN_SLOTS * 2;
	private final Container container;
	private final ContainerData data;

	/** Client constructor. */
	public OvenMenu(int id, Inventory inventory) {
		this(id, inventory, new SimpleContainer(SLOTS), new SimpleContainerData(ElectricRangeBlockEntity.DATA_COUNT));
	}

	public OvenMenu(int id, Inventory inventory, Container container, ContainerData data) {
		super(ModMenus.OVEN, id);
		checkContainerSize(container, SLOTS);
		this.container = container;
		this.data = data;
		int n = ElectricRangeBlockEntity.OVEN_SLOTS;
		for (int i = 0; i < n; i++) {
			addSlot(new Slot(container, i, 53 + i * 18, 20));
		}
		for (int i = 0; i < n; i++) {
			addSlot(new Slot(container, n + i, 53 + i * 18, 52) {
				@Override
				public boolean mayPlace(ItemStack stack) {
					return false;
				}
			});
		}
		addStandardInventorySlots(inventory, 8, 84);
		addDataSlots(data);
	}

	public long energy() {
		return Integer.toUnsignedLong(data.get(0));
	}

	public long capacity() {
		return Math.max(1, Integer.toUnsignedLong(data.get(1)));
	}

	public float progress(int slot) {
		int total = data.get(2 + ElectricRangeBlockEntity.OVEN_SLOTS + slot);
		return total <= 0 ? 0f : Math.min(1f, data.get(2 + slot) / (float) total);
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		Slot slot = slots.get(index);
		if (!slot.hasItem()) {
			return ItemStack.EMPTY;
		}
		ItemStack stack = slot.getItem();
		ItemStack copy = stack.copy();
		int n = ElectricRangeBlockEntity.OVEN_SLOTS;
		if (index < SLOTS) {
			if (!moveItemStackTo(stack, SLOTS, slots.size(), true)) {
				return ItemStack.EMPTY;
			}
		} else if (!moveItemStackTo(stack, 0, n, false)) {
			return ItemStack.EMPTY;
		}
		if (stack.isEmpty()) {
			slot.setByPlayer(ItemStack.EMPTY);
		} else {
			slot.setChanged();
		}
		return copy;
	}

	@Override
	public boolean stillValid(Player player) {
		return container.stillValid(player);
	}
}
