package org.teacon.xkdeco.block;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public final class HollowBlock extends Block implements SimpleWaterloggedBlock {
	private static final VoxelShape TABLE_BASE = Block.box(4, 0, 4, 12, 3, 12);
	private static final VoxelShape TABLE_LEG = Block.box(6, 3, 6, 10, 13, 10);
	private static final VoxelShape TABLE_TOP = Block.box(0, 13, 0, 16, 16, 16);
	private static final VoxelShape BIG_TABLE_TOP = Block.box(0, 8, 0, 16, 16, 16);
	private static final VoxelShape BIG_TABLE_LEG_NN = Block.box(0, 0, 0, 2, 8, 2);
	private static final VoxelShape BIG_TABLE_LEG_NP = Block.box(0, 0, 14, 2, 8, 16);
	private static final VoxelShape BIG_TABLE_LEG_PN = Block.box(14, 0, 0, 16, 8, 2);
	private static final VoxelShape BIG_TABLE_LEG_PP = Block.box(14, 0, 14, 16, 8, 16);

	public static final VoxelShape TABLE_SHAPE = Shapes.or(TABLE_BASE, TABLE_LEG, TABLE_TOP);
	public static final VoxelShape BIG_TABLE_SHAPE = Shapes.or(
			BIG_TABLE_TOP,
			BIG_TABLE_LEG_PP,
			BIG_TABLE_LEG_PN,
			BIG_TABLE_LEG_NP,
			BIG_TABLE_LEG_NN);

	private static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	public HollowBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.getStateDefinition().any().setValue(WATERLOGGED, false));
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		var fluidState = context.getLevel().getFluidState(context.getClickedPos());
		return this.defaultBlockState().setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(WATERLOGGED);
	}

}
