package org.teacon.xkdeco.mixin.fabric;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.teacon.xkdeco.util.CommonProxy;

import net.minecraft.core.registries.BuiltInRegistries;

@Mixin(BuiltInRegistries.class)
public class BuiltInRegistriesMixin {
	@Inject(method = "freeze", at = @At("HEAD"))
	private static void xkdeco$freeze(CallbackInfo ci) {
		CommonProxy.onRegistriesFrozen();
	}
}
