package org.teacon.xkdeco.block.impl;

import java.util.List;

import org.teacon.xkdeco.block.setting.CanSurviveHandler;
import org.teacon.xkdeco.util.CommonProxy;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;

public class MetalLadderCanSurviveHandler implements CanSurviveHandler {
	@Override
	public boolean isSensitiveSide(BlockState state, Direction side) {
		return true;
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
		Direction direction = state.getValue(LadderBlock.FACING);
		BlockPos.MutableBlockPos mutable = pos.mutable();
		for (Direction d : List.of(direction.getOpposite(), Direction.UP, Direction.DOWN)) {
			mutable.setWithOffset(pos, d);
			BlockState neighborState = world.getBlockState(mutable);
			if (neighborState.isFaceSturdy(world, mutable, d.getOpposite(), SupportType.RIGID)) {
				return true;
			}
			if (Direction.Plane.VERTICAL.test(d) &&
					CommonProxy.isLadder(neighborState, world, pos) &&
					neighborState.hasProperty(LadderBlock.FACING) &&
					neighborState.getValue(LadderBlock.FACING) == direction) {
				return true;
			}
		}
		return false;
	}
}
