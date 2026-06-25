package org.teacon.xkdeco.data;

import static net.minecraft.data.recipes.RecipeCategory.BUILDING_BLOCKS;
import static net.minecraft.data.recipes.RecipeCategory.DECORATIONS;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.teacon.xkdeco.XKDeco;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.advancements.criterion.ChangeDimensionTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import snownee.kiwi.AbstractModule;

public class XKDRecipeProvider extends RecipeProvider {
	private static final TagKey<Item> EBONY_LOGS = AbstractModule.itemTag(XKDeco.ID, "ebony_logs");
	private static final TagKey<Item> MAHOGANY_LOGS = AbstractModule.itemTag(XKDeco.ID, "mahogany_logs");
	private static final TagKey<Item> VARNISHED_LOGS = AbstractModule.itemTag(XKDeco.ID, "varnished_logs");

	protected XKDRecipeProvider(Provider registries, RecipeOutput output) {
		super(registries, output);
	}

	// 26.1 vanilla crafts a CHISELED variant from its SLAB and hard-throws ("Slab is not defined for
	// the family") when a family has CHISELED but no SLAB. 1.20.1 silently generated no recipe in that
	// case, so to keep the same output we run recipe generation on a family copy without the CHISELED
	// variant. Families that do have a slab (e.g. maya/aztec stonebricks) are left untouched and still
	// get their vanilla chiseled-from-slab recipe.
	private static BlockFamily withoutUnsupportedChiseled(BlockFamily family) {
		if (!family.getVariants().containsKey(BlockFamily.Variant.CHISELED)
				|| family.getVariants().containsKey(BlockFamily.Variant.SLAB)) {
			return family;
		}
		BlockFamily.Builder builder = new BlockFamily.Builder(family.getBaseBlock());
		if (!family.shouldGenerateCraftingRecipe()) {
			builder.dontGenerateCraftingRecipe();
		}
		if (family.shouldGenerateStonecutterRecipe()) {
			builder.generateStonecutterRecipe();
		}
		family.getRecipeGroupPrefix().ifPresent(builder::recipeGroupPrefix);
		family.getRecipeUnlockedBy().ifPresent(builder::recipeUnlockedBy);
		BlockFamily copy = builder.getFamily();
		copy.getVariants().putAll(family.getVariants());
		copy.getVariants().remove(BlockFamily.Variant.CHISELED);
		return copy;
	}

