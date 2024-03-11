package org.teacon.xkdeco.block.settings;

import java.util.Map;

import com.google.common.collect.Maps;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;

public interface CanSurviveHandler {
	boolean isSensitiveSide(BlockState state, Direction side);

	boolean canSurvive(BlockState state, LevelReader world, BlockPos pos);

	static CanSurviveHandler checkFloor() {
		return Impls.CHECK_FLOOR;
	}

	static CanSurviveHandler checkCeiling() {
		return Impls.CHECK_CEILING;
	}

	static CanSurviveHandler checkFace(Block block) {
		for (Property<?> property : block.getStateDefinition().getProperties()) {
			if (property instanceof DirectionProperty) {
				return Impls.CHECK_FACE.computeIfAbsent((DirectionProperty) property, key -> new CanSurviveHandler() {
					@Override
					public boolean isSensitiveSide(BlockState state, Direction side) {
						return side == state.getValue(key).getOpposite();
					}

					@Override
					public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
						Direction direction = state.getValue(key);
						BlockPos neighbor = pos.relative(direction);
						return world.getBlockState(neighbor).isFaceSturdy(world, neighbor, direction.getOpposite(), SupportType.RIGID);
					}
				});
			}
		}
		throw new IllegalStateException("No direction property found for block " + block);
	}

	final class Impls {
		private Impls() {
		}

		private static final CanSurviveHandler CHECK_FLOOR = new CanSurviveHandler() {
			@Override
			public boolean isSensitiveSide(BlockState state, Direction side) {
				return side == Direction.DOWN;
			}

			@Override
			public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
				return Block.canSupportRigidBlock(world, pos.below());
			}
		};

		private static final CanSurviveHandler CHECK_CEILING = new CanSurviveHandler() {
			@Override
			public boolean isSensitiveSide(BlockState state, Direction side) {
				return side == Direction.UP;
			}

			@Override
			public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
				pos = pos.above();
				return world.getBlockState(pos).isFaceSturdy(world, pos, Direction.DOWN, SupportType.RIGID);
			}
		};

		private static final Map<DirectionProperty, CanSurviveHandler> CHECK_FACE = Maps.newHashMap();
	}
}
