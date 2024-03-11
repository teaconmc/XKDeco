package org.teacon.xkdeco.block;

import static org.teacon.xkdeco.util.RoofUtil.RoofHalf;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public final class RoofTipBlock extends Block implements SimpleWaterloggedBlock {
	public static final EnumProperty<RoofHalf> HALF = XKDStateProperties.ROOF_HALF;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	public RoofTipBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.defaultBlockState().setValue(WATERLOGGED, false));
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean useShapeForLightOcclusion(BlockState pState) {
		return true;
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {
		return false;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		pBuilder.add(HALF, WATERLOGGED);
	}
}