	@Override
	protected void buildRecipes() {
		XKDBlockFamilies.getAllFamilies().forEach(family -> {
			if (family.getBaseBlock().asItem() == Items.AIR) {
				return;
			}
			generateRecipes(withoutUnsupportedChiseled(family), FeatureFlags.VANILLA_SET);
		});

		coloredTiles("black", Items.BLACK_TERRACOTTA);
		coloredTiles("cyan", Items.CYAN_TERRACOTTA);
		coloredTiles("yellow", Items.YELLOW_TERRACOTTA);
		coloredTiles("blue", Items.BLUE_TERRACOTTA);
		coloredTiles("green", Items.GREEN_TERRACOTTA);
		coloredTiles("red", Items.RED_TERRACOTTA);

		Ingredient copperBlock = Ingredient.of(
				Items.COPPER_BLOCK,
				Items.WAXED_COPPER_BLOCK,
				Items.EXPOSED_COPPER,
				Items.WAXED_EXPOSED_COPPER,
				Items.WEATHERED_COPPER,
				Items.WAXED_WEATHERED_COPPER,
				Items.OXIDIZED_COPPER,
				Items.WAXED_OXIDIZED_COPPER);
		SingleItemRecipeBuilder.stonecutting(copperBlock, BUILDING_BLOCKS, i("copper_tiles"), 1)
				.unlockedBy("has_item", has(Items.COPPER_INGOT))
				.save(output, "copper_tiles_from_copper_block");

		shapedSurroundedBy4(BUILDING_BLOCKS, i("mud_wall_block"), Items.CLAY, Items.BONE_MEAL, 4);
		shaped(BUILDING_BLOCKS, i("cyan_bricks"), 6)
				.pattern("TB")
				.pattern("BT")
				.define('B', Items.BRICKS)
				.define('T', Items.CYAN_TERRACOTTA)
				.unlockedBy("has_item", has(Items.BRICKS))
				.save(output);
		shaped(BUILDING_BLOCKS, i("black_bricks"), 6)
				.pattern("TB")
				.pattern("BT")
				.define('B', Items.BRICKS)
				.define('T', Items.BLACK_TERRACOTTA)
				.unlockedBy("has_item", has(Items.BRICKS))
				.save(output);

		for (String s : List.of("_log", "_wood", "_planks")) {
			smokingRecipe(i("ebony" + s), i("minecraft:dark_oak" + s));
			smokingRecipe(i("mahogany" + s), i("minecraft:mangrove" + s));
			smokingRecipe(i("varnished" + s), i("minecraft:oak" + s));
		}
		woodFromLogs(i("ebony_wood"), i("ebony_log"));
		woodFromLogs(i("mahogany_wood"), i("mahogany_log"));
		woodFromLogs(i("varnished_wood"), i("varnished_log"));
		planksFromLogs(i("ebony_planks"), EBONY_LOGS, 4);
		planksFromLogs(i("mahogany_planks"), MAHOGANY_LOGS, 4);
		planksFromLogs(i("varnished_planks"), VARNISHED_LOGS, 4);

		shapelessTwoToOne(BUILDING_BLOCKS,
				i("mossy_deepslate_bricks"),
				Items.DEEPSLATE_BRICKS,
				Items.MOSS_BLOCK,
				1,
				true);
		shapelessTwoToOne(BUILDING_BLOCKS,
				i("mossy_deepslate_bricks"),
				Items.DEEPSLATE_BRICKS,
				Items.VINE,
				1,
				true);
		Item mayaStone = i("maya_stone");
		shaped(BUILDING_BLOCKS, mayaStone, 6)
				.pattern("TB")
				.pattern("BT")
				.define('B', Items.ANDESITE)
				.define('T', Items.GRANITE)
				.unlockedBy(getHasName(Items.ANDESITE), has(Items.ANDESITE))
				.unlockedBy(getHasName(Items.GRANITE), has(Items.GRANITE))
				.save(output);
		shaped(BUILDING_BLOCKS, i("inca_stone"), 4)
				.pattern("TB")
				.pattern("BT")
				.define('B', mayaStone)
				.define('T', Items.GRANITE)
				.unlockedBy(getHasName(mayaStone), has(mayaStone))
				.unlockedBy(getHasName(Items.GRANITE), has(Items.GRANITE))
				.save(output);
		shaped(BUILDING_BLOCKS, i("bronze_block"), 4)
				.pattern("TB")
				.pattern("BT")
				.define('B', Items.IRON_NUGGET)
				.define('T', Items.COPPER_INGOT)
				.unlockedBy(getHasName(Items.IRON_NUGGET), has(Items.IRON_NUGGET))
				.unlockedBy(getHasName(Items.COPPER_INGOT), has(Items.COPPER_INGOT))
				.save(output);

		SimpleCookingRecipeBuilder.blasting(Ingredient.of(Items.IRON_BLOCK), BUILDING_BLOCKS, CookingBookCategory.BLOCKS, i("steel_block"), 0.1f, 100)
				.unlockedBy("has_item", has(Items.IRON_BLOCK))
				.save(output);
		shapelessTwoToOne(BUILDING_BLOCKS, i("translucent_lamp_block"), i("tech_lamp_block"), Items.GLASS, 2, false);

		stonecutterResultFromBase(BUILDING_BLOCKS, i("steel_filings"), i("steel_block"), 4);
		stonecutterResultFromBase(BUILDING_BLOCKS, i("quartz_sand"), Items.QUARTZ_BLOCK, 4);
		shapelessTwoToOne(BUILDING_BLOCKS, i("toughened_sand"), i("steel_filings"), i("quartz_sand"), 4, false);
		smeltingResultFromBase(i("quartz_glass"), i("quartz_sand"));
		smeltingResultFromBase(i("toughened_glass"), i("toughened_sand"));

		stonecutterResultFromBase(BUILDING_BLOCKS, i("quartz_wall"), Items.QUARTZ_BLOCK, 1);
		stonecutterResultFromBase(BUILDING_BLOCKS, i("quartz_wall"), Items.QUARTZ_PILLAR, 1);

		stonecutterResultFromBase(BUILDING_BLOCKS, i("dirt_slab"), Items.DIRT, 2);
		stonecutterResultFromBase(BUILDING_BLOCKS, i("dirt_path_slab"), Items.DIRT, 2);
		stonecutterResultFromBase(BUILDING_BLOCKS, i("grass_block_slab"), Items.DIRT, 2);
		stonecutterResultFromBase(BUILDING_BLOCKS, i("mycelium_slab"), Items.DIRT, 2);
		stonecutterResultFromBase(BUILDING_BLOCKS, i("podzol_slab"), Items.DIRT, 2);
		stonecutterResultFromBase(BUILDING_BLOCKS, i("netherrack_slab"), Items.NETHERRACK, 2);
		stonecutterResultFromBase(BUILDING_BLOCKS, i("crimson_nylium_slab"), Items.NETHERRACK, 2);
		stonecutterResultFromBase(BUILDING_BLOCKS, i("warped_nylium_slab"), Items.NETHERRACK, 2);
		stonecutterResultFromBase(BUILDING_BLOCKS, i("end_stone_slab"), Items.END_STONE, 2);

		shapelessTwoToOne(BUILDING_BLOCKS, i("dirt_cobblestone"), Items.DIRT, Items.COBBLESTONE, 1, false);
		shapelessTwoToOne(BUILDING_BLOCKS, i("grass_cobblestone"), Items.SHORT_GRASS, Items.COBBLESTONE, 1, false);
		shapelessTwoToOne(BUILDING_BLOCKS, i("sandy_cobblestone"), Items.SAND, Items.COBBLESTONE, 1, false);
		shapelessTwoToOne(BUILDING_BLOCKS, i("snowy_cobblestone"), Items.SNOW_BLOCK, Items.COBBLESTONE, 1, false);

		shapedSurroundedBy8(BUILDING_BLOCKS, i("ginkgo_leaves"), Items.YELLOW_DYE, Items.OAK_LEAVES, 8);
		shapedSurroundedBy8(BUILDING_BLOCKS, i("orange_maple_leaves"), Items.ORANGE_DYE, Items.OAK_LEAVES, 8);
		shapedSurroundedBy8(BUILDING_BLOCKS, i("red_maple_leaves"), Items.RED_DYE, Items.OAK_LEAVES, 8);
		shapedSurroundedBy8(BUILDING_BLOCKS, i("peach_blossom"), Items.RED_DYE, Items.CHERRY_LEAVES, 8);
		shapedSurroundedBy8(BUILDING_BLOCKS, i("cherry_blossom"), Items.PINK_DYE, Items.CHERRY_LEAVES, 8);
		shapedSurroundedBy8(BUILDING_BLOCKS, i("white_cherry_blossom"), Items.WHITE_DYE, Items.CHERRY_LEAVES, 8);
		shapedSurroundedBy8(BUILDING_BLOCKS, i("plantable_leaves"), Items.DIRT, Items.OAK_LEAVES, 8);
		shapedSurroundedBy8(BUILDING_BLOCKS, i("plantable_leaves_dark"), Items.DIRT, Items.DARK_OAK_LEAVES, 8);
		shapelessTwoToOne(BUILDING_BLOCKS, i("peach_blossom_leaves"), i("peach_blossom"), Items.OAK_LEAVES, 2, false);
		shapelessTwoToOne(BUILDING_BLOCKS, i("cherry_blossom_leaves"), i("cherry_blossom"), Items.OAK_LEAVES, 2, false);
		shapelessTwoToOne(BUILDING_BLOCKS,
				i("white_cherry_blossom_leaves"),
				i("white_cherry_blossom"),
				Items.OAK_LEAVES,
				2,
				false);
		fallenLeaves(i("fallen_ginkgo_leaves"), i("ginkgo_leaves"));
		fallenLeaves(i("fallen_orange_maple_leaves"), i("orange_maple_leaves"));
		fallenLeaves(i("fallen_red_maple_leaves"), i("red_maple_leaves"));
		fallenLeaves(i("fallen_peach_blossom"), i("peach_blossom"));
		fallenLeaves(i("fallen_cherry_blossom"), i("cherry_blossom"));
		fallenLeaves(i("fallen_white_cherry_blossom"), i("white_cherry_blossom"));

		shaped(DECORATIONS, i("factory_lamp"), 4)
				.pattern(" G ")
				.pattern("GDG")
				.pattern(" S ")
				.define('G', Items.GLASS_PANE)
				.define('S', i("steel_tile_slab"))
				.define('D', Items.GLOWSTONE_DUST)
				.unlockedBy("has_item", has(Items.GLOWSTONE_DUST))
				.save(output);

		stonecutterResultFromBase(DECORATIONS, i("item_frame_cover"), Items.GLASS);
		shapeless(DECORATIONS, i("glow_item_frame_cover"))
				.requires(i("item_frame_cover"))
				.requires(Items.GLOWSTONE_DUST)
				.unlockedBy("has_item", has(i("item_frame_cover")))
				.save(output);

		shaped(DECORATIONS, i("plain_item_display"))
				.pattern(" F ")
				.pattern("SSS")
				.pattern(" B ")
				.define('F', Items.ITEM_FRAME)
				.define('S', Items.SMOOTH_STONE_SLAB)
				.define('B', Items.SMOOTH_STONE)
				.unlockedBy("has_item", has(Items.ITEM_FRAME))
				.save(output);
		shaped(DECORATIONS, i("plain_block_display"))
				.pattern(" F ")
				.pattern("SSS")
				.pattern("B B")
				.define('F', Items.ITEM_FRAME)
				.define('S', Items.SMOOTH_STONE_SLAB)
				.define('B', Items.SMOOTH_STONE)
				.unlockedBy("has_item", has(Items.ITEM_FRAME))
				.save(output);
		shaped(DECORATIONS, i("item_projector"))
				.pattern("A")
				.pattern("B")
				.define('A', Items.SPYGLASS)
				.define('B', i("tech_item_display"))
				.unlockedBy("has_item", has(Items.ITEM_FRAME))
				.save(output);
		shaped(DECORATIONS, i("maya_crystal_skull"))
				.pattern(" A ")
				.pattern("ASA")
				.pattern(" M ")
				.define('A', Items.AMETHYST_SHARD)
				.define('S', Items.SKELETON_SKULL)
				.define('M', i("maya_stone_slab"))
				.unlockedBy("has_item", has(Items.SKELETON_SKULL))
				.save(output);

		miniature("tree", Items.SPRUCE_SAPLING);
		miniature("cherry", Items.CHERRY_SAPLING);
		miniature("ginkgo", i("ginkgo_leaves"));
		miniature("maple", i("red_maple_leaves"));
		miniature("bamboo", Items.BAMBOO);
		miniature("coral", Items.DEAD_FIRE_CORAL);
		miniature("red_coral", Items.FIRE_CORAL);
		miniature("mount", Items.MOSSY_COBBLESTONE);
		miniature("succulents", Items.CACTUS);

		stonecutterResultFromBase(DECORATIONS, i("teapot"), Items.TERRACOTTA);
		stonecutterResultFromBase(DECORATIONS, i("cup"), Items.TERRACOTTA, 1);
		shapedSurroundedBy4(DECORATIONS, i("tea_ware"), i("cup"), i("teapot"), 4);
		shaped(DECORATIONS, i("refreshments"))
				.pattern("ABA")
				.pattern("BCB")
				.pattern(" _ ")
				.define('A', Items.COOKIE)
				.define('B', Items.PUMPKIN_PIE)
				.define('C', Items.CAKE)
				.define('_', Items.STONE_PRESSURE_PLATE)
				.unlockedBy("has_item", has(Items.CAKE))
				.save(output);
		shaped(DECORATIONS, i("fruit_platter"))
				.pattern("ABC")
				.pattern("DDD")
				.pattern(" _ ")
				.define('A', Items.SWEET_BERRIES)
				.define('B', Items.GLOW_BERRIES)
				.define('C', Items.APPLE)
				.define('D', Items.MELON_SLICE)
				.define('_', Items.STONE_PRESSURE_PLATE)
				.unlockedBy("has_item", has(Items.MELON_SLICE))
				.save(output);
		shaped(DECORATIONS, i("calligraphy"))
				.pattern(" F ")
				.pattern(" I ")
				.pattern("PPP")
				.define('F', Items.FEATHER)
				.define('I', Items.INK_SAC)
				.define('P', Items.PAPER)
				.unlockedBy("has_item", has(Items.PAPER))
				.save(output);
		shaped(DECORATIONS, i("ink_painting"))
				.pattern(" F ")
				.pattern("RIG")
				.pattern("PPP")
				.define('F', Items.FEATHER)
				.define('I', Items.INK_SAC)
				.define('P', Items.PAPER)
				.define('R', Items.RED_DYE)
				.define('G', Items.GREEN_DYE)
				.unlockedBy("has_item", has(Items.PAPER))
				.save(output);
		shaped(DECORATIONS, i("weiqi_board"))
				.pattern("AAA")
				.pattern("BBB")
				.pattern(" C ")
				.define('A', Items.BIRCH_BUTTON)
				.define('B', Items.DARK_OAK_BUTTON)
				.define('C', Items.OAK_PRESSURE_PLATE)
				.unlockedBy("has_item", has(ItemTags.WOODEN_BUTTONS))
				.save(output);
		shaped(DECORATIONS, i("xiangqi_board"))
				.pattern("AAA")
				.pattern("BBB")
				.pattern(" C ")
				.define('A', Items.MANGROVE_BUTTON)
				.define('B', Items.DARK_OAK_BUTTON)
				.define('C', Items.SPRUCE_PRESSURE_PLATE)
				.unlockedBy("has_item", has(ItemTags.WOODEN_BUTTONS))
				.save(output);
		shapedSurroundedBy4(DECORATIONS, i("paper_lantern"), Items.LANTERN, Items.PAPER, 1);
		shapelessTwoToOne(DECORATIONS, i("red_lantern"), i("paper_lantern"), Items.RED_DYE, 1, false);
		shapeless(DECORATIONS, i("festival_lantern"))
				.requires(i("paper_lantern"))
				.requires(Items.RED_DYE)
				.requires(Items.YELLOW_DYE)
				.unlockedBy("has_item", has(i("paper_lantern")))
				.save(output);
		shaped(DECORATIONS, i("oil_lamp"))
				.pattern("NTN")
				.pattern(" N ")
				.define('N', Items.IRON_NUGGET)
				.define('T', Items.TORCH)
				.unlockedBy("has_item", has(Items.TORCH))
				.save(output);
		shaped(DECORATIONS, i("candlestick"))
				.pattern("NCN")
				.pattern(" N ")
				.define('N', Items.IRON_NUGGET)
				.define('C', Items.CANDLE)
				.unlockedBy("has_item", has(Items.CANDLE))
				.save(output);
		shaped(DECORATIONS, i("big_candlestick"))
				.pattern("CCC")
				.pattern("CIC")
				.pattern(" N ")
				.define('N', Items.IRON_NUGGET)
				.define('C', Items.CANDLE)
				.define('I', Items.IRON_INGOT)
				.unlockedBy("has_item", has(Items.CANDLE))
				.save(output);
		shaped(DECORATIONS, i("empty_candlestick"), 4)
				.pattern("I")
				.pattern("N")
				.pattern("I")
				.define('N', Items.IRON_NUGGET)
				.define('I', Items.IRON_INGOT)
				.unlockedBy("has_item", has(Items.IRON_INGOT))
				.save(output);
		shaped(DECORATIONS, i("covered_lamp"))
				.pattern("M")
				.pattern("C")
				.define('M', Items.MANGROVE_ROOTS)
				.define('C', i("candlestick"))
				.unlockedBy("has_item", has(i("candlestick")))
				.save(output);
		shaped(DECORATIONS, i("roofed_lamp"))
				.pattern("R")
				.pattern("L")
				.pattern("P")
				.define('R', i("black_roof_ridge"))
				.define('L', Items.REDSTONE_LAMP)
				.define('P', Items.STONE_PRESSURE_PLATE)
				.unlockedBy("has_item", has(Items.REDSTONE_LAMP))
				.save(output);
		stoneLamp(i("stone_lamp"), Items.STONE_BRICKS);
		stoneLamp(i("deepslate_lamp"), Items.DEEPSLATE_BRICKS);
		stoneLamp(i("blackstone_lamp"), Items.POLISHED_BLACKSTONE_BRICKS);
		shaped(DECORATIONS, i("fish_bowl"))
				.pattern("L S")
				.pattern("LFS")
				.pattern("TTT")
				.define('L', Items.LILY_PAD)
				.define('S', Items.STICK)
				.define('F', Items.TROPICAL_FISH_BUCKET)
				.define('T', Items.WHITE_TERRACOTTA)
				.unlockedBy("has_item", has(Items.TROPICAL_FISH_BUCKET))
				.save(output);
		shaped(DECORATIONS, i("dark_fish_bowl"))
				.pattern("L S")
				.pattern("LFS")
				.pattern("TTT")
				.define('L', Items.LILY_PAD)
				.define('S', Items.STICK)
				.define('F', Items.TROPICAL_FISH_BUCKET)
				.define('T', Items.BLACK_TERRACOTTA)
				.unlockedBy("has_item", has(Items.TROPICAL_FISH_BUCKET))
				.save(output);
		shaped(DECORATIONS, i("stone_water_bowl"))
				.pattern("W")
				.pattern("S")
				.define('W', Items.WATER_BUCKET)
				.define('S', Items.STONE)
				.unlockedBy("has_item", has(Items.WATER_BUCKET))
				.save(output);
		shaped(DECORATIONS, i("stone_water_tank"))
				.pattern("W")
				.pattern("S")
				.pattern("S")
				.define('W', Items.WATER_BUCKET)
				.define('S', Items.STONE)
				.unlockedBy("has_item", has(Items.WATER_BUCKET))
				.save(output);
		shaped(DECORATIONS, i("empty_fish_tank"))
				.pattern("G G")
				.pattern("GGG")
				.define('G', Items.GLASS_PANE)
				.unlockedBy("has_item", has(Items.GLASS_PANE))
				.save(output);
		Ingredient coralPlants = Ingredient.of(
				Items.BRAIN_CORAL,
				Items.BUBBLE_CORAL,
				Items.FIRE_CORAL,
				Items.HORN_CORAL,
				Items.TUBE_CORAL);
		shaped(DECORATIONS, i("fish_tank"))
				.pattern("GFG")
				.pattern("NFP")
				.pattern("STC")
				.define('G', Items.SEAGRASS)
				.define('F', Items.TROPICAL_FISH_BUCKET)
				.define('N', Items.NAUTILUS_SHELL)
				.define('P', coralPlants)
				.define('S', Items.SAND)
				.define('T', i("empty_fish_tank"))
				.define('C', Items.COBBLESTONE)
				.unlockedBy("has_item", has(i("empty_fish_tank")))
				.save(output);
		twoByTwoPacker(DECORATIONS, i("small_book_stack"), Items.BOOK);
		twoByTwoPacker(DECORATIONS, i("big_book_stack"), i("small_book_stack"));
		twoByTwoPacker(DECORATIONS, i("empty_bottle_stack"), Items.GLASS_BOTTLE);
		twoByTwoPacker(DECORATIONS, i("bottle_stack"), Items.POTION);
		shaped(DECORATIONS, i("wood_globe"))
				.pattern("  S")
				.pattern(" PS")
				.pattern("HHH")
				.define('S', Items.STICK)
				.define('P', ItemTags.PLANKS)
				.define('H', ItemTags.WOODEN_SLABS)
				.unlockedBy("back_to_overworld", ChangeDimensionTrigger.TriggerInstance.changedDimension(Level.END, Level.OVERWORLD))
				.save(output);
		shaped(DECORATIONS, i("globe"))
				.pattern(" W ")
				.pattern("B*G")
				.pattern(" Y ")
				.define('W', Items.WHITE_DYE)
				.define('B', Items.BLUE_DYE)
				.define('G', Items.GREEN_DYE)
				.define('Y', Items.YELLOW_DYE)
				.define('*', i("wood_globe"))
				.unlockedBy("has_item", has(i("wood_globe")))
				.save(output);
		shaped(DECORATIONS, i("solar_system_model"))
				.pattern("PBP")
				.pattern("BGB")
				.pattern("PBP")
				.define('P', ItemTags.PLANKS)
				.define('B', ItemTags.WOODEN_BUTTONS)
				.define('G', Items.GLOWSTONE)
				.unlockedBy("has_item", has(Items.GLOWSTONE))
				.save(output);
		shaped(DECORATIONS, i("telescope"))
				.pattern(" S ")
				.pattern(" P ")
				.pattern("HHH")
				.define('S', Items.SPYGLASS)
				.define('P', ItemTags.PLANKS)
				.define('H', ItemTags.WOODEN_SLABS)
				.unlockedBy("has_item", has(Items.SPYGLASS))
				.save(output);
		shapedSurroundedBy4(DECORATIONS, i("fan_blade"), Items.LIGHTNING_ROD, Items.HEAVY_WEIGHTED_PRESSURE_PLATE, 1);
		shapedSurroundedBy8(DECORATIONS, i("factory_vent_fan"), i("fan_blade"), Items.IRON_BARS, 1);
		twoByTwoPacker(DECORATIONS, i("factory_vent_fan_big"), i("factory_vent_fan"));
		shapedSurroundedBy4(DECORATIONS, i("steel_windmill"), i("steel_block"), i("steel_trapdoor"), 1);
		shapedSurroundedBy4(DECORATIONS, i("iron_windmill"), Items.IRON_BLOCK, Items.IRON_TRAPDOOR, 1);
		shapedSurroundedBy4(DECORATIONS, i("wooden_windmill"), Items.STRIPPED_OAK_LOG, Items.OAK_TRAPDOOR, 1);
		shapedSurroundedBy4(DECORATIONS, i("screen_off"), i("tech_screen"), Items.IRON_NUGGET, 1);
		stonecutterResultFromBase(DECORATIONS, i("hologram_base"), i("screen_off"), 4);
		shaped(DECORATIONS, i("tech_table"))
				.pattern(" S ")
				.pattern("III")
				.pattern(" I ")
				.define('S', i("screen_off"))
				.define('I', Items.IRON_INGOT)
				.unlockedBy("has_item", has(i("screen_off")))
				.save(output);
		shapeless(DECORATIONS, i("sign_entrance"))
				.requires(ItemTags.SIGNS)
				.requires(Items.GLOW_INK_SAC)
				.unlockedBy("has_item", has(Items.GLOW_INK_SAC))
				.save(output);
		shapeless(DECORATIONS, i("emergency_exit"))
				.requires(ItemTags.SIGNS)
				.requires(Items.GLOW_INK_SAC)
				.requires(Items.GREEN_DYE)
				.unlockedBy("has_item", has(Items.GLOW_INK_SAC))
				.save(output);

		shaped(DECORATIONS, i("tech_screen"))
				.pattern("GGG")
				.pattern("GGG")
				.define('G', Items.BLUE_STAINED_GLASS_PANE)
				.unlockedBy("has_item", has(Items.BLUE_STAINED_GLASS_PANE))
				.save(output);
		shaped(DECORATIONS, i("tech_console"))
				.pattern("NSN")
				.pattern("III")
				.define('N', Items.IRON_NUGGET)
				.define('S', i("tech_screen"))
				.define('I', Items.IRON_INGOT)
				.unlockedBy("has_item", has(i("tech_screen")))
				.save(output);
		shaped(DECORATIONS, i("tech_chair"))
				.pattern("I  ")
				.pattern("IWI")
				.pattern("INI")
				.define('N', Items.IRON_NUGGET)
				.define('I', Items.IRON_INGOT)
				.define('W', Items.BLUE_WOOL)
				.unlockedBy("has_item", has(Items.BLUE_WOOL))
				.save(output);

		shaped(DECORATIONS, i("mechanical_screen"))
				.pattern("BGL")
				.pattern("BGW")
				.define('B', ItemTags.WOODEN_BUTTONS)
				.define('G', Items.GLASS_PANE)
				.define('L', i("factory_lamp"))
				.define('W', i("factory_warning_lamp"))
				.unlockedBy("has_item", has(i("factory_lamp")))
				.save(output);
		shaped(DECORATIONS, i("mechanical_console"))
				.pattern("LLW")
				.pattern("BBT")
				.pattern("HHH")
				.define('L', i("factory_lamp"))
				.define('W', i("factory_warning_lamp"))
				.define('B', ItemTags.WOODEN_BUTTONS)
				.define('T', Items.LEVER)
				.define('H', ItemTags.WOODEN_SLABS)
				.unlockedBy("has_item", has(i("factory_lamp")))
				.save(output);
		shaped(DECORATIONS, i("mechanical_chair"))
				.pattern("H  ")
				.pattern("HWH")
				.pattern("HBH")
				.define('B', ItemTags.WOODEN_BUTTONS)
				.define('H', ItemTags.WOODEN_SLABS)
				.define('W', Items.RED_WOOL)
				.unlockedBy("has_item", has(Items.RED_WOOL))
				.save(output);
	}

