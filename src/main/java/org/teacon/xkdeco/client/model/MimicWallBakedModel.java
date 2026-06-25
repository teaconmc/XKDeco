package org.teacon.xkdeco.client.model;

import java.util.List;
import java.util.Objects;

import org.jspecify.annotations.Nullable;
import org.teacon.xkdeco.util.NotNullByDefault;

import net.fabricmc.fabric.api.client.renderer.v1.model.FabricBlockStateModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.BlockStateModelSet;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.WallSide;

@NotNullByDefault
public final class MimicWallBakedModel implements BlockStateModel, FabricBlockStateModel {
	private static final EnumProperty<WallSide>[] WALL_SIDE_PROPERTIES = makeWallSideProperties();

	@SuppressWarnings("unchecked")
	private static EnumProperty<WallSide>[] makeWallSideProperties() {
		EnumProperty<WallSide>[] array = new EnumProperty[4];
		array[Direction.SOUTH.get2DDataValue()] = BlockStateProperties.SOUTH_WALL;
		array[Direction.WEST.get2DDataValue()] = BlockStateProperties.WEST_WALL;
		array[Direction.NORTH.get2DDataValue()] = BlockStateProperties.NORTH_WALL;
		array[Direction.EAST.get2DDataValue()] = BlockStateProperties.EAST_WALL;
		return array;
	}

	private final WallBlock base;

	public MimicWallBakedModel(WallBlock base) {
		this.base = base;
	}

	private static BlockStateModelSet models() {
		return Minecraft.getInstance().getModelManager().getBlockStateModelSet();
	}

	private BlockState delegateState() {
		return base.defaultBlockState();
	}

	private BlockStateModel delegateModel() {
		return models().get(delegateState());
	}

	@Override
	public void collectParts(
			BlockAndTintGetter level,
			BlockPos pos,
			BlockState state,
			RandomSource random,
			List<BlockStateModelPart> parts) {
		long seed = random.nextLong();
		BlockState delegateState = delegateState();
		random.setSeed(seed);
		delegateModel().collectParts(level, pos, delegateState, random, parts);

		BlockPos.MutableBlockPos mutable = pos.mutable();
		for (Direction direction : Direction.Plane.HORIZONTAL) {
			BlockState neighborState = level.getBlockState(mutable.setWithOffset(pos, direction));
			BlockState armState = neighborArmState(neighborState, direction);
			if (armState != null) {
				random.setSeed(seed);
				models().get(armState).collectParts(level, pos, armState, random, parts);
			}
		}
	}

	@Nullable
	private static BlockState neighborArmState(BlockState neighborState, Direction direction) {
		if (neighborState.getBlock() instanceof WallBlock && neighborState.is(BlockTags.WALLS)) {
			WallSide wallSide = neighborState.getValue(WALL_SIDE_PROPERTIES[direction.get2DDataValue()]);
			if (wallSide != WallSide.NONE) {
				return neighborState.getBlock().defaultBlockState()
						.setValue(WallBlock.UP, false)
						.setValue(WALL_SIDE_PROPERTIES[direction.getOpposite().get2DDataValue()], wallSide);
			}
		}
		return null;
	}

	@Override
	@Nullable
	public Object createGeometryKey(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random) {
		BlockState delegateState = delegateState();
		Object delegateKey = delegateModel().createGeometryKey(level, pos, delegateState, random);
		if (delegateKey == null) {
			return null;
		}
		BlockPos.MutableBlockPos mutable = pos.mutable();
		Object[] neighborKeys = new Object[4];
		for (Direction direction : Direction.Plane.HORIZONTAL) {
			BlockState neighborState = level.getBlockState(mutable.setWithOffset(pos, direction));
			BlockState armState = neighborArmState(neighborState, direction);
			if (armState != null) {
				Object neighborKey = models().get(armState).createGeometryKey(level, pos, armState, random);
				if (neighborKey == null) {
					return null;
				}
				neighborKeys[direction.get2DDataValue()] = neighborKey;
			}
		}
		return new GeometryKey(delegateKey, neighborKeys[0], neighborKeys[1], neighborKeys[2], neighborKeys[3]);
	}

	private record GeometryKey(Object delegateKey, @Nullable Object south, @Nullable Object west, @Nullable Object north, @Nullable Object east) {
		@Override
		public boolean equals(Object o) {
			return o instanceof GeometryKey other
					&& this.delegateKey.equals(other.delegateKey)
					&& Objects.equals(this.south, other.south)
					&& Objects.equals(this.west, other.west)
					&& Objects.equals(this.north, other.north)
					&& Objects.equals(this.east, other.east);
		}

		@Override
		public int hashCode() {
			return Objects.hash(this.delegateKey, this.south, this.west, this.north, this.east);
		}
	}

	@Override
	@Deprecated
	public void collectParts(RandomSource random, List<BlockStateModelPart> output) {
		delegateModel().collectParts(random, output);
	}

	@Override
	@Deprecated
	public Material.Baked particleMaterial() {
		return delegateModel().particleMaterial();
	}

	@Override
	@Deprecated
	@BakedQuad.MaterialFlags
	public int materialFlags() {
		return delegateModel().materialFlags();
	}

	@Override
	public Material.Baked particleMaterial(BlockAndTintGetter level, BlockPos pos, BlockState state) {
		return delegateModel().particleMaterial(level, pos, delegateState());
	}

	@Override
	@BakedQuad.MaterialFlags
	public int materialFlags(BlockAndTintGetter level, BlockPos pos, BlockState state) {
		return delegateModel().materialFlags(level, pos, delegateState());
	}
}
