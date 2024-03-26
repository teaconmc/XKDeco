package org.teacon.xkdeco.mixin.loader;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.teacon.xkdeco.util.CommonProxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import net.minecraft.server.packs.resources.ResourceManager;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
	@Shadow
	public abstract ResourceManager getResourceManager();

	@Inject(method = "<init>", at = @At("RETURN"))
	private void onInit(GameConfig pGameConfig, CallbackInfo ci) {
		CommonProxy.initLoader(getResourceManager());
	}
}
