package org.teacon.xkdeco.client.renderer;

import java.util.Objects;

import org.teacon.xkdeco.blockentity.ItemDisplayBlockEntity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import snownee.kiwi.util.NotNullByDefault;

@NotNullByDefault
public final class ItemDisplayRenderer implements BlockEntityRenderer<ItemDisplayBlockEntity> {
	private final ItemRenderer itemRenderer;
	private final RandomSource random = RandomSource.create();

	public ItemDisplayRenderer(BlockEntityRendererProvider.Context context) {
		this.itemRenderer = Minecraft.getInstance().getItemRenderer();
	}

	@Override
	public boolean shouldRender(ItemDisplayBlockEntity pBlockEntity, Vec3 pCameraPos) {
		return pBlockEntity.isProjector()
				? Vec3.atCenterOf(pBlockEntity.getBlockPos()).closerThan(
				pCameraPos,
				Minecraft.getInstance().options.getEffectiveRenderDistance() * 16)
				: BlockEntityRenderer.super.shouldRender(pBlockEntity, pCameraPos);
	}

	@Override
	public AABB getRenderBoundingBox(ItemDisplayBlockEntity be) {
		if (be.isProjector()) {
			return AABB.ofSize(Vec3.atBottomCenterOf(be.getBlockPos().above(9)), 16, 16, 16);
		} else {
			return AABB.unitCubeFromLowerCorner(Vec3.atLowerCornerOf(be.getBlockPos().above()));
		}
	}

	@Override
	public void render(
			ItemDisplayBlockEntity pBlockEntity,
			float pPartialTick,
			PoseStack pPoseStack,
			MultiBufferSource pBufferSource,
			int pPackedLight,
			int pPackedOverlay) {
		// borrowed from ItemEntityRenderer

		var itemstack = pBlockEntity.getFirstItem();
		if (itemstack.isEmpty()) {
			return;
		}

		var speed = 1;
		var pos = pBlockEntity.getBlockPos();
		var spin = pBlockEntity.getSpin();
		if (!pBlockEntity.hasFixedSpin()) {
			spin += pPartialTick;
		}
		spin *= 0.05F;

		this.random.setSeed(itemstack.isEmpty() ? 187 : Item.getId(itemstack.getItem()) + itemstack.getDamageValue());

		pPoseStack.pushPose();
		var bakedmodel = this.itemRenderer.getModel(itemstack, pBlockEntity.getLevel(), null, speed);
		var gui3d = bakedmodel.isGui3d();
		var amount = this.getRenderAmount(itemstack);
		@SuppressWarnings("deprecation")
		var modelScale = bakedmodel.getTransforms().getTransform(ItemDisplayContext.GROUND).scale.y();
		pPoseStack.translate(0.5, 1 + 0.1F + 0.25 * modelScale * (pBlockEntity.isProjector() ? 24 : 1), 0.5);
		pPoseStack.mulPose(Axis.YP.rotation(spin));

		if (pBlockEntity.isProjector()) {
			pPoseStack.scale(16, 16, 16);
		}

		if (!gui3d) {
			pPoseStack.translate(
					-0.0F * (float) (amount - 1) * 0.5F,
					-0.0F * (float) (amount - 1) * 0.5F,
					-0.09375F * (float) (amount - 1) * 0.5F);
		}


		for (var k = 0; k < amount; ++k) {
			pPoseStack.pushPose();
			if (k > 0) {
				if (gui3d) {
					pPoseStack.translate(
							(this.random.nextFloat() * 2.0F - 1.0F) * 0.15F,
							(this.random.nextFloat() * 2.0F - 1.0F) * 0.15F,
							(this.random.nextFloat() * 2.0F - 1.0F) * 0.15F);
				} else {
					pPoseStack.translate(
							(this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F,
							(this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F,
							0.0D);
				}
			}

			var level = Objects.requireNonNull(pBlockEntity.getLevel());
			BlockPos above = pos.above();
			var packedLight = LightTexture.pack(
					level.getBrightness(LightLayer.BLOCK, above),
					level.getBrightness(LightLayer.SKY, above));
			this.itemRenderer.render(
					itemstack,
					ItemDisplayContext.GROUND,
					false,
					pPoseStack,
					pBufferSource,
					packedLight,
					OverlayTexture.NO_OVERLAY,
					bakedmodel);
			pPoseStack.popPose();
			if (!gui3d) {
				pPoseStack.translate(0.0, 0.0, 0.09375F);
			}
		}

		pPoseStack.popPose();
	}

	private int getRenderAmount(ItemStack pStack) {
		var i = 1;
		if (pStack.getCount() > 48) {
			i = 5;
		} else if (pStack.getCount() > 32) {
			i = 4;
		} else if (pStack.getCount() > 16) {
			i = 3;
		} else if (pStack.getCount() > 1) {
			i = 2;
		}

		return i;
	}
}
