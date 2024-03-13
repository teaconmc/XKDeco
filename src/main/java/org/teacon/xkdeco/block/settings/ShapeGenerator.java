package org.teacon.xkdeco.block.settings;

import java.util.Map;
import java.util.function.Predicate;

import org.teacon.xkdeco.block.XKDStateProperties;
import org.teacon.xkdeco.util.MathUtil;
import org.teacon.xkdeco.util.RoofUtil;

import com.google.common.base.Preconditions;

import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
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
}
