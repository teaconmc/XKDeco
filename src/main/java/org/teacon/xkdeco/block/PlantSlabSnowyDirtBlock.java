package org.teacon.xkdeco.block;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public final class PlantSlabSnowyDirtBlock extends APlantSlabBlock {

	public static final BooleanProperty SNOWY = BlockStateProperties.SNOWY;

	public PlantSlabSnowyDirtBlock(Properties properties, boolean isPath, String dirtSlabId) {
		super(properties, isPath, dirtSlabId);
		this.registerDefaultState(this.stateDefinition.any().setValue(SNOWY, false));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		super.createBlockStateDefinition(pBuilder);
		pBuilder.add(SNOWY);
	}

	@Override
	public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
		return pFacing == Direction.UP ? pState.setValue(SNOWY, isSnowySetting(pFacingState)) : super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		BlockState state = super.getStateForPlacement(pContext);
        if (state != null) {
            return state.setValue(SNOWY,isSnowySetting(state));
        }
        return pContext.getLevel().getBlockState(pContext.getClickedPos().above());
    }

	private static boolean isSnowySetting(BlockState pState) {
		return pState.is(BlockTags.SNOW);
	}

}
