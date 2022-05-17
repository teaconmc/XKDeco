/**
 * block entity part of Item Display Block
 * it should only be responsible for data storage, verification and sync
 */
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
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.teacon.xkdeco.XKDeco;

import java.util.Objects;

import static org.teacon.xkdeco.init.XKDecoObjects.ITEM_DISPLAY_BLOCK_ENTITY;

public class ItemDisplayBlockEntity extends BlockEntity {
    public static final RegistryObject<BlockEntityType<ItemDisplayBlockEntity>> TYPE =
            RegistryObject.of(new ResourceLocation(XKDeco.ID, ITEM_DISPLAY_BLOCK_ENTITY), ForgeRegistries.BLOCK_ENTITIES);
    private static final String ITEMSTACK_NBT_KEY = "Display";
    private static final String SPIN_NBT_KEY = "Spin";
    private ItemStack item = ItemStack.EMPTY;
    private float spin = 0;

    public ItemDisplayBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(TYPE.get(), blockPos, blockState);
    }

    @Override
    public AABB getRenderBoundingBox() {
        return AABB.unitCubeFromLowerCorner(Vec3.atLowerCornerOf(this.getBlockPos().above()));
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        boolean update = !item.isEmpty() || !this.item.isEmpty();
        this.item = item;
        if (update) {
            this.setChanged();
            BlockState blockState = this.getBlockState();
            Objects.requireNonNull(this.level).sendBlockUpdated(this.getBlockPos(), blockState, blockState, 0);
        }
    }

    public float getSpin() {
        return spin;
    }

    public void setSpin(float spin) {
        this.spin = spin;
        // do not send update packet to save network traffic for unimportant data
        this.setChanged();
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this,
                be -> ((ItemDisplayBlockEntity) be).writeNbt(null));
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        readNbt(pkt.getTag());
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        return writeNbt(null);
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        readNbt(tag);
    }

    @Override
    public void load(@NotNull CompoundTag pTag) {
        super.load(pTag);
        readNbt(pTag);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag pTag) {
        super.saveAdditional(pTag);
        writeNbt(pTag);
    }

    private void readNbt(@Nullable CompoundTag tag) {
        if (tag == null) return;
        if (tag.contains(ITEMSTACK_NBT_KEY)) {
            this.item = ItemStack.of(tag.getCompound(ITEMSTACK_NBT_KEY));
        }
        if (tag.contains(SPIN_NBT_KEY)) {
            this.spin = tag.getFloat(SPIN_NBT_KEY);
        }
    }

    private CompoundTag writeNbt(@Nullable CompoundTag tag) {
        if (tag == null) tag = new CompoundTag();
        tag.put(ITEMSTACK_NBT_KEY, item.save(new CompoundTag()));
        tag.putFloat(SPIN_NBT_KEY, spin);
        return tag;
    }
}
