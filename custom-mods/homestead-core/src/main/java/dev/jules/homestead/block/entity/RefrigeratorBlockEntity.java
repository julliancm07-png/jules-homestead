package dev.jules.homestead.block.entity;

import dev.jules.homestead.compat.SpoiledCompat;
import dev.jules.homestead.config.HomesteadConfig;
import dev.jules.homestead.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

/**
 * Refrigerator (27 slots). Powered: 1 E/t, contents never spoil. After power loss the cabinet stays cold for
 * {@code refrigeratorColdRetentionTicks}; then contents spoil at Spoiled's normal rate. Items are never deleted except
 * by Spoiled's own spoil-to-rotten conversion.
 *
 * <p>Deliberately does not implement {@code Container} itself (see {@link SpoiledCompat}).
 */
public class RefrigeratorBlockEntity extends PoweredBlockEntity {
	public static final int SLOTS = 27;
	private final SimpleContainer items = new SimpleContainer(SLOTS) {
		@Override
		public void setChanged() {
			super.setChanged();
			RefrigeratorBlockEntity.this.setChanged();
		}

		@Override
		public boolean stillValid(Player player) {
			return RefrigeratorBlockEntity.this.level != null
					&& RefrigeratorBlockEntity.this.level.getBlockEntity(worldPosition) == RefrigeratorBlockEntity.this
					&& player.distanceToSqr(worldPosition.getCenter()) <= 64.0;
		}
	};
	/** Ticks since the last tick with sufficient power. */
	private int ticksWithoutPower;
	private int spoilSteps;

	public RefrigeratorBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.REFRIGERATOR, pos, state, HomesteadConfig.get().appliances.refrigeratorBuffer, 256, 0);
		ticksWithoutPower = Integer.MAX_VALUE / 2; // a freshly placed fridge starts warm until powered
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, RefrigeratorBlockEntity be) {
		if (!(level instanceof ServerLevel sl)) {
			return;
		}
		HomesteadConfig.Appliances cfg = HomesteadConfig.get().appliances;
		boolean powered = be.draw(cfg.refrigeratorDraw);
		if (powered) {
			be.ticksWithoutPower = 0;
		} else if (be.ticksWithoutPower < Integer.MAX_VALUE / 2) {
			be.ticksWithoutPower++;
		}
		boolean cold = be.isCold();
		if (state.getValue(BlockStateProperties.POWERED) != cold) {
			sl.setBlock(pos, state.setValue(BlockStateProperties.POWERED, cold), 3);
		}
		if (!cold) {
			long rate = SpoiledCompat.spoilRate();
			if (rate > 0 && sl.getGameTime() % rate == 0) {
				if (SpoiledCompat.spoilTick(sl, be.items) > 0) {
					be.spoilSteps++;
				}
			}
		}
	}

	public boolean isCold() {
		return ticksWithoutPower <= HomesteadConfig.get().appliances.refrigeratorColdRetentionTicks;
	}

	public SimpleContainer items() {
		return items;
	}

	public int spoilSteps() {
		return spoilSteps;
	}

	public AbstractContainerMenu createMenu(int id, Inventory inventory) {
		return ChestMenu.threeRows(id, inventory, items);
	}

	public void dropContents(Level level, BlockPos pos) {
		Containers.dropContents(level, pos, items);
	}

	@Override
	public void preRemoveSideEffects(BlockPos pos, BlockState state) {
		if (level != null) {
			dropContents(level, pos);
		}
	}

	@Override
	public Component statusLine() {
		int retention = HomesteadConfig.get().appliances.refrigeratorColdRetentionTicks;
		Component coldState = ticksWithoutPower == 0
				? Component.translatable("homesteadcore.status.fridge.powered")
				: isCold()
				? Component.translatable("homesteadcore.status.fridge.retaining", (retention - ticksWithoutPower) / 20)
				: Component.translatable("homesteadcore.status.fridge.warm");
		return Component.translatable("homesteadcore.status.fridge", coldState, storedEnergy(), energyCapacity());
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		NonNullList<ItemStack> list = NonNullList.withSize(SLOTS, ItemStack.EMPTY);
		ContainerHelper.loadAllItems(input, list);
		for (int i = 0; i < SLOTS; i++) {
			items.setItem(i, list.get(i));
		}
		ticksWithoutPower = input.getIntOr("ticks_without_power", Integer.MAX_VALUE / 2);
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		NonNullList<ItemStack> list = NonNullList.withSize(SLOTS, ItemStack.EMPTY);
		for (int i = 0; i < SLOTS; i++) {
			list.set(i, items.getItem(i));
		}
		ContainerHelper.saveAllItems(output, list);
		output.putInt("ticks_without_power", ticksWithoutPower);
	}
}
