package org.teacon.xkdeco.init;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.teacon.xkdeco.XKDeco;
import org.teacon.xkdeco.XKDecoCommonConfig;
import org.teacon.xkdeco.block.MimicWallBlock;
import org.teacon.xkdeco.block.XKDBlock;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WallBlock;
import net.neoforged.neoforge.registries.RegisterEvent;
import snownee.kiwi.item.ModBlockItem;

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

	public static void addMimicWalls(RegisterEvent event) {
		if (!XKDecoCommonConfig.mimicWalls) {
			return;
		}
		for (var holder : ImmutableList.copyOf(BuiltInRegistries.BLOCK.asHolderIdMap())) {
			newBlockAdded(event, holder.unwrapKey().orElseThrow().identifier(), holder.value());
		}
	}

	public static void newBlockAdded(RegisterEvent event, Identifier id, Block block) {
		if (XKDecoCommonConfig.mimicWalls && block instanceof WallBlock wall && !(block instanceof MimicWallBlock) &&
				!block.defaultBlockState().hasBlockEntity() &&
				id.getPath().endsWith("_wall") &&
				block.getStateDefinition().getProperties().size() == Blocks.COBBLESTONE_WALL.getStateDefinition().getProperties().size()) {
			Identifier mimicId = XKDeco.id(MimicWallBlock.toMimicId(id));
			MimicWallBlock mimicWall = new MimicWallBlock(wall, ResourceKey.create(Registries.BLOCK, mimicId));
			event.register(Registries.BLOCK, mimicId, () -> mimicWall);
			PENDING_MIMIC_WALLS.add(mimicWall);
		}
	}

	public static void addMimicWallItems(RegisterEvent event) {
		if (!XKDecoCommonConfig.mimicWalls) {
			return;
		}
		for (MimicWallBlock mimicWall : mimicWalls()) {
			Identifier mimicId = BuiltInRegistries.BLOCK.getResourceKey(mimicWall)
					.orElseThrow(() -> new IllegalStateException("Mimic wall was not registered: " + mimicWall))
					.identifier();
			event.register(Registries.ITEM, mimicId, () -> new ModBlockItem(
					mimicWall, new Item.Properties().setId(ResourceKey.create(Registries.ITEM, mimicId))));
		}
	}

	public static void addMimicWallBlockTags(Map<Identifier, Collection<Holder<Block>>> tags) {
		List<Holder<Block>> walls = mimicWalls().stream().map(BuiltInRegistries.BLOCK::wrapAsHolder).toList();
		appendTagValues(tags, BlockTags.WALLS, walls);
		appendTagValues(tags, BlockTags.MINEABLE_WITH_PICKAXE, walls);
		appendTagValues(tags, XKDBlock.NON_DIAGONAL_WALLS, walls);
	}

	public static void addMimicWallItemTags(Map<Identifier, Collection<Holder<Item>>> tags) {
		List<Holder<Item>> walls = mimicWalls().stream().map(Block::asItem).map(BuiltInRegistries.ITEM::wrapAsHolder).toList();
		appendTagValues(tags, ItemTags.WALLS, walls);
	}

	public static void addMimicWallBlockPendingTags(Map<TagKey<Block>, List<Holder<Block>>> tags) {
		List<Holder<Block>> walls = mimicWalls().stream().map(BuiltInRegistries.BLOCK::wrapAsHolder).toList();
		appendPendingTagValues(tags, BlockTags.WALLS, walls);
		appendPendingTagValues(tags, BlockTags.MINEABLE_WITH_PICKAXE, walls);
		appendPendingTagValues(tags, XKDBlock.NON_DIAGONAL_WALLS, walls);
	}

	public static void addMimicWallItemPendingTags(Map<TagKey<Item>, List<Holder<Item>>> tags) {
		List<Holder<Item>> walls = mimicWalls().stream().map(Block::asItem).map(BuiltInRegistries.ITEM::wrapAsHolder).toList();
		appendPendingTagValues(tags, ItemTags.WALLS, walls);
	}

	private static <T> void appendTagValues(
			Map<Identifier, Collection<Holder<T>>> tags,
			TagKey<T> key,
			List<Holder<T>> holders) {
		if (holders.isEmpty()) {
			return;
		}
		List<Holder<T>> list = Lists.newArrayList(tags.getOrDefault(key.location(), List.of()));
		list.addAll(holders);
		tags.put(key.location(), list);
	}

	private static <T> void appendPendingTagValues(
			Map<TagKey<T>, List<Holder<T>>> tags,
			TagKey<T> key,
			List<Holder<T>> holders) {
		if (holders.isEmpty()) {
			return;
		}
		LinkedHashSet<Holder<T>> values = new LinkedHashSet<>(tags.getOrDefault(key, List.of()));
		values.addAll(holders);
		tags.put(key, List.copyOf(values));
	}
}
