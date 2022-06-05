package org.teacon.xkdeco.block;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
import org.jetbrains.annotations.NotNull;
import org.teacon.xkdeco.blockentity.BlockDisplayBlockEntity;
import org.teacon.xkdeco.util.MathUtil;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

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
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level worldIn, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand handIn, BlockHitResult hit) {
        BlockEntity be = worldIn.getBlockEntity(pos);
        if (!(be instanceof BlockDisplayBlockEntity blockEntity)) return InteractionResult.PASS;

        boolean swapItem = hit.getDirection() == Direction.UP
                && MathUtil.containsInclusive(TOP.bounds(), hit.getLocation().subtract(Vec3.atLowerCornerOf(pos)));
        if (swapItem) {
            ItemStack handItem = player.getItemInHand(handIn);
            if (!(handItem.getItem() instanceof BlockItem) && !handItem.isEmpty()) {
                return InteractionResult.CONSUME_PARTIAL;
            }
        }

        if (worldIn.isClientSide()) return InteractionResult.SUCCESS;

        if (swapItem) {
            ItemStack temp = player.getItemInHand(handIn).copy();
            player.setItemInHand(handIn, blockEntity.getItem().copy());
            blockEntity.setItem(temp);
        } else if (blockEntity.getSelectedProperty().isEmpty()) {
            message(player, new TranslatableComponent("item.minecraft.debug_stick.empty",
                    Objects.requireNonNull(blockEntity.getStoredBlockState().getBlock().getRegistryName()).toString()));
        } else if (hit.getLocation().subtract(Vec3.atLowerCornerOf(pos)).y() > 0.5) {
            Property<?> property = blockEntity.getSelectedProperty().get();
            blockEntity.setStoredBlockState(cycleState(blockEntity.getStoredBlockState(), property, false));
            message(player, new TranslatableComponent("\"%s\" to %s",
                    property.getName(), getValueName(blockEntity.getStoredBlockState(), property)));
        } else {
            BlockState blockState = blockEntity.getStoredBlockState();
            Property<?> newProperty = getRelative(blockState.getProperties(), blockEntity.getSelectedProperty().get(), false);
            blockEntity.setSelectedProperty(newProperty);
            message(player, new TranslatableComponent("selected \"%s\" (%s)",
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
        return (T)(pBackwards ? Util.findPreviousInIterable(pAllowedValues, pCurrentValue) : Util.findNextInIterable(pAllowedValues, pCurrentValue));
    }

    // borrowed from DebugStickItem#message
    private static void message(Player pPlayer, Component pMessageComponent) {
        ((ServerPlayer)pPlayer).sendMessage(pMessageComponent, ChatType.GAME_INFO, Util.NIL_UUID);
    }

    // borrowed from DebugStickItem#getNameHelper
    private static <T extends Comparable<T>> String getValueName(BlockState pState, Property<T> pProperty) {
        return pProperty.getName(pState.getValue(pProperty));
    }
}
