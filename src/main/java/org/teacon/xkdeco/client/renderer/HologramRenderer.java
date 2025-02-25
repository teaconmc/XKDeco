package org.teacon.xkdeco.client.renderer;

import org.teacon.xkdeco.blockentity.HologramBlockEntity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import snownee.kiwi.util.NotNullByDefault;

@NotNullByDefault
public class HologramRenderer implements BlockEntityRenderer<HologramBlockEntity> {
	private final ItemRenderer itemRenderer;

	public HologramRenderer(BlockEntityRendererProvider.Context context) {
		itemRenderer = context.getItemRenderer();
	}

	@Override
	public void render(
			HologramBlockEntity blockEntity,
			float partialTick,
			PoseStack poseStack,
			MultiBufferSource bufferSource,
			int packedLight,
			int packedOverlay) {
		packedLight = 15728850;
		BlockState blockState = blockEntity.getBlockState();
		ItemStack itemStack = blockState.getBlock().asItem().getDefaultInstance();
		poseStack.pushPose();
		poseStack.translate(0.5F, 0F, 0.5F);
		poseStack.mulPose(Axis.XP.rotationDegrees(90F));
		float rotation;
		if (blockState.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
			rotation = blockState.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot();
		} else {
			rotation = 180F;
		}
		poseStack.mulPose(Axis.ZP.rotationDegrees(rotation));
		poseStack.scale(0.5F, 0.5F, 0.5F);
		itemRenderer.renderStatic(
				itemStack,
				ItemDisplayContext.FIXED,
				packedLight,
				OverlayTexture.NO_OVERLAY,
				poseStack,
				bufferSource,
				blockEntity.getLevel(),
				blockEntity.getBlockPos().hashCode());
		poseStack.popPose();
	}
}
