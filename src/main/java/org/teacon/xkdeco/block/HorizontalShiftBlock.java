package org.teacon.xkdeco.block;

import org.jetbrains.annotations.Nullable;
import org.teacon.xkdeco.util.RoofUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;

public class HorizontalShiftBlock extends HorizontalDirectionalBlock implements SimpleWaterloggedBlock {
	public static final EnumProperty<RoofUtil.RoofHalf> HALF = XKDStateProperties.ROOF_HALF;

	public HorizontalShiftBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.defaultBlockState().setValue(HALF, RoofUtil.RoofHalf.LOWER));
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {
		return false;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		pBuilder.add(HALF);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		return this.defaultBlockState().setValue(HALF, RoofUtil.RoofHalf.LOWER);
	}
}
