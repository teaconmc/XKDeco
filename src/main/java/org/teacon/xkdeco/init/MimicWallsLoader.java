package org.teacon.xkdeco.init;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import org.teacon.xkdeco.XKDeco;
import org.teacon.xkdeco.block.MimicWallBlock;
import org.teacon.xkdeco.block.XKDBlock;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WallBlock;
import snownee.kiwi.item.ModBlockItem;
import snownee.kiwi.util.NotNullByDefault;

@NotNullByDefault
public final class MimicWallsLoader {
	public static ImmutableList<MimicWallBlock> MIMIC_WALLS = ImmutableList.of();

	public static final ResourceKey<CreativeModeTab> STRUCTURE_TAB_KEY = ResourceKey.create(
			Registries.CREATIVE_MODE_TAB,
			XKDeco.id("structure"));

	public static void addMimicWallBlocks(BiConsumer<ResourceLocation, Block> consumer) {
		ImmutableList.Builder<MimicWallBlock> builder = ImmutableList.builder();
		for (var holder : BuiltInRegistries.BLOCK.asHolderIdMap()) {
			var block = holder.value();
			if (block instanceof WallBlock wall && !(block instanceof MimicWallBlock) && !block.defaultBlockState().hasBlockEntity() &&
					block.getStateDefinition().getProperties().size() ==
							Blocks.COBBLESTONE_WALL.getStateDefinition().getProperties().size()) {
				var registryName = holder.unwrapKey().orElseThrow().location();
				if (registryName.getPath().endsWith("_wall")) {
					MimicWallBlock mimicWall = new MimicWallBlock(wall);
					var name = MimicWallBlock.toMimicId(holder.unwrapKey().orElseThrow().location());
					consumer.accept(XKDeco.id(name), mimicWall);
					builder.add(mimicWall);
				}
			}
		}
		MIMIC_WALLS = builder.build();
	}

	public static void addMimicWallItems(BiConsumer<ResourceLocation, Item> consumer) {
		for (MimicWallBlock wall : MIMIC_WALLS) {
			var name = BuiltInRegistries.BLOCK.getKey(wall);
			consumer.accept(name, new ModBlockItem(wall, new Item.Properties()));
		}
	}

	public static void addMimicWallBlockTags(Map<ResourceLocation, Collection<Holder<Block>>> tags) {
		List<Holder<Block>> walls = MIMIC_WALLS.stream().map(BuiltInRegistries.BLOCK::wrapAsHolder).toList();
		appendTagValues(tags, BlockTags.WALLS, walls);
		appendTagValues(tags, BlockTags.MINEABLE_WITH_PICKAXE, walls);
		appendTagValues(tags, XKDBlock.NON_DIAGONAL_WALLS, walls);
	}

	public static void addMimicWallItemTags(Map<ResourceLocation, Collection<Holder<Item>>> tags) {
		List<Holder<Item>> walls = MIMIC_WALLS.stream().map(Block::asItem).map(BuiltInRegistries.ITEM::wrapAsHolder).toList();
		appendTagValues(tags, ItemTags.WALLS, walls);
	}

	private static <T> void appendTagValues(
			Map<ResourceLocation, Collection<Holder<T>>> tags,
			TagKey<T> key,
			List<Holder<T>> holders) {
		List<Holder<T>> list = Lists.newArrayList(tags.getOrDefault(key.location(), List.of()));
		list.addAll(holders);
		tags.put(key.location(), list);
	}
}
