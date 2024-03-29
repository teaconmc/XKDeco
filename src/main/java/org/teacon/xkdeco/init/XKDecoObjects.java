package org.teacon.xkdeco.init;

import static org.teacon.xkdeco.block.setting.KBlockSettings.copyProperties;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.teacon.xkdeco.XKDeco;
import org.teacon.xkdeco.block.BasicBlock;
import org.teacon.xkdeco.block.BlockDisplayBlock;
import org.teacon.xkdeco.block.ItemDisplayBlock;
import org.teacon.xkdeco.block.MimicWallBlock;
import org.teacon.xkdeco.block.RoofBlock;
import org.teacon.xkdeco.block.RoofEaveBlock;
import org.teacon.xkdeco.block.RoofEndBlock;
import org.teacon.xkdeco.block.RoofFlatBlock;
import org.teacon.xkdeco.block.RoofHorizontalShiftBlock;
import org.teacon.xkdeco.block.RoofRidgeBlock;
import org.teacon.xkdeco.block.RoofRidgeEndAsianBlock;
import org.teacon.xkdeco.block.RoofTipBlock;
import org.teacon.xkdeco.block.WardrobeBlock;
import org.teacon.xkdeco.block.impl.MetalLadderCanSurviveHandler;
import org.teacon.xkdeco.block.loader.KBlockComponents;
import org.teacon.xkdeco.block.setting.CanSurviveHandler;
import org.teacon.xkdeco.block.setting.CycleVariantsComponent;
import org.teacon.xkdeco.block.setting.FrontAndTopComponent;
import org.teacon.xkdeco.block.setting.GlassType;
import org.teacon.xkdeco.block.setting.KBlockSettings;
import org.teacon.xkdeco.block.setting.MouldingComponent;
import org.teacon.xkdeco.block.setting.ShapeGenerator;
import org.teacon.xkdeco.block.setting.ShapeStorage;
import org.teacon.xkdeco.block.setting.StackableComponent;
import org.teacon.xkdeco.blockentity.BlockDisplayBlockEntity;
import org.teacon.xkdeco.blockentity.ItemDisplayBlockEntity;
import org.teacon.xkdeco.blockentity.MimicWallBlockEntity;
import org.teacon.xkdeco.blockentity.WardrobeBlockEntity;
import org.teacon.xkdeco.entity.CushionEntity;
import org.teacon.xkdeco.item.MimicWallItem;

