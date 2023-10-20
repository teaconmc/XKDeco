package org.teacon.xkdeco.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.ChunkBufferBuilderPack;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.teacon.xkdeco.XKDeco;
import org.teacon.xkdeco.block.SpecialWallBlock;
import org.teacon.xkdeco.client.IFixedBEREntity;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.Set;

import static org.teacon.xkdeco.init.XKDecoObjects.WALL_BLOCK_ENTITY;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public final class WallBlockEntity extends BlockEntity implements IFixedBEREntity {
    public static final RegistryObject<BlockEntityType<WallBlockEntity>> TYPE =
            RegistryObject.create(new ResourceLocation(XKDeco.ID, WALL_BLOCK_ENTITY), ForgeRegistries.BLOCK_ENTITY_TYPES);

    private Block eastBlock = Blocks.AIR;
    private Block westBlock = Blocks.AIR;
    private Block southBlock = Blocks.AIR;
    private Block northBlock = Blocks.AIR;

    public WallBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(TYPE.get(), pWorldPosition, pBlockState);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    public void updateBlocksFromLevel(SpecialWallBlock wall) {
        var pos = this.getBlockPos();
        var block = this.getBlockState().getBlock();
        if (this.level != null) {
            var northWall = wall.connectsTo(this.level.getBlockState(pos.north()));
            var eastWall = wall.connectsTo(this.level.getBlockState(pos.east()));
            var southWall = wall.connectsTo(this.level.getBlockState(pos.south()));
            var westWall = wall.connectsTo(this.level.getBlockState(pos.west()));
            this.northBlock = northWall.orElse(Blocks.AIR);
            this.eastBlock = eastWall.orElse(Blocks.AIR);
            this.southBlock = southWall.orElse(Blocks.AIR);
            this.westBlock = westWall.orElse(Blocks.AIR);
            this.setChanged();
        }
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        this.northBlock = Objects.requireNonNullElse(ForgeRegistries.BLOCKS
                .getValue(new ResourceLocation(pTag.getString("NorthBlockName"))), Blocks.AIR);
        this.eastBlock = Objects.requireNonNullElse(ForgeRegistries.BLOCKS
                .getValue(new ResourceLocation(pTag.getString("EastBlockName"))), Blocks.AIR);
        this.southBlock = Objects.requireNonNullElse(ForgeRegistries.BLOCKS
                .getValue(new ResourceLocation(pTag.getString("SouthBlockName"))), Blocks.AIR);
        this.westBlock = Objects.requireNonNullElse(ForgeRegistries.BLOCKS
                .getValue(new ResourceLocation(pTag.getString("WestBlockName"))), Blocks.AIR);
        if (this.getBlockState().getBlock() instanceof SpecialWallBlock wall) {
            this.updateBlocksFromLevel(wall);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.putString("NorthBlockName", Objects.requireNonNullElse(ForgeRegistries.BLOCKS
                .getKey(this.northBlock), ForgeRegistries.BLOCKS.getKey(Blocks.AIR)).toString());
        pTag.putString("EastBlockName", Objects.requireNonNullElse(ForgeRegistries.BLOCKS
                .getKey(this.eastBlock), ForgeRegistries.BLOCKS.getKey(Blocks.AIR)).toString());
        pTag.putString("SouthBlockName", Objects.requireNonNullElse(ForgeRegistries.BLOCKS
                .getKey(this.southBlock), ForgeRegistries.BLOCKS.getKey(Blocks.AIR)).toString());
        pTag.putString("WestBlockName", Objects.requireNonNullElse(ForgeRegistries.BLOCKS
                .getKey(this.westBlock), ForgeRegistries.BLOCKS.getKey(Blocks.AIR)).toString());
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void renderFixedBER(Set<RenderType> begunRenderTypes, BlockRenderDispatcher blockRenderer, ChunkBufferBuilderPack builderPack, PoseStack poseStack, int packedOverlay) {
        var level = this.getLevel();
        var pos = this.getBlockPos();
        var state = this.getBlockState();
        if (state.getBlock() instanceof SpecialWallBlock wall) {
            var wallState = this.withState(state, wall.getWallDelegate(), BlockStateProperties.UP);
            var random = RandomSource.create();
            var renderTypes = RenderType.chunkBufferLayers();
            for (var renderType : renderTypes) {
                var buffer = getBuilder(begunRenderTypes, builderPack, renderType);
                assert level != null;
                blockRenderer.renderBatched(wallState, pos, level, poseStack,
                        buffer, false, random, ModelData.EMPTY, renderType);
                var eastWall = wall.connectsTo(level.getBlockState(pos.east()));
                if (eastWall.isPresent()) {
                    var eastWallState = this.withState(state, eastWall.get(), BlockStateProperties.EAST_WALL);
                    blockRenderer.renderBatched(eastWallState, pos, level, poseStack,
                            buffer, false, random, ModelData.EMPTY, renderType);
                }
                var northWall = wall.connectsTo(level.getBlockState(pos.north()));
                if (northWall.isPresent()) {
                    var northWallState = this.withState(state, northWall.get(), BlockStateProperties.NORTH_WALL);
                    blockRenderer.renderBatched(northWallState, pos, level, poseStack,
                            buffer, false, random, ModelData.EMPTY, renderType);
                }
                var southWall = wall.connectsTo(level.getBlockState(pos.south()));
                if (southWall.isPresent()) {
                    var southWallState = this.withState(state, southWall.get(), BlockStateProperties.SOUTH_WALL);
                    blockRenderer.renderBatched(southWallState, pos, level, poseStack,
                            buffer, false, random, ModelData.EMPTY, renderType);
                }
                var westWall = wall.connectsTo(level.getBlockState(pos.west()));
                if (westWall.isPresent()) {
                    var westWallState = this.withState(state, westWall.get(), BlockStateProperties.WEST_WALL);
                    blockRenderer.renderBatched(westWallState, pos, level, poseStack,
                            buffer, false, random, ModelData.EMPTY, renderType);
                }
            }

        }
    }

    public <T extends Comparable<T>> BlockState withState(BlockState source, Block target, Property<T> property) {
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
