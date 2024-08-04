/*
package org.teacon.xkdeco.block;

import java.util.Optional;

import org.teacon.xkdeco.blockentity.MimicWallBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WallSide;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import snownee.kiwi.util.NotNullByDefault;

@NotNullByDefault
public final class MimicWallBlock extends WallBlock implements EntityBlock {
	private static final VoxelShape NORTH_TEST = Block.box(7, 0, 0, 9, 16, 9);
	private static final VoxelShape SOUTH_TEST = Block.box(7, 0, 7, 9, 16, 16);
	private static final VoxelShape WEST_TEST = Block.box(0, 0, 7, 9, 16, 9);
	private static final VoxelShape EAST_TEST = Block.box(7, 0, 7, 16, 16, 9);
	public static final String MIMIC_WALL_PREFIX = "mimic/";

	public static String toMimicId(ResourceLocation original) {
		return MIMIC_WALL_PREFIX + original.getNamespace() + "/" + original.getPath();
	}

	private final WallBlock wall;

	public MimicWallBlock(WallBlock wallDelegate) {
		super(Properties.copy(wallDelegate));
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

	private BlockState updateSides(BlockPos pos, VoxelShape aboveShape, BlockState blockState, LevelAccessor level) {
		var northWall = this.connectsTo(level.getBlockState(pos.north()));
		var eastWall = this.connectsTo(level.getBlockState(pos.east()));
		var southWall = this.connectsTo(level.getBlockState(pos.south()));
		var westWall = this.connectsTo(level.getBlockState(pos.west()));
		return blockState.setValue(NORTH_WALL, this.makeWallState(northWall.isPresent(), aboveShape, NORTH_TEST)).setValue(
				EAST_WALL,
				this.makeWallState(eastWall.isPresent(), aboveShape, EAST_TEST)).setValue(
				SOUTH_WALL,
				this.makeWallState(southWall.isPresent(), aboveShape, SOUTH_TEST)).setValue(
				WEST_WALL,
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
	public BlockState updateShape(
			BlockState pState,
			Direction pFacing,
			BlockState pFacingState,
			LevelAccessor pLevel,
			BlockPos pCurrentPos,
			BlockPos pFacingPos) {
		if (pFacing != Direction.DOWN) {
			var abovePos = pCurrentPos.above();
			var aboveBlockState = pLevel.getBlockState(abovePos);
			var aboveShape = aboveBlockState.getCollisionShape(pLevel, abovePos).getFaceShape(Direction.DOWN);

			if (pLevel.getBlockEntity(pCurrentPos) instanceof MimicWallBlockEntity blockEntity) {
				blockEntity.updateBlocksFromLevel(this);
			}

			return this.updateSides(pCurrentPos, aboveShape, pState, pLevel);
		}

		return pState;
	}

	@Override
	public String getDescriptionId() {
		return this.wall.getDescriptionId();
	}

	@Override
	public MutableComponent getName() {
		return Component.translatable("block.xkdeco.mimic_wall", wall.getName());
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return new MimicWallBlockEntity(pPos, pState);
	}

	@Override
	protected void spawnDestroyParticles(Level pLevel, Player pPlayer, BlockPos pPos, BlockState pState) {
		super.spawnDestroyParticles(pLevel, pPlayer, pPos, this.wall.defaultBlockState());
	}

	@Override
	@SuppressWarnings("deprecation")
	public RenderShape getRenderShape(BlockState pState) {
		return RenderShape.ENTITYBLOCK_ANIMATED;
	}
}
*/
