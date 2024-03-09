package org.teacon.xkdeco.block;

import static org.teacon.xkdeco.util.RoofUtil.RoofHalf;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

import org.teacon.xkdeco.util.IntTriple;
import org.teacon.xkdeco.util.RoofUtil;
import org.teacon.xkdeco.util.RoofUtil.RoofEaveShape;

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
import net.minecraft.world.phys.shapes.VoxelShape;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public final class RoofEaveBlock extends HorizontalDirectionalBlock implements XKDecoBlockRoof {
	public static final EnumProperty<RoofEaveShape> SHAPE = EnumProperty.create("shape", RoofEaveShape.class);
	public static final EnumProperty<RoofHalf> HALF = XKDStateProperties.ROOF_HALF;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	public static final VoxelShape ROOF_EAVE_TIP = Block.box(0, 0, 0, 16, 8, 16);
	public static final VoxelShape ROOF_EAVE_BASE = Block.box(0, 8, 0, 16, 16, 16);

	public RoofEaveBlock(Properties properties, boolean narrow) {
		super(properties);
		this.registerDefaultState(this.defaultBlockState()
				.setValue(SHAPE, RoofEaveShape.STRAIGHT)
				.setValue(HALF, RoofHalf.TIP)
				.setValue(FACING, Direction.NORTH)
				.setValue(WATERLOGGED, false));
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean useShapeForLightOcclusion(BlockState pState) {
		return true;
	}

	@Override
	@SuppressWarnings("deprecation")
	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		return switch (pState.getValue(HALF)) {
			case BASE -> ROOF_EAVE_BASE;
			case TIP -> ROOF_EAVE_TIP;
		};
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
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		pBuilder.add(SHAPE, HALF, FACING, WATERLOGGED);
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
	public Iterable<BlockState> getPlacementChoices(boolean waterlogged, boolean updateSide, Direction... lookingSides) {
		// noinspection DuplicatedCode
		var horizontalSides = Arrays.stream(lookingSides).filter(Direction.Plane.HORIZONTAL).toArray(Direction[]::new);
		var facingFrontRight = horizontalSides[1] == horizontalSides[0].getClockWise();
		var baseState = this.defaultBlockState().setValue(WATERLOGGED, waterlogged).setValue(FACING, horizontalSides[0]);
		var variantState = this.defaultBlockState().setValue(WATERLOGGED, waterlogged).setValue(FACING, horizontalSides[1]);
		var innerState = (facingFrontRight ? baseState : variantState).setValue(SHAPE, RoofEaveShape.INNER);
		var outerState = (facingFrontRight ? baseState : variantState).setValue(SHAPE, RoofEaveShape.OUTER);
		var innerVariantState = innerState.setValue(FACING, facingFrontRight ? horizontalSides[1].getOpposite() : horizontalSides[0]);
		var outerVariantState = outerState.setValue(FACING, facingFrontRight ? horizontalSides[1].getOpposite() : horizontalSides[0]);
		var baseEndState = this.defaultBlockState().setValue(WATERLOGGED, waterlogged)
				.setValue(FACING, horizontalSides[0]).setValue(SHAPE, facingFrontRight ? RoofEaveShape.LEFT_END : RoofEaveShape.RIGHT_END);
		var variantEndState = this.defaultBlockState().setValue(WATERLOGGED, waterlogged)
				.setValue(FACING, horizontalSides[1]).setValue(SHAPE, facingFrontRight ? RoofEaveShape.RIGHT_END : RoofEaveShape.LEFT_END);
		return () -> (
				updateSide
						? Stream.of(baseEndState, variantEndState, baseState,
						variantEndState.cycle(SHAPE), baseEndState.cycle(SHAPE), variantState)
						: Stream.of(baseEndState, variantEndState, baseState, innerState, outerState,
						variantEndState.cycle(SHAPE), baseEndState.cycle(SHAPE), variantState, innerVariantState, outerVariantState))
				.flatMap(s -> Stream.of(RoofHalf.TIP, RoofHalf.BASE).map(v -> s.setValue(HALF, v))).iterator();
	}

	@Override
	public Optional<BlockState> getUpdateShapeChoice(BlockState state, Direction fromSide) {
		if (fromSide == state.getValue(FACING).getClockWise() && state.getValue(SHAPE) == RoofEaveShape.RIGHT_END) {
			return Optional.of(state.setValue(SHAPE, RoofEaveShape.STRAIGHT));
		}
		if (fromSide == state.getValue(FACING).getCounterClockWise() && state.getValue(SHAPE) == RoofEaveShape.LEFT_END) {
			return Optional.of(state.setValue(SHAPE, RoofEaveShape.STRAIGHT));
		}
		return Optional.empty();
	}

	@Override
	public IntTriple getSideHeight(BlockState state, Direction horizontalSide) {
		Preconditions.checkState(Direction.Plane.HORIZONTAL.test(horizontalSide));
		var basicHeights = state.getValue(HALF) == RoofHalf.TIP ? new int[]{0, 4, 8} : new int[]{0, 8, 16}; // lower, higher
		var middleHeights = switch (state.getValue(SHAPE)) { // front, left, back, right
			case STRAIGHT -> new int[]{basicHeights[2], basicHeights[1], basicHeights[0], basicHeights[1]};
			case INNER -> new int[]{basicHeights[2], basicHeights[1], basicHeights[1], basicHeights[2]};
			case OUTER -> new int[]{basicHeights[1], basicHeights[0], basicHeights[0], basicHeights[1]};
			case LEFT_END -> new int[]{basicHeights[2], basicHeights[0], basicHeights[0], basicHeights[1]};
			case RIGHT_END -> new int[]{basicHeights[2], basicHeights[1], basicHeights[0], basicHeights[0]};
		};
		var side2DValue = horizontalSide.get2DDataValue();
		var facing2DValue = state.getValue(FACING).get2DDataValue();
		var middleHeight = middleHeights[(4 + facing2DValue - side2DValue) % 4];
		// noinspection SuspiciousNameCombination
		return IntTriple.of(middleHeight, middleHeight, middleHeight);
	}
}
