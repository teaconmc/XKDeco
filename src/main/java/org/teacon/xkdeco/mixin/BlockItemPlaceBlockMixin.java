package org.teacon.xkdeco.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.teacon.xkdeco.block.place.PlacementSystem;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(BlockItem.class)
public class BlockItemPlaceBlockMixin {
	@Inject(method = "placeBlock", at = @At("TAIL"))
	private void kiwi$placeBlock(BlockPlaceContext pContext, BlockState pState, CallbackInfoReturnable<Boolean> cir) {
		if (cir.getReturnValueZ()) {
			PlacementSystem.onBlockPlaced(pContext);
		}
	}
}
