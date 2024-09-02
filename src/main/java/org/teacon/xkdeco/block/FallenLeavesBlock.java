package org.teacon.xkdeco.block;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.SlabType;
import snownee.kiwi.util.NotNullByDefault;

@NotNullByDefault
public final class FallenLeavesBlock extends Block {
	private static final EnumProperty<XKDStateProperties.RoofHalf> HALF = XKDStateProperties.HALF;

	public FallenLeavesBlock(Properties properties) {
		super(properties);
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
			pState = pState.setValue(
					HALF,
					isBottomSlab(pNeighborState) ? XKDStateProperties.RoofHalf.LOWER : XKDStateProperties.RoofHalf.UPPER);
		}
		return pState;
	}

	@Override
	public @NotNull BlockState getStateForPlacement(BlockPlaceContext pContext) {
		BlockState blockState = pContext.getLevel().getBlockState(pContext.getClickedPos().below());
		return defaultBlockState().setValue(
				HALF,
				isBottomSlab(blockState) ? XKDStateProperties.RoofHalf.LOWER : XKDStateProperties.RoofHalf.UPPER);
	}

	private static boolean isBottomSlab(BlockState state) {
		return state.is(BlockTags.SLABS) && state.hasProperty(SlabBlock.TYPE) && state.getValue(SlabBlock.TYPE) == SlabType.BOTTOM;
	}
}
