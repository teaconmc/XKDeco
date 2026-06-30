package org.teacon.xkdeco.client.renderer;

import org.teacon.xkdeco.blockentity.ItemDisplayBlockEntity;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ItemProjectorRenderer extends ItemDisplayRenderer {
	public ItemProjectorRenderer(BlockEntityRendererProvider.Context context) {
		super(context);
	}

	@Override
	public boolean shouldRender(ItemDisplayBlockEntity blockEntity, Vec3 cameraPosition) {
		return true;
	}

	@Override
	public boolean shouldRenderOffScreen() {
		return true;
	}

	@Override
	public AABB xkdeco$getRenderBoundingBox(ItemDisplayBlockEntity be) {
		return AABB.ofSize(Vec3.atBottomCenterOf(be.getBlockPos().above(9)), 16, 16, 16);
	}
}
