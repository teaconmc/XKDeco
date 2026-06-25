package org.teacon.xkdeco.client.renderer;

import org.teacon.xkdeco.blockentity.HologramBlockEntity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import org.teacon.xkdeco.util.NotNullByDefault;

@NotNullByDefault
public class HologramRenderer implements BlockEntityRenderer<HologramBlockEntity, HologramRenderState> {
	private final ItemModelResolver itemModelResolver;

	public HologramRenderer(BlockEntityRendererProvider.Context context) {
		this.itemModelResolver = context.itemModelResolver();
	}

	@Override
	public HologramRenderState createRenderState() {
		return new HologramRenderState();
	}

	@Override
	public void extractRenderState(
			HologramBlockEntity blockEntity,
			HologramRenderState state,
			float partialTicks,
			Vec3 cameraPosition,
			ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
		BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
		BlockState blockState = blockEntity.getBlockState();
		ItemStack itemStack = blockState.getBlock().asItem().getDefaultInstance();
		this.itemModelResolver.updateForTopItem(
				state.item,
				itemStack,
				ItemDisplayContext.FIXED,
				blockEntity.getLevel(),
				null,
				blockEntity.getBlockPos().hashCode());
		if (blockState.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
			state.rotation = blockState.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot();
		} else {
			state.rotation = 180F;
		}
	}

	@Override
	public void submit(
			HologramRenderState state,
			PoseStack poseStack,
			SubmitNodeCollector submitNodeCollector,
			CameraRenderState camera) {
		if (state.item.isEmpty()) {
			return;
		}
		// hologram is always rendered (near) full-bright; preserve original packed value
		int packedLight = 15728850;
		poseStack.pushPose();
		poseStack.translate(0.5F, 0F, 0.5F);
		poseStack.mulPose(Axis.XP.rotationDegrees(90F));
		poseStack.mulPose(Axis.ZP.rotationDegrees(state.rotation));
		poseStack.scale(0.5F, 0.5F, 0.5F);
		state.item.submit(poseStack, submitNodeCollector, packedLight, OverlayTexture.NO_OVERLAY, 0);
		poseStack.popPose();
	}
}
