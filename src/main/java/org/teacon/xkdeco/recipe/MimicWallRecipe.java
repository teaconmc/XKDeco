package org.teacon.xkdeco.recipe;

import org.teacon.xkdeco.block.MimicWallBlock;
import org.teacon.xkdeco.init.MimicWallsLoader;
import org.teacon.xkdeco.init.XKDecoRecipes;

import com.google.gson.JsonObject;

import net.minecraft.Util;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.block.Block;
import snownee.kiwi.util.NotNullByDefault;

@NotNullByDefault
public class MimicWallRecipe extends ShapelessRecipe {
	private final Ingredient another;

	public MimicWallRecipe(ResourceLocation id, String group, CraftingBookCategory category, Ingredient another) {
		super(
				id, group, category, ItemStack.EMPTY, Util.make(
						NonNullList.create(), $ -> {
							$.add(makeWallIngredient());
							$.add(another);
						}));
		this.another = another;
	}

	public static Ingredient makeWallIngredient() {
		return Ingredient.of(MimicWallsLoader.mimicWalls().stream()
				.map(MimicWallBlock::getWallDelegate)
				.map(Block::asItem)
				.toArray(Item[]::new));
	}

	@Override
	public boolean isSpecial() {
		return true;
	}

	@Override
	public ItemStack assemble(CraftingContainer input, RegistryAccess registries) {
		for (ItemStack itemStack : input.getItems()) {
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
	public RecipeSerializer<?> getSerializer() {
		return XKDecoRecipes.MIMIC_WALL.get();
	}

	public static class Serializer implements RecipeSerializer<MimicWallRecipe> {
		@Override
		public MimicWallRecipe fromJson(ResourceLocation pRecipeId, JsonObject pJson) {
			String group = GsonHelper.getAsString(pJson, "group", "");
			CraftingBookCategory category = CraftingBookCategory.CODEC.byName(
					GsonHelper.getAsString(pJson, "category", null),
					CraftingBookCategory.MISC);
			Ingredient another = Ingredient.fromJson(GsonHelper.getAsJsonObject(pJson, "another"));
			return new MimicWallRecipe(pRecipeId, group, category, another);
		}

		@Override
		public MimicWallRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
			String group = pBuffer.readUtf();
			CraftingBookCategory category = pBuffer.readEnum(CraftingBookCategory.class);
			Ingredient another = Ingredient.fromNetwork(pBuffer);
			return new MimicWallRecipe(pRecipeId, group, category, another);
		}

		@Override
		public void toNetwork(FriendlyByteBuf pBuffer, MimicWallRecipe pRecipe) {
			pBuffer.writeUtf(pRecipe.getGroup());
			pBuffer.writeEnum(pRecipe.category());
			pRecipe.another.toNetwork(pBuffer);
		}
	}
}
