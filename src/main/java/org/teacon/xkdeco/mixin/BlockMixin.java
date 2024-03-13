package org.teacon.xkdeco.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.teacon.xkdeco.block.settings.BlockRenderSettings;
import org.teacon.xkdeco.block.settings.XKBlockSettings;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import snownee.kiwi.loader.Platform;

@Mixin(Block.class)
public abstract class BlockMixin {
	@Inject(method = "<init>", at = @At("RETURN"))
	private void xkdeco$init(BlockBehaviour.Properties pProperties, CallbackInfo ci) {
		if (Platform.isPhysicalClient()) {
			XKBlockSettings settings = XKBlockSettings.of(this);
			if (settings != null) {
				BlockRenderSettings.onBlockInit((Block) (Object) this, settings);
			}
		}
	}

	@Inject(method = "propagatesSkylightDown", at = @At("HEAD"), cancellable = true)
	private void xkdeco$propagatesSkylightDown(BlockState pState, BlockGetter pLevel, BlockPos pPos, CallbackInfoReturnable<Boolean> cir) {
		XKBlockSettings settings = XKBlockSettings.of(this);
		if (settings != null && settings.glassType != null) {
			cir.setReturnValue(true);
		}
	}
}
