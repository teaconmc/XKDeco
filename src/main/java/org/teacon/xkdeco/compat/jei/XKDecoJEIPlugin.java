package org.teacon.xkdeco.compat.jei;

import org.teacon.xkdeco.XKDeco;
import org.teacon.xkdeco.recipe.MimicWallRecipe;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.display.SlotDisplay;

public class XKDecoJEIPlugin implements IModPlugin {
	public static final Identifier ID = XKDeco.id("main");

	@Override
	public Identifier getPluginUid() {
		return ID;
	}

	@Override
	public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
		registration.getCraftingCategory().addExtension(
				MimicWallRecipe.class, new ICraftingCategoryExtension<>() {
					@Override
					public java.util.List<SlotDisplay> getIngredients(RecipeHolder<MimicWallRecipe> recipeHolder) {
						return recipeHolder.value().getIngredientDisplays();
					}

					@Override
					public void setRecipe(
							RecipeHolder<MimicWallRecipe> recipeHolder,
							IRecipeLayoutBuilder builder,
							ICraftingGridHelper craftingGridHelper,
							IFocusGroup focuses) {
						MimicWallRecipe recipe = recipeHolder.value();
						craftingGridHelper.createAndSetOutputs(
								builder, recipe.getResultDisplay());
						craftingGridHelper.createAndSetIngredientsFromDisplays(builder, getIngredients(recipeHolder), 0, 0);
					}
				});
	}
}
