package org.teacon.xkdeco.init;

import org.teacon.xkdeco.block.BlockDisplayBlock;
import org.teacon.xkdeco.block.ItemDisplayBlock;
import org.teacon.xkdeco.block.WardrobeBlock;
import org.teacon.xkdeco.blockentity.BlockDisplayBlockEntity;
import org.teacon.xkdeco.blockentity.ItemDisplayBlockEntity;
import org.teacon.xkdeco.blockentity.WardrobeBlockEntity;

import net.minecraft.world.level.block.entity.BlockEntityType;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.KiwiGO;
import snownee.kiwi.KiwiModule;

@KiwiModule("entity_types")
public class XKDecoEntityTypes extends AbstractModule {
	public static final KiwiGO<BlockEntityType<ItemDisplayBlockEntity>> ITEM_DISPLAY = blockEntity(
			(blockPos, blockState) -> new ItemDisplayBlockEntity(blockPos, blockState, false),
			null,
			ItemDisplayBlock.class);
	public static final KiwiGO<BlockEntityType<ItemDisplayBlockEntity>> ITEM_PROJECTOR = blockEntity(
			(blockPos, blockState) -> new ItemDisplayBlockEntity(blockPos, blockState, true),
			null,
			ItemDisplayBlock.class);
	public static final KiwiGO<BlockEntityType<BlockDisplayBlockEntity>> BLOCK_DISPLAY = blockEntity(
			BlockDisplayBlockEntity::new,
			null,
			BlockDisplayBlock.class);
	public static final KiwiGO<BlockEntityType<WardrobeBlockEntity>> WARDROBE = blockEntity(
			WardrobeBlockEntity::new,
			null,
			WardrobeBlock.class);
//	@SuppressWarnings("deprecation")
//	public static final KiwiGO<BlockEntityType<MimicWallBlockEntity>> MIMIC_WALL = blockEntity(
//			MimicWallBlockEntity::new,
//			null,
//			MimicWallBlock.class);
}