import com.google.common.collect.Lists;
import com.mojang.datafixers.DSL;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;
import snownee.kiwi.datagen.GameObjectLookup;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public final class XKDecoObjects {
	public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, XKDeco.ID);
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, XKDeco.ID);
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, XKDeco.ID);
	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY = DeferredRegister.create(
			ForgeRegistries.BLOCK_ENTITY_TYPES,
			XKDeco.ID);

	public static final String CUSHION_ENTITY = "cushion";

	public static final String WALL_BLOCK_ENTITY = "mimic_wall";
	public static final String ITEM_DISPLAY_BLOCK_ENTITY = "item_display";
	public static final String BLOCK_DISPLAY_BLOCK_ENTITY = "block_display";
	public static final String WARDROBE_BLOCK_ENTITY = "wardrobe";

	public static final String GRASS_PREFIX = "grass_";
	public static final String LINED_PREFIX = "lined_";
	public static final String WILLOW_PREFIX = "willow_";
	public static final String LUXURY_PREFIX = "luxury_";
	public static final String PAINTED_PREFIX = "painted_";
	public static final String CHISELED_PREFIX = "chiseled_";
	public static final String PLANTABLE_PREFIX = "plantable_";
	public static final String DOUBLE_SCREW_PREFIX = "double_screw_";
	public static final String STONE_WATER_PREFIX = "stone_water_";

	public static final String LOG_SUFFIX = "_log";
	public static final String WOOD_SUFFIX = "_wood";
	public static final String SLAB_SUFFIX = "_slab";
	public static final String STOOL_SUFFIX = "_stool";
	public static final String CHAIR_SUFFIX = "_chair";
	public static final String TABLE_SUFFIX = "_table";
	public static final String STAIRS_SUFFIX = "_stairs";
	public static final String PILLAR_SUFFIX = "_pillar";
	public static final String LEAVES_SUFFIX = "_leaves";
	public static final String BLOSSOM_SUFFIX = "_blossom";
	public static final String BIG_TABLE_SUFFIX = "_big_table";
	public static final String TALL_TABLE_SUFFIX = "_tall_table";
	public static final String LEAVES_DARK_SUFFIX = "_leaves_dark";
	public static final String ITEM_DISPLAY_SUFFIX = "_item_display";
	public static final String BLOCK_DISPLAY_SUFFIX = "_block_display";
	public static final String WARDROBE_SUFFIX = "_wardrobe";
	public static final String FALLEN_LEAVES_PREFIX = "fallen_";

	public static final String REFRESHMENT_SPECIAL = "refreshments";
	public static final String ITEM_PROJECTOR_SPECIAL = "item_projector";
	private static final ResourceKey<CreativeModeTab> STRUCTURE_TAB_KEY = ResourceKey.create(
			Registries.CREATIVE_MODE_TAB,
			XKDeco.id("structure"));

	private static void addCushionEntity() {
		ENTITIES.register(CUSHION_ENTITY, () -> EntityType.Builder.<CushionEntity>of(CushionEntity::new, MobCategory.MISC).sized(
				1F / 256F,
				1F / 256F).setTrackingRange(256).build(new ResourceLocation(XKDeco.ID, CUSHION_ENTITY).toString()));
	}

	/**
	 * basic -> horizontal
	 */
	private static void addBasic(
			String id,
			String northShapeId,
			boolean isSupportNeeded,
			KBlockSettings.Builder settings) {
//		var itemProperties = new Item.Properties();
//		VoxelShape northShape = ShapeStorage.getInstance().get(northShapeId);
//		RegistryObject<BasicBlock> block;
//		if (northShape == Shapes.block()) {
//			block = BLOCKS.register(id, () -> new BasicBlock(settings.horizontal().get()));
//		} else {
//			block = BLOCKS.register(
//					id,
//					() -> new BasicBlock(settings
//							.horizontal()
//							.shape(new ResourceLocation(northShapeId))
//							.canSurviveHandler(isSupportNeeded ? CanSurviveHandler.checkFloor() : null)
//							.get()));
//		}
//		ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties));
	}

	private static void addDirectional(String id, KBlockSettings.Builder settings) {
//		var itemProperties = new Item.Properties();
//		var block = BLOCKS.register(
//				id,
//				() -> new BasicBlock(settings.directional().get()));
//		ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties));
	}

	private static void addIsotropic(String id, KBlockSettings.Builder settings) {
//		var itemProperties = new Item.Properties();
//		if (id.startsWith("quartz_glass")) {
//			settings.glassType(GlassType.QUARTZ);
//		} else if (id.startsWith("translucent_")) {
//			settings.glassType(GlassType.TRANSLUCENT);
//		} else if (id.startsWith("toughened_glass")) {
//			settings.glassType(GlassType.TOUGHENED);
//		} else if (id.contains("glass")) {
//			settings.glassType(GlassType.CLEAR);
//		}
//		Supplier<Block> blockSupplier;
//		if (id.contains(SLAB_SUFFIX)) {
////			blockSupplier = () -> new SlabBlock(settings.get());
//			return;
//		} else if (id.contains(STAIRS_SUFFIX)) {
////			blockSupplier = () -> new StairBlock(Blocks.AIR.defaultBlockState(), settings.get());
//			return;
//		} else if (id.contains(LOG_SUFFIX) || id.contains(WOOD_SUFFIX) || id.contains(PILLAR_SUFFIX)) {
////			blockSupplier = () -> new RotatedPillarBlock(settings.get());
//			return;
//		} else if (id.contains(LINED_PREFIX) || id.contains(LUXURY_PREFIX) || id.contains(PAINTED_PREFIX) || id.contains(CHISELED_PREFIX) ||
//				id.contains(DOUBLE_SCREW_PREFIX)) {
////			blockSupplier = () -> new RotatedPillarBlock(settings.get());
//			return;
//		} else if (id.contains(BIG_TABLE_SUFFIX) || id.contains(TALL_TABLE_SUFFIX)) {
//			blockSupplier = () -> new BasicBlock(BlockSettingPresets.thingy(null)
//					.shape(XKDeco.id("big_table"))
//					.get());
//		} else if (id.contains(TABLE_SUFFIX)) {
//			blockSupplier = () -> new BasicBlock(BlockSettingPresets.thingy(null)
//					.shape(XKDeco.id("table"))
//					.get());
//		} else if (id.contains("_trapdoor")) {
////			blockSupplier = () -> new TrapDoorBlock(
////					settings.get(),
////					id.contains("factory") || id.contains("steel") ? BlockSetType.IRON : BlockSetType.OAK);
//			return;
//		} else if (id.endsWith("_door")) {
////			blockSupplier = () -> new DoorBlock(
////					settings.get(),
////					id.contains("factory") || id.contains("steel") ? BlockSetType.IRON : BlockSetType.OAK);
//			return;
//		} else if (id.endsWith("_wall")) {
////			blockSupplier = () -> new WallBlock(settings.get());
//			return;
//		} else if (settings.hasComponent(KBlockComponents.WATER_LOGGABLE.getOrCreate())) {
//			blockSupplier = () -> new BasicBlock(settings.get());
//		} else {
//			blockSupplier = () -> new Block(settings.get());
//		}
//		var blockHolder = BLOCKS.register(id, blockSupplier);
//		ITEMS.register(id, () -> new BlockItem(blockHolder.get(), itemProperties));
	}

	private static void addRoof(String id, Supplier<BlockBehaviour.Properties> propFactory, boolean asian) {
		var itemProperties = new Item.Properties();
		var roof = BLOCKS.register(id, () -> new RoofBlock(propFactory.get()));
		ITEMS.register(id, () -> new BlockItem(roof.get(), itemProperties));
		var roofRidge = BLOCKS.register(id + "_ridge", () -> new RoofRidgeBlock(propFactory.get(), asian));
		ITEMS.register(id + "_ridge", () -> new BlockItem(roofRidge.get(), itemProperties));
		var roofSmallEnd = BLOCKS.register(id + "_small_end", () -> new RoofEndBlock(propFactory.get(), true));
		ITEMS.register(id + "_small_end", () -> new BlockItem(roofSmallEnd.get(), itemProperties));
		var smallRidgeEnd = BLOCKS.register(id + "_small_ridge_end", () -> {
			if (asian) {
				return new RoofRidgeEndAsianBlock(propFactory.get(), true);
			} else {
				return new RoofRidgeEndAsianBlock(propFactory.get(), true);
			}
		});
		var smallFlatEnd = BLOCKS.register(id + "_small_flat_end", () -> new RoofHorizontalShiftBlock(propFactory.get()));

		//TODO remove in the future:
		ITEMS.register(id + "_small_ridge_end", () -> new BlockItem(smallRidgeEnd.get(), itemProperties));
		ITEMS.register(id + "_small_flat_end", () -> new BlockItem(smallFlatEnd.get(), itemProperties));

		var roofSmallEave = BLOCKS.register(id + "_small_eave", () -> new RoofEaveBlock(propFactory.get(), true));
		ITEMS.register(id + "_small_eave", () -> new BlockItem(roofSmallEave.get(), itemProperties));
		var roofFlat = BLOCKS.register(id + "_flat", () -> new RoofFlatBlock(propFactory.get()));
		ITEMS.register(id + "_flat", () -> new BlockItem(roofFlat.get(), itemProperties));

		if (!asian) {
			return;
		}

		var roofEave = BLOCKS.register(id + "_eave", () -> new RoofEaveBlock(propFactory.get(), false));
		ITEMS.register(id + "_eave", () -> new BlockItem(roofEave.get(), itemProperties));
		var roofEnd = BLOCKS.register(id + "_end", () -> new RoofEndBlock(propFactory.get(), false));
		ITEMS.register(id + "_end", () -> new BlockItem(roofEnd.get(), itemProperties));
		var roofRidgeEnd = BLOCKS.register(id + "_ridge_end", () -> new RoofRidgeEndAsianBlock(propFactory.get(), false));

		//TODO remove in the future:
		ITEMS.register(id + "_ridge_end", () -> new BlockItem(roofRidgeEnd.get(), itemProperties));

//		var roofDeco = BLOCKS.register(id + "_deco", () -> new HorizontalShiftBlock(propFactory.get()));
//		ITEMS.register(id + "_deco", () -> new BlockItem(roofDeco.get(), itemProperties));
//		var roofDecoOblique = BLOCKS.register(id + "_deco_oblique", () -> new HorizontalShiftBlock(propFactory.get()));
//		ITEMS.register(id + "_deco_oblique", () -> new BlockItem(roofDecoOblique.get(), itemProperties));
		var roofTip = BLOCKS.register(id + "_tip", () -> new RoofTipBlock(propFactory.get()));
		ITEMS.register(id + "_tip", () -> new BlockItem(roofTip.get(), itemProperties));
	}

	private static void addPlant(String id, KBlockSettings.Builder settings) {
//		var itemProperties = new Item.Properties();
//		if (id.contains(LEAVES_SUFFIX) || id.contains(BLOSSOM_SUFFIX)) {
//			if (id.startsWith(FALLEN_LEAVES_PREFIX)) {
//				var block = BLOCKS.register(id, () -> new FallenLeavesBlock(settings.get()));
//				ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties));
//			}
//			var block = BLOCKS.register(id, () -> new LeavesBlock(settings.get()));
//			ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties));
//		} else {
//			throw new IllegalArgumentException("Illegal id (" + id + ") for plant blocks");
//		}
	}

	private static void addSpecial(String id, KBlockSettings.Builder settings) {
		var itemProperties = new Item.Properties();
		if (id.equals(REFRESHMENT_SPECIAL)) {
			var block = BLOCKS.register(id, () -> new BasicBlock(settings.get()));
			ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties));
		} else if (id.contains(ITEM_DISPLAY_SUFFIX) || id.equals(ITEM_PROJECTOR_SPECIAL)) {
//			var block = BLOCKS.register(id, () -> new ItemDisplayBlock(settings.get()));
//			ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties));
		} else if (id.contains(BLOCK_DISPLAY_SUFFIX)) {
//			var block = BLOCKS.register(id, () -> new BlockDisplayBlock(settings.get()));
//			ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties));
		} else if (id.contains(WARDROBE_SUFFIX)) {
			var block = BLOCKS.register(id, () -> new WardrobeBlock(settings.removeComponent(KBlockComponents.WATER_LOGGABLE.get()).get()));
			ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties));
		} else {
			throw new IllegalArgumentException("Illegal id (" + id + ") for special blocks");
		}
	}

	private static void addItem(String id) {
		var itemProperties = new Item.Properties();
		ITEMS.register(id, () -> new Item(itemProperties));
	}

	private static void addDisplayBlockEntity() {
		BLOCK_ENTITY.register(ITEM_DISPLAY_BLOCK_ENTITY, () -> BlockEntityType.Builder.of(
				ItemDisplayBlockEntity::new,
				BLOCKS.getEntries()
						.stream()
						.map(RegistryObject::get)
						.filter(b -> b instanceof ItemDisplayBlock)
						.toArray(Block[]::new)).build(DSL.remainderType()));
		BLOCK_ENTITY.register(BLOCK_DISPLAY_BLOCK_ENTITY, () -> BlockEntityType.Builder.of(
				BlockDisplayBlockEntity::new,
				BLOCKS.getEntries()
						.stream()
						.map(RegistryObject::get)
						.filter(b -> b instanceof BlockDisplayBlock)
						.toArray(Block[]::new)).build(DSL.remainderType()));
	}

	private static void addWardrobeBlockEntity() {
		BLOCK_ENTITY.register(WARDROBE_BLOCK_ENTITY, () -> BlockEntityType.Builder.of(
				WardrobeBlockEntity::new,
				BLOCKS.getEntries()
						.stream()
						.map(RegistryObject::get)
						.filter(b -> b instanceof WardrobeBlock)
						.toArray(Block[]::new)).build(DSL.remainderType()));
	}

	public static void addMimicWallBlocks(RegisterEvent event) {
		var vanillaWalls = List.of(
				Blocks.COBBLESTONE_WALL,
				Blocks.MOSSY_COBBLESTONE_WALL,
				Blocks.BRICK_WALL,
				Blocks.PRISMARINE_WALL,
				Blocks.RED_SANDSTONE_WALL,
				Blocks.MOSSY_STONE_BRICK_WALL,
				Blocks.GRANITE_WALL,
				Blocks.STONE_BRICK_WALL,
				Blocks.MUD_BRICK_WALL,
				Blocks.NETHER_BRICK_WALL,
				Blocks.ANDESITE_WALL,
				Blocks.RED_NETHER_BRICK_WALL,
				Blocks.SANDSTONE_WALL,
				Blocks.END_STONE_BRICK_WALL,
				Blocks.DIORITE_WALL,
				Blocks.BLACKSTONE_WALL,
				Blocks.POLISHED_BLACKSTONE_BRICK_WALL,
				Blocks.POLISHED_BLACKSTONE_WALL,
				Blocks.COBBLED_DEEPSLATE_WALL,
				Blocks.POLISHED_DEEPSLATE_WALL,
				Blocks.DEEPSLATE_TILE_WALL,
				Blocks.DEEPSLATE_BRICK_WALL);
		for (var block : vanillaWalls) {
			if (block instanceof WallBlock wall) {
				var registryName = Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(block));
				var name = MimicWallBlock.toMimicId(registryName);
				event.register(ForgeRegistries.Keys.BLOCKS, XKDeco.id(name), () -> new MimicWallBlock(wall));
			}
		}
	}

	public static void addMimicWallItems(RegisterEvent event) {
		for (var entry : ForgeRegistries.BLOCKS.getEntries()) {
			var block = entry.getValue();
			if (block instanceof MimicWallBlock wall) {
				var registryName = entry.getKey().location();
				event.register(ForgeRegistries.Keys.ITEMS, registryName, () -> new MimicWallItem(wall, new Item.Properties()));
			}
		}
	}

	public static void addMimicWallBlockEntity(RegisterEvent event) {
		var blocks = ForgeRegistries.BLOCKS.getValues().stream().filter(MimicWallBlock.class::isInstance).toArray(Block[]::new);
		var registryName = new ResourceLocation(XKDeco.ID, WALL_BLOCK_ENTITY);
		event.register(
				ForgeRegistries.Keys.BLOCK_ENTITY_TYPES,
				registryName,
				() -> BlockEntityType.Builder.of(MimicWallBlockEntity::new, blocks).build(DSL.remainderType()));
	}

	public static void addMimicWallTags(TagsUpdatedEvent event) {
		var registry = event.getRegistryAccess().registry(ForgeRegistries.BLOCKS.getRegistryKey()).orElseThrow(RuntimeException::new);
		registry.bindTags(registry.getTagNames().collect(Collectors.toMap(Function.identity(), tagKey -> {
			var tags = Lists.newArrayList(registry.getTagOrEmpty(tagKey));
			if (BlockTags.WALLS.equals(tagKey)) {
				for (var block : ForgeRegistries.BLOCKS.getValues()) {
					if (block instanceof MimicWallBlock) {
						tags.add(ForgeRegistries.BLOCKS.getHolder(block).orElseThrow());
					}
				}
			}
			return tags;
		})));
		Blocks.rebuildCache();
	}

	public static void addMimicWallsToTab(BuildCreativeModeTabContentsEvent event) {
		if (STRUCTURE_TAB_KEY.equals(event.getTabKey())) {
			GameObjectLookup.all(Registries.BLOCK, XKDeco.ID).forEach(block -> {
				if (block instanceof MimicWallBlock) {
					event.accept(block);
				}
			});
		}
	}

	static {
		addCushionEntity();

		addIsotropic("black_tiles", BlockSettingPresets.blackTiles());
		addIsotropic("black_tile_slab", BlockSettingPresets.blackTiles());
		addIsotropic("black_tile_stairs", BlockSettingPresets.blackTiles());

		addIsotropic("cyan_tiles", BlockSettingPresets.cyanTiles());
		addIsotropic("cyan_tile_slab", BlockSettingPresets.cyanTiles());
		addIsotropic("cyan_tile_stairs", BlockSettingPresets.cyanTiles());

		addIsotropic("yellow_tiles", BlockSettingPresets.yellowTiles());
		addIsotropic("yellow_tile_slab", BlockSettingPresets.yellowTiles());
		addIsotropic("yellow_tile_stairs", BlockSettingPresets.yellowTiles());

		addIsotropic("blue_tiles", BlockSettingPresets.blueTiles());
		addIsotropic("blue_tile_slab", BlockSettingPresets.blueTiles());
		addIsotropic("blue_tile_stairs", BlockSettingPresets.blueTiles());

		addIsotropic("green_tiles", BlockSettingPresets.greenTiles());
		addIsotropic("green_tile_slab", BlockSettingPresets.greenTiles());
		addIsotropic("green_tile_stairs", BlockSettingPresets.greenTiles());

		addIsotropic("red_tiles", BlockSettingPresets.redTiles());
		addIsotropic("red_tile_slab", BlockSettingPresets.redTiles());
		addIsotropic("red_tile_stairs", BlockSettingPresets.redTiles());

		addIsotropic("steel_tiles", BlockSettingPresets.steel());
		addIsotropic("steel_tile_slab", BlockSettingPresets.steel());
		addIsotropic("steel_tile_stairs", BlockSettingPresets.steel());

		addIsotropic("copper_tiles", copyProperties(Blocks.COPPER_BLOCK));
		addIsotropic("copper_tile_slab", copyProperties(Blocks.COPPER_BLOCK));
		addIsotropic("copper_tile_stairs", copyProperties(Blocks.COPPER_BLOCK));

		addIsotropic("glass_tiles", copyProperties(Blocks.GLASS));
		addIsotropic("glass_tile_slab", copyProperties(Blocks.GLASS));
		addIsotropic("glass_tile_stairs", copyProperties(Blocks.GLASS));
		addIsotropic("glass_trapdoor", copyProperties(Blocks.GLASS));
		addIsotropic("glass_door", copyProperties(Blocks.GLASS));

		addIsotropic("mud_wall_block", BlockSettingPresets.mudWall());
		addIsotropic("mud_wall_slab", BlockSettingPresets.mudWall());
		addIsotropic("mud_wall_stairs", BlockSettingPresets.mudWall());
		addIsotropic("mud_wall_wall", BlockSettingPresets.mudWall());

		addIsotropic("framed_mud_wall_block", BlockSettingPresets.mudWall());

		addIsotropic("lined_mud_wall_block", BlockSettingPresets.mudWall());
		addIsotropic("lined_mud_wall_slab", BlockSettingPresets.mudWall());
		addIsotropic("lined_mud_wall_stairs", BlockSettingPresets.mudWall());

		addIsotropic("crossed_mud_wall_block", BlockSettingPresets.mudWall());
		addIsotropic("crossed_mud_wall_slab", BlockSettingPresets.mudWall());
		addIsotropic("crossed_mud_wall_stairs", BlockSettingPresets.mudWall());

		addIsotropic("dirty_mud_wall_block", BlockSettingPresets.mudWall());
		addIsotropic("dirty_mud_wall_slab", BlockSettingPresets.mudWall());
		addIsotropic("dirty_mud_wall_stairs", BlockSettingPresets.mudWall());
		addIsotropic("dirty_mud_wall_wall", BlockSettingPresets.mudWall());

		addIsotropic("cyan_bricks", BlockSettingPresets.cyanTiles());
		addIsotropic("cyan_brick_slab", BlockSettingPresets.cyanTiles());
		addIsotropic("cyan_brick_stairs", BlockSettingPresets.cyanTiles());
		addIsotropic("cyan_brick_wall", BlockSettingPresets.cyanTiles());

		addIsotropic("black_bricks", BlockSettingPresets.blackTiles());
		addIsotropic("black_brick_slab", BlockSettingPresets.blackTiles());
		addIsotropic("black_brick_stairs", BlockSettingPresets.blackTiles());
		addIsotropic("black_brick_wall", BlockSettingPresets.blackTiles());

		addTreatedWood("varnished", MapColor.WOOD);
		addTreatedWood("ebony", MapColor.WOOD);
		addTreatedWood("mahogany", MapColor.WOOD);

		addIsotropic("sandstone_pillar", copyProperties(Blocks.SANDSTONE));

		addIsotropic("polished_sandstone", copyProperties(Blocks.SANDSTONE));
		addIsotropic("polished_sandstone_slab", copyProperties(Blocks.SANDSTONE));
//		addIsotropic("polished_sandstone_stairs", copyProperties(Blocks.SANDSTONE), TAB_BASIC_CONTENTS);

		addIsotropic("sandstone_bricks", copyProperties(Blocks.SANDSTONE));
		addIsotropic("sandstone_brick_slab", copyProperties(Blocks.SANDSTONE));
		addIsotropic("sandstone_brick_stairs", copyProperties(Blocks.SANDSTONE));

		addIsotropic("sandstone_small_bricks", copyProperties(Blocks.SANDSTONE));
		addIsotropic("sandstone_small_brick_slab", copyProperties(Blocks.SANDSTONE));
		addIsotropic("sandstone_small_brick_stairs", copyProperties(Blocks.SANDSTONE));

		addIsotropic("red_sandstone_pillar", copyProperties(Blocks.SANDSTONE));

		addIsotropic("polished_red_sandstone", copyProperties(Blocks.SANDSTONE));
		addIsotropic("polished_red_sandstone_slab", copyProperties(Blocks.SANDSTONE));
//		addIsotropic("polished_red_sandstone_stairs", copyProperties(Blocks.SANDSTONE), TAB_BASIC_CONTENTS);

		addIsotropic("red_sandstone_bricks", copyProperties(Blocks.STONE_BRICKS));
		addIsotropic("red_sandstone_brick_slab", copyProperties(Blocks.STONE_BRICKS));
		addIsotropic("red_sandstone_brick_stairs", copyProperties(Blocks.STONE_BRICKS));

		addIsotropic("red_sandstone_small_bricks", copyProperties(Blocks.STONE_BRICKS));
		addIsotropic("red_sandstone_small_brick_slab", copyProperties(Blocks.STONE_BRICKS));
		addIsotropic("red_sandstone_small_brick_stairs", copyProperties(Blocks.STONE_BRICKS));

		addIsotropic("stone_brick_pillar", copyProperties(Blocks.STONE_BRICKS));
		addIsotropic("stone_brick_pavement", copyProperties(Blocks.STONE_BRICKS));
		addIsotropic("stone_brick_pavement_slab", copyProperties(Blocks.STONE_BRICKS));

		addIsotropic("deepslate_pillar", copyProperties(Blocks.DEEPSLATE));
		addIsotropic("deepslate_pavement", copyProperties(Blocks.DEEPSLATE));
		addIsotropic("deepslate_pavement_slab", copyProperties(Blocks.DEEPSLATE));

		addIsotropic("mossy_deepslate_bricks", copyProperties(Blocks.STONE_BRICKS));
		addIsotropic("mossy_deepslate_brick_slab", copyProperties(Blocks.STONE_BRICKS));
		addIsotropic("mossy_deepslate_brick_stairs", copyProperties(Blocks.STONE_BRICKS));

		addIsotropic("blackstone_pillar", copyProperties(Blocks.BLACKSTONE));
		addIsotropic("blackstone_pavement", copyProperties(Blocks.BLACKSTONE));
		addIsotropic("blackstone_pavement_slab", copyProperties(Blocks.BLACKSTONE));

		addIsotropic("gilded_blackstone_bricks", copyProperties(Blocks.STONE_BRICKS));
		addIsotropic("gilded_blackstone_brick_slab", copyProperties(Blocks.STONE_BRICKS));
		addIsotropic("gilded_blackstone_brick_stairs", copyProperties(Blocks.STONE_BRICKS));
		addIsotropic("gilded_blackstone_brick_pillar", copyProperties(Blocks.STONE_BRICKS));

		addIsotropic("chiseled_gilded_blackstone", copyProperties(Blocks.GILDED_BLACKSTONE));
		addIsotropic("luxury_gilded_blackstone", copyProperties(Blocks.GILDED_BLACKSTONE));

		addIsotropic("maya_stone", copyProperties(Blocks.STONE));
		addIsotropic("maya_stone_slab", copyProperties(Blocks.STONE));
		addIsotropic("maya_stone_stairs", copyProperties(Blocks.STONE));

		addIsotropic("maya_stonebricks", copyProperties(Blocks.STONE_BRICKS));
		addIsotropic("maya_stonebrick_slab", copyProperties(Blocks.STONE_BRICKS));
		addIsotropic("maya_stonebrick_stairs", copyProperties(Blocks.STONE_BRICKS));
		addIsotropic("maya_stonebrick_wall", copyProperties(Blocks.STONE_BRICKS));

		addIsotropic("maya_bricks", copyProperties(Blocks.STONE_BRICKS));
		addIsotropic("maya_brick_slab", copyProperties(Blocks.STONE_BRICKS));
		addIsotropic("maya_brick_stairs", copyProperties(Blocks.STONE_BRICKS));
		addIsotropic("maya_brick_wall", copyProperties(Blocks.STONE_BRICKS));

		addIsotropic("maya_polished_stonebricks", copyProperties(Blocks.STONE_BRICKS));
		addIsotropic("maya_polished_stonebrick_slab", copyProperties(Blocks.STONE_BRICKS));
		addIsotropic("maya_polished_stonebrick_stairs", copyProperties(Blocks.STONE_BRICKS));

		addIsotropic("maya_mossy_stonebricks", copyProperties(Blocks.STONE_BRICKS));
		addIsotropic("maya_mossy_stonebrick_slab", copyProperties(Blocks.STONE_BRICKS));
		addIsotropic("maya_mossy_stonebrick_stairs", copyProperties(Blocks.STONE_BRICKS));
		addIsotropic("maya_mossy_stonebrick_wall", copyProperties(Blocks.STONE_BRICKS));

		addIsotropic("maya_mossy_bricks", copyProperties(Blocks.STONE_BRICKS));
		addIsotropic("maya_mossy_brick_slab", copyProperties(Blocks.STONE_BRICKS));
		addIsotropic("maya_mossy_brick_stairs", copyProperties(Blocks.STONE_BRICKS));
		addIsotropic("maya_mossy_brick_wall", copyProperties(Blocks.STONE_BRICKS));

		addIsotropic("maya_chiseled_stonebricks", copyProperties(Blocks.STONE_BRICKS));
		addIsotropic("maya_cut_stonebricks", copyProperties(Blocks.STONE_BRICKS));

		addBasic("maya_single_screw_thread_stone", "block", false, copyProperties(Blocks.STONE));
		addIsotropic("maya_double_screw_thread_stone", copyProperties(Blocks.STONE));
		addIsotropic("maya_quad_screw_thread_stone", copyProperties(Blocks.STONE));

		addIsotropic("maya_pictogram_stone", copyProperties(Blocks.STONE));
		addIsotropic(
				"maya_skull_stone",
				copyProperties(Blocks.STONE)
		);

		addIsotropic("maya_pillar", copyProperties(Blocks.STONE));
		addIsotropic("maya_mossy_pillar", copyProperties(Blocks.STONE));

		addBasic(
				"maya_crystal_skull",
				"xkdeco:maya_crystal_skull",
				false,
				copyProperties(Blocks.DEEPSLATE)
		);

		addIsotropic("aztec_stonebricks", copyProperties(Blocks.STONE_BRICKS));
		addIsotropic("aztec_stonebrick_slab", copyProperties(Blocks.STONE_BRICKS));
		addIsotropic("aztec_stonebrick_stairs", copyProperties(Blocks.STONE_BRICKS));

		addIsotropic("aztec_mossy_stonebricks", copyProperties(Blocks.STONE_BRICKS));
		addIsotropic("aztec_mossy_stonebrick_slab", copyProperties(Blocks.STONE_BRICKS));
		addIsotropic("aztec_mossy_stonebrick_stairs", copyProperties(Blocks.STONE_BRICKS));

		addIsotropic("aztec_sculpture_stone", copyProperties(Blocks.STONE_BRICKS));
		addIsotropic("aztec_chiseled_stonebricks", copyProperties(Blocks.STONE_BRICKS));
		addIsotropic("aztec_cut_stonebricks", copyProperties(Blocks.STONE_BRICKS));

		addIsotropic("inca_stone", copyProperties(Blocks.STONE));
		addIsotropic("inca_stone_slab", copyProperties(Blocks.STONE));
		addIsotropic("inca_stone_stairs", copyProperties(Blocks.STONE));

		addIsotropic("inca_stonebricks", copyProperties(Blocks.STONE_BRICKS));
		addIsotropic("inca_stonebrick_slab", copyProperties(Blocks.STONE_BRICKS));
		addIsotropic("inca_stonebrick_stairs", copyProperties(Blocks.STONE_BRICKS));

		addIsotropic("inca_bricks", copyProperties(Blocks.STONE_BRICKS));
		addIsotropic("inca_brick_slab", copyProperties(Blocks.STONE_BRICKS));
		addIsotropic("inca_brick_stairs", copyProperties(Blocks.STONE_BRICKS));

		addIsotropic("cut_obsidian", copyProperties(Blocks.OBSIDIAN));
		addIsotropic("cut_obsidian_slab", copyProperties(Blocks.OBSIDIAN));
		addIsotropic("cut_obsidian_pillar", copyProperties(Blocks.OBSIDIAN));

		addIsotropic("cut_obsidian_bricks", copyProperties(Blocks.OBSIDIAN));
		addIsotropic("cut_obsidian_brick_slab", copyProperties(Blocks.OBSIDIAN));
		addIsotropic("cut_obsidian_brick_stairs", copyProperties(Blocks.OBSIDIAN));

		addIsotropic("crying_obsidian_bricks", copyProperties(Blocks.OBSIDIAN));
		addIsotropic("crying_obsidian_brick_slab", copyProperties(Blocks.OBSIDIAN));
		addIsotropic("crying_obsidian_brick_stairs", copyProperties(Blocks.OBSIDIAN));

		addIsotropic("cut_gold_block", copyProperties(Blocks.GOLD_BLOCK));
		addIsotropic("cut_gold_block_slab", copyProperties(Blocks.GOLD_BLOCK));
		addIsotropic("cut_gold_block_stairs", copyProperties(Blocks.GOLD_BLOCK));

		addIsotropic("gold_bricks", copyProperties(Blocks.GOLD_BLOCK));
		addIsotropic("gold_brick_slab", copyProperties(Blocks.GOLD_BLOCK));
		addIsotropic("gold_brick_stairs", copyProperties(Blocks.GOLD_BLOCK));
		addIsotropic("gold_pillar", copyProperties(Blocks.GOLD_BLOCK));

		addIsotropic("chiseled_gold_block", copyProperties(Blocks.GOLD_BLOCK));
		addIsotropic("painted_gold_block", copyProperties(Blocks.GOLD_BLOCK));

		addIsotropic("bronze_block", copyProperties(Blocks.COPPER_BLOCK));
		addIsotropic("smooth_bronze_block", copyProperties(Blocks.COPPER_BLOCK));
		addIsotropic("inscription_bronze_block", copyProperties(Blocks.COPPER_BLOCK));

		addIsotropic("cut_bronze_block", copyProperties(Blocks.COPPER_BLOCK));
		addIsotropic("cut_bronze_block_slab", copyProperties(Blocks.COPPER_BLOCK));
		addIsotropic("cut_bronze_block_stairs", copyProperties(Blocks.COPPER_BLOCK));

		addIsotropic("chiseled_bronze_block", copyProperties(Blocks.COPPER_BLOCK));
		addBasic("screw_thread_bronze_block", "block", false, copyProperties(Blocks.COPPER_BLOCK));
		addIsotropic("bronze_pillar", copyProperties(Blocks.COPPER_BLOCK));

		addIsotropic("steel_block", BlockSettingPresets.steel());
		addIsotropic("smooth_steel_block", BlockSettingPresets.steel());
		addIsotropic("steel_pillar", BlockSettingPresets.steel());
		addIsotropic("steel_trapdoor", BlockSettingPresets.steel().noOcclusion());

		addIsotropic("steel_floor", BlockSettingPresets.steel());
		addIsotropic("steel_floor_slab", BlockSettingPresets.steel());
		addIsotropic("steel_floor_stairs", BlockSettingPresets.steel());

		addIsotropic("chiseled_steel_block", BlockSettingPresets.steel());
		addIsotropic("hollow_steel_block", BlockSettingPresets.hollowSteel().waterLoggable());
		addIsotropic("hollow_steel_trapdoor", BlockSettingPresets.hollowSteel());
//		addBlock(
//				"steel_safety_ladder",
//				() -> new BasicBlock(BlockSettingPresets.hollowSteel()
//						.horizontal()
//						.waterLoggable()
//						.shape(XKDeco.id("safety_ladder")).get())
//		);
//		addBlock(
//				"steel_ladder",
//				() -> new BasicBlock(BlockSettingPresets.hollowSteel()
//						.horizontal()
//						.waterLoggable()
//						.shape(XKDeco.id("ladder"))
//						.canSurviveHandler(new MetalLadderCanSurviveHandler()).get())
//		);
		addIsotropic("framed_steel_block", BlockSettingPresets.steel());

		addIsotropic("factory_block", BlockSettingPresets.steel());
		addIsotropic("factory_slab", BlockSettingPresets.steel());
		addIsotropic("factory_stairs", BlockSettingPresets.steel());
		addIsotropic("factory_trapdoor", BlockSettingPresets.steel());

		addIsotropic("factory_block_rusting", BlockSettingPresets.steel());
		addIsotropic("factory_slab_rusting", BlockSettingPresets.steel());
		addIsotropic("factory_stairs_rusting", BlockSettingPresets.steel());
		addIsotropic("factory_trapdoor_rusting", BlockSettingPresets.steel());

		addIsotropic("factory_block_rusted", BlockSettingPresets.steel());
		addIsotropic("factory_slab_rusted", BlockSettingPresets.steel());
		addIsotropic("factory_stairs_rusted", BlockSettingPresets.steel());
		addIsotropic("factory_trapdoor_rusted", BlockSettingPresets.steel());

		addIsotropic("factory_danger", BlockSettingPresets.steel());
		addIsotropic("factory_danger_rusting", BlockSettingPresets.steel());
		addIsotropic("factory_danger_rusted", BlockSettingPresets.steel());

		addIsotropic("factory_attention", BlockSettingPresets.steel());
		addIsotropic("factory_attention_rusting", BlockSettingPresets.steel());
		addIsotropic("factory_attention_rusted", BlockSettingPresets.steel());

		addIsotropic("factory_electricity", BlockSettingPresets.steel());
		addIsotropic("factory_electricity_rusting", BlockSettingPresets.steel());
		addIsotropic("factory_electricity_rusted", BlockSettingPresets.steel());

		addIsotropic("factory_toxic", BlockSettingPresets.steel());
		addIsotropic("factory_toxic_rusting", BlockSettingPresets.steel());
		addIsotropic("factory_toxic_rusted", BlockSettingPresets.steel());

		addIsotropic("factory_radiation", BlockSettingPresets.steel());
		addIsotropic("factory_radiation_rusting", BlockSettingPresets.steel());
		addIsotropic("factory_radiation_rusted", BlockSettingPresets.steel());

		addIsotropic("factory_biohazard", BlockSettingPresets.steel());
		addIsotropic("factory_biohazard_rusting", BlockSettingPresets.steel());
		addIsotropic("factory_biohazard_rusted", BlockSettingPresets.steel());

		addIsotropic("factory_lamp_block", BlockSettingPresets.lampBlock());
		addIsotropic("factory_lamp_slab", BlockSettingPresets.lampBlock());
		addIsotropic("factory_lamp_stairs", BlockSettingPresets.lampBlock());

		addIsotropic("tech_lamp_block", BlockSettingPresets.lampBlock());
		addIsotropic("tech_lamp_slab", BlockSettingPresets.lampBlock());
		addIsotropic("tech_lamp_stairs", BlockSettingPresets.lampBlock());

		addIsotropic("translucent_lamp_block", BlockSettingPresets.lampBlock().noOcclusion());
		addIsotropic("translucent_lamp_slab", BlockSettingPresets.lampBlock().noOcclusion());
		addIsotropic("translucent_lamp_stairs", BlockSettingPresets.lampBlock().noOcclusion());

//		addBlock("steel_filings", () -> new SandBlock(14406560, copyProperties(Blocks.SAND).get()));
//		addBlock("quartz_sand", () -> new SandBlock(14406560, copyProperties(Blocks.SAND).get()));
//		addBlock("toughened_sand", () -> new SandBlock(14406560, copyProperties(Blocks.SAND).get()));

		addIsotropic("quartz_glass", BlockSettingPresets.hardenedGlass());
		addIsotropic("quartz_glass_slab", BlockSettingPresets.hardenedGlass());
		addIsotropic("quartz_glass_stairs", BlockSettingPresets.hardenedGlass());

		addIsotropic("toughened_glass", BlockSettingPresets.hardenedGlass());
		addIsotropic("toughened_glass_slab", BlockSettingPresets.hardenedGlass());
		addIsotropic("toughened_glass_stairs", BlockSettingPresets.hardenedGlass());

		addRoof("black_roof", () -> BlockSettingPresets.blackTiles().noOcclusion().get(), true);
		addRoof("cyan_roof", () -> BlockSettingPresets.cyanTiles().noOcclusion().get(), true);
		addRoof("yellow_roof", () -> BlockSettingPresets.yellowTiles().noOcclusion().get(), true);
		addRoof("blue_roof", () -> BlockSettingPresets.blueTiles().noOcclusion().get(), false);
		addRoof("green_roof", () -> BlockSettingPresets.greenTiles().noOcclusion().get(), false);
		addRoof("red_roof", () -> BlockSettingPresets.redTiles().noOcclusion().get(), false);

//		addBlock("dirt_slab", () -> new SlabBlock(copyProperties(Blocks.DIRT).sustainsPlant().get()));
//		addBlock(
//				"dirt_path_slab",
//				() -> new SpecialSlabBlock(copyProperties(Blocks.DIRT_PATH).get(), SpecialSlabBlock.Type.PATH)
//		);
//		addBlock(
//				"grass_block_slab",
//				() -> new SnowySlabBlock(copyProperties(Blocks.GRASS_BLOCK).sustainsPlant()
//
//						.get())
//		);
//		addBlock(
//				"mycelium_slab",
//				() -> new SnowySlabBlock(copyProperties(Blocks.MYCELIUM).sustainsPlant().get())
//		);
//		addBlock(
//				"podzol_slab",
//				() -> new SnowySlabBlock(copyProperties(Blocks.PODZOL).sustainsPlant().get())
//		);
//		addBlock("netherrack_slab", () -> new SlabBlock(copyProperties(Blocks.NETHERRACK).get()));
//		addBlock(
//				"crimson_nylium_slab",
//				() -> new SpecialSlabBlock(copyProperties(Blocks.CRIMSON_NYLIUM).get(), SpecialSlabBlock.Type.NYLIUM)
//		);
//		addBlock(
//				"warped_nylium_slab",
//				() -> new SpecialSlabBlock(copyProperties(Blocks.CRIMSON_NYLIUM).get(), SpecialSlabBlock.Type.NYLIUM)
//		);
//		addBlock("end_stone_slab", () -> new SlabBlock(copyProperties(Blocks.END_STONE).get()));

		addIsotropic("dirt_cobblestone", copyProperties(Blocks.SANDSTONE));
		addIsotropic(
				"grass_cobblestone",
				copyProperties(Blocks.SANDSTONE)
		);
		addIsotropic("sandy_cobblestone", copyProperties(Blocks.SANDSTONE));
		addIsotropic("snowy_cobblestone", copyProperties(Blocks.SANDSTONE));

		addIsotropic("cobblestone_path", copyProperties(Blocks.SANDSTONE));
		addIsotropic("dirt_cobblestone_path", copyProperties(Blocks.SANDSTONE));
		addIsotropic(
				"grass_cobblestone_path",
				copyProperties(Blocks.SANDSTONE)
		);
		addIsotropic("sandy_cobblestone_path", copyProperties(Blocks.SANDSTONE));
		addIsotropic("snowy_cobblestone_path", copyProperties(Blocks.SANDSTONE));

		addIsotropic("dirt_cobblestone_slab", copyProperties(Blocks.SANDSTONE));
		addIsotropic(
				"grass_cobblestone_slab",
				copyProperties(Blocks.SANDSTONE)
		);
		addIsotropic("sandy_cobblestone_slab", copyProperties(Blocks.SANDSTONE));
		addIsotropic("snowy_cobblestone_slab", copyProperties(Blocks.SANDSTONE));

		addIsotropic("cobblestone_path_slab", copyProperties(Blocks.SANDSTONE));
		addIsotropic("dirt_cobblestone_path_slab", copyProperties(Blocks.SANDSTONE));
		addIsotropic(
				"grass_cobblestone_path_slab",
				copyProperties(Blocks.SANDSTONE)
		);
		addIsotropic("sandy_cobblestone_path_slab", copyProperties(Blocks.SANDSTONE));
		addIsotropic("snowy_cobblestone_path_slab", copyProperties(Blocks.SANDSTONE));

		addIsotropic("dirt_cobblestone_stairs", copyProperties(Blocks.SANDSTONE));
		addIsotropic(
				"grass_cobblestone_stairs",
				copyProperties(Blocks.SANDSTONE)
		);
		addIsotropic("sandy_cobblestone_stairs", copyProperties(Blocks.SANDSTONE));
		addIsotropic("snowy_cobblestone_stairs", copyProperties(Blocks.SANDSTONE));

		addIsotropic("cobblestone_path_stairs", copyProperties(Blocks.SANDSTONE));
		addIsotropic("dirt_cobblestone_path_stairs", copyProperties(Blocks.SANDSTONE));
		addIsotropic(
				"grass_cobblestone_path_stairs",
				copyProperties(Blocks.SANDSTONE)
		);
		addIsotropic("sandy_cobblestone_path_stairs", copyProperties(Blocks.SANDSTONE));
		addIsotropic("snowy_cobblestone_path_stairs", copyProperties(Blocks.SANDSTONE));

		addPlant("ginkgo_leaves", copyProperties(Blocks.OAK_LEAVES, MapColor.GOLD));
		addPlant("orange_maple_leaves", copyProperties(Blocks.OAK_LEAVES, MapColor.COLOR_ORANGE));
		addPlant("red_maple_leaves", copyProperties(Blocks.OAK_LEAVES, MapColor.COLOR_RED));
		addPlant("peach_blossom", copyProperties(Blocks.CHERRY_LEAVES));
		addPlant("peach_blossom_leaves", copyProperties(Blocks.CHERRY_LEAVES));
		addPlant("cherry_blossom", copyProperties(Blocks.CHERRY_LEAVES));
		addPlant("cherry_blossom_leaves", copyProperties(Blocks.CHERRY_LEAVES));
		addPlant("white_cherry_blossom", copyProperties(Blocks.CHERRY_LEAVES, MapColor.SNOW));
		addPlant("white_cherry_blossom_leaves", copyProperties(Blocks.CHERRY_LEAVES, MapColor.SNOW));
		addPlant("plantable_leaves", copyProperties(Blocks.OAK_LEAVES).sustainsPlant());
		addPlant("plantable_leaves_dark", copyProperties(Blocks.OAK_LEAVES).sustainsPlant());
		addPlant("willow_leaves", copyProperties(Blocks.OAK_LEAVES));
		addPlant("hanging_willow_leaves", copyProperties(Blocks.VINE)); //TODO proper class

		addBasic("miniature_tree", "xkdeco:miniature", false, BlockSettingPresets.thingy(null));
		addBasic("miniature_cherry", "xkdeco:miniature", false, BlockSettingPresets.thingy(null));
		addBasic("miniature_ginkgo", "xkdeco:miniature", false, BlockSettingPresets.thingy(null));
		addBasic("miniature_maple", "xkdeco:miniature", false, BlockSettingPresets.thingy(null));
		addBasic("miniature_bamboo", "xkdeco:miniature", false, BlockSettingPresets.thingy(null));
		addBasic("miniature_coral", "xkdeco:miniature", false, BlockSettingPresets.thingy(null));
		addBasic("miniature_red_coral", "xkdeco:miniature", false, BlockSettingPresets.thingy(null));
		addBasic("miniature_mount", "xkdeco:miniature", false, BlockSettingPresets.thingy(null));
		addBasic("miniature_succulents", "xkdeco:miniature", false, BlockSettingPresets.thingy(null));

		addBasic("teapot", "xkdeco:teapot", true, BlockSettingPresets.thingy(null));
		StackableComponent component = StackableComponent.create(4);
//		addBlock(
//				"cup",
//				() -> new BasicBlock(BlockSettingPresets.thingy(null)
//						.horizontal()
//						.component(component)
//						.canSurviveHandler(CanSurviveHandler.checkFloor())
//						.shape(ShapeGenerator.layered(component, XKDeco.id("cup")))
//						.get())
//		);
		addBasic("tea_ware", "xkdeco:tea_ware", true, BlockSettingPresets.thingy(null));

		FoodProperties refreshmentsFood = new FoodProperties.Builder().nutrition(2).saturationMod(0.1f).build();
//		addBlock(
//				"refreshments",
//				() -> new BasicBlock(BlockSettingPresets.food(1, 7, refreshmentsFood, null).horizontal().get())
//		);
//		addBlock(
//				"refreshments2",
//				() -> new BasicBlock(BlockSettingPresets.food(1, 7, refreshmentsFood, null).horizontal().get())
//		);
//		addBlock(
//				"refreshments3",
//				() -> new BasicBlock(BlockSettingPresets.food(1, 7, refreshmentsFood, null).horizontal().get())
//		);
//		addBlock(
//				"refreshments4",
//				() -> new BasicBlock(BlockSettingPresets.food(1, 7, refreshmentsFood, null).horizontal().get())
//		);
//		addBlock(
//				"refreshments5",
//				() -> new BasicBlock(BlockSettingPresets.food(1, 7, refreshmentsFood, null).horizontal().get())
//		);
//		addBlock(
//				"refreshments6",
//				() -> new BasicBlock(BlockSettingPresets.food(1, 7, refreshmentsFood, null).horizontal().get())
//		);

		FoodProperties fruitPlatterFood = new FoodProperties.Builder().nutrition(2).saturationMod(0.1f).build();
//		addBlock(
//				"fruit_platter",
//				() -> new BasicBlock(BlockSettingPresets.food(1, 7, fruitPlatterFood, null).horizontal().get())
//		);

		addBasic("calligraphy", "carpet", true, BlockSettingPresets.thingy(null));
		addBasic("ink_painting", "carpet", true, BlockSettingPresets.thingy(null));
		addBasic("weiqi_board", "xkdeco:board", true, BlockSettingPresets.thingy(null));
		addBasic("xiangqi_board", "xkdeco:board", true, BlockSettingPresets.thingy(null));

		addSpecial("plain_item_display", BlockSettingPresets.thingy(null));
		addSpecial("gorgeous_item_display", BlockSettingPresets.thingy(null));
		addSpecial("mechanical_item_display", BlockSettingPresets.thingy(null));
		addSpecial("tech_item_display", BlockSettingPresets.thingy(null));
		addSpecial("item_projector", BlockSettingPresets.thingy(null));

		addSpecial("plain_block_display", BlockSettingPresets.thingy(null));
		addSpecial("gorgeous_block_display", BlockSettingPresets.thingy(null));
		addSpecial("mechanical_block_display", BlockSettingPresets.thingy(null));
		addSpecial("tech_block_display", BlockSettingPresets.thingy(null));

		addDisplayBlockEntity();

		addSpecial("varnished_wardrobe", BlockSettingPresets.thingy(null));
		addSpecial("ebony_wardrobe", BlockSettingPresets.thingy(null));
		addSpecial("mahogany_wardrobe", BlockSettingPresets.thingy(null));
		addSpecial("iron_wardrobe", BlockSettingPresets.thingy(null));
		addSpecial("glass_wardrobe", BlockSettingPresets.thingy(null));
		addSpecial("full_glass_wardrobe", BlockSettingPresets.thingy(null));

		addWardrobeBlockEntity();

		addBasic("white_porcelain", "xkdeco:porcelain", false, BlockSettingPresets.thingy(null));
		addBasic("white_porcelain_tall", "xkdeco:porcelain", false, BlockSettingPresets.thingy(null));
		addBasic(
				"white_porcelain_small",
				"xkdeco:porcelain_small",
				false,
				BlockSettingPresets.thingy(null)
		);
		addBasic("bluewhite_porcelain", "xkdeco:porcelain", false, BlockSettingPresets.thingy(null));
		addBasic("bluewhite_porcelain_tall", "xkdeco:porcelain", false, BlockSettingPresets.thingy(null));
		addBasic(
				"bluewhite_porcelain_small",
				"xkdeco:porcelain_small",
				false,
				BlockSettingPresets.thingy(null)
		);
		addBasic("celadon_porcelain", "xkdeco:porcelain", false, BlockSettingPresets.thingy(null));
		addBasic(
				"celadon_porcelain_tall",
				"xkdeco:porcelain",
				false,
				BlockSettingPresets.thingy(null)
		);
		addBasic(
				"celadon_porcelain_small",
				"xkdeco:porcelain_small",
				false,
				BlockSettingPresets.thingy(null)
		);

		addIsotropic("paper_lantern", BlockSettingPresets.lightThingy(null).shape(XKDeco.id("lantern")));
		addIsotropic("red_lantern", BlockSettingPresets.lightThingy(null).shape(XKDeco.id("lantern")));
		addIsotropic(
				"festival_lantern",
				BlockSettingPresets.lightThingy(null).shape(XKDeco.id("festival_lantern"))
		);
		addDirectional(
				"oil_lamp",
				BlockSettingPresets.lightThingy(null).shape(XKDeco.id("oil_lamp"))
		);
		addIsotropic("candlestick", BlockSettingPresets.lightThingy(null).shape(XKDeco.id("candlestick")));
		addIsotropic(
				"big_candlestick",
				BlockSettingPresets.lightThingy(null).shape(XKDeco.id("big_candlestick"))
		);
		addDirectional(
				"empty_candlestick",
				BlockSettingPresets.thingy(null).shape(XKDeco.id("empty_candlestick"))
		);
		addIsotropic("covered_lamp", BlockSettingPresets.lightThingy(null).shape(XKDeco.id("covered_lamp")));
		addIsotropic("roofed_lamp", BlockSettingPresets.lightThingy(null).shape(XKDeco.id("roofed_lamp")));
		addIsotropic("stone_lamp", BlockSettingPresets.lightThingy(null).shape(XKDeco.id("stone_lamp")));
		addIsotropic("deepslate_lamp", BlockSettingPresets.lightThingy(null).shape(XKDeco.id("stone_lamp")));
		addIsotropic("blackstone_lamp", BlockSettingPresets.lightThingy(null).shape(XKDeco.id("stone_lamp")));
		addBasic("fish_bowl", "xkdeco:fish_bowl", false, BlockSettingPresets.thingy(null));
		addBasic("dark_fish_bowl", "xkdeco:fish_bowl", false, BlockSettingPresets.thingy(null));
		addIsotropic("stone_water_bowl", BlockSettingPresets.thingy(null).shape(XKDeco.id("water_bowl")));
		addIsotropic("stone_water_tank", BlockSettingPresets.thingy(null).shape(XKDeco.id("water_tank")));
		addBasic("fish_tank", "xkdeco:fish_tank", false, BlockSettingPresets.thingy(null));
		addIsotropic(
				"empty_fish_tank",
				BlockSettingPresets.thingy(null).shape(XKDeco.id("fish_tank"))
		);

		addBasic(
				"small_book_stack",
				"xkdeco:small_book_stack",
				false,
				BlockSettingPresets.thingy(null)
		);
		addBasic(
				"big_book_stack",
				"xkdeco:big_book_stack",
				false,
				BlockSettingPresets.thingy(null)
		);
		addBasic(
				"empty_bottle_stack",
				"xkdeco:bottle_stack",
				false,
				BlockSettingPresets.thingy(null).component(CycleVariantsComponent.create(3))
		);
		addBasic(
				"bottle_stack",
				"xkdeco:bottle_stack",
				false,
				BlockSettingPresets.thingy(null).component(CycleVariantsComponent.create(3))
		);
		addBasic("wood_globe", "xkdeco:covered_lamp", false, BlockSettingPresets.thingy(null));
		addBasic("globe", "xkdeco:covered_lamp", false, BlockSettingPresets.thingy(null));
		addBasic(
				"solar_system_model",
				"xkdeco:solar_system_model",
				false,
				BlockSettingPresets.thingy(null)
		);
		addBasic("big_solar_system_model", "block", false, BlockSettingPresets.thingy(null));
		addBasic("telescope", "block", false, BlockSettingPresets.thingy(null));

		addPlant("fallen_ginkgo_leaves", BlockSettingPresets.fallenLeaves(MapColor.GOLD));
		addPlant("fallen_orange_maple_leaves", BlockSettingPresets.fallenLeaves(MapColor.COLOR_ORANGE));
		addPlant("fallen_red_maple_leaves", BlockSettingPresets.fallenLeaves(MapColor.COLOR_RED));
		addPlant("fallen_peach_blossom", BlockSettingPresets.fallenLeaves(MapColor.COLOR_PINK));
		addPlant("fallen_cherry_blossom", BlockSettingPresets.fallenLeaves(MapColor.COLOR_PINK));
		addPlant("fallen_white_cherry_blossom", BlockSettingPresets.fallenLeaves(MapColor.SNOW));

		addDirectional(
				"factory_lamp",
				BlockSettingPresets.lightThingy(null).shape(XKDeco.id("factory_lamp"))
		);
		addDirectional(
				"factory_lamp_broken",
				BlockSettingPresets.lightThingy(null).shape(XKDeco.id("factory_lamp"))
		);
		addDirectional(
				"factory_warning_lamp",
				BlockSettingPresets.lightThingy(null).shape(XKDeco.id("factory_lamp"))
		);

//		addBlock(
//				"factory_light_bar",
//				() -> new BasicBlock(BlockSettingPresets.lightThingy(null)
//						.noCollision()
//						.component(MouldingComponent.getInstance())
//						.shape(XKDeco.id("factory_light_bar"))
//						.get())
//		);

		addIsotropic(
				"factory_ceiling_lamp",
				BlockSettingPresets.lightThingy(null).shape(XKDeco.id("factory_ceiling_lamp"))
		);
		addIsotropic("factory_pendant", BlockSettingPresets.lightThingy(null).shape(XKDeco.id("factory_pendant")));

		addDirectional(
				"fan_blade",
				BlockSettingPresets.thingy(null).shape(XKDeco.id("fan"))
		);

		addDirectional(
				"factory_vent_fan",
				BlockSettingPresets.thingy(null)
		);
		addDirectional(
				"factory_vent_fan_big",
				BlockSettingPresets.thingy(null)
		);

		addDirectional(
				"steel_windmill",
				BlockSettingPresets.thingy(null).shape(XKDeco.id("fan"))
		);
		addDirectional(
				"iron_windmill",
				BlockSettingPresets.thingy(null).shape(XKDeco.id("fan"))
		);
		addDirectional(
				"wooden_windmill",
				BlockSettingPresets.thingy(null).shape(XKDeco.id("fan"))
		);

//		addBlock(
//				"mechanical_console",
//				() -> new BasicBlock(BlockSettingPresets.lightThingy(null)
//						.component(MouldingComponent.getInstance())
//						.get())
//		);
		addBasic("mechanical_screen", "xkdeco:wide_screen", false, BlockSettingPresets.thingy(null));
		addBasic("mechanical_chair", "xkdeco:chair", false, BlockSettingPresets.thingy(null));

//		addBlock(
//				"tech_console",
//				() -> new BasicBlock(BlockSettingPresets.lightThingy(null)
//						.component(MouldingComponent.getInstance())
//						.get())
//		);
		addBasic("tech_screen", "xkdeco:wide_screen", false, BlockSettingPresets.thingy(null));
		addBasic("tech_chair", "xkdeco:chair", false, BlockSettingPresets.thingy(null));

		addIsotropic("tech_table", BlockSettingPresets.thingy(null).shape(XKDeco.id("tech_table")));
		addIsotropic("tech_table_circle", BlockSettingPresets.thingy(null).shape(XKDeco.id("tech_table")));
		addBasic(
				"tech_table_bigcircle",
				"xkdeco:tech_table",
				false,
				BlockSettingPresets.thingy(null)
		);

		addDirectional(
				"hologram_base",
				BlockSettingPresets.thingy(null).shape(XKDeco.id("hologram_base"))
		);
		addItem("hologram_planet");
		addItem("hologram_dna");
		addItem("hologram_pictures");
		addItem("hologram_message");
		addItem("hologram_xekr_logo");

		addDirectional(
				"item_frame_cover",
				KBlockSettings.copyProperties(Blocks.GLASS).shape(XKDeco.id("item_frame_cover"))
		);
		addDirectional(
				"glow_item_frame_cover",
				KBlockSettings.copyProperties(Blocks.GLASS).shape(XKDeco.id("item_frame_cover")).configure($ -> $.lightLevel($$ -> 15))
		);

		addBasic("sign_entrance", "xkdeco:screen", false, BlockSettingPresets.thingy(null));
		addBasic("sign_exit", "xkdeco:screen", false, BlockSettingPresets.thingy(null));
		addBasic("sign_left", "xkdeco:screen", false, BlockSettingPresets.thingy(null));
		addBasic("sign_right", "xkdeco:screen", false, BlockSettingPresets.thingy(null));

		addBasic(
				"small_sign_left",
				"xkdeco:screen",
				false,
				BlockSettingPresets.lightThingy(null)
		);
		addBasic(
				"small_sign_right",
				"xkdeco:screen",
				false,
				BlockSettingPresets.lightThingy(null)
		);
		addBasic(
				"small_sign_ground",
				"carpet",
				false,
				BlockSettingPresets.lightThingy(null)
		);

//		addBlock("air_duct", () -> {
//			VoxelShape base = Shapes.join(
//					Shapes.block(),
//					Shapes.or(box(0, 2, 2, 16, 14, 14), box(2, 0, 2, 14, 16, 14), box(2, 2, 0, 14, 14, 16)),
//					BooleanOp.ONLY_FIRST);
//			VoxelShape side = box(2, 0, 2, 14, 2, 14);
//			return new AirDuctBlock(BlockSettingPresets.hollowSteel()
//					.shape(ShapeGenerator.sixWay(base, Shapes.empty(), side))
//					.interactionShape(ShapeGenerator.unit(Shapes.block()))
//					.waterLoggable()
//					.get());
//		});
//		addBlock("air_duct_oblique", () -> {
//			VoxelShape trueNorth = ShapeStorage.getInstance().get(XKDeco.id("air_duct"));
//			VoxelShape falseNorth = ShapeStorage.getInstance().get(XKDeco.id("air_duct2"));
//			return new HorizontalShiftBlock(BlockSettingPresets.hollowSteel()
//					.shape(ShapeGenerator.horizontalShifted(trueNorth, falseNorth))
//					.interactionShape(ShapeGenerator.unit(Shapes.block()))
//					.get());
//		});
//		addBlock("hollow_steel_beam", () -> new WallBlock(BlockSettingPresets.hollowSteel().get()));
//		addBlock(
//				"hollow_steel_half_beam",
//				() -> new HollowSteelHalfBeamBlock(BlockSettingPresets.hollowSteel()
//						.waterLoggable()
//						.shape(ShapeGenerator.faceAttached(
//								ShapeStorage.getInstance().get(XKDeco.id("hollow_steel_half_beam_floor")),
//								ShapeStorage.getInstance().get(XKDeco.id("hollow_steel_half_beam_ceiling")),
//								ShapeStorage.getInstance().get(XKDeco.id("hollow_steel_half_beam_wall"))))
//						.get())
//		);
//		addBlock(
//				"dark_wall_base",
//				() -> new BasicBlock(KBlockSettings.copyProperties(Blocks.STONE_BRICKS)
//						.waterLoggable()
//						.component(MouldingComponent.getInstance())
//						.shape(XKDeco.id("wall_base"))
//						.get())
//		);
//		addBlock(
//				"dark_wall_base2",
//				() -> new BasicBlock(KBlockSettings.copyProperties(Blocks.STONE_BRICKS)
//						.waterLoggable()
//						.component(MouldingComponent.getInstance())
//						.shape(XKDeco.id("wall_base2"))
//						.get())
//		);
//		addBlock(
//				"light_wall_base",
//				() -> new BasicBlock(KBlockSettings.copyProperties(Blocks.STONE_BRICKS)
//						.waterLoggable()
//						.component(MouldingComponent.getInstance())
//						.shape(XKDeco.id("wall_base"))
//						.get())
//		);
//		addBlock(
//				"light_wall_base2",
//				() -> new BasicBlock(KBlockSettings.copyProperties(Blocks.STONE_BRICKS)
//						.waterLoggable()
//						.component(MouldingComponent.getInstance())
//						.shape(XKDeco.id("wall_base2"))
//						.get())
//		);
		addDirectional("dark_stone_handrail_head", BlockSettingPresets.thingy(null));
		addDirectional("light_stone_handrail_head", BlockSettingPresets.thingy(null));

		addIsotropic("egyptian_brick_column", BlockSettingPresets.stoneColumn(Blocks.STONE_BRICKS, null));
		addIsotropic(
				"egyptian_brick_column_base",
				BlockSettingPresets.stoneColumn(Blocks.STONE_BRICKS, null)
		);
		addIsotropic(
				"egyptian_brick_column_head",
				BlockSettingPresets.stoneColumn(Blocks.STONE_BRICKS, null)
		);
		addIsotropic("egyptian_bump_column", BlockSettingPresets.stoneColumn(Blocks.STONE_BRICKS, null));
		addIsotropic(
				"egyptian_bump_column_base",
				BlockSettingPresets.stoneColumn(Blocks.STONE_BRICKS, null)
		);
		addIsotropic(
				"egyptian_bump_column_head",
				BlockSettingPresets.stoneColumn(Blocks.STONE_BRICKS, null)
		);
		addIsotropic("egyptian_carved_column", BlockSettingPresets.stoneColumn(Blocks.STONE_BRICKS, null));
		addIsotropic(
				"egyptian_carved_column_base",
				BlockSettingPresets.stoneColumn(Blocks.STONE_BRICKS, null)
		);
		addIsotropic("egyptian_column", BlockSettingPresets.stoneColumn(Blocks.STONE_BRICKS, null));
		addIsotropic("egyptian_column_base", BlockSettingPresets.stoneColumn(Blocks.STONE_BRICKS, null));
		addIsotropic("egyptian_column_head", BlockSettingPresets.stoneColumn(Blocks.STONE_BRICKS, null));
		addIsotropic("egyptian_smooth_column", BlockSettingPresets.stoneColumn(Blocks.STONE_BRICKS, null));
		addIsotropic(
				"egyptian_smooth_column_base",
				BlockSettingPresets.stoneColumn(Blocks.STONE_BRICKS, null)
		);
		addIsotropic("egyptian_stripe_column", BlockSettingPresets.stoneColumn(Blocks.STONE_BRICKS, null));
		addIsotropic(
				"egyptian_stripe_column_base",
				BlockSettingPresets.stoneColumn(Blocks.STONE_BRICKS, null)
		);
		addIsotropic(
				"egyptian_stripe_column_head",
				BlockSettingPresets.stoneColumn(Blocks.STONE_BRICKS, null)
		);
//		addBlock(
//				"egyptian_moulding",
//				() -> new BasicBlock(KBlockSettings.copyProperties(Blocks.STONE_BRICKS)
//						.waterLoggable()
//						.component(MouldingComponent.getInstance())
//						.shape(XKDeco.id("wall_base2")) //TODO
//						.get())
//		);
//		addBlock(
//				"egyptian_moulding2",
//				() -> new BasicBlock(KBlockSettings.copyProperties(Blocks.STONE_BRICKS)
//						.waterLoggable()
//						.component(MouldingComponent.getInstance())
//						.shape(XKDeco.id("wall_base2")) //TODO
//						.get())
//		);

		addIsotropic("quartz_wall", KBlockSettings.copyProperties(Blocks.QUARTZ_BLOCK));
		addIsotropic("greek_column", BlockSettingPresets.stoneColumn(Blocks.STONE_BRICKS, null));
		addIsotropic(
				"greek_corinthian_column_base",
				BlockSettingPresets.stoneColumn(Blocks.STONE_BRICKS, null)
		);
		addIsotropic(
				"greek_corinthian_column_head",
				BlockSettingPresets.stoneColumn(Blocks.STONE_BRICKS, null)
		);
		addIsotropic("greek_doric_column_head", BlockSettingPresets.stoneColumn(Blocks.STONE_BRICKS, null));
		addIsotropic("greek_ionic_column_base", BlockSettingPresets.stoneColumn(Blocks.STONE_BRICKS, null));
		addIsotropic(
				"greek_ionic_column_head",
				BlockSettingPresets.stoneColumn(Blocks.STONE_BRICKS, null).horizontal()
		);
		addIsotropic(
				"greek_ionic_column_head_corner",
				BlockSettingPresets.stoneColumn(Blocks.STONE_BRICKS, null).horizontal()
		);
//		addBlock(
//				"greek_moulding",
//				() -> new BasicBlock(KBlockSettings.copyProperties(Blocks.STONE_BRICKS)
//						.waterLoggable()
//						.component(MouldingComponent.getInstance())
//						.shape(XKDeco.id("wall_base2")) //TODO
//						.get())
//		);
//		addBlock(
//				"greek_moulding2",
//				() -> new BasicBlock(KBlockSettings.copyProperties(Blocks.STONE_BRICKS)
//						.waterLoggable()
//						.component(MouldingComponent.getInstance())
//						.shape(XKDeco.id("wall_base2")) //TODO
//						.get())
//		);

		addIsotropic("maya_column", BlockSettingPresets.stoneColumn(Blocks.STONE_BRICKS, null));
		addIsotropic("maya_stonebrick_column", BlockSettingPresets.stoneColumn(Blocks.STONE_BRICKS, null));

		addIsotropic("roman_column", BlockSettingPresets.stoneColumn(Blocks.STONE_BRICKS, null));
		addIsotropic("roman_column_base", BlockSettingPresets.stoneColumn(Blocks.STONE_BRICKS, null));
		addIsotropic(
				"roman_composite_column_base",
				BlockSettingPresets.stoneColumn(Blocks.STONE_BRICKS, null)
		);
		addIsotropic(
				"roman_composite_column_head",
				BlockSettingPresets.stoneColumn(Blocks.STONE_BRICKS, null)
		);
		addIsotropic(
				"roman_corinthian_column_base",
				BlockSettingPresets.stoneColumn(Blocks.STONE_BRICKS, null)
		);
		addIsotropic(
				"roman_corinthian_column_head",
				BlockSettingPresets.stoneColumn(Blocks.STONE_BRICKS, null)
		);
		addIsotropic(
				"roman_doric_column",
				BlockSettingPresets.stoneColumn(Blocks.STONE_BRICKS, null)
		);
		addIsotropic(
				"roman_doric_column_base",
				BlockSettingPresets.stoneColumn(Blocks.STONE_BRICKS, null)
		);
		addIsotropic(
				"roman_doric_column_head",
				BlockSettingPresets.stoneColumn(Blocks.STONE_BRICKS, null)
		);
		addIsotropic(
				"roman_ionic_column_base",
				BlockSettingPresets.stoneColumn(Blocks.STONE_BRICKS, null)
		);
		addIsotropic(
				"roman_ionic_column_head",
				BlockSettingPresets.stoneColumn(Blocks.STONE_BRICKS, null).horizontal()
		);
		addIsotropic(
				"roman_ionic_column_head_corner",
				BlockSettingPresets.stoneColumn(Blocks.STONE_BRICKS, null).horizontal()
		);
		addIsotropic(
				"roman_toscan_column",
				BlockSettingPresets.stoneColumn(Blocks.STONE_BRICKS, null)
		);
		addIsotropic(
				"roman_toscan_column_base",
				BlockSettingPresets.stoneColumn(Blocks.STONE_BRICKS, null)
		);
		addIsotropic(
				"roman_toscan_column_head",
				BlockSettingPresets.stoneColumn(Blocks.STONE_BRICKS, null)
		);
//		addBlock(
//				"roman_moulding",
//				() -> new BasicBlock(KBlockSettings.copyProperties(Blocks.STONE_BRICKS)
//						.waterLoggable()
//						.component(MouldingComponent.getInstance())
//						.shape(XKDeco.id("wall_base2")) //TODO
//						.get())
//		);
//		addBlock(
//				"roman_moulding2",
//				() -> new BasicBlock(KBlockSettings.copyProperties(Blocks.STONE_BRICKS)
//						.waterLoggable()
//						.component(MouldingComponent.getInstance())
//						.shape(XKDeco.id("wall_base2")) //TODO
//						.get())
//		);
//		addBlock(
//				"hollow_steel_bars",
//				() -> new IronBarsBlock(BlockSettingPresets.hollowSteel()
//						.get())
//		);

		addScreen("off");
		addScreen("wave");
		addScreen("cube");
		addScreen("diagram");
		addScreen("dna");
		addScreen("list");
		addScreen("message");
		addScreen("three_bodies");
		addScreen("transport");
	}

	private static void addScreen(String id) {
//		var settings = KBlockSettings.builder()
//				.waterLoggable()
//				.component(FrontAndTopComponent.getInstance())
//				.shape(XKDeco.id("screen"))
//				.configure($ -> {
//					if (!"off".equals(id)) {
//						$.lightLevel($$ -> 9);
//					}
//				});
//		addBlock("screen_" + id, () -> new BasicBlock(settings.get()));
	}

	private static void addBlock(String id, Supplier<Block> blockSupplier) {
		var itemProperties = new Item.Properties();
		var block = BLOCKS.register(id, blockSupplier);
		ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties));
	}

	public static void addTreatedWood(String id, MapColor mapColor) {
		addIsotropic(id + "_wood", copyProperties(Blocks.OAK_WOOD, mapColor));
		addIsotropic(id + "_log", copyProperties(Blocks.OAK_LOG, mapColor));
		addIsotropic(id + "_log_slab", copyProperties(Blocks.OAK_LOG, mapColor));

		addIsotropic(id + "_planks", copyProperties(Blocks.OAK_PLANKS, mapColor));
		addIsotropic(id + "_slab", copyProperties(Blocks.OAK_SLAB, mapColor));
		addIsotropic(id + "_stairs", copyProperties(Blocks.OAK_STAIRS, mapColor));

		addIsotropic(id + "_table", BlockSettingPresets.thingy(mapColor, SoundType.WOOD));
		addIsotropic(id + "_big_table", BlockSettingPresets.thingy(mapColor, SoundType.WOOD));
		addIsotropic(id + "_tall_table", BlockSettingPresets.thingy(mapColor, SoundType.WOOD));
//		addBlock(id + "_desk", () -> new BasicBlock(BlockSettingPresets.thingy(mapColor, SoundType.WOOD)
//				.horizontalAxis()
//				.shape(XKDeco.id("big_table"))
//				.get()));
		addBasic(
				id + "_stool",
				"xkdeco:long_stool",
				false,
				BlockSettingPresets.thingy(mapColor, SoundType.WOOD)
		);
		addBasic(
				id + "_chair",
				"xkdeco:chair",
				false,
				BlockSettingPresets.thingy(mapColor, SoundType.WOOD)
		);
		addBasic(
				id + "_empty_shelf",
				"xkdeco:shelf",
				false,
				BlockSettingPresets.thingy(mapColor, SoundType.WOOD)
		);
		addBasic(
				id + "_shelf",
				"xkdeco:shelf",
				false,
				BlockSettingPresets.thingy(mapColor, SoundType.WOOD)
		);
		addBasic(
				id + "_divided_shelf",
				"xkdeco:shelf",
				false,
				BlockSettingPresets.thingy(mapColor, SoundType.WOOD)
		);

//		addBlock(
//				id + "_fence",
//				() -> new FenceBlock(copyProperties(Blocks.OAK_FENCE, mapColor).get())
//		);
		addDirectional(
				id + "_fence_head",
				copyProperties(Blocks.OAK_FENCE, mapColor).shape(XKDeco.id("wooden_fence_head"))
		);
//		addBlock(
//				id + "_fence_gate",
//				() -> new FenceGateBlock(copyProperties(Blocks.OAK_FENCE_GATE, mapColor).get(), WoodType.OAK)
//		);
		addIsotropic(id + "_door", copyProperties(Blocks.OAK_DOOR, mapColor));
		addIsotropic(
				id + "_trapdoor",
				copyProperties(Blocks.OAK_TRAPDOOR, mapColor)
		);
//		addBlock(
//				id + "_column_wall",
//				() -> new WallBlock(copyProperties(Blocks.OAK_FENCE, mapColor).get())
//		);
//		addBlock(
//				"hollow_" + id + "_column_wall",
//				() -> new WallBlock(copyProperties(Blocks.OAK_FENCE, mapColor).get())
//		);

//		addBlock(
//				id + "_meiren_kao",
//				() -> new BasicBlock(copyProperties(Blocks.OAK_PLANKS).waterLoggable()
//						.shape(XKDeco.id("meiren_kao"))
//						.noOcclusion()
//						.component(MouldingComponent.getInstance())
//						.get())
//		);
//		addBlock(
//				id + "_meiren_kao_with_column",
//				() -> new BasicBlock(copyProperties(Blocks.OAK_PLANKS).waterLoggable()
//						.shape(XKDeco.id("meiren_kao_with_column"))
//						.noOcclusion()
//						.component(MouldingComponent.getInstance())
//						.get())
//		);
//		addBlock(
//				id + "_dougong",
//				() -> new BasicBlock(copyProperties(Blocks.OAK_PLANKS)
//						.waterLoggable()
//						.noOcclusion()
//						.component(MouldingComponent.getInstance())
//						.get())
//		);
//		addBlock(
//				id + "_dougong_connection",
//				() -> new BasicBlock(copyProperties(Blocks.OAK_PLANKS)
//						.waterLoggable()
//						.noOcclusion()
//						.component(MouldingComponent.getInstance())
//						.get())
//		);
//		addBlock(
//				id + "_dougong_hollow_connection",
//				() -> new BasicBlock(copyProperties(Blocks.OAK_PLANKS)
//						.waterLoggable()
//						.noOcclusion()
//						.component(MouldingComponent.getInstance())
//						.get())
//		);
//		addBlock(
//				id + "_window",
//				() -> new TrapDoorBlock(
//						copyProperties(Blocks.OAK_TRAPDOOR, mapColor).get(),
//						BlockSetType.OAK)
//		);
//		addBlock(
//				id + "_awning_window",
//				() -> new TrapDoorBlock(
//						copyProperties(Blocks.OAK_TRAPDOOR, mapColor).get(),
//						BlockSetType.OAK)
//		);
//		addBlock(
//				id + "_hanging_fascia",
//				() -> new HangingFasciaBlock(copyProperties(Blocks.OAK_PLANKS, mapColor).shape(XKDeco.id("hanging_fascia")))
//		);
	}
}
