package org.teacon.xkdeco.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.teacon.xkdeco.block.settings.XKBlockSettings;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

@Mixin({BlockItem.class, StandingAndWallBlockItem.class})
public class BlockItemMixin {
	@WrapOperation(
			method = "getPlacementState",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/block/Block;getStateForPlacement(Lnet/minecraft/world/item/context/BlockPlaceContext;)Lnet/minecraft/world/level/block/state/BlockState;"))
	private BlockState xkdeco$getPlacementState(Block block, BlockPlaceContext pContext, Operation<BlockState> original) {
		BlockState state = original.call(block, pContext);
		if (state == null || !state.is(block)) {
			return state;
		}
		XKBlockSettings settings = XKBlockSettings.of(block);
		if (settings != null) {
			state = settings.getStateForPlacement(state, pContext);
		}
		return state;
	}
}
