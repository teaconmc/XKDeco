package org.teacon.xkdeco.block;

import java.util.List;
import java.util.Locale;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public class HangingFasciaBlock extends BasicBlock {
	public static final EnumProperty<Side> SIDE = EnumProperty.create("side", Side.class);
	public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;

	public HangingFasciaBlock(BlockBehaviour.Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(SIDE, Side.NONE));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		super.createBlockStateDefinition(pBuilder);
		pBuilder.add(SIDE);
	}

	@Override
	public BlockState updateShape(
			BlockState pState,
			Direction pDirection,
			BlockState pNeighborState,
			LevelAccessor pLevel,
			BlockPos pPos,
			BlockPos pNeighborPos) {
		var axis = pState.getValue(AXIS);
		if (pDirection.getAxis() != axis) {
			return pState;
		}
		var side = pState.getValue(SIDE);
		if (side != Side.NONE && pDirection.getAxisDirection() !=
				(side == Side.POSITIVE ? Direction.AxisDirection.POSITIVE : Direction.AxisDirection.NEGATIVE)) {
			return pState;
		}
		return getSideAt(pState, pLevel, pPos, pDirection);
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		Direction.Axis axis = pContext.getHorizontalDirection().getCounterClockWise().getAxis();
		BlockState blockState = defaultBlockState().setValue(AXIS, axis);
		Direction preferredSide;
		if (axis.test(pContext.getClickedFace())) {
			preferredSide = pContext.getClickedFace();
		} else {
			preferredSide = Direction.get(Direction.AxisDirection.POSITIVE, axis);
		}
		return getSideAt(blockState, pContext.getLevel(), pContext.getClickedPos(), preferredSide);
	}

	public static BlockState getSideAt(BlockState state, BlockGetter level, BlockPos pos, Direction preferredSide) {
		for (Direction side : List.of(preferredSide, preferredSide.getOpposite())) {
			BlockPos neighborPos = pos.relative(side.getOpposite());
			BlockState neighbor = level.getBlockState(neighborPos);
			if (neighbor.isFaceSturdy(level, neighborPos, side, SupportType.RIGID)) {
				return state.setValue(SIDE, side.getAxisDirection() == Direction.AxisDirection.NEGATIVE ? Side.POSITIVE : Side.NEGATIVE);
			}
		}
		return state.setValue(SIDE, Side.NONE);
	}

	public enum Side implements StringRepresentable {
		POSITIVE, NEGATIVE, NONE;

		@Override
		public String getSerializedName() {
			return name().toLowerCase(Locale.ENGLISH);
		}
	}
}
