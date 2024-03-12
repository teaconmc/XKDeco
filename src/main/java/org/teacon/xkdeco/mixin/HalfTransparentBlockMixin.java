package org.teacon.xkdeco.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.teacon.xkdeco.Hooks;
import org.teacon.xkdeco.block.settings.XKBlockSettings;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(HalfTransparentBlock.class)
public abstract class HalfTransparentBlockMixin {
	@Inject(method = "skipRendering", at = @At("HEAD"), cancellable = true)
	private void xkdeco$skipRendering(
			BlockState pState,
			BlockState pAdjacentBlockState,
			Direction pSide,
			CallbackInfoReturnable<Boolean> cir) {
		XKBlockSettings settings = XKBlockSettings.of(this);
		if (settings != null && settings.glassType != null) {
			// generally, XKDeco blocks should not extend HalfTransparentBlock. avoids stack overflow here.
			cir.setReturnValue(Hooks.skipGlassRendering(pState, pAdjacentBlockState, pSide));
		}
		settings = XKBlockSettings.of(pAdjacentBlockState.getBlock());
		if (settings != null && settings.glassType != null && pAdjacentBlockState.skipRendering(pState, pSide.getOpposite())) {
			cir.setReturnValue(true);
		}
	}
}
