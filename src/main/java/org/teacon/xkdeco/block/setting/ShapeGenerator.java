package org.teacon.xkdeco.block.setting;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.teacon.xkdeco.block.XKDStateProperties;
import org.teacon.xkdeco.util.MathUtil;
import org.teacon.xkdeco.util.RoofUtil;

import com.google.common.base.Preconditions;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import snownee.kiwi.util.VoxelUtil;

public interface ShapeGenerator {
	VoxelShape getShape(BlockState blockState, CollisionContext context);

	static ShapeGenerator unit(VoxelShape shape) {
		return (blockState, context) -> shape;
	}

	static ShapeGenerator horizontal(VoxelShape north) {
		if (MathUtil.isIsotropicHorizontally(north)) {
			return unit(north);
		}
		VoxelShape[] shapes = new VoxelShape[4];
		shapes[Direction.NORTH.get2DDataValue()] = north;
		return (blockState, context) -> {
			Direction direction = blockState.getValue(BlockStateProperties.HORIZONTAL_FACING);
			int index = direction.get2DDataValue();
			VoxelShape shape = shapes[index];
			if (shape == null) {
				synchronized (shapes) {
					shape = shapes[index];
					if (shape == null) {
						shapes[index] = shape = VoxelUtil.rotateHorizontal(shapes[Direction.NORTH.get2DDataValue()], direction);
					}
				}
			}
			return shape;
		};
	}

	static ShapeGenerator directional(VoxelShape up, Function<BlockState, Direction> directionGetter) {
		if (Shapes.block() == up) {
			return unit(up);
		}
		VoxelShape[] shapes = new VoxelShape[6];
		shapes[Direction.DOWN.get3DDataValue()] = VoxelUtil.rotate(up, Direction.UP);
		return (blockState, context) -> {
			Direction direction = directionGetter.apply(blockState);
			int index = direction.get3DDataValue();
			VoxelShape shape = shapes[index];
			if (shape == null) {
				synchronized (shapes) {
					shape = shapes[index];
					if (shape == null) {
						shapes[index] = shape = VoxelUtil.rotate(shapes[Direction.DOWN.get3DDataValue()], direction);
					}
				}
			}
			return shape;
		};
	}

	static ShapeGenerator shifted(Predicate<BlockState> predicate, ShapeGenerator trueGenerator, ShapeGenerator falseGenerator) {
		return (blockState, context) ->
				predicate.test(blockState) ?
						trueGenerator.getShape(blockState, context) :
						falseGenerator.getShape(blockState, context);
	}

	static <T extends Enum<T> & StringRepresentable> ShapeGenerator shifted(EnumProperty<T> property, Map<T, ShapeGenerator> map) {
		return (blockState, context) -> map.get(blockState.getValue(property)).getShape(blockState, context);
	}

	static ShapeGenerator horizontalShifted(VoxelShape trueNorth, VoxelShape falseNorth) {
		return shifted(
				state -> state.getValue(XKDStateProperties.ROOF_HALF) == RoofUtil.RoofHalf.BASE,
				horizontal(trueNorth),
				horizontal(falseNorth));
	}

	static ShapeGenerator sixWay(VoxelShape base, VoxelShape trueDown, VoxelShape falseDown) {
		Preconditions.checkArgument(!trueDown.isEmpty() || !falseDown.isEmpty());
		VoxelShape[] shapes = new VoxelShape[64]; // 2^6
		return (blockState, context) -> {
			int index = 0;
			for (int i = 0; i < 6; i++) {
				if (blockState.getValue(XKDStateProperties.DIRECTION_PROPERTIES.get(i))) {
					index |= 1 << i;
				}
			}
			VoxelShape shape = shapes[index];
			if (shape == null) {
				synchronized (shapes) {
					shape = shapes[index];
					if (shape == null) {
						shape = base;
						for (int i = 0; i < 6; i++) {
							var sideShape = VoxelUtil.rotate(
									(index & (1 << i)) != 0 ? trueDown : falseDown,
									Direction.from3DDataValue(i));
							shape = Shapes.joinUnoptimized(shape, sideShape, BooleanOp.OR);
						}
						shapes[index] = shape = shape.optimize();
					}
				}
			}
			return shape;
		};
	}

	static ShapeGenerator faceAttached(VoxelShape floor, VoxelShape ceiling, VoxelShape wall) {
		return shifted(
				BlockStateProperties.ATTACH_FACE,
				Map.of(AttachFace.FLOOR, horizontal(floor), AttachFace.CEILING, horizontal(ceiling), AttachFace.WALL, horizontal(wall)));
	}

	static ShapeGenerator moulding(VoxelShape northStraight) {
		VoxelShape northInner = Shapes.or(northStraight, VoxelUtil.rotateHorizontal(northStraight, Direction.NORTH.getClockWise()));
		VoxelShape northOuter = Shapes.join(
				northStraight,
				VoxelUtil.rotateHorizontal(northStraight, Direction.NORTH.getClockWise()),
				BooleanOp.AND);
		VoxelShape[] shapes = Stream.of(northStraight, northInner, northOuter)
				.flatMap($ -> Direction.Plane.HORIZONTAL.stream().map(direction -> VoxelUtil.rotateHorizontal($, direction)))
				.toArray(VoxelShape[]::new);
		VoxelShape[] mappedShapes = new VoxelShape[5 * 4];
		for (int i = 0; i < mappedShapes.length; i++) {
			mappedShapes[i] = shapes[Moulding.mapping[i]];
		}
		return new Moulding(mappedShapes);
	}

	static ShapeGenerator layered(LayeredComponent component, ResourceLocation shapeId) {
		IntegerProperty property = component.getLayerProperty();
		int min = property.min;
		int max = property.max;
		VoxelShape[] shapes = new VoxelShape[max - min + 1];
		for (int i = min; i <= max; i++) {
			shapes[i - min] = ShapeStorage.getInstance().get(shapeId.withSuffix("_" + i));
		}
		return (blockState, context) -> shapes[blockState.getValue(property) - property.min];
	}

	final class Moulding implements ShapeGenerator {
		private static final int[] mapping = new int[5 * 4];

		static {
			prepareMapping();
		}

		private static void prepareMapping() {
			StairsShape[] stairsShapes = StairsShape.values();
			for (int i = 0; i < 5; i++) {
				StairsShape stairsShape = stairsShapes[i];
				int stairsShapeIndex = switch (stairsShape) {
					case STRAIGHT -> 0;
					case INNER_LEFT, INNER_RIGHT -> 1;
					case OUTER_LEFT, OUTER_RIGHT -> 2;
				};
				int rotationOffset = switch (stairsShape) {
					case INNER_LEFT, OUTER_LEFT -> 0;
					default -> 1;
				};
				for (int j = 0; j < 4; j++) {
					mapping[i * 4 + j] = stairsShapeIndex * 4 + (j + rotationOffset) % 4;
				}
			}
		}

		private final VoxelShape[] shapes;

		private Moulding(VoxelShape[] shapes) {
			this.shapes = shapes;
		}

		@Override
		public VoxelShape getShape(BlockState blockState, CollisionContext context) {
			int shape = blockState.getValue(BlockStateProperties.STAIRS_SHAPE).ordinal();
			int facing = blockState.getValue(BlockStateProperties.HORIZONTAL_FACING).get2DDataValue();
			return shapes[shape * 4 + facing];
		}
	}
}
