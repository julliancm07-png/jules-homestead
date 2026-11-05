package dev.jules.homestead.block;

import com.mojang.serialization.MapCodec;
import dev.jules.homestead.weather.Front;
import dev.jules.homestead.weather.WeatherManager;
import dev.jules.homestead.weather.WeatherSavedData;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.Nullable;

/**
 * Barometer: use it to read the forecast. Emits a redstone signal (POWERED) while a front is active or due within
 * {@link #WARNING_TICKS} — useful for automatically shutting storm shutters or starting heaters.
 */
public class BarometerBlock extends HorizontalDirectionalBlock {
	public static final MapCodec<BarometerBlock> CODEC = simpleCodec(BarometerBlock::new);
	public static final int WARNING_TICKS = 2_400;

	public BarometerBlock(Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(BlockStateProperties.POWERED, false));
	}

	@Override
	protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
		return CODEC;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, BlockStateProperties.POWERED);
	}

	@Override
	public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
		return defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
	}

	@Override
	protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
		if (!level.isClientSide()) {
			level.scheduleTick(pos, this, 20);
		}
	}

	@Override
	protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		boolean warn = warningActive(level);
		if (state.getValue(BlockStateProperties.POWERED) != warn) {
			level.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.POWERED, warn));
		}
		level.scheduleTick(pos, this, 100);
	}

	public static boolean warningActive(ServerLevel level) {
		WeatherSavedData d = WeatherManager.data(level.getServer());
		long now = level.getServer().overworld().getGameTime();
		return d.current() != null || (d.next() != null && d.next().startTick() - now <= WARNING_TICKS);
	}

	@Override
	protected boolean isSignalSource(BlockState state) {
		return true;
	}

	@Override
	protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
		return state.getValue(BlockStateProperties.POWERED) ? 15 : 0;
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		if (level instanceof ServerLevel sl) {
			player.sendSystemMessage(reading(sl));
		}
		return InteractionResult.SUCCESS;
	}

	public static Component reading(ServerLevel level) {
		WeatherSavedData d = WeatherManager.data(level.getServer());
		long now = level.getServer().overworld().getGameTime();
		Front cur = d.current();
		if (cur != null) {
			return Component.translatable("homesteadcore.barometer.now." + cur.kind().getSerializedName(),
					WeatherManager.severityName(cur.severity()), WeatherManager.formatDuration(cur.endTick() - now)).withStyle(ChatFormatting.GOLD);
		}
		if (d.next() != null) {
			return WeatherManager.forecastLine(d.next(), now).copy().withStyle(ChatFormatting.AQUA);
		}
		return Component.translatable("homesteadcore.barometer.fair").withStyle(ChatFormatting.GREEN);
	}
}
