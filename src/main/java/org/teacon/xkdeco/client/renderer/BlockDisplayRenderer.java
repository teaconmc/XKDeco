package org.teacon.xkdeco.client.renderer;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import org.teacon.xkdeco.blockentity.BlockDisplayBlockEntity;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockModelResolver;
import net.minecraft.client.renderer.block.model.BlockDisplayContext;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;
import org.teacon.xkdeco.util.NotNullByDefault;

@NotNullByDefault
public final class BlockDisplayRenderer implements BlockEntityRenderer<BlockDisplayBlockEntity, BlockDisplayRenderState> {
	private static final BlockDisplayContext BLOCK_DISPLAY_CONTEXT = BlockDisplayContext.create();
	private static final float BLOCK_SCALE = 0.99f;
	private final BlockModelResolver blockModelResolver;

	public BlockDisplayRenderer(BlockEntityRendererProvider.Context context) {
		this.blockModelResolver = context.blockModelResolver();
	}

	@Override
	public AABB getRenderBoundingBox(BlockDisplayBlockEntity be) {
		return AABB.unitCubeFromLowerCorner(Vec3.atLowerCornerOf(be.getBlockPos().above()));
	}

	@Override
	public BlockDisplayRenderState createRenderState() {
		return new BlockDisplayRenderState();
	}

	@Override
	public void extractRenderState(
			BlockDisplayBlockEntity blockEntity,
			BlockDisplayRenderState state,
			float partialTicks,
			Vec3 cameraPosition,
			ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
		BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
		BlockState storedState = blockEntity.getStoredBlockState();
		state.hasBlock = !storedState.isAir();
		if (state.hasBlock) {
			this.blockModelResolver.update(state.blockModel, storedState, BLOCK_DISPLAY_CONTEXT);
			Level level = blockEntity.getLevel();
			BlockPos above = blockEntity.getBlockPos().above();
			state.displayLightCoords = level != null
					? LightCoordsUtil.pack(
					level.getBrightness(LightLayer.BLOCK, above),
					level.getBrightness(LightLayer.SKY, above))
					: state.lightCoords;
		}
	}

	@Override
	public void submit(
			BlockDisplayRenderState state,
			PoseStack poseStack,
			SubmitNodeCollector submitNodeCollector,
			CameraRenderState camera) {
		if (!state.hasBlock || state.blockModel.isEmpty()) {
			return;
		}
		poseStack.pushPose();
		poseStack.scale(BLOCK_SCALE, BLOCK_SCALE, BLOCK_SCALE);
		var delta = (1 - BLOCK_SCALE) / 2;
		poseStack.translate(delta, 1, delta);
		state.blockModel.submit(
				poseStack,
				submitNodeCollector,
				state.displayLightCoords,
				OverlayTexture.NO_OVERLAY,
				0);
		poseStack.popPose();
	}
}
