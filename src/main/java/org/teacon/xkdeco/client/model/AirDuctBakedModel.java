package org.teacon.xkdeco.client.model;

import java.util.List;

import org.jspecify.annotations.Nullable;
import org.teacon.xkdeco.block.XKDStateProperties;
import org.teacon.xkdeco.util.NotNullByDefault;

import com.google.common.collect.Lists;

import net.fabricmc.fabric.api.client.renderer.v1.model.FabricBlockStateModel;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

@NotNullByDefault
public final class AirDuctBakedModel implements BlockStateModel, FabricBlockStateModel {
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

	private static int connectionMask(@Nullable BlockState state) {
		if (state == null || !state.hasProperty(XKDStateProperties.DIRECTION_PROPERTIES.getFirst())) {
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
		return new GeometryKey(this, connectionMask(state));
	}

	private record GeometryKey(AirDuctBakedModel model, int mask) {
	}

	@Override
	@Deprecated
	public void collectParts(RandomSource random, List<BlockStateModelPart> output) {
		collectForMask(-1, output);
	}

	@Override
	@Deprecated
	public Material.Baked particleMaterial() {
		return straight.getFirst().particleMaterial();
	}

	@Override
	@Deprecated
	@BakedQuad.MaterialFlags
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
