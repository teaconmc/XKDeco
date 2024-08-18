package org.teacon.xkdeco.client.renderer;

import java.util.Objects;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import org.teacon.xkdeco.blockentity.BlockDisplayBlockEntity;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LightLayer;
import snownee.kiwi.util.NotNullByDefault;

@NotNullByDefault
public final class BlockDisplayRenderer implements BlockEntityRenderer<BlockDisplayBlockEntity> {
	private final BlockRenderDispatcher blockRenderer;
	private static final float BLOCK_SCALE = 0.99f;

	public BlockDisplayRenderer(BlockEntityRendererProvider.Context context) {
		blockRenderer = Minecraft.getInstance().getBlockRenderer();
	}

	@Override
	public AABB getRenderBoundingBox(BlockDisplayBlockEntity be) {
		return AABB.unitCubeFromLowerCorner(Vec3.atLowerCornerOf(be.getBlockPos().above()));
	}

	@Override
	public void render(
			BlockDisplayBlockEntity pBlockEntity,
			float pPartialTick,
			PoseStack pPoseStack,
			MultiBufferSource pBufferSource,
			int pPackedLight,
			int pPackedOverlay) {
		var state = pBlockEntity.getStoredBlockState();
		if (state.isAir()) {
			return;
		}

		var pos = pBlockEntity.getBlockPos();
		BlockPos above = pos.above();
		var level = Objects.requireNonNull(pBlockEntity.getLevel());
		var packedLight = LightTexture.pack(
				level.getBrightness(LightLayer.BLOCK, above),
				level.getBrightness(LightLayer.SKY, above));

		pPoseStack.pushPose();

		pPoseStack.scale(BLOCK_SCALE, BLOCK_SCALE, BLOCK_SCALE);
		var delta = (1 - BLOCK_SCALE) / 2;
		pPoseStack.translate(delta, 1, delta);
		//noinspection deprecation
		blockRenderer.renderSingleBlock(state, pPoseStack, pBufferSource, packedLight, pPackedOverlay);
		// to fix this, implement a custom TransformingVertexConsumer
/*		FluidState fluidState = state.getFluidState();
		if (!fluidState.isEmpty()) {
			RenderType rendertype = ItemBlockRenderTypes.getRenderLayer(fluidState);
			VertexConsumer consumer = pBufferSource.getBuffer(rendertype);
			blockRenderer.renderLiquid(above, level, consumer, state, fluidState);
		}*/
		pPoseStack.popPose();
	}
}
