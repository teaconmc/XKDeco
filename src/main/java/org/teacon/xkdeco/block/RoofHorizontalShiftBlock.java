package org.teacon.xkdeco.block;

import java.util.List;
import java.util.Optional;

import org.teacon.xkdeco.util.IntTriple;
import org.teacon.xkdeco.util.RoofUtil;

import com.google.common.base.Preconditions;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public final class RoofHorizontalShiftBlock extends HorizontalShiftBlock implements XKDecoBlockRoof {

	public RoofHorizontalShiftBlock(Properties properties) {
		super(properties);
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean useShapeForLightOcclusion(BlockState pState) {
		return true;
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
		return RoofUtil.updateShape(pState, pFacingState, pFacing);
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
