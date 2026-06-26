package org.teacon.xkdeco.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import snownee.kiwi.customization.block.StringProperty;

public final class FallenLeavesBlock extends Block {
	private static final StringProperty HALF = XKDStateProperties.HALF;

	public FallenLeavesBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected BlockState updateShape(
			BlockState pState,
			LevelReader pLevel,
			ScheduledTickAccess pTicks,
			BlockPos pPos,
			Direction pDirection,
			BlockPos pNeighborPos,
			BlockState pNeighborState,
			RandomSource pRandom) {
		if (pDirection == Direction.DOWN) {
			pState = pState.setValue(HALF, isBottomSlab(pNeighborState) ? "lower" : "upper");
		}
		return pState;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		BlockState blockState = pContext.getLevel().getBlockState(pContext.getClickedPos().below());
		return defaultBlockState().setValue(HALF, isBottomSlab(blockState) ? "lower" : "upper");
	}

	private static boolean isBottomSlab(BlockState state) {
		return state.is(BlockTags.SLABS) && state.hasProperty(SlabBlock.TYPE) && state.getValue(SlabBlock.TYPE) == SlabType.BOTTOM;
	}
}
