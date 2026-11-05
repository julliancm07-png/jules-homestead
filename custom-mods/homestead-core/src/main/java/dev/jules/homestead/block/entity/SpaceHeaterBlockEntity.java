package dev.jules.homestead.block.entity;

import dev.jules.homestead.config.HomesteadConfig;
import dev.jules.homestead.registry.ModBlockEntities;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;

/**
 * Space heater: runs (8 E/t) while it has a redstone signal, or while a player is within its radius. While running it
 * is LIT (light 15 → Frostiful's warmth-from-light) and adds Thermoo warmth to entities within the radius (see
 * ThermooCompat). Active heaters are tracked per dimension for the cheap radius lookup.
 */
public class SpaceHeaterBlockEntity extends PoweredBlockEntity {
	private static final Map<ResourceKey<Level>, LongSet> ACTIVE = new HashMap<>();
	private boolean running;
	private boolean demand;

	public SpaceHeaterBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.SPACE_HEATER, pos, state, HomesteadConfig.get().appliances.heaterBuffer, 256, 0);
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, SpaceHeaterBlockEntity be) {
		if (!(level instanceof ServerLevel sl)) {
			return;
		}
		HomesteadConfig.Appliances cfg = HomesteadConfig.get().appliances;
		if (sl.getGameTime() % 20 == 0) {
			int r = cfg.heaterRadius;
			be.demand = sl.hasNeighborSignal(pos) || !sl.getEntitiesOfClass(net.minecraft.world.entity.player.Player.class, new AABB(pos).inflate(r), p -> !p.isSpectator()).isEmpty();
		}
		boolean demand = be.demand;
		boolean run = demand && be.draw(cfg.heaterDraw);
		if (run != be.running) {
			be.running = run;
			LongSet set = ACTIVE.computeIfAbsent(sl.dimension(), k -> new LongOpenHashSet());
			if (run) {
				set.add(pos.asLong());
			} else {
				set.remove(pos.asLong());
			}
		}
		if (state.getValue(BlockStateProperties.LIT) != run) {
			sl.setBlock(pos, state.setValue(BlockStateProperties.LIT, run), 3);
		}
	}

	/** True when {@code pos} is within the radius of a running heater in this level. */
	public static boolean isWarmedByHeater(ServerLevel level, Vec3 pos) {
		LongSet set = ACTIVE.get(level.dimension());
		if (set == null || set.isEmpty()) {
			return false;
		}
		int r = HomesteadConfig.get().appliances.heaterRadius;
		double r2 = (r + 0.5) * (r + 0.5);
		for (long l : set) {
			BlockPos p = BlockPos.of(l);
			if (p.getCenter().distanceToSqr(pos) <= r2) {
				return true;
			}
		}
		return false;
	}

	public static void clearAll() {
		ACTIVE.clear();
	}

	@Override
	public void setRemoved() {
		super.setRemoved();
		if (level != null) {
			LongSet set = ACTIVE.get(level.dimension());
			if (set != null) {
				set.remove(worldPosition.asLong());
			}
		}
	}

	public boolean running() {
		return running;
	}

	@Override
	public Component statusLine() {
		return Component.translatable("homesteadcore.status.heater",
				Component.translatable(running ? "homesteadcore.status.on" : "homesteadcore.status.off"), storedEnergy(), energyCapacity());
	}
}
