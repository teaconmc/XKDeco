package org.teacon.xkdeco.init;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.teacon.xkdeco.XKDeco;
import org.teacon.xkdeco.XKDecoCommonConfig;
import org.teacon.xkdeco.block.MimicWallBlock;
import org.teacon.xkdeco.block.XKDBlock;
import org.teacon.xkdeco.util.CommonProxy;

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
	private static ImmutableList<MimicWallBlock> MIMIC_WALLS = ImmutableList.of();
	private static final List<MimicWallBlock> PENDING_MIMIC_WALLS = Lists.newArrayList();

	public static final ResourceKey<CreativeModeTab> STRUCTURE_TAB_KEY = ResourceKey.create(
			Registries.CREATIVE_MODE_TAB,
			XKDeco.id("structure"));

	public static ImmutableList<MimicWallBlock> mimicWalls() {
		if (!PENDING_MIMIC_WALLS.isEmpty()) {
			MIMIC_WALLS = ImmutableList.<MimicWallBlock>builder()
					.addAll(MIMIC_WALLS)
					.addAll(PENDING_MIMIC_WALLS)
					.build();
			PENDING_MIMIC_WALLS.clear();
		}
		return MIMIC_WALLS;
	}

	public static void addMimicWalls() {
		for (var holder : BuiltInRegistries.BLOCK.asHolderIdMap()) {
			newBlockAdded(holder.unwrapKey().orElseThrow().location(), holder.value());
		}
	}

	public static void newBlockAdded(ResourceLocation id, Block block) {
		if (XKDecoCommonConfig.mimicWalls && block instanceof WallBlock wall && !(block instanceof MimicWallBlock) &&
				!block.defaultBlockState().hasBlockEntity() &&
				id.getPath().endsWith("_wall") &&
				block.getStateDefinition().getProperties().size() == Blocks.COBBLESTONE_WALL.getStateDefinition().getProperties().size()) {
			MimicWallBlock mimicWall = new MimicWallBlock(wall);
			var name = MimicWallBlock.toMimicId(id);
			id = XKDeco.id(name);
			CommonProxy.registerBlock(id, mimicWall);
			CommonProxy.registerItem(id, new ModBlockItem(mimicWall, new Item.Properties()));
			PENDING_MIMIC_WALLS.add(mimicWall);
		}
	}

	public static void addMimicWallBlockTags(Map<ResourceLocation, Collection<Holder<Block>>> tags) {
		List<Holder<Block>> walls = mimicWalls().stream().map(BuiltInRegistries.BLOCK::wrapAsHolder).toList();
		appendTagValues(tags, BlockTags.WALLS, walls);
		appendTagValues(tags, BlockTags.MINEABLE_WITH_PICKAXE, walls);
		appendTagValues(tags, XKDBlock.NON_DIAGONAL_WALLS, walls);
	}

	public static void addMimicWallItemTags(Map<ResourceLocation, Collection<Holder<Item>>> tags) {
		List<Holder<Item>> walls = mimicWalls().stream().map(Block::asItem).map(BuiltInRegistries.ITEM::wrapAsHolder).toList();
		appendTagValues(tags, ItemTags.WALLS, walls);
	}

	private static <T> void appendTagValues(
			Map<ResourceLocation, Collection<Holder<T>>> tags,
			TagKey<T> key,
			List<Holder<T>> holders) {
		if (holders.isEmpty()) {
			return;
		}
		List<Holder<T>> list = Lists.newArrayList(tags.getOrDefault(key.location(), List.of()));
		list.addAll(holders);
		tags.put(key.location(), list);
	}
}
