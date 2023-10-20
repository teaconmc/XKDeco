package org.teacon.xkdeco.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.ChunkBufferBuilderPack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.teacon.xkdeco.client.IFixedBEREntity;

import java.util.Iterator;
import java.util.Set;

/**
 * @author USS_Shenzhou
 */
@Mixin(ChunkRenderDispatcher.RenderChunk.RebuildTask.class)
public class ChunkRenderDispatcher$RenderChunk$RebuildTaskMixin {

    @Inject(method = "compile", at = @At(value = "INVOKE", shift = At.Shift.AFTER,
            target = "Lnet/minecraft/client/renderer/chunk/ChunkRenderDispatcher$RenderChunk$RebuildTask;handleBlockEntity(Lnet/minecraft/client/renderer/chunk/ChunkRenderDispatcher$RenderChunk$RebuildTask$CompileResults;Lnet/minecraft/world/level/block/entity/BlockEntity;)V"),
            locals = LocalCapture.CAPTURE_FAILSOFT)
    private void xkdecoCompileFixedBlockEntity(float pX, float pY, float pZ, ChunkBufferBuilderPack pChunkBufferBuilderPack,
                                            CallbackInfoReturnable<ChunkRenderDispatcher.RenderChunk.RebuildTask.CompileResults> cir,
                                            ChunkRenderDispatcher.RenderChunk.RebuildTask.CompileResults compileResults, int i, BlockPos from, BlockPos to, VisGraph visgraph, RenderChunkRegion renderchunkregion, PoseStack poseStack, Set<RenderType> renderTypes, RandomSource random, BlockRenderDispatcher blockRenderer, Iterator<BlockPos> posIterator, BlockPos pos, BlockState state, BlockEntity entity) {
        if (entity instanceof IFixedBEREntity fixedEntity) {
            poseStack.pushPose();
            poseStack.translate(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15);
            fixedEntity.renderFixedBER(renderTypes,blockRenderer, pChunkBufferBuilderPack, poseStack, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();
        }
    }
}
