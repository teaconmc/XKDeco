package org.teacon.xkdeco.block;

import org.jspecify.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SnowyBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.SlabType;

public final class SnowySlabBlock extends SpecialSlabBlock {
	public static final BooleanProperty SNOWY = BlockStateProperties.SNOWY;

	public SnowySlabBlock(Properties properties) {
		super(properties, Type.DIRT);
		this.registerDefaultState(this.stateDefinition.any()
				.setValue(TYPE, SlabType.BOTTOM)
				.setValue(WATERLOGGED, Boolean.FALSE)
				.setValue(SNOWY, false));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		super.createBlockStateDefinition(pBuilder);
		pBuilder.add(SNOWY);
	}

	@Override
	protected BlockState updateShape(
			BlockState pState,
			LevelReader pLevel,
			ScheduledTickAccess pTicks,
			BlockPos pCurrentPos,
			Direction pFacing,
			BlockPos pFacingPos,
			BlockState pFacingState,
			RandomSource pRandom) {
		if (pFacing == Direction.UP) {
			pState = pState.setValue(SNOWY, pState.getValue(TYPE) != SlabType.BOTTOM && SnowyBlock.isSnowySetting(pFacingState));
		}
		return super.updateShape(pState, pLevel, pTicks, pCurrentPos, pFacing, pFacingPos, pFacingState, pRandom);
	}

	@Override
	public @Nullable BlockState getStateForPlacement(BlockPlaceContext pContext) {
		BlockState state = super.getStateForPlacement(pContext);
		if (state != null && state.getValue(TYPE) != SlabType.BOTTOM) {
			BlockState aboveState = pContext.getLevel().getBlockState(pContext.getClickedPos().above());
			state = state.setValue(SNOWY, SnowyBlock.isSnowySetting(aboveState));
		}
		return state;
	}
}
