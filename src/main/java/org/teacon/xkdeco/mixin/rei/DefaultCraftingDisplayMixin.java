package org.teacon.xkdeco.mixin.rei;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.teacon.xkdeco.init.MimicWallsLoader;
import org.teacon.xkdeco.recipe.MimicWallRecipe;

import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.plugin.common.displays.crafting.DefaultCraftingDisplay;
import me.shedaniel.rei.plugin.common.displays.crafting.DefaultCustomShapelessDisplay;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.ItemLike;

@Mixin(DefaultCraftingDisplay.class)
public class DefaultCraftingDisplayMixin {
	@Inject(
			method = "of",
			at = @At(
					value = "INVOKE",
					target = "Lme/shedaniel/rei/plugin/common/displays/crafting/DefaultShapelessDisplay;<init>(Lnet/minecraft/world/item/crafting/RecipeHolder;)V"),
			cancellable = true)
	private static void xkdeco$of(RecipeHolder<? extends Recipe<?>> holder, CallbackInfoReturnable<DefaultCraftingDisplay<?>> cir) {
		if (holder.value() instanceof MimicWallRecipe recipe) {
			List<EntryIngredient> inputs = EntryIngredients.ofIngredients(recipe.getIngredients());
			List<EntryIngredient> outputs = List.of(EntryIngredients.ofItems(MimicWallsLoader.mimicWalls().stream()
					.map($ -> (ItemLike) $)
					.toList()));
			cir.setReturnValue(new DefaultCustomShapelessDisplay(holder, inputs, outputs));
		}
	}
}
