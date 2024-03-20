package org.teacon.xkdeco.init;

import static net.minecraft.world.level.block.Block.box;
import static org.teacon.xkdeco.block.setting.XKBlockSettings.copyProperties;
import static org.teacon.xkdeco.init.XKDecoCreativeTabs.TAB_BASIC_CONTENTS;
import static org.teacon.xkdeco.init.XKDecoCreativeTabs.TAB_FUNCTIONAL_CONTENTS;
import static org.teacon.xkdeco.init.XKDecoCreativeTabs.TAB_FURNITURE_CONTENTS;
import static org.teacon.xkdeco.init.XKDecoCreativeTabs.TAB_NATURE_CONTENTS;
import static org.teacon.xkdeco.init.XKDecoCreativeTabs.TAB_STRUCTURE_CONTENTS;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.teacon.xkdeco.XKDeco;
import org.teacon.xkdeco.block.AirDuctBlock;
import org.teacon.xkdeco.block.BasicBlock;
import org.teacon.xkdeco.block.BlockDisplayBlock;
import org.teacon.xkdeco.block.FallenLeavesBlock;
import org.teacon.xkdeco.block.HangingFasciaBlock;
import org.teacon.xkdeco.block.HollowSteelHalfBeamBlock;
import org.teacon.xkdeco.block.HorizontalShiftBlock;
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
import org.teacon.xkdeco.block.SnowySlabBlock;
import org.teacon.xkdeco.block.SpecialSlabBlock;
import org.teacon.xkdeco.block.WardrobeBlock;
import org.teacon.xkdeco.block.impl.MetalLadderCanSurviveHandler;
import org.teacon.xkdeco.block.setting.CanSurviveHandler;
import org.teacon.xkdeco.block.setting.FrontAndTopComponent;
import org.teacon.xkdeco.block.setting.GlassType;
import org.teacon.xkdeco.block.setting.MouldingComponent;
import org.teacon.xkdeco.block.setting.ShapeGenerator;
import org.teacon.xkdeco.block.setting.ShapeStorage;
import org.teacon.xkdeco.block.setting.StackableComponent;
import org.teacon.xkdeco.block.setting.WaterLoggableComponent;
import org.teacon.xkdeco.block.setting.XKBlockSettings;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SandBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;
import snownee.kiwi.KiwiModule;

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
	public static final String ROOF_SUFFIX = "_roof";
	public static final String ROOF_END_SUFFIX = "_roof_end";
	public static final String ROOF_FLAT_SUFFIX = "_roof_flat";
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
			XKBlockSettings.Builder settings,
			Collection<RegistryObject<Item>> tabContents) {
		var itemProperties = new Item.Properties();
		VoxelShape northShape = ShapeStorage.getInstance().get(northShapeId);
		RegistryObject<BasicBlock> block;
		if (northShape == Shapes.block()) {
			block = BLOCKS.register(id, () -> new BasicBlock(settings.horizontal().get()));
		} else {
			block = BLOCKS.register(
					id,
					() -> new BasicBlock(settings
							.horizontal()
							.shape(new ResourceLocation(northShapeId))
							.canSurviveHandler(isSupportNeeded ? CanSurviveHandler.checkFloor() : null)
							.get()));
		}
		tabContents.add(ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties)));
	}

	private static void addDirectional(
			String id,
			XKBlockSettings.Builder settings,
			Collection<RegistryObject<Item>> tabContents) {
		var itemProperties = new Item.Properties();
		var block = BLOCKS.register(
				id,
				() -> new BasicBlock(settings.directional().get()));
		tabContents.add(ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties)));
	}

	private static void addIsotropic(
			String id,
			XKBlockSettings.Builder settings,
			Collection<RegistryObject<Item>> tabContents) {
		var itemProperties = new Item.Properties();
		if (id.startsWith("quartz_glass")) {
			settings.glassType(GlassType.QUARTZ);
		} else if (id.startsWith("translucent_")) {
			settings.glassType(GlassType.TRANSLUCENT);
		} else if (id.startsWith("toughened_glass")) {
			settings.glassType(GlassType.TOUGHENED);
		} else if (id.contains("glass")) {
			settings.glassType(GlassType.CLEAR);
		}
		Supplier<Block> blockSupplier;
		if (id.contains(SLAB_SUFFIX)) {
			blockSupplier = () -> new SlabBlock(settings.get());
		} else if (id.contains(STAIRS_SUFFIX)) {
			blockSupplier = () -> new StairBlock(Blocks.AIR.defaultBlockState(), settings.get());
		} else if (id.contains(LOG_SUFFIX) || id.contains(WOOD_SUFFIX) || id.contains(PILLAR_SUFFIX)) {
			blockSupplier = () -> new RotatedPillarBlock(settings.get());
		} else if (id.contains(LINED_PREFIX) || id.contains(LUXURY_PREFIX) || id.contains(PAINTED_PREFIX) || id.contains(CHISELED_PREFIX) ||
				id.contains(DOUBLE_SCREW_PREFIX)) {
			blockSupplier = () -> new RotatedPillarBlock(settings.get());
		} else if (id.contains(BIG_TABLE_SUFFIX) || id.contains(TALL_TABLE_SUFFIX)) {
			blockSupplier = () -> new BasicBlock(BlockSettingPresets.thingy(null)
					.shape(XKDeco.id("big_table"))
					.get());
		} else if (id.contains(TABLE_SUFFIX)) {
			blockSupplier = () -> new BasicBlock(BlockSettingPresets.thingy(null)
					.shape(XKDeco.id("table"))
					.get());
		} else if (id.contains("_trapdoor")) {
			blockSupplier = () -> new TrapDoorBlock(
					settings.get(),
					id.contains("factory") || id.contains("steel") ? BlockSetType.IRON : BlockSetType.OAK);
		} else if (id.endsWith("_door")) {
			blockSupplier = () -> new DoorBlock(
					settings.get(),
					id.contains("factory") || id.contains("steel") ? BlockSetType.IRON : BlockSetType.OAK);
		} else if (id.endsWith("_wall")) {
			blockSupplier = () -> new WallBlock(settings.get());
		} else if (settings.hasComponent(WaterLoggableComponent.TYPE)) {
			blockSupplier = () -> new BasicBlock(settings.get());
		} else {
			blockSupplier = () -> new Block(settings.get());
		}
		var blockHolder = BLOCKS.register(id, blockSupplier);
		tabContents.add(ITEMS.register(id, () -> new BlockItem(blockHolder.get(), itemProperties)));
	}

	private static void addRoof(
			String id,
			Supplier<BlockBehaviour.Properties> propFactory,
			Collection<RegistryObject<Item>> tabContents,
			boolean asian) {
		var itemProperties = new Item.Properties();
		var roof = BLOCKS.register(id, () -> new RoofBlock(propFactory.get()));
		tabContents.add(ITEMS.register(id, () -> new BlockItem(roof.get(), itemProperties)));
		var roofRidge = BLOCKS.register(id + "_ridge", () -> new RoofRidgeBlock(propFactory.get(), asian));
		tabContents.add(ITEMS.register(id + "_ridge", () -> new BlockItem(roofRidge.get(), itemProperties)));
		var roofSmallEnd = BLOCKS.register(id + "_small_end", () -> new RoofEndBlock(propFactory.get(), true));
		tabContents.add(ITEMS.register(id + "_small_end", () -> new BlockItem(roofSmallEnd.get(), itemProperties)));
		var smallRidgeEnd = BLOCKS.register(id + "_small_ridge_end", () -> {
			if (asian) {
				return new RoofRidgeEndAsianBlock(propFactory.get(), true);
			} else {
				return new RoofRidgeEndAsianBlock(propFactory.get(), true);
			}
		});
		var smallFlatEnd = BLOCKS.register(id + "_small_flat_end", () -> new RoofHorizontalShiftBlock(propFactory.get()));

		//TODO remove in the future:
		tabContents.add(ITEMS.register(id + "_small_ridge_end", () -> new BlockItem(smallRidgeEnd.get(), itemProperties)));
		tabContents.add(ITEMS.register(id + "_small_flat_end", () -> new BlockItem(smallFlatEnd.get(), itemProperties)));

		var roofSmallEave = BLOCKS.register(id + "_small_eave", () -> new RoofEaveBlock(propFactory.get(), true));
		tabContents.add(ITEMS.register(id + "_small_eave", () -> new BlockItem(roofSmallEave.get(), itemProperties)));
		var roofFlat = BLOCKS.register(id + "_flat", () -> new RoofFlatBlock(propFactory.get()));
		tabContents.add(ITEMS.register(id + "_flat", () -> new BlockItem(roofFlat.get(), itemProperties)));

		if (!asian) {
			return;
		}

		var roofEave = BLOCKS.register(id + "_eave", () -> new RoofEaveBlock(propFactory.get(), false));
		tabContents.add(ITEMS.register(id + "_eave", () -> new BlockItem(roofEave.get(), itemProperties)));
		var roofEnd = BLOCKS.register(id + "_end", () -> new RoofEndBlock(propFactory.get(), false));
		tabContents.add(ITEMS.register(id + "_end", () -> new BlockItem(roofEnd.get(), itemProperties)));
		var roofRidgeEnd = BLOCKS.register(id + "_ridge_end", () -> new RoofRidgeEndAsianBlock(propFactory.get(), false));

		//TODO remove in the future:
		tabContents.add(ITEMS.register(id + "_ridge_end", () -> new BlockItem(roofRidgeEnd.get(), itemProperties)));

		var roofDeco = BLOCKS.register(id + "_deco", () -> new HorizontalShiftBlock(propFactory.get()));
		tabContents.add(ITEMS.register(id + "_deco", () -> new BlockItem(roofDeco.get(), itemProperties)));
		var roofDecoOblique = BLOCKS.register(id + "_deco_oblique", () -> new HorizontalShiftBlock(propFactory.get()));
		tabContents.add(ITEMS.register(id + "_deco_oblique", () -> new BlockItem(roofDecoOblique.get(), itemProperties)));
		var roofTip = BLOCKS.register(id + "_tip", () -> new RoofTipBlock(propFactory.get()));
		tabContents.add(ITEMS.register(id + "_tip", () -> new BlockItem(roofTip.get(), itemProperties)));
	}

	private static void addPlant(
			String id,
			XKBlockSettings.Builder settings,
			Collection<RegistryObject<Item>> tabContents) {
		var itemProperties = new Item.Properties();
		if (id.contains(LEAVES_SUFFIX) || id.contains(BLOSSOM_SUFFIX)) {
			if (id.startsWith(FALLEN_LEAVES_PREFIX)) {
				var block = BLOCKS.register(id, () -> new FallenLeavesBlock(settings.get()));
				tabContents.add(ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties)));
				return;
			}
			var block = BLOCKS.register(id, () -> new LeavesBlock(settings.get()));
			tabContents.add(ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties)));
		} else {
			throw new IllegalArgumentException("Illegal id (" + id + ") for plant blocks");
		}
	}

	private static void addSpecial(
			String id,
			XKBlockSettings.Builder settings,
			Collection<RegistryObject<Item>> tabContents) {
		var itemProperties = new Item.Properties();
		if (id.equals(REFRESHMENT_SPECIAL)) {
			var block = BLOCKS.register(id, () -> new BasicBlock(settings.get()));
			tabContents.add(ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties)));
		} else if (id.contains(ITEM_DISPLAY_SUFFIX) || id.equals(ITEM_PROJECTOR_SPECIAL)) {
			var block = BLOCKS.register(id, () -> new ItemDisplayBlock(settings.get()));
			tabContents.add(ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties)));
		} else if (id.contains(BLOCK_DISPLAY_SUFFIX)) {
			var block = BLOCKS.register(id, () -> new BlockDisplayBlock(settings.get()));
			tabContents.add(ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties)));
		} else if (id.contains(WARDROBE_SUFFIX)) {
			var block = BLOCKS.register(id, () -> new WardrobeBlock(settings.removeComponent(WaterLoggableComponent.TYPE).get()));
			ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties));
