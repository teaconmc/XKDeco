package org.teacon.xkdeco.init;

import com.google.common.collect.Maps;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.teacon.xkdeco.XKDeco;
import org.teacon.xkdeco.block.*;
import org.teacon.xkdeco.entity.CushionEntity;
import org.teacon.xkdeco.item.XKDecoCreativeModTab;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public final class XKDecoObjects {
    public static final CreativeModeTab TAB_BASIC = new XKDecoCreativeModTab(XKDeco.ID + "_basic", "black_tiles");
    public static final CreativeModeTab TAB_NATURE = new XKDecoCreativeModTab(XKDeco.ID + "_nature", "grass_block_slab");
    public static final CreativeModeTab TAB_FURNITURE = new XKDecoCreativeModTab(XKDeco.ID + "_furniture", "varnished_big_table");

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, XKDeco.ID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, XKDeco.ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, XKDeco.ID);

    private static final BlockBehaviour.Properties BLOCK_MUD = BlockBehaviour.Properties.of(Material.STONE).strength(1.5f, 3f);
    private static final BlockBehaviour.Properties BLOCK_SANDSTONE = BlockBehaviour.Properties.of(Material.STONE).strength(1.5f, 6f);
    private static final BlockBehaviour.Properties BLOCK_GLASS = BlockBehaviour.Properties.of(Material.GLASS).noOcclusion().isValidSpawn((s, g, p, e) -> false).isRedstoneConductor((s, g, p) -> false).isSuffocating((s, g, p) -> false).isViewBlocking((s, g, p) -> false).strength(1.5f, 3f);
    private static final BlockBehaviour.Properties BLOCK_IRON = BlockBehaviour.Properties.of(Material.METAL).strength(2f, 12f).requiresCorrectToolForDrops();
    private static final BlockBehaviour.Properties BLOCK_HARD_IRON = BlockBehaviour.Properties.of(Material.METAL).strength(3f, 12f).requiresCorrectToolForDrops();
    private static final BlockBehaviour.Properties BLOCK_HOLLOW_IRON = BlockBehaviour.Properties.of(Material.METAL).strength(3f, 12f).noOcclusion().requiresCorrectToolForDrops();
    private static final BlockBehaviour.Properties BLOCK_GOLD = BlockBehaviour.Properties.of(Material.METAL, MaterialColor.GOLD).strength(3f, 12f).requiresCorrectToolForDrops();
    private static final BlockBehaviour.Properties BLOCK_COPPER = BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_ORANGE).strength(2f, 12f).requiresCorrectToolForDrops();
    private static final BlockBehaviour.Properties BLOCK_BRONZE = BlockBehaviour.Properties.of(Material.METAL, MaterialColor.WARPED_NYLIUM).strength(3f, 12f).requiresCorrectToolForDrops();
    private static final BlockBehaviour.Properties BLOCK_WOOD = BlockBehaviour.Properties.of(Material.WOOD).strength(2f, 3f).requiresCorrectToolForDrops();
    private static final BlockBehaviour.Properties BLOCK_BRICK = BlockBehaviour.Properties.of(Material.STONE).strength(1.8f, 6f).requiresCorrectToolForDrops();
    private static final BlockBehaviour.Properties BLOCK_STONE = BlockBehaviour.Properties.of(Material.STONE).strength(1.8f, 9f).requiresCorrectToolForDrops();
    private static final BlockBehaviour.Properties BLOCK_HARD_STONE = BlockBehaviour.Properties.of(Material.STONE).strength(2f, 10f).requiresCorrectToolForDrops();
    private static final BlockBehaviour.Properties BLOCK_OBSIDIAN = BlockBehaviour.Properties.of(Material.PISTON, MaterialColor.COLOR_BLACK).strength(20f, 20f).requiresCorrectToolForDrops();
    private static final BlockBehaviour.Properties BLOCK_LIGHT = BlockBehaviour.Properties.of(Material.GLASS).noOcclusion().strength(2f, 10f).lightLevel(s -> 15);
    private static final BlockBehaviour.Properties BLOCK_SAND = BlockBehaviour.Properties.of(Material.SAND).strength(1f, 10f);
    private static final BlockBehaviour.Properties BLOCK_HARD_SAND = BlockBehaviour.Properties.of(Material.SAND).strength(1f, 12f);
    private static final BlockBehaviour.Properties BLOCK_DIRT = BlockBehaviour.Properties.of(Material.DIRT).strength(0.5f, 1f);
    private static final BlockBehaviour.Properties BLOCK_NETHER_STONE = BlockBehaviour.Properties.of(Material.STONE).strength(0.5f, 1f).requiresCorrectToolForDrops();
    private static final BlockBehaviour.Properties BLOCK_END_STONE = BlockBehaviour.Properties.of(Material.STONE).strength(2f, 9f).requiresCorrectToolForDrops();
    private static final BlockBehaviour.Properties BLOCK_LEAVES = BlockBehaviour.Properties.of(Material.LEAVES).strength(1f, 0.2f).noOcclusion();
    private static final BlockBehaviour.Properties BLOCK_WOOD_FURNITURE = BlockBehaviour.Properties.of(Material.WOOD).strength(2f, 2.5f).noOcclusion().requiresCorrectToolForDrops();
    private static final BlockBehaviour.Properties BLOCK_MINIATURE = BlockBehaviour.Properties.of(Material.STONE).strength(0.5f, 0.5f).noOcclusion().requiresCorrectToolForDrops();
    private static final BlockBehaviour.Properties BLOCK_DESSERT = BlockBehaviour.Properties.of(Material.CAKE).strength(0.5f, 0.5f);
    private static final BlockBehaviour.Properties BLOCK_CARPET = BlockBehaviour.Properties.of(Material.WOOL).strength(0.5f, 0.5f).noOcclusion();
    private static final BlockBehaviour.Properties BLOCK_BOARD = BlockBehaviour.Properties.of(Material.WOOD).strength(0.5f, 0.5f).noOcclusion();

    private static final Item.Properties ITEM_BASIC = new Item.Properties().tab(TAB_BASIC);
    private static final Item.Properties ITEM_NATURE = new Item.Properties().tab(TAB_NATURE);
    private static final Item.Properties ITEM_FURNITURE = new Item.Properties().tab(TAB_FURNITURE);

    public static final String CUSHION_ENTITY = "cushion";

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

    public static final String LOG_SUFFIX = "_log";
    public static final String WOOD_SUFFIX = "_wood";
    public static final String SLAB_SUFFIX = "_slab";
    public static final String PATH_SUFFIX = "_path";
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

    public static final String CUP_SPECIAL = "cup";
    public static final String REFRESHMENT_SPECIAL = "refreshments";
    public static final String FRUIT_PLATTER_SPECIAL = "fruit_platter";

    private static void addCushionEntity() {
        ENTITIES.register(CUSHION_ENTITY, () -> EntityType.Builder
                .<CushionEntity>of(CushionEntity::new, MobCategory.MISC).sized(1F / 256F, 1F / 256F)
                .setTrackingRange(256).build(new ResourceLocation(XKDeco.ID, CUSHION_ENTITY).toString()));
    }

    private static void addBasic(String id, ShapeFunction shapeFunction, boolean isSupportNeeded,
                                 BlockBehaviour.Properties properties, Item.Properties itemProperties) {
        var horizontalShapes = Maps.toMap(Direction.Plane.HORIZONTAL, shapeFunction::getShape);
        if (!isSupportNeeded && horizontalShapes.values().stream().anyMatch(s -> Block
                .isFaceFull(Shapes.join(Shapes.block(), s, BooleanOp.ONLY_FIRST), Direction.DOWN))) {
            var shapes = Maps.toMap(Arrays.stream(Direction.values()).toList(), shapeFunction::getShape);
            var block = BLOCKS.register(id, () -> new BasicFullDirectionBlock(properties, shapes));
            ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties));
        } else if (horizontalShapes.values().stream().allMatch(Block::isShapeFullBlock)) {
            var block = BLOCKS.register(id, () -> new BasicCubeBlock(properties, horizontalShapes));
            ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties));
        } else {
            var block = BLOCKS.register(id, () -> new BasicBlock(properties, isSupportNeeded, horizontalShapes));
            ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties));
        }
    }

    private static void addIsotropic(String id,
                                     BlockBehaviour.Properties properties, Item.Properties itemProperties) {
        var isGlass = id.contains(GLASS_SUFFIX) || id.contains(TRANSLUCENT_PREFIX) || id.contains(GLASS_PREFIX);
        if (id.contains(SLAB_SUFFIX)) {
            var block = BLOCKS.register(id, () -> new IsotropicSlabBlock(properties, isGlass));
            ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties));
        } else if (id.contains(STAIRS_SUFFIX)) {
            var block = BLOCKS.register(id, () -> new IsotropicStairBlock(properties, isGlass));
            ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties));
        } else if (id.contains(LOG_SUFFIX) || id.contains(WOOD_SUFFIX) || id.contains(PILLAR_SUFFIX)) {
            var block = BLOCKS.register(id, () -> new IsotropicPillarBlock(properties, isGlass));
            ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties));
        } else if (id.contains(LINED_PREFIX) || id.contains(LUXURY_PREFIX) || id.contains(PAINTED_PREFIX)
                || id.contains(CHISELED_PREFIX) || id.contains(DOUBLE_SCREW_PREFIX)) {
            var block = BLOCKS.register(id, () -> new IsotropicPillarBlock(properties, isGlass));
            ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties));
        } else if (id.contains(BIG_TABLE_SUFFIX) || id.contains(TALL_TABLE_SUFFIX)) {
            var block = BLOCKS.register(id, () -> new IsotropicHollowBlock(properties, IsotropicHollowBlock.BIG_TABLE_SHAPE));
            ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties));
        } else if (id.contains(TABLE_SUFFIX)) {
            var block = BLOCKS.register(id, () -> new IsotropicHollowBlock(properties, IsotropicHollowBlock.TABLE_SHAPE));
            ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties));
        } else if (id.contains(HOLLOW_PREFIX)) {
            var block = BLOCKS.register(id, () -> new IsotropicHollowBlock(properties, Shapes.block()));
            ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties));
        } else {
            var block = BLOCKS.register(id, () -> new IsotropicCubeBlock(properties, isGlass));
            ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties));
        }
    }

    private static void addPlant(String id,
                                 BlockBehaviour.Properties properties, Item.Properties itemProperties) {
        var isPath = id.contains(PATH_SUFFIX);
        if (id.contains(LEAVES_SUFFIX) || id.contains(BLOSSOM_SUFFIX)) {
            var block = BLOCKS.register(id, () -> new PlantLeavesBlock(properties));
            ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties));
        } else if (id.contains(SLAB_SUFFIX)) {
            var block = BLOCKS.register(id, () -> new PlantSlabBlock(properties, isPath, "dirt_slab"));
            ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties));
        } else {
            throw new IllegalArgumentException("Illegal id (" + id + ") for plant blocks");
        }
    }

    private static void addSpecial(String id,
                                   BlockBehaviour.Properties properties, Item.Properties itemProperties) {
        if (id.equals(CUP_SPECIAL)) {
            var block = BLOCKS.register(id, () -> new SpecialCupBlock(properties));
            ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties));
        } else if (id.equals(REFRESHMENT_SPECIAL) || id.equals(FRUIT_PLATTER_SPECIAL)) {
            var block = BLOCKS.register(id, () -> new SpecialDessertBlock(properties));
            ITEMS.register(id, () -> new BlockItem(block.get(), itemProperties));
        } else {
            throw new IllegalArgumentException("Illegal id (" + id + ") for special blocks");
        }
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
                        Block.box(0, 0, 0, 16, 1, 16), Block.box(0, 15, 0, 16, 16, 16),
                        Block.box(0, 1, 0, 16, 15, 1), Block.box(0, 1, 15, 16, 15, 16));
                case NORTH, SOUTH -> Shapes.or(
                        Block.box(15, 0, 0, 16, 16, 16), Block.box(0, 0, 0, 1, 16, 16),
                        Block.box(1, 15, 0, 15, 16, 16), Block.box(1, 0, 0, 15, 1, 16));
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
    }

    static {
        addCushionEntity();

        addIsotropic("black_tiles", BLOCK_STONE, ITEM_BASIC);
        addIsotropic("black_tile_slab", BLOCK_STONE, ITEM_BASIC);
        addIsotropic("black_tile_stairs", BLOCK_STONE, ITEM_BASIC);

        addIsotropic("cyan_tiles", BLOCK_STONE, ITEM_BASIC);
        addIsotropic("cyan_tile_slab", BLOCK_STONE, ITEM_BASIC);
        addIsotropic("cyan_tile_stairs", BLOCK_STONE, ITEM_BASIC);

        addIsotropic("yellow_tiles", BLOCK_STONE, ITEM_BASIC);
        addIsotropic("yellow_tile_slab", BLOCK_STONE, ITEM_BASIC);
        addIsotropic("yellow_tile_stairs", BLOCK_STONE, ITEM_BASIC);

        addIsotropic("blue_tiles", BLOCK_STONE, ITEM_BASIC);
        addIsotropic("blue_tile_slab", BLOCK_STONE, ITEM_BASIC);
        addIsotropic("blue_tile_stairs", BLOCK_STONE, ITEM_BASIC);

        addIsotropic("green_tiles", BLOCK_STONE, ITEM_BASIC);
        addIsotropic("green_tile_slab", BLOCK_STONE, ITEM_BASIC);
        addIsotropic("green_tile_stairs", BLOCK_STONE, ITEM_BASIC);

        addIsotropic("red_tiles", BLOCK_STONE, ITEM_BASIC);
        addIsotropic("red_tile_slab", BLOCK_STONE, ITEM_BASIC);
        addIsotropic("red_tile_stairs", BLOCK_STONE, ITEM_BASIC);

        addIsotropic("steel_tiles", BLOCK_IRON, ITEM_BASIC);
        addIsotropic("steel_tile_slab", BLOCK_IRON, ITEM_BASIC);
        addIsotropic("steel_tile_stairs", BLOCK_IRON, ITEM_BASIC);

        addIsotropic("copper_tiles", BLOCK_COPPER, ITEM_BASIC);
        addIsotropic("copper_tile_slab", BLOCK_COPPER, ITEM_BASIC);
        addIsotropic("copper_tile_stairs", BLOCK_COPPER, ITEM_BASIC);

        addIsotropic("glass_tiles", BLOCK_GLASS, ITEM_BASIC);
        addIsotropic("glass_tile_slab", BLOCK_GLASS, ITEM_BASIC);
        addIsotropic("glass_tile_stairs", BLOCK_GLASS, ITEM_BASIC);

        addIsotropic("mud_wall_block", BLOCK_MUD, ITEM_BASIC);
        addIsotropic("mud_wall_slab", BLOCK_MUD, ITEM_BASIC);
        addIsotropic("mud_wall_stairs", BLOCK_MUD, ITEM_BASIC);

        addIsotropic("framed_mud_wall_block", BLOCK_MUD, ITEM_BASIC);

        addIsotropic("lined_mud_wall_block", BLOCK_MUD, ITEM_BASIC);
        addIsotropic("lined_mud_wall_slab", BLOCK_MUD, ITEM_BASIC);
        addIsotropic("lined_mud_wall_stairs", BLOCK_MUD, ITEM_BASIC);

        addIsotropic("crossed_mud_wall_slab", BLOCK_MUD, ITEM_BASIC);
        addIsotropic("crossed_mud_wall_stairs", BLOCK_MUD, ITEM_BASIC);

        addIsotropic("dirty_mud_wall_block", BLOCK_MUD, ITEM_BASIC);
        addIsotropic("dirty_mud_wall_slab", BLOCK_MUD, ITEM_BASIC);
        addIsotropic("dirty_mud_wall_stairs", BLOCK_MUD, ITEM_BASIC);

        addIsotropic("cyan_bricks", BLOCK_BRICK, ITEM_BASIC);
        addIsotropic("cyan_brick_slab", BLOCK_BRICK, ITEM_BASIC);
        addIsotropic("cyan_brick_stairs", BLOCK_BRICK, ITEM_BASIC);

        addIsotropic("black_bricks", BLOCK_BRICK, ITEM_BASIC);
        addIsotropic("black_brick_slab", BLOCK_BRICK, ITEM_BASIC);
        addIsotropic("black_brick_stairs", BLOCK_BRICK, ITEM_BASIC);

        addIsotropic("varnished_wood", BLOCK_WOOD, ITEM_BASIC);
        addIsotropic("varnished_log", BLOCK_WOOD, ITEM_BASIC);
        addIsotropic("varnished_log_slab", BLOCK_WOOD, ITEM_BASIC);

        addIsotropic("ebony_wood", BLOCK_WOOD, ITEM_BASIC);
        addIsotropic("ebony_log", BLOCK_WOOD, ITEM_BASIC);
        addIsotropic("ebony_log_slab", BLOCK_WOOD, ITEM_BASIC);

        addIsotropic("mahogany_wood", BLOCK_WOOD, ITEM_BASIC);
        addIsotropic("mahogany_log", BLOCK_WOOD, ITEM_BASIC);
        addIsotropic("mahogany_log_slab", BLOCK_WOOD, ITEM_BASIC);

        addIsotropic("varnished_planks", BLOCK_WOOD, ITEM_BASIC);
        addIsotropic("varnished_slab", BLOCK_WOOD, ITEM_BASIC);
        addIsotropic("varnished_stairs", BLOCK_WOOD, ITEM_BASIC);

        addIsotropic("ebony_planks", BLOCK_WOOD, ITEM_BASIC);
        addIsotropic("ebony_slab", BLOCK_WOOD, ITEM_BASIC);
        addIsotropic("ebony_stairs", BLOCK_WOOD, ITEM_BASIC);

        addIsotropic("mahogany_planks", BLOCK_WOOD, ITEM_BASIC);
        addIsotropic("mahogany_slab", BLOCK_WOOD, ITEM_BASIC);
        addIsotropic("mahogany_stairs", BLOCK_WOOD, ITEM_BASIC);

        addIsotropic("sandstone_pillar", BLOCK_SANDSTONE, ITEM_BASIC);

        addIsotropic("polished_sandstone", BLOCK_SANDSTONE, ITEM_BASIC);
        addIsotropic("polished_sandstone_slab", BLOCK_SANDSTONE, ITEM_BASIC);

        addIsotropic("sandstone_bricks", BLOCK_SANDSTONE, ITEM_BASIC);
        addIsotropic("sandstone_brick_slab", BLOCK_SANDSTONE, ITEM_BASIC);
        addIsotropic("sandstone_brick_stairs", BLOCK_SANDSTONE, ITEM_BASIC);

        addIsotropic("sandstone_small_bricks", BLOCK_SANDSTONE, ITEM_BASIC);
        addIsotropic("sandstone_small_brick_slab", BLOCK_SANDSTONE, ITEM_BASIC);
        addIsotropic("sandstone_small_brick_stairs", BLOCK_SANDSTONE, ITEM_BASIC);

        addIsotropic("red_sandstone_pillar", BLOCK_SANDSTONE, ITEM_BASIC);

        addIsotropic("polished_red_sandstone", BLOCK_SANDSTONE, ITEM_BASIC);
        addIsotropic("polished_red_sandstone_slab", BLOCK_SANDSTONE, ITEM_BASIC);

        addIsotropic("red_sandstone_bricks", BLOCK_SANDSTONE, ITEM_BASIC);
        addIsotropic("red_sandstone_brick_slab", BLOCK_SANDSTONE, ITEM_BASIC);
        addIsotropic("red_sandstone_brick_stairs", BLOCK_SANDSTONE, ITEM_BASIC);

        addIsotropic("red_sandstone_small_bricks", BLOCK_SANDSTONE, ITEM_BASIC);
        addIsotropic("red_sandstone_small_brick_slab", BLOCK_SANDSTONE, ITEM_BASIC);
        addIsotropic("red_sandstone_small_brick_stairs", BLOCK_SANDSTONE, ITEM_BASIC);

        addIsotropic("stone_brick_pillar", BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("stone_brick_pavement", BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("stone_brick_pavement_slab", BLOCK_HARD_STONE, ITEM_BASIC);

        addIsotropic("deepslate_pillar", BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("deepslate_pavement", BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("deepslate_pavement_slab", BLOCK_HARD_STONE, ITEM_BASIC);

        addIsotropic("mossy_deepslate_bricks", BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("mossy_deepslate_brick_slab", BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("mossy_deepslate_brick_stairs", BLOCK_HARD_STONE, ITEM_BASIC);

        addIsotropic("blackstone_pillar", BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("blackstone_pavement", BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("blackstone_pavement_slab", BLOCK_HARD_STONE, ITEM_BASIC);

        addIsotropic("gilded_blackstone_bricks", BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("gilded_blackstone_brick_slab", BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("gilded_blackstone_brick_stairs", BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("gilded_blackstone_brick_pillar", BLOCK_HARD_STONE, ITEM_BASIC);

        addIsotropic("chiseled_gilded_blackstone", BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("luxury_gilded_blackstone", BLOCK_HARD_STONE, ITEM_BASIC);

        addIsotropic("maya_stone", BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("maya_stone_slab", BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("maya_stone_stairs", BLOCK_HARD_STONE, ITEM_BASIC);

        addIsotropic("maya_stonebricks", BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("maya_stonebrick_slab", BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("maya_stonebrick_stairs", BLOCK_HARD_STONE, ITEM_BASIC);

        addIsotropic("maya_bricks", BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("maya_brick_slab", BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("maya_brick_stairs", BLOCK_HARD_STONE, ITEM_BASIC);

        addIsotropic("maya_polished_stonebricks", BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("maya_polished_stonebrick_slab", BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("maya_polished_stonebrick_stairs", BLOCK_HARD_STONE, ITEM_BASIC);

        addIsotropic("maya_mossy_stonebricks", BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("maya_mossy_stonebrick_slab", BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("maya_mossy_stonebrick_stairs", BLOCK_HARD_STONE, ITEM_BASIC);

        addIsotropic("maya_mossy_bricks", BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("maya_mossy_brick_slab", BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("maya_mossy_brick_stairs", BLOCK_HARD_STONE, ITEM_BASIC);

        addIsotropic("maya_chiseled_stonebricks", BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("maya_cut_stonebricks", BLOCK_HARD_STONE, ITEM_BASIC);

        addBasic("maya_single_screw_thread_stone", s -> Shapes.block(), false, BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("maya_double_screw_thread_stone", BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("maya_quad_screw_thread_stone", BLOCK_HARD_STONE, ITEM_BASIC);

        addIsotropic("maya_pictogram_stone", BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("maya_skull_stone", BLOCK_HARD_STONE, ITEM_BASIC);

        addIsotropic("maya_pillar", BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("maya_mossy_pillar", BLOCK_HARD_STONE, ITEM_BASIC);

        addIsotropic("aztec_stonebricks", BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("aztec_stonebrick_slab", BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("aztec_stonebrick_stairs", BLOCK_HARD_STONE, ITEM_BASIC);

        addIsotropic("aztec_mossy_stonebricks", BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("aztec_mossy_stonebrick_slab", BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("aztec_mossy_stonebrick_stairs", BLOCK_HARD_STONE, ITEM_BASIC);

        addIsotropic("aztec_sculpture_stone", BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("aztec_chiseled_stonebricks", BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("aztec_cut_stonebricks", BLOCK_HARD_STONE, ITEM_BASIC);

        addIsotropic("inca_stone", BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("inca_stone_slab", BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("inca_stone_stairs", BLOCK_HARD_STONE, ITEM_BASIC);

        addIsotropic("inca_stonebricks", BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("inca_stonebrick_slab", BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("inca_stonebrick_stairs", BLOCK_HARD_STONE, ITEM_BASIC);

        addIsotropic("inca_bricks", BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("inca_brick_slab", BLOCK_HARD_STONE, ITEM_BASIC);
        addIsotropic("inca_brick_stairs", BLOCK_HARD_STONE, ITEM_BASIC);

        addIsotropic("cut_obsidian", BLOCK_OBSIDIAN, ITEM_BASIC);
        addIsotropic("cut_obsidian_slab", BLOCK_OBSIDIAN, ITEM_BASIC);
        addIsotropic("cut_obsidian_pillar", BLOCK_OBSIDIAN, ITEM_BASIC);

        addIsotropic("cut_obsidian_bricks", BLOCK_OBSIDIAN, ITEM_BASIC);
        addIsotropic("cut_obsidian_brick_slab", BLOCK_OBSIDIAN, ITEM_BASIC);
        addIsotropic("cut_obsidian_brick_stairs", BLOCK_OBSIDIAN, ITEM_BASIC);

        addIsotropic("crying_obsidian_bricks", BLOCK_OBSIDIAN, ITEM_BASIC);
        addIsotropic("crying_obsidian_brick_slab", BLOCK_OBSIDIAN, ITEM_BASIC);
        addIsotropic("crying_obsidian_brick_stairs", BLOCK_OBSIDIAN, ITEM_BASIC);

        addIsotropic("cut_gold_block", BLOCK_GOLD, ITEM_BASIC);
        addIsotropic("cut_gold_block_slab", BLOCK_GOLD, ITEM_BASIC);
        addIsotropic("cut_gold_block_stairs", BLOCK_GOLD, ITEM_BASIC);

        addIsotropic("gold_bricks", BLOCK_GOLD, ITEM_BASIC);
        addIsotropic("gold_brick_slab", BLOCK_GOLD, ITEM_BASIC);
        addIsotropic("gold_brick_stairs", BLOCK_GOLD, ITEM_BASIC);
        addIsotropic("gold_pillar", BLOCK_GOLD, ITEM_BASIC);

        addIsotropic("chiseled_gold_block", BLOCK_GOLD, ITEM_BASIC);
        addIsotropic("painted_gold_block", BLOCK_GOLD, ITEM_BASIC);

        addIsotropic("bronze_block", BLOCK_BRONZE, ITEM_BASIC);
        addIsotropic("smooth_bronze_block", BLOCK_BRONZE, ITEM_BASIC);
        addIsotropic("inscription_bronze_block", BLOCK_BRONZE, ITEM_BASIC);

        addIsotropic("cut_bronze_block", BLOCK_BRONZE, ITEM_BASIC);
        addIsotropic("cut_bronze_block_slab", BLOCK_BRONZE, ITEM_BASIC);
        addIsotropic("cut_bronze_block_stairs", BLOCK_BRONZE, ITEM_BASIC);

        addIsotropic("chiseled_bronze_block", BLOCK_BRONZE, ITEM_BASIC);
        addBasic("screw_thread_bronze_block", s -> Shapes.block(), false, BLOCK_BRONZE, ITEM_BASIC);
        addIsotropic("bronze_pillar", BLOCK_BRONZE, ITEM_BASIC);

        addIsotropic("steel_block", BLOCK_HARD_IRON, ITEM_BASIC);
        addIsotropic("smooth_steel_block", BLOCK_HARD_IRON, ITEM_BASIC);
        addIsotropic("steel_pillar", BLOCK_HARD_IRON, ITEM_BASIC);

        addIsotropic("steel_floor", BLOCK_HARD_IRON, ITEM_BASIC);
        addIsotropic("steel_floor_slab", BLOCK_HARD_IRON, ITEM_BASIC);
        addIsotropic("steel_floor_stairs", BLOCK_HARD_IRON, ITEM_BASIC);

        addIsotropic("chiseled_steel_block", BLOCK_HARD_IRON, ITEM_BASIC);
        addIsotropic("hollow_steel_block", BLOCK_HOLLOW_IRON, ITEM_BASIC);
        addIsotropic("framed_steel_block", BLOCK_HARD_IRON, ITEM_BASIC);

        addIsotropic("factory_block", BLOCK_IRON, ITEM_BASIC);
        addIsotropic("factory_slab", BLOCK_IRON, ITEM_BASIC);
        addIsotropic("factory_stairs", BLOCK_IRON, ITEM_BASIC);

        addIsotropic("factory_block_rusting", BLOCK_IRON, ITEM_BASIC);
        addIsotropic("factory_slab_rusting", BLOCK_IRON, ITEM_BASIC);
        addIsotropic("factory_stairs_rusting", BLOCK_IRON, ITEM_BASIC);

        addIsotropic("factory_block_rusted", BLOCK_IRON, ITEM_BASIC);
        addIsotropic("factory_slab_rusted", BLOCK_IRON, ITEM_BASIC);
        addIsotropic("factory_stairs_rusted", BLOCK_IRON, ITEM_BASIC);

        addIsotropic("factory_danger", BLOCK_IRON, ITEM_BASIC);
        addIsotropic("factory_danger_rusting", BLOCK_IRON, ITEM_BASIC);
        addIsotropic("factory_danger_rusted", BLOCK_IRON, ITEM_BASIC);

        addIsotropic("factory_attention", BLOCK_IRON, ITEM_BASIC);
        addIsotropic("factory_attention_rusting", BLOCK_IRON, ITEM_BASIC);
        addIsotropic("factory_attention_rusted", BLOCK_IRON, ITEM_BASIC);

        addIsotropic("factory_electricity", BLOCK_IRON, ITEM_BASIC);
        addIsotropic("factory_electricity_rusting", BLOCK_IRON, ITEM_BASIC);
        addIsotropic("factory_electricity_rusted", BLOCK_IRON, ITEM_BASIC);

        addIsotropic("factory_toxic", BLOCK_IRON, ITEM_BASIC);
        addIsotropic("factory_toxic_rusting", BLOCK_IRON, ITEM_BASIC);
        addIsotropic("factory_toxic_rusted", BLOCK_IRON, ITEM_BASIC);

        addIsotropic("factory_radiation", BLOCK_IRON, ITEM_BASIC);
        addIsotropic("factory_radiation_rusting", BLOCK_IRON, ITEM_BASIC);
        addIsotropic("factory_radiation_rusted", BLOCK_IRON, ITEM_BASIC);

        addIsotropic("factory_biohazard", BLOCK_IRON, ITEM_BASIC);
        addIsotropic("factory_biohazard_rusting", BLOCK_IRON, ITEM_BASIC);
        addIsotropic("factory_biohazard_rusted", BLOCK_IRON, ITEM_BASIC);

        addIsotropic("factory_lamp_block", BLOCK_LIGHT, ITEM_BASIC);
        addIsotropic("factory_lamp_slab", BLOCK_LIGHT, ITEM_BASIC);
        addIsotropic("factory_lamp_stairs", BLOCK_LIGHT, ITEM_BASIC);

        addIsotropic("tech_lamp_block", BLOCK_LIGHT, ITEM_BASIC);
        addIsotropic("tech_lamp_slab", BLOCK_LIGHT, ITEM_BASIC);
        addIsotropic("tech_lamp_stairs", BLOCK_LIGHT, ITEM_BASIC);

        addIsotropic("translucent_lamp_block", BLOCK_LIGHT, ITEM_BASIC);
        addIsotropic("translucent_lamp_slab", BLOCK_LIGHT, ITEM_BASIC);
        addIsotropic("translucent_lamp_stairs", BLOCK_LIGHT, ITEM_BASIC);

        addIsotropic("steel_filings", BLOCK_SAND, ITEM_BASIC);

        addIsotropic("quartz_sand", BLOCK_SAND, ITEM_BASIC);
        addIsotropic("toughened_sand", BLOCK_HARD_SAND, ITEM_BASIC);

        addIsotropic("quartz_glass", BLOCK_LIGHT, ITEM_BASIC);
        addIsotropic("quartz_glass_slab", BLOCK_LIGHT, ITEM_BASIC);
        addIsotropic("quartz_glass_stairs", BLOCK_LIGHT, ITEM_BASIC);

        addIsotropic("toughened_glass", BLOCK_LIGHT, ITEM_BASIC);
        addIsotropic("toughened_glass_slab", BLOCK_LIGHT, ITEM_BASIC);
        addIsotropic("toughened_glass_stairs", BLOCK_LIGHT, ITEM_BASIC);

        addPlant("dirt_slab", BLOCK_DIRT, ITEM_NATURE);
        addPlant("dirt_path_slab", BLOCK_DIRT, ITEM_NATURE);
        addPlant("grass_block_slab", BLOCK_DIRT, ITEM_NATURE);
        addPlant("mycelium_slab", BLOCK_DIRT, ITEM_NATURE);
        addPlant("podzol_slab", BLOCK_DIRT, ITEM_NATURE);

        addIsotropic("netherrack_slab", BLOCK_NETHER_STONE, ITEM_NATURE);
        addIsotropic("crimson_nylium_slab", BLOCK_NETHER_STONE, ITEM_NATURE);
        addIsotropic("warped_nylium_slab", BLOCK_NETHER_STONE, ITEM_NATURE);
        addIsotropic("end_stone_slab", BLOCK_END_STONE, ITEM_NATURE);

        addIsotropic("dirt_cobblestone", BLOCK_SANDSTONE, ITEM_NATURE);
        addIsotropic("grass_cobblestone", BLOCK_SANDSTONE, ITEM_NATURE);
        addIsotropic("sandy_cobblestone", BLOCK_SANDSTONE, ITEM_NATURE);
        addIsotropic("snowy_cobblestone", BLOCK_SANDSTONE, ITEM_NATURE);

        addIsotropic("cobblestone_path", BLOCK_SANDSTONE, ITEM_NATURE);
        addIsotropic("dirt_cobblestone_path", BLOCK_SANDSTONE, ITEM_NATURE);
        addIsotropic("grass_cobblestone_path", BLOCK_SANDSTONE, ITEM_NATURE);
        addIsotropic("sandy_cobblestone_path", BLOCK_SANDSTONE, ITEM_NATURE);
        addIsotropic("snowy_cobblestone_path", BLOCK_SANDSTONE, ITEM_NATURE);

        addIsotropic("dirt_cobblestone_slab", BLOCK_SANDSTONE, ITEM_NATURE);
        addIsotropic("grass_cobblestone_slab", BLOCK_SANDSTONE, ITEM_NATURE);
        addIsotropic("sandy_cobblestone_slab", BLOCK_SANDSTONE, ITEM_NATURE);
        addIsotropic("snowy_cobblestone_slab", BLOCK_SANDSTONE, ITEM_NATURE);

        addIsotropic("cobblestone_path_slab", BLOCK_SANDSTONE, ITEM_NATURE);
        addIsotropic("dirt_cobblestone_path_slab", BLOCK_SANDSTONE, ITEM_NATURE);
        addIsotropic("grass_cobblestone_path_slab", BLOCK_SANDSTONE, ITEM_NATURE);
        addIsotropic("sandy_cobblestone_path_slab", BLOCK_SANDSTONE, ITEM_NATURE);
        addIsotropic("snowy_cobblestone_path_slab", BLOCK_SANDSTONE, ITEM_NATURE);

        addIsotropic("dirt_cobblestone_stairs", BLOCK_SANDSTONE, ITEM_NATURE);
        addIsotropic("grass_cobblestone_stairs", BLOCK_SANDSTONE, ITEM_NATURE);
        addIsotropic("sandy_cobblestone_stairs", BLOCK_SANDSTONE, ITEM_NATURE);
        addIsotropic("snowy_cobblestone_stairs", BLOCK_SANDSTONE, ITEM_NATURE);

        addIsotropic("cobblestone_path_stairs", BLOCK_SANDSTONE, ITEM_NATURE);
        addIsotropic("dirt_cobblestone_path_stairs", BLOCK_SANDSTONE, ITEM_NATURE);
        addIsotropic("grass_cobblestone_path_stairs", BLOCK_SANDSTONE, ITEM_NATURE);
        addIsotropic("sandy_cobblestone_path_stairs", BLOCK_SANDSTONE, ITEM_NATURE);
        addIsotropic("snowy_cobblestone_path_stairs", BLOCK_SANDSTONE, ITEM_NATURE);

        addPlant("ginkgo_leaves", BLOCK_LEAVES, ITEM_NATURE);
        addPlant("orange_maple_leaves", BLOCK_LEAVES, ITEM_NATURE);
        addPlant("red_maple_leaves", BLOCK_LEAVES, ITEM_NATURE);
        addPlant("peach_blossom", BLOCK_LEAVES, ITEM_NATURE);
        addPlant("peach_blossom_leaves", BLOCK_LEAVES, ITEM_NATURE);
        addPlant("cherry_blossom", BLOCK_LEAVES, ITEM_NATURE);
        addPlant("cherry_blossom_leaves", BLOCK_LEAVES, ITEM_NATURE);
        addPlant("white_cherry_blossom", BLOCK_LEAVES, ITEM_NATURE);
        addPlant("white_cherry_blossom_leaves", BLOCK_LEAVES, ITEM_NATURE);
        addPlant("plantable_leaves", BLOCK_LEAVES, ITEM_NATURE);
        addPlant("plantable_leaves_dark", BLOCK_LEAVES, ITEM_NATURE);
        addPlant("willow_leaves", BLOCK_LEAVES, ITEM_NATURE);

        addIsotropic("varnished_table", BLOCK_WOOD_FURNITURE, ITEM_FURNITURE);
        addIsotropic("varnished_big_table", BLOCK_WOOD_FURNITURE, ITEM_FURNITURE);
        addIsotropic("varnished_tall_table", BLOCK_WOOD_FURNITURE, ITEM_FURNITURE);
        addBasic("varnished_desk", ShapeFunction.fromBigTable(), false, BLOCK_WOOD_FURNITURE, ITEM_FURNITURE);
        addBasic("varnished_stool", ShapeFunction.fromLongStool(), false, BLOCK_WOOD_FURNITURE, ITEM_FURNITURE);
        addBasic("varnished_chair", ShapeFunction.fromChair(), false, BLOCK_WOOD_FURNITURE, ITEM_FURNITURE);
        addBasic("varnished_empty_shelf", ShapeFunction.fromShelf(), false, BLOCK_WOOD_FURNITURE, ITEM_FURNITURE);
        addBasic("varnished_shelf", ShapeFunction.fromShelf(), false, BLOCK_WOOD_FURNITURE, ITEM_FURNITURE);
        addBasic("varnished_divided_shelf", ShapeFunction.fromShelf(), false, BLOCK_WOOD_FURNITURE, ITEM_FURNITURE);

        addIsotropic("ebony_table", BLOCK_WOOD_FURNITURE, ITEM_FURNITURE);
        addIsotropic("ebony_big_table", BLOCK_WOOD_FURNITURE, ITEM_FURNITURE);
        addIsotropic("ebony_tall_table", BLOCK_WOOD_FURNITURE, ITEM_FURNITURE);
        addBasic("ebony_desk", ShapeFunction.fromBigTable(), false, BLOCK_WOOD_FURNITURE, ITEM_FURNITURE);
        addBasic("ebony_stool", ShapeFunction.fromLongStool(), false, BLOCK_WOOD_FURNITURE, ITEM_FURNITURE);
        addBasic("ebony_chair", ShapeFunction.fromChair(), false, BLOCK_WOOD_FURNITURE, ITEM_FURNITURE);
        addBasic("ebony_empty_shelf", ShapeFunction.fromShelf(), false, BLOCK_WOOD_FURNITURE, ITEM_FURNITURE);
        addBasic("ebony_shelf", ShapeFunction.fromShelf(), false, BLOCK_WOOD_FURNITURE, ITEM_FURNITURE);
        addBasic("ebony_divided_shelf", ShapeFunction.fromShelf(), false, BLOCK_WOOD_FURNITURE, ITEM_FURNITURE);

        addIsotropic("mahogany_table", BLOCK_WOOD_FURNITURE, ITEM_FURNITURE);
        addIsotropic("mahogany_big_table", BLOCK_WOOD_FURNITURE, ITEM_FURNITURE);
        addIsotropic("mahogany_tall_table", BLOCK_WOOD_FURNITURE, ITEM_FURNITURE);
        addBasic("mahogany_desk", ShapeFunction.fromBigTable(), false, BLOCK_WOOD_FURNITURE, ITEM_FURNITURE);
        addBasic("mahogany_stool", ShapeFunction.fromLongStool(), false, BLOCK_WOOD_FURNITURE, ITEM_FURNITURE);
        addBasic("mahogany_chair", ShapeFunction.fromChair(), false, BLOCK_WOOD_FURNITURE, ITEM_FURNITURE);
        addBasic("mahogany_empty_shelf", ShapeFunction.fromShelf(), false, BLOCK_WOOD_FURNITURE, ITEM_FURNITURE);
        addBasic("mahogany_shelf", ShapeFunction.fromShelf(), false, BLOCK_WOOD_FURNITURE, ITEM_FURNITURE);
        addBasic("mahogany_divided_shelf", ShapeFunction.fromShelf(), false, BLOCK_WOOD_FURNITURE, ITEM_FURNITURE);

        addBasic("miniature_tree", ShapeFunction.fromMiniature(), false, BLOCK_MINIATURE, ITEM_FURNITURE);
        addBasic("miniature_cherry", ShapeFunction.fromMiniature(), false, BLOCK_MINIATURE, ITEM_FURNITURE);
        addBasic("miniature_ginkgo", ShapeFunction.fromMiniature(), false, BLOCK_MINIATURE, ITEM_FURNITURE);
        addBasic("miniature_maple", ShapeFunction.fromMiniature(), false, BLOCK_MINIATURE, ITEM_FURNITURE);
        addBasic("miniature_bamboo", ShapeFunction.fromMiniature(), false, BLOCK_MINIATURE, ITEM_FURNITURE);
        addBasic("miniature_coral", ShapeFunction.fromMiniature(), false, BLOCK_MINIATURE, ITEM_FURNITURE);
        addBasic("miniature_red_coral", ShapeFunction.fromMiniature(), false, BLOCK_MINIATURE, ITEM_FURNITURE);
        addBasic("miniature_mount", ShapeFunction.fromMiniature(), false, BLOCK_MINIATURE, ITEM_FURNITURE);
        addBasic("miniature_succulents", ShapeFunction.fromMiniature(), false, BLOCK_MINIATURE, ITEM_FURNITURE);

        addBasic("teapot", ShapeFunction.fromTeapot(), true, BLOCK_MINIATURE, ITEM_FURNITURE);
        addSpecial("cup", BLOCK_MINIATURE, ITEM_FURNITURE);
        addBasic("tea_ware", ShapeFunction.fromTeaWare(), true, BLOCK_MINIATURE, ITEM_FURNITURE);
        addSpecial("refreshments", BLOCK_DESSERT, ITEM_FURNITURE);
        addSpecial("fruit_platter", BLOCK_DESSERT, ITEM_FURNITURE);
        addBasic("calligraphy", ShapeFunction.fromCarpet(), true, BLOCK_CARPET, ITEM_FURNITURE);
        addBasic("ink_painting", ShapeFunction.fromCarpet(), true, BLOCK_CARPET, ITEM_FURNITURE);
        addBasic("weiqi_board", ShapeFunction.fromBoard(), true, BLOCK_BOARD, ITEM_FURNITURE);
        addBasic("xiangqi_board", ShapeFunction.fromBoard(), true, BLOCK_BOARD, ITEM_FURNITURE);
    }
}
