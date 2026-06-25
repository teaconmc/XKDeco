package org.teacon.xkdeco.init;

import org.teacon.xkdeco.recipe.MimicWallRecipe;

import net.minecraft.world.item.crafting.RecipeSerializer;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.KiwiGO;
import snownee.kiwi.KiwiModule;

@KiwiModule("recipes")
public class XKDecoRecipes extends AbstractModule {
	public static final KiwiGO<RecipeSerializer<MimicWallRecipe>> MIMIC_WALL = go(
			() -> new RecipeSerializer<>(MimicWallRecipe.Serializer.CODEC, MimicWallRecipe.Serializer.STREAM_CODEC));
}
