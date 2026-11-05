package dev.jules.homestead.block;

import dev.jules.homestead.block.entity.ElectricRangeBlockEntity;
import dev.jules.homestead.block.entity.PoweredBlockEntity;
import dev.jules.homestead.block.entity.RefrigeratorBlockEntity;
import dev.jules.homestead.block.entity.SpaceHeaterBlockEntity;
import dev.jules.homestead.block.entity.WaterWheelBlockEntity;
import dev.jules.homestead.block.entity.WindTurbineBlockEntity;
import dev.jules.homestead.registry.ModBlockEntities;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.Nullable;

/**
 * One block class for all homestead-core machines, parameterised by {@link Kind}. Horizontal facing + a boolean
 * state (POWERED for generators/fridge, LIT for range/heater). Sneak-use with an empty hand prints the machine's
 * status (energy, output, cold state); plain use opens the fridge/oven menu.
 */
public class MachineBlock extends BaseEntityBlock {
	public enum Kind { WIND_TURBINE, WATER_WHEEL, REFRIGERATOR, ELECTRIC_RANGE, SPACE_HEATER }

	private final Kind kind;

	public MachineBlock(Kind kind, Properties properties) {
		super(properties);
		this.kind = kind;
		registerDefaultState(stateDefinition.any()
				.setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)
				.setValue(flag(kind), false));
	}

	public static BooleanProperty flag(Kind kind) {
		return switch (kind) {
			case ELECTRIC_RANGE, SPACE_HEATER -> BlockStateProperties.LIT;
			default -> BlockStateProperties.POWERED;
		};
	}

	public Kind kind() {
		return kind;
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		// Not data-driven; the codec is only used for block-type serialisation.
		return simpleCodec(p -> new MachineBlock(kind, p));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.POWERED, BlockStateProperties.LIT);
	}

	@Override
	public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
		return defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, ctx.getHorizontalDirection().getOpposite());
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return switch (kind) {
			case WIND_TURBINE -> new WindTurbineBlockEntity(pos, state);
			case WATER_WHEEL -> new WaterWheelBlockEntity(pos, state);
			case REFRIGERATOR -> new RefrigeratorBlockEntity(pos, state);
			case ELECTRIC_RANGE -> new ElectricRangeBlockEntity(pos, state);
			case SPACE_HEATER -> new SpaceHeaterBlockEntity(pos, state);
		};
	}

	@Override
	public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		if (level.isClientSide()) {
			return null;
		}
		return switch (kind) {
			case WIND_TURBINE -> createTickerHelper(type, ModBlockEntities.WIND_TURBINE, WindTurbineBlockEntity::serverTick);
			case WATER_WHEEL -> createTickerHelper(type, ModBlockEntities.WATER_WHEEL, WaterWheelBlockEntity::serverTick);
			case REFRIGERATOR -> createTickerHelper(type, ModBlockEntities.REFRIGERATOR, RefrigeratorBlockEntity::serverTick);
			case ELECTRIC_RANGE -> createTickerHelper(type, ModBlockEntities.ELECTRIC_RANGE, ElectricRangeBlockEntity::serverTick);
			case SPACE_HEATER -> createTickerHelper(type, ModBlockEntities.SPACE_HEATER, SpaceHeaterBlockEntity::serverTick);
		};
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		BlockEntity be = level.getBlockEntity(pos);
		if (!(be instanceof PoweredBlockEntity machine)) {
			return InteractionResult.PASS;
		}
		if (level.isClientSide()) {
			return InteractionResult.SUCCESS;
		}
		if (player.isShiftKeyDown()) {
			player.sendSystemMessage(machine.statusLine());
			return InteractionResult.CONSUME;
		}
		if (machine instanceof RefrigeratorBlockEntity fridge) {
			player.openMenu(new SimpleMenuProvider((id, inv, p) -> fridge.createMenu(id, inv), Component.translatable("block.homesteadcore.refrigerator")));
			return InteractionResult.CONSUME;
		}
		if (machine instanceof ElectricRangeBlockEntity range) {
			player.openMenu(new SimpleMenuProvider((id, inv, p) -> range.createMenu(id, inv), Component.translatable("container.homesteadcore.oven")));
			return InteractionResult.CONSUME;
		}
		player.sendSystemMessage(machine.statusLine());
		return InteractionResult.CONSUME;
	}
}
