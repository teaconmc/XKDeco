package org.teacon.xkdeco.blockentity;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.LockCode;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.ticks.ContainerSingleItem;
import snownee.kiwi.block.entity.ModBlockEntity;

public class SingleSlotContainerBlockEntity extends ModBlockEntity implements ContainerSingleItem.BlockContainerSingleItem {
	public static final String ITEM_STACK_KEY = "Display";
	protected ItemStack item = ItemStack.EMPTY;
	private LockCode lockKey = LockCode.NO_LOCK;

	protected SingleSlotContainerBlockEntity(
			BlockEntityType<?> pType,
			BlockPos pPos,
			BlockState pBlockState) {
		super(pType, pPos, pBlockState);
	}

	@Override
	public ItemStack getTheItem() {
		return item;
	}

	@Override
	public void setTheItem(ItemStack itemStack) {
		boolean empty = isEmpty();
		item = itemStack;
		refresh();
		if (level != null && !level.isClientSide()) {
			if (empty && !isEmpty()) {
				level.playSound(null, worldPosition, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 1, 1);
			} else if (!empty && isEmpty()) {
				level.playSound(null, worldPosition, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1, 1);
			}
		}
	}

	@Override
	public ItemStack splitTheItem(int count) {
		ItemStack itemStack = BlockContainerSingleItem.super.splitTheItem(count);
		refresh();
		return itemStack;
	}

	@Override
	public void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		lockKey = LockCode.fromTag(input);
		readPacketData(input);
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		lockKey.addToTag(output);
		writePacketData(output);
	}

	@Override
	protected void readPacketData(ValueInput input) {
		item = input.read(ITEM_STACK_KEY, ItemStack.CODEC).orElse(ItemStack.EMPTY);
	}

	@Override
	protected void writePacketData(ValueOutput output) {
		if (!item.isEmpty()) {
			output.store(ITEM_STACK_KEY, ItemStack.CODEC, item);
		}
	}

	public boolean canOpen(Player player) {
		return lockKey.canUnlock(player);
	}

	public boolean isLocked() {
		return !lockKey.equals(LockCode.NO_LOCK);
	}

	public void unlock() {
		lockKey = LockCode.NO_LOCK;
		setChanged();
	}

	@Override
	public BlockEntity getContainerBlockEntity() {
		return this;
	}

	@Override
	protected void applyImplicitComponents(DataComponentGetter components) {
		super.applyImplicitComponents(components);
		lockKey = components.getOrDefault(DataComponents.LOCK, LockCode.NO_LOCK);
		item = components.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyOne();
	}

	@Override
	protected void collectImplicitComponents(DataComponentMap.Builder components) {
		super.collectImplicitComponents(components);
		if (isLocked()) {
			components.set(DataComponents.LOCK, lockKey);
		}
		components.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(List.of(item)));
	}

	@SuppressWarnings("deprecation")
	@Override
	public void removeComponentsFromTag(ValueOutput output) {
		output.discard("lock");
		output.discard(ITEM_STACK_KEY);
	}
}