	private void stoneLamp(ItemLike lamp, ItemLike material) {
		shaped(DECORATIONS, lamp)
				.pattern("S")
				.pattern("G")
				.pattern("S")
				.define('S', material)
				.define('G', Items.GLOWSTONE)
				.unlockedBy("has_item", has(Items.GLOWSTONE))
				.save(output);
	}

	private void miniature(String result, ItemLike material) {
		shaped(RecipeCategory.DECORATIONS, i("miniature_%s".formatted(result)))
				.pattern("A")
				.pattern("B")
				.pattern("C")
				.define('A', material)
				.define('B', Items.FLOWER_POT)
				.define('C', ItemTags.WOODEN_SLABS)
				.unlockedBy("has_item", has(material))
				.save(output);
	}

	private void fallenLeaves(ItemLike pCarpet, ItemLike pMaterial) {
		shaped(RecipeCategory.DECORATIONS, pCarpet, 3)
				.define('#', pMaterial)
				.pattern("##")
				.group("fallen_leaves")
				.unlockedBy(getHasName(pMaterial), has(pMaterial))
				.save(output);
	}

	private void shapedSurroundedBy8(			RecipeCategory category,
			ItemLike result,
			Item middle,
			ItemLike surround,
			int count) {
		shaped(category, result, count)
				.pattern("SSS")
				.pattern("SMS")
				.pattern("SSS")
				.define('S', surround)
				.define('M', middle)
				.unlockedBy("has_item", has(surround))
				.save(output);
	}

