package org.teacon.xkdeco.block;

import org.teacon.xkdeco.block.settings.CheckedWaterloggedBlock;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public final class SpecialDessertBlock extends Block implements CheckedWaterloggedBlock {
	private static final int MAXIMUM_COUNT = 7;

	private static final IntegerProperty COUNT = IntegerProperty.create("count", 1, MAXIMUM_COUNT);

	public SpecialDessertBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(COUNT, MAXIMUM_COUNT));
	}

	@Override
	@SuppressWarnings("deprecation")
	public InteractionResult use(
			BlockState state, Level world, BlockPos pos,
			Player player, InteractionHand hand, BlockHitResult hit) {
		var item = player.getItemInHand(hand);
		if (world.isClientSide) {
			if (this.eat(world, pos, state, player).consumesAction()) {
				return InteractionResult.SUCCESS;
			}
			if (item.isEmpty()) {
				return InteractionResult.CONSUME;
			}
		}
		return this.eat(world, pos, state, player);
	}

	private InteractionResult eat(Level world, BlockPos pos, BlockState state, Player player) {
		// noinspection DuplicatedCode
		if (player.canEat(false)) {
			// TODO: player stats
			// player.awardStat(Stats.EAT_CAKE_SLICE);
			player.getFoodData().eat(2, 0.1F);
			world.gameEvent(player, GameEvent.EAT, pos);
			var count = (int) state.getValue(COUNT);
			if (count > 1) {
				world.setBlock(pos, state.setValue(COUNT, count - 1), UPDATE_ALL);
			} else {
				world.removeBlock(pos, false);
				world.gameEvent(player, GameEvent.BLOCK_DESTROY, pos);
			}
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, PathComputationType type) {
		return false;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(COUNT);
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}

	@Override
	@SuppressWarnings("deprecation")
	public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos) {
		return 2 * state.getValue(COUNT);
	}
}
