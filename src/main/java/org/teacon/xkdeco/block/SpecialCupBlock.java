package org.teacon.xkdeco.block;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public final class SpecialCupBlock extends Block {
	private static final int MAXIMUM_COUNT = 4;

	private static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	private static final IntegerProperty COUNT = IntegerProperty.create("count", 1, MAXIMUM_COUNT);

	private static final VoxelShape ONE_SHAPE = Block.box(6, 0, 6, 10, 6, 10);
	private static final VoxelShape TWO_SHAPE = Block.box(3, 0, 3, 13, 6, 13);
	private static final VoxelShape THREE_SHAPE = Block.box(2, 0, 2, 14, 6, 14);
	private static final VoxelShape FOUR_SHAPE = Block.box(2, 0, 2, 14, 7, 14);

	public SpecialCupBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, false).setValue(COUNT, 1));
	}

	@Override
	@SuppressWarnings("deprecation")
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
		return switch (state.getValue(COUNT)) {
			case 1 -> ONE_SHAPE;
			case 2 -> TWO_SHAPE;
			case 3 -> THREE_SHAPE;
			default -> FOUR_SHAPE;
		};
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		var state = context.getLevel().getBlockState(context.getClickedPos());
		if (state.is(this)) {
			return state.setValue(COUNT, Math.min(MAXIMUM_COUNT, state.getValue(COUNT) + 1));
		}
		var fluidState = context.getLevel().getFluidState(context.getClickedPos());
		return this.defaultBlockState().setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
	}

	@Override
	@SuppressWarnings("deprecation")
	public BlockState updateShape(
			BlockState state, Direction direction, BlockState neighborState,
			LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
		// noinspection DuplicatedCode
		if (direction == Direction.DOWN && !state.canSurvive(world, pos)) {
			return Blocks.AIR.defaultBlockState();
		}
		if (state.getValue(WATERLOGGED)) {
			world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
		}
		return super.updateShape(state, direction, neighborState, world, pos, neighborPos);
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
		if (!context.isSecondaryUseActive()) {
			var item = context.getItemInHand();
			if (item.is(this.asItem()) && state.getValue(COUNT) < MAXIMUM_COUNT) {
				return true;
			}
		}
		return super.canBeReplaced(state, context);
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
		return world.getBlockState(pos.below()).isFaceSturdy(world, pos.below(), Direction.UP);
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, PathComputationType type) {
		return false;
	}

	@Override
	@SuppressWarnings("deprecation")
	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(COUNT, WATERLOGGED);
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}

	@Override
	@SuppressWarnings("deprecation")
	public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos) {
		return state.getValue(COUNT);
	}
}
