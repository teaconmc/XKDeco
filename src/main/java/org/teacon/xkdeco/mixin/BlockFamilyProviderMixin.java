package org.teacon.xkdeco.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.teacon.xkdeco.data.XKDModelProvider;

import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.world.level.block.Block;

@Mixin(BlockModelGenerators.BlockFamilyProvider.class)
public class BlockFamilyProviderMixin {
	@Shadow
	@Final
	BlockModelGenerators this$0;

	@Inject(method = "fullBlockVariant", at = @At("HEAD"), cancellable = true)
	private void xkdeco_fullBlockVariant(Block pBlock, CallbackInfoReturnable<BlockModelGenerators.BlockFamilyProvider> cir) {
		if (XKDModelProvider.createIfRotatedPillar(pBlock, this$0)) {
			cir.setReturnValue((BlockModelGenerators.BlockFamilyProvider) (Object) this);
		}
	}
}
