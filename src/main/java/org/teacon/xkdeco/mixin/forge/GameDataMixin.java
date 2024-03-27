package org.teacon.xkdeco.mixin.forge;

import java.util.List;
import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.GameData;

@Mixin(value = GameData.class, remap = false)
public class GameDataMixin {
	@Inject(method = "postRegisterEvents", at = @At(value = "INVOKE", target = "Ljava/lang/RuntimeException;<init>()V"))
	private static void postRegisterEvents(CallbackInfo ci, @Local(ordinal = 1) Set<ResourceLocation> ordered) {
		List<ResourceLocation> copy = List.copyOf(ordered);
		ordered.clear();
		List<ResourceLocation> prioritized = List.of(
				new ResourceLocation("kiwi:block_component"),
				new ResourceLocation("kiwi:block_template"));
		ordered.addAll(prioritized);
		ordered.addAll(copy.stream().filter($ -> !prioritized.contains($)).toList());
	}
}
