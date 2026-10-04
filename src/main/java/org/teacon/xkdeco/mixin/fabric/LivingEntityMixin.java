package org.teacon.xkdeco.mixin.fabric;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.teacon.xkdeco.block.XKDBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
	public LivingEntityMixin(EntityType<?> entityType, Level level) {
		super(entityType, level);
	}

	@Inject(method = "trapdoorUsableAsLadder", at = @At("HEAD"), cancellable = true)
	private void xkdeco$onClimbable(BlockPos pos, BlockState trapdoorState, CallbackInfoReturnable<Boolean> cir) {
		BlockState blockState;
		if (trapdoorState.getValue(TrapDoorBlock.OPEN) &&
				(blockState = level().getBlockState(pos.below())).getBlock() instanceof XKDBlock block) {
			cir.setReturnValue(block.xkdeco$makesOpenTrapdoorAboveClimbable(trapdoorState, level(), pos, blockState));
		}
	}
}
