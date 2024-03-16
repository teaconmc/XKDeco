package org.teacon.xkdeco.init;

import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.registries.RegistryObject;

@Deprecated
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

	public static final BlockBehaviour.Properties BLOCK_WOOD_FURNITURE = BlockBehaviour.Properties.of()
			.mapColor(MapColor.WOOD)
			.ignitedByLava()
			.strength(2f, 2.5f)
			.noOcclusion()
			.requiresCorrectToolForDrops();
	public static final BlockBehaviour.Properties BLOCK_MINIATURE = BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(
			0.5f,
			0.5f).noOcclusion().requiresCorrectToolForDrops();
	public static final BlockBehaviour.Properties BLOCK_DESSERT = BlockBehaviour.Properties.of().mapColor(MapColor.NONE).pushReaction(
			PushReaction.DESTROY).strength(0.5f, 0.5f);
	public static final BlockBehaviour.Properties BLOCK_CARPET = BlockBehaviour.Properties.of()
			.mapColor(MapColor.WOOL)
			.ignitedByLava()
			.strength(0.5f, 0.5f)
			.noOcclusion();
	public static final BlockBehaviour.Properties BLOCK_BOARD = BlockBehaviour.Properties.of()
			.mapColor(MapColor.WOOD)
			.ignitedByLava()
			.strength(0.5f, 0.5f)
			.noOcclusion();
	public static final BlockBehaviour.Properties BLOCK_STONE_DISPLAY = BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(
			1.5f,
			6f).isRedstoneConductor((a, b, c) -> false);
	public static final BlockBehaviour.Properties BLOCK_METAL_DISPLAY = BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(
			1.5f,
			6f).isRedstoneConductor((a, b, c) -> false);
	public static final BlockBehaviour.Properties BLOCK_WOOD_WARDROBE = BlockBehaviour.Properties.of()
			.mapColor(MapColor.WOOD)
			.ignitedByLava()
			.strength(1.5f, 6f)
			.noOcclusion();
	public static final BlockBehaviour.Properties BLOCK_METAL_WARDROBE = BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(
			1.5f,
			6f).noOcclusion();
	public static final BlockBehaviour.Properties BLOCK_GLASS_WARDROBE = BlockBehaviour.Properties.of().mapColor(MapColor.NONE).strength(
			1.5f,
			6f).noOcclusion();
	public static final BlockBehaviour.Properties BLOCK_PORCELAIN = BlockBehaviour.Properties.of().mapColor(MapColor.NONE).strength(
			0.5f,
			0.5f);
	public static final BlockBehaviour.Properties BLOCK_LANTERN = BlockBehaviour.Properties.of()
			.mapColor(MapColor.WOOL)
			.ignitedByLava()
			.strength(0.5f, 0.5f)
			.lightLevel(s -> 15);
	public static final BlockBehaviour.Properties BLOCK_METAL_LIGHT = BlockBehaviour.Properties.of()
			.mapColor(MapColor.METAL)
			.noOcclusion()
			.strength(0.5f, 0.5f)
			.lightLevel(s -> 15);
	public static final BlockBehaviour.Properties BLOCK_STONE_LIGHT = BlockBehaviour.Properties.of()
			.mapColor(MapColor.STONE)
			.noOcclusion()
			.strength(0.5f, 0.5f)
			.lightLevel(s -> 15);
	public static final BlockBehaviour.Properties BLOCK_WOOD_LIGHT = BlockBehaviour.Properties.of()
			.mapColor(MapColor.WOOD)
			.ignitedByLava()
			.noOcclusion()
			.strength(0.5f, 0.5f)
			.lightLevel(s -> 15);
	public static final BlockBehaviour.Properties BLOCK_STONE_NO_OCCLUSION = BlockBehaviour.Properties.of()
			.mapColor(MapColor.STONE)
			.noOcclusion()
			.strength(0.5f, 0.5f);
	public static final BlockBehaviour.Properties BLOCK_GLASS_NO_OCCLUSION = BlockBehaviour.Properties.of()
			.mapColor(MapColor.NONE)
			.noOcclusion()
			.strength(0.5f, 0.5f);
	public static final BlockBehaviour.Properties BLOCK_METAL_NO_OCCLUSION = BlockBehaviour.Properties.of()
			.mapColor(MapColor.METAL)
			.noOcclusion()
			.strength(0.5f, 0.5f);
	public static final BlockBehaviour.Properties BLOCK_METAL_LIGHT_NO_COLLISSION = BlockBehaviour.Properties.of()
			.mapColor(MapColor.METAL)
			.noCollission()
			.strength(0.5f, 0.5f)
			.lightLevel(s -> 15);
}
