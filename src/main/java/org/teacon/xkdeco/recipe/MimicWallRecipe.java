package org.teacon.xkdeco.recipe;

import java.util.List;

import org.teacon.xkdeco.block.MimicWallBlock;
import org.teacon.xkdeco.init.MimicWallsLoader;
import org.teacon.xkdeco.init.XKDecoRecipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.ShapelessCraftingRecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.teacon.xkdeco.util.NotNullByDefault;

@NotNullByDefault
public class MimicWallRecipe extends CustomRecipe {
	private final String group;
	private final CraftingBookCategory category;
	private final Ingredient another;
	private final Ingredient wall;

	public MimicWallRecipe(String group, CraftingBookCategory category, Ingredient another) {
		this.group = group;
		this.category = category;
		this.another = another;
		this.wall = makeWallIngredient();
	}

	public static Ingredient makeWallIngredient() {
		return Ingredient.of(MimicWallsLoader.mimicWalls().stream()
				.map(MimicWallBlock::getWallDelegate)
				.map(Block::asItem));
	}

	public List<Ingredient> getIngredients() {
		return List.of(wall, another);
	}

	public List<SlotDisplay> getIngredientDisplays() {
		return List.of(wall.display(), another.display());
	}

	public SlotDisplay getResultDisplay() {
		return new SlotDisplay.Composite(MimicWallsLoader.mimicWalls().stream()
				.map(Block::asItem)
				.map(item -> (SlotDisplay) new SlotDisplay.ItemSlotDisplay(item))
				.toList());
	}

	@Override
	public String group() {
		return group;
	}

	@Override
	public CraftingBookCategory category() {
		return category;
	}

	@Override
	public boolean matches(CraftingInput input, Level level) {
		if (input.ingredientCount() != 2) {
			return false;
		}
		var nonEmptyItems = new java.util.ArrayList<ItemStack>(2);
		for (ItemStack item : input.items()) {
			if (!item.isEmpty()) {
				nonEmptyItems.add(item);
			}
		}
		return net.neoforged.neoforge.common.util.RecipeMatcher.findMatches(nonEmptyItems, List.of(wall, another)) != null;
	}

	@Override
	public ItemStack assemble(CraftingInput input) {
		for (ItemStack itemStack : input.items()) {
			if (another.test(itemStack)) {
				continue;
			}
			for (MimicWallBlock mimicWall : MimicWallsLoader.mimicWalls()) {
				if (itemStack.is(mimicWall.getWallDelegate().asItem())) {
					return new ItemStack(mimicWall);
				}
			}
		}
		return ItemStack.EMPTY;
	}

	@Override
	public List<RecipeDisplay> display() {
		return List.of(new ShapelessCraftingRecipeDisplay(
				getIngredientDisplays(),
				getResultDisplay(),
				new SlotDisplay.ItemSlotDisplay(Items.CRAFTING_TABLE)));
	}

	@Override
	public RecipeSerializer<MimicWallRecipe> getSerializer() {
		return XKDecoRecipes.MIMIC_WALL.get();
	}

	public static class Serializer {
		public static final MapCodec<MimicWallRecipe> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
				Codec.STRING.optionalFieldOf("group", "").forGetter(MimicWallRecipe::group),
				CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(MimicWallRecipe::category),
				Ingredient.CODEC.fieldOf("another").forGetter($ -> $.another)).apply(i, MimicWallRecipe::new));

		public static final StreamCodec<RegistryFriendlyByteBuf, MimicWallRecipe> STREAM_CODEC = StreamCodec.composite(
				ByteBufCodecs.STRING_UTF8,
				MimicWallRecipe::group,
				CraftingBookCategory.STREAM_CODEC,
				MimicWallRecipe::category,
				Ingredient.CONTENTS_STREAM_CODEC,
				$ -> $.another,
				MimicWallRecipe::new);
	}
}
