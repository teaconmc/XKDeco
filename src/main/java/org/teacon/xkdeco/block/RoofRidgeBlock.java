package org.teacon.xkdeco.block;

import java.util.Optional;
import java.util.stream.Stream;

import org.teacon.xkdeco.util.IntTriple;
import org.teacon.xkdeco.util.RoofUtil;

import com.google.common.base.Preconditions;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.WallSide;
import net.minecraft.world.level.material.Fluids;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public final class RoofRidgeBlock extends WallBlock implements XKDecoBlock.Roof {
	public RoofRidgeBlock(Properties properties, boolean asian) {
		super(properties);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		return RoofUtil.getStateForPlacement(this, pContext.getLevel(),
				pContext.getClickedPos(), pContext.getNearestLookingDirections());
	}

	@Override
	public BlockState updateShape(
			BlockState pState, Direction pFacing, BlockState pFacingState,
			LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
		if (pState.getValue(WATERLOGGED)) {
			pLevel.scheduleTick(pCurrentPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
		}
		return RoofUtil.updateShape(pState, pFacingState, pFacing);
	}

	@Override
	public Optional<BlockState> getUpdateShapeChoice(BlockState state, Direction fromSide) {
		var propertyChoice = switch (fromSide) {
			case DOWN, UP -> Optional.<EnumProperty<WallSide>>empty();
			case NORTH -> Optional.of(NORTH_WALL);
			case SOUTH -> Optional.of(SOUTH_WALL);
			case WEST -> Optional.of(WEST_WALL);
			case EAST -> Optional.of(EAST_WALL);
		};
		return propertyChoice.flatMap(p -> switch (state.getValue(p)) {
			case NONE -> Optional.of(state.setValue(p, WallSide.LOW));
			case LOW, TALL -> Optional.empty();
		});
	}

	@Override
	public Iterable<BlockState> getPlacementChoices(boolean waterlogged, boolean updateSide, Direction... lookingSides) {
		var props = Stream.of(lookingSides).flatMap(s -> switch (s) {
			case DOWN, UP -> Stream.of();
			case NORTH -> Stream.of(NORTH_WALL);
			case SOUTH -> Stream.of(SOUTH_WALL);
			case WEST -> Stream.of(WEST_WALL);
			case EAST -> Stream.of(EAST_WALL);
		}).toList();
		return () -> Stream.of(this.defaultBlockState().setValue(WATERLOGGED, waterlogged))
				.flatMap(s -> props.get(3).getAllValues().map(v -> s.setValue(props.get(3), v.value())))
				.flatMap(s -> props.get(2).getAllValues().map(v -> s.setValue(props.get(2), v.value())))
				.flatMap(s -> props.get(1).getAllValues().map(v -> s.setValue(props.get(1), v.value())))
				.flatMap(s -> props.get(0).getAllValues().map(v -> s.setValue(props.get(0), v.value()))).iterator();
	}

	@Override
	public IntTriple getSideHeight(BlockState state, Direction horizontalSide) {
		Preconditions.checkState(Direction.Plane.HORIZONTAL.test(horizontalSide));
		var wallSide = switch (horizontalSide) {
			case DOWN, UP -> WallSide.NONE;
			case NORTH -> state.getValue(NORTH_WALL);
			case SOUTH -> state.getValue(SOUTH_WALL);
			case WEST -> state.getValue(WEST_WALL);
			case EAST -> state.getValue(EAST_WALL);
		};
		var middleHeight = switch (wallSide) {
			case NONE -> 0;
			case LOW -> 8;
			case TALL -> 16;
		};
		return IntTriple.of(0, middleHeight, 0);
	}
}
