package org.teacon.xkdeco.block;

import static org.teacon.xkdeco.block.XKDStateProperties.DIRECTION_PROPERTIES;

import java.util.List;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Lists;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import snownee.kiwi.customization.block.BasicBlock;
import snownee.kiwi.customization.placement.PlaceSlot;
import snownee.kiwi.util.NotNullByDefault;

@SuppressWarnings("deprecation")
@NotNullByDefault
public class AirDuctBlock extends BasicBlock {

	private static final Direction[] DIRECTIONS = Direction.values();

	public AirDuctBlock(Properties pProperties) {
		super(pProperties);
		BlockState blockState = this.stateDefinition.any();
		for (BooleanProperty property : DIRECTION_PROPERTIES) {
			blockState = blockState.setValue(property, false);
		}
		this.registerDefaultState(blockState);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		DIRECTION_PROPERTIES.forEach(pBuilder::add);
	}

	@Override
	public BlockState rotate(BlockState pState, Rotation pRotation) {
		BlockState newState = pState;
		for (Direction direction : Direction.Plane.HORIZONTAL) {
			boolean value = pState.getValue(DIRECTION_PROPERTIES.get(direction.get3DDataValue()));
			newState = newState.setValue(DIRECTION_PROPERTIES.get(pRotation.rotate(direction).get3DDataValue()), value);
		}
		return newState;
	}

	@Override
	public BlockState mirror(BlockState pState, Mirror pMirror) {
		BlockState newState = pState;
		for (Direction direction : Direction.Plane.HORIZONTAL) {
			boolean value = pState.getValue(DIRECTION_PROPERTIES.get(direction.get3DDataValue()));
			newState = newState.setValue(DIRECTION_PROPERTIES.get(pMirror.mirror(direction).get3DDataValue()), value);
		}
		return newState;
	}

	@Override
	public InteractionResult useWithoutItem(
			BlockState pState,
			Level pLevel,
			BlockPos pPos,
			Player pPlayer,
			BlockHitResult pHit) {
		if (!pPlayer.getOffhandItem().is(Items.CHAINMAIL_HELMET)) {
			return InteractionResult.PASS;
		}
		Direction direction = pHit.getDirection();
		pState = pState.cycle(DIRECTION_PROPERTIES.get(direction.get3DDataValue()));
		pLevel.setBlockAndUpdate(pPos, pState);
		return InteractionResult.sidedSuccess(pLevel.isClientSide);
	}

	@Override
	public boolean useShapeForLightOcclusion(BlockState pState) {
		return true;
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		Level level = pContext.getLevel();
		BlockPos pos = pContext.getClickedPos();
		BlockPos.MutableBlockPos mutable = pos.mutable();
		List<Direction> neighbors = Lists.newArrayList();
		for (Direction direction : DIRECTIONS) {
			BlockState neighborState = level.getBlockState(mutable.setWithOffset(pos, direction));
			if (isAirDuctSlot(neighborState, direction.getOpposite())) {
				neighbors.add(direction);
			}
		}
		BlockState blockState = defaultBlockState();
		if (neighbors.size() < 2) {
			Direction face = neighbors.isEmpty() ? pContext.getClickedFace() : neighbors.get(0);
			return blockState.setValue(DIRECTION_PROPERTIES.get(face.get3DDataValue()), true)
					.setValue(DIRECTION_PROPERTIES.get(face.getOpposite().get3DDataValue()), true);
		} else {
			for (Direction direction : neighbors) {
				blockState = blockState.setValue(DIRECTION_PROPERTIES.get(direction.get3DDataValue()), true);
			}
			return blockState;
		}
	}

	@Override
	public BlockState updateShape(
			BlockState blockState,
			Direction pDirection,
			BlockState pNeighborState,
			LevelAccessor level,
			BlockPos pos,
			BlockPos pNeighborPos) {
		if (!isAirDuctSlot(pNeighborState, pDirection.getOpposite())) {
			return blockState;
		}
		BlockPos.MutableBlockPos mutable = pos.mutable();
		List<Direction> neighbors = Lists.newArrayListWithExpectedSize(2);
		for (Direction direction : DIRECTIONS) {
			if (direction == pDirection) {
				continue;
			}
			BlockState neighborState = level.getBlockState(mutable.setWithOffset(pos, direction));
			if (isAirDuctSlot(neighborState, direction.getOpposite())) {
				neighbors.add(direction);
				if (neighbors.size() >= 2) {
					return blockState.setValue(DIRECTION_PROPERTIES.get(pDirection.get3DDataValue()), true);
				}
			}
		}
		Direction theOtherDirection = neighbors.isEmpty() ? pDirection.getOpposite() : neighbors.get(0);
		for (Direction direction : DIRECTIONS) {
			blockState = blockState.setValue(
					DIRECTION_PROPERTIES.get(direction.get3DDataValue()),
					direction == pDirection || direction == theOtherDirection);
		}
		return blockState;
	}

	public static boolean isAirDuctSlot(BlockState blockState, Direction side) {
		return PlaceSlot.find(blockState, side, "*xkdeco.air_duct").isPresent();
	}
}
