package snownee.kiwi.customization.block.family;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import snownee.kiwi.customization.util.KHolder;
import snownee.kiwi.customization.util.resource.OneTimeLoader;

public class BlockFamilies {
	private static ImmutableListMultimap<ItemLike, KHolder<BlockFamily>> byItemLike = ImmutableListMultimap.of();
	private static ImmutableMap<ResourceLocation, KHolder<BlockFamily>> byId = ImmutableMap.of();
	private static ImmutableListMultimap<Item, KHolder<BlockFamily>> byStonecutterSource = ImmutableListMultimap.of();

	public static Collection<KHolder<BlockFamily>> find(ItemLike itemLike) {
		if (itemLike == Items.AIR) {
			return List.of();
		}
		Item item = itemLike.asItem();
		if (item == Items.AIR) {
			return byItemLike.get(itemLike);
		}
		return byItemLike.get(item);
	}

	public static List<KHolder<BlockFamily>> findQuickSwitch(ItemLike itemLike) {
		return find(itemLike).stream().filter(f -> f.value().quickSwitch()).toList();
	}

	public static Collection<KHolder<BlockFamily>> findByStonecutterSource(Item item) {
		return byStonecutterSource.get(item);
	}

	public static int reload(ResourceManager resourceManager) {
		Map<ResourceLocation, BlockFamily> families = OneTimeLoader.load(resourceManager, "kiwi/family/block", BlockFamily.CODEC);
		byId = ImmutableMap.copyOf(families.entrySet()
				.stream()
				.map(e -> new KHolder<>(e.getKey(), e.getValue()))
				.collect(ImmutableMap.toImmutableMap(
						KHolder::key,
						Function.identity())));
		ImmutableListMultimap.Builder<ItemLike, KHolder<BlockFamily>> byItemLikeBuilder = ImmutableListMultimap.builder();
		ImmutableListMultimap.Builder<Item, KHolder<BlockFamily>> byStonecutterBuilder = ImmutableListMultimap.builder();
		for (var family : byId.values()) {
			for (var block : family.value().holders()) {
				Item item = block.value().asItem();
				if (item == Items.AIR) {
					byItemLikeBuilder.put(block.value(), family);
				} else {
					byItemLikeBuilder.put(item, family);
				}
			}
			Item stonecutterFrom = family.value().stonecutterSource();
			if (stonecutterFrom != Items.AIR) {
				byStonecutterBuilder.put(stonecutterFrom, family);
			}
		}
		byItemLike = byItemLikeBuilder.build();
		byStonecutterSource = byStonecutterBuilder.build();
		StonecutterRecipeMaker.invalidateCache();
		return byId.size();
	}

	public static BlockFamily get(ResourceLocation id) {
		KHolder<BlockFamily> holder = byId.get(id);
		return holder == null ? null : holder.value();
	}

	public static Collection<KHolder<BlockFamily>> all() {
		return byId.values();
	}
}
