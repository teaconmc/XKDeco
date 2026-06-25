package org.teacon.xkdeco.client.model;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.DynamicBlockStateModel;
import org.jspecify.annotations.Nullable;
import org.teacon.xkdeco.block.XKDStateProperties;
import org.teacon.xkdeco.util.NotNullByDefault;

/**
 * The baked, connection-driven block-state model for the air duct, ported from the 1.21.1 Fabric
 * dynamic {@code BakedModel}.
 *
 * <p>In 26.1 the part-selection logic moved from {@code getQuads} into
 * {@link #collectParts(BlockAndTintGetter, BlockPos, BlockState, RandomSource, List)}: instead of
 * merging the chosen sub-models' quads into one list, we hand the renderer the chosen
 * {@link BlockStateModelPart}s and it gathers their quads. Geometry reuse (previously a Guava cache
 * keyed by {@code (blockState, direction)}) is now driven by {@link #createGeometryKey}.
 */
@NotNullByDefault
public final class AirDuctBakedModel implements DynamicBlockStateModel {
	private final List<BlockStateModelPart> straight;
	private final List<BlockStateModelPart> corner;
	private final List<BlockStateModelPart> cover;
	private final BlockStateModelPart frame;

	public AirDuctBakedModel(
			List<BlockStateModelPart> straight,
			List<BlockStateModelPart> corner,
			List<BlockStateModelPart> cover,
			BlockStateModelPart frame) {
		this.straight = straight;
		this.corner = corner;
		this.cover = cover;
		this.frame = frame;
	}

	/**
	 * Reads the six per-direction connection booleans for the given state, in
	 * {@link Direction#from3DDataValue} order, packed into a bitmask. Returns {@code -1} when the
	 * state is null (item / fallback rendering) which maps to the first straight part.
	 */
	private static int connectionMask(@Nullable BlockState state) {
		// Null state, or a state without the air-duct direction properties (e.g. the AIR fallback
		// supplied by the no-context collectParts overload, or item rendering), maps to -1 which
		// selects the first straight part -- matching the old getQuads(null, ...) behaviour.
		if (state == null || !state.hasProperty(XKDStateProperties.DIRECTION_PROPERTIES.get(0))) {
			return -1;
		}
		int mask = 0;
		for (int i = 0; i < 6; i++) {
			if (state.getValue(XKDStateProperties.DIRECTION_PROPERTIES.get(i))) {
				mask |= 1 << i;
			}
		}
		return mask;
	}

	private void collectForMask(int mask, List<BlockStateModelPart> parts) {
		if (mask < 0) {
			parts.add(straight.getFirst());
			return;
		}
		List<Direction> trueDirections = Lists.newArrayListWithExpectedSize(6);
		for (int i = 0; i < 6; i++) {
			if ((mask & (1 << i)) != 0) {
				trueDirections.add(Direction.from3DDataValue(i));
			}
		}
		if (trueDirections.size() == 2) {
			var direction1 = trueDirections.get(0);
			var direction2 = trueDirections.get(1);
			if (direction1.getOpposite() == direction2) {
				parts.add(straight.get(direction1.getAxis().ordinal()));
			} else {
				int index;
				if (direction1 == Direction.DOWN) {
					index = 4 + direction2.getCounterClockWise().get2DDataValue();
				} else if (direction1 == Direction.UP) {
					index = 8 + direction2.getCounterClockWise().get2DDataValue();
				} else if (direction1 == Direction.SOUTH && direction2 == Direction.EAST) {
					index = 2;
				} else if (direction1.get2DDataValue() < direction2.get2DDataValue()) {
					index = direction1.getCounterClockWise().get2DDataValue();
				} else {
					index = direction2.getCounterClockWise().get2DDataValue();
				}
				parts.add(corner.get(index));
			}
			return;
		}
		parts.add(frame);
		for (int i = 0; i < 6; i++) {
			if ((mask & (1 << i)) == 0) {
				parts.add(cover.get(i));
			}
		}
	}

	@Override
	public void collectParts(
			BlockAndTintGetter level,
			BlockPos pos,
			BlockState state,
			RandomSource random,
			List<BlockStateModelPart> parts) {
		collectForMask(connectionMask(state), parts);
	}

	@Override
	@Nullable
	public Object createGeometryKey(
			BlockAndTintGetter level,
			BlockPos pos,
			BlockState state,
			RandomSource random) {
		// Geometry depends only on which of the six faces are connected. Two states with the same
		// connection mask produce identical parts, so they can share baked geometry (the old code
		// achieved this by ignoring WATERLOGGED in its cache key).
		return new GeometryKey(this, connectionMask(state));
	}

	private record GeometryKey(AirDuctBakedModel model, int mask) {
	}

	@Override
	public Material.Baked particleMaterial() {
		return straight.getFirst().particleMaterial();
	}

	@Override
	public int materialFlags() {
		int flags = 0;
		for (BlockStateModelPart part : straight) {
			flags |= part.materialFlags();
		}
		for (BlockStateModelPart part : corner) {
			flags |= part.materialFlags();
		}
		for (BlockStateModelPart part : cover) {
			flags |= part.materialFlags();
		}
		flags |= frame.materialFlags();
		return flags;
	}
}
