package org.teacon.xkdeco.data;

import static net.minecraft.data.recipes.RecipeCategory.BUILDING_BLOCKS;
import static net.minecraft.data.recipes.RecipeCategory.DECORATIONS;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.recipes.RecipeOutput;

import net.minecraft.world.flag.FeatureFlagSet;

import org.teacon.xkdeco.XKDeco;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import snownee.kiwi.AbstractModule;

public class XKDRecipeProvider extends FabricRecipeProvider {
	private static final TagKey<Item> EBONY_LOGS = AbstractModule.itemTag(XKDeco.ID, "ebony_logs");
	private static final TagKey<Item> MAHOGANY_LOGS = AbstractModule.itemTag(XKDeco.ID, "mahogany_logs");
	private static final TagKey<Item> VARNISHED_LOGS = AbstractModule.itemTag(XKDeco.ID, "varnished_logs");

	public XKDRecipeProvider(FabricDataOutput output, CompletableFuture<Provider> registriesFuture) {
		super(output, registriesFuture);
	}

	@Override
	public void buildRecipes(RecipeOutput consumer) {
		XKDBlockFamilies.getAllFamilies().forEach(family -> {
			if (family.getBaseBlock().asItem() == Items.AIR) {
				return;
			}
			generateRecipes(consumer, family, FeatureFlagSet.of());
		});

		coloredTiles(consumer, "black", Items.BLACK_TERRACOTTA);
		coloredTiles(consumer, "cyan", Items.CYAN_TERRACOTTA);
		coloredTiles(consumer, "yellow", Items.YELLOW_TERRACOTTA);
		coloredTiles(consumer, "blue", Items.BLUE_TERRACOTTA);
		coloredTiles(consumer, "green", Items.GREEN_TERRACOTTA);
		coloredTiles(consumer, "red", Items.RED_TERRACOTTA);
		stonecutterResultFromBase(consumer, BUILDING_BLOCKS, i("glass_tiles"), Items.GLASS);
		stonecutterResultFromBase(consumer, BUILDING_BLOCKS, i("steel_tiles"), i("steel_block"));
		Ingredient copperBlock = Ingredient.of(
				Items.COPPER_BLOCK,
				Items.WAXED_COPPER_BLOCK,
				Items.EXPOSED_COPPER,
				Items.WAXED_EXPOSED_COPPER,
				Items.WEATHERED_COPPER,
				Items.WAXED_WEATHERED_COPPER,
				Items.OXIDIZED_COPPER,
				Items.WAXED_OXIDIZED_COPPER);
		SingleItemRecipeBuilder.stonecutting(copperBlock, BUILDING_BLOCKS, i("copper_tiles"))
				.unlockedBy("has_item", has(Items.COPPER_INGOT))
				.save(consumer, "copper_tiles_from_copper_block");

		ShapedRecipeBuilder.shaped(BUILDING_BLOCKS, i("mud_wall_block"), 4)
				.pattern(" B ")
				.pattern("BCB")
				.pattern(" B ")
				.define('B', Items.BONE_MEAL)
				.define('C', Items.CLAY)
				.unlockedBy("has_item", has(Items.CLAY))
				.save(consumer);
		ShapedRecipeBuilder.shaped(BUILDING_BLOCKS, i("cyan_bricks"), 6)
				.pattern("TB")
				.pattern("BT")
				.define('B', Items.BRICKS)
				.define('T', Items.CYAN_TERRACOTTA)
				.unlockedBy("has_item", has(Items.BRICKS))
				.save(consumer);
		ShapedRecipeBuilder.shaped(BUILDING_BLOCKS, i("black_bricks"), 6)
				.pattern("TB")
				.pattern("BT")
				.define('B', Items.BRICKS)
				.define('T', Items.BLACK_TERRACOTTA)
				.unlockedBy("has_item", has(Items.BRICKS))
				.save(consumer);

		for (String s : List.of("_log", "_wood", "_planks")) {
			smokingRecipe(consumer, i("ebony" + s), i("minecraft:dark_oak" + s));
			smokingRecipe(consumer, i("mahogany" + s), i("minecraft:mangrove" + s));
			smokingRecipe(consumer, i("varnished" + s), i("minecraft:oak" + s));
		}
		woodFromLogs(consumer, i("ebony_wood"), i("ebony_log"));
		woodFromLogs(consumer, i("mahogany_wood"), i("mahogany_log"));
		woodFromLogs(consumer, i("varnished_wood"), i("varnished_log"));
		planksFromLogs(consumer, i("ebony_planks"), EBONY_LOGS, 4);
		planksFromLogs(consumer, i("mahogany_planks"), MAHOGANY_LOGS, 4);
		planksFromLogs(consumer, i("varnished_planks"), VARNISHED_LOGS, 4);

		shapelessTwoToOne(
				consumer,
				BUILDING_BLOCKS,
				i("mossy_deepslate_bricks"),
				Items.DEEPSLATE_BRICKS,
				Items.MOSS_BLOCK,
				1,
				true);
		shapelessTwoToOne(
				consumer,
				BUILDING_BLOCKS,
				i("mossy_deepslate_bricks"),
				Items.DEEPSLATE_BRICKS,
				Items.VINE,
				1,
				true);
		Item mayaStone = i("maya_stone");
		ShapedRecipeBuilder.shaped(BUILDING_BLOCKS, mayaStone, 6)
				.pattern("TB")
				.pattern("BT")
				.define('B', Items.ANDESITE)
				.define('T', Items.GRANITE)
				.unlockedBy(getHasName(Items.ANDESITE), has(Items.ANDESITE))
				.unlockedBy(getHasName(Items.GRANITE), has(Items.GRANITE))
				.save(consumer);
		stonecutterResultFromBase(consumer, BUILDING_BLOCKS, i("aztec_stonebricks"), mayaStone);
		ShapedRecipeBuilder.shaped(BUILDING_BLOCKS, i("inca_stone"), 4)
				.pattern("TB")
				.pattern("BT")
				.define('B', mayaStone)
				.define('T', Items.GRANITE)
				.unlockedBy(getHasName(mayaStone), has(mayaStone))
				.unlockedBy(getHasName(Items.GRANITE), has(Items.GRANITE))
				.save(consumer);
		ShapedRecipeBuilder.shaped(BUILDING_BLOCKS, i("bronze_block"), 4)
				.pattern("TB")
				.pattern("BT")
				.define('B', Items.IRON_NUGGET)
				.define('T', Items.COPPER_INGOT)
				.unlockedBy(getHasName(Items.IRON_NUGGET), has(Items.IRON_NUGGET))
				.unlockedBy(getHasName(Items.COPPER_INGOT), has(Items.COPPER_INGOT))
				.save(consumer);

		SimpleCookingRecipeBuilder.blasting(Ingredient.of(Items.IRON_BLOCK), BUILDING_BLOCKS, i("steel_block"), 0.1f, 100)
				.unlockedBy("has_item", has(Items.IRON_BLOCK))
				.save(consumer);
		stonecutterResultFromBase(consumer, BUILDING_BLOCKS, i("factory_block"), Blocks.IRON_BLOCK, 9);
		stonecutterResultFromBase(consumer, BUILDING_BLOCKS, i("factory_lamp_block"), Items.GLOWSTONE, 4);
		stonecutterResultFromBase(consumer, BUILDING_BLOCKS, i("tech_lamp_block"), Items.SEA_LANTERN, 4);
		shapelessTwoToOne(consumer, BUILDING_BLOCKS, i("translucent_lamp_block"), i("tech_lamp_block"), Items.GLASS, 2, false);

		stonecutterResultFromBase(consumer, BUILDING_BLOCKS, i("steel_filings"), i("steel_block"), 4);
		stonecutterResultFromBase(consumer, BUILDING_BLOCKS, i("quartz_sand"), Items.QUARTZ_BLOCK, 4);
		shapelessTwoToOne(consumer, BUILDING_BLOCKS, i("toughened_sand"), i("steel_filings"), i("quartz_sand"), 4, false);
		smeltingResultFromBase(consumer, i("quartz_glass"), i("quartz_sand"));
		smeltingResultFromBase(consumer, i("toughened_glass"), i("toughened_sand"));

		stonecutterResultFromBase(consumer, BUILDING_BLOCKS, i("quartz_wall"), Items.QUARTZ_BLOCK, 1);
		stonecutterResultFromBase(consumer, BUILDING_BLOCKS, i("quartz_wall"), Items.QUARTZ_PILLAR, 1);

		stonecutterResultFromBase(consumer, BUILDING_BLOCKS, i("dirt_slab"), Items.DIRT, 2);
		stonecutterResultFromBase(consumer, BUILDING_BLOCKS, i("dirt_path_slab"), Items.DIRT, 2);
		stonecutterResultFromBase(consumer, BUILDING_BLOCKS, i("grass_block_slab"), Items.DIRT, 2);
		stonecutterResultFromBase(consumer, BUILDING_BLOCKS, i("mycelium_slab"), Items.DIRT, 2);
		stonecutterResultFromBase(consumer, BUILDING_BLOCKS, i("podzol_slab"), Items.DIRT, 2);
		stonecutterResultFromBase(consumer, BUILDING_BLOCKS, i("netherrack_slab"), Items.NETHERRACK, 2);
		stonecutterResultFromBase(consumer, BUILDING_BLOCKS, i("crimson_nylium_slab"), Items.NETHERRACK, 2);
		stonecutterResultFromBase(consumer, BUILDING_BLOCKS, i("warped_nylium_slab"), Items.NETHERRACK, 2);
		stonecutterResultFromBase(consumer, BUILDING_BLOCKS, i("end_stone_slab"), Items.END_STONE, 2);

		shapelessTwoToOne(consumer, BUILDING_BLOCKS, i("dirt_cobblestone"), Items.DIRT, Items.COBBLESTONE, 1, false);
		shapelessTwoToOne(consumer, BUILDING_BLOCKS, i("grass_cobblestone"), Items.TALL_GRASS, Items.COBBLESTONE, 1, false);
		shapelessTwoToOne(consumer, BUILDING_BLOCKS, i("sandy_cobblestone"), Items.SAND, Items.COBBLESTONE, 1, false);
		shapelessTwoToOne(consumer, BUILDING_BLOCKS, i("snowy_cobblestone"), Items.SNOW_BLOCK, Items.COBBLESTONE, 1, false);

		shapedSurroundedBy(consumer, BUILDING_BLOCKS, i("ginkgo_leaves"), Items.YELLOW_DYE, Items.OAK_LEAVES, 8);
		shapedSurroundedBy(consumer, BUILDING_BLOCKS, i("orange_maple_leaves"), Items.ORANGE_DYE, Items.OAK_LEAVES, 8);
		shapedSurroundedBy(consumer, BUILDING_BLOCKS, i("red_maple_leaves"), Items.RED_DYE, Items.OAK_LEAVES, 8);
		shapedSurroundedBy(consumer, BUILDING_BLOCKS, i("peach_blossom"), Items.RED_DYE, Items.CHERRY_LEAVES, 8);
		shapedSurroundedBy(consumer, BUILDING_BLOCKS, i("cherry_blossom"), Items.PINK_DYE, Items.CHERRY_LEAVES, 8);
		shapedSurroundedBy(consumer, BUILDING_BLOCKS, i("white_cherry_blossom"), Items.WHITE_DYE, Items.CHERRY_LEAVES, 8);
		shapedSurroundedBy(consumer, BUILDING_BLOCKS, i("plantable_leaves"), Items.DIRT, Items.OAK_LEAVES, 8);
		shapedSurroundedBy(consumer, BUILDING_BLOCKS, i("plantable_leaves_dark"), Items.DIRT, Items.DARK_OAK_LEAVES, 8);
		shapelessTwoToOne(consumer, BUILDING_BLOCKS, i("peach_blossom_leaves"), i("peach_blossom"), Items.OAK_LEAVES, 2, false);
		shapelessTwoToOne(consumer, BUILDING_BLOCKS, i("cherry_blossom_leaves"), i("cherry_blossom"), Items.OAK_LEAVES, 2, false);
		shapelessTwoToOne(
				consumer,
				BUILDING_BLOCKS,
				i("white_cherry_blossom_leaves"),
				i("white_cherry_blossom"),
				Items.OAK_LEAVES,
				2,
				false);
		fallenLeaves(consumer, i("fallen_ginkgo_leaves"), i("ginkgo_leaves"));
		fallenLeaves(consumer, i("fallen_orange_maple_leaves"), i("orange_maple_leaves"));
		fallenLeaves(consumer, i("fallen_red_maple_leaves"), i("red_maple_leaves"));
		fallenLeaves(consumer, i("fallen_peach_blossom"), i("peach_blossom"));
		fallenLeaves(consumer, i("fallen_cherry_blossom"), i("cherry_blossom"));
		fallenLeaves(consumer, i("fallen_white_cherry_blossom"), i("white_cherry_blossom"));

		ShapedRecipeBuilder.shaped(DECORATIONS, i("factory_lamp"), 4)
				.pattern(" G ")
				.pattern("GDG")
				.pattern(" S ")
				.define('G', Items.GLASS_PANE)
				.define('S', i("steel_tile_slab"))
				.define('D', Items.GLOWSTONE_DUST)
				.unlockedBy("has_item", has(Items.GLOWSTONE_DUST))
				.save(consumer);

		stonecutterResultFromBase(consumer, DECORATIONS, i("item_frame_cover"), Items.GLASS);
		ShapelessRecipeBuilder.shapeless(DECORATIONS, i("glow_item_frame_cover"))
				.requires(i("item_frame_cover"))
				.requires(Items.GLOWSTONE_DUST)
				.unlockedBy("has_item", has(i("item_frame_cover")))
				.save(consumer);

		ShapedRecipeBuilder.shaped(DECORATIONS, i("plain_item_display"))
				.pattern(" F ")
				.pattern("SSS")
				.pattern(" B ")
				.define('F', Items.ITEM_FRAME)
				.define('S', Items.SMOOTH_STONE_SLAB)
				.define('B', Items.SMOOTH_STONE)
				.unlockedBy("has_item", has(Items.ITEM_FRAME))
				.save(consumer);
		ShapedRecipeBuilder.shaped(DECORATIONS, i("plain_block_display"))
				.pattern(" F ")
				.pattern("SSS")
				.pattern("B B")
				.define('F', Items.ITEM_FRAME)
				.define('S', Items.SMOOTH_STONE_SLAB)
				.define('B', Items.SMOOTH_STONE)
				.unlockedBy("has_item", has(Items.ITEM_FRAME))
				.save(consumer);
		ShapedRecipeBuilder.shaped(DECORATIONS, i("item_projector"))
				.pattern("A")
				.pattern("B")
				.define('A', Items.SPYGLASS)
				.define('B', i("tech_item_display"))
				.unlockedBy("has_item", has(Items.ITEM_FRAME))
				.save(consumer);
	}

