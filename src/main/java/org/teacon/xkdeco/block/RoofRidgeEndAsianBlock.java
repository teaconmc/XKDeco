package org.teacon.xkdeco.block;

import java.util.List;
import java.util.Optional;

import org.teacon.xkdeco.util.IntTriple;
import org.teacon.xkdeco.util.RoofUtil;
import org.teacon.xkdeco.util.RoofUtil.RoofHalf;

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
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public final class RoofRidgeEndAsianBlock extends HorizontalDirectionalBlock implements XKDecoBlock.Roof {
	public static final EnumProperty<RoofUtil.RoofVariant> VARIANT = EnumProperty.create(
			"variant",
			RoofUtil.RoofVariant.class,
			RoofUtil.RoofVariant.NORMAL,
			RoofUtil.RoofVariant.STEEP);
	public static final EnumProperty<RoofHalf> HALF = XKDStateProperties.ROOF_HALF;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	public RoofRidgeEndAsianBlock(Properties properties, boolean narrow) {
		super(properties);
		this.registerDefaultState(this.defaultBlockState()
				.setValue(VARIANT, RoofUtil.RoofVariant.NORMAL)
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
		return super.getShape(pState, pLevel, pPos, pContext); //TODO
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		return RoofUtil.getStateForPlacement(this, pContext.getLevel(),
				pContext.getClickedPos(), pContext.getNearestLookingDirections());
	}

	@Override
	@SuppressWarnings("deprecation")
	public BlockState updateShape(
			BlockState pState,
			Direction pFacing,
			BlockState pFacingState,
			LevelAccessor pLevel,
			BlockPos pCurrentPos,
			BlockPos pFacingPos) {
		if (pState.getValue(WATERLOGGED)) {
			pLevel.scheduleTick(pCurrentPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
		}
		return RoofUtil.updateShape(pState, pFacingState, pFacing);
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {
		return false;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		pBuilder.add(VARIANT, HALF, FACING, WATERLOGGED);
	}

	@Override
	public Iterable<BlockState> getPlacementChoices(boolean waterlogged, boolean updateSide, Direction... lookingSides) {
		return List.of(defaultBlockState()); //TODO
	}

	@Override
	public Optional<BlockState> getUpdateShapeChoice(BlockState state, Direction fromSide) {
		return Optional.empty(); //TODO
	}

	@Override
	public IntTriple getSideHeight(BlockState state, Direction horizontalSide) {
		// noinspection DuplicatedCode
		Preconditions.checkState(Direction.Plane.HORIZONTAL.test(horizontalSide));
		return IntTriple.of(0, 0, 0); //TODO
	}
}
