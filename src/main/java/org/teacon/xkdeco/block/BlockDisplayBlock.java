package org.teacon.xkdeco.block;

import java.util.Objects;

import net.minecraft.world.ItemInteractionResult;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.teacon.xkdeco.blockentity.BlockDisplayBlockEntity;

import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import snownee.kiwi.util.NotNullByDefault;

@NotNullByDefault
public final class BlockDisplayBlock extends DisplayBlock {
	public BlockDisplayBlock(Properties properties) {
		super(properties);
	}

	@Override
	public @NotNull BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return new BlockDisplayBlockEntity(pPos, pState);
	}

	@Override
	public boolean canBeDestroyed(BlockState blockState, Level level, BlockPos pos, Player player, BlockHitResult hit) {
		return !(level.getBlockEntity(pos) instanceof BlockDisplayBlockEntity be) || be.getStoredBlockState().isAir();
	}

	@Override
	protected ItemInteractionResult useTop(
			ItemStack held,
			BlockState pState,
			Level pLevel,
			BlockPos pPos,
			Player pPlayer,
			InteractionHand pHand,
			BlockHitResult pHit) {
		if (!(pLevel.getBlockEntity(pPos) instanceof BlockDisplayBlockEntity be)) {
			return ItemInteractionResult.FAIL;
		}
		boolean empty = be.getStoredBlockState().isAir();
		ItemInteractionResult result = super.useTop(held, pState, pLevel, pPos, pPlayer, pHand, pHit);
		if (!pLevel.isClientSide && empty && !be.getStoredBlockState().isAir()) {
			Block block = be.getStoredBlockState().getBlock();
			BlockState blockState = block.getStateForPlacement(new BlockPlaceContext(pPlayer, pHand, held, pHit));
			if (blockState != null) {
				be.setStoredBlockState(blockState);
			}
		}
		return result;
	}

	@Override
	protected ItemInteractionResult useSide(
			ItemStack held,
			BlockState pState,
			Level pLevel,
			BlockPos pPos,
			Player pPlayer,
			InteractionHand pHand,
			BlockHitResult pHit) {
		if (!(pLevel.getBlockEntity(pPos) instanceof BlockDisplayBlockEntity be)) {
			return ItemInteractionResult.FAIL;
		}
		if (pLevel.isClientSide) {
			return ItemInteractionResult.SUCCESS;
		}
		if (checkEmptyProperties(pPlayer, be)) {
			return ItemInteractionResult.CONSUME;
		}
		var property = Objects.requireNonNull(be.getSelectedProperty());
		be.setStoredBlockState(cycleState(be.getStoredBlockState(), property, pPlayer.isSecondaryUseActive()));
		message(pPlayer, Component.translatable(
				Items.DEBUG_STICK.getDescriptionId() + ".update",
				property.getName(), getValueName(be.getStoredBlockState(), property)));
		return ItemInteractionResult.CONSUME;
	}

	@Override
	protected void clickSide(BlockState blockState, Level level, BlockPos pos, ServerPlayer player, BlockHitResult hit) {
		if (!(level.getBlockEntity(pos) instanceof BlockDisplayBlockEntity be)) {
			return;
		}
		if (checkEmptyProperties(player, be)) {
			return;
		}
		var property = Objects.requireNonNull(be.getSelectedProperty());
		var newProperty = getRelative(be.getStoredBlockState().getProperties(), property, player.isSecondaryUseActive());
		be.setSelectedProperty(newProperty);
		message(player, Component.translatable(Items.DEBUG_STICK.getDescriptionId() + ".select",
				newProperty.getName(), getValueName(be.getStoredBlockState(), newProperty)));
	}

	private static boolean checkEmptyProperties(Player player, BlockDisplayBlockEntity be) {
		if (be.getSelectedProperty() == null) {
			MutableComponent s = be.getStoredBlockState().getBlock().getName();
			message(player, Component.translatable(Items.DEBUG_STICK.getDescriptionId() + ".empty", s));
			return true;
		}
		return false;
	}

	// borrowed from DebugStickItem#cycleState
	private static <T extends Comparable<T>> BlockState cycleState(BlockState pState, Property<T> pProperty, boolean pBackwards) {
		return pState.setValue(pProperty, getRelative(pProperty.getPossibleValues(), pState.getValue(pProperty), pBackwards));
	}

	// borrowed from DebugStickItem#getRelative
	private static <T> T getRelative(Iterable<T> pAllowedValues, @Nullable T pCurrentValue, boolean pBackwards) {
		return pBackwards ? Util.findPreviousInIterable(pAllowedValues, pCurrentValue) : Util.findNextInIterable(
				pAllowedValues,
				pCurrentValue);
	}

	// borrowed from DebugStickItem#message
	private static void message(Player pPlayer, Component pMessageComponent) {
		((ServerPlayer) pPlayer).sendSystemMessage(pMessageComponent, true);
	}

	// borrowed from DebugStickItem#getNameHelper
	private static <T extends Comparable<T>> String getValueName(BlockState pState, Property<T> pProperty) {
		return pProperty.getName(pState.getValue(pProperty));
	}
}
