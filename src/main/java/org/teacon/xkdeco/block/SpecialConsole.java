package org.teacon.xkdeco.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class SpecialConsole extends StairBlock {
	private static final int[] SHAPE_BY_STATE = new int[]{12, 5, 3, 10, 14, 13, 7, 11, 13, 7, 11, 14, 8, 4, 1, 2, 4, 1, 2, 8};

	public SpecialConsole(Properties properties) {
		super(Blocks.AIR::defaultBlockState, properties);
	}

	@Override
	@SuppressWarnings("deprecation")
	public float getShadeBrightness(BlockState state, BlockGetter world, BlockPos pos) {
		return 1.0F;
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter world, BlockPos pos) {
		return true;
	}

	@Override
	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		return BOTTOM_SHAPES[SHAPE_BY_STATE[this.getShapeIndex(pState)]];
	}

	private int getShapeIndex(BlockState pState) {
		return pState.getValue(SHAPE).ordinal() * 4 + pState.getValue(FACING).get2DDataValue();
	}
}
