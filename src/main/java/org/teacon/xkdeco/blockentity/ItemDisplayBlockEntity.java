/**
 * block entity part of Item Display Block
 * it should only be responsible for data storage, verification and sync
 */
package org.teacon.xkdeco.blockentity;

import static org.teacon.xkdeco.init.XKDecoObjects.ITEM_DISPLAY_BLOCK_ENTITY;

import java.util.Objects;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.teacon.xkdeco.XKDeco;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Clearable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public final class ItemDisplayBlockEntity extends BlockEntity implements Clearable {
	public static final RegistryObject<BlockEntityType<ItemDisplayBlockEntity>> TYPE =
			RegistryObject.create(new ResourceLocation(XKDeco.ID, ITEM_DISPLAY_BLOCK_ENTITY), ForgeRegistries.BLOCK_ENTITY_TYPES);
	public static final String ITEMSTACK_NBT_KEY = "Display";
	private static final String SPIN_NBT_KEY = "Spin";

	private final boolean isProjector;
	private ItemStack item = ItemStack.EMPTY;
	private float spin = 0;

	public ItemDisplayBlockEntity(BlockPos blockPos, BlockState blockState) {
		super(TYPE.get(), blockPos, blockState);
		this.isProjector = blockState.is(Objects.requireNonNull(ForgeRegistries.BLOCKS.getValue(new ResourceLocation(
				XKDeco.ID,
				"item_projector"))));
	}

	@Override
	public AABB getRenderBoundingBox() {
		if (isProjector) {
			return AABB.ofSize(Vec3.atBottomCenterOf(this.getBlockPos().above(9)), 16, 16, 16);
		} else {
			return AABB.unitCubeFromLowerCorner(Vec3.atLowerCornerOf(this.getBlockPos().above()));
		}
	}

	public boolean isProjector() {
		return isProjector;
	}

	public ItemStack getItem() {
		return item;
	}

	public void setItem(ItemStack item) {
		var update = !item.isEmpty() || !this.item.isEmpty();
		this.item = item;
		if (update) {
			this.setChanged();
			var blockState = this.getBlockState();
			Objects.requireNonNull(this.level).sendBlockUpdated(this.getBlockPos(), blockState, blockState, 0);
		}
	}

	public float getSpin() {
		return spin;
	}

	public void setSpin(float spin) {
		this.spin = spin;
	}

	@Nullable
	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(
				this,
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
	public void clearContent() {
		this.setItem(ItemStack.EMPTY);
	}

	@Override
	protected void saveAdditional(@NotNull CompoundTag pTag) {
		super.saveAdditional(pTag);
		writeNbt(pTag);
	}

	private void readNbt(@Nullable CompoundTag tag) {
		if (tag == null) {
			return;
		}
		if (tag.contains(ITEMSTACK_NBT_KEY)) {
			this.item = ItemStack.of(tag.getCompound(ITEMSTACK_NBT_KEY));
		}
		if (tag.contains(SPIN_NBT_KEY)) {
			this.spin = tag.getFloat(SPIN_NBT_KEY);
		}
	}

	private CompoundTag writeNbt(@Nullable CompoundTag tag) {
		if (tag == null) {
			tag = new CompoundTag();
		}
		tag.put(ITEMSTACK_NBT_KEY, item.save(new CompoundTag()));
		tag.putFloat(SPIN_NBT_KEY, spin);
		return tag;
	}
}
