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
	BlockModelGenerators this$0; //TODO check if this is working on production environment

	@Inject(method = "fullBlockVariant", at = @At("HEAD"), cancellable = true)
	private void xkdeco_fullBlockVariant(Block pBlock, CallbackInfoReturnable<BlockModelGenerators.BlockFamilyProvider> cir) {
		BlockModelGenerators.BlockFamilyProvider self = (BlockModelGenerators.BlockFamilyProvider) (Object) this;
		if (XKDModelProvider.createIfRotatedPillar(pBlock, this$0)) {
			cir.setReturnValue(self);
		}
	}

	@Inject(method = "slab", at = @At("HEAD"), cancellable = true)
	private void xkdeco_slab(Block pBlock, CallbackInfoReturnable<BlockModelGenerators.BlockFamilyProvider> cir) {
		BlockModelGenerators.BlockFamilyProvider self = (BlockModelGenerators.BlockFamilyProvider) (Object) this;
		if (XKDModelProvider.createIfSpecialDoubleSlabs(pBlock, this$0)) {
			cir.setReturnValue(self);
		}
	}
}
