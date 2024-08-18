package org.teacon.xkdeco.blockentity;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

//@NotNullByDefault
public class SingleSlotContainerBlockEntity extends BaseContainerBlockEntity {
	public static final String ITEM_STACK_KEY = "Display";
	protected ItemStack item = ItemStack.EMPTY;

	protected SingleSlotContainerBlockEntity(
			BlockEntityType<?> pType,
			BlockPos pPos,
			BlockState pBlockState) {
		super(pType, pPos, pBlockState);
	}

	@Override
	protected Component getDefaultName() {
		return Component.translatable(Util.makeDescriptionId("container", BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(getType())));
	}

	@Override
	protected AbstractContainerMenu createMenu(int i, Inventory inventory) {
		return null;
	}

	@Override
	public int getContainerSize() {
		return 1;
	}

	@Override
	protected NonNullList<ItemStack> getItems() {
		return NonNullList.of(this.item);
	}

	@Override
	protected void setItems(NonNullList<ItemStack> items) {
		this.item = items.getFirst();
	}

	@Override
	public boolean isEmpty() {
		return getFirstItem().isEmpty();
	}

	public ItemStack getFirstItem() {
		return getItem(0);
	}

	@Override
	public ItemStack getItem(int pSlot) {
		return item;
	}

	@Override
	public ItemStack removeItem(int pSlot, int pAmount) {
		if (item.isEmpty() || pAmount <= 0) {
			return ItemStack.EMPTY;
		}
		ItemStack itemstack = item.copyWithCount(pAmount);
		setItem(0, item.copyWithCount(item.getCount() - pAmount)); // play the removing sound if possible
		return itemstack;
	}

	@Override
	public ItemStack removeItemNoUpdate(int pSlot) {
		ItemStack itemstack = item;
		item = ItemStack.EMPTY;
		return itemstack;
	}

	@Override
	public void setItem(int pSlot, ItemStack pStack) {
		boolean empty = isEmpty();
		item = pStack;
		if (pStack.getCount() > getMaxStackSize()) {
			pStack.setCount(getMaxStackSize());
		}
		refresh();
		if (level != null && !level.isClientSide) {
			if (empty && !isEmpty()) {
				level.playSound(null, worldPosition, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 1, 1);
			} else if (!empty && isEmpty()) {
				level.playSound(null, worldPosition, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1, 1);
			}
		}
	}

	@Override
	public boolean stillValid(Player pPlayer) {
		return Container.stillValidBlockEntity(this, pPlayer);
	}

	@Override
	public void clearContent() {
		setItem(0, ItemStack.EMPTY);
	}

	@Override
	public void loadAdditional(CompoundTag pTag, HolderLookup.Provider registries) {
		super.loadAdditional(pTag, registries);
		readPacketData(pTag, registries);
	}

	@Override
	protected void saveAdditional(CompoundTag pTag, HolderLookup.Provider registries) {
		super.saveAdditional(pTag, registries);
		writePacketData(pTag, registries);
	}

	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider registries) {
		CompoundTag compoundtag = pkt.getTag();
		if (compoundtag != null) {
			this.readPacketData(compoundtag, registries);
		}
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
		return this.writePacketData(new CompoundTag(), registries);
	}

	protected void readPacketData(CompoundTag pTag, HolderLookup.Provider registries) {
		item = ItemStack.EMPTY;
		if (pTag.contains(ITEM_STACK_KEY)) {
			item = ItemStack.parseOptional(registries, pTag.getCompound(ITEM_STACK_KEY));
		}
	}

	protected CompoundTag writePacketData(CompoundTag pTag, HolderLookup.Provider registries) {
		if (!item.isEmpty()) {
			pTag.put(ITEM_STACK_KEY, item.save(registries, new CompoundTag()));
		}
		return pTag;
	}

	public void refresh() {
		if (this.hasLevel() && !this.level.isClientSide) {
			BlockState state = this.getBlockState();
			this.level.sendBlockUpdated(this.worldPosition, state, state, 11);
			this.setChanged();
		}
	}
}
