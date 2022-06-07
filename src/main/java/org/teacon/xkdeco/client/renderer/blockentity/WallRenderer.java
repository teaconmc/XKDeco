package org.teacon.xkdeco.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.data.EmptyModelData;
import org.teacon.xkdeco.block.SpecialWallBlock;
import org.teacon.xkdeco.blockentity.WallBlockEntity;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Random;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public final class WallRenderer implements BlockEntityRenderer<WallBlockEntity> {
    private final BlockRenderDispatcher blockRenderer;

    public WallRenderer(BlockEntityRendererProvider.Context context) {
        this.blockRenderer = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(WallBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack,
                       MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        var level = pBlockEntity.getLevel();
        var pos = pBlockEntity.getBlockPos();
        var state = pBlockEntity.getBlockState();
        if (state.getBlock() instanceof SpecialWallBlock wall) {
            var wallState = this.withState(state, wall.getWallDelegate(), BlockStateProperties.UP);
            if (level == null) {
                this.blockRenderer.renderSingleBlock(wallState, pPoseStack,
                        pBufferSource, pPackedLight, pPackedOverlay, EmptyModelData.INSTANCE);
            } else {
                var random = new Random();
                var renderTypes = RenderType.chunkBufferLayers();
                var oldRenderType = MinecraftForgeClient.getRenderType();
                for (var renderType : renderTypes) {
                    ForgeHooksClient.setRenderType(renderType);
                    if (ItemBlockRenderTypes.canRenderInLayer(wallState, renderType)) {
                        this.blockRenderer.renderBatched(wallState, pos, level, pPoseStack,
                                pBufferSource.getBuffer(renderType), false, random, EmptyModelData.INSTANCE);
                    }
                    var eastWall = wall.connectsTo(level.getBlockState(pos.east()));
                    if (eastWall.isPresent()) {
                        var eastWallState = this.withState(state, eastWall.get(), BlockStateProperties.EAST_WALL);
                        if (ItemBlockRenderTypes.canRenderInLayer(eastWallState, renderType)) {
                            this.blockRenderer.renderBatched(eastWallState, pos, level, pPoseStack,
                                    pBufferSource.getBuffer(renderType), false, random, EmptyModelData.INSTANCE);
                        }
                    }
                    var northWall = wall.connectsTo(level.getBlockState(pos.north()));
                    if (northWall.isPresent()) {
                        var northWallState = this.withState(state, northWall.get(), BlockStateProperties.NORTH_WALL);
                        if (ItemBlockRenderTypes.canRenderInLayer(northWallState, renderType)) {
                            this.blockRenderer.renderBatched(northWallState, pos, level, pPoseStack,
                                    pBufferSource.getBuffer(renderType), false, random, EmptyModelData.INSTANCE);
                        }
                    }
                    var southWall = wall.connectsTo(level.getBlockState(pos.south()));
                    if (southWall.isPresent()) {
                        var southWallState = this.withState(state, southWall.get(), BlockStateProperties.SOUTH_WALL);
                        if (ItemBlockRenderTypes.canRenderInLayer(southWallState, renderType)) {
                            this.blockRenderer.renderBatched(southWallState, pos, level, pPoseStack,
                                    pBufferSource.getBuffer(renderType), false, random, EmptyModelData.INSTANCE);
                        }
                    }
                    var westWall = wall.connectsTo(level.getBlockState(pos.west()));
                    if (westWall.isPresent()) {
                        var westWallState = this.withState(state, westWall.get(), BlockStateProperties.WEST_WALL);
                        if (ItemBlockRenderTypes.canRenderInLayer(westWallState, renderType)) {
                            this.blockRenderer.renderBatched(westWallState, pos, level, pPoseStack,
                                    pBufferSource.getBuffer(renderType), false, random, EmptyModelData.INSTANCE);
                        }
                    }
                }
                ForgeHooksClient.setRenderType(oldRenderType);
            }
        }
    }

    private <T extends Comparable<T>> BlockState withState(BlockState source, Block target, Property<T> property) {
        var state = target.defaultBlockState();
        if (state.hasProperty(BlockStateProperties.UP)) {
            state = state.setValue(BlockStateProperties.UP, false);
        }
        if (state.hasProperty(property)) {
            state = state.setValue(property, source.getValue(property));
        }
        return state;
    }
}
