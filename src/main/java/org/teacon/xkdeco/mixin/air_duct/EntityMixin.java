package org.teacon.xkdeco.mixin.air_duct;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.teacon.xkdeco.block.AirDuctBlock;
import org.teacon.xkdeco.duck.XKDPlayer;
import org.teacon.xkdeco.util.CommonProxy;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

@Mixin(Entity.class)
public abstract class EntityMixin {
	@Shadow
	private Level level;

	@Shadow
	public boolean noPhysics;

	@Shadow
	public boolean horizontalCollision;

	@Shadow
	public boolean minorHorizontalCollision;

	@Shadow
	private BlockPos blockPosition;

	@Shadow
	public abstract BlockState getFeetBlockState();

	@Shadow
	public abstract AABB getBoundingBox();

	@Shadow
	public abstract boolean hasPose(Pose pPose);

	@Inject(method = "canEnterPose", at = @At("HEAD"), cancellable = true)
	private void xkdeco$canEnterPose(Pose pPose, CallbackInfoReturnable<Boolean> cir) {
		if (pPose != Pose.STANDING && pPose != Pose.CROUCHING) {
			return;
		}
		if (!(this instanceof XKDPlayer player)) {
			return;
		}
		if (player.xkdeco$forceSwimmingPose() || getFeetBlockState().getBlock() instanceof AirDuctBlock) {
			cir.setReturnValue(false);
		}
	}

	@Inject(method = "isInvisible", at = @At("HEAD"), cancellable = true)
	private void xkdeco$isInvisible(CallbackInfoReturnable<Boolean> cir) {
		if (this instanceof XKDPlayer player && player.xkdeco$isHidingInAirDuct()) {
			cir.setReturnValue(true);
		}
	}

	@Inject(method = "move", at = @At("TAIL"))
	private void xkdeco$move(MoverType pType, Vec3 pPos, CallbackInfo ci) {
		if (!(this instanceof XKDPlayer player)) {
			return;
		}
		CommonProxy.moveEntity(player, (Entity) (Object) this, pPos);
	}
}
