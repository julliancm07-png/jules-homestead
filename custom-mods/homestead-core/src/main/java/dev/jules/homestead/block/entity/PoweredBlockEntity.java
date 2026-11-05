package dev.jules.homestead.block.entity;

import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.EnergyStorageUtil;
import team.reborn.energy.api.base.SimpleEnergyStorage;

/**
 * Common base for homestead-core machines: a Team Reborn Energy buffer exposed on every side (so cables can enter
 * from behind walls or below floors), persisted as "energy".
 */
public abstract class PoweredBlockEntity extends BlockEntity {
	protected final SimpleEnergyStorage energy;

	protected PoweredBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, long capacity, long maxInsert, long maxExtract) {
		super(type, pos, state);
		this.energy = new SimpleEnergyStorage(capacity, maxInsert, maxExtract) {
			@Override
			protected void onFinalCommit() {
				setChanged();
			}
		};
	}

	public EnergyStorage energyStorage(Direction side) {
		return energy;
	}

	public long storedEnergy() {
		return energy.amount;
	}

	public long energyCapacity() {
		return energy.capacity;
	}

	/** Directly set the stored energy (clamped). Used by GameTests and admin tooling; normal play goes through the API. */
	public void setStoredEnergy(long amount) {
		energy.amount = Math.max(0, Math.min(energy.capacity, amount));
		setChanged();
	}

	/** Consume exactly {@code amount} if available. Returns true when the full amount was drawn. */
	protected boolean draw(long amount) {
		if (amount <= 0) {
			return true;
		}
		if (energy.amount >= amount) {
			energy.amount -= amount;
			setChanged();
			return true;
		}
		return false;
	}

	protected void generate(long amount) {
		if (amount > 0) {
			energy.amount = Math.min(energy.capacity, energy.amount + amount);
			setChanged();
		}
	}

	/** Push stored energy into adjacent TR Energy storages (generators only). */
	protected void pushToNeighbors(Level level, BlockPos pos, long maxPerSide) {
		if (energy.amount <= 0) {
			return;
		}
		for (Direction dir : Direction.values()) {
			EnergyStorage target = EnergyStorage.SIDED.find(level, pos.relative(dir), dir.getOpposite());
			if (target == null || !target.supportsInsertion()) {
				continue;
			}
			try (Transaction tx = Transaction.openOuter()) {
				EnergyStorageUtil.move(energy, target, maxPerSide, tx);
				tx.commit();
			}
			if (energy.amount <= 0) {
				return;
			}
		}
	}

	/** Shown when a player sneak-uses the block with an empty hand. */
	public abstract Component statusLine();

	@Override
	protected void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		energy.amount = Math.max(0, Math.min(energy.capacity, input.getLongOr("energy", 0L)));
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		output.putLong("energy", energy.amount);
	}
}
