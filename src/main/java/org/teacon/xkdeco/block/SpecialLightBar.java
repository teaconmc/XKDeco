package org.teacon.xkdeco.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class SpecialLightBar extends StairBlock {
	private static final VoxelShape SOUTH_BOTTOM = Block.box(0, 2, 13, 16, 6, 16);
	private static final VoxelShape NORTH_BOTTOM = Block.box(0, 2, 0, 16, 6, 3);
	private static final VoxelShape WEST_BOTTOM = Block.box(0, 2, 0, 3, 6, 16);
	private static final VoxelShape EAST_BOTTOM = Block.box(13, 2, 0, 16, 6, 16);

	private static final VoxelShape SOUTH_TOP = Block.box(0, 10, 13, 16, 14, 16);
	private static final VoxelShape NORTH_TOP = Block.box(0, 10, 0, 16, 14, 3);
	private static final VoxelShape WEST_TOP = Block.box(0, 10, 0, 3, 14, 16);
	private static final VoxelShape EAST_TOP = Block.box(13, 10, 0, 16, 14, 16);

	private static final VoxelShape NE_TOP_INNER = Shapes.or(NORTH_TOP, EAST_TOP);
	private static final VoxelShape ES_TOP_INNER = Shapes.or(SOUTH_TOP, EAST_TOP);
	private static final VoxelShape SW_TOP_INNER = Shapes.or(WEST_TOP, SOUTH_TOP);
	private static final VoxelShape WN_TOP_INNER = Shapes.or(NORTH_TOP, WEST_TOP);

	private static final VoxelShape NE_TOP_OUTER = Shapes.join(NORTH_TOP, EAST_TOP, BooleanOp.AND);
	private static final VoxelShape ES_TOP_OUTER = Shapes.join(SOUTH_TOP, EAST_TOP, BooleanOp.AND);
	private static final VoxelShape SW_TOP_OUTER = Shapes.join(WEST_TOP, SOUTH_TOP, BooleanOp.AND);
	private static final VoxelShape WN_TOP_OUTER = Shapes.join(NORTH_TOP, WEST_TOP, BooleanOp.AND);

	private static final VoxelShape NE_BOTTOM_INNER = Shapes.or(NORTH_BOTTOM, EAST_BOTTOM);
	private static final VoxelShape ES_BOTTOM_INNER = Shapes.or(SOUTH_BOTTOM, EAST_BOTTOM);
	private static final VoxelShape SW_BOTTOM_INNER = Shapes.or(WEST_BOTTOM, SOUTH_BOTTOM);
	private static final VoxelShape WN_BOTTOM_INNER = Shapes.or(NORTH_BOTTOM, WEST_BOTTOM);

	private static final VoxelShape NE_BOTTOM_OUTER = Shapes.join(NORTH_BOTTOM, EAST_BOTTOM, BooleanOp.AND);
	private static final VoxelShape ES_BOTTOM_OUTER = Shapes.join(SOUTH_BOTTOM, EAST_BOTTOM, BooleanOp.AND);
	private static final VoxelShape SW_BOTTOM_OUTER = Shapes.join(WEST_BOTTOM, SOUTH_BOTTOM, BooleanOp.AND);
	private static final VoxelShape WN_BOTTOM_OUTER = Shapes.join(NORTH_BOTTOM, WEST_BOTTOM, BooleanOp.AND);

	public SpecialLightBar(Properties properties) {
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
}
