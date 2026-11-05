package dev.jules.homestead.block.entity;

import dev.jules.homestead.config.HomesteadConfig;
import dev.jules.homestead.menu.OvenMenu;
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
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmokingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.Optional;

/**
 * Electric range: a cooktop and a 4-slot oven.
 * <ul>
 *   <li>Cooktop: while powered and something stands on it, the block is {@code LIT} — it is in the
 *   {@code farmersdelight:heat_sources} tag, and Farmer's Delight only treats LIT heat sources as heated. After power
 *   loss it stays lit for {@code rangeResidualHeatTicks} (residual heat).</li>
 *   <li>Oven: cooks {@code minecraft:smoking} recipes (food) at {@code ovenSpeed}× while powered. Unpowered, progress
 *   PAUSES (never regresses) and nothing is lost.</li>
 * </ul>
 * Draws {@code rangeDraw} E/t whenever the cooktop is heating or any oven slot is cooking.
 */
public class ElectricRangeBlockEntity extends PoweredBlockEntity {
	public static final int OVEN_SLOTS = 4;
	public static final int DATA_COUNT = 2 + OVEN_SLOTS * 2;
	private final SimpleContainer items = new SimpleContainer(OVEN_SLOTS * 2) {
		@Override
		public void setChanged() {
			super.setChanged();
			ElectricRangeBlockEntity.this.setChanged();
		}

		@Override
		public boolean canPlaceItem(int slot, ItemStack stack) {
			return slot < OVEN_SLOTS;
		}

		@Override
		public boolean stillValid(Player player) {
			return level != null && level.getBlockEntity(worldPosition) == ElectricRangeBlockEntity.this
					&& player.distanceToSqr(worldPosition.getCenter()) <= 64.0;
		}
	};
	private final double[] progress = new double[OVEN_SLOTS];
	private final int[] total = new int[OVEN_SLOTS];
	private int residualHeat;
	private boolean poweredLastTick;

	private final ContainerData data = new ContainerData() {
		@Override
		public int get(int i) {
			return switch (i) {
				case 0 -> (int) Math.min(Integer.MAX_VALUE, storedEnergy());
				case 1 -> (int) Math.min(Integer.MAX_VALUE, energyCapacity());
				default -> i < 2 + OVEN_SLOTS ? (int) progress[i - 2] : total[i - 2 - OVEN_SLOTS];
			};
		}

		@Override
		public void set(int i, int value) {
		}

		@Override
		public int getCount() {
			return DATA_COUNT;
		}
	};

	public ElectricRangeBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.ELECTRIC_RANGE, pos, state, HomesteadConfig.get().appliances.rangeBuffer, 256, 0);
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, ElectricRangeBlockEntity be) {
		if (!(level instanceof ServerLevel sl)) {
			return;
		}
		HomesteadConfig.Appliances cfg = HomesteadConfig.get().appliances;
		boolean cookwareAbove = !sl.getBlockState(pos.above()).isAir();
		boolean[] cookable = new boolean[OVEN_SLOTS];
		boolean ovenWork = false;
		for (int i = 0; i < OVEN_SLOTS; i++) {
			cookable[i] = be.recipeFor(sl, i).isPresent();
			ovenWork |= cookable[i];
		}
		boolean wantsPower = cookwareAbove || ovenWork;
		boolean powered = wantsPower && be.draw(cfg.rangeDraw);

		if (powered && ovenWork) {
			for (int i = 0; i < OVEN_SLOTS; i++) {
				if (cookable[i]) {
					be.advance(sl, i, cfg.ovenSpeed);
				} else {
					be.progress[i] = 0;
					be.total[i] = 0;
				}
			}
		}
		// Unpowered: progress[] untouched (paused).

		boolean lit;
		if (powered && cookwareAbove) {
			be.residualHeat = cfg.rangeResidualHeatTicks;
			lit = true;
		} else if (cookwareAbove && be.residualHeat > 0) {
			be.residualHeat--;
			lit = true;
		} else {
			be.residualHeat = 0;
			lit = false;
		}
		be.poweredLastTick = powered;
		if (state.getValue(BlockStateProperties.LIT) != lit) {
			sl.setBlock(pos, state.setValue(BlockStateProperties.LIT, lit), 3);
		}
	}

	private Optional<RecipeHolder<SmokingRecipe>> recipeFor(ServerLevel level, int slot) {
		ItemStack in = items.getItem(slot);
		if (in.isEmpty()) {
			return Optional.empty();
		}
		Optional<RecipeHolder<SmokingRecipe>> r = level.recipeAccess().getRecipeFor(RecipeType.SMOKING, new SingleRecipeInput(in), level);
		if (r.isEmpty()) {
			return r;
		}
		ItemStack result = r.get().value().assemble(new SingleRecipeInput(in));
		ItemStack out = items.getItem(slot + OVEN_SLOTS);
		boolean fits = out.isEmpty() || (ItemStack.isSameItemSameComponents(out, result) && out.getCount() + result.getCount() <= out.getMaxStackSize());
		return fits ? r : Optional.empty();
	}

	private void advance(ServerLevel level, int slot, double speed) {
		RecipeHolder<SmokingRecipe> r = recipeFor(level, slot).orElse(null);
		if (r == null) {
			return;
		}
		total[slot] = Math.max(1, r.value().cookingTime());
		progress[slot] += speed;
		if (progress[slot] >= total[slot]) {
			ItemStack in = items.getItem(slot);
			ItemStack result = r.value().assemble(new SingleRecipeInput(in));
			ItemStack out = items.getItem(slot + OVEN_SLOTS);
			if (out.isEmpty()) {
				items.setItem(slot + OVEN_SLOTS, result.copy());
			} else {
				out.grow(result.getCount());
			}
			in.shrink(1);
			progress[slot] = 0;
			items.setChanged();
		}
	}

	public SimpleContainer items() {
		return items;
	}

	public double progress(int slot) {
		return progress[slot];
	}

	public boolean poweredLastTick() {
		return poweredLastTick;
	}

	public AbstractContainerMenu createMenu(int id, Inventory inventory) {
		return new OvenMenu(id, inventory, items, data);
	}

	@Override
	public void preRemoveSideEffects(BlockPos pos, BlockState state) {
		if (level != null) {
			Containers.dropContents(level, pos, items);
		}
	}

	@Override
	public Component statusLine() {
		return Component.translatable("homesteadcore.status.range",
				Component.translatable(poweredLastTick ? "homesteadcore.status.on" : "homesteadcore.status.off"), storedEnergy(), energyCapacity());
	}

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		NonNullList<ItemStack> list = NonNullList.withSize(OVEN_SLOTS * 2, ItemStack.EMPTY);
		ContainerHelper.loadAllItems(input, list);
		for (int i = 0; i < list.size(); i++) {
			items.setItem(i, list.get(i));
		}
		for (int i = 0; i < OVEN_SLOTS; i++) {
			progress[i] = input.getDoubleOr("progress_" + i, 0.0);
		}
		residualHeat = input.getIntOr("residual_heat", 0);
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		NonNullList<ItemStack> list = NonNullList.withSize(OVEN_SLOTS * 2, ItemStack.EMPTY);
		for (int i = 0; i < list.size(); i++) {
			list.set(i, items.getItem(i));
		}
		ContainerHelper.saveAllItems(output, list);
		for (int i = 0; i < OVEN_SLOTS; i++) {
			output.putDouble("progress_" + i, progress[i]);
		}
		output.putInt("residual_heat", residualHeat);
	}
}
