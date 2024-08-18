package org.teacon.xkdeco.mixin.data;

import java.util.function.Consumer;

import net.minecraft.data.recipes.RecipeOutput;

import net.minecraft.world.flag.FeatureFlagSet;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.data.BlockFamily;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.level.block.Block;

@Mixin(RecipeProvider.class)
public class RecipeProviderMixin {
	@Inject(method = "lambda$generateRecipes$22", at = @At("HEAD"), cancellable = true)
	private static void xkdeco$generateRecipes(
			FeatureFlagSet requiredFeatures,
			BlockFamily blockFamily,
			RecipeOutput recipeOutput,
			BlockFamily.Variant variant,
			Block p_313458_,
			CallbackInfo ci) {
		//noinspection ConstantValue
		if (variant == BlockFamily.Variant.CHISELED && blockFamily.get(BlockFamily.Variant.SLAB) == null) {
			ci.cancel();
		}
	}
}