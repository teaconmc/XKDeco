package org.teacon.xkdeco.block;

import static org.teacon.xkdeco.util.RoofUtil.RoofHalf;
import static org.teacon.xkdeco.util.RoofUtil.RoofShape;
import static org.teacon.xkdeco.util.RoofUtil.RoofVariant;

import java.util.Arrays;
import java.util.List;
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
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public final class RoofBlock extends HorizontalDirectionalBlock implements XKDecoBlock.Roof {
	public static final EnumProperty<RoofVariant> VARIANT = XKDStateProperties.ROOF_VARIANT;
	public static final EnumProperty<RoofShape> SHAPE = EnumProperty.create("shape", RoofShape.class);
	public static final EnumProperty<RoofHalf> HALF = XKDStateProperties.ROOF_HALF;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	public static final VoxelShape ROOF_E = Shapes.or(Block.box(0, 8, 0, 8, 16, 16), Block.box(0, 0, 0, 16, 8, 16));
	public static final VoxelShape ROOF_INNER_EN = Shapes.or(
			Block.box(0, 8, 0, 8, 16, 8),
			Block.box(0, 8, 8, 16, 16, 16),
			Block.box(0, 0, 0, 16, 8, 16));
	public static final VoxelShape ROOF_INNER_NW = Shapes.or(
			Block.box(0, 8, 8, 8, 16, 16),
			Block.box(8, 8, 0, 16, 16, 16),
			Block.box(0, 0, 0, 16, 8, 16));
	public static final VoxelShape ROOF_INNER_SE = Shapes.or(
			Block.box(8, 8, 0, 16, 16, 8),
			Block.box(0, 8, 0, 8, 16, 16),
			Block.box(0, 0, 0, 16, 8, 16));
	public static final VoxelShape ROOF_INNER_BASE_EN = Shapes.or(
			Block.box(0, 16, 0, 8, 24, 8),
			Block.box(0, 16, 8, 16, 24, 16),
			Block.box(0, 8, 0, 16, 16, 16));
	public static final VoxelShape ROOF_INNER_BASE_NW = Shapes.or(
			Block.box(0, 16, 8, 8, 24, 16),
			Block.box(8, 16, 0, 16, 24, 16),
			Block.box(0, 8, 0, 16, 16, 16));
	public static final VoxelShape ROOF_INNER_BASE_SE = Shapes.or(
			Block.box(8, 16, 0, 16, 24, 8),
			Block.box(0, 16, 0, 8, 24, 16),
			Block.box(0, 8, 0, 16, 16, 16));
	public static final VoxelShape ROOF_INNER_BASE_WS = Shapes.or(
			Block.box(8, 16, 8, 16, 24, 16),
			Block.box(0, 16, 0, 16, 24, 8),
			Block.box(0, 8, 0, 16, 16, 16));
	public static final VoxelShape ROOF_INNER_WS = Shapes.or(
			Block.box(8, 8, 8, 16, 16, 16),
			Block.box(0, 8, 0, 16, 16, 8),
			Block.box(0, 0, 0, 16, 8, 16));
	public static final VoxelShape ROOF_N = Shapes.or(Block.box(0, 8, 8, 16, 16, 16), Block.box(0, 0, 0, 16, 8, 16));
	public static final VoxelShape ROOF_OUTER_EN = Shapes.or(Block.box(0, 8, 8, 8, 16, 16), Block.box(0, 0, 0, 16, 8, 16));
	public static final VoxelShape ROOF_OUTER_NW = Shapes.or(Block.box(8, 8, 8, 16, 16, 16), Block.box(0, 0, 0, 16, 8, 16));
	public static final VoxelShape ROOF_OUTER_SE = Shapes.or(Block.box(0, 8, 0, 8, 16, 8), Block.box(0, 0, 0, 16, 8, 16));
	public static final VoxelShape ROOF_OUTER_BASE_EN = Shapes.or(Block.box(0, 16, 8, 8, 24, 16), Block.box(0, 8, 0, 16, 16, 16));
	public static final VoxelShape ROOF_OUTER_BASE_NW = Shapes.or(Block.box(8, 16, 8, 16, 24, 16), Block.box(0, 8, 0, 16, 16, 16));
	public static final VoxelShape ROOF_OUTER_BASE_SE = Shapes.or(Block.box(0, 16, 0, 8, 24, 8), Block.box(0, 8, 0, 16, 16, 16));
	public static final VoxelShape ROOF_OUTER_BASE_WS = Shapes.or(Block.box(8, 16, 0, 16, 24, 8), Block.box(0, 8, 0, 16, 16, 16));
	public static final VoxelShape ROOF_OUTER_WS = Shapes.or(Block.box(8, 8, 0, 16, 16, 8), Block.box(0, 0, 0, 16, 8, 16));
	public static final VoxelShape ROOF_S = Shapes.or(Block.box(0, 8, 0, 16, 16, 8), Block.box(0, 0, 0, 16, 8, 16));
	public static final VoxelShape ROOF_BASE_E = Shapes.or(Block.box(0, 16, 0, 8, 24, 16), Block.box(0, 8, 0, 16, 16, 16));
	public static final VoxelShape ROOF_BASE_N = Shapes.or(Block.box(0, 16, 8, 16, 24, 16), Block.box(0, 8, 0, 16, 16, 16));
	public static final VoxelShape ROOF_BASE_S = Shapes.or(Block.box(0, 16, 0, 16, 24, 8), Block.box(0, 8, 0, 16, 16, 16));
	public static final VoxelShape ROOF_BASE_W = Shapes.or(Block.box(8, 16, 0, 16, 24, 16), Block.box(0, 8, 0, 16, 16, 16));
	public static final VoxelShape ROOF_W = Shapes.or(Block.box(8, 8, 0, 16, 16, 16), Block.box(0, 0, 0, 16, 8, 16));
	public static final VoxelShape SLOW_ROOF_E = Shapes.or(Block.box(0, 4, 0, 8, 8, 16), Block.box(0, 0, 0, 16, 4, 16));
	public static final VoxelShape SLOW_ROOF_INNER_EN = Shapes.or(
			Block.box(0, 4, 8, 16, 8, 16),
			Block.box(0, 4, 0, 8, 8, 8),
			Block.box(0, 0, 0, 16, 4, 16));
	public static final VoxelShape SLOW_ROOF_INNER_NW = Shapes.or(
			Block.box(8, 4, 0, 16, 8, 16),
			Block.box(0, 4, 8, 8, 8, 16),
			Block.box(0, 0, 0, 16, 4, 16));
	public static final VoxelShape SLOW_ROOF_INNER_SE = Shapes.or(
			Block.box(0, 4, 0, 8, 8, 16),
			Block.box(8, 4, 0, 16, 8, 8),
			Block.box(0, 0, 0, 16, 4, 16));
	public static final VoxelShape SLOW_ROOF_INNER_BASE_EN = Shapes.or(
			Block.box(0, 12, 8, 16, 16, 16),
			Block.box(0, 12, 0, 8, 16, 8),
			Block.box(0, 0, 0, 16, 12, 16));
	public static final VoxelShape SLOW_ROOF_INNER_BASE_NW = Shapes.or(
			Block.box(8, 12, 0, 16, 16, 16),
			Block.box(0, 12, 8, 8, 16, 16),
			Block.box(0, 0, 0, 16, 12, 16));
	public static final VoxelShape SLOW_ROOF_INNER_BASE_SE = Shapes.or(
			Block.box(0, 12, 0, 8, 16, 16),
			Block.box(8, 12, 0, 16, 16, 8),
			Block.box(0, 0, 0, 16, 12, 16));
	public static final VoxelShape SLOW_ROOF_INNER_BASE_WS = Shapes.or(
			Block.box(0, 12, 0, 16, 16, 8),
			Block.box(8, 12, 8, 16, 16, 16),
			Block.box(0, 0, 0, 16, 12, 16));
	public static final VoxelShape SLOW_ROOF_INNER_WS = Shapes.or(
			Block.box(0, 4, 0, 16, 8, 8),
			Block.box(8, 4, 8, 16, 8, 16),
			Block.box(0, 0, 0, 16, 4, 16));
	public static final VoxelShape SLOW_ROOF_N = Shapes.or(Block.box(0, 4, 8, 16, 8, 16), Block.box(0, 0, 0, 16, 4, 16));
	public static final VoxelShape SLOW_ROOF_OUTER_EN = Shapes.or(Block.box(0, 4, 8, 8, 8, 16), Block.box(0, 0, 0, 16, 4, 16));
	public static final VoxelShape SLOW_ROOF_OUTER_NW = Shapes.or(Block.box(8, 4, 8, 16, 8, 16), Block.box(0, 0, 0, 16, 4, 16));
	public static final VoxelShape SLOW_ROOF_OUTER_SE = Shapes.or(Block.box(0, 4, 0, 8, 8, 8), Block.box(0, 0, 0, 16, 4, 16));
	public static final VoxelShape SLOW_ROOF_OUTER_BASE_EN = Shapes.or(Block.box(0, 12, 8, 8, 16, 16), Block.box(0, 0, 0, 16, 12, 16));
	public static final VoxelShape SLOW_ROOF_OUTER_BASE_NW = Shapes.or(Block.box(8, 12, 8, 16, 16, 16), Block.box(0, 0, 0, 16, 12, 16));
	public static final VoxelShape SLOW_ROOF_OUTER_BASE_SE = Shapes.or(Block.box(0, 12, 0, 8, 16, 8), Block.box(0, 0, 0, 16, 12, 16));
	public static final VoxelShape SLOW_ROOF_OUTER_BASE_WS = Shapes.or(Block.box(8, 12, 0, 16, 16, 8), Block.box(0, 0, 0, 16, 12, 16));
	public static final VoxelShape SLOW_ROOF_OUTER_WS = Shapes.or(Block.box(8, 4, 0, 16, 8, 8), Block.box(0, 0, 0, 16, 4, 16));
	public static final VoxelShape SLOW_ROOF_S = Shapes.or(Block.box(0, 4, 0, 16, 8, 8), Block.box(0, 0, 0, 16, 4, 16));
	public static final VoxelShape SLOW_ROOF_BASE_E = Shapes.or(Block.box(0, 12, 0, 8, 16, 16), Block.box(0, 0, 0, 16, 12, 16));
	public static final VoxelShape SLOW_ROOF_BASE_N = Shapes.or(Block.box(0, 12, 8, 16, 16, 16), Block.box(0, 0, 0, 16, 12, 16));
	public static final VoxelShape SLOW_ROOF_BASE_S = Shapes.or(Block.box(0, 12, 0, 16, 16, 8), Block.box(0, 0, 0, 16, 12, 16));
	public static final VoxelShape SLOW_ROOF_BASE_W = Shapes.or(Block.box(8, 12, 0, 16, 16, 16), Block.box(0, 0, 0, 16, 12, 16));
	public static final VoxelShape SLOW_ROOF_W = Shapes.or(Block.box(8, 4, 0, 16, 8, 16), Block.box(0, 0, 0, 16, 4, 16));
	public static final VoxelShape STEEP_ROOF_E = Shapes.or(Block.box(0, 8, 0, 4, 16, 16), Block.box(0, 0, 0, 8, 8, 16));
	public static final VoxelShape STEEP_ROOF_INNER_EN = Shapes.or(Block.box(0, 8, 12, 16, 16, 16), Block.box(0, 8, 0, 4, 16, 12));
	public static final VoxelShape STEEP_ROOF_INNER_NW = Shapes.or(Block.box(12, 8, 0, 16, 16, 16), Block.box(0, 8, 12, 12, 16, 16));
	public static final VoxelShape STEEP_ROOF_INNER_SE = Shapes.or(Block.box(0, 8, 0, 4, 16, 16), Block.box(4, 8, 0, 16, 16, 4));
	public static final VoxelShape STEEP_ROOF_INNER_BASE_EN = Shapes.or(
			Block.box(0, 8, 4, 16, 16, 16),
			Block.box(0, 8, 0, 12, 16, 4),
			Block.box(0, 0, 0, 16, 8, 16));
	public static final VoxelShape STEEP_ROOF_INNER_BASE_NW = Shapes.or(
			Block.box(4, 8, 0, 16, 16, 16),
			Block.box(0, 8, 4, 4, 16, 16),
			Block.box(0, 0, 0, 16, 8, 16));
	public static final VoxelShape STEEP_ROOF_INNER_BASE_SE = Shapes.or(
			Block.box(0, 8, 0, 12, 16, 16),
			Block.box(12, 8, 0, 16, 16, 12),
			Block.box(0, 0, 0, 16, 8, 16));
	public static final VoxelShape STEEP_ROOF_INNER_BASE_WS = Shapes.or(
			Block.box(0, 8, 0, 16, 16, 12),
			Block.box(4, 8, 12, 16, 16, 16),
			Block.box(0, 0, 0, 16, 8, 16));
	public static final VoxelShape STEEP_ROOF_INNER_WS = Shapes.or(Block.box(0, 8, 0, 16, 16, 4), Block.box(12, 8, 4, 16, 16, 16));
	public static final VoxelShape STEEP_ROOF_N = Shapes.or(Block.box(0, 8, 12, 16, 16, 16), Block.box(0, 0, 8, 16, 8, 16));
	public static final VoxelShape STEEP_ROOF_OUTER_EN = Shapes.or(Block.box(0, 0, 8, 8, 8, 16), Block.box(0, 8, 12, 4, 16, 16));
	public static final VoxelShape STEEP_ROOF_OUTER_NW = Shapes.or(Block.box(8, 0, 8, 16, 8, 16), Block.box(12, 8, 12, 16, 16, 16));
	public static final VoxelShape STEEP_ROOF_OUTER_SE = Shapes.or(Block.box(0, 0, 0, 8, 8, 8), Block.box(0, 8, 0, 4, 16, 4));
	public static final VoxelShape STEEP_ROOF_OUTER_BASE_EN = Shapes.or(Block.box(0, 8, 4, 12, 16, 16), Block.box(0, 0, 0, 16, 8, 16));
	public static final VoxelShape STEEP_ROOF_OUTER_BASE_NW = Shapes.or(Block.box(4, 8, 4, 16, 16, 16), Block.box(0, 0, 0, 16, 8, 16));
	public static final VoxelShape STEEP_ROOF_OUTER_BASE_SE = Shapes.or(Block.box(0, 8, 0, 12, 16, 12), Block.box(0, 0, 0, 16, 8, 16));
	public static final VoxelShape STEEP_ROOF_OUTER_BASE_WS = Shapes.or(Block.box(4, 8, 0, 16, 16, 12), Block.box(0, 0, 0, 16, 8, 16));
	public static final VoxelShape STEEP_ROOF_OUTER_WS = Shapes.or(Block.box(8, 0, 0, 16, 8, 8), Block.box(12, 8, 0, 16, 16, 4));
	public static final VoxelShape STEEP_ROOF_S = Shapes.or(Block.box(0, 8, 0, 16, 16, 4), Block.box(0, 0, 0, 16, 8, 8));
	public static final VoxelShape STEEP_ROOF_BASE_E = Shapes.or(Block.box(0, 8, 0, 12, 16, 16), Block.box(0, 0, 0, 16, 8, 16));
	public static final VoxelShape STEEP_ROOF_BASE_N = Shapes.or(Block.box(0, 8, 4, 16, 16, 16), Block.box(0, 0, 0, 16, 8, 16));
	public static final VoxelShape STEEP_ROOF_BASE_S = Shapes.or(Block.box(0, 8, 0, 16, 16, 12), Block.box(0, 0, 0, 16, 8, 16));
	public static final VoxelShape STEEP_ROOF_BASE_W = Shapes.or(Block.box(4, 8, 0, 16, 16, 16), Block.box(0, 0, 0, 16, 8, 16));
	public static final VoxelShape STEEP_ROOF_W = Shapes.or(Block.box(12, 8, 0, 16, 16, 16), Block.box(8, 0, 0, 16, 8, 16));

	public static final List<VoxelShape> ROOF_SHAPES = List.of(
			Shapes.block(), ROOF_OUTER_EN, ROOF_E, ROOF_INNER_SE,
			ROOF_INNER_WS, Shapes.block(), ROOF_OUTER_SE, ROOF_S,
			ROOF_W, ROOF_INNER_NW, Shapes.block(), ROOF_OUTER_WS,
			ROOF_OUTER_NW, ROOF_N, ROOF_INNER_EN, Shapes.block(),
			Shapes.block(), SLOW_ROOF_OUTER_EN, SLOW_ROOF_E, SLOW_ROOF_INNER_SE,
			SLOW_ROOF_INNER_WS, Shapes.block(), SLOW_ROOF_OUTER_SE, SLOW_ROOF_S,
			SLOW_ROOF_W, SLOW_ROOF_INNER_NW, Shapes.block(), SLOW_ROOF_OUTER_WS,
			SLOW_ROOF_OUTER_NW, SLOW_ROOF_N, SLOW_ROOF_INNER_EN, Shapes.block(),
			Shapes.block(), STEEP_ROOF_OUTER_EN, STEEP_ROOF_E, STEEP_ROOF_INNER_SE,
			STEEP_ROOF_INNER_WS, Shapes.block(), STEEP_ROOF_OUTER_SE, STEEP_ROOF_S,
			STEEP_ROOF_W, STEEP_ROOF_INNER_NW, Shapes.block(), STEEP_ROOF_OUTER_WS,
			STEEP_ROOF_OUTER_NW, STEEP_ROOF_N, STEEP_ROOF_INNER_EN, Shapes.block());
	public static final List<VoxelShape> ROOF_BASE_SHAPES = List.of(
			Shapes.block(), ROOF_OUTER_BASE_EN, ROOF_BASE_E, ROOF_INNER_BASE_SE,
			ROOF_INNER_BASE_WS, Shapes.block(), ROOF_OUTER_BASE_SE, ROOF_BASE_S,
			ROOF_BASE_W, ROOF_INNER_BASE_NW, Shapes.block(), ROOF_OUTER_BASE_WS,
			ROOF_OUTER_BASE_NW, ROOF_BASE_N, ROOF_INNER_BASE_EN, Shapes.block(),
			Shapes.block(), SLOW_ROOF_OUTER_BASE_EN, SLOW_ROOF_BASE_E, SLOW_ROOF_INNER_BASE_SE,
			SLOW_ROOF_INNER_BASE_WS, Shapes.block(), SLOW_ROOF_OUTER_BASE_SE, SLOW_ROOF_BASE_S,
			SLOW_ROOF_BASE_W, SLOW_ROOF_INNER_BASE_NW, Shapes.block(), SLOW_ROOF_OUTER_BASE_WS,
			SLOW_ROOF_OUTER_BASE_NW, SLOW_ROOF_BASE_N, SLOW_ROOF_INNER_BASE_EN, Shapes.block(),
			Shapes.block(), STEEP_ROOF_OUTER_BASE_EN, STEEP_ROOF_BASE_E, STEEP_ROOF_INNER_BASE_SE,
			STEEP_ROOF_INNER_BASE_WS, Shapes.block(), STEEP_ROOF_OUTER_BASE_SE, STEEP_ROOF_BASE_S,
			STEEP_ROOF_BASE_W, STEEP_ROOF_INNER_BASE_NW, Shapes.block(), STEEP_ROOF_OUTER_BASE_WS,
			STEEP_ROOF_OUTER_BASE_NW, STEEP_ROOF_BASE_N, STEEP_ROOF_INNER_BASE_EN, Shapes.block());

	public RoofBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.defaultBlockState()
				.setValue(VARIANT, RoofVariant.NORMAL).setValue(SHAPE, RoofShape.STRAIGHT)
				.setValue(HALF, RoofHalf.TIP).setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false));
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean useShapeForLightOcclusion(BlockState pState) {
		return true;
	}

	@Override
	@SuppressWarnings("deprecation")
	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		var facing = pState.getValue(FACING);
		var roofHalf = pState.getValue(HALF);
		var roofVariant = pState.getValue(VARIANT);
		return RoofUtil.getShape(pState.getValue(SHAPE), facing, roofHalf, roofVariant);
	}

	@Override
	@SuppressWarnings("deprecation")
	public FluidState getFluidState(BlockState pState) {
		return pState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {
		return false;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		return RoofUtil.getStateForPlacement(this, pContext.getLevel(),
				pContext.getClickedPos(), pContext.getNearestLookingDirections());
	}

	@Override
	@SuppressWarnings("deprecation")
	public BlockState updateShape(
			BlockState pState, Direction pFacing, BlockState pFacingState,
			LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
		if (pState.getValue(WATERLOGGED)) {
			pLevel.scheduleTick(pCurrentPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
		}
		return RoofUtil.updateShape(pState, pFacingState, pFacing);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		pBuilder.add(VARIANT, SHAPE, HALF, FACING, WATERLOGGED);
	}

	@Override
	public Iterable<BlockState> getPlacementChoices(boolean waterlogged, boolean updateSide, Direction... lookingSides) {
		// noinspection DuplicatedCode
		var horizontalSides = Arrays.stream(lookingSides).filter(Direction.Plane.HORIZONTAL).toArray(Direction[]::new);
		var facingFrontRight = horizontalSides[1] == horizontalSides[0].getClockWise();
		var baseState = this.defaultBlockState().setValue(WATERLOGGED, waterlogged).setValue(FACING, horizontalSides[0]);
		var variantState = this.defaultBlockState().setValue(WATERLOGGED, waterlogged).setValue(FACING, horizontalSides[1]);
		var innerState = (facingFrontRight ? baseState : variantState).setValue(SHAPE, RoofShape.INNER);
		var outerState = (facingFrontRight ? baseState : variantState).setValue(SHAPE, RoofShape.OUTER);
		var innerVariantState = innerState.setValue(FACING, facingFrontRight ? horizontalSides[1].getOpposite() : horizontalSides[0]);
		var outerVariantState = outerState.setValue(FACING, facingFrontRight ? horizontalSides[1].getOpposite() : horizontalSides[0]);
		return () -> (
				updateSide
						? Stream.of(baseState, variantState)
						: Stream.of(baseState, innerState, outerState, variantState, innerVariantState, outerVariantState))
				.flatMap(s -> Stream.of(RoofHalf.TIP, RoofHalf.BASE).map(v -> s.setValue(HALF, v)))
				.flatMap(s -> Stream.of(RoofVariant.NORMAL, RoofVariant.SLOW, RoofVariant.STEEP).map(v -> s.setValue(VARIANT, v)))
				.filter(s -> s.getValue(HALF) != RoofHalf.BASE || s.getValue(VARIANT) != RoofVariant.NORMAL).iterator();
	}

	@Override
	public Optional<BlockState> getUpdateShapeChoice(BlockState state, Direction fromSide) {
		if (fromSide == state.getValue(FACING).getOpposite() && state.getValue(VARIANT) == RoofVariant.NORMAL &&
				state.getValue(SHAPE) == RoofShape.STRAIGHT) {
			return Optional.of(state.setValue(VARIANT, RoofVariant.SLOW).setValue(HALF, RoofHalf.BASE));
		}
		if (fromSide == Direction.UP && state.getValue(VARIANT) == RoofVariant.NORMAL) {
			return Optional.of(state.setValue(VARIANT, RoofVariant.STEEP).setValue(HALF, RoofHalf.BASE));
		}
		return Optional.empty();
	}

	@Override
	public IntTriple getSideHeight(BlockState state, Direction horizontalSide) {
		// noinspection DuplicatedCode
		Preconditions.checkState(Direction.Plane.HORIZONTAL.test(horizontalSide));
		var basicHeights = switch (state.getValue(VARIANT)) { // lower, higher
			case NORMAL -> state.getValue(HALF) == RoofHalf.TIP ? new int[]{0, 16} : new int[]{8, 24};
			case SLOW -> state.getValue(HALF) == RoofHalf.TIP ? new int[]{0, 8} : new int[]{8, 16};
			case STEEP -> state.getValue(HALF) == RoofHalf.TIP ? new int[]{-16, 16} : new int[]{0, 32};
		};
		var cornerHeights = switch (state.getValue(SHAPE)) { // front-right, front-left, back-left, back-right
			case STRAIGHT -> new int[]{basicHeights[1], basicHeights[1], basicHeights[0], basicHeights[0]};
			case INNER -> new int[]{basicHeights[1], basicHeights[1], basicHeights[0], basicHeights[1]};
			case OUTER -> new int[]{basicHeights[1], basicHeights[0], basicHeights[0], basicHeights[0]};
		};
		var side2DValue = horizontalSide.get2DDataValue();
		var facing2DValue = state.getValue(FACING).get2DDataValue();
		var leftHeight = cornerHeights[(4 + facing2DValue - side2DValue) % 4];
		var rightHeight = cornerHeights[(5 + facing2DValue - side2DValue) % 4];
		return IntTriple.of(leftHeight, (leftHeight + rightHeight) / 2, rightHeight);
	}
}
