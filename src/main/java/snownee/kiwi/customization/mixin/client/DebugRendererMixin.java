package snownee.kiwi.customization.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import snownee.kiwi.customization.place.PlaceDebugRenderer;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Items;

@Mixin(DebugRenderer.class)
public class DebugRendererMixin {
	@Inject(method = "render", at = @At("HEAD"))
	private void kiwi$render(
			PoseStack pPoseStack,
			MultiBufferSource.BufferSource pBufferSource,
			double pCamX,
			double pCamY,
			double pCamZ,
			CallbackInfo ci) {
		LocalPlayer player = Minecraft.getInstance().player;
		if (player != null && (player.isCreative() || player.isSpectator()) && player.getItemBySlot(EquipmentSlot.HEAD).is(Items.CHAINMAIL_HELMET)) {
			PlaceDebugRenderer.getInstance().render(pPoseStack, pBufferSource, pCamX, pCamY, pCamZ);
		}
	}
}
