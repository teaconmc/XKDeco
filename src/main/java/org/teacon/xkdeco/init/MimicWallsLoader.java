/*
package org.teacon.xkdeco.init;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.teacon.xkdeco.XKDeco;
import org.teacon.xkdeco.block.MimicWallBlock;
import org.teacon.xkdeco.item.MimicWallItem;

import com.google.common.collect.Lists;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WallBlock;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.registries.RegisterEvent;
import snownee.kiwi.datagen.GameObjectLookup;
import snownee.kiwi.util.NotNullByDefault;

@NotNullByDefault
public final class MimicWallsLoader {
	public static final String WALL_BLOCK_ENTITY = "mimic_wall";

	private static final ResourceKey<CreativeModeTab> STRUCTURE_TAB_KEY = ResourceKey.create(
			Registries.CREATIVE_MODE_TAB,
			XKDeco.id("structure"));

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
				var registryName = Objects.requireNonNull(BuiltInRegistries.BLOCK.getKey(block));
				var name = MimicWallBlock.toMimicId(registryName);
				event.register(Registries.BLOCK, XKDeco.id(name), () -> new MimicWallBlock(wall));
			}
		}
	}

	public static void addMimicWallItems(RegisterEvent event) {
		for (var holder : BuiltInRegistries.BLOCK.asHolderIdMap()) {
			var block = holder.value();
			if (block instanceof MimicWallBlock wall) {
				var registryName = holder.unwrapKey().orElseThrow().location();
				event.register(Registries.ITEM, registryName, () -> new MimicWallItem(wall, new Item.Properties()));
			}
		}
	}

	public static void addMimicWallTags(Map<ResourceLocation, Collection<Holder<Block>>> tags) {
		List<Holder<Block>> walls = Lists.newArrayList(tags.getOrDefault(BlockTags.WALLS.location(), List.of()));
		for (var holder : BuiltInRegistries.BLOCK.asHolderIdMap()) {
			if (holder.value() instanceof MimicWallBlock) {
				walls.add(holder);
			}
		}
		tags.put(BlockTags.WALLS.location(), walls);
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
}
*/
