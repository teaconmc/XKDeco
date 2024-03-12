package org.teacon.xkdeco.init;

import static net.minecraft.world.level.block.Block.box;
import static org.teacon.xkdeco.block.settings.XKBlockSettings.copyProperties;
import static org.teacon.xkdeco.init.XKDecoProperties.*;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.teacon.xkdeco.XKDeco;
import org.teacon.xkdeco.block.AirDuctBlock;
import org.teacon.xkdeco.block.BasicBlock;
import org.teacon.xkdeco.block.FallenLeavesBlock;
import org.teacon.xkdeco.block.HorizontalShiftBlock;
import org.teacon.xkdeco.block.MimicWallBlock;
import org.teacon.xkdeco.block.PlantSlabBlock;
import org.teacon.xkdeco.block.PlantSlabSnowyDirtBlock;
import org.teacon.xkdeco.block.RoofBlock;
import org.teacon.xkdeco.block.RoofEaveBlock;
import org.teacon.xkdeco.block.RoofEndBlock;
import org.teacon.xkdeco.block.RoofFlatBlock;
import org.teacon.xkdeco.block.RoofHorizontalShiftBlock;
import org.teacon.xkdeco.block.RoofRidgeBlock;
import org.teacon.xkdeco.block.RoofRidgeEndAsianBlock;
import org.teacon.xkdeco.block.RoofTipBlock;
import org.teacon.xkdeco.block.SpecialBlockDisplayBlock;
import org.teacon.xkdeco.block.SpecialConsole;
import org.teacon.xkdeco.block.SpecialCupBlock;
import org.teacon.xkdeco.block.SpecialDessertBlock;
import org.teacon.xkdeco.block.SpecialItemDisplayBlock;
import org.teacon.xkdeco.block.SpecialLightBar;
import org.teacon.xkdeco.block.SpecialWardrobeBlock;
import org.teacon.xkdeco.block.settings.CanSurviveHandler;
import org.teacon.xkdeco.block.settings.GlassType;
import org.teacon.xkdeco.block.settings.ShapeGenerator;
import org.teacon.xkdeco.block.settings.ShapeStorage;
import org.teacon.xkdeco.block.settings.WaterLoggableComponent;
import org.teacon.xkdeco.block.settings.XKBlockSettings;
import org.teacon.xkdeco.blockentity.BlockDisplayBlockEntity;
import org.teacon.xkdeco.blockentity.ItemDisplayBlockEntity;
import org.teacon.xkdeco.blockentity.MimicWallBlockEntity;
import org.teacon.xkdeco.blockentity.WardrobeBlockEntity;
import org.teacon.xkdeco.entity.CushionEntity;
import org.teacon.xkdeco.item.MimicWallItem;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.mojang.datafixers.DSL;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
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
	public static final String GRASS_BLOCK_PREFIX = "grass_block_";
	public static final String MYCELIUM__PREFIX = "mycelium_";
	public static final String PODZOL_PREFIX = "podzol_";

	public static final String LOG_SUFFIX = "_log";
	public static final String WOOD_SUFFIX = "_wood";
	public static final String SLAB_SUFFIX = "_slab";
	public static final String PATH_SUFFIX = "_path";
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
	public static final String CONSOLE_SUFFIX = "_console";
	public static final String VENT_FAN_SUFFIX = "_vent_fan";

	public static final String REFRESHMENT_SPECIAL = "refreshments";
	public static final String FRUIT_PLATTER_SPECIAL = "fruit_platter";
	public static final String ITEM_PROJECTOR_SPECIAL = "item_projector";
	public static final String FACTORY_LIGHT_BAR_SPECIAL = "factory_light_bar";

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
			BlockBehaviour.Properties properties,
			Item.Properties itemProperties,
			Collection<RegistryObject<Item>> tabContents) {
		VoxelShape northShape = ShapeStorage.getInstance().get(northShapeId);
		Preconditions.checkNotNull(northShape, "Missing shape for block: %s, shape: %s", id, northShapeId);
		RegistryObject<BasicBlock> block;
		if (northShape == Shapes.block()) {
			block = BLOCKS.register(id, () -> new BasicBlock(XKBlockSettings.builder().horizontal().get()));
		} else {
			block = BLOCKS.register(
					id,
					() -> new BasicBlock(BlockSettingPresets.thingy(null)
							.horizontal()
							.shape(ShapeGenerator.horizontal(northShape))
							.canSurviveHandler(isSupportNeeded ? CanSurviveHandler.checkFloor() : null)
							.get()));
		}
		tabContents.add(ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties)));
	}

	private static void addDirectionalBasic(
			String id,
			String shapeDownId,
			BlockBehaviour.Properties properties,
			Item.Properties itemProperties,
			Collection<RegistryObject<Item>> tabContents) {
		//TODO shape
		var block = BLOCKS.register(id, () -> new BasicBlock(XKBlockSettings.builder().waterLoggable().directional().get()));
		tabContents.add(ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties)));
	}

	private static void addIsotropic(
			String id,
			XKBlockSettings.Builder settings,
			Item.Properties itemProperties,
			Collection<RegistryObject<Item>> tabContents) {
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
					.shape(ShapeGenerator.unit(ShapeStorage.getInstance().get(XKDeco.id("big_table"))))
					.get());
		} else if (id.contains(TABLE_SUFFIX)) {
			blockSupplier = () -> new BasicBlock(BlockSettingPresets.thingy(null)
					.shape(ShapeGenerator.unit(ShapeStorage.getInstance().get(XKDeco.id("table"))))
					.get());
		} else if (id.contains("_trapdoor")) {
			blockSupplier = () -> new TrapDoorBlock(
					settings.get(),
					id.contains("factory") || id.contains("steel") ? BlockSetType.IRON : BlockSetType.OAK);
		} else if (id.endsWith("_door")) {
			blockSupplier = () -> new DoorBlock(
					settings.get(),
					id.contains("factory") || id.contains("steel") ? BlockSetType.IRON : BlockSetType.OAK);
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
			Item.Properties itemProperties,
			Collection<RegistryObject<Item>> tabContents,
			boolean asian) {
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
			Item.Properties itemProperties,
			Collection<RegistryObject<Item>> tabContents) {
		var isPath = id.contains(PATH_SUFFIX);
		if (id.contains(LEAVES_SUFFIX) || id.contains(BLOSSOM_SUFFIX)) {
			if (id.startsWith(FALLEN_LEAVES_PREFIX)) {
				var block = BLOCKS.register(id, () -> new FallenLeavesBlock(settings.get()));
				tabContents.add(ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties)));
				return;
			}
			var block = BLOCKS.register(id, () -> new LeavesBlock(settings.get()));
			tabContents.add(ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties)));
		} else if (id.contains(SLAB_SUFFIX)) {
			var block = BLOCKS.register(id, () -> new PlantSlabBlock(settings.get(), isPath, "dirt_slab"));
			tabContents.add(ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties)));
		} else {
			throw new IllegalArgumentException("Illegal id (" + id + ") for plant blocks");
		}
	}

	private static void addSpecial(
			String id,
			BlockBehaviour.Properties properties,
			Item.Properties itemProperties,
			Collection<RegistryObject<Item>> tabContents) {
		if (id.equals(REFRESHMENT_SPECIAL) || id.equals(FRUIT_PLATTER_SPECIAL)) {
			var block = BLOCKS.register(id, () -> new SpecialDessertBlock(BlockSettingPresets.thingy(null)
					.shape(ShapeGenerator.unit(ShapeStorage.getInstance().get(XKDeco.id("dessert"))))
					.canSurviveHandler(CanSurviveHandler.checkFloor())
					.get()));
			tabContents.add(ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties)));
		} else if (id.contains(ITEM_DISPLAY_SUFFIX) || id.equals(ITEM_PROJECTOR_SPECIAL)) {
			var block = BLOCKS.register(id, () -> new SpecialItemDisplayBlock(properties));
			tabContents.add(ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties)));
		} else if (id.contains(BLOCK_DISPLAY_SUFFIX)) {
			var block = BLOCKS.register(id, () -> new SpecialBlockDisplayBlock(properties));
			tabContents.add(ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties)));
		} else if (id.contains(WARDROBE_SUFFIX)) {
			var block = BLOCKS.register(id, () -> new SpecialWardrobeBlock(properties));
			tabContents.add(ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties)));
		} else if (id.equals(FACTORY_LIGHT_BAR_SPECIAL)) {
			var block = BLOCKS.register(id, () -> new SpecialLightBar(properties));
			tabContents.add(ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties)));
		} else if (id.contains(VENT_FAN_SUFFIX)) {
			//TODO shape
			var block = BLOCKS.register(id, () -> new BasicBlock(XKBlockSettings.builder().waterLoggable().directional().get()));
			tabContents.add(ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties)));
		} else if (id.contains(CONSOLE_SUFFIX)) {
			var block = BLOCKS.register(id, () -> new SpecialConsole(properties));
			tabContents.add(ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties)));
		} else {
			throw new IllegalArgumentException("Illegal id (" + id + ") for special blocks");
		}
	}

	private static void addItem(String id, Item.Properties itemProperties, Collection<RegistryObject<Item>> tabContents) {
		tabContents.add(ITEMS.register(id, () -> new Item(itemProperties)));
	}

	private static void addDisplayBlockEntity() {
		BLOCK_ENTITY.register(ITEM_DISPLAY_BLOCK_ENTITY, () -> BlockEntityType.Builder.of(
				ItemDisplayBlockEntity::new,
				BLOCKS.getEntries()
						.stream()
						.map(RegistryObject::get)
						.filter(b -> b instanceof SpecialItemDisplayBlock)
						.toArray(Block[]::new)).build(DSL.remainderType()));
		BLOCK_ENTITY.register(BLOCK_DISPLAY_BLOCK_ENTITY, () -> BlockEntityType.Builder.of(
				BlockDisplayBlockEntity::new,
				BLOCKS.getEntries()
						.stream()
						.map(RegistryObject::get)
						.filter(b -> b instanceof SpecialBlockDisplayBlock)
						.toArray(Block[]::new)).build(DSL.remainderType()));
	}

	private static void addWardrobeBlockEntity() {
		BLOCK_ENTITY.register(WARDROBE_BLOCK_ENTITY, () -> BlockEntityType.Builder.of(
				WardrobeBlockEntity::new,
				BLOCKS.getEntries()
						.stream()
						.map(RegistryObject::get)
						.filter(b -> b instanceof SpecialWardrobeBlock)
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

	public static void addSpecialWallItems(RegisterEvent event) {
		for (var entry : ForgeRegistries.BLOCKS.getEntries()) {
			var block = entry.getValue();
			if (block instanceof MimicWallBlock wall) {
				var registryName = entry.getKey().location();
				event.register(ForgeRegistries.Keys.ITEMS, registryName, () -> new MimicWallItem(wall, XKDecoProperties.ITEM_STRUCTURE));
				TAB_STRUCTURE_CONTENTS.add(RegistryObject.create(registryName, ForgeRegistries.ITEMS));
			}
		}
	}

	public static void addSpecialWallBlockEntity(RegisterEvent event) {
		var blocks = ForgeRegistries.BLOCKS.getValues().stream().filter(MimicWallBlock.class::isInstance).toArray(Block[]::new);
		var registryName = new ResourceLocation(XKDeco.ID, WALL_BLOCK_ENTITY);
		event.register(
				ForgeRegistries.Keys.BLOCK_ENTITY_TYPES,
				registryName,
				() -> BlockEntityType.Builder.of(MimicWallBlockEntity::new, blocks).build(DSL.remainderType()));
	}

	public static void addSpecialWallTags(TagsUpdatedEvent event) {
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

		addIsotropic("black_tiles", BlockSettingPresets.blackTiles(), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("black_tile_slab", BlockSettingPresets.blackTiles(), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("black_tile_stairs", BlockSettingPresets.blackTiles(), ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("cyan_tiles", BlockSettingPresets.cyanTiles(), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("cyan_tile_slab", BlockSettingPresets.cyanTiles(), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("cyan_tile_stairs", BlockSettingPresets.cyanTiles(), ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("yellow_tiles", BlockSettingPresets.yellowTiles(), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("yellow_tile_slab", BlockSettingPresets.yellowTiles(), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("yellow_tile_stairs", BlockSettingPresets.yellowTiles(), ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("blue_tiles", BlockSettingPresets.blueTiles(), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("blue_tile_slab", BlockSettingPresets.blueTiles(), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("blue_tile_stairs", BlockSettingPresets.blueTiles(), ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("green_tiles", BlockSettingPresets.greenTiles(), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("green_tile_slab", BlockSettingPresets.greenTiles(), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("green_tile_stairs", BlockSettingPresets.greenTiles(), ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("red_tiles", BlockSettingPresets.redTiles(), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("red_tile_slab", BlockSettingPresets.redTiles(), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("red_tile_stairs", BlockSettingPresets.redTiles(), ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("steel_tiles", BlockSettingPresets.steel(), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("steel_tile_slab", BlockSettingPresets.steel(), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("steel_tile_stairs", BlockSettingPresets.steel(), ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("copper_tiles", copyProperties(Blocks.COPPER_BLOCK), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("copper_tile_slab", copyProperties(Blocks.COPPER_BLOCK), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("copper_tile_stairs", copyProperties(Blocks.COPPER_BLOCK), ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("glass_tiles", copyProperties(Blocks.GLASS), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("glass_tile_slab", copyProperties(Blocks.GLASS), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("glass_tile_stairs", copyProperties(Blocks.GLASS), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("glass_trapdoor", copyProperties(Blocks.GLASS), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("glass_door", copyProperties(Blocks.GLASS), ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("mud_wall_block", BlockSettingPresets.mudWall(), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("mud_wall_slab", BlockSettingPresets.mudWall(), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("mud_wall_stairs", BlockSettingPresets.mudWall(), ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("framed_mud_wall_block", BlockSettingPresets.mudWall(), ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("lined_mud_wall_block", BlockSettingPresets.mudWall(), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("lined_mud_wall_slab", BlockSettingPresets.mudWall(), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("lined_mud_wall_stairs", BlockSettingPresets.mudWall(), ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("crossed_mud_wall_block", BlockSettingPresets.mudWall(), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("crossed_mud_wall_slab", BlockSettingPresets.mudWall(), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("crossed_mud_wall_stairs", BlockSettingPresets.mudWall(), ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("dirty_mud_wall_block", BlockSettingPresets.mudWall(), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("dirty_mud_wall_slab", BlockSettingPresets.mudWall(), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("dirty_mud_wall_stairs", BlockSettingPresets.mudWall(), ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("cyan_bricks", BlockSettingPresets.cyanTiles(), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("cyan_brick_slab", BlockSettingPresets.cyanTiles(), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("cyan_brick_stairs", BlockSettingPresets.cyanTiles(), ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("black_bricks", BlockSettingPresets.blackTiles(), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("black_brick_slab", BlockSettingPresets.blackTiles(), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("black_brick_stairs", BlockSettingPresets.blackTiles(), ITEM_BASIC, TAB_BASIC_CONTENTS);

		addTreatedWood("varnished", MapColor.WOOD);
		addTreatedWood("ebony", MapColor.WOOD);
		addTreatedWood("mahogany", MapColor.WOOD);

		addIsotropic("sandstone_pillar", copyProperties(Blocks.SANDSTONE), ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("polished_sandstone", copyProperties(Blocks.SANDSTONE), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("polished_sandstone_slab", copyProperties(Blocks.SANDSTONE), ITEM_BASIC, TAB_BASIC_CONTENTS);
//		addIsotropic("polished_sandstone_stairs", copyProperties(Blocks.SANDSTONE), ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("sandstone_bricks", copyProperties(Blocks.SANDSTONE), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("sandstone_brick_slab", copyProperties(Blocks.SANDSTONE), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("sandstone_brick_stairs", copyProperties(Blocks.SANDSTONE), ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("sandstone_small_bricks", copyProperties(Blocks.SANDSTONE), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("sandstone_small_brick_slab", copyProperties(Blocks.SANDSTONE), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("sandstone_small_brick_stairs", copyProperties(Blocks.SANDSTONE), ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("red_sandstone_pillar", copyProperties(Blocks.SANDSTONE), ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("polished_red_sandstone", copyProperties(Blocks.SANDSTONE), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("polished_red_sandstone_slab", copyProperties(Blocks.SANDSTONE), ITEM_BASIC, TAB_BASIC_CONTENTS);
//		addIsotropic("polished_red_sandstone_stairs", copyProperties(Blocks.SANDSTONE), ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("red_sandstone_bricks", copyProperties(Blocks.STONE_BRICKS), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("red_sandstone_brick_slab", copyProperties(Blocks.STONE_BRICKS), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("red_sandstone_brick_stairs", copyProperties(Blocks.STONE_BRICKS), ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("red_sandstone_small_bricks", copyProperties(Blocks.STONE_BRICKS), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("red_sandstone_small_brick_slab", copyProperties(Blocks.STONE_BRICKS), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("red_sandstone_small_brick_stairs", copyProperties(Blocks.STONE_BRICKS), ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("stone_brick_pillar", copyProperties(Blocks.STONE_BRICKS), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("stone_brick_pavement", copyProperties(Blocks.STONE_BRICKS), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("stone_brick_pavement_slab", copyProperties(Blocks.STONE_BRICKS), ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("deepslate_pillar", copyProperties(Blocks.DEEPSLATE), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("deepslate_pavement", copyProperties(Blocks.DEEPSLATE), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("deepslate_pavement_slab", copyProperties(Blocks.DEEPSLATE), ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("mossy_deepslate_bricks", copyProperties(Blocks.STONE_BRICKS), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("mossy_deepslate_brick_slab", copyProperties(Blocks.STONE_BRICKS), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("mossy_deepslate_brick_stairs", copyProperties(Blocks.STONE_BRICKS), ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("blackstone_pillar", copyProperties(Blocks.BLACKSTONE), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("blackstone_pavement", copyProperties(Blocks.BLACKSTONE), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("blackstone_pavement_slab", copyProperties(Blocks.BLACKSTONE), ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("gilded_blackstone_bricks", copyProperties(Blocks.STONE_BRICKS), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("gilded_blackstone_brick_slab", copyProperties(Blocks.STONE_BRICKS), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("gilded_blackstone_brick_stairs", copyProperties(Blocks.STONE_BRICKS), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("gilded_blackstone_brick_pillar", copyProperties(Blocks.STONE_BRICKS), ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("chiseled_gilded_blackstone", copyProperties(Blocks.GILDED_BLACKSTONE), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("luxury_gilded_blackstone", copyProperties(Blocks.GILDED_BLACKSTONE), ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("maya_stone", copyProperties(Blocks.STONE), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("maya_stone_slab", copyProperties(Blocks.STONE), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("maya_stone_stairs", copyProperties(Blocks.STONE), ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("maya_stonebricks", copyProperties(Blocks.STONE_BRICKS), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("maya_stonebrick_slab", copyProperties(Blocks.STONE_BRICKS), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("maya_stonebrick_stairs", copyProperties(Blocks.STONE_BRICKS), ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("maya_bricks", copyProperties(Blocks.STONE_BRICKS), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("maya_brick_slab", copyProperties(Blocks.STONE_BRICKS), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("maya_brick_stairs", copyProperties(Blocks.STONE_BRICKS), ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("maya_polished_stonebricks", copyProperties(Blocks.STONE_BRICKS), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("maya_polished_stonebrick_slab", copyProperties(Blocks.STONE_BRICKS), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("maya_polished_stonebrick_stairs", copyProperties(Blocks.STONE_BRICKS), ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("maya_mossy_stonebricks", copyProperties(Blocks.STONE_BRICKS), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("maya_mossy_stonebrick_slab", copyProperties(Blocks.STONE_BRICKS), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("maya_mossy_stonebrick_stairs", copyProperties(Blocks.STONE_BRICKS), ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("maya_mossy_bricks", copyProperties(Blocks.STONE_BRICKS), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("maya_mossy_brick_slab", copyProperties(Blocks.STONE_BRICKS), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("maya_mossy_brick_stairs", copyProperties(Blocks.STONE_BRICKS), ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("maya_chiseled_stonebricks", copyProperties(Blocks.STONE_BRICKS), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("maya_cut_stonebricks", copyProperties(Blocks.STONE_BRICKS), ITEM_BASIC, TAB_BASIC_CONTENTS);

		addBasic("maya_single_screw_thread_stone", "block", false, copyProperties(Blocks.STONE).get(), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("maya_double_screw_thread_stone", copyProperties(Blocks.STONE), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("maya_quad_screw_thread_stone", copyProperties(Blocks.STONE), ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("maya_pictogram_stone", copyProperties(Blocks.STONE), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("maya_skull_stone", copyProperties(Blocks.STONE), ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("maya_pillar", copyProperties(Blocks.STONE), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("maya_mossy_pillar", copyProperties(Blocks.STONE), ITEM_BASIC, TAB_BASIC_CONTENTS);

		addBasic(
				"maya_crystal_skull",
				"xkdeco:maya_crystal_skull",
				false,
				copyProperties(Blocks.DEEPSLATE).get(),
				ITEM_FURNITURE,
				TAB_FURNITURE_CONTENTS);

		addIsotropic("aztec_stonebricks", copyProperties(Blocks.STONE_BRICKS), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("aztec_stonebrick_slab", copyProperties(Blocks.STONE_BRICKS), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("aztec_stonebrick_stairs", copyProperties(Blocks.STONE_BRICKS), ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("aztec_mossy_stonebricks", copyProperties(Blocks.STONE_BRICKS), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("aztec_mossy_stonebrick_slab", copyProperties(Blocks.STONE_BRICKS), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("aztec_mossy_stonebrick_stairs", copyProperties(Blocks.STONE_BRICKS), ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("aztec_sculpture_stone", copyProperties(Blocks.STONE_BRICKS), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("aztec_chiseled_stonebricks", copyProperties(Blocks.STONE_BRICKS), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("aztec_cut_stonebricks", copyProperties(Blocks.STONE_BRICKS), ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("inca_stone", copyProperties(Blocks.STONE), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("inca_stone_slab", copyProperties(Blocks.STONE), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("inca_stone_stairs", copyProperties(Blocks.STONE), ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("inca_stonebricks", copyProperties(Blocks.STONE_BRICKS), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("inca_stonebrick_slab", copyProperties(Blocks.STONE_BRICKS), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("inca_stonebrick_stairs", copyProperties(Blocks.STONE_BRICKS), ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("inca_bricks", copyProperties(Blocks.STONE_BRICKS), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("inca_brick_slab", copyProperties(Blocks.STONE_BRICKS), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("inca_brick_stairs", copyProperties(Blocks.STONE_BRICKS), ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("cut_obsidian", copyProperties(Blocks.OBSIDIAN), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("cut_obsidian_slab", copyProperties(Blocks.OBSIDIAN), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("cut_obsidian_pillar", copyProperties(Blocks.OBSIDIAN), ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("cut_obsidian_bricks", copyProperties(Blocks.OBSIDIAN), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("cut_obsidian_brick_slab", copyProperties(Blocks.OBSIDIAN), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("cut_obsidian_brick_stairs", copyProperties(Blocks.OBSIDIAN), ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("crying_obsidian_bricks", copyProperties(Blocks.OBSIDIAN), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("crying_obsidian_brick_slab", copyProperties(Blocks.OBSIDIAN), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("crying_obsidian_brick_stairs", copyProperties(Blocks.OBSIDIAN), ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("cut_gold_block", copyProperties(Blocks.GOLD_BLOCK), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("cut_gold_block_slab", copyProperties(Blocks.GOLD_BLOCK), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("cut_gold_block_stairs", copyProperties(Blocks.GOLD_BLOCK), ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("gold_bricks", copyProperties(Blocks.GOLD_BLOCK), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("gold_brick_slab", copyProperties(Blocks.GOLD_BLOCK), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("gold_brick_stairs", copyProperties(Blocks.GOLD_BLOCK), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("gold_pillar", copyProperties(Blocks.GOLD_BLOCK), ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("chiseled_gold_block", copyProperties(Blocks.GOLD_BLOCK), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("painted_gold_block", copyProperties(Blocks.GOLD_BLOCK), ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("bronze_block", copyProperties(Blocks.COPPER_BLOCK), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("smooth_bronze_block", copyProperties(Blocks.COPPER_BLOCK), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("inscription_bronze_block", copyProperties(Blocks.COPPER_BLOCK), ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("cut_bronze_block", copyProperties(Blocks.COPPER_BLOCK), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("cut_bronze_block_slab", copyProperties(Blocks.COPPER_BLOCK), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("cut_bronze_block_stairs", copyProperties(Blocks.COPPER_BLOCK), ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("chiseled_bronze_block", copyProperties(Blocks.COPPER_BLOCK), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addBasic("screw_thread_bronze_block", "block", false, copyProperties(Blocks.COPPER_BLOCK).get(), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("bronze_pillar", copyProperties(Blocks.COPPER_BLOCK), ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("steel_block", BlockSettingPresets.steel(), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("smooth_steel_block", BlockSettingPresets.steel(), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("steel_pillar", BlockSettingPresets.steel(), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("steel_trapdoor", BlockSettingPresets.steel(), ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("steel_floor", BlockSettingPresets.steel(), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("steel_floor_slab", BlockSettingPresets.steel(), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("steel_floor_stairs", BlockSettingPresets.steel(), ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("chiseled_steel_block", BlockSettingPresets.steel(), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("hollow_steel_block", BlockSettingPresets.hollowSteel().waterLoggable(), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("hollow_steel_trapdoor", BlockSettingPresets.hollowSteel(), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("framed_steel_block", BlockSettingPresets.steel(), ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("factory_block", BlockSettingPresets.steel(), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("factory_slab", BlockSettingPresets.steel(), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("factory_stairs", BlockSettingPresets.steel(), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("factory_trapdoor", BlockSettingPresets.steel(), ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("factory_block_rusting", BlockSettingPresets.steel(), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("factory_slab_rusting", BlockSettingPresets.steel(), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("factory_stairs_rusting", BlockSettingPresets.steel(), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("factory_trapdoor_rusting", BlockSettingPresets.steel(), ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("factory_block_rusted", BlockSettingPresets.steel(), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("factory_slab_rusted", BlockSettingPresets.steel(), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("factory_stairs_rusted", BlockSettingPresets.steel(), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("factory_trapdoor_rusted", BlockSettingPresets.steel(), ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("factory_danger", BlockSettingPresets.steel(), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("factory_danger_rusting", BlockSettingPresets.steel(), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("factory_danger_rusted", BlockSettingPresets.steel(), ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("factory_attention", BlockSettingPresets.steel(), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("factory_attention_rusting", BlockSettingPresets.steel(), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("factory_attention_rusted", BlockSettingPresets.steel(), ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("factory_electricity", BlockSettingPresets.steel(), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("factory_electricity_rusting", BlockSettingPresets.steel(), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("factory_electricity_rusted", BlockSettingPresets.steel(), ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("factory_toxic", BlockSettingPresets.steel(), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("factory_toxic_rusting", BlockSettingPresets.steel(), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("factory_toxic_rusted", BlockSettingPresets.steel(), ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("factory_radiation", BlockSettingPresets.steel(), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("factory_radiation_rusting", BlockSettingPresets.steel(), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("factory_radiation_rusted", BlockSettingPresets.steel(), ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("factory_biohazard", BlockSettingPresets.steel(), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("factory_biohazard_rusting", BlockSettingPresets.steel(), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("factory_biohazard_rusted", BlockSettingPresets.steel(), ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("factory_lamp_block", BlockSettingPresets.lampBlock(), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("factory_lamp_slab", BlockSettingPresets.lampBlock(), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("factory_lamp_stairs", BlockSettingPresets.lampBlock(), ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("tech_lamp_block", BlockSettingPresets.lampBlock(), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("tech_lamp_slab", BlockSettingPresets.lampBlock(), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("tech_lamp_stairs", BlockSettingPresets.lampBlock(), ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("translucent_lamp_block", BlockSettingPresets.lampBlock().noOcclusion(), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("translucent_lamp_slab", BlockSettingPresets.lampBlock().noOcclusion(), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("translucent_lamp_stairs", BlockSettingPresets.lampBlock().noOcclusion(), ITEM_BASIC, TAB_BASIC_CONTENTS);

		//TODO gravity?
		addIsotropic("steel_filings", copyProperties(Blocks.SAND), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("quartz_sand", copyProperties(Blocks.SAND), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("toughened_sand", copyProperties(Blocks.SAND), ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("quartz_glass", BlockSettingPresets.hardenedGlass(), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("quartz_glass_slab", BlockSettingPresets.hardenedGlass(), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("quartz_glass_stairs", BlockSettingPresets.hardenedGlass(), ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("toughened_glass", BlockSettingPresets.hardenedGlass(), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("toughened_glass_slab", BlockSettingPresets.hardenedGlass(), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("toughened_glass_stairs", BlockSettingPresets.hardenedGlass(), ITEM_BASIC, TAB_BASIC_CONTENTS);

		addRoof("black_roof", () -> BlockSettingPresets.blackTiles().noOcclusion().get(), ITEM_STRUCTURE, TAB_STRUCTURE_CONTENTS, true);
		addRoof("cyan_roof", () -> BlockSettingPresets.cyanTiles().noOcclusion().get(), ITEM_STRUCTURE, TAB_STRUCTURE_CONTENTS, true);
		addRoof("yellow_roof", () -> BlockSettingPresets.yellowTiles().noOcclusion().get(), ITEM_STRUCTURE, TAB_STRUCTURE_CONTENTS, true);
		addRoof("blue_roof", () -> BlockSettingPresets.blueTiles().noOcclusion().get(), ITEM_STRUCTURE, TAB_STRUCTURE_CONTENTS, false);
		addRoof("green_roof", () -> BlockSettingPresets.greenTiles().noOcclusion().get(), ITEM_STRUCTURE, TAB_STRUCTURE_CONTENTS, false);
		addRoof("red_roof", () -> BlockSettingPresets.redTiles().noOcclusion().get(), ITEM_STRUCTURE, TAB_STRUCTURE_CONTENTS, false);

		addPlant("dirt_slab", copyProperties(Blocks.DIRT).sustainsPlant(), ITEM_NATURE, TAB_NATURE_CONTENTS);
		addPlant("dirt_path_slab", copyProperties(Blocks.DIRT_PATH), ITEM_NATURE, TAB_NATURE_CONTENTS);
		addPlant("grass_block_slab", copyProperties(Blocks.GRASS_BLOCK).sustainsPlant(), ITEM_NATURE, TAB_NATURE_CONTENTS);
		addPlant("mycelium_slab", copyProperties(Blocks.MYCELIUM).sustainsPlant(), ITEM_NATURE, TAB_NATURE_CONTENTS);
		addPlant("podzol_slab", copyProperties(Blocks.PODZOL).sustainsPlant(), ITEM_NATURE, TAB_NATURE_CONTENTS);

		addIsotropic("netherrack_slab", copyProperties(Blocks.NETHERRACK), ITEM_NATURE, TAB_NATURE_CONTENTS);
		addIsotropic("crimson_nylium_slab", copyProperties(Blocks.CRIMSON_NYLIUM), ITEM_NATURE, TAB_NATURE_CONTENTS);
		addIsotropic("warped_nylium_slab", copyProperties(Blocks.WARPED_NYLIUM), ITEM_NATURE, TAB_NATURE_CONTENTS);
		addIsotropic("end_stone_slab", copyProperties(Blocks.END_STONE), ITEM_NATURE, TAB_NATURE_CONTENTS);

		addIsotropic("dirt_cobblestone", copyProperties(Blocks.SANDSTONE), ITEM_NATURE, TAB_NATURE_CONTENTS);
		addIsotropic("grass_cobblestone", copyProperties(Blocks.SANDSTONE), ITEM_NATURE, TAB_NATURE_CONTENTS);
		addIsotropic("sandy_cobblestone", copyProperties(Blocks.SANDSTONE), ITEM_NATURE, TAB_NATURE_CONTENTS);
		addIsotropic("snowy_cobblestone", copyProperties(Blocks.SANDSTONE), ITEM_NATURE, TAB_NATURE_CONTENTS);

		addIsotropic("cobblestone_path", copyProperties(Blocks.SANDSTONE), ITEM_NATURE, TAB_NATURE_CONTENTS);
		addIsotropic("dirt_cobblestone_path", copyProperties(Blocks.SANDSTONE), ITEM_NATURE, TAB_NATURE_CONTENTS);
		addIsotropic("grass_cobblestone_path", copyProperties(Blocks.SANDSTONE), ITEM_NATURE, TAB_NATURE_CONTENTS);
		addIsotropic("sandy_cobblestone_path", copyProperties(Blocks.SANDSTONE), ITEM_NATURE, TAB_NATURE_CONTENTS);
		addIsotropic("snowy_cobblestone_path", copyProperties(Blocks.SANDSTONE), ITEM_NATURE, TAB_NATURE_CONTENTS);

		addIsotropic("dirt_cobblestone_slab", copyProperties(Blocks.SANDSTONE), ITEM_NATURE, TAB_NATURE_CONTENTS);
		addIsotropic("grass_cobblestone_slab", copyProperties(Blocks.SANDSTONE), ITEM_NATURE, TAB_NATURE_CONTENTS);
		addIsotropic("sandy_cobblestone_slab", copyProperties(Blocks.SANDSTONE), ITEM_NATURE, TAB_NATURE_CONTENTS);
		addIsotropic("snowy_cobblestone_slab", copyProperties(Blocks.SANDSTONE), ITEM_NATURE, TAB_NATURE_CONTENTS);

		addIsotropic("cobblestone_path_slab", copyProperties(Blocks.SANDSTONE), ITEM_NATURE, TAB_NATURE_CONTENTS);
		addIsotropic("dirt_cobblestone_path_slab", copyProperties(Blocks.SANDSTONE), ITEM_NATURE, TAB_NATURE_CONTENTS);
		addIsotropic("grass_cobblestone_path_slab", copyProperties(Blocks.SANDSTONE), ITEM_NATURE, TAB_NATURE_CONTENTS);
		addIsotropic("sandy_cobblestone_path_slab", copyProperties(Blocks.SANDSTONE), ITEM_NATURE, TAB_NATURE_CONTENTS);
		addIsotropic("snowy_cobblestone_path_slab", copyProperties(Blocks.SANDSTONE), ITEM_NATURE, TAB_NATURE_CONTENTS);

		addIsotropic("dirt_cobblestone_stairs", copyProperties(Blocks.SANDSTONE), ITEM_NATURE, TAB_NATURE_CONTENTS);
		addIsotropic("grass_cobblestone_stairs", copyProperties(Blocks.SANDSTONE), ITEM_NATURE, TAB_NATURE_CONTENTS);
		addIsotropic("sandy_cobblestone_stairs", copyProperties(Blocks.SANDSTONE), ITEM_NATURE, TAB_NATURE_CONTENTS);
		addIsotropic("snowy_cobblestone_stairs", copyProperties(Blocks.SANDSTONE), ITEM_NATURE, TAB_NATURE_CONTENTS);

		addIsotropic("cobblestone_path_stairs", copyProperties(Blocks.SANDSTONE), ITEM_NATURE, TAB_NATURE_CONTENTS);
		addIsotropic("dirt_cobblestone_path_stairs", copyProperties(Blocks.SANDSTONE), ITEM_NATURE, TAB_NATURE_CONTENTS);
		addIsotropic("grass_cobblestone_path_stairs", copyProperties(Blocks.SANDSTONE), ITEM_NATURE, TAB_NATURE_CONTENTS);
		addIsotropic("sandy_cobblestone_path_stairs", copyProperties(Blocks.SANDSTONE), ITEM_NATURE, TAB_NATURE_CONTENTS);
		addIsotropic("snowy_cobblestone_path_stairs", copyProperties(Blocks.SANDSTONE), ITEM_NATURE, TAB_NATURE_CONTENTS);

		addPlant("ginkgo_leaves", copyProperties(Blocks.OAK_LEAVES, MapColor.GOLD), ITEM_NATURE, TAB_NATURE_CONTENTS);
		addPlant("orange_maple_leaves", copyProperties(Blocks.OAK_LEAVES, MapColor.COLOR_ORANGE), ITEM_NATURE, TAB_NATURE_CONTENTS);
		addPlant("red_maple_leaves", copyProperties(Blocks.OAK_LEAVES, MapColor.COLOR_RED), ITEM_NATURE, TAB_NATURE_CONTENTS);
		addPlant("peach_blossom", copyProperties(Blocks.CHERRY_LEAVES), ITEM_NATURE, TAB_NATURE_CONTENTS);
		addPlant("peach_blossom_leaves", copyProperties(Blocks.CHERRY_LEAVES), ITEM_NATURE, TAB_NATURE_CONTENTS);
		addPlant("cherry_blossom", copyProperties(Blocks.CHERRY_LEAVES), ITEM_NATURE, TAB_NATURE_CONTENTS);
		addPlant("cherry_blossom_leaves", copyProperties(Blocks.CHERRY_LEAVES), ITEM_NATURE, TAB_NATURE_CONTENTS);
		addPlant("white_cherry_blossom", copyProperties(Blocks.CHERRY_LEAVES, MapColor.SNOW), ITEM_NATURE, TAB_NATURE_CONTENTS);
		addPlant("white_cherry_blossom_leaves", copyProperties(Blocks.CHERRY_LEAVES, MapColor.SNOW), ITEM_NATURE, TAB_NATURE_CONTENTS);
		addPlant("plantable_leaves", copyProperties(Blocks.OAK_LEAVES).sustainsPlant(), ITEM_NATURE, TAB_NATURE_CONTENTS);
		addPlant("plantable_leaves_dark", copyProperties(Blocks.OAK_LEAVES).sustainsPlant(), ITEM_NATURE, TAB_NATURE_CONTENTS);
		addPlant("willow_leaves", copyProperties(Blocks.OAK_LEAVES), ITEM_NATURE, TAB_NATURE_CONTENTS);
		addPlant("hanging_willow_leaves", copyProperties(Blocks.VINE), ITEM_NATURE, TAB_NATURE_CONTENTS); //TODO proper class

		addBasic("miniature_tree", "xkdeco:miniature", false, BLOCK_MINIATURE, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("miniature_cherry", "xkdeco:miniature", false, BLOCK_MINIATURE, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("miniature_ginkgo", "xkdeco:miniature", false, BLOCK_MINIATURE, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("miniature_maple", "xkdeco:miniature", false, BLOCK_MINIATURE, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("miniature_bamboo", "xkdeco:miniature", false, BLOCK_MINIATURE, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("miniature_coral", "xkdeco:miniature", false, BLOCK_MINIATURE, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("miniature_red_coral", "xkdeco:miniature", false, BLOCK_MINIATURE, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("miniature_mount", "xkdeco:miniature", false, BLOCK_MINIATURE, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("miniature_succulents", "xkdeco:miniature", false, BLOCK_MINIATURE, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);

		addBasic("teapot", "xkdeco:teapot", true, BLOCK_MINIATURE, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBlock(
				"cup",
				() -> new SpecialCupBlock(BlockSettingPresets.thingy(null).canSurviveHandler(CanSurviveHandler.checkFloor()).get()),
				ITEM_FURNITURE,
				TAB_FURNITURE_CONTENTS);
		addBasic("tea_ware", "xkdeco:tea_ware", true, BLOCK_MINIATURE, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addSpecial("refreshments", BLOCK_DESSERT, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addSpecial("fruit_platter", BLOCK_DESSERT, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("calligraphy", "carpet", true, BLOCK_CARPET, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("ink_painting", "carpet", true, BLOCK_CARPET, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("weiqi_board", "xkdeco:board", true, BLOCK_BOARD, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("xiangqi_board", "xkdeco:board", true, BLOCK_BOARD, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);

		addSpecial("plain_item_display", BLOCK_STONE_DISPLAY, ITEM_FUNCTIONAL, TAB_FUNCTIONAL_CONTENTS);
		addSpecial("gorgeous_item_display", BLOCK_STONE_DISPLAY, ITEM_FUNCTIONAL, TAB_FUNCTIONAL_CONTENTS);
		addSpecial("mechanical_item_display", BLOCK_METAL_DISPLAY, ITEM_FUNCTIONAL, TAB_FUNCTIONAL_CONTENTS);
		addSpecial("tech_item_display", BLOCK_METAL_DISPLAY, ITEM_FUNCTIONAL, TAB_FUNCTIONAL_CONTENTS);
		addSpecial("item_projector", BLOCK_METAL_DISPLAY, ITEM_FUNCTIONAL, TAB_FUNCTIONAL_CONTENTS);

		addSpecial("plain_block_display", BLOCK_STONE_DISPLAY, ITEM_FUNCTIONAL, TAB_FUNCTIONAL_CONTENTS);
		addSpecial("gorgeous_block_display", BLOCK_STONE_DISPLAY, ITEM_FUNCTIONAL, TAB_FUNCTIONAL_CONTENTS);
		addSpecial("mechanical_block_display", BLOCK_METAL_DISPLAY, ITEM_FUNCTIONAL, TAB_FUNCTIONAL_CONTENTS);
		addSpecial("tech_block_display", BLOCK_METAL_DISPLAY, ITEM_FUNCTIONAL, TAB_FUNCTIONAL_CONTENTS);

		addDisplayBlockEntity();

		addSpecial("varnished_wardrobe", BLOCK_WOOD_WARDROBE, ITEM_FUNCTIONAL, TAB_FUNCTIONAL_CONTENTS);
		addSpecial("ebony_wardrobe", BLOCK_WOOD_WARDROBE, ITEM_FUNCTIONAL, TAB_FUNCTIONAL_CONTENTS);
		addSpecial("mahogany_wardrobe", BLOCK_WOOD_WARDROBE, ITEM_FUNCTIONAL, TAB_FUNCTIONAL_CONTENTS);
		addSpecial("iron_wardrobe", BLOCK_METAL_WARDROBE, ITEM_FUNCTIONAL, TAB_FUNCTIONAL_CONTENTS);
		addSpecial("glass_wardrobe", BLOCK_METAL_WARDROBE, ITEM_FUNCTIONAL, TAB_FUNCTIONAL_CONTENTS);
		addSpecial("full_glass_wardrobe", BLOCK_GLASS_WARDROBE, ITEM_FUNCTIONAL, TAB_FUNCTIONAL_CONTENTS);

		addWardrobeBlockEntity();

		addBasic("white_porcelain", "xkdeco:porcelain", false, BLOCK_PORCELAIN, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("white_porcelain_tall", "xkdeco:porcelain", false, BLOCK_PORCELAIN, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic(
				"white_porcelain_small",
				"xkdeco:porcelain_small",
				false,
				BLOCK_PORCELAIN,
				ITEM_FURNITURE,
				TAB_FURNITURE_CONTENTS);
		addBasic("bluewhite_porcelain", "xkdeco:porcelain", false, BLOCK_PORCELAIN, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("bluewhite_porcelain_tall", "xkdeco:porcelain", false, BLOCK_PORCELAIN, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic(
				"bluewhite_porcelain_small",
				"xkdeco:porcelain_small",
				false,
				BLOCK_PORCELAIN,
				ITEM_FURNITURE,
				TAB_FURNITURE_CONTENTS);
		addBasic("celadon_porcelain", "xkdeco:porcelain", false, BLOCK_PORCELAIN, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("celadon_porcelain_tall", "xkdeco:porcelain", false, BLOCK_PORCELAIN, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic(
				"celadon_porcelain_small",
				"xkdeco:porcelain_small",
				false,
				BLOCK_PORCELAIN,
				ITEM_FURNITURE,
				TAB_FURNITURE_CONTENTS);

		addBasic("paper_lantern", "xkdeco:lantern", false, BLOCK_LANTERN, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("red_lantern", "xkdeco:lantern", false, BLOCK_LANTERN, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("festival_lantern", "xkdeco:festival_lantern", false, BLOCK_LANTERN, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addDirectionalBasic("oil_lamp", "xkdeco:oil_lamp", BLOCK_METAL_LIGHT, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("candlestick", "xkdeco:candlestick", false, BLOCK_METAL_LIGHT, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("big_candlestick", "xkdeco:big_candlestick", false, BLOCK_METAL_LIGHT, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addDirectionalBasic(
				"empty_candlestick",
				"xkdeco:empty_candlestick",
				BLOCK_METAL_WITHOUT_LIGHT,
				ITEM_FURNITURE,
				TAB_FURNITURE_CONTENTS);
		addBasic("covered_lamp", "xkdeco:covered_lamp", false, BLOCK_WOOD_LIGHT, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("roofed_lamp", "xkdeco:big_candlestick", false, BLOCK_STONE_LIGHT, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("stone_lamp", "xkdeco:stone_lamp", false, BLOCK_STONE_LIGHT, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("deepslate_lamp", "xkdeco:stone_lamp", false, BLOCK_STONE_LIGHT, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("blackstone_lamp", "xkdeco:stone_lamp", false, BLOCK_STONE_LIGHT, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("fish_bowl", "xkdeco:fish_bowl", false, BLOCK_STONE_NO_OCCLUSION, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("dark_fish_bowl", "xkdeco:fish_bowl", false, BLOCK_STONE_NO_OCCLUSION, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic(
				"stone_water_bowl",
				"xkdeco:water_bowl",
				false,
				BLOCK_STONE_NO_OCCLUSION,
				ITEM_FURNITURE,
				TAB_FURNITURE_CONTENTS);
		addBasic(
				"stone_water_tank",
				"xkdeco:water_tank",
				false,
				BLOCK_STONE_NO_OCCLUSION,
				ITEM_FURNITURE,
				TAB_FURNITURE_CONTENTS);
		addBasic("fish_tank", "xkdeco:fish_tank", false, BLOCK_GLASS_NO_OCCLUSION, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic(
				"empty_fish_tank",
				"xkdeco:fish_tank",
				false,
				BLOCK_GLASS_NO_OCCLUSION,
				ITEM_FURNITURE,
				TAB_FURNITURE_CONTENTS);

		addBasic(
				"small_book_stack",
				"xkdeco:small_book_stack",
				false,
				BLOCK_WOOD_FURNITURE,
				ITEM_FURNITURE,
				TAB_FURNITURE_CONTENTS);
		addBasic(
				"big_book_stack",
				"xkdeco:big_book_stack",
				false,
				BLOCK_WOOD_FURNITURE,
				ITEM_FURNITURE,
				TAB_FURNITURE_CONTENTS);
		addBasic(
				"empty_bottle_stack",
				"xkdeco:bottle_stack",
				false,
				BLOCK_GLASS_NO_OCCLUSION,
				ITEM_FURNITURE,
				TAB_FURNITURE_CONTENTS);
		addBasic(
				"bottle_stack",
				"xkdeco:bottle_stack",
				false,
				BLOCK_GLASS_NO_OCCLUSION,
				ITEM_FURNITURE,
				TAB_FURNITURE_CONTENTS);
		addBasic("wood_globe", "xkdeco:covered_lamp", false, BLOCK_WOOD_FURNITURE, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("globe", "xkdeco:covered_lamp", false, BLOCK_WOOD_FURNITURE, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic(
				"solar_system_model",
				"xkdeco:solar_system_model",
				false,
				BLOCK_WOOD_FURNITURE,
				ITEM_FURNITURE,
				TAB_FURNITURE_CONTENTS);
		addBasic("big_solar_system_model", "block", false, BLOCK_WOOD_FURNITURE, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("telescope", "block", false, BLOCK_WOOD_FURNITURE, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);

		addPlant("fallen_ginkgo_leaves", BlockSettingPresets.fallenLeaves(MapColor.GOLD), ITEM_NATURE, TAB_NATURE_CONTENTS);
		addPlant("fallen_orange_maple_leaves", BlockSettingPresets.fallenLeaves(MapColor.COLOR_ORANGE), ITEM_NATURE, TAB_NATURE_CONTENTS);
		addPlant("fallen_red_maple_leaves", BlockSettingPresets.fallenLeaves(MapColor.COLOR_RED), ITEM_NATURE, TAB_NATURE_CONTENTS);
		addPlant("fallen_peach_blossom", BlockSettingPresets.fallenLeaves(MapColor.COLOR_PINK), ITEM_NATURE, TAB_NATURE_CONTENTS);
		addPlant("fallen_cherry_blossom", BlockSettingPresets.fallenLeaves(MapColor.COLOR_PINK), ITEM_NATURE, TAB_NATURE_CONTENTS);
		addPlant("fallen_white_cherry_blossom", BlockSettingPresets.fallenLeaves(MapColor.SNOW), ITEM_NATURE, TAB_NATURE_CONTENTS);

		addDirectionalBasic(
				"factory_lamp",
				"xkdeco:factory_lamp",
				BLOCK_METAL_LIGHT,
				ITEM_FURNITURE,
				TAB_FURNITURE_CONTENTS);
		addDirectionalBasic(
				"factory_lamp_broken",
				"xkdeco:factory_lamp",
				BLOCK_METAL_WITHOUT_LIGHT,
				ITEM_FURNITURE,
				TAB_FURNITURE_CONTENTS);
		addDirectionalBasic(
				"factory_warning_lamp",
				"xkdeco:factory_lamp",
				BLOCK_METAL_HALF_LIGHT,
				ITEM_FURNITURE,
				TAB_FURNITURE_CONTENTS);

		addSpecial("factory_light_bar", BLOCK_METAL_LIGHT_NO_COLLISSION, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);

		//FIXME non-directional
		addBasic("factory_ceiling_lamp", "xkdeco:factory_ceiling_lamp", false, BLOCK_METAL_LIGHT, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("factory_pendant", "xkdeco:factory_pendant", false, BLOCK_METAL_LIGHT, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);

		addDirectionalBasic("fan_blade", "xkdeco:fan", BLOCK_METAL_NO_OCCLUSION, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);

		addSpecial("factory_vent_fan", BLOCK_METAL_NO_OCCLUSION, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addSpecial("factory_vent_fan_big", BLOCK_METAL_NO_OCCLUSION, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);

		addDirectionalBasic("steel_windmill", "xkdeco:fan", BLOCK_METAL_NO_OCCLUSION, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addDirectionalBasic("iron_windmill", "xkdeco:fan", BLOCK_METAL_NO_OCCLUSION, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addDirectionalBasic("wooden_windmill", "xkdeco:fan", BLOCK_WOOD_FURNITURE, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);

		addSpecial("mechanical_console", BLOCK_METAL_LIGHT, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);

		addBasic("mechanical_screen", "xkdeco:screen2", false, BLOCK_METAL_NO_OCCLUSION, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("mechanical_chair", "xkdeco:chair", false, BLOCK_METAL_NO_OCCLUSION, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);

		addSpecial("tech_console", BLOCK_METAL_LIGHT, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("tech_screen", "xkdeco:screen2", false, BLOCK_METAL_NO_OCCLUSION, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("tech_chair", "xkdeco:chair", false, BLOCK_METAL_NO_OCCLUSION, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);

		addBasic("screen_off", "xkdeco:screen", false, BLOCK_METAL_NO_OCCLUSION, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("screen", "xkdeco:screen", false, BLOCK_METAL_NO_OCCLUSION, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("screen_cube", "xkdeco:screen", false, BLOCK_METAL_NO_OCCLUSION, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("screen_diagram", "xkdeco:screen", false, BLOCK_METAL_NO_OCCLUSION, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("screen_dna", "xkdeco:screen", false, BLOCK_METAL_NO_OCCLUSION, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("screen_list", "xkdeco:screen", false, BLOCK_METAL_NO_OCCLUSION, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("screen_message", "xkdeco:screen", false, BLOCK_METAL_NO_OCCLUSION, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("screen_threebodies", "xkdeco:screen", false, BLOCK_METAL_NO_OCCLUSION, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("screen_transport", "xkdeco:screen", false, BLOCK_METAL_NO_OCCLUSION, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);

		addBasic("tech_table", "xkdeco:tech_table", false, BLOCK_METAL_NO_OCCLUSION, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic(
				"tech_table_circle",
				"xkdeco:tech_table",
				false,
				BLOCK_METAL_NO_OCCLUSION,
				ITEM_FURNITURE,
				TAB_FURNITURE_CONTENTS);
		addBasic(
				"tech_table_bigcircle",
				"xkdeco:tech_table",
				false,
				BLOCK_METAL_NO_OCCLUSION,
				ITEM_FURNITURE,
				TAB_FURNITURE_CONTENTS);

		addDirectionalBasic(
				"hologram_base",
				"xkdeco:hologram_base",
				BLOCK_METAL_NO_COLLISSION,
				ITEM_FURNITURE,
				TAB_FURNITURE_CONTENTS);
		addItem("hologram_planet", ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addItem("hologram_dna", ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addItem("hologram_pictures", ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addItem("hologram_message", ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addItem("hologram_xekr_logo", ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);

		addBasic("sign_entrance", "xkdeco:screen", false, BLOCK_METAL_NO_OCCLUSION, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("sign_exit", "xkdeco:screen", false, BLOCK_METAL_NO_OCCLUSION, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("sign_left", "xkdeco:screen", false, BLOCK_METAL_NO_OCCLUSION, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("sign_right", "xkdeco:screen", false, BLOCK_METAL_NO_OCCLUSION, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);

		addBasic(
				"small_sign_left",
				"xkdeco:screen",
				false,
				BLOCK_METAL_LIGHT_NO_COLLISSION,
				ITEM_FURNITURE,
				TAB_FURNITURE_CONTENTS);
		addBasic(
				"small_sign_right",
				"xkdeco:screen",
				false,
				BLOCK_METAL_LIGHT_NO_COLLISSION,
				ITEM_FURNITURE,
				TAB_FURNITURE_CONTENTS);
		addBasic(
				"small_sign_ground",
				"carpet",
				false,
				BLOCK_METAL_LIGHT_NO_COLLISSION,
				ITEM_FURNITURE,
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
		}, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBlock("air_duct_oblique", () -> {
			VoxelShape trueNorth = ShapeStorage.getInstance().get(XKDeco.id("air_duct"));
			VoxelShape falseNorth = ShapeStorage.getInstance().get(XKDeco.id("air_duct2"));
			return new HorizontalShiftBlock(BlockSettingPresets.hollowSteel()
					.shape(ShapeGenerator.horizontalShifted(trueNorth, falseNorth))
					.interactionShape(ShapeGenerator.unit(Shapes.block()))
					.get());
		}, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBlock("hollow_steel_beam", () -> new WallBlock(BlockSettingPresets.hollowSteel().get()), ITEM_BASIC, TAB_BASIC_CONTENTS);
	}

	private static void addBlock(
			String id,
			Supplier<Block> blockSupplier,
			Item.Properties itemProp,
			Collection<RegistryObject<Item>> tabContents) {
		var block = BLOCKS.register(id, blockSupplier);
		tabContents.add(ITEMS.register(id, () -> new BlockItem(block.get(), itemProp)));
	}

	public static void addTreatedWood(String id, MapColor mapColor) {
		addIsotropic(id + "_wood", copyProperties(Blocks.OAK_WOOD, mapColor), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic(id + "_log", copyProperties(Blocks.OAK_LOG, mapColor), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic(id + "_log_slab", copyProperties(Blocks.OAK_LOG, mapColor), ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic(id + "_planks", copyProperties(Blocks.OAK_PLANKS, mapColor), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic(id + "_slab", copyProperties(Blocks.OAK_SLAB, mapColor), ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic(id + "_stairs", copyProperties(Blocks.OAK_STAIRS, mapColor), ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic(id + "_table", BlockSettingPresets.thingy(mapColor, SoundType.WOOD), ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addIsotropic(id + "_big_table", BlockSettingPresets.thingy(mapColor, SoundType.WOOD), ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addIsotropic(id + "_tall_table", BlockSettingPresets.thingy(mapColor, SoundType.WOOD), ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic(
				id + "_desk",
				"xkdeco:big_table",
				false,
				BlockSettingPresets.thingy(mapColor, SoundType.WOOD).get(),
				ITEM_FURNITURE,
				TAB_FURNITURE_CONTENTS);
		addBasic(
				id + "_stool",
				"xkdeco:long_stool",
				false,
				BlockSettingPresets.thingy(mapColor, SoundType.WOOD).get(),
				ITEM_FURNITURE,
				TAB_FURNITURE_CONTENTS);
		addBasic(
				id + "_chair",
				"xkdeco:chair",
				false,
				BlockSettingPresets.thingy(mapColor, SoundType.WOOD).get(),
				ITEM_FURNITURE,
				TAB_FURNITURE_CONTENTS);
		addBasic(
				id + "_empty_shelf",
				"xkdeco:shelf",
				false,
				BlockSettingPresets.thingy(mapColor, SoundType.WOOD).get(),
				ITEM_FURNITURE,
				TAB_FURNITURE_CONTENTS);
		addBasic(
				id + "_shelf",
				"xkdeco:shelf",
				false,
				BlockSettingPresets.thingy(mapColor, SoundType.WOOD).get(),
				ITEM_FURNITURE,
				TAB_FURNITURE_CONTENTS);
		addBasic(
				id + "_divided_shelf",
				"xkdeco:shelf",
				false,
				BlockSettingPresets.thingy(mapColor, SoundType.WOOD).get(),
				ITEM_FURNITURE,
				TAB_FURNITURE_CONTENTS);

		addBlock(
				id + "_fence",
				() -> new FenceBlock(copyProperties(Blocks.OAK_FENCE, mapColor).get()),
				ITEM_FURNITURE,
				TAB_FURNITURE_CONTENTS);
		addBlock(
				id + "_fence_gate",
				() -> new FenceGateBlock(copyProperties(Blocks.OAK_FENCE_GATE, mapColor).get(), WoodType.OAK),
				ITEM_FURNITURE,
				TAB_FURNITURE_CONTENTS);
		addIsotropic(id + "_door", copyProperties(Blocks.OAK_DOOR, mapColor), ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addIsotropic(
				id + "_trapdoor",
				copyProperties(Blocks.OAK_TRAPDOOR, mapColor),
				ITEM_FURNITURE,
				TAB_FURNITURE_CONTENTS);
		addBlock(
				id + "_column_wall",
				() -> new WallBlock(copyProperties(Blocks.OAK_FENCE, mapColor).get()),
				ITEM_FURNITURE,
				TAB_FURNITURE_CONTENTS);
		addBlock(
				"hollow_" + id + "_column_wall",
				() -> new WallBlock(copyProperties(Blocks.OAK_FENCE, mapColor).get()),
				ITEM_FURNITURE,
				TAB_FURNITURE_CONTENTS);
	}
}
