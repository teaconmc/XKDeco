package org.teacon.xkdeco.util;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import snownee.kiwi.util.NotNullByDefault;

@NotNullByDefault
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
}
