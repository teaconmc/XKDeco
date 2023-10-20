package org.teacon.xkdeco.client;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.ChunkBufferBuilderPack;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Set;

public interface IFixedBEREntity {

    private BlockEntity self() {
        return (BlockEntity) this;
    }

    /**
     * Use {@link IFixedBEREntity#getBuilder(Set, ChunkBufferBuilderPack, RenderType)} instead of {@link ChunkBufferBuilderPack#builder(RenderType)} .
     */
    void renderFixedBER(Set<RenderType> begunRenderTypes, BlockRenderDispatcher blockRenderer, ChunkBufferBuilderPack builderPack, PoseStack poseStack, int packedOverlay);

    default int getPackedLight() {
        if (self().getLevel() != null) {
            return LevelRenderer.getLightColor(self().getLevel(), self().getBlockPos());
        } else {
            return 0;
        }
    }

    default BufferBuilder getBuilder(Set<RenderType> begunRenderTypes, ChunkBufferBuilderPack bufferBuilderPack, RenderType type) {
        var builder = bufferBuilderPack.builder(type);
        if (begunRenderTypes.add(type)) {
            builder.begin(type.mode(), type.format());
        }
        return builder;
    }
}
