package org.teacon.xkdeco.client.model;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.renderer.block.BlockAndTintGetter;
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
import org.teacon.xkdeco.util.NotNullByDefault;

/**
 * 26.1 (NeoForge) port of the mimic wall model.
 *
 * <p>This is a {@link BlockStateModel} rather than the old Fabric {@code ForwardingBakedModel}.
 * The new render pipeline does not bake quads up-front; instead a {@code BlockStateModel}
 * produces {@link BlockStateModelPart}s on demand via the NeoForge level-and-pos aware
 * {@link #collectParts(BlockAndTintGetter, BlockPos, BlockState, RandomSource, List)}.
 *
 * <p>Behavior preserved from the old {@code MimicWallBakedModel}: render the delegate wall
 * geometry for the current state, then for each of the four horizontal directions, if the
 * neighbor is a {@link WallBlock} in {@link BlockTags#WALLS} whose {@link WallSide} toward
 * this block is not {@link WallSide#NONE}, also render that neighbor's connecting arm using
 * the neighbor's own baked model (neighbor default state with UP=false and only the
 * facing-back wall side set). This produces the cross-material seamless connection.
 *
 * <p>The delegate model and the neighbor models are all resolved from the baked
 * {@code BlockState -> BlockStateModel} map. The delegate model is captured at bind time
 * (it never changes for a given mimic state); neighbor models are looked up lazily through
 * {@code neighborModelLookup} because they depend on the surrounding world state.
 */
@NotNullByDefault
public final class MimicWallModel implements BlockStateModel {
	/**
	 * Wall-side properties indexed by {@link Direction#get2DDataValue()}
	 * (0=south, 1=west, 2=north, 3=east).
	 */
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

	/** The wall delegate this mimic imitates (used to build neighbor-arm states). */
	private final WallBlock base;
	/** Baked model for the delegate wall state equivalent to the current mimic state. */
	private final BlockStateModel delegate;
	/** Resolves a baked block-state model for any block state (the baked map lookup). */
	private final Function<BlockState, BlockStateModel> neighborModelLookup;

	public MimicWallModel(WallBlock base, BlockStateModel delegate, Function<BlockState, BlockStateModel> neighborModelLookup) {
		this.base = base;
		this.delegate = delegate;
		this.neighborModelLookup = neighborModelLookup;
	}

	@Override
	public void collectParts(BlockAndTintGetter level, BlockPos pos, BlockState state, RandomSource random, List<BlockStateModelPart> parts) {
		// Own delegate-wall geometry for the current state.
		long seed = random.nextLong();
		random.setSeed(seed);
		this.delegate.collectParts(level, pos, state, random, parts);

		// Cross-material connecting arms from neighboring walls.
		BlockPos.MutableBlockPos mutable = pos.mutable();
		for (Direction direction : Direction.Plane.HORIZONTAL) {
			BlockState neighborState = level.getBlockState(mutable.setWithOffset(pos, direction));
			BlockState armState = neighborArmState(neighborState, direction);
			if (armState != null) {
				BlockStateModel neighborModel = this.neighborModelLookup.apply(armState);
				random.setSeed(seed);
				neighborModel.collectParts(level, pos, armState, random, parts);
			}
		}
	}

	/**
	 * Computes the neighbor connecting-arm state to render for {@code direction}, or {@code null}
	 * if no arm should be drawn. The arm is the neighbor's default state with UP=false and only the
	 * wall side facing back toward this block set to the neighbor's matching wall side.
	 */
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
		Object delegateKey = this.delegate.createGeometryKey(level, pos, state, random);
		if (delegateKey == null) {
			return null;
		}
		BlockPos.MutableBlockPos mutable = pos.mutable();
		Object[] neighborKeys = new Object[4];
		for (Direction direction : Direction.Plane.HORIZONTAL) {
			BlockState neighborState = level.getBlockState(mutable.setWithOffset(pos, direction));
			BlockState armState = neighborArmState(neighborState, direction);
			if (armState != null) {
				Object neighborKey = this.neighborModelLookup.apply(armState).createGeometryKey(level, pos, armState, random);
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

	// --- Deprecated, level-unaware overloads. Forward to the delegate so the model still
	// --- behaves sanely if something calls the legacy path. The mimic-specific neighbor
	// --- connection is only produced by the level-aware collectParts above.

	@Override
	@Deprecated
	public void collectParts(RandomSource random, List<BlockStateModelPart> output) {
		this.delegate.collectParts(random, output);
	}

	@Override
	@Deprecated
	public Material.Baked particleMaterial() {
		return this.delegate.particleMaterial();
	}

	@Override
	@Deprecated
	@BakedQuad.MaterialFlags
	public int materialFlags() {
		return this.delegate.materialFlags();
	}

	@Override
	public Material.Baked particleMaterial(BlockAndTintGetter level, BlockPos pos, BlockState state) {
		return this.delegate.particleMaterial(level, pos, state);
	}

	@Override
	@BakedQuad.MaterialFlags
	public int materialFlags(BlockAndTintGetter level, BlockPos pos, BlockState state) {
		return this.delegate.materialFlags(level, pos, state);
	}

	/** Exposes the wall delegate; unused internally but handy for integrators / debugging. */
	public WallBlock base() {
		return this.base;
	}
}
