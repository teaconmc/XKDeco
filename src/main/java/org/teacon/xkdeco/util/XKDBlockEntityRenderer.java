package org.teacon.xkdeco.util;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.client.extensions.IBlockEntityRendererExtension;

public interface XKDBlockEntityRenderer<T extends BlockEntity> extends IBlockEntityRendererExtension<T> {
	default AABB xkdeco$getRenderBoundingBox(T blockEntity) {
		return new AABB(blockEntity.getBlockPos());
	}

	@Override
	default AABB getRenderBoundingBox(T blockEntity) {
		return xkdeco$getRenderBoundingBox(blockEntity);
	}
}
