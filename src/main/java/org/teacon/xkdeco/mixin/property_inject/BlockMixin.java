package org.teacon.xkdeco.mixin.property_inject;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.teacon.xkdeco.block.settings.XKBlockSettings;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(Block.class)
public class BlockMixin {
	@Shadow
	private BlockState defaultBlockState;

	@Inject(method = "registerDefaultState", at = @At("RETURN"))
	private void xkdeco$registerDefaultState(BlockState pState, CallbackInfo ci) {
		XKBlockSettings settings = XKBlockSettings.of(this);
		if (settings != null) {
			defaultBlockState = settings.registerDefaultState(defaultBlockState);
		}
	}
}
