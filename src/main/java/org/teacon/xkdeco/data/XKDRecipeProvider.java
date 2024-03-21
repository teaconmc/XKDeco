package org.teacon.xkdeco.data;

import java.util.List;
import java.util.function.Consumer;

import org.teacon.xkdeco.XKDeco;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

public class XKDRecipeProvider extends FabricRecipeProvider {
	public XKDRecipeProvider(FabricDataOutput output) {
		super(output);
	}

	@Override
	public void buildRecipes(Consumer<FinishedRecipe> consumer) {
		XKDBlockFamilies.getAllFamilies().forEach(family -> {
			generateRecipes(consumer, family);
		});

		stonecutterResultFromBase(consumer, RecipeCategory.BUILDING_BLOCKS, i("glass_tiles"), Blocks.GLASS);
		stonecutterResultFromBase(consumer, RecipeCategory.DECORATIONS, i("item_frame_cover"), Blocks.GLASS);
		ShapelessRecipeBuilder.shapeless(RecipeCategory.DECORATIONS, i("glow_item_frame_cover"))
				.requires(i("item_frame_cover"))
				.requires(Items.GLOWSTONE_DUST)
				.unlockedBy("has_item", has(i("item_frame_cover")))
				.save(consumer);

		for (String s : List.of("danger", "attention", "electricity", "toxic", "radiation", "biohazard")) {
			for (String suffix : List.of("", "_rusting", "_rusted")) {
				stonecutterResultFromBase(
						consumer,
						RecipeCategory.BUILDING_BLOCKS,
						i("factory_" + s + suffix),
						i("factory_block" + suffix));
			}
		}

		//TODO tag-based
//		stonecutterManyToManyStr(
//				consumer,
//				RecipeCategory.BUILDING_BLOCKS,
//				List.of("factory_block", "factory_block_rusting", "factory_block_rusted", "steel_tiles"));
	}

	private static void stonecutterManyToManyStr(Consumer<FinishedRecipe> consumer, RecipeCategory category, List<String> items) {
		stonecutterManyToMany(consumer, category, items.stream().map(XKDRecipeProvider::i).toList());
	}

	private static void stonecutterManyToMany(Consumer<FinishedRecipe> consumer, RecipeCategory category, List<Item> items) {
		for (Item result : items) {
			for (Item mat : items) {
				if (result != mat) {
					stonecutterResultFromBase(consumer, category, result, mat);
				}
			}
		}
	}

	private static Item i(String id) {
		return BuiltInRegistries.ITEM.getOptional(XKDeco.id(id)).orElseThrow();
	}
}