	private void shapedSurroundedBy4(			RecipeCategory category,
			ItemLike result,
			Item middle,
			ItemLike surround,
			int count) {
		shaped(category, result, count)
				.pattern(" S ")
				.pattern("SMS")
				.pattern(" S ")
				.define('S', surround)
				.define('M', middle)
				.unlockedBy("has_item", has(surround))
				.save(output);
	}

	private void shapelessTwoToOne(			RecipeCategory category,
			ItemLike result,
			ItemLike input1,
			ItemLike input2,
			int resultCount,
			boolean longName) {
		String name = BuiltInRegistries.ITEM.getKey(result.asItem()).getPath();
		if (longName) {
			name += "_from_" + BuiltInRegistries.ITEM.getKey(input2.asItem()).getPath();
		}
		shapeless(category, result, resultCount)
				.requires(input1)
				.requires(input2)
				.unlockedBy("has_item", has(input1))
				.save(output, name);
	}

	private void smokingRecipe(ItemLike result, ItemLike material) {
		SimpleCookingRecipeBuilder.smoking(Ingredient.of(material), BUILDING_BLOCKS, result, 0.1f, 100)
				.unlockedBy("has_item", has(material))
				.save(output, BuiltInRegistries.ITEM.getKey(result.asItem()).getPath() + "_from_smoking");
	}

	private void coloredTiles(String color, ItemLike terracotta) {
		shaped(BUILDING_BLOCKS, i(color + "_tiles"), 8)
				.pattern("TTT")
				.pattern("WWW")
				.define('T', terracotta)
				.define('W', ItemTags.PLANKS)
				.unlockedBy("has_item", has(terracotta))
				.save(output);
	}

	private static Item i(String id) {
		Identifier resourceLocation;
		if (id.contains(":")) {
			resourceLocation = Identifier.parse(id);
		} else {
			resourceLocation = XKDeco.id(id);
		}
		return BuiltInRegistries.ITEM.getOptional(resourceLocation).orElseThrow();
	}

	public static class Runner extends FabricRecipeProvider {
		public Runner(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
			super(output, registriesFuture);
		}

		@Override
		protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
			return new XKDRecipeProvider(registries, output);
		}

		@Override
		public String getName() {
			return "XKDeco Recipes";
		}
	}
}
