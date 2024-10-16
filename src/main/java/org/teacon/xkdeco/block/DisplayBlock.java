package org.teacon.xkdeco.block;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import snownee.kiwi.block.ModBlock;
import snownee.kiwi.customization.block.CheckedWaterloggedBlock;
import snownee.kiwi.util.NotNullByDefault;

@NotNullByDefault
public abstract class DisplayBlock extends ModBlock implements EntityBlock, CheckedWaterloggedBlock {
	public DisplayBlock(Properties builder) {
		super(builder);
	}

	@Override
	public InteractionResult use(
			BlockState pState,
			Level pLevel,
			BlockPos pPos,
			Player pPlayer,
			InteractionHand pHand,
			BlockHitResult pHit) {
		if (doesHitTop(pHit)) {
			return useTop(pState, pLevel, pPos, pPlayer, pHand, pHit);
		} else {
			return useSide(pState, pLevel, pPos, pPlayer, pHand, pHit);
		}
	}

	protected InteractionResult useSide(
			BlockState pState,
			Level pLevel,
			BlockPos pPos,
			Player pPlayer,
			InteractionHand pHand,
			BlockHitResult pHit) {
		return InteractionResult.PASS;
	}

	protected InteractionResult useTop(
			BlockState pState,
			Level pLevel,
			BlockPos pPos,
			Player pPlayer,
			InteractionHand pHand,
			BlockHitResult pHit) {
		if (!(pLevel.getBlockEntity(pPos) instanceof Container container)) {
			return InteractionResult.FAIL;
		}
		if (pLevel.isClientSide) {
			return InteractionResult.SUCCESS;
		}
		ItemStack held = pPlayer.getItemInHand(pHand);
		if (held.isEmpty()) {
			grab(pState, pLevel, pPos, pPlayer);
			return InteractionResult.CONSUME;
		}
		insertItem(container, pPlayer.getAbilities().instabuild ? held.copy() : held);
		return InteractionResult.CONSUME;
	}

	public void click(BlockState blockState, Level level, BlockPos pos, ServerPlayer player, BlockHitResult hit) {
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

	public boolean insertItem(Container container, ItemStack itemStack) {
		if (!container.canPlaceItem(0, itemStack)) {
			return false;
		}
		ItemStack displayed = container.getItem(0);
		if (displayed.isEmpty() || ItemStack.isSameItemSameTags(displayed, itemStack)) {
			int maxSize = Math.min(itemStack.getMaxStackSize(), container.getMaxStackSize());
			int transferAmount = Math.min(itemStack.getCount(), maxSize - displayed.getCount());
			if (transferAmount > 0) {
				ItemStack split = itemStack.split(transferAmount);
				split.grow(displayed.getCount());
				container.setItem(0, split);
				return true;
			}
		}
		return false;
	}

	@Override
	public void stepOn(Level pLevel, BlockPos pPos, BlockState pState, Entity pEntity) {
		super.stepOn(pLevel, pPos, pState, pEntity);
		if (!pLevel.isClientSide && pEntity instanceof ItemEntity itemEntity &&
				pLevel.getBlockEntity(pPos) instanceof Container container) {
			if (insertItem(container, itemEntity.getItem())) {
				itemEntity.setItem(itemEntity.getItem()); // send update packet
			}
		}
	}

	public void grab(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer) {
		if (pLevel.isClientSide || !(pLevel.getBlockEntity(pPos) instanceof Container be)) {
			return;
		}
		ItemStack item = be.getItem(0);
		if (item.isEmpty()) {
			return;
		}
		be.setItem(0, ItemStack.EMPTY);
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

	@Override
	public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
		if (pStack.hasCustomHoverName() && pLevel.getBlockEntity(pPos) instanceof BaseContainerBlockEntity be) {
			be.setCustomName(pStack.getHoverName());
		}
	}

	@Override
	public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
		if (!pState.is(pNewState.getBlock()) && pLevel.getBlockEntity(pPos) instanceof Container container) {
			Containers.dropContents(pLevel, pPos, container);
			pLevel.updateNeighbourForOutputSignal(pPos, this);
		}

		super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState pState) {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState pBlockState, Level pLevel, BlockPos pPos) {
		return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(pLevel.getBlockEntity(pPos));
	}

	public boolean canBeDestroyed(BlockState blockState, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		return !(level.getBlockEntity(pos) instanceof Container container) || container.isEmpty();
	}
}
