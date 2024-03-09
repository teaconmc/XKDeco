package org.teacon.xkdeco.init;

import static org.teacon.xkdeco.init.XKDecoProperties.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.teacon.xkdeco.XKDeco;
import org.teacon.xkdeco.block.AirDuctBlock;
import org.teacon.xkdeco.block.BasicBlock;
import org.teacon.xkdeco.block.BasicCubeBlock;
import org.teacon.xkdeco.block.BasicFullDirectionBlock;
import org.teacon.xkdeco.block.FallenLeavesBlock;
import org.teacon.xkdeco.block.HorizontalShiftBlock;
import org.teacon.xkdeco.block.IsotropicCubeBlock;
import org.teacon.xkdeco.block.IsotropicHollowBlock;
import org.teacon.xkdeco.block.IsotropicPillarBlock;
import org.teacon.xkdeco.block.IsotropicSlabBlock;
import org.teacon.xkdeco.block.IsotropicStairBlock;
import org.teacon.xkdeco.block.PlantSlabBlock;
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
import org.teacon.xkdeco.block.SpecialWallBlock;
import org.teacon.xkdeco.block.SpecialWardrobeBlock;
import org.teacon.xkdeco.block.settings.XKDBlockSettings;
import org.teacon.xkdeco.blockentity.BlockDisplayBlockEntity;
import org.teacon.xkdeco.blockentity.ItemDisplayBlockEntity;
import org.teacon.xkdeco.blockentity.WallBlockEntity;
import org.teacon.xkdeco.blockentity.WardrobeBlockEntity;
import org.teacon.xkdeco.entity.CushionEntity;
import org.teacon.xkdeco.item.SpecialWallItem;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Direction;
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
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;
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

	public static final String WALL_BLOCK_ENTITY = "special_wall";
	public static final String ITEM_DISPLAY_BLOCK_ENTITY = "item_display";
	public static final String BLOCK_DISPLAY_BLOCK_ENTITY = "block_display";
	public static final String WARDROBE_BLOCK_ENTITY = "wardrobe";

	public static final String GRASS_PREFIX = "grass_";
	public static final String GLASS_PREFIX = "glass_";
	public static final String LINED_PREFIX = "lined_";
	public static final String WILLOW_PREFIX = "willow_";
	public static final String HOLLOW_PREFIX = "hollow_";
	public static final String LUXURY_PREFIX = "luxury_";
	public static final String PAINTED_PREFIX = "painted_";
	public static final String CHISELED_PREFIX = "chiseled_";
	public static final String PLANTABLE_PREFIX = "plantable_";
	public static final String TRANSLUCENT_PREFIX = "translucent_";
	public static final String DOUBLE_SCREW_PREFIX = "double_screw_";
	public static final String SPECIAL_WALL_PREFIX = "special_wall_";
	public static final String STONE_WATER_PREFIX = "stone_water_";

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
	public static final String GLASS_SUFFIX = "_glass";
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

	public static final String CUP_SPECIAL = "cup";
	public static final String REFRESHMENT_SPECIAL = "refreshments";
	public static final String FRUIT_PLATTER_SPECIAL = "fruit_platter";
	public static final String ITEM_PROJECTOR_SPECIAL = "item_projector";
	public static final String FACTORY_LIGHT_BAR_SPECIAL = "factory_light_bar";

	private static void addCushionEntity() {
		ENTITIES.register(CUSHION_ENTITY, () -> EntityType.Builder.<CushionEntity>of(CushionEntity::new, MobCategory.MISC).sized(
				1F / 256F,
				1F / 256F).setTrackingRange(256).build(new ResourceLocation(XKDeco.ID, CUSHION_ENTITY).toString()));
	}

	private static void addBasic(
			String id,
			ShapeFunction shapeFunction,
			boolean isSupportNeeded,
			BlockBehaviour.Properties properties,
			Item.Properties itemProperties,
			Collection<RegistryObject<Item>> tabContents) {
		var horizontalShapes = Maps.toMap(Direction.Plane.HORIZONTAL, shapeFunction::getShape);
		if (!isSupportNeeded && horizontalShapes.values().stream().anyMatch(s -> Block.isFaceFull(Shapes.join(
				Shapes.block(),
				s,
				BooleanOp.ONLY_FIRST), Direction.DOWN))) {
			var shapes = Maps.toMap(Arrays.stream(Direction.values()).toList(), shapeFunction::getShape);
			var block = BLOCKS.register(id, () -> new BasicFullDirectionBlock(properties, shapes));
			tabContents.add(ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties)));
		} else if (horizontalShapes.values().stream().allMatch(Block::isShapeFullBlock)) {
			var block = BLOCKS.register(id, () -> new BasicCubeBlock(properties, horizontalShapes));
			tabContents.add(ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties)));
		} else {
			var block = BLOCKS.register(id, () -> new BasicBlock(properties, isSupportNeeded, horizontalShapes));
			tabContents.add(ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties)));
		}
	}

	private static void addIsotropic(
			String id,
			BlockBehaviour.Properties properties,
			Item.Properties itemProperties,
			Collection<RegistryObject<Item>> tabContents) {
		var isGlass = id.contains(GLASS_SUFFIX) || id.contains(TRANSLUCENT_PREFIX) || id.contains(GLASS_PREFIX);
		if (id.contains(SLAB_SUFFIX)) {
			var block = BLOCKS.register(id, () -> new IsotropicSlabBlock(properties, isGlass));
			tabContents.add(ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties)));
		} else if (id.contains(STAIRS_SUFFIX)) {
			var block = BLOCKS.register(id, () -> new IsotropicStairBlock(properties, isGlass));
			tabContents.add(ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties)));
		} else if (id.contains(LOG_SUFFIX) || id.contains(WOOD_SUFFIX) || id.contains(PILLAR_SUFFIX)) {
			var block = BLOCKS.register(id, () -> new IsotropicPillarBlock(properties, isGlass));
			tabContents.add(ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties)));
		} else if (id.contains(LINED_PREFIX) || id.contains(LUXURY_PREFIX) || id.contains(PAINTED_PREFIX) || id.contains(CHISELED_PREFIX) ||
				id.contains(DOUBLE_SCREW_PREFIX)) {
			var block = BLOCKS.register(id, () -> new IsotropicPillarBlock(properties, isGlass));
			tabContents.add(ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties)));
		} else if (id.contains(BIG_TABLE_SUFFIX) || id.contains(TALL_TABLE_SUFFIX)) {
			var block = BLOCKS.register(id, () -> new IsotropicHollowBlock(properties, IsotropicHollowBlock.BIG_TABLE_SHAPE));
			tabContents.add(ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties)));
		} else if (id.contains(TABLE_SUFFIX)) {
			var block = BLOCKS.register(id, () -> new IsotropicHollowBlock(properties, IsotropicHollowBlock.TABLE_SHAPE));
			tabContents.add(ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties)));
		} else if (id.contains("_trapdoor")) {
			var block = BLOCKS.register(
					id,
					() -> new TrapDoorBlock(properties, properties == BLOCK_WOOD ? BlockSetType.OAK : BlockSetType.IRON));
			tabContents.add(ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties)));
		} else if (id.endsWith("_door")) {
			var block = BLOCKS.register(
					id,
					() -> new DoorBlock(properties, properties == BLOCK_WOOD ? BlockSetType.OAK : BlockSetType.IRON));
			tabContents.add(ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties)));
		} else if (id.contains(HOLLOW_PREFIX)) {
			var block = BLOCKS.register(id, () -> new IsotropicHollowBlock(properties, Shapes.block()));
			tabContents.add(ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties)));
		} else {
			var block = BLOCKS.register(id, () -> new IsotropicCubeBlock(properties, isGlass));
			tabContents.add(ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties)));
		}
	}

	private static void addRoof(
			String id,
			BlockBehaviour.Properties properties,
			Item.Properties itemProperties,
			Collection<RegistryObject<Item>> tabContents,
			boolean asian) {
		var roof = BLOCKS.register(id, () -> new RoofBlock(properties));
		tabContents.add(ITEMS.register(id, () -> new BlockItem(roof.get(), itemProperties)));
		var roofRidge = BLOCKS.register(id + "_ridge", () -> new RoofRidgeBlock(properties, asian));
		tabContents.add(ITEMS.register(id + "_ridge", () -> new BlockItem(roofRidge.get(), itemProperties)));
		var roofSmallEnd = BLOCKS.register(id + "_small_end", () -> new RoofEndBlock(properties, true));
		tabContents.add(ITEMS.register(id + "_small_end", () -> new BlockItem(roofSmallEnd.get(), itemProperties)));
		var smallRidgeEnd = BLOCKS.register(id + "_small_ridge_end", () -> {
			if (asian) {
				return new RoofRidgeEndAsianBlock(properties, true);
			} else {
				return new RoofRidgeEndAsianBlock(properties, true);
			}
		});
		var smallFlatEnd = BLOCKS.register(id + "_small_flat_end", () -> new RoofHorizontalShiftBlock(properties));

		//TODO remove in the future:
		tabContents.add(ITEMS.register(id + "_small_ridge_end", () -> new BlockItem(smallRidgeEnd.get(), itemProperties)));
		tabContents.add(ITEMS.register(id + "_small_flat_end", () -> new BlockItem(smallFlatEnd.get(), itemProperties)));

		var roofSmallEave = BLOCKS.register(id + "_small_eave", () -> new RoofEaveBlock(properties, true));
		tabContents.add(ITEMS.register(id + "_small_eave", () -> new BlockItem(roofSmallEave.get(), itemProperties)));
		var roofFlat = BLOCKS.register(id + "_flat", () -> new RoofFlatBlock(properties));
		tabContents.add(ITEMS.register(id + "_flat", () -> new BlockItem(roofFlat.get(), itemProperties)));

		if (!asian) {
			return;
		}

		var roofEave = BLOCKS.register(id + "_eave", () -> new RoofEaveBlock(properties, false));
		tabContents.add(ITEMS.register(id + "_eave", () -> new BlockItem(roofEave.get(), itemProperties)));
		var roofEnd = BLOCKS.register(id + "_end", () -> new RoofEndBlock(properties, false));
		tabContents.add(ITEMS.register(id + "_end", () -> new BlockItem(roofEnd.get(), itemProperties)));
		var roofRidgeEnd = BLOCKS.register(id + "_ridge_end", () -> new RoofRidgeEndAsianBlock(properties, false));

		//TODO remove in the future:
		tabContents.add(ITEMS.register(id + "_ridge_end", () -> new BlockItem(roofRidgeEnd.get(), itemProperties)));

		var roofDeco = BLOCKS.register(id + "_deco", () -> new HorizontalShiftBlock(properties));
		tabContents.add(ITEMS.register(id + "_deco", () -> new BlockItem(roofDeco.get(), itemProperties)));
		var roofDecoOblique = BLOCKS.register(id + "_deco_oblique", () -> new HorizontalShiftBlock(properties));
		tabContents.add(ITEMS.register(id + "_deco_oblique", () -> new BlockItem(roofDecoOblique.get(), itemProperties)));
		var roofTip = BLOCKS.register(id + "_tip", () -> new RoofTipBlock(properties));
		tabContents.add(ITEMS.register(id + "_tip", () -> new BlockItem(roofTip.get(), itemProperties)));
	}

	private static void addPlant(
			String id,
			BlockBehaviour.Properties properties,
			Item.Properties itemProperties,
			Collection<RegistryObject<Item>> tabContents) {
		var isPath = id.contains(PATH_SUFFIX);
		if (id.contains(LEAVES_SUFFIX) || id.contains(BLOSSOM_SUFFIX)) {
			if (id.startsWith(FALLEN_LEAVES_PREFIX)) {
				var block = BLOCKS.register(id, () -> new FallenLeavesBlock(properties));
				tabContents.add(ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties)));
				return;
			}
			var block = BLOCKS.register(id, () -> {
				LeavesBlock $ = new LeavesBlock(properties);
				if (id.startsWith(PLANTABLE_PREFIX)) {
					XKDBlockSettings.builder().sustainsPlant().build().setTo($);
				}
				return $;
			});
			tabContents.add(ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties)));
		} else if (id.contains(SLAB_SUFFIX)) {
			var block = BLOCKS.register(id, () -> new PlantSlabBlock(properties, isPath, "dirt_slab"));
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
		if (id.equals(CUP_SPECIAL)) {
			var block = BLOCKS.register(id, () -> new SpecialCupBlock(properties));
			tabContents.add(ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties)));
		} else if (id.equals(REFRESHMENT_SPECIAL) || id.equals(FRUIT_PLATTER_SPECIAL)) {
			var block = BLOCKS.register(id, () -> new SpecialDessertBlock(properties));
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
			var shapes = Maps.toMap(Arrays.stream(Direction.values()).toList(), d -> ShapeFunction.fromVentFan().getShape(d));
			var block = BLOCKS.register(id, () -> new BasicFullDirectionBlock(properties, shapes));
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

	public static void addSpecialWallBlocks(RegisterEvent event) {
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
				var name = SPECIAL_WALL_PREFIX + registryName.toString().replace(':', '_');
				event.register(ForgeRegistries.Keys.BLOCKS, new ResourceLocation(XKDeco.ID, name), () -> new SpecialWallBlock(wall));
			}
		}
	}

	public static void addSpecialWallItems(RegisterEvent event) {
		for (var entry : ForgeRegistries.BLOCKS.getEntries()) {
			var block = entry.getValue();
			if (block instanceof SpecialWallBlock wall) {
				var registryName = entry.getKey().location();
				event.register(ForgeRegistries.Keys.ITEMS, registryName, () -> new SpecialWallItem(wall, XKDecoProperties.ITEM_STRUCTURE));
				TAB_STRUCTURE_CONTENTS.add(RegistryObject.create(registryName, ForgeRegistries.ITEMS));
			}
		}
	}

	public static void addSpecialWallBlockEntity(RegisterEvent event) {
		var blocks = ForgeRegistries.BLOCKS.getValues().stream().filter(SpecialWallBlock.class::isInstance).toArray(Block[]::new);
		var registryName = new ResourceLocation(XKDeco.ID, WALL_BLOCK_ENTITY);
		event.register(
				ForgeRegistries.Keys.BLOCK_ENTITY_TYPES,
				registryName,
				() -> BlockEntityType.Builder.of(WallBlockEntity::new, blocks).build(DSL.remainderType()));
	}

	public static void addSpecialWallTags(TagsUpdatedEvent event) {
		var registry = event.getRegistryAccess().registry(ForgeRegistries.BLOCKS.getRegistryKey()).orElseThrow(RuntimeException::new);
		registry.bindTags(registry.getTagNames().collect(Collectors.toMap(Function.identity(), tagKey -> {
			var tags = Lists.newArrayList(registry.getTagOrEmpty(tagKey));
			if (BlockTags.WALLS.equals(tagKey)) {
				for (var block : ForgeRegistries.BLOCKS.getValues()) {
					if (block instanceof SpecialWallBlock) {
						tags.add(ForgeRegistries.BLOCKS.getHolder(block).orElseThrow());
					}
				}
			}
			return tags;
		})));
		Blocks.rebuildCache();
	}

	@FunctionalInterface
	private interface ShapeFunction {
		VoxelShape getShape(Direction direction);

		static ShapeFunction fromBigTable() {
			return d -> Direction.Plane.HORIZONTAL.test(d) ? IsotropicHollowBlock.BIG_TABLE_SHAPE : Shapes.block();
		}

		static ShapeFunction fromLongStool() {
			return d -> switch (d) {
				case EAST, WEST -> Block.box(3, 0, 0, 13, 10, 16);
				case NORTH, SOUTH -> Block.box(0, 0, 3, 16, 10, 13);
				default -> Shapes.block();
			};
		}

		static ShapeFunction fromChair() {
			return d -> switch (d) {
				case EAST -> Shapes.or(Block.box(2, 0, 2, 14, 10, 14), Block.box(2, 10, 2, 4, 16, 14));
				case SOUTH -> Shapes.or(Block.box(2, 0, 2, 14, 10, 14), Block.box(2, 10, 2, 14, 16, 4));
				case WEST -> Shapes.or(Block.box(2, 0, 2, 14, 10, 14), Block.box(12, 10, 2, 14, 16, 14));
				case NORTH -> Shapes.or(Block.box(2, 0, 2, 14, 10, 14), Block.box(2, 10, 12, 14, 16, 14));
				default -> Shapes.block();
			};
		}

		static ShapeFunction fromShelf() {
			return d -> switch (d) {
				case EAST, WEST -> Shapes.or(
						Block.box(0, 0, 0, 16, 1, 16),
						Block.box(0, 15, 0, 16, 16, 16),
						Block.box(0, 1, 0, 16, 15, 1),
						Block.box(0, 1, 15, 16, 15, 16));
				case NORTH, SOUTH -> Shapes.or(
						Block.box(15, 0, 0, 16, 16, 16),
						Block.box(0, 0, 0, 1, 16, 16),
						Block.box(1, 15, 0, 15, 16, 16),
						Block.box(1, 0, 0, 15, 1, 16));
				default -> Shapes.block();
			};
		}

		static ShapeFunction fromMiniature() {
			return d -> switch (d) {
				case EAST, WEST -> Block.box(3, 0, 0, 13, 6, 16);
				case NORTH, SOUTH -> Block.box(0, 0, 3, 16, 6, 13);
				default -> Shapes.block();
			};
		}

		static ShapeFunction fromTeapot() {
			return d -> Direction.Plane.HORIZONTAL.test(d) ? Block.box(4, 0, 4, 12, 6, 12) : Shapes.block();
		}

		static ShapeFunction fromTeaWare() {
			return d -> switch (d) {
				case EAST, WEST -> Block.box(3, 0, 0, 13, 2, 16);
				case NORTH, SOUTH -> Block.box(0, 0, 3, 16, 2, 13);
				default -> Shapes.block();
			};
		}

		static ShapeFunction fromCarpet() {
			return d -> Direction.Plane.HORIZONTAL.test(d) ? Block.box(0, 0, 0, 16, 1, 16) : Shapes.block();
		}

		static ShapeFunction fromBoard() {
			return d -> Direction.Plane.HORIZONTAL.test(d) ? Block.box(1, 0, 1, 15, 1, 15) : Shapes.block();
		}

		static ShapeFunction fromPorcelain() {
			return d -> Block.box(2, 0, 2, 14, 16, 14);
		}

		static ShapeFunction fromPorcelainSmall() {
			return d -> Block.box(5, 0, 5, 11, 12, 11);
		}

		static ShapeFunction fromLantern() {
			return d -> Shapes.or(Block.box(2, 2, 2, 14, 14, 14), Block.box(5, 0, 5, 11, 16, 11));
		}

		static ShapeFunction fromFestivalLantern() {
			return d -> Shapes.or(Block.box(2, 2, 2, 14, 14, 14), Block.box(5, 0, 5, 11, 16, 11), Block.box(0, 3, 0, 16, 13, 16));
		}

		static ShapeFunction fromCandlestick() {
			return d -> Block.box(5, 0, 5, 11, 13, 11);
		}

		static ShapeFunction fromBigCandlestick() {
			return d -> Block.box(2, 0, 2, 14, 14, 14);
		}

		static ShapeFunction fromCoveredLamp() {
			return d -> Block.box(4, 0, 4, 12, 16, 12);
		}

		static ShapeFunction fromStoneLamp() {
			return d -> Block.box(3, 0, 3, 13, 16, 13);
		}

		static ShapeFunction fromWaterBowl() {
			return d -> Block.box(0, 0, 0, 16, 5, 16);
		}

		static ShapeFunction fromFishBowl() {
			return d -> Block.box(1, 0, 1, 15, 6, 15);
		}

		static ShapeFunction fromFishTank() {
			return d -> Shapes.join(Shapes.block(), Block.box(1, 1, 1, 15, 16, 15), BooleanOp.ONLY_FIRST);
		}

		static ShapeFunction fromWaterTank() {
			return d -> Shapes.join(Block.box(1, 0, 1, 15, 16, 15), Block.box(3, 3, 3, 13, 16, 13), BooleanOp.ONLY_FIRST);
		}

		static ShapeFunction fromOilLamp() {
			return d -> switch (d) {
				case SOUTH -> Block.box(5, 4, 0, 11, 12, 8);
				case EAST -> Block.box(0, 4, 5, 8, 12, 11);
				case NORTH -> Block.box(5, 4, 8, 11, 12, 16);
				case WEST -> Block.box(8, 4, 5, 16, 12, 11);
				case DOWN -> Block.box(5, 5, 5, 11, 16, 11);
				case UP -> Block.box(5, 0, 5, 11, 8, 11);
			};
		}

		static ShapeFunction fromEmptyCandlestick() {
			return d -> switch (d) {
				case SOUTH -> Block.box(5, 5, 0, 11, 16, 11);
				case EAST -> Block.box(0, 5, 5, 11, 16, 11);
				case NORTH -> Block.box(5, 5, 5, 11, 16, 16);
				case WEST -> Block.box(5, 5, 5, 16, 16, 11);
				default -> Block.box(5, 0, 5, 11, 16, 11);
			};
		}

		static ShapeFunction fromFactoryLamp() {
			return d -> switch (d) {
				case SOUTH -> Block.box(4, 4, 0, 12, 12, 8);
				case EAST -> Block.box(0, 4, 4, 8, 12, 12);
				case NORTH -> Block.box(4, 4, 8, 12, 12, 16);
				case WEST -> Block.box(8, 4, 4, 16, 12, 12);
				case DOWN -> Block.box(4, 8, 4, 12, 16, 12);
				case UP -> Block.box(4, 0, 4, 12, 8, 12);
			};
		}

		static ShapeFunction fromFan() {
			return d -> switch (d) {
				case EAST -> Block.box(1, 0, 1, 6, 15, 15);
				case SOUTH -> Block.box(1, 0, 1, 15, 15, 6);
				case WEST -> Block.box(10, 1, 1, 16, 15, 15);
				case NORTH -> Block.box(1, 1, 10, 15, 15, 16);
				case UP -> Block.box(1, 0, 1, 15, 6, 15);
				case DOWN -> Block.box(1, 10, 1, 15, 16, 15);
			};
		}

		static ShapeFunction fromScreen() {
			return d -> switch (d) {
				case SOUTH -> Block.box(0, 0, 0, 16, 16, 2);
				case WEST -> Block.box(14, 0, 0, 16, 16, 16);
				case NORTH -> Block.box(0, 0, 14, 16, 16, 16);
				default -> Block.box(0, 0, 0, 2, 16, 16);
			};
		}

		static ShapeFunction fromScreen2() {
			return d -> switch (d) {
				case SOUTH -> Block.box(0, 0, 2, 16, 16, 3);
				case WEST -> Block.box(13, 0, 0, 14, 16, 16);
				case NORTH -> Block.box(0, 0, 13, 16, 16, 14);
				default -> Block.box(2, 0, 0, 3, 16, 16);
			};
		}

		static ShapeFunction fromVentFan() {
			return d -> switch (d) {
				case SOUTH, NORTH -> Block.box(0, 0, 2, 16, 16, 14);
				case EAST, WEST -> Block.box(2, 0, 0, 14, 16, 16);
				default -> Block.box(0, 2, 0, 16, 14, 16);
			};
		}

		static ShapeFunction fromTechTable() {
			return d -> Shapes.or(Block.box(2, 0, 2, 14, 10, 14), Block.box(0, 10, 0, 16, 16, 16));
		}

		static ShapeFunction fromHologramBase() {
			return d -> switch (d) {
				case EAST -> Block.box(0, 1, 1, 2, 15, 15);
				case SOUTH -> Block.box(1, 1, 0, 15, 15, 2);
				case WEST -> Block.box(14, 1, 1, 16, 15, 15);
				case NORTH -> Block.box(1, 1, 14, 15, 15, 16);
				case UP -> Block.box(1, 0, 1, 15, 2, 15);
				case DOWN -> Block.box(1, 14, 1, 15, 16, 15);
			};
		}

		@Deprecated
		static ShapeFunction wip() {
			return fromChair();
		}
	}

	static {
		addCushionEntity();

		addIsotropic("black_tiles", BLOCK_STONE, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("black_tile_slab", BLOCK_STONE, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("black_tile_stairs", BLOCK_STONE, ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("cyan_tiles", BLOCK_STONE, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("cyan_tile_slab", BLOCK_STONE, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("cyan_tile_stairs", BLOCK_STONE, ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("yellow_tiles", BLOCK_STONE, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("yellow_tile_slab", BLOCK_STONE, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("yellow_tile_stairs", BLOCK_STONE, ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("blue_tiles", BLOCK_STONE, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("blue_tile_slab", BLOCK_STONE, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("blue_tile_stairs", BLOCK_STONE, ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("green_tiles", BLOCK_STONE, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("green_tile_slab", BLOCK_STONE, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("green_tile_stairs", BLOCK_STONE, ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("red_tiles", BLOCK_STONE, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("red_tile_slab", BLOCK_STONE, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("red_tile_stairs", BLOCK_STONE, ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("steel_tiles", BLOCK_IRON, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("steel_tile_slab", BLOCK_IRON, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("steel_tile_stairs", BLOCK_IRON, ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("copper_tiles", BLOCK_COPPER, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("copper_tile_slab", BLOCK_COPPER, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("copper_tile_stairs", BLOCK_COPPER, ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("glass_tiles", BLOCK_GLASS, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("glass_tile_slab", BLOCK_GLASS, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("glass_tile_stairs", BLOCK_GLASS, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("glass_trapdoor", BLOCK_GLASS, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("glass_door", BLOCK_GLASS, ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("mud_wall_block", BLOCK_MUD, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("mud_wall_slab", BLOCK_MUD, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("mud_wall_stairs", BLOCK_MUD, ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("framed_mud_wall_block", BLOCK_MUD, ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("lined_mud_wall_block", BLOCK_MUD, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("lined_mud_wall_slab", BLOCK_MUD, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("lined_mud_wall_stairs", BLOCK_MUD, ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("crossed_mud_wall_block", BLOCK_MUD, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("crossed_mud_wall_slab", BLOCK_MUD, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("crossed_mud_wall_stairs", BLOCK_MUD, ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("dirty_mud_wall_block", BLOCK_MUD, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("dirty_mud_wall_slab", BLOCK_MUD, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("dirty_mud_wall_stairs", BLOCK_MUD, ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("cyan_bricks", BLOCK_BRICK, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("cyan_brick_slab", BLOCK_BRICK, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("cyan_brick_stairs", BLOCK_BRICK, ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("black_bricks", BLOCK_BRICK, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("black_brick_slab", BLOCK_BRICK, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("black_brick_stairs", BLOCK_BRICK, ITEM_BASIC, TAB_BASIC_CONTENTS);

		addTreatedWood("varnished", BLOCK_WOOD, BLOCK_WOOD_FURNITURE);
		addTreatedWood("ebony", BLOCK_WOOD, BLOCK_WOOD_FURNITURE);
		addTreatedWood("mahogany", BLOCK_WOOD, BLOCK_WOOD_FURNITURE);

		addIsotropic("sandstone_pillar", BLOCK_SANDSTONE, ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("polished_sandstone", BLOCK_SANDSTONE, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("polished_sandstone_slab", BLOCK_SANDSTONE, ITEM_BASIC, TAB_BASIC_CONTENTS);
//		addIsotropic("polished_sandstone_stairs", BLOCK_SANDSTONE, ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("sandstone_bricks", BLOCK_SANDSTONE, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("sandstone_brick_slab", BLOCK_SANDSTONE, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("sandstone_brick_stairs", BLOCK_SANDSTONE, ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("sandstone_small_bricks", BLOCK_SANDSTONE, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("sandstone_small_brick_slab", BLOCK_SANDSTONE, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("sandstone_small_brick_stairs", BLOCK_SANDSTONE, ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("red_sandstone_pillar", BLOCK_SANDSTONE, ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("polished_red_sandstone", BLOCK_SANDSTONE, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("polished_red_sandstone_slab", BLOCK_SANDSTONE, ITEM_BASIC, TAB_BASIC_CONTENTS);
//		addIsotropic("polished_red_sandstone_stairs", BLOCK_SANDSTONE, ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("red_sandstone_bricks", BLOCK_SANDSTONE, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("red_sandstone_brick_slab", BLOCK_SANDSTONE, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("red_sandstone_brick_stairs", BLOCK_SANDSTONE, ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("red_sandstone_small_bricks", BLOCK_SANDSTONE, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("red_sandstone_small_brick_slab", BLOCK_SANDSTONE, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("red_sandstone_small_brick_stairs", BLOCK_SANDSTONE, ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("stone_brick_pillar", BLOCK_HARD_STONE, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("stone_brick_pavement", BLOCK_HARD_STONE, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("stone_brick_pavement_slab", BLOCK_HARD_STONE, ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("deepslate_pillar", BLOCK_HARD_STONE, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("deepslate_pavement", BLOCK_HARD_STONE, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("deepslate_pavement_slab", BLOCK_HARD_STONE, ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("mossy_deepslate_bricks", BLOCK_HARD_STONE, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("mossy_deepslate_brick_slab", BLOCK_HARD_STONE, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("mossy_deepslate_brick_stairs", BLOCK_HARD_STONE, ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("blackstone_pillar", BLOCK_HARD_STONE, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("blackstone_pavement", BLOCK_HARD_STONE, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("blackstone_pavement_slab", BLOCK_HARD_STONE, ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("gilded_blackstone_bricks", BLOCK_HARD_STONE, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("gilded_blackstone_brick_slab", BLOCK_HARD_STONE, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("gilded_blackstone_brick_stairs", BLOCK_HARD_STONE, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("gilded_blackstone_brick_pillar", BLOCK_HARD_STONE, ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("chiseled_gilded_blackstone", BLOCK_HARD_STONE, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("luxury_gilded_blackstone", BLOCK_HARD_STONE, ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("maya_stone", BLOCK_HARD_STONE, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("maya_stone_slab", BLOCK_HARD_STONE, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("maya_stone_stairs", BLOCK_HARD_STONE, ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("maya_stonebricks", BLOCK_HARD_STONE, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("maya_stonebrick_slab", BLOCK_HARD_STONE, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("maya_stonebrick_stairs", BLOCK_HARD_STONE, ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("maya_bricks", BLOCK_HARD_STONE, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("maya_brick_slab", BLOCK_HARD_STONE, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("maya_brick_stairs", BLOCK_HARD_STONE, ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("maya_polished_stonebricks", BLOCK_HARD_STONE, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("maya_polished_stonebrick_slab", BLOCK_HARD_STONE, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("maya_polished_stonebrick_stairs", BLOCK_HARD_STONE, ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("maya_mossy_stonebricks", BLOCK_HARD_STONE, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("maya_mossy_stonebrick_slab", BLOCK_HARD_STONE, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("maya_mossy_stonebrick_stairs", BLOCK_HARD_STONE, ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("maya_mossy_bricks", BLOCK_HARD_STONE, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("maya_mossy_brick_slab", BLOCK_HARD_STONE, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("maya_mossy_brick_stairs", BLOCK_HARD_STONE, ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("maya_chiseled_stonebricks", BLOCK_HARD_STONE, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("maya_cut_stonebricks", BLOCK_HARD_STONE, ITEM_BASIC, TAB_BASIC_CONTENTS);

		addBasic("maya_single_screw_thread_stone", s -> Shapes.block(), false, BLOCK_HARD_STONE, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("maya_double_screw_thread_stone", BLOCK_HARD_STONE, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("maya_quad_screw_thread_stone", BLOCK_HARD_STONE, ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("maya_pictogram_stone", BLOCK_HARD_STONE, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("maya_skull_stone", BLOCK_HARD_STONE, ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("maya_pillar", BLOCK_HARD_STONE, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("maya_mossy_pillar", BLOCK_HARD_STONE, ITEM_BASIC, TAB_BASIC_CONTENTS);

		addBasic("maya_crystal_skull", ShapeFunction.wip(), false, BLOCK_HARD_STONE, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);

		addIsotropic("aztec_stonebricks", BLOCK_HARD_STONE, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("aztec_stonebrick_slab", BLOCK_HARD_STONE, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("aztec_stonebrick_stairs", BLOCK_HARD_STONE, ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("aztec_mossy_stonebricks", BLOCK_HARD_STONE, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("aztec_mossy_stonebrick_slab", BLOCK_HARD_STONE, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("aztec_mossy_stonebrick_stairs", BLOCK_HARD_STONE, ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("aztec_sculpture_stone", BLOCK_HARD_STONE, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("aztec_chiseled_stonebricks", BLOCK_HARD_STONE, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("aztec_cut_stonebricks", BLOCK_HARD_STONE, ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("inca_stone", BLOCK_HARD_STONE, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("inca_stone_slab", BLOCK_HARD_STONE, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("inca_stone_stairs", BLOCK_HARD_STONE, ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("inca_stonebricks", BLOCK_HARD_STONE, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("inca_stonebrick_slab", BLOCK_HARD_STONE, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("inca_stonebrick_stairs", BLOCK_HARD_STONE, ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("inca_bricks", BLOCK_HARD_STONE, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("inca_brick_slab", BLOCK_HARD_STONE, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("inca_brick_stairs", BLOCK_HARD_STONE, ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("cut_obsidian", BLOCK_OBSIDIAN, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("cut_obsidian_slab", BLOCK_OBSIDIAN, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("cut_obsidian_pillar", BLOCK_OBSIDIAN, ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("cut_obsidian_bricks", BLOCK_OBSIDIAN, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("cut_obsidian_brick_slab", BLOCK_OBSIDIAN, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("cut_obsidian_brick_stairs", BLOCK_OBSIDIAN, ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("crying_obsidian_bricks", BLOCK_OBSIDIAN, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("crying_obsidian_brick_slab", BLOCK_OBSIDIAN, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("crying_obsidian_brick_stairs", BLOCK_OBSIDIAN, ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("cut_gold_block", BLOCK_GOLD, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("cut_gold_block_slab", BLOCK_GOLD, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("cut_gold_block_stairs", BLOCK_GOLD, ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("gold_bricks", BLOCK_GOLD, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("gold_brick_slab", BLOCK_GOLD, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("gold_brick_stairs", BLOCK_GOLD, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("gold_pillar", BLOCK_GOLD, ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("chiseled_gold_block", BLOCK_GOLD, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("painted_gold_block", BLOCK_GOLD, ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("bronze_block", BLOCK_BRONZE, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("smooth_bronze_block", BLOCK_BRONZE, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("inscription_bronze_block", BLOCK_BRONZE, ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("cut_bronze_block", BLOCK_BRONZE, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("cut_bronze_block_slab", BLOCK_BRONZE, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("cut_bronze_block_stairs", BLOCK_BRONZE, ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("chiseled_bronze_block", BLOCK_BRONZE, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addBasic("screw_thread_bronze_block", s -> Shapes.block(), false, BLOCK_BRONZE, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("bronze_pillar", BLOCK_BRONZE, ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("steel_block", BLOCK_HARD_IRON, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("smooth_steel_block", BLOCK_HARD_IRON, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("steel_pillar", BLOCK_HARD_IRON, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("steel_trapdoor", BLOCK_HARD_IRON, ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("steel_floor", BLOCK_HARD_IRON, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("steel_floor_slab", BLOCK_HARD_IRON, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("steel_floor_stairs", BLOCK_HARD_IRON, ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("chiseled_steel_block", BLOCK_HARD_IRON, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("hollow_steel_block", BLOCK_HOLLOW_IRON, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("hollow_steel_trapdoor", BLOCK_HOLLOW_IRON, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("framed_steel_block", BLOCK_HARD_IRON, ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("factory_block", BLOCK_IRON, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("factory_slab", BLOCK_IRON, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("factory_stairs", BLOCK_IRON, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("factory_trapdoor", BLOCK_IRON, ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("factory_block_rusting", BLOCK_IRON, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("factory_slab_rusting", BLOCK_IRON, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("factory_stairs_rusting", BLOCK_IRON, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("factory_trapdoor_rusting", BLOCK_IRON, ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("factory_block_rusted", BLOCK_IRON, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("factory_slab_rusted", BLOCK_IRON, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("factory_stairs_rusted", BLOCK_IRON, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("factory_trapdoor_rusted", BLOCK_IRON, ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("factory_danger", BLOCK_IRON, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("factory_danger_rusting", BLOCK_IRON, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("factory_danger_rusted", BLOCK_IRON, ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("factory_attention", BLOCK_IRON, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("factory_attention_rusting", BLOCK_IRON, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("factory_attention_rusted", BLOCK_IRON, ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("factory_electricity", BLOCK_IRON, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("factory_electricity_rusting", BLOCK_IRON, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("factory_electricity_rusted", BLOCK_IRON, ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("factory_toxic", BLOCK_IRON, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("factory_toxic_rusting", BLOCK_IRON, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("factory_toxic_rusted", BLOCK_IRON, ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("factory_radiation", BLOCK_IRON, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("factory_radiation_rusting", BLOCK_IRON, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("factory_radiation_rusted", BLOCK_IRON, ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("factory_biohazard", BLOCK_IRON, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("factory_biohazard_rusting", BLOCK_IRON, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("factory_biohazard_rusted", BLOCK_IRON, ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("factory_lamp_block", BLOCK_LIGHT, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("factory_lamp_slab", BLOCK_LIGHT, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("factory_lamp_stairs", BLOCK_LIGHT, ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("tech_lamp_block", BLOCK_LIGHT, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("tech_lamp_slab", BLOCK_LIGHT, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("tech_lamp_stairs", BLOCK_LIGHT, ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("translucent_lamp_block", BLOCK_LIGHT, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("translucent_lamp_slab", BLOCK_LIGHT, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("translucent_lamp_stairs", BLOCK_LIGHT, ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("steel_filings", BLOCK_SAND, ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("quartz_sand", BLOCK_SAND, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("toughened_sand", BLOCK_HARD_SAND, ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("quartz_glass", BLOCK_LIGHT, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("quartz_glass_slab", BLOCK_LIGHT, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("quartz_glass_stairs", BLOCK_LIGHT, ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic("toughened_glass", BLOCK_LIGHT, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("toughened_glass_slab", BLOCK_LIGHT, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic("toughened_glass_stairs", BLOCK_LIGHT, ITEM_BASIC, TAB_BASIC_CONTENTS);

		addRoof("black_roof", BLOCK_ROOF, ITEM_STRUCTURE, TAB_STRUCTURE_CONTENTS, true);
		addRoof("cyan_roof", BLOCK_ROOF, ITEM_STRUCTURE, TAB_STRUCTURE_CONTENTS, true);
		addRoof("yellow_roof", BLOCK_ROOF, ITEM_STRUCTURE, TAB_STRUCTURE_CONTENTS, true);
		addRoof("blue_roof", BLOCK_ROOF, ITEM_STRUCTURE, TAB_STRUCTURE_CONTENTS, false);
		addRoof("green_roof", BLOCK_ROOF, ITEM_STRUCTURE, TAB_STRUCTURE_CONTENTS, false);
		addRoof("red_roof", BLOCK_ROOF, ITEM_STRUCTURE, TAB_STRUCTURE_CONTENTS, false);

		addPlant("dirt_slab", BLOCK_DIRT, ITEM_NATURE, TAB_NATURE_CONTENTS);
		addPlant("dirt_path_slab", BLOCK_DIRT, ITEM_NATURE, TAB_NATURE_CONTENTS);
		addPlant("grass_block_slab", BLOCK_DIRT, ITEM_NATURE, TAB_NATURE_CONTENTS);
		addPlant("mycelium_slab", BLOCK_DIRT, ITEM_NATURE, TAB_NATURE_CONTENTS);
		addPlant("podzol_slab", BLOCK_DIRT, ITEM_NATURE, TAB_NATURE_CONTENTS);

		addIsotropic("netherrack_slab", BLOCK_NETHER_STONE, ITEM_NATURE, TAB_NATURE_CONTENTS);
		addIsotropic("crimson_nylium_slab", BLOCK_NETHER_STONE, ITEM_NATURE, TAB_NATURE_CONTENTS);
		addIsotropic("warped_nylium_slab", BLOCK_NETHER_STONE, ITEM_NATURE, TAB_NATURE_CONTENTS);
		addIsotropic("end_stone_slab", BLOCK_END_STONE, ITEM_NATURE, TAB_NATURE_CONTENTS);

		addIsotropic("dirt_cobblestone", BLOCK_SANDSTONE, ITEM_NATURE, TAB_NATURE_CONTENTS);
		addIsotropic("grass_cobblestone", BLOCK_SANDSTONE, ITEM_NATURE, TAB_NATURE_CONTENTS);
		addIsotropic("sandy_cobblestone", BLOCK_SANDSTONE, ITEM_NATURE, TAB_NATURE_CONTENTS);
		addIsotropic("snowy_cobblestone", BLOCK_SANDSTONE, ITEM_NATURE, TAB_NATURE_CONTENTS);

		addIsotropic("cobblestone_path", BLOCK_SANDSTONE, ITEM_NATURE, TAB_NATURE_CONTENTS);
		addIsotropic("dirt_cobblestone_path", BLOCK_SANDSTONE, ITEM_NATURE, TAB_NATURE_CONTENTS);
		addIsotropic("grass_cobblestone_path", BLOCK_SANDSTONE, ITEM_NATURE, TAB_NATURE_CONTENTS);
		addIsotropic("sandy_cobblestone_path", BLOCK_SANDSTONE, ITEM_NATURE, TAB_NATURE_CONTENTS);
		addIsotropic("snowy_cobblestone_path", BLOCK_SANDSTONE, ITEM_NATURE, TAB_NATURE_CONTENTS);

		addIsotropic("dirt_cobblestone_slab", BLOCK_SANDSTONE, ITEM_NATURE, TAB_NATURE_CONTENTS);
		addIsotropic("grass_cobblestone_slab", BLOCK_SANDSTONE, ITEM_NATURE, TAB_NATURE_CONTENTS);
		addIsotropic("sandy_cobblestone_slab", BLOCK_SANDSTONE, ITEM_NATURE, TAB_NATURE_CONTENTS);
		addIsotropic("snowy_cobblestone_slab", BLOCK_SANDSTONE, ITEM_NATURE, TAB_NATURE_CONTENTS);

		addIsotropic("cobblestone_path_slab", BLOCK_SANDSTONE, ITEM_NATURE, TAB_NATURE_CONTENTS);
		addIsotropic("dirt_cobblestone_path_slab", BLOCK_SANDSTONE, ITEM_NATURE, TAB_NATURE_CONTENTS);
		addIsotropic("grass_cobblestone_path_slab", BLOCK_SANDSTONE, ITEM_NATURE, TAB_NATURE_CONTENTS);
		addIsotropic("sandy_cobblestone_path_slab", BLOCK_SANDSTONE, ITEM_NATURE, TAB_NATURE_CONTENTS);
		addIsotropic("snowy_cobblestone_path_slab", BLOCK_SANDSTONE, ITEM_NATURE, TAB_NATURE_CONTENTS);

		addIsotropic("dirt_cobblestone_stairs", BLOCK_SANDSTONE, ITEM_NATURE, TAB_NATURE_CONTENTS);
		addIsotropic("grass_cobblestone_stairs", BLOCK_SANDSTONE, ITEM_NATURE, TAB_NATURE_CONTENTS);
		addIsotropic("sandy_cobblestone_stairs", BLOCK_SANDSTONE, ITEM_NATURE, TAB_NATURE_CONTENTS);
		addIsotropic("snowy_cobblestone_stairs", BLOCK_SANDSTONE, ITEM_NATURE, TAB_NATURE_CONTENTS);

		addIsotropic("cobblestone_path_stairs", BLOCK_SANDSTONE, ITEM_NATURE, TAB_NATURE_CONTENTS);
		addIsotropic("dirt_cobblestone_path_stairs", BLOCK_SANDSTONE, ITEM_NATURE, TAB_NATURE_CONTENTS);
		addIsotropic("grass_cobblestone_path_stairs", BLOCK_SANDSTONE, ITEM_NATURE, TAB_NATURE_CONTENTS);
		addIsotropic("sandy_cobblestone_path_stairs", BLOCK_SANDSTONE, ITEM_NATURE, TAB_NATURE_CONTENTS);
		addIsotropic("snowy_cobblestone_path_stairs", BLOCK_SANDSTONE, ITEM_NATURE, TAB_NATURE_CONTENTS);

		addPlant("ginkgo_leaves", BLOCK_LEAVES, ITEM_NATURE, TAB_NATURE_CONTENTS);
		addPlant("orange_maple_leaves", BLOCK_LEAVES, ITEM_NATURE, TAB_NATURE_CONTENTS);
		addPlant("red_maple_leaves", BLOCK_LEAVES, ITEM_NATURE, TAB_NATURE_CONTENTS);
		addPlant("peach_blossom", BLOCK_LEAVES, ITEM_NATURE, TAB_NATURE_CONTENTS);
		addPlant("peach_blossom_leaves", BLOCK_LEAVES, ITEM_NATURE, TAB_NATURE_CONTENTS);
		addPlant("cherry_blossom", BLOCK_LEAVES, ITEM_NATURE, TAB_NATURE_CONTENTS);
		addPlant("cherry_blossom_leaves", BLOCK_LEAVES, ITEM_NATURE, TAB_NATURE_CONTENTS);
		addPlant("white_cherry_blossom", BLOCK_LEAVES, ITEM_NATURE, TAB_NATURE_CONTENTS);
		addPlant("white_cherry_blossom_leaves", BLOCK_LEAVES, ITEM_NATURE, TAB_NATURE_CONTENTS);
		addPlant("plantable_leaves", BLOCK_LEAVES, ITEM_NATURE, TAB_NATURE_CONTENTS);
		addPlant("plantable_leaves_dark", BLOCK_LEAVES, ITEM_NATURE, TAB_NATURE_CONTENTS);
		addPlant("willow_leaves", BLOCK_LEAVES, ITEM_NATURE, TAB_NATURE_CONTENTS);
		addPlant("hanging_willow_leaves", BLOCK_LEAVES, ITEM_NATURE, TAB_NATURE_CONTENTS); //TODO proper class

		addBasic("miniature_tree", ShapeFunction.fromMiniature(), false, BLOCK_MINIATURE, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("miniature_cherry", ShapeFunction.fromMiniature(), false, BLOCK_MINIATURE, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("miniature_ginkgo", ShapeFunction.fromMiniature(), false, BLOCK_MINIATURE, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("miniature_maple", ShapeFunction.fromMiniature(), false, BLOCK_MINIATURE, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("miniature_bamboo", ShapeFunction.fromMiniature(), false, BLOCK_MINIATURE, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("miniature_coral", ShapeFunction.fromMiniature(), false, BLOCK_MINIATURE, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("miniature_red_coral", ShapeFunction.fromMiniature(), false, BLOCK_MINIATURE, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("miniature_mount", ShapeFunction.fromMiniature(), false, BLOCK_MINIATURE, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("miniature_succulents", ShapeFunction.fromMiniature(), false, BLOCK_MINIATURE, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);

		addBasic("teapot", ShapeFunction.fromTeapot(), true, BLOCK_MINIATURE, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addSpecial("cup", BLOCK_MINIATURE, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("tea_ware", ShapeFunction.fromTeaWare(), true, BLOCK_MINIATURE, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addSpecial("refreshments", BLOCK_DESSERT, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addSpecial("fruit_platter", BLOCK_DESSERT, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("calligraphy", ShapeFunction.fromCarpet(), true, BLOCK_CARPET, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("ink_painting", ShapeFunction.fromCarpet(), true, BLOCK_CARPET, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("weiqi_board", ShapeFunction.fromBoard(), true, BLOCK_BOARD, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("xiangqi_board", ShapeFunction.fromBoard(), true, BLOCK_BOARD, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);

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

		addBasic("white_porcelain", ShapeFunction.fromPorcelain(), false, BLOCK_PORCELAIN, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("white_porcelain_tall", ShapeFunction.fromPorcelain(), false, BLOCK_PORCELAIN, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic(
				"white_porcelain_small",
				ShapeFunction.fromPorcelainSmall(),
				false,
				BLOCK_PORCELAIN,
				ITEM_FURNITURE,
				TAB_FURNITURE_CONTENTS);
		addBasic("bluewhite_porcelain", ShapeFunction.fromPorcelain(), false, BLOCK_PORCELAIN, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("bluewhite_porcelain_tall", ShapeFunction.fromPorcelain(), false, BLOCK_PORCELAIN, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic(
				"bluewhite_porcelain_small",
				ShapeFunction.fromPorcelainSmall(),
				false,
				BLOCK_PORCELAIN,
				ITEM_FURNITURE,
				TAB_FURNITURE_CONTENTS);
		addBasic("celadon_porcelain", ShapeFunction.fromPorcelain(), false, BLOCK_PORCELAIN, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("celadon_porcelain_tall", ShapeFunction.fromPorcelain(), false, BLOCK_PORCELAIN, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic(
				"celadon_porcelain_small",
				ShapeFunction.fromPorcelainSmall(),
				false,
				BLOCK_PORCELAIN,
				ITEM_FURNITURE,
				TAB_FURNITURE_CONTENTS);

		addBasic("paper_lantern", ShapeFunction.fromLantern(), false, BLOCK_LANTERN, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("red_lantern", ShapeFunction.fromLantern(), false, BLOCK_LANTERN, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("festival_lantern", ShapeFunction.fromFestivalLantern(), false, BLOCK_LANTERN, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("oil_lamp", ShapeFunction.fromOilLamp(), false, BLOCK_METAL_LIGHT, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("candlestick", ShapeFunction.fromCandlestick(), false, BLOCK_METAL_LIGHT, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("big_candlestick", ShapeFunction.fromBigCandlestick(), false, BLOCK_METAL_LIGHT, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic(
				"empty_candlestick",
				ShapeFunction.fromEmptyCandlestick(),
				false,
				BLOCK_METAL_WITHOUT_LIGHT,
				ITEM_FURNITURE,
				TAB_FURNITURE_CONTENTS);
		addBasic("covered_lamp", ShapeFunction.fromCoveredLamp(), false, BLOCK_WOOD_LIGHT, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("roofed_lamp", ShapeFunction.fromBigCandlestick(), false, BLOCK_STONE_LIGHT, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("stone_lamp", ShapeFunction.fromStoneLamp(), false, BLOCK_STONE_LIGHT, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("deepslate_lamp", ShapeFunction.fromStoneLamp(), false, BLOCK_STONE_LIGHT, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("blackstone_lamp", ShapeFunction.fromStoneLamp(), false, BLOCK_STONE_LIGHT, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("fish_bowl", ShapeFunction.fromFishBowl(), false, BLOCK_STONE_NO_OCCLUSION, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("dark_fish_bowl", ShapeFunction.fromFishBowl(), false, BLOCK_STONE_NO_OCCLUSION, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic(
				"stone_water_bowl",
				ShapeFunction.fromWaterBowl(),
				false,
				BLOCK_STONE_NO_OCCLUSION,
				ITEM_FURNITURE,
				TAB_FURNITURE_CONTENTS);
		addBasic(
				"stone_water_tank",
				ShapeFunction.fromWaterTank(),
				false,
				BLOCK_STONE_NO_OCCLUSION,
				ITEM_FURNITURE,
				TAB_FURNITURE_CONTENTS);
		addBasic("fish_tank", ShapeFunction.fromFishTank(), false, BLOCK_GLASS_NO_OCCLUSION, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("empty_fish_tank", ShapeFunction.fromFishTank(), false, BLOCK_GLASS_NO_OCCLUSION, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);

		addBasic(
				"small_book_stack",
				s -> Block.box(2, 0, 2, 14, 8, 14),
				false,
				BLOCK_WOOD_FURNITURE,
				ITEM_FURNITURE,
				TAB_FURNITURE_CONTENTS);
		addBasic(
				"big_book_stack",
				s -> Block.box(0, 0, 0, 16, 10, 16),
				false,
				BLOCK_WOOD_FURNITURE,
				ITEM_FURNITURE,
				TAB_FURNITURE_CONTENTS);
		addBasic(
				"empty_bottle_stack",
				s -> Block.box(2, 0, 2, 14, 8, 14),
				false,
				BLOCK_GLASS_NO_OCCLUSION,
				ITEM_FURNITURE,
				TAB_FURNITURE_CONTENTS);
		addBasic(
				"bottle_stack",
				s -> Block.box(2, 0, 2, 14, 8, 14),
				false,
				BLOCK_GLASS_NO_OCCLUSION,
				ITEM_FURNITURE,
				TAB_FURNITURE_CONTENTS);
		addBasic("wood_globe", ShapeFunction.fromCoveredLamp(), false, BLOCK_WOOD_FURNITURE, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("globe", ShapeFunction.fromCoveredLamp(), false, BLOCK_WOOD_FURNITURE, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic(
				"solar_system_model",
				s -> Block.box(0, 0, 0, 16, 10, 16),
				false,
				BLOCK_WOOD_FURNITURE,
				ITEM_FURNITURE,
				TAB_FURNITURE_CONTENTS);
		addBasic("big_solar_system_model", s -> Shapes.block(), false, BLOCK_WOOD_FURNITURE, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("telescope", s -> Shapes.block(), false, BLOCK_WOOD_FURNITURE, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);

		addPlant("fallen_ginkgo_leaves", BLOCK_LEAVES, ITEM_NATURE, TAB_NATURE_CONTENTS);
		addPlant("fallen_orange_maple_leaves", BLOCK_LEAVES, ITEM_NATURE, TAB_NATURE_CONTENTS);
		addPlant("fallen_red_maple_leaves", BLOCK_LEAVES, ITEM_NATURE, TAB_NATURE_CONTENTS);
		addPlant("fallen_peach_blossom", BLOCK_LEAVES, ITEM_NATURE, TAB_NATURE_CONTENTS);
		addPlant("fallen_cherry_blossom", BLOCK_LEAVES, ITEM_NATURE, TAB_NATURE_CONTENTS);
		addPlant("fallen_white_cherry_blossom", BLOCK_LEAVES, ITEM_NATURE, TAB_NATURE_CONTENTS);

		addBasic("factory_lamp", ShapeFunction.fromFactoryLamp(), false, BLOCK_METAL_LIGHT, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic(
				"factory_lamp_broken",
				ShapeFunction.fromFactoryLamp(),
				false,
				BLOCK_METAL_WITHOUT_LIGHT,
				ITEM_FURNITURE,
				TAB_FURNITURE_CONTENTS);
		addBasic(
				"factory_warning_lamp",
				ShapeFunction.fromFactoryLamp(),
				false,
				BLOCK_METAL_HALF_LIGHT,
				ITEM_FURNITURE,
				TAB_FURNITURE_CONTENTS);

		addSpecial("factory_light_bar", BLOCK_METAL_LIGHT_NO_COLLISSION, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);

		addBasic(
				"factory_ceiling_lamp",
				s -> Block.box(0, 12, 0, 16, 16, 16),
				false,
				BLOCK_METAL_LIGHT,
				ITEM_FURNITURE,
				TAB_FURNITURE_CONTENTS);
		addBasic("factory_pendant", s -> Block.box(2, 4, 2, 14, 16, 14), false, BLOCK_METAL_LIGHT, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);

		addBasic("fan_blade", ShapeFunction.fromFan(), false, BLOCK_METAL_NO_OCCLUSION, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);

		addSpecial("factory_vent_fan", BLOCK_METAL_NO_OCCLUSION, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addSpecial("factory_vent_fan_big", BLOCK_METAL_NO_OCCLUSION, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);

		addBasic("steel_windmill", ShapeFunction.fromFan(), false, BLOCK_METAL_NO_OCCLUSION, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("iron_windmill", ShapeFunction.fromFan(), false, BLOCK_METAL_NO_OCCLUSION, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("wooden_windmill", ShapeFunction.fromFan(), false, BLOCK_WOOD_FURNITURE, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);

		addSpecial("mechanical_console", BLOCK_METAL_LIGHT, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);

		addBasic("mechanical_screen", ShapeFunction.fromScreen2(), false, BLOCK_METAL_NO_OCCLUSION, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("mechanical_chair", ShapeFunction.fromChair(), false, BLOCK_METAL_NO_OCCLUSION, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);

		addSpecial("tech_console", BLOCK_METAL_LIGHT, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("tech_screen", ShapeFunction.fromScreen2(), false, BLOCK_METAL_NO_OCCLUSION, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("tech_chair", ShapeFunction.fromChair(), false, BLOCK_METAL_NO_OCCLUSION, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);

		addBasic("screen_off", ShapeFunction.fromScreen(), false, BLOCK_METAL_NO_OCCLUSION, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("screen", ShapeFunction.fromScreen(), false, BLOCK_METAL_NO_OCCLUSION, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("screen_cube", ShapeFunction.fromScreen(), false, BLOCK_METAL_NO_OCCLUSION, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("screen_diagram", ShapeFunction.fromScreen(), false, BLOCK_METAL_NO_OCCLUSION, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("screen_dna", ShapeFunction.fromScreen(), false, BLOCK_METAL_NO_OCCLUSION, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("screen_list", ShapeFunction.fromScreen(), false, BLOCK_METAL_NO_OCCLUSION, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("screen_message", ShapeFunction.fromScreen(), false, BLOCK_METAL_NO_OCCLUSION, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("screen_threebodies", ShapeFunction.fromScreen(), false, BLOCK_METAL_NO_OCCLUSION, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("screen_transport", ShapeFunction.fromScreen(), false, BLOCK_METAL_NO_OCCLUSION, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);

		addBasic("tech_table", ShapeFunction.fromTechTable(), false, BLOCK_METAL_NO_OCCLUSION, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic(
				"tech_table_circle",
				ShapeFunction.fromTechTable(),
				false,
				BLOCK_METAL_NO_OCCLUSION,
				ITEM_FURNITURE,
				TAB_FURNITURE_CONTENTS);
		addBasic(
				"tech_table_bigcircle",
				ShapeFunction.fromTechTable(),
				false,
				BLOCK_METAL_NO_OCCLUSION,
				ITEM_FURNITURE,
				TAB_FURNITURE_CONTENTS);

		addBasic(
				"hologram_base",
				ShapeFunction.fromHologramBase(),
				false,
				BLOCK_METAL_NO_COLLISSION,
				ITEM_FURNITURE,
				TAB_FURNITURE_CONTENTS);
		addItem("hologram_planet", ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addItem("hologram_dna", ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addItem("hologram_pictures", ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addItem("hologram_message", ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addItem("hologram_xekr_logo", ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);

		addBasic("sign_entrance", ShapeFunction.fromScreen(), false, BLOCK_METAL_NO_OCCLUSION, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("sign_exit", ShapeFunction.fromScreen(), false, BLOCK_METAL_NO_OCCLUSION, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("sign_left", ShapeFunction.fromScreen(), false, BLOCK_METAL_NO_OCCLUSION, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic("sign_right", ShapeFunction.fromScreen(), false, BLOCK_METAL_NO_OCCLUSION, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);

		addBasic(
				"small_sign_left",
				ShapeFunction.fromScreen(),
				false,
				BLOCK_METAL_LIGHT_NO_COLLISSION,
				ITEM_FURNITURE,
				TAB_FURNITURE_CONTENTS);
		addBasic(
				"small_sign_right",
				ShapeFunction.fromScreen(),
				false,
				BLOCK_METAL_LIGHT_NO_COLLISSION,
				ITEM_FURNITURE,
				TAB_FURNITURE_CONTENTS);
		addBasic(
				"small_sign_ground",
				s -> Block.box(0, 0, 0, 16, 1, 16),
				false,
				BLOCK_METAL_LIGHT_NO_COLLISSION,
				ITEM_FURNITURE,
				TAB_FURNITURE_CONTENTS);

		addBlock("air_duct", () -> new AirDuctBlock(BLOCK_HOLLOW_IRON), ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBlock("air_duct_oblique", () -> new HorizontalShiftBlock(BLOCK_HOLLOW_IRON), ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
	}

	private static void addBlock(
			String id,
			Supplier<Block> blockSupplier,
			Item.Properties itemProp,
			Collection<RegistryObject<Item>> tabContents) {
		var block = BLOCKS.register(id, blockSupplier);
		tabContents.add(ITEMS.register(id, () -> new BlockItem(block.get(), itemProp)));
	}

	public static void addTreatedWood(String id, BlockBehaviour.Properties woodProp, BlockBehaviour.Properties furnitureProp) {
		addIsotropic(id + "_wood", woodProp, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic(id + "_log", woodProp, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic(id + "_log_slab", woodProp, ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic(id + "_planks", woodProp, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic(id + "_slab", woodProp, ITEM_BASIC, TAB_BASIC_CONTENTS);
		addIsotropic(id + "_stairs", woodProp, ITEM_BASIC, TAB_BASIC_CONTENTS);

		addIsotropic(id + "_table", furnitureProp, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addIsotropic(id + "_big_table", furnitureProp, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addIsotropic(id + "_tall_table", furnitureProp, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic(id + "_desk", ShapeFunction.fromBigTable(), false, furnitureProp, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic(id + "_stool", ShapeFunction.fromLongStool(), false, furnitureProp, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic(id + "_chair", ShapeFunction.fromChair(), false, furnitureProp, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic(id + "_empty_shelf", ShapeFunction.fromShelf(), false, furnitureProp, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic(id + "_shelf", ShapeFunction.fromShelf(), false, furnitureProp, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBasic(id + "_divided_shelf", ShapeFunction.fromShelf(), false, furnitureProp, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);

		addBlock(id + "_fence", () -> new FenceBlock(woodProp), ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addBlock(id + "_fence_gate", () -> new FenceGateBlock(woodProp, WoodType.OAK), ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addIsotropic(id + "_door", woodProp, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
		addIsotropic(id + "_trapdoor", woodProp, ITEM_FURNITURE, TAB_FURNITURE_CONTENTS);
	}
}
