package org.teacon.xkdeco.block;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.teacon.xkdeco.blockentity.WardrobeBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.AbstractChestBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoubleBlockCombiner;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class SpecialWardrobeBlock extends AbstractChestBlock<WardrobeBlockEntity> {
	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
	public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
	public static final EnumProperty<DoorHingeSide> HINGE = BlockStateProperties.DOOR_HINGE;
	public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
	public static final BooleanProperty DOUBLE = BooleanProperty.create("double");

	public static final VoxelShape SHAPE_NORTH_OPEN = Block.box(0, 0, 1, 16, 16, 16);
	public static final VoxelShape SHAPE_SOUTH_OPEN = Block.box(0, 0, 0, 16, 16, 15);
	public static final VoxelShape SHAPE_WEST_OPEN = Block.box(1, 0, 0, 16, 16, 16);
	public static final VoxelShape SHAPE_EAST_OPEN = Block.box(0, 0, 0, 15, 16, 16);

	public SpecialWardrobeBlock(Properties pProperties) {
		super(pProperties.pushReaction(PushReaction.BLOCK), WardrobeBlockEntity.TYPE::get);
	}

	@SuppressWarnings("deprecation")
	@Override
	public @NotNull RenderShape getRenderShape(@NotNull BlockState pState) {
		return RenderShape.MODEL;
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, OPEN, HINGE, HALF, DOUBLE);
	}

	@SuppressWarnings("deprecation")
	@Override
	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		return !pState.getValue(OPEN) ? Shapes.block() : switch (pState.getValue(FACING)) {
			case NORTH -> SHAPE_NORTH_OPEN;
			case SOUTH -> SHAPE_SOUTH_OPEN;
			case WEST -> SHAPE_WEST_OPEN;
			case EAST -> SHAPE_EAST_OPEN;
			default -> throw new IllegalStateException("Block %s has an invalid state property: %s=%s".formatted(
					pState.toString(), FACING.getName(), pState.getValue(FACING)
			));
		};
	}

	@SuppressWarnings("deprecation")
	@Override
	public BlockState rotate(BlockState pState, Rotation pRotation) {
		return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
	}

	@Override
	@SuppressWarnings("deprecation")
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}

	/**
	 * Borrowed from DoorBlock#getStateForPlacement
	 */
	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		BlockPos blockpos = pContext.getClickedPos();
		Level level = pContext.getLevel();
		if (blockpos.getY() < level.getMaxBuildHeight() - 1 && level.getBlockState(blockpos.above()).canBeReplaced(pContext)) {
			return this.defaultBlockState()
					.setValue(FACING, pContext.getHorizontalDirection().getOpposite())
					.setValue(OPEN, false)
					.setValue(HINGE, this.getHinge(pContext))
					.setValue(HALF, DoubleBlockHalf.LOWER)
					.setValue(DOUBLE, false);
		} else {
			return null;
		}
	}

	public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, LivingEntity pPlacer, ItemStack pStack) {
		pLevel.setBlock(pPos.above(), pState.setValue(HALF, DoubleBlockHalf.UPPER), 3);
	}

	public void playerWillDestroy(Level pLevel, BlockPos pPos, BlockState pState, Player pPlayer) {
		if (!pLevel.isClientSide && pPlayer.isCreative()) {
			preventCreativeDropFromBottomPart(pLevel, pPos, pState, pPlayer);
		}
		super.playerWillDestroy(pLevel, pPos, pState, pPlayer);
	}

	/**
	 * Borrowed from DoorBlock#updateShape
	 */
	@SuppressWarnings("deprecation")
	public BlockState updateShape(
			BlockState pState,
			Direction pFacing,
			BlockState pFacingState,
			LevelAccessor pLevel,
			BlockPos pCurrentPos,
			BlockPos pFacingPos) {
		DoubleBlockHalf currentHalf = pState.getValue(HALF);
		if (pFacing.getAxis() == Direction.Axis.Y && currentHalf == DoubleBlockHalf.LOWER == (pFacing == Direction.UP)) {
			// vertical direction and from a place supposed to be a counterpart
			if (pFacingState.is(this) && pFacingState.getValue(HALF) != currentHalf) {
				// the neighbor is the same block and of a different half (actually a counterpart)
				return pState.setValue(FACING, pFacingState.getValue(FACING))
						.setValue(OPEN, pFacingState.getValue(OPEN))
						.setValue(HINGE, pFacingState.getValue(HINGE))
						.setValue(DOUBLE, pFacingState.getValue(DOUBLE));
			} else {
				// the neighbor is a different block or of the same half (not actually a counterpart)
				return Blocks.AIR.defaultBlockState();
			}
		} else {
			// this is a lower part and not supported by the ground
			return super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
		}
	}

	@Override
	public DoubleBlockCombiner.NeighborCombineResult<? extends ChestBlockEntity> combine(
			BlockState pState,
			Level pLevel,
			BlockPos pPos,
			boolean pOverride) {
		return DoubleBlockCombiner.Combiner::acceptNone;
	}

	@Override
	public boolean propagatesSkylightDown(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
		return pState.getValue(HALF) == DoubleBlockHalf.UPPER;
	}

	@SuppressWarnings("deprecation")
	@Override
	public int getLightBlock(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
		return this.propagatesSkylightDown(pState, pLevel, pPos) ? 0 : 15;
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return null;
	}

	/**
	 * Borrowed from DoorBlock::getHinge
	 */
	private DoorHingeSide getHinge(BlockPlaceContext pContext) {
		BlockPos clickPos = pContext.getClickedPos();
		Direction facing = pContext.getHorizontalDirection();
		int stepX = facing.getStepX();
		int stepZ = facing.getStepZ();
		Vec3 clickLocation = pContext.getClickLocation();
		double xDiff = clickLocation.x - (double) clickPos.getX();
		double zDiff = clickLocation.z - (double) clickPos.getZ();
		if ((stepX >= 0 || !(zDiff < 0.5D)) && (stepX <= 0 || !(zDiff > 0.5D)) && (stepZ >= 0 || !(xDiff > 0.5D)) &&
				(stepZ <= 0 || !(xDiff < 0.5D))) {
			return DoorHingeSide.LEFT;
		} else {
			return DoorHingeSide.RIGHT;
		}
	}

	/**
	 * Borrowed from DoublePlantBlock#preventCreativeDropFromBottomPart
	 */
	public static void preventCreativeDropFromBottomPart(Level pLevel, BlockPos pPos, BlockState pState, Player pPlayer) {
		DoubleBlockHalf doubleblockhalf = pState.getValue(HALF);
		if (doubleblockhalf == DoubleBlockHalf.UPPER) {
			BlockPos blockpos = pPos.below();
			BlockState blockstate = pLevel.getBlockState(blockpos);
			if (blockstate.is(pState.getBlock()) && blockstate.getValue(HALF) == DoubleBlockHalf.LOWER) {
				BlockState blockstate1 = blockstate.hasProperty(BlockStateProperties.WATERLOGGED) && blockstate.getValue(
						BlockStateProperties.WATERLOGGED) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
				pLevel.setBlock(blockpos, blockstate1, 35);
				pLevel.levelEvent(pPlayer, 2001, blockpos, Block.getId(blockstate));
			}
		}
	}
}
