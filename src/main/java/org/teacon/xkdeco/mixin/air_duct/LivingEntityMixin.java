package org.teacon.xkdeco.mixin.air_duct;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.teacon.xkdeco.block.AirDuctBlock;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
	public LivingEntityMixin(EntityType<?> pEntityType, Level pLevel) {
		super(pEntityType, pLevel);
	}

	@ModifyExpressionValue(
			method = "handleRelativeFrictionAndCalculateMovement", at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/world/entity/LivingEntity;horizontalCollision:Z"))
	private boolean xkdeco$suppressClimbing(boolean original) {
		if (original && getFeetBlockState().getBlock() instanceof AirDuctBlock) {
			return false;
		}
		return original;
	}
}
