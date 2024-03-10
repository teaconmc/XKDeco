package org.teacon.xkdeco.util;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public final class MathUtil {
	public static final double TAU = Math.PI * 2;

	public static boolean containsInclusive(AABB boundingBox, Vec3 vec) {
		return containsInclusive(boundingBox, vec.x(), vec.y(), vec.z());
	}

	public static boolean containsInclusive(AABB boundingBox, double x, double y, double z) {
		return x >= boundingBox.minX && x <= boundingBox.maxX
				&& y >= boundingBox.minY && y <= boundingBox.maxY
				&& z >= boundingBox.minZ && z <= boundingBox.maxZ;
	}

	public static boolean isIsotropicHorizontally(AABB aabb) {
		return aabb.minX == aabb.minZ && aabb.maxX == aabb.maxZ && aabb.minX == 1 - aabb.maxX;
	}

	public static boolean isIsotropicHorizontally(VoxelShape shape) {
		if (shape.isEmpty() || shape == Shapes.block()) {
			return true;
		}
		return shape.toAabbs().stream().allMatch(MathUtil::isIsotropicHorizontally);
	}
}
