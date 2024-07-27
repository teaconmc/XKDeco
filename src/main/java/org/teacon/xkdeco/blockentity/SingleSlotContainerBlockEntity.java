package org.teacon.xkdeco.blockentity;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
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
import snownee.kiwi.util.NotNullByDefault;

@NotNullByDefault
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
	public void load(CompoundTag pTag) {
		super.load(pTag);
		readPacketData(pTag);
	}

	@Override
	protected void saveAdditional(CompoundTag pTag) {
		super.saveAdditional(pTag);
		writePacketData(pTag);
	}

	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
		CompoundTag compoundtag = pkt.getTag();
		if (compoundtag != null) {
			this.readPacketData(compoundtag);
		}
	}

	@Override
	public CompoundTag getUpdateTag() {
		return this.writePacketData(new CompoundTag());
	}

	protected void readPacketData(CompoundTag pTag) {
		item = ItemStack.EMPTY;
		if (pTag.contains(ITEM_STACK_KEY)) {
			item = ItemStack.of(pTag.getCompound(ITEM_STACK_KEY));
		}
	}

	protected CompoundTag writePacketData(CompoundTag pTag) {
		if (!item.isEmpty()) {
			pTag.put(ITEM_STACK_KEY, item.save(new CompoundTag()));
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
