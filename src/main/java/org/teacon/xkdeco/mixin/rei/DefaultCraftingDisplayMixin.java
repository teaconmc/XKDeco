package org.teacon.xkdeco.mixin.rei;

import java.util.List;
import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.teacon.xkdeco.init.MimicWallsLoader;
import org.teacon.xkdeco.recipe.MimicWallRecipe;

import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.plugin.common.displays.crafting.CraftingDisplay;
import me.shedaniel.rei.plugin.common.displays.crafting.DefaultCraftingDisplay;
import me.shedaniel.rei.plugin.common.displays.crafting.DefaultCustomShapelessDisplay;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.ItemLike;

@Mixin(DefaultCraftingDisplay.class)
public class DefaultCraftingDisplayMixin {
	@Inject(
			method = "of",
			at = @At("HEAD"),
			cancellable = true)
	private static void xkdeco$of(RecipeHolder<? extends Recipe<?>> holder, CallbackInfoReturnable<CraftingDisplay> cir) {
		if (holder.value() instanceof MimicWallRecipe recipe) {
			List<EntryIngredient> inputs = EntryIngredients.ofSlotDisplays(recipe.getIngredientDisplays());
			List<EntryIngredient> outputs = List.of(EntryIngredients.ofItems(MimicWallsLoader.mimicWalls().stream()
					.map($ -> (ItemLike) $)
					.toList()));
			cir.setReturnValue(new DefaultCustomShapelessDisplay(inputs, outputs, Optional.of(holder.id().identifier())));
		}
	}
}
