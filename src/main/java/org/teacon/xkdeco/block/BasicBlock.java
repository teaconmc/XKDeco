package org.teacon.xkdeco.block;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

import com.google.common.collect.ImmutableMap;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public final class BasicBlock extends Block implements SimpleWaterloggedBlock, XKDecoBlock.Basic {
	private static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	private static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

	private final boolean isSupportNeeded;
	private final ImmutableMap<Direction, VoxelShape> shapes;

	public BasicBlock(Properties properties, boolean isSupportNeeded, Map<Direction, ? extends VoxelShape> shapes) {
		super(properties);
		this.isSupportNeeded = isSupportNeeded;
		this.shapes = ImmutableMap.copyOf(shapes);
		this.registerDefaultState(this.getStateDefinition().any()
				.setValue(WATERLOGGED, false).setValue(FACING, Direction.NORTH));
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		var directions = context.getNearestLookingDirections();
		var fluidState = context.getLevel().getFluidState(context.getClickedPos());
		var direction = Arrays.stream(directions).filter(Direction.Plane.HORIZONTAL).findFirst();
		return this.defaultBlockState()
				.setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER)
				.setValue(FACING, direction.orElse(Direction.SOUTH).getOpposite());
	}

	@Override
	@SuppressWarnings("deprecation")
	public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
		return Objects.requireNonNull(this.shapes.get(state.getValue(FACING)));
	}

	@Override
	@SuppressWarnings("deprecation")
	public BlockState rotate(BlockState state, Rotation rotation) {
		return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}

	@Override
	@SuppressWarnings("deprecation")
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}

	@Override
	@SuppressWarnings("deprecation")
	public BlockState updateShape(
			BlockState state, Direction direction, BlockState prevState,
			LevelAccessor world, BlockPos pos, BlockPos prevPos) {
		// noinspection DuplicatedCode
		if (direction == Direction.DOWN && !state.canSurvive(world, pos)) {
			return Blocks.AIR.defaultBlockState();
		}
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
		builder.add(FACING, WATERLOGGED);
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
		return !this.isSupportNeeded || world.getBlockState(pos.below()).isFaceSturdy(world, pos.below(), Direction.UP);
	}
}
