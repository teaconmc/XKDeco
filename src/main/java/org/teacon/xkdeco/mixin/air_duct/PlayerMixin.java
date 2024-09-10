package org.teacon.xkdeco.mixin.air_duct;

import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.teacon.xkdeco.XKDeco;
import org.teacon.xkdeco.block.AirDuctBlock;
import org.teacon.xkdeco.block.XKDBlock;
import org.teacon.xkdeco.duck.XKDPlayer;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

// Make player invisible if they are in air ducts
@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements XKDPlayer {
	@Unique
	private boolean xkdeco$isHidingInAirDuct;
	@Unique
	private int xkdeco$forceSwimmingPose;

	protected PlayerMixin(EntityType<? extends LivingEntity> pEntityType, Level pLevel) {
		super(pEntityType, pLevel);
	}

	@Inject(method = "aiStep", at = @At("HEAD"))
	private void xkdeco$aiStep(CallbackInfo ci) {
		if (xkdeco$forceSwimmingPose > 0) {
			xkdeco$forceSwimmingPose--;
		}
		if (tickCount % 10 != 0) {
			return;
		}
		if (!hasPose(Pose.SWIMMING)) {
			xkdeco$isHidingInAirDuct = false;
			return;
		}
		int[] counts = {0, 0};
		level().getBlockStates(getBoundingBox().deflate(getBbWidth() / 4)).forEach(state -> {
			if (state.is(XKDBlock.AIR_DUCTS)) {
				counts[0]++;
			} else {
				counts[1]++;
			}
		});
		xkdeco$isHidingInAirDuct = counts[0] > 0 && counts[0] >= counts[1];
		if (xkdeco$isHidingInAirDuct && !level().isClientSide) {
			addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 15, 0, false, false, true));
		}
	}

	@Inject(method = "canPlayerFitWithinBlocksAndEntitiesWhen", at = @At("HEAD"), cancellable = true)
	private void xkdeco$canPlayerFitWithinBlocksAndEntitiesWhen(Pose pose, CallbackInfoReturnable<Boolean> cir) {
		if (pose != Pose.STANDING && pose != Pose.CROUCHING) {
			return;
		}
		if (!(this instanceof XKDPlayer player)) {
			return;
		}
		if (player.xkdeco$forceSwimmingPose() || getInBlockState().getBlock() instanceof AirDuctBlock) {
			cir.setReturnValue(false);
		}
	}

	@Override
	public boolean xkdeco$isHidingInAirDuct() {
		return xkdeco$isHidingInAirDuct;
	}

	@Override
	public boolean xkdeco$forceSwimmingPose() {
		return xkdeco$forceSwimmingPose > 0;
	}

	@Override
	public void xkdeco$collideWithAirDuctHorizontally() {
		xkdeco$forceSwimmingPose = 5;
	}
}
