package org.teacon.xkdeco.mixin.air_duct;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.teacon.xkdeco.duck.XKDPlayer;
import org.teacon.xkdeco.util.CommonProxy;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;

@Mixin(Entity.class)
public abstract class EntityMixin {
	@Inject(method = "isInvisible", at = @At("HEAD"), cancellable = true)
	private void xkdeco$isInvisible(CallbackInfoReturnable<Boolean> cir) {
		if (this instanceof XKDPlayer player && player.xkdeco$isHidingInAirDuct()) {
			cir.setReturnValue(true);
		}
	}

	@Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
	private void xkdeco$shouldRender(CallbackInfoReturnable<Boolean> cir) {
		if (this instanceof XKDPlayer player && player.xkdeco$isHidingInAirDuct()) {
			cir.setReturnValue(false);
		}
	}

	@Inject(method = "move", at = @At("TAIL"))
	private void xkdeco$move(MoverType moverType, Vec3 delta, CallbackInfo ci) {
		if (!(this instanceof XKDPlayer player)) {
			return;
		}
		CommonProxy.moveEntity(player, (Entity) (Object) this, delta);
	}
}
