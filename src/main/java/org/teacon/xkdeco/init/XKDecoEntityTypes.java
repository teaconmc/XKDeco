package org.teacon.xkdeco.init;

import org.teacon.xkdeco.block.BlockDisplayBlock;
import org.teacon.xkdeco.block.HologramBlock;
import org.teacon.xkdeco.block.ItemDisplayBlock;
import org.teacon.xkdeco.block.WardrobeBlock;
import org.teacon.xkdeco.blockentity.BlockDisplayBlockEntity;
import org.teacon.xkdeco.blockentity.HologramBlockEntity;
import org.teacon.xkdeco.blockentity.ItemDisplayBlockEntity;
import org.teacon.xkdeco.blockentity.WardrobeBlockEntity;

import net.minecraft.world.level.block.entity.BlockEntityType;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.KiwiGO;
import snownee.kiwi.KiwiModule;

@KiwiModule("entity_types")
public class XKDecoEntityTypes extends AbstractModule {
	public static final KiwiGO<BlockEntityType<ItemDisplayBlockEntity>> ITEM_DISPLAY = blockEntity(
			(blockPos, blockState) -> new ItemDisplayBlockEntity(blockPos, blockState, false),			ItemDisplayBlock.class);
	public static final KiwiGO<BlockEntityType<ItemDisplayBlockEntity>> ITEM_PROJECTOR = blockEntity(
			(blockPos, blockState) -> new ItemDisplayBlockEntity(blockPos, blockState, true),			ItemDisplayBlock.class);
	public static final KiwiGO<BlockEntityType<BlockDisplayBlockEntity>> BLOCK_DISPLAY = blockEntity(
			BlockDisplayBlockEntity::new,			BlockDisplayBlock.class);
	public static final KiwiGO<BlockEntityType<WardrobeBlockEntity>> WARDROBE = blockEntity(
			WardrobeBlockEntity::new,			WardrobeBlock.class);
	public static final KiwiGO<BlockEntityType<HologramBlockEntity>> HOLOGRAM = blockEntity(
			HologramBlockEntity::new,			HologramBlock.class);
}
