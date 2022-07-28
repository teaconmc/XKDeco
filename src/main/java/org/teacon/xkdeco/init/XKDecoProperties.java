package org.teacon.xkdeco.init;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import org.teacon.xkdeco.item.XKDecoCreativeModTab;

public final class XKDecoProperties {
    public static final Item.Properties ITEM_BASIC = new Item.Properties().tab(XKDecoCreativeModTab.TAB_BASIC);
    public static final Item.Properties ITEM_STRUCTURE = new Item.Properties().tab(XKDecoCreativeModTab.TAB_STRUCTURE);
    public static final Item.Properties ITEM_NATURE = new Item.Properties().tab(XKDecoCreativeModTab.TAB_NATURE);
    public static final Item.Properties ITEM_FURNITURE = new Item.Properties().tab(XKDecoCreativeModTab.TAB_FURNITURE);
    public static final Item.Properties ITEM_FUNCTIONAL = new Item.Properties().tab(XKDecoCreativeModTab.TAB_FUNCTIONAL);

    public static final BlockBehaviour.Properties BLOCK_MUD = BlockBehaviour.Properties.of(Material.STONE).strength(1.5f, 3f);
    public static final BlockBehaviour.Properties BLOCK_SANDSTONE = BlockBehaviour.Properties.of(Material.STONE).strength(1.5f, 6f);
    public static final BlockBehaviour.Properties BLOCK_GLASS = BlockBehaviour.Properties.of(Material.GLASS).noOcclusion().isValidSpawn((s, g, p, e) -> false).isRedstoneConductor((s, g, p) -> false).isSuffocating((s, g, p) -> false).isViewBlocking((s, g, p) -> false).strength(1.5f, 3f);
    public static final BlockBehaviour.Properties BLOCK_IRON = BlockBehaviour.Properties.of(Material.METAL).strength(2f, 12f).requiresCorrectToolForDrops();
    public static final BlockBehaviour.Properties BLOCK_HARD_IRON = BlockBehaviour.Properties.of(Material.METAL).strength(3f, 12f).requiresCorrectToolForDrops();
    public static final BlockBehaviour.Properties BLOCK_HOLLOW_IRON = BlockBehaviour.Properties.of(Material.METAL).strength(3f, 12f).noOcclusion().requiresCorrectToolForDrops();
    public static final BlockBehaviour.Properties BLOCK_GOLD = BlockBehaviour.Properties.of(Material.METAL, MaterialColor.GOLD).strength(3f, 12f).requiresCorrectToolForDrops();
    public static final BlockBehaviour.Properties BLOCK_COPPER = BlockBehaviour.Properties.of(Material.METAL, MaterialColor.COLOR_ORANGE).strength(2f, 12f).requiresCorrectToolForDrops();
    public static final BlockBehaviour.Properties BLOCK_BRONZE = BlockBehaviour.Properties.of(Material.METAL, MaterialColor.WARPED_NYLIUM).strength(3f, 12f).requiresCorrectToolForDrops();
    public static final BlockBehaviour.Properties BLOCK_WOOD = BlockBehaviour.Properties.of(Material.WOOD).strength(2f, 3f).requiresCorrectToolForDrops();
    public static final BlockBehaviour.Properties BLOCK_BRICK = BlockBehaviour.Properties.of(Material.STONE).strength(1.8f, 6f).requiresCorrectToolForDrops();
    public static final BlockBehaviour.Properties BLOCK_STONE = BlockBehaviour.Properties.of(Material.STONE).strength(1.8f, 9f).requiresCorrectToolForDrops();
    public static final BlockBehaviour.Properties BLOCK_HARD_STONE = BlockBehaviour.Properties.of(Material.STONE).strength(2f, 10f).requiresCorrectToolForDrops();
    public static final BlockBehaviour.Properties BLOCK_OBSIDIAN = BlockBehaviour.Properties.of(Material.PISTON, MaterialColor.COLOR_BLACK).strength(20f, 20f).requiresCorrectToolForDrops();
    public static final BlockBehaviour.Properties BLOCK_LIGHT = BlockBehaviour.Properties.of(Material.GLASS).noOcclusion().strength(2f, 10f).lightLevel(s -> 15);
    public static final BlockBehaviour.Properties BLOCK_SAND = BlockBehaviour.Properties.of(Material.SAND).strength(1f, 10f);
    public static final BlockBehaviour.Properties BLOCK_HARD_SAND = BlockBehaviour.Properties.of(Material.SAND).strength(1f, 12f);
    public static final BlockBehaviour.Properties BLOCK_DIRT = BlockBehaviour.Properties.of(Material.DIRT).strength(0.5f, 1f);
    public static final BlockBehaviour.Properties BLOCK_NETHER_STONE = BlockBehaviour.Properties.of(Material.STONE).strength(0.5f, 1f).requiresCorrectToolForDrops();
    public static final BlockBehaviour.Properties BLOCK_END_STONE = BlockBehaviour.Properties.of(Material.STONE).strength(2f, 9f).requiresCorrectToolForDrops();
    public static final BlockBehaviour.Properties BLOCK_LEAVES = BlockBehaviour.Properties.of(Material.LEAVES).strength(1f, 0.2f).noOcclusion();
    public static final BlockBehaviour.Properties BLOCK_WOOD_FURNITURE = BlockBehaviour.Properties.of(Material.WOOD).strength(2f, 2.5f).noOcclusion().requiresCorrectToolForDrops();
    public static final BlockBehaviour.Properties BLOCK_MINIATURE = BlockBehaviour.Properties.of(Material.STONE).strength(0.5f, 0.5f).noOcclusion().requiresCorrectToolForDrops();
    public static final BlockBehaviour.Properties BLOCK_DESSERT = BlockBehaviour.Properties.of(Material.CAKE).strength(0.5f, 0.5f);
    public static final BlockBehaviour.Properties BLOCK_CARPET = BlockBehaviour.Properties.of(Material.WOOL).strength(0.5f, 0.5f).noOcclusion();
    public static final BlockBehaviour.Properties BLOCK_BOARD = BlockBehaviour.Properties.of(Material.WOOD).strength(0.5f, 0.5f).noOcclusion();
    public static final BlockBehaviour.Properties BLOCK_ROOF = BlockBehaviour.Properties.of(Material.STONE).strength(1.8f, 12f).noOcclusion();
    public static final BlockBehaviour.Properties BLOCK_STONE_DISPLAY = BlockBehaviour.Properties.of(Material.METAL).strength(1.5f, 6f).isRedstoneConductor((a, b, c) -> false);
    public static final BlockBehaviour.Properties BLOCK_METAL_DISPLAY = BlockBehaviour.Properties.of(Material.METAL).strength(1.5f, 6f).isRedstoneConductor((a, b, c) -> false);
    public static final BlockBehaviour.Properties BLOCK_WOOD_WARDROBE = BlockBehaviour.Properties.of(Material.WOOD).strength(1.5f, 6f).noOcclusion();
    public static final BlockBehaviour.Properties BLOCK_METAL_WARDROBE = BlockBehaviour.Properties.of(Material.METAL).strength(1.5f, 6f).noOcclusion();
    public static final BlockBehaviour.Properties BLOCK_GLASS_WARDROBE = BlockBehaviour.Properties.of(Material.GLASS).strength(1.5f, 6f).noOcclusion();
    public static final BlockBehaviour.Properties BLOCK_PORCELAIN = BlockBehaviour.Properties.of(Material.GLASS).strength(0.5f, 0.5f);
    public static final BlockBehaviour.Properties BLOCK_LANTERN = BlockBehaviour.Properties.of(Material.WOOL).strength(0.5f, 0.5f).lightLevel(s -> 15);
    public static final BlockBehaviour.Properties BLOCK_CANDLESTICK = BlockBehaviour.Properties.of(Material.METAL).strength(0.5f, 0.5f).lightLevel(s -> 15);
    public static final BlockBehaviour.Properties BLOCK_EMPTY_CANDLESTICK = BlockBehaviour.Properties.of(Material.METAL).strength(0.5f, 0.5f);
    public static final BlockBehaviour.Properties BLOCK_LAMP = BlockBehaviour.Properties.of(Material.STONE).strength(0.5f, 0.5f).lightLevel(s -> 15);
    public static final BlockBehaviour.Properties BLOCK_WOOD_LAMP = BlockBehaviour.Properties.of(Material.WOOD).strength(0.5f, 0.5f).lightLevel(s -> 15);
    public static final BlockBehaviour.Properties BLOCK_STONE_TANK = BlockBehaviour.Properties.of(Material.STONE).noOcclusion().strength(0.5f, 0.5f);
    public static final BlockBehaviour.Properties BLOCK_GLASS_TANK = BlockBehaviour.Properties.of(Material.GLASS).noOcclusion().strength(0.5f, 0.5f);

}
