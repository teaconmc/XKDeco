package org.teacon.xkdeco.init;

import org.teacon.xkdeco.recipe.MimicWallRecipe;

import snownee.kiwi.AbstractModule;
import snownee.kiwi.KiwiGO;
import snownee.kiwi.KiwiModule;

@KiwiModule("recipes")
public class XKDecoRecipes extends AbstractModule {
	public static final KiwiGO<MimicWallRecipe.Serializer> MIMIC_WALL = go(MimicWallRecipe.Serializer::new);
}
