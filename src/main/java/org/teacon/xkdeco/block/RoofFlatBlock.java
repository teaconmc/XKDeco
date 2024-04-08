package org.teacon.xkdeco.block;

import static org.teacon.xkdeco.util.RoofUtil.RoofHalf;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
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
public final class RoofFlatBlock extends Block implements SimpleWaterloggedBlock {
	public static final EnumProperty<RoofHalf> HALF = XKDStateProperties.ROOF_HALF;
	public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	public static final VoxelShape ROOF_FLAT_TIP = Block.box(0, 0, 0, 16, 8, 16);
	public static final VoxelShape ROOF_FLAT_BASE = Block.box(0, 8, 0, 16, 16, 16);

	public RoofFlatBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.defaultBlockState()
				.setValue(HALF, RoofHalf.LOWER).setValue(AXIS, Direction.Axis.X)
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
			case UPPER -> ROOF_FLAT_BASE;
			case LOWER -> ROOF_FLAT_TIP;
		};
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {
		return false;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		pBuilder.add(HALF, AXIS, WATERLOGGED);
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
//
//	@Override
//	public Iterable<BlockState> getPlacementChoices(boolean waterlogged, boolean updateSide, Direction... lookingSides) {
//		var horizontalSides = Arrays.stream(lookingSides).filter(Direction.Plane.HORIZONTAL).toArray(Direction[]::new);
//		var baseState = this.defaultBlockState().setValue(WATERLOGGED, waterlogged).setValue(AXIS, horizontalSides[0].getAxis());
//		var variantSide = this.defaultBlockState().setValue(WATERLOGGED, waterlogged).setValue(AXIS, horizontalSides[1].getAxis());
//		return () -> Stream.of(baseState, variantSide)
//				.flatMap(s -> Stream.of(RoofHalf.TIP, RoofHalf.BASE).map(v -> s.setValue(HALF, v))).iterator();
//	}
//
//	@Override
//	public Optional<BlockState> getUpdateShapeChoice(BlockState state, Direction fromSide) {
//		return Optional.empty();
//	}
//
//	@Override
//	public IntTriple getSideHeight(BlockState state, Direction horizontalSide) {
//		Preconditions.checkState(Direction.Plane.HORIZONTAL.test(horizontalSide));
//		var basicHeights = state.getValue(HALF) == RoofHalf.TIP ? new int[]{0, 8} : new int[]{0, 16}; // lower, higher
//		var middleHeight = horizontalSide.getAxis() == state.getValue(AXIS) ? basicHeights[1] : basicHeights[0];
//		// noinspection SuspiciousNameCombination
//		return IntTriple.of(middleHeight, middleHeight, middleHeight);
//	}

	@Override
	public BlockState rotate(BlockState pState, Rotation pRotation) {
		if (pRotation == Rotation.CLOCKWISE_90 || pRotation == Rotation.COUNTERCLOCKWISE_90) {
			return pState.cycle(AXIS);
		}
		return pState;
	}
}