//			tabContents.add(ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties)));
		} else {
			throw new IllegalArgumentException("Illegal id (" + id + ") for special blocks");
		}
	}

	private static void addItem(String id) {
		var itemProperties = new Item.Properties();
		TAB_FURNITURE_CONTENTS.add(ITEMS.register(id, () -> new Item(itemProperties)));
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
				TAB_STRUCTURE_CONTENTS.add(RegistryObject.create(registryName, ForgeRegistries.ITEMS));
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

	static {
		addCushionEntity();

		addIsotropic("black_tiles", BlockSettingPresets.blackTiles(), TAB_BASIC_CONTENTS);
		addIsotropic("black_tile_slab", BlockSettingPresets.blackTiles(), TAB_BASIC_CONTENTS);
		addIsotropic("black_tile_stairs", BlockSettingPresets.blackTiles(), TAB_BASIC_CONTENTS);

		addIsotropic("cyan_tiles", BlockSettingPresets.cyanTiles(), TAB_BASIC_CONTENTS);
		addIsotropic("cyan_tile_slab", BlockSettingPresets.cyanTiles(), TAB_BASIC_CONTENTS);
		addIsotropic("cyan_tile_stairs", BlockSettingPresets.cyanTiles(), TAB_BASIC_CONTENTS);

		addIsotropic("yellow_tiles", BlockSettingPresets.yellowTiles(), TAB_BASIC_CONTENTS);
		addIsotropic("yellow_tile_slab", BlockSettingPresets.yellowTiles(), TAB_BASIC_CONTENTS);
		addIsotropic("yellow_tile_stairs", BlockSettingPresets.yellowTiles(), TAB_BASIC_CONTENTS);

		addIsotropic("blue_tiles", BlockSettingPresets.blueTiles(), TAB_BASIC_CONTENTS);
		addIsotropic("blue_tile_slab", BlockSettingPresets.blueTiles(), TAB_BASIC_CONTENTS);
		addIsotropic("blue_tile_stairs", BlockSettingPresets.blueTiles(), TAB_BASIC_CONTENTS);

		addIsotropic("green_tiles", BlockSettingPresets.greenTiles(), TAB_BASIC_CONTENTS);
		addIsotropic("green_tile_slab", BlockSettingPresets.greenTiles(), TAB_BASIC_CONTENTS);
		addIsotropic("green_tile_stairs", BlockSettingPresets.greenTiles(), TAB_BASIC_CONTENTS);

		addIsotropic("red_tiles", BlockSettingPresets.redTiles(), TAB_BASIC_CONTENTS);
		addIsotropic("red_tile_slab", BlockSettingPresets.redTiles(), TAB_BASIC_CONTENTS);
		addIsotropic("red_tile_stairs", BlockSettingPresets.redTiles(), TAB_BASIC_CONTENTS);

		addIsotropic("steel_tiles", BlockSettingPresets.steel(), TAB_BASIC_CONTENTS);
		addIsotropic("steel_tile_slab", BlockSettingPresets.steel(), TAB_BASIC_CONTENTS);
		addIsotropic("steel_tile_stairs", BlockSettingPresets.steel(), TAB_BASIC_CONTENTS);

		addIsotropic("copper_tiles", copyProperties(Blocks.COPPER_BLOCK), TAB_BASIC_CONTENTS);
		addIsotropic("copper_tile_slab", copyProperties(Blocks.COPPER_BLOCK), TAB_BASIC_CONTENTS);
		addIsotropic("copper_tile_stairs", copyProperties(Blocks.COPPER_BLOCK), TAB_BASIC_CONTENTS);

		addIsotropic("glass_tiles", copyProperties(Blocks.GLASS), TAB_BASIC_CONTENTS);
		addIsotropic("glass_tile_slab", copyProperties(Blocks.GLASS), TAB_BASIC_CONTENTS);
		addIsotropic("glass_tile_stairs", copyProperties(Blocks.GLASS), TAB_BASIC_CONTENTS);
		addIsotropic("glass_trapdoor", copyProperties(Blocks.GLASS), TAB_BASIC_CONTENTS);
		addIsotropic("glass_door", copyProperties(Blocks.GLASS), TAB_BASIC_CONTENTS);

		addIsotropic("mud_wall_block", BlockSettingPresets.mudWall(), TAB_BASIC_CONTENTS);
		addIsotropic("mud_wall_slab", BlockSettingPresets.mudWall(), TAB_BASIC_CONTENTS);
		addIsotropic("mud_wall_stairs", BlockSettingPresets.mudWall(), TAB_BASIC_CONTENTS);
		addIsotropic("mud_wall_wall", BlockSettingPresets.mudWall(), TAB_BASIC_CONTENTS);

		addIsotropic("framed_mud_wall_block", BlockSettingPresets.mudWall(), TAB_BASIC_CONTENTS);

		addIsotropic("lined_mud_wall_block", BlockSettingPresets.mudWall(), TAB_BASIC_CONTENTS);
		addIsotropic("lined_mud_wall_slab", BlockSettingPresets.mudWall(), TAB_BASIC_CONTENTS);
		addIsotropic("lined_mud_wall_stairs", BlockSettingPresets.mudWall(), TAB_BASIC_CONTENTS);

		addIsotropic("crossed_mud_wall_block", BlockSettingPresets.mudWall(), TAB_BASIC_CONTENTS);
		addIsotropic("crossed_mud_wall_slab", BlockSettingPresets.mudWall(), TAB_BASIC_CONTENTS);
		addIsotropic("crossed_mud_wall_stairs", BlockSettingPresets.mudWall(), TAB_BASIC_CONTENTS);

		addIsotropic("dirty_mud_wall_block", BlockSettingPresets.mudWall(), TAB_BASIC_CONTENTS);
		addIsotropic("dirty_mud_wall_slab", BlockSettingPresets.mudWall(), TAB_BASIC_CONTENTS);
		addIsotropic("dirty_mud_wall_stairs", BlockSettingPresets.mudWall(), TAB_BASIC_CONTENTS);
		addIsotropic("dirty_mud_wall_wall", BlockSettingPresets.mudWall(), TAB_BASIC_CONTENTS);

		addIsotropic("cyan_bricks", BlockSettingPresets.cyanTiles(), TAB_BASIC_CONTENTS);
		addIsotropic("cyan_brick_slab", BlockSettingPresets.cyanTiles(), TAB_BASIC_CONTENTS);
		addIsotropic("cyan_brick_stairs", BlockSettingPresets.cyanTiles(), TAB_BASIC_CONTENTS);
		addIsotropic("cyan_brick_wall", BlockSettingPresets.cyanTiles(), TAB_BASIC_CONTENTS);

		addIsotropic("black_bricks", BlockSettingPresets.blackTiles(), TAB_BASIC_CONTENTS);
		addIsotropic("black_brick_slab", BlockSettingPresets.blackTiles(), TAB_BASIC_CONTENTS);
		addIsotropic("black_brick_stairs", BlockSettingPresets.blackTiles(), TAB_BASIC_CONTENTS);
		addIsotropic("black_brick_wall", BlockSettingPresets.blackTiles(), TAB_BASIC_CONTENTS);

		addTreatedWood("varnished", MapColor.WOOD);
		addTreatedWood("ebony", MapColor.WOOD);
		addTreatedWood("mahogany", MapColor.WOOD);

		addIsotropic("sandstone_pillar", copyProperties(Blocks.SANDSTONE), TAB_BASIC_CONTENTS);

		addIsotropic("polished_sandstone", copyProperties(Blocks.SANDSTONE), TAB_BASIC_CONTENTS);
		addIsotropic("polished_sandstone_slab", copyProperties(Blocks.SANDSTONE), TAB_BASIC_CONTENTS);
//		addIsotropic("polished_sandstone_stairs", copyProperties(Blocks.SANDSTONE), TAB_BASIC_CONTENTS);

		addIsotropic("sandstone_bricks", copyProperties(Blocks.SANDSTONE), TAB_BASIC_CONTENTS);
		addIsotropic("sandstone_brick_slab", copyProperties(Blocks.SANDSTONE), TAB_BASIC_CONTENTS);
		addIsotropic("sandstone_brick_stairs", copyProperties(Blocks.SANDSTONE), TAB_BASIC_CONTENTS);

		addIsotropic("sandstone_small_bricks", copyProperties(Blocks.SANDSTONE), TAB_BASIC_CONTENTS);
		addIsotropic("sandstone_small_brick_slab", copyProperties(Blocks.SANDSTONE), TAB_BASIC_CONTENTS);
		addIsotropic("sandstone_small_brick_stairs", copyProperties(Blocks.SANDSTONE), TAB_BASIC_CONTENTS);

		addIsotropic("red_sandstone_pillar", copyProperties(Blocks.SANDSTONE), TAB_BASIC_CONTENTS);

		addIsotropic("polished_red_sandstone", copyProperties(Blocks.SANDSTONE), TAB_BASIC_CONTENTS);
		addIsotropic("polished_red_sandstone_slab", copyProperties(Blocks.SANDSTONE), TAB_BASIC_CONTENTS);
//		addIsotropic("polished_red_sandstone_stairs", copyProperties(Blocks.SANDSTONE), TAB_BASIC_CONTENTS);

		addIsotropic("red_sandstone_bricks", copyProperties(Blocks.STONE_BRICKS), TAB_BASIC_CONTENTS);
		addIsotropic("red_sandstone_brick_slab", copyProperties(Blocks.STONE_BRICKS), TAB_BASIC_CONTENTS);
		addIsotropic("red_sandstone_brick_stairs", copyProperties(Blocks.STONE_BRICKS), TAB_BASIC_CONTENTS);

		addIsotropic("red_sandstone_small_bricks", copyProperties(Blocks.STONE_BRICKS), TAB_BASIC_CONTENTS);
		addIsotropic("red_sandstone_small_brick_slab", copyProperties(Blocks.STONE_BRICKS), TAB_BASIC_CONTENTS);
		addIsotropic("red_sandstone_small_brick_stairs", copyProperties(Blocks.STONE_BRICKS), TAB_BASIC_CONTENTS);

		addIsotropic("stone_brick_pillar", copyProperties(Blocks.STONE_BRICKS), TAB_BASIC_CONTENTS);
		addIsotropic("stone_brick_pavement", copyProperties(Blocks.STONE_BRICKS), TAB_BASIC_CONTENTS);
		addIsotropic("stone_brick_pavement_slab", copyProperties(Blocks.STONE_BRICKS), TAB_BASIC_CONTENTS);

		addIsotropic("deepslate_pillar", copyProperties(Blocks.DEEPSLATE), TAB_BASIC_CONTENTS);
		addIsotropic("deepslate_pavement", copyProperties(Blocks.DEEPSLATE), TAB_BASIC_CONTENTS);
		addIsotropic("deepslate_pavement_slab", copyProperties(Blocks.DEEPSLATE), TAB_BASIC_CONTENTS);

		addIsotropic("mossy_deepslate_bricks", copyProperties(Blocks.STONE_BRICKS), TAB_BASIC_CONTENTS);
		addIsotropic("mossy_deepslate_brick_slab", copyProperties(Blocks.STONE_BRICKS), TAB_BASIC_CONTENTS);
		addIsotropic("mossy_deepslate_brick_stairs", copyProperties(Blocks.STONE_BRICKS), TAB_BASIC_CONTENTS);

		addIsotropic("blackstone_pillar", copyProperties(Blocks.BLACKSTONE), TAB_BASIC_CONTENTS);
		addIsotropic("blackstone_pavement", copyProperties(Blocks.BLACKSTONE), TAB_BASIC_CONTENTS);
		addIsotropic("blackstone_pavement_slab", copyProperties(Blocks.BLACKSTONE), TAB_BASIC_CONTENTS);

		addIsotropic("gilded_blackstone_bricks", copyProperties(Blocks.STONE_BRICKS), TAB_BASIC_CONTENTS);
		addIsotropic("gilded_blackstone_brick_slab", copyProperties(Blocks.STONE_BRICKS), TAB_BASIC_CONTENTS);
		addIsotropic("gilded_blackstone_brick_stairs", copyProperties(Blocks.STONE_BRICKS), TAB_BASIC_CONTENTS);
		addIsotropic("gilded_blackstone_brick_pillar", copyProperties(Blocks.STONE_BRICKS), TAB_BASIC_CONTENTS);

		addIsotropic("chiseled_gilded_blackstone", copyProperties(Blocks.GILDED_BLACKSTONE), TAB_BASIC_CONTENTS);
		addIsotropic("luxury_gilded_blackstone", copyProperties(Blocks.GILDED_BLACKSTONE), TAB_BASIC_CONTENTS);

		addIsotropic("maya_stone", copyProperties(Blocks.STONE), TAB_BASIC_CONTENTS);
		addIsotropic("maya_stone_slab", copyProperties(Blocks.STONE), TAB_BASIC_CONTENTS);
		addIsotropic("maya_stone_stairs", copyProperties(Blocks.STONE), TAB_BASIC_CONTENTS);

		addIsotropic("maya_stonebricks", copyProperties(Blocks.STONE_BRICKS), TAB_BASIC_CONTENTS);
		addIsotropic("maya_stonebrick_slab", copyProperties(Blocks.STONE_BRICKS), TAB_BASIC_CONTENTS);
		addIsotropic("maya_stonebrick_stairs", copyProperties(Blocks.STONE_BRICKS), TAB_BASIC_CONTENTS);
		addIsotropic("maya_stonebrick_wall", copyProperties(Blocks.STONE_BRICKS), TAB_BASIC_CONTENTS);

		addIsotropic("maya_bricks", copyProperties(Blocks.STONE_BRICKS), TAB_BASIC_CONTENTS);
		addIsotropic("maya_brick_slab", copyProperties(Blocks.STONE_BRICKS), TAB_BASIC_CONTENTS);
		addIsotropic("maya_brick_stairs", copyProperties(Blocks.STONE_BRICKS), TAB_BASIC_CONTENTS);
		addIsotropic("maya_brick_wall", copyProperties(Blocks.STONE_BRICKS), TAB_BASIC_CONTENTS);

		addIsotropic("maya_polished_stonebricks", copyProperties(Blocks.STONE_BRICKS), TAB_BASIC_CONTENTS);
		addIsotropic("maya_polished_stonebrick_slab", copyProperties(Blocks.STONE_BRICKS), TAB_BASIC_CONTENTS);
		addIsotropic("maya_polished_stonebrick_stairs", copyProperties(Blocks.STONE_BRICKS), TAB_BASIC_CONTENTS);

		addIsotropic("maya_mossy_stonebricks", copyProperties(Blocks.STONE_BRICKS), TAB_BASIC_CONTENTS);
		addIsotropic("maya_mossy_stonebrick_slab", copyProperties(Blocks.STONE_BRICKS), TAB_BASIC_CONTENTS);
		addIsotropic("maya_mossy_stonebrick_stairs", copyProperties(Blocks.STONE_BRICKS), TAB_BASIC_CONTENTS);
		addIsotropic("maya_mossy_stonebrick_wall", copyProperties(Blocks.STONE_BRICKS), TAB_BASIC_CONTENTS);

		addIsotropic("maya_mossy_bricks", copyProperties(Blocks.STONE_BRICKS), TAB_BASIC_CONTENTS);
		addIsotropic("maya_mossy_brick_slab", copyProperties(Blocks.STONE_BRICKS), TAB_BASIC_CONTENTS);
		addIsotropic("maya_mossy_brick_stairs", copyProperties(Blocks.STONE_BRICKS), TAB_BASIC_CONTENTS);
		addIsotropic("maya_mossy_brick_wall", copyProperties(Blocks.STONE_BRICKS), TAB_BASIC_CONTENTS);

		addIsotropic("maya_chiseled_stonebricks", copyProperties(Blocks.STONE_BRICKS), TAB_BASIC_CONTENTS);
		addIsotropic("maya_cut_stonebricks", copyProperties(Blocks.STONE_BRICKS), TAB_BASIC_CONTENTS);

		addBasic("maya_single_screw_thread_stone", "block", false, copyProperties(Blocks.STONE), TAB_BASIC_CONTENTS);
		addIsotropic("maya_double_screw_thread_stone", copyProperties(Blocks.STONE), TAB_BASIC_CONTENTS);
		addIsotropic("maya_quad_screw_thread_stone", copyProperties(Blocks.STONE), TAB_BASIC_CONTENTS);

		addIsotropic("maya_pictogram_stone", copyProperties(Blocks.STONE), TAB_BASIC_CONTENTS);
		addIsotropic(
				"maya_skull_stone",
				copyProperties(Blocks.STONE).renderType(KiwiModule.RenderLayer.Layer.TRANSLUCENT),
				TAB_BASIC_CONTENTS);

		addIsotropic("maya_pillar", copyProperties(Blocks.STONE), TAB_BASIC_CONTENTS);
		addIsotropic("maya_mossy_pillar", copyProperties(Blocks.STONE), TAB_BASIC_CONTENTS);

		addBasic(
				"maya_crystal_skull",
				"xkdeco:maya_crystal_skull",
				false,
				copyProperties(Blocks.DEEPSLATE).renderType(KiwiModule.RenderLayer.Layer.TRANSLUCENT),
				TAB_FURNITURE_CONTENTS);

		addIsotropic("aztec_stonebricks", copyProperties(Blocks.STONE_BRICKS), TAB_BASIC_CONTENTS);
		addIsotropic("aztec_stonebrick_slab", copyProperties(Blocks.STONE_BRICKS), TAB_BASIC_CONTENTS);
		addIsotropic("aztec_stonebrick_stairs", copyProperties(Blocks.STONE_BRICKS), TAB_BASIC_CONTENTS);

		addIsotropic("aztec_mossy_stonebricks", copyProperties(Blocks.STONE_BRICKS), TAB_BASIC_CONTENTS);
		addIsotropic("aztec_mossy_stonebrick_slab", copyProperties(Blocks.STONE_BRICKS), TAB_BASIC_CONTENTS);
		addIsotropic("aztec_mossy_stonebrick_stairs", copyProperties(Blocks.STONE_BRICKS), TAB_BASIC_CONTENTS);

		addIsotropic("aztec_sculpture_stone", copyProperties(Blocks.STONE_BRICKS), TAB_BASIC_CONTENTS);
		addIsotropic("aztec_chiseled_stonebricks", copyProperties(Blocks.STONE_BRICKS), TAB_BASIC_CONTENTS);
		addIsotropic("aztec_cut_stonebricks", copyProperties(Blocks.STONE_BRICKS), TAB_BASIC_CONTENTS);

		addIsotropic("inca_stone", copyProperties(Blocks.STONE), TAB_BASIC_CONTENTS);
		addIsotropic("inca_stone_slab", copyProperties(Blocks.STONE), TAB_BASIC_CONTENTS);
		addIsotropic("inca_stone_stairs", copyProperties(Blocks.STONE), TAB_BASIC_CONTENTS);

		addIsotropic("inca_stonebricks", copyProperties(Blocks.STONE_BRICKS), TAB_BASIC_CONTENTS);
		addIsotropic("inca_stonebrick_slab", copyProperties(Blocks.STONE_BRICKS), TAB_BASIC_CONTENTS);
		addIsotropic("inca_stonebrick_stairs", copyProperties(Blocks.STONE_BRICKS), TAB_BASIC_CONTENTS);

		addIsotropic("inca_bricks", copyProperties(Blocks.STONE_BRICKS), TAB_BASIC_CONTENTS);
		addIsotropic("inca_brick_slab", copyProperties(Blocks.STONE_BRICKS), TAB_BASIC_CONTENTS);
		addIsotropic("inca_brick_stairs", copyProperties(Blocks.STONE_BRICKS), TAB_BASIC_CONTENTS);

		addIsotropic("cut_obsidian", copyProperties(Blocks.OBSIDIAN), TAB_BASIC_CONTENTS);
		addIsotropic("cut_obsidian_slab", copyProperties(Blocks.OBSIDIAN), TAB_BASIC_CONTENTS);
		addIsotropic("cut_obsidian_pillar", copyProperties(Blocks.OBSIDIAN), TAB_BASIC_CONTENTS);

		addIsotropic("cut_obsidian_bricks", copyProperties(Blocks.OBSIDIAN), TAB_BASIC_CONTENTS);
		addIsotropic("cut_obsidian_brick_slab", copyProperties(Blocks.OBSIDIAN), TAB_BASIC_CONTENTS);
		addIsotropic("cut_obsidian_brick_stairs", copyProperties(Blocks.OBSIDIAN), TAB_BASIC_CONTENTS);

		addIsotropic("crying_obsidian_bricks", copyProperties(Blocks.OBSIDIAN), TAB_BASIC_CONTENTS);
		addIsotropic("crying_obsidian_brick_slab", copyProperties(Blocks.OBSIDIAN), TAB_BASIC_CONTENTS);
		addIsotropic("crying_obsidian_brick_stairs", copyProperties(Blocks.OBSIDIAN), TAB_BASIC_CONTENTS);

		addIsotropic("cut_gold_block", copyProperties(Blocks.GOLD_BLOCK), TAB_BASIC_CONTENTS);
		addIsotropic("cut_gold_block_slab", copyProperties(Blocks.GOLD_BLOCK), TAB_BASIC_CONTENTS);
		addIsotropic("cut_gold_block_stairs", copyProperties(Blocks.GOLD_BLOCK), TAB_BASIC_CONTENTS);

		addIsotropic("gold_bricks", copyProperties(Blocks.GOLD_BLOCK), TAB_BASIC_CONTENTS);
		addIsotropic("gold_brick_slab", copyProperties(Blocks.GOLD_BLOCK), TAB_BASIC_CONTENTS);
		addIsotropic("gold_brick_stairs", copyProperties(Blocks.GOLD_BLOCK), TAB_BASIC_CONTENTS);
		addIsotropic("gold_pillar", copyProperties(Blocks.GOLD_BLOCK), TAB_BASIC_CONTENTS);

		addIsotropic("chiseled_gold_block", copyProperties(Blocks.GOLD_BLOCK), TAB_BASIC_CONTENTS);
		addIsotropic("painted_gold_block", copyProperties(Blocks.GOLD_BLOCK), TAB_BASIC_CONTENTS);

		addIsotropic("bronze_block", copyProperties(Blocks.COPPER_BLOCK), TAB_BASIC_CONTENTS);
		addIsotropic("smooth_bronze_block", copyProperties(Blocks.COPPER_BLOCK), TAB_BASIC_CONTENTS);
		addIsotropic("inscription_bronze_block", copyProperties(Blocks.COPPER_BLOCK), TAB_BASIC_CONTENTS);

		addIsotropic("cut_bronze_block", copyProperties(Blocks.COPPER_BLOCK), TAB_BASIC_CONTENTS);
		addIsotropic("cut_bronze_block_slab", copyProperties(Blocks.COPPER_BLOCK), TAB_BASIC_CONTENTS);
		addIsotropic("cut_bronze_block_stairs", copyProperties(Blocks.COPPER_BLOCK), TAB_BASIC_CONTENTS);

		addIsotropic("chiseled_bronze_block", copyProperties(Blocks.COPPER_BLOCK), TAB_BASIC_CONTENTS);
		addBasic("screw_thread_bronze_block", "block", false, copyProperties(Blocks.COPPER_BLOCK), TAB_BASIC_CONTENTS);
		addIsotropic("bronze_pillar", copyProperties(Blocks.COPPER_BLOCK), TAB_BASIC_CONTENTS);

		addIsotropic("steel_block", BlockSettingPresets.steel(), TAB_BASIC_CONTENTS);
		addIsotropic("smooth_steel_block", BlockSettingPresets.steel(), TAB_BASIC_CONTENTS);
		addIsotropic("steel_pillar", BlockSettingPresets.steel(), TAB_BASIC_CONTENTS);
		addIsotropic("steel_trapdoor", BlockSettingPresets.steel().noOcclusion(), TAB_BASIC_CONTENTS);

		addIsotropic("steel_floor", BlockSettingPresets.steel(), TAB_BASIC_CONTENTS);
		addIsotropic("steel_floor_slab", BlockSettingPresets.steel(), TAB_BASIC_CONTENTS);
		addIsotropic("steel_floor_stairs", BlockSettingPresets.steel(), TAB_BASIC_CONTENTS);

		addIsotropic("chiseled_steel_block", BlockSettingPresets.steel(), TAB_BASIC_CONTENTS);
		addIsotropic("hollow_steel_block", BlockSettingPresets.hollowSteel().waterLoggable(), TAB_BASIC_CONTENTS);
		addIsotropic("hollow_steel_trapdoor", BlockSettingPresets.hollowSteel(), TAB_BASIC_CONTENTS);
		addBlock(
				"steel_safety_ladder",
				() -> new BasicBlock(BlockSettingPresets.hollowSteel()
						.horizontal()
						.waterLoggable()
						.shape(XKDeco.id("safety_ladder")).get()),
				TAB_BASIC_CONTENTS);
		addBlock(
				"steel_ladder",
				() -> new BasicBlock(BlockSettingPresets.hollowSteel()
						.horizontal()
						.waterLoggable()
						.shape(XKDeco.id("ladder"))
						.canSurviveHandler(new MetalLadderCanSurviveHandler()).get()),
				TAB_BASIC_CONTENTS);
		addIsotropic("framed_steel_block", BlockSettingPresets.steel(), TAB_BASIC_CONTENTS);

		addIsotropic("factory_block", BlockSettingPresets.steel(), TAB_BASIC_CONTENTS);
		addIsotropic("factory_slab", BlockSettingPresets.steel(), TAB_BASIC_CONTENTS);
		addIsotropic("factory_stairs", BlockSettingPresets.steel(), TAB_BASIC_CONTENTS);
		addIsotropic("factory_trapdoor", BlockSettingPresets.steel(), TAB_BASIC_CONTENTS);

		addIsotropic("factory_block_rusting", BlockSettingPresets.steel(), TAB_BASIC_CONTENTS);
		addIsotropic("factory_slab_rusting", BlockSettingPresets.steel(), TAB_BASIC_CONTENTS);
		addIsotropic("factory_stairs_rusting", BlockSettingPresets.steel(), TAB_BASIC_CONTENTS);
		addIsotropic("factory_trapdoor_rusting", BlockSettingPresets.steel(), TAB_BASIC_CONTENTS);

		addIsotropic("factory_block_rusted", BlockSettingPresets.steel(), TAB_BASIC_CONTENTS);
		addIsotropic("factory_slab_rusted", BlockSettingPresets.steel(), TAB_BASIC_CONTENTS);
		addIsotropic("factory_stairs_rusted", BlockSettingPresets.steel(), TAB_BASIC_CONTENTS);
		addIsotropic("factory_trapdoor_rusted", BlockSettingPresets.steel(), TAB_BASIC_CONTENTS);

		addIsotropic("factory_danger", BlockSettingPresets.steel(), TAB_BASIC_CONTENTS);
		addIsotropic("factory_danger_rusting", BlockSettingPresets.steel(), TAB_BASIC_CONTENTS);
		addIsotropic("factory_danger_rusted", BlockSettingPresets.steel(), TAB_BASIC_CONTENTS);

		addIsotropic("factory_attention", BlockSettingPresets.steel(), TAB_BASIC_CONTENTS);
		addIsotropic("factory_attention_rusting", BlockSettingPresets.steel(), TAB_BASIC_CONTENTS);
		addIsotropic("factory_attention_rusted", BlockSettingPresets.steel(), TAB_BASIC_CONTENTS);

		addIsotropic("factory_electricity", BlockSettingPresets.steel(), TAB_BASIC_CONTENTS);
		addIsotropic("factory_electricity_rusting", BlockSettingPresets.steel(), TAB_BASIC_CONTENTS);
		addIsotropic("factory_electricity_rusted", BlockSettingPresets.steel(), TAB_BASIC_CONTENTS);

		addIsotropic("factory_toxic", BlockSettingPresets.steel(), TAB_BASIC_CONTENTS);
		addIsotropic("factory_toxic_rusting", BlockSettingPresets.steel(), TAB_BASIC_CONTENTS);
		addIsotropic("factory_toxic_rusted", BlockSettingPresets.steel(), TAB_BASIC_CONTENTS);

		addIsotropic("factory_radiation", BlockSettingPresets.steel(), TAB_BASIC_CONTENTS);
		addIsotropic("factory_radiation_rusting", BlockSettingPresets.steel(), TAB_BASIC_CONTENTS);
		addIsotropic("factory_radiation_rusted", BlockSettingPresets.steel(), TAB_BASIC_CONTENTS);

		addIsotropic("factory_biohazard", BlockSettingPresets.steel(), TAB_BASIC_CONTENTS);
		addIsotropic("factory_biohazard_rusting", BlockSettingPresets.steel(), TAB_BASIC_CONTENTS);
		addIsotropic("factory_biohazard_rusted", BlockSettingPresets.steel(), TAB_BASIC_CONTENTS);

		addIsotropic("factory_lamp_block", BlockSettingPresets.lampBlock(), TAB_BASIC_CONTENTS);
		addIsotropic("factory_lamp_slab", BlockSettingPresets.lampBlock(), TAB_BASIC_CONTENTS);
		addIsotropic("factory_lamp_stairs", BlockSettingPresets.lampBlock(), TAB_BASIC_CONTENTS);

		addIsotropic("tech_lamp_block", BlockSettingPresets.lampBlock(), TAB_BASIC_CONTENTS);
		addIsotropic("tech_lamp_slab", BlockSettingPresets.lampBlock(), TAB_BASIC_CONTENTS);
		addIsotropic("tech_lamp_stairs", BlockSettingPresets.lampBlock(), TAB_BASIC_CONTENTS);

		addIsotropic("translucent_lamp_block", BlockSettingPresets.lampBlock().noOcclusion(), TAB_BASIC_CONTENTS);
		addIsotropic("translucent_lamp_slab", BlockSettingPresets.lampBlock().noOcclusion(), TAB_BASIC_CONTENTS);
		addIsotropic("translucent_lamp_stairs", BlockSettingPresets.lampBlock().noOcclusion(), TAB_BASIC_CONTENTS);

		addBlock("steel_filings", () -> new SandBlock(14406560, copyProperties(Blocks.SAND).get()), TAB_BASIC_CONTENTS);
		addBlock("quartz_sand", () -> new SandBlock(14406560, copyProperties(Blocks.SAND).get()), TAB_BASIC_CONTENTS);
		addBlock("toughened_sand", () -> new SandBlock(14406560, copyProperties(Blocks.SAND).get()), TAB_BASIC_CONTENTS);

		addIsotropic("quartz_glass", BlockSettingPresets.hardenedGlass(), TAB_BASIC_CONTENTS);
		addIsotropic("quartz_glass_slab", BlockSettingPresets.hardenedGlass(), TAB_BASIC_CONTENTS);
		addIsotropic("quartz_glass_stairs", BlockSettingPresets.hardenedGlass(), TAB_BASIC_CONTENTS);

		addIsotropic("toughened_glass", BlockSettingPresets.hardenedGlass(), TAB_BASIC_CONTENTS);
		addIsotropic("toughened_glass_slab", BlockSettingPresets.hardenedGlass(), TAB_BASIC_CONTENTS);
		addIsotropic("toughened_glass_stairs", BlockSettingPresets.hardenedGlass(), TAB_BASIC_CONTENTS);

		addRoof("black_roof", () -> BlockSettingPresets.blackTiles().noOcclusion().get(), TAB_STRUCTURE_CONTENTS, true);
		addRoof("cyan_roof", () -> BlockSettingPresets.cyanTiles().noOcclusion().get(), TAB_STRUCTURE_CONTENTS, true);
		addRoof("yellow_roof", () -> BlockSettingPresets.yellowTiles().noOcclusion().get(), TAB_STRUCTURE_CONTENTS, true);
		addRoof("blue_roof", () -> BlockSettingPresets.blueTiles().noOcclusion().get(), TAB_STRUCTURE_CONTENTS, false);
		addRoof("green_roof", () -> BlockSettingPresets.greenTiles().noOcclusion().get(), TAB_STRUCTURE_CONTENTS, false);
		addRoof("red_roof", () -> BlockSettingPresets.redTiles().noOcclusion().get(), TAB_STRUCTURE_CONTENTS, false);

		addBlock("dirt_slab", () -> new SlabBlock(copyProperties(Blocks.DIRT).sustainsPlant().get()), TAB_NATURE_CONTENTS);
		addBlock(
				"dirt_path_slab",
				() -> new SpecialSlabBlock(copyProperties(Blocks.DIRT_PATH).get(), SpecialSlabBlock.Type.PATH),
				TAB_NATURE_CONTENTS);
		addBlock(
				"grass_block_slab",
				() -> new SnowySlabBlock(copyProperties(Blocks.GRASS_BLOCK).sustainsPlant()
						.renderType(KiwiModule.RenderLayer.Layer.CUTOUT_MIPPED)
						.get()),
				TAB_NATURE_CONTENTS);
		addBlock(
				"mycelium_slab",
				() -> new SnowySlabBlock(copyProperties(Blocks.MYCELIUM).sustainsPlant().get()),
				TAB_NATURE_CONTENTS);
		addBlock(
				"podzol_slab",
				() -> new SnowySlabBlock(copyProperties(Blocks.PODZOL).sustainsPlant().get()),
				TAB_NATURE_CONTENTS);
		addBlock("netherrack_slab", () -> new SlabBlock(copyProperties(Blocks.NETHERRACK).get()), TAB_NATURE_CONTENTS);
		addBlock(
				"crimson_nylium_slab",
				() -> new SpecialSlabBlock(copyProperties(Blocks.CRIMSON_NYLIUM).get(), SpecialSlabBlock.Type.NYLIUM),
				TAB_NATURE_CONTENTS);
		addBlock(
				"warped_nylium_slab",
				() -> new SpecialSlabBlock(copyProperties(Blocks.CRIMSON_NYLIUM).get(), SpecialSlabBlock.Type.NYLIUM),
				TAB_NATURE_CONTENTS);
		addBlock("end_stone_slab", () -> new SlabBlock(copyProperties(Blocks.END_STONE).get()), TAB_NATURE_CONTENTS);

		addIsotropic("dirt_cobblestone", copyProperties(Blocks.SANDSTONE), TAB_NATURE_CONTENTS);
		addIsotropic(
				"grass_cobblestone",
				copyProperties(Blocks.SANDSTONE).renderType(KiwiModule.RenderLayer.Layer.CUTOUT_MIPPED),
				TAB_NATURE_CONTENTS);
		addIsotropic("sandy_cobblestone", copyProperties(Blocks.SANDSTONE), TAB_NATURE_CONTENTS);
		addIsotropic("snowy_cobblestone", copyProperties(Blocks.SANDSTONE), TAB_NATURE_CONTENTS);

		addIsotropic("cobblestone_path", copyProperties(Blocks.SANDSTONE), TAB_NATURE_CONTENTS);
		addIsotropic("dirt_cobblestone_path", copyProperties(Blocks.SANDSTONE), TAB_NATURE_CONTENTS);
		addIsotropic(
				"grass_cobblestone_path",
				copyProperties(Blocks.SANDSTONE).renderType(KiwiModule.RenderLayer.Layer.CUTOUT_MIPPED),
				TAB_NATURE_CONTENTS);
		addIsotropic("sandy_cobblestone_path", copyProperties(Blocks.SANDSTONE), TAB_NATURE_CONTENTS);
		addIsotropic("snowy_cobblestone_path", copyProperties(Blocks.SANDSTONE), TAB_NATURE_CONTENTS);

		addIsotropic("dirt_cobblestone_slab", copyProperties(Blocks.SANDSTONE), TAB_NATURE_CONTENTS);
		addIsotropic(
				"grass_cobblestone_slab",
				copyProperties(Blocks.SANDSTONE).renderType(KiwiModule.RenderLayer.Layer.CUTOUT_MIPPED),
				TAB_NATURE_CONTENTS);
		addIsotropic("sandy_cobblestone_slab", copyProperties(Blocks.SANDSTONE), TAB_NATURE_CONTENTS);
		addIsotropic("snowy_cobblestone_slab", copyProperties(Blocks.SANDSTONE), TAB_NATURE_CONTENTS);

		addIsotropic("cobblestone_path_slab", copyProperties(Blocks.SANDSTONE), TAB_NATURE_CONTENTS);
		addIsotropic("dirt_cobblestone_path_slab", copyProperties(Blocks.SANDSTONE), TAB_NATURE_CONTENTS);
		addIsotropic(
				"grass_cobblestone_path_slab",
				copyProperties(Blocks.SANDSTONE).renderType(KiwiModule.RenderLayer.Layer.CUTOUT_MIPPED),
				TAB_NATURE_CONTENTS);
		addIsotropic("sandy_cobblestone_path_slab", copyProperties(Blocks.SANDSTONE), TAB_NATURE_CONTENTS);
		addIsotropic("snowy_cobblestone_path_slab", copyProperties(Blocks.SANDSTONE), TAB_NATURE_CONTENTS);

		addIsotropic("dirt_cobblestone_stairs", copyProperties(Blocks.SANDSTONE), TAB_NATURE_CONTENTS);
		addIsotropic(
				"grass_cobblestone_stairs",
				copyProperties(Blocks.SANDSTONE).renderType(KiwiModule.RenderLayer.Layer.CUTOUT_MIPPED),
				TAB_NATURE_CONTENTS);
		addIsotropic("sandy_cobblestone_stairs", copyProperties(Blocks.SANDSTONE), TAB_NATURE_CONTENTS);
		addIsotropic("snowy_cobblestone_stairs", copyProperties(Blocks.SANDSTONE), TAB_NATURE_CONTENTS);

		addIsotropic("cobblestone_path_stairs", copyProperties(Blocks.SANDSTONE), TAB_NATURE_CONTENTS);
		addIsotropic("dirt_cobblestone_path_stairs", copyProperties(Blocks.SANDSTONE), TAB_NATURE_CONTENTS);
		addIsotropic(
				"grass_cobblestone_path_stairs",
				copyProperties(Blocks.SANDSTONE).renderType(KiwiModule.RenderLayer.Layer.CUTOUT_MIPPED),
				TAB_NATURE_CONTENTS);
		addIsotropic("sandy_cobblestone_path_stairs", copyProperties(Blocks.SANDSTONE), TAB_NATURE_CONTENTS);
		addIsotropic("snowy_cobblestone_path_stairs", copyProperties(Blocks.SANDSTONE), TAB_NATURE_CONTENTS);

		addPlant("ginkgo_leaves", copyProperties(Blocks.OAK_LEAVES, MapColor.GOLD), TAB_NATURE_CONTENTS);
		addPlant("orange_maple_leaves", copyProperties(Blocks.OAK_LEAVES, MapColor.COLOR_ORANGE), TAB_NATURE_CONTENTS);
		addPlant("red_maple_leaves", copyProperties(Blocks.OAK_LEAVES, MapColor.COLOR_RED), TAB_NATURE_CONTENTS);
		addPlant("peach_blossom", copyProperties(Blocks.CHERRY_LEAVES), TAB_NATURE_CONTENTS);
		addPlant("peach_blossom_leaves", copyProperties(Blocks.CHERRY_LEAVES), TAB_NATURE_CONTENTS);
		addPlant("cherry_blossom", copyProperties(Blocks.CHERRY_LEAVES), TAB_NATURE_CONTENTS);
		addPlant("cherry_blossom_leaves", copyProperties(Blocks.CHERRY_LEAVES), TAB_NATURE_CONTENTS);
		addPlant("white_cherry_blossom", copyProperties(Blocks.CHERRY_LEAVES, MapColor.SNOW), TAB_NATURE_CONTENTS);
		addPlant("white_cherry_blossom_leaves", copyProperties(Blocks.CHERRY_LEAVES, MapColor.SNOW), TAB_NATURE_CONTENTS);
		addPlant("plantable_leaves", copyProperties(Blocks.OAK_LEAVES).sustainsPlant(), TAB_NATURE_CONTENTS);
		addPlant("plantable_leaves_dark", copyProperties(Blocks.OAK_LEAVES).sustainsPlant(), TAB_NATURE_CONTENTS);
		addPlant("willow_leaves", copyProperties(Blocks.OAK_LEAVES), TAB_NATURE_CONTENTS);
		addPlant("hanging_willow_leaves", copyProperties(Blocks.VINE), TAB_NATURE_CONTENTS); //TODO proper class

		addBasic("miniature_tree", "xkdeco:miniature", false, BlockSettingPresets.thingy(null), TAB_FURNITURE_CONTENTS);
		addBasic("miniature_cherry", "xkdeco:miniature", false, BlockSettingPresets.thingy(null), TAB_FURNITURE_CONTENTS);
		addBasic("miniature_ginkgo", "xkdeco:miniature", false, BlockSettingPresets.thingy(null), TAB_FURNITURE_CONTENTS);
		addBasic("miniature_maple", "xkdeco:miniature", false, BlockSettingPresets.thingy(null), TAB_FURNITURE_CONTENTS);
		addBasic("miniature_bamboo", "xkdeco:miniature", false, BlockSettingPresets.thingy(null), TAB_FURNITURE_CONTENTS);
		addBasic("miniature_coral", "xkdeco:miniature", false, BlockSettingPresets.thingy(null), TAB_FURNITURE_CONTENTS);
		addBasic("miniature_red_coral", "xkdeco:miniature", false, BlockSettingPresets.thingy(null), TAB_FURNITURE_CONTENTS);
		addBasic("miniature_mount", "xkdeco:miniature", false, BlockSettingPresets.thingy(null), TAB_FURNITURE_CONTENTS);
		addBasic("miniature_succulents", "xkdeco:miniature", false, BlockSettingPresets.thingy(null), TAB_FURNITURE_CONTENTS);

		addBasic("teapot", "xkdeco:teapot", true, BlockSettingPresets.thingy(null), TAB_FURNITURE_CONTENTS);
		StackableComponent component = StackableComponent.create(4);
		addBlock(
				"cup",
				() -> new BasicBlock(BlockSettingPresets.thingy(null)
						.horizontal()
						.component(component)
						.canSurviveHandler(CanSurviveHandler.checkFloor())
						.shape(ShapeGenerator.layered(component, XKDeco.id("cup")))
						.get()),
				TAB_FURNITURE_CONTENTS);
		addBasic("tea_ware", "xkdeco:tea_ware", true, BlockSettingPresets.thingy(null), TAB_FURNITURE_CONTENTS);

		FoodProperties refreshmentsFood = new FoodProperties.Builder().nutrition(2).saturationMod(0.1f).build();
		addBlock(
				"refreshments",
				() -> new BasicBlock(BlockSettingPresets.food(1, 7, refreshmentsFood, null).horizontal().get()),
				TAB_FURNITURE_CONTENTS);
		addBlock(
				"refreshments2",
				() -> new BasicBlock(BlockSettingPresets.food(1, 7, refreshmentsFood, null).horizontal().get()),
				TAB_FURNITURE_CONTENTS);
		addBlock(
				"refreshments3",
				() -> new BasicBlock(BlockSettingPresets.food(1, 7, refreshmentsFood, null).horizontal().get()),
				TAB_FURNITURE_CONTENTS);
		addBlock(
				"refreshments4",
				() -> new BasicBlock(BlockSettingPresets.food(1, 7, refreshmentsFood, null).horizontal().get()),
				TAB_FURNITURE_CONTENTS);
		addBlock(
				"refreshments5",
				() -> new BasicBlock(BlockSettingPresets.food(1, 7, refreshmentsFood, null).horizontal().get()),
				TAB_FURNITURE_CONTENTS);
		addBlock(
				"refreshments6",
				() -> new BasicBlock(BlockSettingPresets.food(1, 7, refreshmentsFood, null).horizontal().get()),
				TAB_FURNITURE_CONTENTS);

		FoodProperties fruitPlatterFood = new FoodProperties.Builder().nutrition(2).saturationMod(0.1f).build();
		addBlock(
				"fruit_platter",
				() -> new BasicBlock(BlockSettingPresets.food(1, 7, fruitPlatterFood, null).horizontal().get()),
				TAB_FURNITURE_CONTENTS);

		addBasic("calligraphy", "carpet", true, BlockSettingPresets.thingy(null), TAB_FURNITURE_CONTENTS);
		addBasic("ink_painting", "carpet", true, BlockSettingPresets.thingy(null), TAB_FURNITURE_CONTENTS);
		addBasic("weiqi_board", "xkdeco:board", true, BlockSettingPresets.thingy(null), TAB_FURNITURE_CONTENTS);
		addBasic("xiangqi_board", "xkdeco:board", true, BlockSettingPresets.thingy(null), TAB_FURNITURE_CONTENTS);

		addSpecial("plain_item_display", BlockSettingPresets.thingy(null), TAB_FUNCTIONAL_CONTENTS);
		addSpecial("gorgeous_item_display", BlockSettingPresets.thingy(null), TAB_FUNCTIONAL_CONTENTS);
		addSpecial("mechanical_item_display", BlockSettingPresets.thingy(null), TAB_FUNCTIONAL_CONTENTS);
		addSpecial("tech_item_display", BlockSettingPresets.thingy(null), TAB_FUNCTIONAL_CONTENTS);
		addSpecial("item_projector", BlockSettingPresets.thingy(null), TAB_FUNCTIONAL_CONTENTS);

		addSpecial("plain_block_display", BlockSettingPresets.thingy(null), TAB_FUNCTIONAL_CONTENTS);
		addSpecial("gorgeous_block_display", BlockSettingPresets.thingy(null), TAB_FUNCTIONAL_CONTENTS);
		addSpecial("mechanical_block_display", BlockSettingPresets.thingy(null), TAB_FUNCTIONAL_CONTENTS);
		addSpecial("tech_block_display", BlockSettingPresets.thingy(null), TAB_FUNCTIONAL_CONTENTS);

		addDisplayBlockEntity();

		addSpecial("varnished_wardrobe", BlockSettingPresets.thingy(null), TAB_FUNCTIONAL_CONTENTS);
		addSpecial("ebony_wardrobe", BlockSettingPresets.thingy(null), TAB_FUNCTIONAL_CONTENTS);
		addSpecial("mahogany_wardrobe", BlockSettingPresets.thingy(null), TAB_FUNCTIONAL_CONTENTS);
		addSpecial("iron_wardrobe", BlockSettingPresets.thingy(null), TAB_FUNCTIONAL_CONTENTS);
		addSpecial("glass_wardrobe", BlockSettingPresets.thingy(null), TAB_FUNCTIONAL_CONTENTS);
		addSpecial("full_glass_wardrobe", BlockSettingPresets.thingy(null), TAB_FUNCTIONAL_CONTENTS);

		addWardrobeBlockEntity();

		addBasic("white_porcelain", "xkdeco:porcelain", false, BlockSettingPresets.thingy(null), TAB_FURNITURE_CONTENTS);
		addBasic("white_porcelain_tall", "xkdeco:porcelain", false, BlockSettingPresets.thingy(null), TAB_FURNITURE_CONTENTS);
		addBasic(
				"white_porcelain_small",
				"xkdeco:porcelain_small",
				false,
				BlockSettingPresets.thingy(null),
				TAB_FURNITURE_CONTENTS);
		addBasic("bluewhite_porcelain", "xkdeco:porcelain", false, BlockSettingPresets.thingy(null), TAB_FURNITURE_CONTENTS);
		addBasic("bluewhite_porcelain_tall", "xkdeco:porcelain", false, BlockSettingPresets.thingy(null), TAB_FURNITURE_CONTENTS);
		addBasic(
				"bluewhite_porcelain_small",
				"xkdeco:porcelain_small",
				false,
				BlockSettingPresets.thingy(null),
				TAB_FURNITURE_CONTENTS);
		addBasic("celadon_porcelain", "xkdeco:porcelain", false, BlockSettingPresets.thingy(null), TAB_FURNITURE_CONTENTS);
		addBasic(
				"celadon_porcelain_tall",
				"xkdeco:porcelain",
				false,
				BlockSettingPresets.thingy(null),
				TAB_FURNITURE_CONTENTS);
		addBasic(
				"celadon_porcelain_small",
				"xkdeco:porcelain_small",
				false,
				BlockSettingPresets.thingy(null),
				TAB_FURNITURE_CONTENTS);

		addIsotropic("paper_lantern", BlockSettingPresets.lightThingy(null).shape(XKDeco.id("lantern")), TAB_FURNITURE_CONTENTS);
		addIsotropic("red_lantern", BlockSettingPresets.lightThingy(null).shape(XKDeco.id("lantern")), TAB_FURNITURE_CONTENTS);
		addIsotropic(
				"festival_lantern",
				BlockSettingPresets.lightThingy(null).shape(XKDeco.id("festival_lantern")),
				TAB_FURNITURE_CONTENTS);
		addDirectional(
				"oil_lamp",
				BlockSettingPresets.lightThingy(null).renderType(KiwiModule.RenderLayer.Layer.CUTOUT).shape(XKDeco.id("oil_lamp")),
				TAB_FURNITURE_CONTENTS);
		addIsotropic("candlestick", BlockSettingPresets.lightThingy(null).shape(XKDeco.id("candlestick")), TAB_FURNITURE_CONTENTS);
		addIsotropic(
				"big_candlestick",
				BlockSettingPresets.lightThingy(null).shape(XKDeco.id("big_candlestick")),
				TAB_FURNITURE_CONTENTS);
		addDirectional(
				"empty_candlestick",
				BlockSettingPresets.thingy(null).shape(XKDeco.id("empty_candlestick")),
				TAB_FURNITURE_CONTENTS);
		addIsotropic("covered_lamp", BlockSettingPresets.lightThingy(null).shape(XKDeco.id("covered_lamp")), TAB_FURNITURE_CONTENTS);
		addIsotropic("roofed_lamp", BlockSettingPresets.lightThingy(null).shape(XKDeco.id("roofed_lamp")), TAB_FURNITURE_CONTENTS);
		addIsotropic("stone_lamp", BlockSettingPresets.lightThingy(null).shape(XKDeco.id("stone_lamp")), TAB_FURNITURE_CONTENTS);
		addIsotropic("deepslate_lamp", BlockSettingPresets.lightThingy(null).shape(XKDeco.id("stone_lamp")), TAB_FURNITURE_CONTENTS);
		addIsotropic("blackstone_lamp", BlockSettingPresets.lightThingy(null).shape(XKDeco.id("stone_lamp")), TAB_FURNITURE_CONTENTS);
		addBasic("fish_bowl", "xkdeco:fish_bowl", false, BlockSettingPresets.thingy(null), TAB_FURNITURE_CONTENTS);
		addBasic("dark_fish_bowl", "xkdeco:fish_bowl", false, BlockSettingPresets.thingy(null), TAB_FURNITURE_CONTENTS);
		addIsotropic("stone_water_bowl", BlockSettingPresets.thingy(null).shape(XKDeco.id("water_bowl")), TAB_FURNITURE_CONTENTS);
		addIsotropic("stone_water_tank", BlockSettingPresets.thingy(null).shape(XKDeco.id("water_tank")), TAB_FURNITURE_CONTENTS);
		addBasic("fish_tank", "xkdeco:fish_tank", false, BlockSettingPresets.thingy(null), TAB_FURNITURE_CONTENTS);
		addIsotropic(
				"empty_fish_tank",
				BlockSettingPresets.thingy(null).shape(XKDeco.id("fish_tank")),
				TAB_FURNITURE_CONTENTS);

		addBasic(
				"small_book_stack",
				"xkdeco:small_book_stack",
				false,
				BlockSettingPresets.thingy(null),
				TAB_FURNITURE_CONTENTS);
		addBasic(
				"big_book_stack",
				"xkdeco:big_book_stack",
				false,
				BlockSettingPresets.thingy(null),
				TAB_FURNITURE_CONTENTS);
		addBasic(
				"empty_bottle_stack",
				"xkdeco:bottle_stack",
				false,
				BlockSettingPresets.thingy(null).component(StackableComponent.create(3)),
				TAB_FURNITURE_CONTENTS);
		addBasic(
				"bottle_stack",
				"xkdeco:bottle_stack",
				false,
				BlockSettingPresets.thingy(null).component(StackableComponent.create(3)),
				TAB_FURNITURE_CONTENTS);
		addBasic("wood_globe", "xkdeco:covered_lamp", false, BlockSettingPresets.thingy(null), TAB_FURNITURE_CONTENTS);
		addBasic("globe", "xkdeco:covered_lamp", false, BlockSettingPresets.thingy(null), TAB_FURNITURE_CONTENTS);
		addBasic(
				"solar_system_model",
				"xkdeco:solar_system_model",
				false,
				BlockSettingPresets.thingy(null),
				TAB_FURNITURE_CONTENTS);
		addBasic("big_solar_system_model", "block", false, BlockSettingPresets.thingy(null), TAB_FURNITURE_CONTENTS);
		addBasic("telescope", "block", false, BlockSettingPresets.thingy(null), TAB_FURNITURE_CONTENTS);

		addPlant("fallen_ginkgo_leaves", BlockSettingPresets.fallenLeaves(MapColor.GOLD), TAB_NATURE_CONTENTS);
		addPlant("fallen_orange_maple_leaves", BlockSettingPresets.fallenLeaves(MapColor.COLOR_ORANGE), TAB_NATURE_CONTENTS);
		addPlant("fallen_red_maple_leaves", BlockSettingPresets.fallenLeaves(MapColor.COLOR_RED), TAB_NATURE_CONTENTS);
		addPlant("fallen_peach_blossom", BlockSettingPresets.fallenLeaves(MapColor.COLOR_PINK), TAB_NATURE_CONTENTS);
		addPlant("fallen_cherry_blossom", BlockSettingPresets.fallenLeaves(MapColor.COLOR_PINK), TAB_NATURE_CONTENTS);
		addPlant("fallen_white_cherry_blossom", BlockSettingPresets.fallenLeaves(MapColor.SNOW), TAB_NATURE_CONTENTS);

		addDirectional(
				"factory_lamp",
				BlockSettingPresets.lightThingy(null).shape(XKDeco.id("factory_lamp")),
				TAB_FURNITURE_CONTENTS);
		addDirectional(
				"factory_lamp_broken",
				BlockSettingPresets.lightThingy(null).shape(XKDeco.id("factory_lamp")),
				TAB_FURNITURE_CONTENTS);
		addDirectional(
				"factory_warning_lamp",
				BlockSettingPresets.lightThingy(null).shape(XKDeco.id("factory_lamp")),
				TAB_FURNITURE_CONTENTS);

		addBlock(
				"factory_light_bar",
				() -> new BasicBlock(BlockSettingPresets.lightThingy(null)
						.noCollission()
						.component(MouldingComponent.getInstance())
						.shape(XKDeco.id("factory_light_bar"))
						.get()),
				TAB_FURNITURE_CONTENTS);

		addIsotropic(
				"factory_ceiling_lamp",
				BlockSettingPresets.lightThingy(null).shape(XKDeco.id("factory_ceiling_lamp")),
				TAB_FURNITURE_CONTENTS);
		addIsotropic("factory_pendant", BlockSettingPresets.lightThingy(null).shape(XKDeco.id("factory_pendant")), TAB_FURNITURE_CONTENTS);

		addDirectional(
				"fan_blade",
				BlockSettingPresets.thingy(null).renderType(KiwiModule.RenderLayer.Layer.CUTOUT).shape(XKDeco.id("fan")),
				TAB_FURNITURE_CONTENTS);

		addDirectional(
				"factory_vent_fan",
				BlockSettingPresets.thingy(null).renderType(KiwiModule.RenderLayer.Layer.CUTOUT),
				TAB_FURNITURE_CONTENTS);
		addDirectional(
				"factory_vent_fan_big",
				BlockSettingPresets.thingy(null).renderType(KiwiModule.RenderLayer.Layer.CUTOUT),
				TAB_FURNITURE_CONTENTS);

		addDirectional(
				"steel_windmill",
				BlockSettingPresets.thingy(null).renderType(KiwiModule.RenderLayer.Layer.CUTOUT).shape(XKDeco.id("fan")),
				TAB_FURNITURE_CONTENTS);
		addDirectional(
				"iron_windmill",
				BlockSettingPresets.thingy(null).renderType(KiwiModule.RenderLayer.Layer.CUTOUT).shape(XKDeco.id("fan")),
				TAB_FURNITURE_CONTENTS);
		addDirectional(
				"wooden_windmill",
				BlockSettingPresets.thingy(null).renderType(KiwiModule.RenderLayer.Layer.CUTOUT).shape(XKDeco.id("fan")),
				TAB_FURNITURE_CONTENTS);

		addBlock(
				"mechanical_console",
				() -> new BasicBlock(BlockSettingPresets.lightThingy(null)
						.component(MouldingComponent.getInstance())
						.get()),
				TAB_FURNITURE_CONTENTS);
		addBasic("mechanical_screen", "xkdeco:wide_screen", false, BlockSettingPresets.thingy(null), TAB_FURNITURE_CONTENTS);
		addBasic("mechanical_chair", "xkdeco:chair", false, BlockSettingPresets.thingy(null), TAB_FURNITURE_CONTENTS);

		addBlock(
				"tech_console",
				() -> new BasicBlock(BlockSettingPresets.lightThingy(null)
						.component(MouldingComponent.getInstance())
						.get()),
				TAB_FURNITURE_CONTENTS);
		addBasic("tech_screen", "xkdeco:wide_screen", false, BlockSettingPresets.thingy(null), TAB_FURNITURE_CONTENTS);
		addBasic("tech_chair", "xkdeco:chair", false, BlockSettingPresets.thingy(null), TAB_FURNITURE_CONTENTS);

		addBasic("tech_table", "xkdeco:tech_table", false, BlockSettingPresets.thingy(null), TAB_FURNITURE_CONTENTS);
		addBasic(
				"tech_table_circle",
				"xkdeco:tech_table",
				false,
				BlockSettingPresets.thingy(null),
				TAB_FURNITURE_CONTENTS);
		addBasic(
				"tech_table_bigcircle",
				"xkdeco:tech_table",
				false,
				BlockSettingPresets.thingy(null),
				TAB_FURNITURE_CONTENTS);

		addDirectional(
				"hologram_base",
				BlockSettingPresets.thingy(null).shape(XKDeco.id("hologram_base")),
				TAB_FURNITURE_CONTENTS);
		addItem("hologram_planet");
		addItem("hologram_dna");
		addItem("hologram_pictures");
		addItem("hologram_message");
		addItem("hologram_xekr_logo");

		addDirectional(
				"item_frame_cover",
				XKBlockSettings.copyProperties(Blocks.GLASS).shape(XKDeco.id("item_frame_cover")),
				TAB_FURNITURE_CONTENTS);
		addDirectional(
				"glow_item_frame_cover",
				XKBlockSettings.copyProperties(Blocks.GLASS).shape(XKDeco.id("item_frame_cover")).configure($ -> $.lightLevel($$ -> 15)),
				TAB_FURNITURE_CONTENTS);

		addBasic("sign_entrance", "xkdeco:screen", false, BlockSettingPresets.thingy(null), TAB_FURNITURE_CONTENTS);
		addBasic("sign_exit", "xkdeco:screen", false, BlockSettingPresets.thingy(null), TAB_FURNITURE_CONTENTS);
		addBasic("sign_left", "xkdeco:screen", false, BlockSettingPresets.thingy(null), TAB_FURNITURE_CONTENTS);
		addBasic("sign_right", "xkdeco:screen", false, BlockSettingPresets.thingy(null), TAB_FURNITURE_CONTENTS);

		addBasic(
				"small_sign_left",
				"xkdeco:screen",
				false,
				BlockSettingPresets.lightThingy(null),
				TAB_FURNITURE_CONTENTS);
		addBasic(
				"small_sign_right",
				"xkdeco:screen",
				false,
				BlockSettingPresets.lightThingy(null),
				TAB_FURNITURE_CONTENTS);
		addBasic(
				"small_sign_ground",
				"carpet",
				false,
				BlockSettingPresets.lightThingy(null),
				TAB_FURNITURE_CONTENTS);

		addBlock("air_duct", () -> {
			VoxelShape base = Shapes.join(
					Shapes.block(),
					Shapes.or(box(0, 2, 2, 16, 14, 14), box(2, 0, 2, 14, 16, 14), box(2, 2, 0, 14, 14, 16)),
					BooleanOp.ONLY_FIRST);
			VoxelShape side = box(2, 0, 2, 14, 2, 14);
			return new AirDuctBlock(BlockSettingPresets.hollowSteel()
					.shape(ShapeGenerator.sixWay(base, Shapes.empty(), side))
					.interactionShape(ShapeGenerator.unit(Shapes.block()))
					.waterLoggable()
					.get());
		}, TAB_FURNITURE_CONTENTS);
		addBlock("air_duct_oblique", () -> {
			VoxelShape trueNorth = ShapeStorage.getInstance().get(XKDeco.id("air_duct"));
			VoxelShape falseNorth = ShapeStorage.getInstance().get(XKDeco.id("air_duct2"));
			return new HorizontalShiftBlock(BlockSettingPresets.hollowSteel()
					.shape(ShapeGenerator.horizontalShifted(trueNorth, falseNorth))
					.interactionShape(ShapeGenerator.unit(Shapes.block()))
					.get());
		}, TAB_FURNITURE_CONTENTS);
		addBlock("hollow_steel_beam", () -> new WallBlock(BlockSettingPresets.hollowSteel().get()), TAB_BASIC_CONTENTS);
		addBlock(
				"hollow_steel_half_beam",
				() -> new HollowSteelHalfBeamBlock(BlockSettingPresets.hollowSteel()
						.waterLoggable()
						.shape(ShapeGenerator.faceAttached(
								ShapeStorage.getInstance().get(XKDeco.id("hollow_steel_half_beam_floor")),
								ShapeStorage.getInstance().get(XKDeco.id("hollow_steel_half_beam_ceiling")),
								ShapeStorage.getInstance().get(XKDeco.id("hollow_steel_half_beam_wall"))))
						.get()),
				TAB_BASIC_CONTENTS);
		addBlock(
				"dark_wall_base",
				() -> new BasicBlock(XKBlockSettings.copyProperties(Blocks.STONE_BRICKS)
						.waterLoggable()
						.component(MouldingComponent.getInstance())
						.shape(XKDeco.id("wall_base"))
						.get()),
				TAB_FURNITURE_CONTENTS);
		addBlock(
				"dark_wall_base2",
				() -> new BasicBlock(XKBlockSettings.copyProperties(Blocks.STONE_BRICKS)
						.waterLoggable()
						.component(MouldingComponent.getInstance())
						.shape(XKDeco.id("wall_base2"))
						.get()),
				TAB_FURNITURE_CONTENTS);
		addBlock(
				"light_wall_base",
				() -> new BasicBlock(XKBlockSettings.copyProperties(Blocks.STONE_BRICKS)
						.waterLoggable()
						.component(MouldingComponent.getInstance())
						.shape(XKDeco.id("wall_base"))
						.get()),
				TAB_FURNITURE_CONTENTS);
		addBlock(
				"light_wall_base2",
				() -> new BasicBlock(XKBlockSettings.copyProperties(Blocks.STONE_BRICKS)
						.waterLoggable()
						.component(MouldingComponent.getInstance())
						.shape(XKDeco.id("wall_base2"))
						.get()),
				TAB_FURNITURE_CONTENTS);
		addDirectional("dark_stone_handrail_head", BlockSettingPresets.thingy(null), TAB_FURNITURE_CONTENTS);
		addDirectional("light_stone_handrail_head", BlockSettingPresets.thingy(null), TAB_FURNITURE_CONTENTS);

		addIsotropic("egyptian_brick_column", BlockSettingPresets.stoneColumn(Blocks.STONE_BRICKS, null), TAB_BASIC_CONTENTS);
		addIsotropic(
				"egyptian_brick_column_base",
				BlockSettingPresets.stoneColumn(Blocks.STONE_BRICKS, null),
				TAB_BASIC_CONTENTS);
		addIsotropic(
				"egyptian_brick_column_head",
				BlockSettingPresets.stoneColumn(Blocks.STONE_BRICKS, null),
				TAB_BASIC_CONTENTS);
		addIsotropic("egyptian_bump_column", BlockSettingPresets.stoneColumn(Blocks.STONE_BRICKS, null), TAB_BASIC_CONTENTS);
		addIsotropic(
				"egyptian_bump_column_base",
				BlockSettingPresets.stoneColumn(Blocks.STONE_BRICKS, null),
				TAB_BASIC_CONTENTS);
		addIsotropic(
				"egyptian_bump_column_head",
				BlockSettingPresets.stoneColumn(Blocks.STONE_BRICKS, null),
				TAB_BASIC_CONTENTS);
		addIsotropic("egyptian_carved_column", BlockSettingPresets.stoneColumn(Blocks.STONE_BRICKS, null), TAB_BASIC_CONTENTS);
		addIsotropic(
				"egyptian_carved_column_base",
				BlockSettingPresets.stoneColumn(Blocks.STONE_BRICKS, null),
				TAB_BASIC_CONTENTS);
		addIsotropic("egyptian_column", BlockSettingPresets.stoneColumn(Blocks.STONE_BRICKS, null), TAB_BASIC_CONTENTS);
		addIsotropic("egyptian_column_base", BlockSettingPresets.stoneColumn(Blocks.STONE_BRICKS, null), TAB_BASIC_CONTENTS);
		addIsotropic("egyptian_column_head", BlockSettingPresets.stoneColumn(Blocks.STONE_BRICKS, null), TAB_BASIC_CONTENTS);
		addIsotropic("egyptian_smooth_column", BlockSettingPresets.stoneColumn(Blocks.STONE_BRICKS, null), TAB_BASIC_CONTENTS);
		addIsotropic(
				"egyptian_smooth_column_base",
				BlockSettingPresets.stoneColumn(Blocks.STONE_BRICKS, null),
				TAB_BASIC_CONTENTS);
		addIsotropic("egyptian_stripe_column", BlockSettingPresets.stoneColumn(Blocks.STONE_BRICKS, null), TAB_BASIC_CONTENTS);
		addIsotropic(
				"egyptian_stripe_column_base",
				BlockSettingPresets.stoneColumn(Blocks.STONE_BRICKS, null),
				TAB_BASIC_CONTENTS);
		addIsotropic(
				"egyptian_stripe_column_head",
				BlockSettingPresets.stoneColumn(Blocks.STONE_BRICKS, null),
				TAB_BASIC_CONTENTS);
		addBlock(
				"egyptian_moulding",
				() -> new BasicBlock(XKBlockSettings.copyProperties(Blocks.STONE_BRICKS)
						.waterLoggable()
						.component(MouldingComponent.getInstance())
						.shape(XKDeco.id("wall_base2")) //TODO
						.get()),
				TAB_BASIC_CONTENTS);
		addBlock(
				"egyptian_moulding2",
				() -> new BasicBlock(XKBlockSettings.copyProperties(Blocks.STONE_BRICKS)
						.waterLoggable()
						.component(MouldingComponent.getInstance())
						.shape(XKDeco.id("wall_base2")) //TODO
						.get()),
				TAB_BASIC_CONTENTS);

		addIsotropic("quartz_wall", XKBlockSettings.copyProperties(Blocks.QUARTZ_BLOCK), TAB_BASIC_CONTENTS);
		addIsotropic("greek_column", BlockSettingPresets.stoneColumn(Blocks.STONE_BRICKS, null), TAB_BASIC_CONTENTS);
		addIsotropic(
				"greek_corinthian_column_base",
				BlockSettingPresets.stoneColumn(Blocks.STONE_BRICKS, null),
				TAB_BASIC_CONTENTS);
		addIsotropic(
				"greek_corinthian_column_head",
				BlockSettingPresets.stoneColumn(Blocks.STONE_BRICKS, null),
				TAB_BASIC_CONTENTS);
		addIsotropic("greek_doric_column_head", BlockSettingPresets.stoneColumn(Blocks.STONE_BRICKS, null), TAB_BASIC_CONTENTS);
		addIsotropic("greek_ionic_column_base", BlockSettingPresets.stoneColumn(Blocks.STONE_BRICKS, null), TAB_BASIC_CONTENTS);
		addIsotropic(
				"greek_ionic_column_head",
				BlockSettingPresets.stoneColumn(Blocks.STONE_BRICKS, null).horizontal(),
				TAB_BASIC_CONTENTS);
		addIsotropic(
				"greek_ionic_column_head_corner",
				BlockSettingPresets.stoneColumn(Blocks.STONE_BRICKS, null).horizontal(),
				TAB_BASIC_CONTENTS);
		addBlock(
				"greek_moulding",
				() -> new BasicBlock(XKBlockSettings.copyProperties(Blocks.STONE_BRICKS)
						.waterLoggable()
						.component(MouldingComponent.getInstance())
						.shape(XKDeco.id("wall_base2")) //TODO
						.get()),
				TAB_BASIC_CONTENTS);
		addBlock(
				"greek_moulding2",
				() -> new BasicBlock(XKBlockSettings.copyProperties(Blocks.STONE_BRICKS)
						.waterLoggable()
						.component(MouldingComponent.getInstance())
						.shape(XKDeco.id("wall_base2")) //TODO
						.get()),
				TAB_BASIC_CONTENTS);

		addIsotropic("maya_column", BlockSettingPresets.stoneColumn(Blocks.STONE_BRICKS, null), TAB_BASIC_CONTENTS);
		addIsotropic("maya_stonebrick_column", BlockSettingPresets.stoneColumn(Blocks.STONE_BRICKS, null), TAB_BASIC_CONTENTS);

		addIsotropic("roman_column", BlockSettingPresets.stoneColumn(Blocks.STONE_BRICKS, null), TAB_BASIC_CONTENTS);
		addIsotropic("roman_column_base", BlockSettingPresets.stoneColumn(Blocks.STONE_BRICKS, null), TAB_BASIC_CONTENTS);
		addIsotropic(
				"roman_composite_column_base",
				BlockSettingPresets.stoneColumn(Blocks.STONE_BRICKS, null),
				TAB_BASIC_CONTENTS);
		addIsotropic(
				"roman_composite_column_head",
				BlockSettingPresets.stoneColumn(Blocks.STONE_BRICKS, null),
				TAB_BASIC_CONTENTS);
		addIsotropic(
				"roman_corinthian_column_base",
				BlockSettingPresets.stoneColumn(Blocks.STONE_BRICKS, null),
				TAB_BASIC_CONTENTS);
		addIsotropic(
				"roman_corinthian_column_head",
				BlockSettingPresets.stoneColumn(Blocks.STONE_BRICKS, null),
				TAB_BASIC_CONTENTS);
		addIsotropic(
				"roman_doric_column",
				BlockSettingPresets.stoneColumn(Blocks.STONE_BRICKS, null),
				TAB_BASIC_CONTENTS);
		addIsotropic(
				"roman_doric_column_base",
				BlockSettingPresets.stoneColumn(Blocks.STONE_BRICKS, null),
				TAB_BASIC_CONTENTS);
		addIsotropic(
				"roman_doric_column_head",
				BlockSettingPresets.stoneColumn(Blocks.STONE_BRICKS, null),
				TAB_BASIC_CONTENTS);
		addIsotropic(
				"roman_ionic_column_base",
				BlockSettingPresets.stoneColumn(Blocks.STONE_BRICKS, null),
				TAB_BASIC_CONTENTS);
		addIsotropic(
				"roman_ionic_column_head",
				BlockSettingPresets.stoneColumn(Blocks.STONE_BRICKS, null).horizontal(),
				TAB_BASIC_CONTENTS);
		addIsotropic(
				"roman_ionic_column_head_corner",
				BlockSettingPresets.stoneColumn(Blocks.STONE_BRICKS, null).horizontal(),
				TAB_BASIC_CONTENTS);
		addIsotropic(
				"roman_toscan_column",
				BlockSettingPresets.stoneColumn(Blocks.STONE_BRICKS, null),
				TAB_BASIC_CONTENTS);
		addIsotropic(
				"roman_toscan_column_base",
				BlockSettingPresets.stoneColumn(Blocks.STONE_BRICKS, null),
				TAB_BASIC_CONTENTS);
		addIsotropic(
				"roman_toscan_column_head",
				BlockSettingPresets.stoneColumn(Blocks.STONE_BRICKS, null),
				TAB_BASIC_CONTENTS);
		addBlock(
				"roman_moulding",
				() -> new BasicBlock(XKBlockSettings.copyProperties(Blocks.STONE_BRICKS)
						.waterLoggable()
						.component(MouldingComponent.getInstance())
						.shape(XKDeco.id("wall_base2")) //TODO
						.get()),
				TAB_BASIC_CONTENTS);
		addBlock(
				"roman_moulding2",
				() -> new BasicBlock(XKBlockSettings.copyProperties(Blocks.STONE_BRICKS)
						.waterLoggable()
						.component(MouldingComponent.getInstance())
						.shape(XKDeco.id("wall_base2")) //TODO
						.get()),
				TAB_BASIC_CONTENTS);
		addBlock(
				"hollow_steel_bars",
				() -> new IronBarsBlock(BlockSettingPresets.hollowSteel()
						.renderType(KiwiModule.RenderLayer.Layer.CUTOUT_MIPPED)
						.get()),
				TAB_FURNITURE_CONTENTS);

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
		var settings = XKBlockSettings.builder()
				.waterLoggable()
				.component(FrontAndTopComponent.getInstance())
				.shape(XKDeco.id("screen"))
				.configure($ -> {
					if (!"off".equals(id)) {
						$.lightLevel($$ -> 9);
					}
				});
		addBlock("screen_" + id, () -> new BasicBlock(settings.get()), TAB_FURNITURE_CONTENTS);
	}

	private static void addBlock(
			String id,
			Supplier<Block> blockSupplier,
			Collection<RegistryObject<Item>> tabContents) {
		var itemProperties = new Item.Properties();
		var block = BLOCKS.register(id, blockSupplier);
		tabContents.add(ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties)));
	}

	public static void addTreatedWood(String id, MapColor mapColor) {
		addIsotropic(id + "_wood", copyProperties(Blocks.OAK_WOOD, mapColor), TAB_BASIC_CONTENTS);
		addIsotropic(id + "_log", copyProperties(Blocks.OAK_LOG, mapColor), TAB_BASIC_CONTENTS);
		addIsotropic(id + "_log_slab", copyProperties(Blocks.OAK_LOG, mapColor), TAB_BASIC_CONTENTS);

		addIsotropic(id + "_planks", copyProperties(Blocks.OAK_PLANKS, mapColor), TAB_BASIC_CONTENTS);
		addIsotropic(id + "_slab", copyProperties(Blocks.OAK_SLAB, mapColor), TAB_BASIC_CONTENTS);
		addIsotropic(id + "_stairs", copyProperties(Blocks.OAK_STAIRS, mapColor), TAB_BASIC_CONTENTS);

		addIsotropic(id + "_table", BlockSettingPresets.thingy(mapColor, SoundType.WOOD), TAB_FURNITURE_CONTENTS);
		addIsotropic(id + "_big_table", BlockSettingPresets.thingy(mapColor, SoundType.WOOD), TAB_FURNITURE_CONTENTS);
		addIsotropic(id + "_tall_table", BlockSettingPresets.thingy(mapColor, SoundType.WOOD), TAB_FURNITURE_CONTENTS);
		addBlock(id + "_desk", () -> new BasicBlock(BlockSettingPresets.thingy(mapColor, SoundType.WOOD)
				.horizontalAxis()
				.shape(XKDeco.id("big_table"))
				.get()), TAB_FURNITURE_CONTENTS);
		addBasic(
				id + "_stool",
				"xkdeco:long_stool",
				false,
				BlockSettingPresets.thingy(mapColor, SoundType.WOOD),
				TAB_FURNITURE_CONTENTS);
		addBasic(
				id + "_chair",
				"xkdeco:chair",
				false,
				BlockSettingPresets.thingy(mapColor, SoundType.WOOD),
				TAB_FURNITURE_CONTENTS);
		addBasic(
				id + "_empty_shelf",
				"xkdeco:shelf",
				false,
				BlockSettingPresets.thingy(mapColor, SoundType.WOOD),
				TAB_FURNITURE_CONTENTS);
		addBasic(
				id + "_shelf",
				"xkdeco:shelf",
				false,
				BlockSettingPresets.thingy(mapColor, SoundType.WOOD),
				TAB_FURNITURE_CONTENTS);
		addBasic(
				id + "_divided_shelf",
				"xkdeco:shelf",
				false,
				BlockSettingPresets.thingy(mapColor, SoundType.WOOD),
				TAB_FURNITURE_CONTENTS);

		addBlock(
				id + "_fence",
				() -> new FenceBlock(copyProperties(Blocks.OAK_FENCE, mapColor).renderType(KiwiModule.RenderLayer.Layer.CUTOUT).get()),
				TAB_FURNITURE_CONTENTS);
		addDirectional(
				id + "_fence_head",
				copyProperties(Blocks.OAK_FENCE, mapColor).shape(XKDeco.id("wooden_fence_head")),
				TAB_FURNITURE_CONTENTS);
		addBlock(
				id + "_fence_gate",
				() -> new FenceGateBlock(copyProperties(Blocks.OAK_FENCE_GATE, mapColor).get(), WoodType.OAK),
				TAB_FURNITURE_CONTENTS);
		addIsotropic(id + "_door", copyProperties(Blocks.OAK_DOOR, mapColor), TAB_FURNITURE_CONTENTS);
		addIsotropic(
				id + "_trapdoor",
				copyProperties(Blocks.OAK_TRAPDOOR, mapColor),
				TAB_FURNITURE_CONTENTS);
		addBlock(
				id + "_column_wall",
				() -> new WallBlock(copyProperties(Blocks.OAK_FENCE, mapColor).get()),
				TAB_FURNITURE_CONTENTS);
		addBlock(
				"hollow_" + id + "_column_wall",
				() -> new WallBlock(copyProperties(Blocks.OAK_FENCE, mapColor).get()),
				TAB_FURNITURE_CONTENTS);

		addBlock(
				id + "_meiren_kao",
				() -> new BasicBlock(copyProperties(Blocks.OAK_PLANKS).waterLoggable()
						.shape(XKDeco.id("meiren_kao"))
						.noOcclusion()
						.component(MouldingComponent.getInstance())
						.get()),
				TAB_FURNITURE_CONTENTS);
		addBlock(
				id + "_meiren_kao_with_column",
				() -> new BasicBlock(copyProperties(Blocks.OAK_PLANKS).waterLoggable()
						.shape(XKDeco.id("meiren_kao_with_column"))
						.noOcclusion()
						.component(MouldingComponent.getInstance())
						.get()),
				TAB_FURNITURE_CONTENTS);
		addBlock(
				id + "_dougong",
				() -> new BasicBlock(copyProperties(Blocks.OAK_PLANKS)
						.waterLoggable()
						.noOcclusion()
						.component(MouldingComponent.getInstance())
						.get()),
				TAB_FURNITURE_CONTENTS);
		addBlock(
				id + "_dougong_connection",
				() -> new BasicBlock(copyProperties(Blocks.OAK_PLANKS)
						.waterLoggable()
						.noOcclusion()
						.component(MouldingComponent.getInstance())
						.get()),
				TAB_FURNITURE_CONTENTS);
		addBlock(
				id + "_dougong_hollow_connection",
				() -> new BasicBlock(copyProperties(Blocks.OAK_PLANKS)
						.waterLoggable()
						.noOcclusion()
						.component(MouldingComponent.getInstance())
						.get()),
				TAB_FURNITURE_CONTENTS);
		addBlock(
				id + "_window",
				() -> new TrapDoorBlock(
						copyProperties(Blocks.OAK_TRAPDOOR, mapColor).renderType(KiwiModule.RenderLayer.Layer.CUTOUT).get(),
						BlockSetType.OAK),
				TAB_FURNITURE_CONTENTS);
		addBlock(
				id + "_awning_window",
				() -> new TrapDoorBlock(
						copyProperties(Blocks.OAK_TRAPDOOR, mapColor).renderType(KiwiModule.RenderLayer.Layer.CUTOUT).get(),
						BlockSetType.OAK),
				TAB_FURNITURE_CONTENTS);
		addBlock(
				id + "_hanging_fascia",
				() -> new HangingFasciaBlock(copyProperties(Blocks.OAK_PLANKS, mapColor).shape(XKDeco.id("hanging_fascia"))),
				TAB_FURNITURE_CONTENTS);
	}
}
