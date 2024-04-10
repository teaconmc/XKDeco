package snownee.kiwi.customization.block.family;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import snownee.kiwi.customization.block.loader.KHolder;

public class StonecutterRecipeMaker {
	private static final Cache<Item, List<StonecutterRecipe>> CACHE = CacheBuilder.newBuilder().expireAfterAccess(Duration.of(
			2,
			ChronoUnit.MINUTES)).build();

	public static List<StonecutterRecipe> appendRecipesFor(List<StonecutterRecipe> recipes, Container container) {
		ItemStack item = container.getItem(0);
		if (item.isEmpty()) {
			return recipes;
		}
		List<StonecutterRecipe> extraRecipes = List.of();
		try {
			extraRecipes = CACHE.get(item.getItem(), () -> {
				Collection<KHolder<BlockFamily>> families = BlockFamilies.find(item.getItem());
				List<StonecutterRecipe> list = null;
				for (KHolder<BlockFamily> family : families) {
					if (family.value().stonecutterExchange()) {
						if (list == null) {
							list = Lists.newArrayList();
						}
						ResourceLocation itemKey = BuiltInRegistries.ITEM.getKey(item.getItem());
						list.addAll(makeRecipes("%s/%s".formatted(itemKey.getNamespace(), itemKey.getPath()), Ingredient.of(item), family));
					}
					Ingredient from = family.value().stonecutterFromIngredient();
					if (!from.isEmpty()) {
						if (list == null) {
							list = Lists.newArrayList();
						}
						list.addAll(makeRecipes("to", from, family));
					}
				}
				return list == null ? List.of() : list;
			});
		} catch (ExecutionException ignored) {
		}
		if (extraRecipes.isEmpty()) {
			return recipes;
		}
		return Stream.concat(recipes.stream(), extraRecipes.stream()).collect(Collectors.toCollection(ArrayList::new));
	}

	private static List<StonecutterRecipe> makeRecipes(String type, Ingredient input, KHolder<BlockFamily> family) {
		ResourceLocation prefix = family.key().withPath("fake/stonecutter/%s/%s".formatted(family.key().getPath(), type));
		return family.value().blocks().stream().map(block -> {
			Item item = block.value().asItem();
			if (item == Items.AIR) {
				return null;
			}
			ResourceLocation itemKey = BuiltInRegistries.ITEM.getKey(item);
			return new StonecutterRecipe(
					prefix.withSuffix("/%s/%s".formatted(itemKey.getNamespace(), itemKey.getPath())),
					prefix.toString(),
					input,
					new ItemStack(block.value()));
		}).filter(Objects::nonNull).toList();
	}

	public static void invalidateCache() {
		CACHE.invalidateAll();
	}
}
