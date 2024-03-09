package org.teacon.xkdeco.block;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableMap;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public final class BasicCubeBlock extends Block implements XKDecoBlockBasic {
	private static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

	private final ImmutableMap<Direction, VoxelShape> shapes;

	public BasicCubeBlock(Properties properties, Map<Direction, ? extends VoxelShape> shapes) {
		super(properties);
		this.shapes = ImmutableMap.copyOf(shapes);
		this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH));
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

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		var directions = context.getNearestLookingDirections();
		var direction = Arrays.stream(directions).filter(Direction.Plane.HORIZONTAL).findFirst();
		return this.defaultBlockState().setValue(FACING, direction.orElse(Direction.SOUTH).getOpposite());
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}
}
