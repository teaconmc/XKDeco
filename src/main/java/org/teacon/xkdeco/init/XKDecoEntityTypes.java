package org.teacon.xkdeco.init;

import org.teacon.xkdeco.block.BlockDisplayBlock;
import org.teacon.xkdeco.block.ItemDisplayBlock;
import org.teacon.xkdeco.block.MimicWallBlock;
import org.teacon.xkdeco.block.WardrobeBlock;
import org.teacon.xkdeco.blockentity.BlockDisplayBlockEntity;
import org.teacon.xkdeco.blockentity.ItemDisplayBlockEntity;
import org.teacon.xkdeco.blockentity.MimicWallBlockEntity;
import org.teacon.xkdeco.blockentity.WardrobeBlockEntity;
import org.teacon.xkdeco.entity.CushionEntity;

import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.KiwiGO;
import snownee.kiwi.KiwiModule;
import snownee.kiwi.util.KiwiEntityTypeBuilder;

@KiwiModule("entity_types")
public class XKDecoEntityTypes extends AbstractModule {
	public static final KiwiGO<BlockEntityType<ItemDisplayBlockEntity>> ITEM_DISPLAY = blockEntity(
			ItemDisplayBlockEntity::new,
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
	@SuppressWarnings("deprecation")
	public static final KiwiGO<BlockEntityType<MimicWallBlockEntity>> MIMIC_WALL = blockEntity(
			MimicWallBlockEntity::new,
			null,
			MimicWallBlock.class);

	public static final KiwiGO<EntityType<CushionEntity>> CUSHION = go(() -> KiwiEntityTypeBuilder.create()
			.<CushionEntity>entityFactory(CushionEntity::new)
			.dimensions(EntityDimensions.scalable(1F / 256F, 1F / 256F))
			.trackRangeChunks(16)
			.disableSummon()
			.build());
}
