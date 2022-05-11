package org.teacon.xkdeco.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;
import org.teacon.xkdeco.XKDeco;

import java.util.Objects;
import java.util.Optional;

import static org.teacon.xkdeco.init.XKDecoObjects.ITEM_DISPLAY_BLOCK_ENTITY;

public class ItemDisplayBlockEntity extends BlockEntity {
    public static final RegistryObject<BlockEntityType<ItemDisplayBlockEntity>> TYPE =
            RegistryObject.of(new ResourceLocation(XKDeco.ID, ITEM_DISPLAY_BLOCK_ENTITY), ForgeRegistries.BLOCK_ENTITIES);
    private ItemStack item = ItemStack.EMPTY;
    private static final String ITEMSTACK_NBT_KEY = "Display";

    public ItemDisplayBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(RegistryObject.of(new ResourceLocation(XKDeco.ID, ITEM_DISPLAY_BLOCK_ENTITY), ForgeRegistries.BLOCK_ENTITIES).get(), blockPos, blockState);
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
        this.setChanged();
        BlockState blockState = this.getBlockState();
        Objects.requireNonNull(this.level).sendBlockUpdated(this.getBlockPos(), blockState, blockState, 0);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this,
                be -> ((ItemDisplayBlockEntity) be).item.save(new CompoundTag()));
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        Optional.ofNullable(pkt.getTag()).map(ItemStack::of).ifPresent(this::setItem);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.item.save(new CompoundTag());
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        Optional.ofNullable(tag).map(ItemStack::of).ifPresent(this::setItem);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        if (pTag.contains(ITEMSTACK_NBT_KEY)) {
            this.item = ItemStack.of(pTag.getCompound(ITEMSTACK_NBT_KEY));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        if (!item.isEmpty()) {
            pTag.put(ITEMSTACK_NBT_KEY, item.save(new CompoundTag()));
        }
    }
}
