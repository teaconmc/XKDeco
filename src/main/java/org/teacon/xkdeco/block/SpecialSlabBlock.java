package org.teacon.xkdeco.block;

import java.util.function.Supplier;

import org.teacon.xkdeco.XKDeco;

import com.google.common.base.Suppliers;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.NyliumBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SpreadingSnowyDirtBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class SpecialSlabBlock extends SlabBlock {
	private static final VoxelShape PATH_TOP_AABB = Block.box(0, 8, 0, 16, 15, 16);
	private static final VoxelShape PATH_BOTTOM_AABB = Block.box(0, 0, 0, 16, 7, 16);
	private static final VoxelShape PATH_DOUBLE_AABB = Block.box(0, 0, 0, 16, 15, 16);
	private static final Supplier<Block> DIRT_SLAB = Suppliers.memoize(() -> BuiltInRegistries.BLOCK.get(XKDeco.id("dirt_slab")));
	private static final Supplier<Block> NETHERRACK_SLAB = Suppliers.memoize(() -> BuiltInRegistries.BLOCK.get(XKDeco.id("netherrack_slab")));

	protected final Type type;

	public SpecialSlabBlock(Properties properties, Type type) {
		super(properties);
		this.type = type;
	}

	@Override
	public boolean useShapeForLightOcclusion(BlockState state) {
		return type == Type.PATH || super.useShapeForLightOcclusion(state);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		if (type == Type.PATH) {
			return switch (state.getValue(TYPE)) {
				case TOP -> PATH_TOP_AABB;
				case BOTTOM -> PATH_BOTTOM_AABB;
				case DOUBLE -> PATH_DOUBLE_AABB;
			};
		}
		return super.getShape(state, level, pos, context);
	}

	@Override
	public BlockState updateShape(
			BlockState state, Direction facing,
			BlockState facingState, LevelAccessor world, BlockPos pos, BlockPos facingPos) {
		if (type == Type.PATH && facing == Direction.UP && !state.canSurvive(world, pos)) {
			world.scheduleTick(pos, this, 1);
		}
		return super.updateShape(state, facing, facingState, world, pos, facingPos);
	}

	@Override
	public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
		if (type == Type.PATH) {
			BlockState newState = turnToAnotherSlab(DIRT_SLAB, pState, pLevel, pPos);
			pushEntitiesUp(pState, Blocks.DIRT.defaultBlockState(), pLevel, pPos);
			pLevel.gameEvent(GameEvent.BLOCK_CHANGE, pPos, GameEvent.Context.of(null, newState));
		} else if (type == Type.DIRT) {
			// Mojang's code is shitty and only supports full block.
			if (pState.getValue(TYPE) == SlabType.BOTTOM) {
				if (pState.getValue(WATERLOGGED)) {
					turnToAnotherSlab(DIRT_SLAB, pState, pLevel, pPos);
				}
			} else if (!SpreadingSnowyDirtBlock.canBeGrass(pState.setValue(TYPE, SlabType.DOUBLE), pLevel, pPos)) {
				turnToAnotherSlab(DIRT_SLAB, pState, pLevel, pPos);
			}
		} else if (type == Type.NYLIUM) {
			if (pState.getValue(TYPE) != SlabType.BOTTOM && !NyliumBlock.canBeNylium(
					pState.setValue(TYPE, SlabType.DOUBLE),
					pLevel,
					pPos)) {
				turnToAnotherSlab(NETHERRACK_SLAB, pState, pLevel, pPos);
			}
		}
	}

	private BlockState turnToAnotherSlab(Supplier<Block> blockSupplier, BlockState original, ServerLevel world, BlockPos pos) {
		if (!(original.getBlock() instanceof SlabBlock)) {
			return original;
		}
		BlockState state = blockSupplier.get().defaultBlockState()
				.setValue(TYPE, original.getValue(TYPE))
				.setValue(WATERLOGGED, original.getValue(WATERLOGGED));
		world.setBlockAndUpdate(pos, state);
		return state;
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
		if (type == Type.PATH && state.getValue(TYPE) != SlabType.BOTTOM) {
			var aboveState = world.getBlockState(pos.above());
			return !aboveState.isSolid() || aboveState.getBlock() instanceof FenceGateBlock;
		}
		return true;
	}

	public enum Type {
		DIRT, PATH, NYLIUM
	}

}
