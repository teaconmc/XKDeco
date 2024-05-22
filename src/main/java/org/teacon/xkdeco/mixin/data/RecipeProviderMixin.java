package org.teacon.xkdeco.mixin.data;

import java.util.function.Consumer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.data.BlockFamily;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.level.block.Block;

@Mixin(RecipeProvider.class)
public class RecipeProviderMixin {
	@Inject(method = {"lambda$generateRecipes$23", "m_176526_"}, at = @At("HEAD"), cancellable = true)
	private static void xkdeco$generateRecipes(
			BlockFamily pFamily,
			Consumer<FinishedRecipe> pFinishedRecipeConsumer,
			BlockFamily.Variant variant,
			Block block,
			CallbackInfo ci) {
		//noinspection ConstantValue
		if (variant == BlockFamily.Variant.CHISELED && pFamily.get(BlockFamily.Variant.SLAB) == null) {
			ci.cancel();
		}
	}
}