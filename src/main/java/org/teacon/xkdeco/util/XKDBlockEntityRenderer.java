package org.teacon.xkdeco.util;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;

public interface XKDBlockEntityRenderer<T extends BlockEntity> {
	AABB xkdeco$getRenderBoundingBox(T blockEntity);
}
