package org.teacon.xkdeco.block;

import org.teacon.xkdeco.blockentity.SingleSlotContainerBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.ticks.ContainerSingleItem;
import snownee.kiwi.block.ModBlock;
import snownee.kiwi.customization.block.CheckedWaterloggedBlock;

public abstract class DisplayBlock extends ModBlock implements EntityBlock, CheckedWaterloggedBlock {
	public DisplayBlock(Properties builder) {
		super(builder);
	}

	@Override
	protected InteractionResult useItemOn(
			ItemStack stack,
			BlockState state,
			Level level,
			BlockPos pos,
			Player player,
			InteractionHand hand,
			BlockHitResult hitResult) {
		if (checkLock(player, level, pos)) {
			return InteractionResult.SUCCESS;
		}
		if (doesHitTop(hitResult)) {
			return useTop(stack, state, level, pos, player, hand, hitResult);
		} else {
			return useSide(stack, state, level, pos, player, hand, hitResult);
		}
	}

	private boolean checkLock(Player player, Level level, BlockPos pos) {
		if (!level.isClientSide() && level.getBlockEntity(pos) instanceof SingleSlotContainerBlockEntity container &&
				container.isLocked()) {
			if (container.canOpen(player)) {
				container.unlock();
				player.sendOverlayMessage(Component.translatable("tip.xkdeco.block_unlocked", getName()));
			} else {
				BaseContainerBlockEntity.sendChestLockedNotifications(Vec3.atCenterOf(pos), player, getName());
			}
			return true;
		}
		return false;
	}

	protected InteractionResult useSide(
			ItemStack held,
			BlockState pState,
			Level pLevel,
			BlockPos pPos,
			Player pPlayer,
			InteractionHand pHand,
			BlockHitResult pHit) {
		return InteractionResult.TRY_WITH_EMPTY_HAND;
	}

	protected InteractionResult useTop(
			ItemStack held,
			BlockState pState,
			Level pLevel,
			BlockPos pPos,
			Player pPlayer,
			InteractionHand pHand,
			BlockHitResult pHit) {
		if (!(pLevel.getBlockEntity(pPos) instanceof ContainerSingleItem container)) {
			return InteractionResult.FAIL;
		}
		if (pLevel.isClientSide()) {
			return InteractionResult.SUCCESS;
		}
		if (held.isEmpty()) {
			grab(pState, pLevel, pPos, pPlayer);
			return InteractionResult.CONSUME;
		}
		insertItem(container, pPlayer.getAbilities().instabuild ? held.copy() : held);
		return InteractionResult.CONSUME;
	}

	public void click(BlockState blockState, Level level, BlockPos pos, ServerPlayer player, BlockHitResult hit) {
		if (checkLock(player, level, pos)) {
			return;
		}
		if (doesHitTop(hit)) {
			clickTop(blockState, level, pos, player, hit);
		} else {
			clickSide(blockState, level, pos, player, hit);
		}
	}

	protected void clickSide(BlockState blockState, Level level, BlockPos pos, ServerPlayer player, BlockHitResult hit) {}

	protected void clickTop(BlockState blockState, Level level, BlockPos pos, ServerPlayer player, BlockHitResult hit) {
		grab(blockState, level, pos, player);
	}

	public boolean insertItem(ContainerSingleItem container, ItemStack itemStack) {
		if (!container.canPlaceItem(0, itemStack)) {
			return false;
		}
		ItemStack displayed = container.getTheItem();
		if (displayed.isEmpty() || ItemStack.isSameItemSameComponents(displayed, itemStack)) {
			int maxSize = Math.min(itemStack.getMaxStackSize(), container.getMaxStackSize());
			int transferAmount = Math.min(itemStack.getCount(), maxSize - displayed.getCount());
			if (transferAmount > 0) {
				ItemStack split = itemStack.split(transferAmount);
				split.grow(displayed.getCount());
				container.setTheItem(split);
				return true;
			}
		}
		return false;
	}

	@Override
	public void stepOn(Level pLevel, BlockPos pPos, BlockState pState, Entity pEntity) {
		super.stepOn(pLevel, pPos, pState, pEntity);
		if (!pLevel.isClientSide() && pEntity instanceof ItemEntity itemEntity &&
				pLevel.getBlockEntity(pPos) instanceof ContainerSingleItem container) {
			if (insertItem(container, itemEntity.getItem())) {
				itemEntity.setItem(itemEntity.getItem()); // send update packet
			}
		}
	}

	public void grab(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer) {
		if (pLevel.isClientSide() || !(pLevel.getBlockEntity(pPos) instanceof ContainerSingleItem be)) {
			return;
		}
		ItemStack item = be.removeTheItem();
		if (item.isEmpty()) {
			return;
		}
		double d3 = pPos.getX() + 0.5;
		double d4 = pPos.getY() + 1;
		double d5 = pPos.getZ() + 0.5;
		ItemEntity itementity = new ItemEntity(pLevel, d3, d4, d5, item);
		itementity.setDeltaMovement(0, 0.2, 0);
		pLevel.addFreshEntity(itementity);
		itementity.playerTouch(pPlayer);
	}

	public boolean doesHitTop(BlockHitResult pHit) {
		return pHit.getDirection() == Direction.UP && pHit.getLocation().y - pHit.getBlockPos().getY() > 0.75;
	}

	// BlockItem now automatically handles custom name. setPlacedBy is no longer required.
	// cf. BlockEntity.applyComponentsFromItemStack

	// Container contents are dropped automatically via BlockEntity.preRemoveSideEffects.
	@Override
	protected void affectNeighborsAfterRemoval(BlockState pState, ServerLevel pLevel, BlockPos pPos, boolean pMovedByPiston) {
		Containers.updateNeighboursAfterDestroy(pState, pLevel, pPos);
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState pState) {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState pBlockState, Level pLevel, BlockPos pPos, Direction pDirection) {
		return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(pLevel.getBlockEntity(pPos));
	}

	public boolean canBeDestroyed(BlockState blockState, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		return !(level.getBlockEntity(pos) instanceof Container container) || container.isEmpty();
	}

	@Override
	public BlockItem createItem(Item.Properties builder) {
		return super.createItem(builder.component(DataComponents.CONTAINER, ItemContainerContents.EMPTY));
	}
}
