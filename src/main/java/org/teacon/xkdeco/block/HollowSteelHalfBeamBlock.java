package org.teacon.xkdeco.block;

import org.teacon.xkdeco.block.setting.CheckedWaterloggedBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;

public class HollowSteelHalfBeamBlock extends FaceAttachedHorizontalDirectionalBlock implements CheckedWaterloggedBlock {
	public HollowSteelHalfBeamBlock(Properties pProperties) {
		super(pProperties);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(FACE, AttachFace.WALL));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		pBuilder.add(FACING, FACE);
	}

	@Override
	public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
		Direction connectedDirection = getConnectedDirection(pState);
		BlockState connectedState = pLevel.getBlockState(pPos.relative(connectedDirection.getOpposite()));
		if (!(connectedState.getBlock() instanceof HollowSteelHalfBeamBlock)) {
			return true;
		}
		if (connectedDirection != getConnectedDirection(connectedState).getOpposite()) {
			return true;
		}
		if (connectedDirection.getAxis().isVertical()) {
			return pState.getValue(FACING).getAxis() != connectedState.getValue(FACING).getAxis();
		} else {
			return false;
		}
	}
}
