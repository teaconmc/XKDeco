package org.teacon.xkdeco.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.LightLayer;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;
import org.teacon.xkdeco.blockentity.BlockDisplayBlockEntity;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public final class BlockDisplayRenderer implements BlockEntityRenderer<BlockDisplayBlockEntity> {
    private final BlockRenderDispatcher blockRenderer;
    private static final float BLOCK_SCALE = 0.99f;

    public BlockDisplayRenderer(BlockEntityRendererProvider.Context context) {
        blockRenderer = Minecraft.getInstance().getBlockRenderer();
    }

    @Override
    public void render(BlockDisplayBlockEntity pBlockEntity, float pPartialTick, @NotNull PoseStack pPoseStack, @NotNull MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        var state = pBlockEntity.getStoredBlockState();
        if (state.isAir()) return;

        var pos = pBlockEntity.getBlockPos();
        var level = Objects.requireNonNull(pBlockEntity.getLevel());
        var packedLight = LightTexture.pack(level.getBrightness(LightLayer.BLOCK, pos.above()), level.getBrightness(LightLayer.SKY, pos.above()));

        pPoseStack.pushPose();

        pPoseStack.scale(BLOCK_SCALE, BLOCK_SCALE, BLOCK_SCALE);
        var delta = (1 - BLOCK_SCALE) / 2;
        pPoseStack.translate(delta, 1, delta);
        blockRenderer.renderSingleBlock(state, pPoseStack, pBufferSource, packedLight, pPackedOverlay, ModelData.EMPTY, null);

        pPoseStack.popPose();
    }
}
