package org.teacon.xkdeco.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SnowyDirtBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.SlabType;
import snownee.kiwi.util.NotNullByDefault;

@NotNullByDefault
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
	public BlockState updateShape(
			BlockState pState,
			Direction pFacing,
			BlockState pFacingState,
			LevelAccessor pLevel,
			BlockPos pCurrentPos,
			BlockPos pFacingPos) {
		if (pFacing == Direction.UP) {
			pState = pState.setValue(SNOWY, pState.getValue(TYPE) != SlabType.BOTTOM && SnowyDirtBlock.isSnowySetting(pFacingState));
		}
		return super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		BlockState state = super.getStateForPlacement(pContext);
		if (state != null && state.getValue(TYPE) != SlabType.BOTTOM) {
			BlockState aboveState = pContext.getLevel().getBlockState(pContext.getClickedPos().above());
			state = state.setValue(SNOWY, SnowyDirtBlock.isSnowySetting(aboveState));
		}
		return state;
	}
}
