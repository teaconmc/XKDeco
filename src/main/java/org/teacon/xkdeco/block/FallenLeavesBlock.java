package org.teacon.xkdeco.block;

import org.jetbrains.annotations.NotNull;
import org.teacon.xkdeco.util.RoofUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.SlabType;

public final class FallenLeavesBlock extends Block {
	public static final EnumProperty<RoofUtil.RoofHalf> HALF = XKDStateProperties.ROOF_HALF;

	public FallenLeavesBlock(Properties properties) {
		super(properties);
		registerDefaultState(this.stateDefinition.any().setValue(HALF, RoofUtil.RoofHalf.LOWER));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		pBuilder.add(HALF);
	}

	@Override
	public BlockState updateShape(
			BlockState pState,
			Direction pDirection,
			BlockState pNeighborState,
			LevelAccessor pLevel,
			BlockPos pPos,
			BlockPos pNeighborPos) {
		if (pDirection == Direction.DOWN) {
			pState = pState.setValue(HALF, isBottomSlab(pNeighborState) ? RoofUtil.RoofHalf.UPPER : RoofUtil.RoofHalf.LOWER);
		}
		return pState;
	}

	@Override
	public @NotNull BlockState getStateForPlacement(BlockPlaceContext pContext) {
		BlockState blockState = pContext.getLevel().getBlockState(pContext.getClickedPos().below());
		return defaultBlockState().setValue(HALF, isBottomSlab(blockState) ? RoofUtil.RoofHalf.UPPER : RoofUtil.RoofHalf.LOWER);
	}

	private static boolean isBottomSlab(BlockState state) {
		return state.is(BlockTags.SLABS) && state.hasProperty(SlabBlock.TYPE) && state.getValue(SlabBlock.TYPE) == SlabType.BOTTOM;
	}
}
