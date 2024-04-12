package snownee.kiwi.customization.block.family;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.google.common.collect.Streams;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import snownee.kiwi.customization.util.KHolder;

public class StonecutterRecipeMaker {
	private static final Cache<Item, List<StonecutterRecipe>> EXCHANGE_CACHE = CacheBuilder.newBuilder().expireAfterAccess(Duration.of(
			1,
			ChronoUnit.MINUTES)).build();
	private static final Cache<Item, List<StonecutterRecipe>> SOURCE_CACHE = CacheBuilder.newBuilder().expireAfterAccess(Duration.of(
			1,
			ChronoUnit.MINUTES)).build();

	public static List<StonecutterRecipe> appendRecipesFor(List<StonecutterRecipe> recipes, Container container) {
		ItemStack itemStack = container.getItem(0);
		if (itemStack.isEmpty()) {
			return recipes;
		}
		Item item = itemStack.getItem();
		List<StonecutterRecipe> exchangeRecipes = List.of();
		get_recipes:
		try {
			Collection<KHolder<BlockFamily>> families = BlockFamilies.find(item);
			if (families.isEmpty()) {
				break get_recipes;
			}
			exchangeRecipes = EXCHANGE_CACHE.get(item, () -> {
				List<StonecutterRecipe> list = null;
				for (KHolder<BlockFamily> family : families) {
					if (!family.value().stonecutterExchange()) {
						continue;
					}
					if (list == null) {
						list = Lists.newArrayList();
					}
					ResourceLocation itemKey = BuiltInRegistries.ITEM.getKey(item);
					list.addAll(makeRecipes(
							"%s/%s".formatted(itemKey.getNamespace(), itemKey.getPath()),
							family.value().ingredient(),
							family));
				}
				return list == null ? List.of() : list;
			});
		} catch (ExecutionException ignored) {
		}
		List<StonecutterRecipe> sourceRecipes = List.of();
		get_recipes:
		try {
			Collection<KHolder<BlockFamily>> families = BlockFamilies.findByStonecutterSource(item);
			if (families.isEmpty()) {
				break get_recipes;
			}
			sourceRecipes = SOURCE_CACHE.get(item, () -> {
				List<StonecutterRecipe> list = Lists.newArrayList();
				for (KHolder<BlockFamily> family : families) {
					Ingredient from = family.value().stonecutterSourceIngredient();
					list.addAll(makeRecipes("to", from, family));
				}
				return list;
			});
		} catch (ExecutionException ignored) {
		}
		if (exchangeRecipes.isEmpty() && sourceRecipes.isEmpty()) {
			return recipes;
		}
		return Streams.concat(recipes.stream(), exchangeRecipes.stream(), sourceRecipes.stream())
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private static List<StonecutterRecipe> makeRecipes(String type, Ingredient input, KHolder<BlockFamily> family) {
		ResourceLocation prefix = family.key().withPath("fake/stonecutter/%s/%s".formatted(family.key().getPath(), type));
		return family.value().values().map(block -> {
			Item item = block.asItem();
			if (item == Items.AIR) {
				return null;
			}
			ResourceLocation itemKey = BuiltInRegistries.ITEM.getKey(item);
			return new StonecutterRecipe(
					prefix.withSuffix("/%s/%s".formatted(itemKey.getNamespace(), itemKey.getPath())),
					prefix.toString(),
					input,
					new ItemStack(block));
		}).filter(Objects::nonNull).toList();
	}

	public static void invalidateCache() {
		EXCHANGE_CACHE.invalidateAll();
		SOURCE_CACHE.invalidateAll();
	}
}
