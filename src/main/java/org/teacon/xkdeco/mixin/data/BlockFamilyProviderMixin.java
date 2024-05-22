package org.teacon.xkdeco.mixin.data;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.teacon.xkdeco.data.XKDModelProvider;

import javax.annotation.Nullable;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.world.level.block.Block;

@Mixin(BlockModelGenerators.BlockFamilyProvider.class)
public class BlockFamilyProviderMixin {
	@Shadow(aliases = {"f_125029_", "field_22836"})
	@Final
	BlockModelGenerators this$0;

	@Shadow
	@Final
	private TextureMapping mapping;

	@Shadow
	@Nullable
	private BlockFamily family;

	@Inject(method = "fullBlockVariant", at = @At("HEAD"), cancellable = true)
	private void xkdeco$fullBlockVariant(Block pBlock, CallbackInfoReturnable<BlockModelGenerators.BlockFamilyProvider> cir) {
		BlockModelGenerators.BlockFamilyProvider self = (BlockModelGenerators.BlockFamilyProvider) (Object) this;
		if (XKDModelProvider.createIfRotatedPillar(pBlock, this$0)) {
			cir.setReturnValue(self);
		}
	}

	@Inject(method = "slab", at = @At("HEAD"), cancellable = true)
	private void xkdeco$slab(Block pBlock, CallbackInfoReturnable<BlockModelGenerators.BlockFamilyProvider> cir) {
		BlockModelGenerators.BlockFamilyProvider self = (BlockModelGenerators.BlockFamilyProvider) (Object) this;
		if (XKDModelProvider.createIfSpecialDoubleSlabs(pBlock, this$0, family)) {
			cir.setReturnValue(self);
		}
	}

	@Inject(method = "stairs", at = @At("HEAD"), cancellable = true)
	private void xkdeco$stairs(Block pBlock, CallbackInfoReturnable<BlockModelGenerators.BlockFamilyProvider> cir) {
		BlockModelGenerators.BlockFamilyProvider self = (BlockModelGenerators.BlockFamilyProvider) (Object) this;
		if (XKDModelProvider.createIfSpecialStairs(pBlock, mapping, this$0)) {
			cir.setReturnValue(self);
		}
	}

	@Inject(method = "trapdoor", at = @At("HEAD"), cancellable = true)
	private void xkdeco$trapdoor(Block pBlock, CallbackInfo ci) {
		if (XKDModelProvider.createIfSpecialTrapdoor(pBlock, this$0, family)) {
			ci.cancel();
		}
	}

	@Inject(method = "fence", at = @At("HEAD"), cancellable = true)
	private void xkdeco$fence(Block pBlock, CallbackInfoReturnable<BlockModelGenerators.BlockFamilyProvider> cir) {
		BlockModelGenerators.BlockFamilyProvider self = (BlockModelGenerators.BlockFamilyProvider) (Object) this;
		if (XKDModelProvider.createIfSpecialFence(pBlock, this$0, family)) {
			cir.setReturnValue(self);
		}
	}

	@Inject(method = "fenceGate", at = @At("HEAD"), cancellable = true)
	private void xkdeco$fenceGate(Block pBlock, CallbackInfoReturnable<BlockModelGenerators.BlockFamilyProvider> cir) {
		BlockModelGenerators.BlockFamilyProvider self = (BlockModelGenerators.BlockFamilyProvider) (Object) this;
		if (XKDModelProvider.createIfSpecialFenceGate(pBlock, this$0, family)) {
			cir.setReturnValue(self);
		}
	}
}