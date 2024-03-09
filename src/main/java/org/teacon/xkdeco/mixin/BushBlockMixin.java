package org.teacon.xkdeco.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.teacon.xkdeco.block.settings.XKDBlockSettings;
import org.teacon.xkdeco.duck.XKDBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(BushBlock.class)
public abstract class BushBlockMixin implements XKDBlock {
	@Inject(method = "mayPlaceOn", at = @At("HEAD"), cancellable = true)
	private void xkdeco$mayPlaceOn(BlockState pState, BlockGetter pLevel, BlockPos pPos, CallbackInfoReturnable<Boolean> cir) {
		XKDBlockSettings settings = xkdeco$getSettings();
		if (settings != null && settings.sustainsPlant()) {
			cir.setReturnValue(true);
		}
	}
}
