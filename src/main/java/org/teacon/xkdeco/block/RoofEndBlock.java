package org.teacon.xkdeco.block;

import org.teacon.xkdeco.util.RoofUtil;
import org.teacon.xkdeco.util.RoofUtil.RoofEndShape;
import org.teacon.xkdeco.util.RoofUtil.RoofHalf;
import org.teacon.xkdeco.util.RoofUtil.RoofShape;
import org.teacon.xkdeco.util.RoofUtil.RoofVariant;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public final class RoofEndBlock extends HorizontalDirectionalBlock implements XKDecoBlockRoof {
	public static final EnumProperty<RoofVariant> VARIANT = XKDStateProperties.ROOF_VARIANT;
	public static final EnumProperty<RoofEndShape> SHAPE = EnumProperty.create("shape", RoofEndShape.class);
	public static final EnumProperty<RoofHalf> HALF = XKDStateProperties.ROOF_HALF;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	public RoofEndBlock(Properties properties, boolean narrow) {
		super(properties);
		this.registerDefaultState(this.defaultBlockState()
				.setValue(VARIANT, RoofVariant.NORMAL).setValue(SHAPE, RoofEndShape.LEFT)
				.setValue(HALF, RoofHalf.LOWER).setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false));
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
		return RoofUtil.getShape(RoofShape.STRAIGHT, facing, roofHalf, roofVariant);
	}

//	@Override
//	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
//		return RoofUtil.getStateForPlacement(this, pContext.getLevel(),
//				pContext.getClickedPos(), pContext.getNearestLookingDirections());
//	}
//
//	@Override
//	@SuppressWarnings("deprecation")
//	public BlockState updateShape(
//			BlockState pState, Direction pFacing, BlockState pFacingState,
//			LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
//		return RoofUtil.updateShape(pState, pFacingState, pFacing);
//	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {
		return false;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		pBuilder.add(VARIANT, SHAPE, HALF, FACING, WATERLOGGED);
	}

//	@Override
//	public Iterable<BlockState> getPlacementChoices(boolean waterlogged, boolean updateSide, Direction... lookingSides) {
//		var horizontalSides = Arrays.stream(lookingSides).filter(Direction.Plane.HORIZONTAL).toArray(Direction[]::new);
//		var facingFrontRight = horizontalSides[1] == horizontalSides[0].getClockWise();
//		var baseState = this.defaultBlockState().setValue(WATERLOGGED, waterlogged)
//				.setValue(FACING, horizontalSides[0]).setValue(SHAPE, facingFrontRight ? RoofEndShape.LEFT : RoofEndShape.RIGHT);
//		var variantState = this.defaultBlockState().setValue(WATERLOGGED, waterlogged)
//				.setValue(FACING, horizontalSides[1]).setValue(SHAPE, facingFrontRight ? RoofEndShape.RIGHT : RoofEndShape.LEFT);
//		return () -> Stream.of(baseState, variantState, variantState.cycle(SHAPE), baseState.cycle(SHAPE))
//				.flatMap(s -> Stream.of(RoofHalf.TIP, RoofHalf.BASE).map(v -> s.setValue(HALF, v)))
//				.flatMap(s -> Stream.of(RoofVariant.NORMAL, RoofVariant.SLOW, RoofVariant.STEEP).map(v -> s.setValue(VARIANT, v)))
//				.filter(s -> s.getValue(HALF) != RoofHalf.BASE || s.getValue(VARIANT) != RoofVariant.NORMAL).iterator();
//	}
//
//	@Override
//	public Optional<BlockState> getUpdateShapeChoice(BlockState state, Direction fromSide) {
//		if (fromSide == state.getValue(FACING).getOpposite() && state.getValue(VARIANT) == RoofVariant.NORMAL) {
//			return Optional.of(state.setValue(VARIANT, RoofVariant.SLOW).setValue(HALF, RoofHalf.BASE));
//		}
//		if (fromSide == Direction.UP && state.getValue(VARIANT) == RoofVariant.NORMAL) {
//			return Optional.of(state.setValue(VARIANT, RoofVariant.STEEP).setValue(HALF, RoofHalf.BASE));
//		}
//		return Optional.empty();
//	}
//
//	@Override
//	public IntTriple getSideHeight(BlockState state, Direction horizontalSide) {
//		// noinspection DuplicatedCode
//		Preconditions.checkState(Direction.Plane.HORIZONTAL.test(horizontalSide));
//		var basicHeights = switch (state.getValue(VARIANT)) { // lower, higher
//			case NORMAL -> state.getValue(HALF) == RoofHalf.TIP ? new int[]{0, 16} : new int[]{8, 24};
//			case SLOW -> state.getValue(HALF) == RoofHalf.TIP ? new int[]{0, 8} : new int[]{8, 16};
//			case STEEP -> state.getValue(HALF) == RoofHalf.TIP ? new int[]{-16, 16} : new int[]{0, 32};
//		};
//		var leftHeights = switch (state.getValue(SHAPE)) { // front-right, front-left, back-left, back-right
//			case LEFT -> new int[]{basicHeights[1], basicHeights[0], basicHeights[0], basicHeights[0]};
//			case RIGHT -> new int[]{basicHeights[1], basicHeights[1], basicHeights[0], basicHeights[0]};
//		};
//		var rightHeights = switch (state.getValue(SHAPE)) { // front-left, back-left, back-right, front-right
//			case LEFT -> new int[]{basicHeights[1], basicHeights[0], basicHeights[0], basicHeights[1]};
//			case RIGHT -> new int[]{basicHeights[1], basicHeights[0], basicHeights[0], basicHeights[0]};
//		};
//		var side2DValue = horizontalSide.get2DDataValue();
//		var facing2DValue = state.getValue(FACING).get2DDataValue();
//		var leftHeight = leftHeights[(4 + facing2DValue - side2DValue) % 4];
//		var rightHeight = rightHeights[(4 + facing2DValue - side2DValue) % 4];
//		return IntTriple.of(leftHeight, (leftHeight + rightHeight) / 2, rightHeight);
//	}
}
