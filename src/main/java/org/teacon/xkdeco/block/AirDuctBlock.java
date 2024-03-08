package org.teacon.xkdeco.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

public class AirDuctBlock extends Block implements SimpleWaterloggedBlock {
	public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
	public static final BooleanProperty EAST = BlockStateProperties.EAST;
	public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
	public static final BooleanProperty WEST = BlockStateProperties.WEST;
	public static final BooleanProperty UP = BlockStateProperties.UP;
	public static final BooleanProperty DOWN = BlockStateProperties.DOWN;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	public AirDuctBlock(Properties pProperties) {
		super(pProperties);
		this.registerDefaultState(this.stateDefinition.any()
				.setValue(NORTH, false)
				.setValue(EAST, false)
				.setValue(SOUTH, false)
				.setValue(WEST, false)
				.setValue(UP, false)
				.setValue(DOWN, false)
				.setValue(WATERLOGGED, false));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		pBuilder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN, WATERLOGGED);
	}

	@Override
	public BlockState rotate(BlockState pState, Rotation pRotation) {
		BlockState newState = pState;
		for (Direction direction : Direction.Plane.HORIZONTAL) {
			boolean value = pState.getValue(XKDStateProperties.DIRECTION_PROPERTIES.get(direction.get3DDataValue()));
			newState = newState.setValue(XKDStateProperties.DIRECTION_PROPERTIES.get(pRotation.rotate(direction).get3DDataValue()), value);
		}
		return newState;
	}

	@Override
	public BlockState mirror(BlockState pState, Mirror pMirror) {
		BlockState newState = pState;
		for (Direction direction : Direction.Plane.HORIZONTAL) {
			boolean value = pState.getValue(XKDStateProperties.DIRECTION_PROPERTIES.get(direction.get3DDataValue()));
			newState = newState.setValue(XKDStateProperties.DIRECTION_PROPERTIES.get(pMirror.mirror(direction).get3DDataValue()), value);
		}
		return newState;
	}

	@Override
	public InteractionResult use(
			BlockState pState,
			Level pLevel,
			BlockPos pPos,
			Player pPlayer,
			InteractionHand pHand,
			BlockHitResult pHit) {
		Direction direction = pHit.getDirection();
		pState = pState.cycle(XKDStateProperties.DIRECTION_PROPERTIES.get(direction.get3DDataValue()));
		pLevel.setBlockAndUpdate(pPos, pState);
		return InteractionResult.sidedSuccess(pLevel.isClientSide);
	}
}
