package org.teacon.xkdeco.block;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.WallSide;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import snownee.kiwi.block.IKiwiBlock;

public final class MimicWallBlock extends WallBlock implements IKiwiBlock {
	private static final VoxelShape NORTH_TEST = Block.box(7, 0, 0, 9, 16, 9);
	private static final VoxelShape SOUTH_TEST = Block.box(7, 0, 7, 9, 16, 16);
	private static final VoxelShape WEST_TEST = Block.box(0, 0, 7, 9, 16, 9);
	private static final VoxelShape EAST_TEST = Block.box(7, 0, 7, 16, 16, 9);
	public static final String ID_TEMPLATE = "mimic/%s/%s";

	public static String toMimicId(Identifier original) {
		return ID_TEMPLATE.formatted(original.getNamespace(), original.getPath());
	}

	private final WallBlock wall;
	private final Cache<BlockState, BlockState> delegateLookup = CacheBuilder.newBuilder().expireAfterAccess(3, TimeUnit.MINUTES).build();

	public MimicWallBlock(WallBlock wallDelegate, ResourceKey<Block> blockId) {
		// 26.1 requires an id on the Properties (ofFullCopy does not copy it); mimic walls are
		// registered dynamically, so the id is threaded in from MimicWallsLoader.
		super(Properties.ofFullCopy(wallDelegate).setId(blockId));
		this.wall = wallDelegate;
	}

	public WallBlock getWallDelegate() {
		return this.wall;
	}

	public Optional<Block> connectsTo(BlockState pState) {
		if (pState.is(BlockTags.WALLS)) {
			var block = pState.getBlock();
			if (!(block instanceof MimicWallBlock)) {
				return Optional.of(block);
			}
		}
		return Optional.empty();
	}

	private WallSide makeWallState(boolean connectedTo, VoxelShape aboveShape, VoxelShape connectedToShape) {
		if (!connectedTo) {
			return WallSide.NONE;
		}
		if (Shapes.joinIsNotEmpty(connectedToShape, aboveShape, BooleanOp.ONLY_FIRST)) {
			return WallSide.LOW;
		}
		return WallSide.TALL;
	}

	private BlockState updateSides(BlockPos pos, VoxelShape aboveShape, BlockState blockState, LevelReader level) {
		var northWall = this.connectsTo(level.getBlockState(pos.north()));
		var eastWall = this.connectsTo(level.getBlockState(pos.east()));
		var southWall = this.connectsTo(level.getBlockState(pos.south()));
		var westWall = this.connectsTo(level.getBlockState(pos.west()));
		return blockState.setValue(BlockStateProperties.NORTH_WALL, this.makeWallState(northWall.isPresent(), aboveShape, NORTH_TEST)).setValue(
				BlockStateProperties.EAST_WALL,
				this.makeWallState(eastWall.isPresent(), aboveShape, EAST_TEST)).setValue(
				BlockStateProperties.SOUTH_WALL,
				this.makeWallState(southWall.isPresent(), aboveShape, SOUTH_TEST)).setValue(
				BlockStateProperties.WEST_WALL,
				this.makeWallState(westWall.isPresent(), aboveShape, WEST_TEST));
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		var pos = pContext.getClickedPos();

		var abovePos = pos.above();
		var aboveBlockState = pContext.getLevel().getBlockState(abovePos);
		var aboveShape = aboveBlockState.getCollisionShape(pContext.getLevel(), abovePos).getFaceShape(Direction.DOWN);

		var fluidState = pContext.getLevel().getFluidState(pos);
		var blockState = this.defaultBlockState().setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);

		return this.updateSides(pos, aboveShape, blockState, pContext.getLevel()).setValue(UP, true);
	}

	@Override
	protected BlockState updateShape(
			BlockState pState,
			LevelReader pLevel,
			ScheduledTickAccess pTicks,
			BlockPos pCurrentPos,
			Direction pFacing,
			BlockPos pFacingPos,
			BlockState pFacingState,
			RandomSource pRandom) {
		if (pFacing != Direction.DOWN) {
			var abovePos = pCurrentPos.above();
			var aboveBlockState = pLevel.getBlockState(abovePos);
			var aboveShape = aboveBlockState.getCollisionShape(pLevel, abovePos).getFaceShape(Direction.DOWN);
			return this.updateSides(pCurrentPos, aboveShape, pState, pLevel);
		}

		return pState;
	}

	@Override
	public MutableComponent getName() {
		return Component.translatable("block.xkdeco.mimic_wall", wall.getName());
	}

	@Override
	public MutableComponent getName(ItemStack stack) {
		return getName();
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		try {
			return lookupDelegate(state).getShape(level, pos, context);
		} catch (Exception ignored) {
		}
		return super.getShape(state, level, pos, context);
	}

	@Override
	protected VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		try {
			return lookupDelegate(state).getVisualShape(level, pos, context);
		} catch (Exception ignored) {
		}
		return super.getVisualShape(state, level, pos, context);
	}

	@Override
	protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		try {
			return lookupDelegate(state).getCollisionShape(level, pos, context);
		} catch (Exception ignored) {
		}
		return super.getCollisionShape(state, level, pos, context);
	}

	@Override
	protected VoxelShape getInteractionShape(BlockState state, BlockGetter level, BlockPos pos) {
		try {
			return lookupDelegate(state).getInteractionShape(level, pos);
		} catch (Exception ignored) {
		}
		return super.getInteractionShape(state, level, pos);
	}

	@Override
	protected VoxelShape getOcclusionShape(BlockState state) {
		try {
			VoxelShape shape = lookupDelegate(state).getOcclusionShape();
			if (shape != null) {
				return shape;
			}
		} catch (Exception ignored) {
		}
		return super.getOcclusionShape(state);
	}

	private BlockState lookupDelegate(BlockState state) {
		try {
			return this.delegateLookup.get(
					state.setValue(WATERLOGGED, false), () -> wall.defaultBlockState()
							.setValue(BlockStateProperties.NORTH_WALL, state.getValue(BlockStateProperties.NORTH_WALL))
							.setValue(BlockStateProperties.EAST_WALL, state.getValue(BlockStateProperties.EAST_WALL))
							.setValue(BlockStateProperties.SOUTH_WALL, state.getValue(BlockStateProperties.SOUTH_WALL))
							.setValue(BlockStateProperties.WEST_WALL, state.getValue(BlockStateProperties.WEST_WALL))
							.setValue(UP, state.getValue(UP)));
		} catch (ExecutionException e) {
			return wall.defaultBlockState();
		}
	}
}
