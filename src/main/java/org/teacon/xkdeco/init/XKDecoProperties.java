package org.teacon.xkdeco.init;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.Collection;

public final class XKDecoProperties {
    public static final Item.Properties ITEM_BASIC = new Item.Properties();
    public static final Item.Properties ITEM_STRUCTURE = new Item.Properties();
    public static final Item.Properties ITEM_NATURE = new Item.Properties();
    public static final Item.Properties ITEM_FURNITURE = new Item.Properties();
    public static final Item.Properties ITEM_FUNCTIONAL = new Item.Properties();

    public static final Collection<RegistryObject<Item>> TAB_BASIC_CONTENTS = new ArrayList<>();
    public static final Collection<RegistryObject<Item>> TAB_STRUCTURE_CONTENTS = new ArrayList<>();
    public static final Collection<RegistryObject<Item>> TAB_NATURE_CONTENTS = new ArrayList<>();
    public static final Collection<RegistryObject<Item>> TAB_FURNITURE_CONTENTS = new ArrayList<>();
    public static final Collection<RegistryObject<Item>> TAB_FUNCTIONAL_CONTENTS = new ArrayList<>();

    public static final BlockBehaviour.Properties BLOCK_MUD = BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(1.5f, 3f);
    public static final BlockBehaviour.Properties BLOCK_SANDSTONE = BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(1.5f, 6f);
    public static final BlockBehaviour.Properties BLOCK_GLASS = BlockBehaviour.Properties.of().mapColor(MapColor.NONE).noOcclusion().isValidSpawn((s, g, p, e) -> false).isRedstoneConductor((s, g, p) -> false).isSuffocating((s, g, p) -> false).isViewBlocking((s, g, p) -> false).strength(1.5f, 3f);
    public static final BlockBehaviour.Properties BLOCK_IRON = BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(2f, 12f).requiresCorrectToolForDrops();
    public static final BlockBehaviour.Properties BLOCK_HARD_IRON = BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(3f, 12f).requiresCorrectToolForDrops();
    public static final BlockBehaviour.Properties BLOCK_HOLLOW_IRON = BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(3f, 12f).noOcclusion().requiresCorrectToolForDrops();
    public static final BlockBehaviour.Properties BLOCK_GOLD = BlockBehaviour.Properties.of().mapColor(MapColor.GOLD).strength(3f, 12f).requiresCorrectToolForDrops();
    public static final BlockBehaviour.Properties BLOCK_COPPER = BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).strength(2f, 12f).requiresCorrectToolForDrops();
    public static final BlockBehaviour.Properties BLOCK_BRONZE = BlockBehaviour.Properties.of().mapColor(MapColor.WARPED_NYLIUM).strength(3f, 12f).requiresCorrectToolForDrops();
    public static final BlockBehaviour.Properties BLOCK_WOOD = BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).ignitedByLava().strength(2f, 3f).requiresCorrectToolForDrops();
    public static final BlockBehaviour.Properties BLOCK_BRICK = BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(1.8f, 6f).requiresCorrectToolForDrops();
    public static final BlockBehaviour.Properties BLOCK_STONE = BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(1.8f, 9f).requiresCorrectToolForDrops();
    public static final BlockBehaviour.Properties BLOCK_HARD_STONE = BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(2f, 10f).requiresCorrectToolForDrops();
    public static final BlockBehaviour.Properties BLOCK_OBSIDIAN = BlockBehaviour.Properties.of().pushReaction(PushReaction.BLOCK).mapColor(MapColor.COLOR_BLACK).strength(20f, 20f).requiresCorrectToolForDrops();
    public static final BlockBehaviour.Properties BLOCK_LIGHT = BlockBehaviour.Properties.of().mapColor(MapColor.NONE).noOcclusion().strength(2f, 10f).lightLevel(s -> 15);
    public static final BlockBehaviour.Properties BLOCK_SAND = BlockBehaviour.Properties.of().mapColor(MapColor.SAND).strength(1f, 10f);
    public static final BlockBehaviour.Properties BLOCK_HARD_SAND = BlockBehaviour.Properties.of().mapColor(MapColor.SAND).strength(1f, 12f);
    public static final BlockBehaviour.Properties BLOCK_DIRT = BlockBehaviour.Properties.of().mapColor(MapColor.DIRT).strength(0.5f, 1f);
    public static final BlockBehaviour.Properties BLOCK_NETHER_STONE = BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(0.5f, 1f).requiresCorrectToolForDrops();
    public static final BlockBehaviour.Properties BLOCK_END_STONE = BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(2f, 9f).requiresCorrectToolForDrops();
    public static final BlockBehaviour.Properties BLOCK_LEAVES = BlockBehaviour.Properties.of().mapColor(MapColor.PLANT).pushReaction(PushReaction.DESTROY).ignitedByLava().strength(1f, 0.2f).noOcclusion();
    public static final BlockBehaviour.Properties BLOCK_WOOD_FURNITURE = BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).ignitedByLava().strength(2f, 2.5f).noOcclusion().requiresCorrectToolForDrops();
    public static final BlockBehaviour.Properties BLOCK_MINIATURE = BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(0.5f, 0.5f).noOcclusion().requiresCorrectToolForDrops();
    public static final BlockBehaviour.Properties BLOCK_DESSERT = BlockBehaviour.Properties.of().mapColor(MapColor.NONE).pushReaction(PushReaction.DESTROY).strength(0.5f, 0.5f);
    public static final BlockBehaviour.Properties BLOCK_CARPET = BlockBehaviour.Properties.of().mapColor(MapColor.WOOL).ignitedByLava().strength(0.5f, 0.5f).noOcclusion();
    public static final BlockBehaviour.Properties BLOCK_BOARD = BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).ignitedByLava().strength(0.5f, 0.5f).noOcclusion();
    public static final BlockBehaviour.Properties BLOCK_ROOF = BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(1.8f, 12f).noOcclusion();
    public static final BlockBehaviour.Properties BLOCK_STONE_DISPLAY = BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(1.5f, 6f).isRedstoneConductor((a, b, c) -> false);
    public static final BlockBehaviour.Properties BLOCK_METAL_DISPLAY = BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(1.5f, 6f).isRedstoneConductor((a, b, c) -> false);
    public static final BlockBehaviour.Properties BLOCK_WOOD_WARDROBE = BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).ignitedByLava().strength(1.5f, 6f).noOcclusion();
    public static final BlockBehaviour.Properties BLOCK_METAL_WARDROBE = BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(1.5f, 6f).noOcclusion();
    public static final BlockBehaviour.Properties BLOCK_GLASS_WARDROBE = BlockBehaviour.Properties.of().mapColor(MapColor.NONE).strength(1.5f, 6f).noOcclusion();
    public static final BlockBehaviour.Properties BLOCK_PORCELAIN = BlockBehaviour.Properties.of().mapColor(MapColor.NONE).strength(0.5f, 0.5f);
    public static final BlockBehaviour.Properties BLOCK_LANTERN = BlockBehaviour.Properties.of().mapColor(MapColor.WOOL).ignitedByLava().strength(0.5f, 0.5f).lightLevel(s -> 15);
    public static final BlockBehaviour.Properties BLOCK_METAL_LIGHT = BlockBehaviour.Properties.of().mapColor(MapColor.METAL).noOcclusion().strength(0.5f, 0.5f).lightLevel(s -> 15);
    public static final BlockBehaviour.Properties BLOCK_METAL_HALF_LIGHT = BlockBehaviour.Properties.of().mapColor(MapColor.METAL).noOcclusion().strength(0.5f, 0.5f).lightLevel(s -> 7);
    public static final BlockBehaviour.Properties BLOCK_METAL_WITHOUT_LIGHT = BlockBehaviour.Properties.of().mapColor(MapColor.METAL).noOcclusion().strength(0.5f, 0.5f);
    public static final BlockBehaviour.Properties BLOCK_STONE_LIGHT = BlockBehaviour.Properties.of().mapColor(MapColor.STONE).noOcclusion().strength(0.5f, 0.5f).lightLevel(s -> 15);
    public static final BlockBehaviour.Properties BLOCK_WOOD_LIGHT = BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).ignitedByLava().noOcclusion().strength(0.5f, 0.5f).lightLevel(s -> 15);
    public static final BlockBehaviour.Properties BLOCK_STONE_NO_OCCLUSION = BlockBehaviour.Properties.of().mapColor(MapColor.STONE).noOcclusion().strength(0.5f, 0.5f);
    public static final BlockBehaviour.Properties BLOCK_GLASS_NO_OCCLUSION = BlockBehaviour.Properties.of().mapColor(MapColor.NONE).noOcclusion().strength(0.5f, 0.5f);
    public static final BlockBehaviour.Properties BLOCK_METAL_NO_OCCLUSION = BlockBehaviour.Properties.of().mapColor(MapColor.METAL).noOcclusion().strength(0.5f, 0.5f);
    public static final BlockBehaviour.Properties BLOCK_METAL_NO_COLLISSION = BlockBehaviour.Properties.of().mapColor(MapColor.METAL).noCollission().strength(0.5f, 0.5f);
    public static final BlockBehaviour.Properties BLOCK_METAL_LIGHT_NO_COLLISSION = BlockBehaviour.Properties.of().mapColor(MapColor.METAL).noCollission().strength(0.5f, 0.5f).lightLevel(s -> 15);
}
