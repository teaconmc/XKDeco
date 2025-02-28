package org.teacon.xkdeco.compat.jei;

import org.teacon.xkdeco.XKDeco;
import org.teacon.xkdeco.init.MimicWallsLoader;
import org.teacon.xkdeco.recipe.MimicWallRecipe;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.Block;
import snownee.kiwi.util.NotNullByDefault;

@NotNullByDefault
public class XKDecoJEIPlugin implements IModPlugin {
	public static final ResourceLocation ID = XKDeco.id("main");

	@Override
	public ResourceLocation getPluginUid() {
		return ID;
	}

	@Override
	public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
		registration.getCraftingCategory().addExtension(
				MimicWallRecipe.class, new ICraftingCategoryExtension<>() {
					@Override
					public void setRecipe(
							RecipeHolder<MimicWallRecipe> recipeHolder,
							IRecipeLayoutBuilder builder,
							ICraftingGridHelper craftingGridHelper,
							IFocusGroup focuses) {
						MimicWallRecipe recipe = recipeHolder.value();
						craftingGridHelper.createAndSetOutputs(
								builder,
								MimicWallsLoader.MIMIC_WALLS.stream().map(Block::asItem).map(Item::getDefaultInstance).toList());
						craftingGridHelper.createAndSetIngredients(builder, recipe.getIngredients(), 0, 0);
					}
				});
	}
}
