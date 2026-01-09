package org.teacon.xkdeco.compat.jei;

import java.util.List;

import org.teacon.xkdeco.XKDeco;
import org.teacon.xkdeco.init.MimicWallsLoader;
import org.teacon.xkdeco.recipe.MimicWallRecipe;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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
		registration.getCraftingCategory().addCategoryExtension(
				MimicWallRecipe.class, recipe -> (builder, craftingGridHelper, focuses) -> {
					craftingGridHelper.createAndSetOutputs(
							builder,
							MimicWallsLoader.mimicWalls().stream().map(Block::asItem).map(Item::getDefaultInstance).toList());
					List<List<ItemStack>> inputs = recipe.getIngredients().stream()
							.map(ingredient -> List.of(ingredient.getItems()))
							.toList();
					craftingGridHelper.createAndSetInputs(builder, inputs, 0, 0);
				});
	}
}
