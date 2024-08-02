package org.teacon.xkdeco.mixin.air_duct;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.teacon.xkdeco.duck.XKDPlayer;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;

@Mixin(PlayerRenderer.class)
public class PlayerRendererMixin {
	@Inject(
			method = "render(Lnet/minecraft/client/player/AbstractClientPlayer;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/renderer/entity/player/PlayerRenderer;setModelProperties(Lnet/minecraft/client/player/AbstractClientPlayer;)V",
					shift = At.Shift.AFTER),
			cancellable = true)
	private void xkdeco$render(
			AbstractClientPlayer pEntity,
			float pEntityYaw,
			float pPartialTicks,
			PoseStack pPoseStack,
			MultiBufferSource pBuffer,
			int pPackedLight,
			CallbackInfo ci) {
		if (pEntity instanceof XKDPlayer player && player.xkdeco$isHidingInAirDuct()) {
			ci.cancel();
		}
	}
}
