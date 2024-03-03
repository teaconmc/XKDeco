package org.teacon.xkdeco.block;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public final class IsotropicHollowBlock extends Block implements SimpleWaterloggedBlock, XKDecoBlock.Isotropic {
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
	private final VoxelShape blockShape;

	public IsotropicHollowBlock(Properties properties, VoxelShape blockShape) {
		super(properties);
		this.blockShape = blockShape;
		this.registerDefaultState(this.getStateDefinition().any().setValue(WATERLOGGED, false));
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		var fluidState = context.getLevel().getFluidState(context.getClickedPos());
		return this.defaultBlockState().setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
	}

	@Override
	@SuppressWarnings("deprecation")
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return this.blockShape;
	}

	@Override
	@SuppressWarnings("deprecation")
	public BlockState updateShape(
			BlockState state, Direction direction, BlockState prevState,
			LevelAccessor world, BlockPos pos, BlockPos prevPos) {
		if (state.getValue(WATERLOGGED)) {
			world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
		}
		return super.updateShape(state, direction, prevState, world, pos, prevPos);
	}

	@Override
	@SuppressWarnings("deprecation")
	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(WATERLOGGED);
	}

	@Override
	public boolean isGlass() {
		return false;
	}

	@Override
	public VoxelShape getShapeStatic(BlockState state) {
		return this.blockShape;
	}
}
