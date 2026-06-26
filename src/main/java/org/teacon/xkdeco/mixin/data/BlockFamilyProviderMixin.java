package org.teacon.xkdeco.mixin.data;

import java.util.Objects;

import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.teacon.xkdeco.data.XKDModelProvider;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.data.BlockFamily;
import net.minecraft.world.level.block.Block;

@Mixin(BlockModelGenerators.BlockFamilyProvider.class)
public class BlockFamilyProviderMixin {
	@Shadow(aliases = {"field_22836"})
	@Final
	BlockModelGenerators this$0;

	@Shadow
	@Final
	public TextureMapping mapping;

	@Shadow
	@Nullable
	public BlockFamily family;

	@Inject(method = "fullBlockVariant", at = @At("HEAD"), cancellable = true)
	private void xkdeco$fullBlockVariant(Block variant, CallbackInfoReturnable<BlockModelGenerators.BlockFamilyProvider> cir) {
		BlockModelGenerators.BlockFamilyProvider self = (BlockModelGenerators.BlockFamilyProvider) (Object) this;
		if (XKDModelProvider.createIfRotatedPillar(variant, this$0)) {
			cir.setReturnValue(self);
		}
	}

	@Inject(method = "slab", at = @At("HEAD"), cancellable = true)
	private void xkdeco$slab(Block slab, CallbackInfoReturnable<BlockModelGenerators.BlockFamilyProvider> cir) {
		BlockModelGenerators.BlockFamilyProvider self = (BlockModelGenerators.BlockFamilyProvider) (Object) this;
		if (XKDModelProvider.createIfSpecialDoubleSlabs(slab, this$0, Objects.requireNonNull(family))) {
			cir.setReturnValue(self);
		}
	}

	@Inject(method = "stairs", at = @At("HEAD"), cancellable = true)
	private void xkdeco$stairs(Block stairs, CallbackInfoReturnable<BlockModelGenerators.BlockFamilyProvider> cir) {
		BlockModelGenerators.BlockFamilyProvider self = (BlockModelGenerators.BlockFamilyProvider) (Object) this;
		if (XKDModelProvider.createIfSpecialStairs(stairs, mapping, this$0)) {
			cir.setReturnValue(self);
		}
	}

	@Inject(method = "trapdoor", at = @At("HEAD"), cancellable = true)
	private void xkdeco$trapdoor(Block result, CallbackInfo ci) {
		if (XKDModelProvider.createIfSpecialTrapdoor(result, this$0, Objects.requireNonNull(family))) {
			ci.cancel();
		}
	}

	@Inject(method = "fence", at = @At("HEAD"), cancellable = true)
	private void xkdeco$fence(Block block, CallbackInfoReturnable<BlockModelGenerators.BlockFamilyProvider> cir) {
		BlockModelGenerators.BlockFamilyProvider self = (BlockModelGenerators.BlockFamilyProvider) (Object) this;
		if (XKDModelProvider.createIfSpecialFence(block, this$0, Objects.requireNonNull(family))) {
			cir.setReturnValue(self);
		}
	}

	@Inject(method = "fenceGate", at = @At("HEAD"), cancellable = true)
	private void xkdeco$fenceGate(Block block, CallbackInfoReturnable<BlockModelGenerators.BlockFamilyProvider> cir) {
		BlockModelGenerators.BlockFamilyProvider self = (BlockModelGenerators.BlockFamilyProvider) (Object) this;
		if (XKDModelProvider.createIfSpecialFenceGate(block, this$0, Objects.requireNonNull(family))) {
			cir.setReturnValue(self);
		}
	}
}
