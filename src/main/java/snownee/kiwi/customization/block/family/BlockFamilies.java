package snownee.kiwi.customization.block.family;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import snownee.kiwi.customization.block.loader.KHolder;
import snownee.kiwi.customization.util.resource.OneTimeLoader;

public class BlockFamilies {
	private static ImmutableListMultimap<ItemLike, KHolder<BlockFamily>> byItemLike = ImmutableListMultimap.of();
	private static ImmutableMap<ResourceLocation, KHolder<BlockFamily>> byId = ImmutableMap.of();

	public static Collection<KHolder<BlockFamily>> find(ItemLike itemLike) {
		Item item = itemLike.asItem();
		if (item == Items.AIR) {
			return byItemLike.get(itemLike);
		}
		return byItemLike.get(item);
	}

	public static int reload(ResourceManager resourceManager) {
		Map<ResourceLocation, BlockFamily> families = OneTimeLoader.load(resourceManager, "kiwi/family/block", BlockFamily.CODEC);
		byId = ImmutableMap.copyOf(families.entrySet()
				.stream()
				.map(e -> new KHolder<>(e.getKey(), e.getValue()))
				.collect(ImmutableMap.toImmutableMap(
						KHolder::key,
						Function.identity())));
		ImmutableListMultimap.Builder<ItemLike, KHolder<BlockFamily>> builder = ImmutableListMultimap.builder();
		for (var entry : byId.entrySet()) {
			KHolder<BlockFamily> family = entry.getValue();
			for (var block : family.value().blocks()) {
				Item item = block.value().asItem();
				if (item == Items.AIR) {
					builder.put(block.value(), family);
				} else {
					builder.put(item, family);
				}
			}
		}
		byItemLike = builder.build();
		StonecutterRecipeMaker.invalidateCache();
		return byId.size();
	}
}
