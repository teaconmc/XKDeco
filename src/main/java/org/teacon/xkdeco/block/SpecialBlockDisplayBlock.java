package org.teacon.xkdeco.block;

import java.util.Objects;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.teacon.xkdeco.blockentity.BlockDisplayBlockEntity;
import org.teacon.xkdeco.util.MathUtil;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.registries.ForgeRegistries;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public final class SpecialBlockDisplayBlock extends BaseEntityBlock implements XKDecoBlock.Special {
	private static final VoxelShape TOP = Block.box(0, 11, 0, 16, 16, 16);
	private static final VoxelShape NECK = Block.box(2, 8, 2, 14, 11, 14);
	private static final VoxelShape BOTTOM = Shapes.or(
			Block.box(0, 0, 0, 4, 2, 4),
			Block.box(12, 0, 0, 16, 2, 4),
			Block.box(0, 0, 12, 4, 2, 16),
			Block.box(12, 0, 12, 16, 2, 16),
			Block.box(0, 2, 0, 16, 8, 16));
	private static final VoxelShape SHAPE = Shapes.or(TOP, NECK, BOTTOM);

	public SpecialBlockDisplayBlock(Properties properties) {
		super(properties);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
		if (!pState.is(pNewState.getBlock())) {
			this.dropContent(pLevel, pPos);
			super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
		}
	}

	/**
	 * Borrowed from JukeboxBlock#dropRecording
	 */
	private void dropContent(Level pLevel, BlockPos pPos) {
		if (!pLevel.isClientSide) {
			BlockEntity blockentity = pLevel.getBlockEntity(pPos);
			if (blockentity instanceof BlockDisplayBlockEntity blockDisplayBlockEntity) {
				ItemStack itemstack = blockDisplayBlockEntity.getItem();
				if (!itemstack.isEmpty()) {
					pLevel.levelEvent(1010, pPos, 0);
					blockDisplayBlockEntity.clearContent();
					ItemEntity itementity = new ItemEntity(
							pLevel,
							pPos.getX() + pLevel.random.nextFloat() * 0.7F + 0.15F,
							pPos.getY() + pLevel.random.nextFloat() * 0.7F + 0.060000002F + 0.6D,
							pPos.getZ() + pLevel.random.nextFloat() * 0.7F + 0.15F,
							itemstack.copy());
					itementity.setDefaultPickUpDelay();
					pLevel.addFreshEntity(itementity);
				}
			}
		}
	}

	/**
	 * Handle special conditions when block is placed with NBT
	 */
	@Override
	public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
		super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
		CompoundTag compoundtag = BlockItem.getBlockEntityData(pStack);
		if (compoundtag != null && compoundtag.contains(BlockDisplayBlockEntity.ITEMSTACK_NBT_KEY)) {
			pLevel.setBlock(pPos, pState, 2);
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public @NotNull RenderShape getRenderShape(@NotNull BlockState pState) {
		return RenderShape.MODEL;
	}

	@SuppressWarnings("deprecation")
	@Override
	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		return SHAPE;
	}

	@Override
	public @NotNull BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return new BlockDisplayBlockEntity(pPos, pState);
	}

	@SuppressWarnings("deprecation")
	@Override
	public @NotNull InteractionResult use(
			@NotNull BlockState state,
			@NotNull Level worldIn,
			@NotNull BlockPos pos,
			@NotNull Player player,
			@NotNull InteractionHand handIn,
			BlockHitResult hit) {
		var be = worldIn.getBlockEntity(pos);
		if (!(be instanceof BlockDisplayBlockEntity blockEntity)) {
			return InteractionResult.PASS;
		}

		var swapItem = hit.getDirection() == Direction.UP
				&& MathUtil.containsInclusive(TOP.bounds(), hit.getLocation().subtract(Vec3.atLowerCornerOf(pos)));
		if (swapItem) {
			var handItem = player.getItemInHand(handIn);
			if (!(handItem.getItem() instanceof BlockItem) && !handItem.isEmpty()) {
				return InteractionResult.CONSUME_PARTIAL;
			}
		}

		if (worldIn.isClientSide()) {
			return InteractionResult.SUCCESS;
		}

		if (swapItem) {
			var temp = player.getItemInHand(handIn).copy();
			player.setItemInHand(handIn, blockEntity.getItem().copy());
			blockEntity.setItem(temp);
		} else if (blockEntity.getSelectedProperty().isEmpty()) {
			var blockRegName = ForgeRegistries.BLOCKS.getKey(blockEntity.getStoredBlockState().getBlock());
			message(player, Component.translatable("item.minecraft.debug_stick.empty", Objects.requireNonNull(blockRegName).toString()));
		} else if (hit.getLocation().subtract(Vec3.atLowerCornerOf(pos)).y() > 0.5) {
			var property = blockEntity.getSelectedProperty().get();
			blockEntity.setStoredBlockState(cycleState(blockEntity.getStoredBlockState(), property, false));
			message(player, Component.translatable("\"%s\" to %s",
					property.getName(), getValueName(blockEntity.getStoredBlockState(), property)));
		} else {
			var blockState = blockEntity.getStoredBlockState();
			var newProperty = getRelative(blockState.getProperties(), blockEntity.getSelectedProperty().get(), false);
			blockEntity.setSelectedProperty(newProperty);
			message(player, Component.translatable("selected \"%s\" (%s)",
					newProperty.getName(), getValueName(blockState, newProperty)));
		}

		return InteractionResult.SUCCESS;
	}

	// borrowed from DebugStickItem#cycleState
	private static <T extends Comparable<T>> BlockState cycleState(BlockState pState, Property<T> pProperty, boolean pBackwards) {
		return pState.setValue(pProperty, getRelative(pProperty.getPossibleValues(), pState.getValue(pProperty), pBackwards));
	}

	// borrowed from DebugStickItem#getRelative
	private static <T> T getRelative(Iterable<T> pAllowedValues, @javax.annotation.Nullable T pCurrentValue, boolean pBackwards) {
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
