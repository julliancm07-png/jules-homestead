package dev.jules.homestead.block.entity;

import dev.jules.homestead.config.HomesteadConfig;
import dev.jules.homestead.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;

/**
 * Water wheel: {@code waterWheelOutputPerFace} E/t for each horizontally adjacent block of FLOWING water (non-source
 * with a non-zero flow vector). Still water and ice give nothing, so freezing winters reduce hydro output.
 */
public class WaterWheelBlockEntity extends PoweredBlockEntity {
	private int flowingFaces;

	public WaterWheelBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.WATER_WHEEL, pos, state, HomesteadConfig.get().power.waterWheelBuffer, 0, Long.MAX_VALUE);
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, WaterWheelBlockEntity be) {
		if (!(level instanceof ServerLevel sl)) {
			return;
		}
		HomesteadConfig.Power cfg = HomesteadConfig.get().power;
		if (sl.getGameTime() % 20 == 0) {
			be.flowingFaces = countFlowingFaces(sl, pos);
			boolean active = be.flowingFaces > 0;
			if (state.hasProperty(BlockStateProperties.POWERED) && state.getValue(BlockStateProperties.POWERED) != active) {
				sl.setBlock(pos, state.setValue(BlockStateProperties.POWERED, active), 3);
			}
		}
		be.generate(be.flowingFaces * cfg.waterWheelOutputPerFace);
		be.pushToNeighbors(sl, pos, cfg.generatorPushPerTick);
	}

	public static int countFlowingFaces(Level level, BlockPos pos) {
		int n = 0;
		for (Direction d : Direction.Plane.HORIZONTAL) {
			BlockPos p = pos.relative(d);
			FluidState fs = level.getFluidState(p);
			if (fs.is(FluidTags.WATER) && !fs.isSource() && fs.getFlow(level, p).lengthSqr() > 1.0e-6) {
				n++;
			}
		}
		return n;
	}

	public int flowingFaces() {
		return flowingFaces;
	}

	@Override
	public Component statusLine() {
		return Component.translatable("homesteadcore.status.water_wheel", flowingFaces,
				flowingFaces * HomesteadConfig.get().power.waterWheelOutputPerFace, storedEnergy(), energyCapacity());
	}
}
