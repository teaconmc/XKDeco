package org.teacon.xkdeco.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.EmptyModelData;
import org.jetbrains.annotations.NotNull;
import org.teacon.xkdeco.blockentity.BlockDisplayBlockEntity;

import java.util.Objects;

public class BlockDisplayRenderer implements BlockEntityRenderer<BlockDisplayBlockEntity> {
    private final BlockRenderDispatcher blockRenderer;
    private final float BLOCK_SCALE = 0.99f;

    public BlockDisplayRenderer(BlockEntityRendererProvider.Context context) {
        blockRenderer = Minecraft.getInstance().getBlockRenderer();
    }

    @Override
    public void render(BlockDisplayBlockEntity pBlockEntity, float pPartialTick, @NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        BlockState state = pBlockEntity.getStoredBlockState();
        if (state.isAir()) return;

        BlockPos pos = pBlockEntity.getBlockPos();
        Level level = Objects.requireNonNull(pBlockEntity.getLevel());
        int packedLight = LightTexture.pack(level.getBrightness(LightLayer.BLOCK, pos.above()), level.getBrightness(LightLayer.SKY, pos.above()));

        pPoseStack.pushPose();

        pPoseStack.scale(BLOCK_SCALE, BLOCK_SCALE, BLOCK_SCALE);
        float delta = (1 - BLOCK_SCALE) / 2;
        pPoseStack.translate(delta, 1, delta);
        blockRenderer.renderSingleBlock(state, pPoseStack, pBufferSource, packedLight, pPackedOverlay, EmptyModelData.INSTANCE);

        pPoseStack.popPose();
    }
}
