package org.teacon.xkdeco.block;

import java.util.Locale;

import org.teacon.xkdeco.util.RoofUtil;
import org.teacon.xkdeco.util.RoofUtil.RoofShape;
import snownee.kiwi.customization.block.StringProperty;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public final class RoofEndBlock extends HorizontalDirectionalBlock implements SimpleWaterloggedBlock {
	private static final StringProperty VARIANT = XKDStateProperties.ROOF_VARIANT;
	private static final StringProperty HALF = XKDStateProperties.HALF;

	public RoofEndBlock(Properties properties) {
		super(properties);
	}

	@Override
	@SuppressWarnings("deprecation")
	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		var facing = pState.getValue(HorizontalDirectionalBlock.FACING);
		var roofHalf = RoofUtil.RoofHalf.valueOf(pState.getValue(HALF).toUpperCase(Locale.ENGLISH));
		var roofVariant = RoofUtil.RoofVariant.valueOf(pState.getValue(VARIANT).toUpperCase(Locale.ENGLISH));
		return RoofUtil.getShape(RoofShape.STRAIGHT, facing, roofHalf, roofVariant);
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {
		return false;
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
