package org.teacon.xkdeco.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import snownee.kiwi.customization.block.component.FrontAndTopComponent;
import snownee.kiwi.util.NotNullByDefault;

@NotNullByDefault
public class HollowSteelHalfBeamBlock extends XKDBlock {
	public HollowSteelHalfBeamBlock(Properties pProperties) {
		super(pProperties);
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
			return pState.getValue(FrontAndTopComponent.ORIENTATION).top().getAxis() !=
					connectedState.getValue(FrontAndTopComponent.ORIENTATION).top().getAxis();
		} else {
			return false;
		}
	}

	private Direction getConnectedDirection(BlockState blockState) {
		return blockState.getValue(FrontAndTopComponent.ORIENTATION).front();
	}
}