	private static void fallenLeaves(RecipeOutput consumer, ItemLike pCarpet, ItemLike pMaterial) {
		ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, pCarpet, 3)
				.define('#', pMaterial)
				.pattern("##")
				.group("fallen_leaves")
				.unlockedBy(getHasName(pMaterial), has(pMaterial))
				.save(consumer);
	}

	private static void shapedSurroundedBy(
			RecipeOutput consumer,
			RecipeCategory category,
			ItemLike result,
			Item middle,
			ItemLike surround,
			int count) {
		ShapedRecipeBuilder.shaped(category, result, count)
				.pattern("SSS")
				.pattern("SMS")
				.pattern("SSS")
				.define('S', surround)
				.define('M', middle)
				.unlockedBy("has_item", has(surround))
				.save(consumer);
	}

	private static void shapelessTwoToOne(
			RecipeOutput consumer,
			RecipeCategory category,
			ItemLike result,
			ItemLike input1,
			ItemLike input2,
			int resultCount,
			boolean longName) {
		String name = BuiltInRegistries.ITEM.getKey(result.asItem()).getPath();
		if (longName) {
			name += "_from_" + BuiltInRegistries.ITEM.getKey(input2.asItem()).getPath();
		}
		ShapelessRecipeBuilder.shapeless(category, result, resultCount)
				.requires(input1)
				.requires(input2)
				.unlockedBy("has_item", has(input1))
				.save(consumer, name);
	}

	private static void smokingRecipe(RecipeOutput consumer, ItemLike result, ItemLike material) {
		SimpleCookingRecipeBuilder.smoking(Ingredient.of(material), BUILDING_BLOCKS, result, 0.1f, 100)
				.unlockedBy("has_item", has(material))
				.save(consumer, BuiltInRegistries.ITEM.getKey(result.asItem()).getPath() + "_from_smoking");
	}

	private static void coloredTiles(RecipeOutput consumer, String color, ItemLike terracotta) {
		ShapedRecipeBuilder.shaped(BUILDING_BLOCKS, i(color + "_tiles"), 8)
				.pattern("TTT")
				.pattern("WWW")
				.define('T', terracotta)
				.define('W', ItemTags.PLANKS)
				.unlockedBy("has_item", has(terracotta))
				.save(consumer);
	}

	private static Item i(String id) {
		ResourceLocation resourceLocation;
		if (id.contains(":")) {
			resourceLocation = ResourceLocation.parse(id);
		} else {
			resourceLocation = XKDeco.id(id);
		}
		return BuiltInRegistries.ITEM.getOptional(resourceLocation).orElseThrow();
	}
}
