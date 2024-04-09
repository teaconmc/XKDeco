package org.teacon.xkdeco.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.teacon.xkdeco.block.place.PlacementSystem;
import org.teacon.xkdeco.block.setting.KBlockSettings;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import snownee.kiwi.Kiwi;

@Mixin({BlockItem.class, StandingAndWallBlockItem.class})
public class BlockItemGetPlacementStateMixin {
	@WrapOperation(
			method = "getPlacementState",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/block/Block;getStateForPlacement(Lnet/minecraft/world/item/context/BlockPlaceContext;)Lnet/minecraft/world/level/block/state/BlockState;"))
	private BlockState kiwi$getPlacementState(Block block, BlockPlaceContext pContext, Operation<BlockState> original) {
		BlockState state = original.call(block, pContext);
		if (state == null || !state.is(block)) {
			return state;
		}
		KBlockSettings settings = KBlockSettings.of(block);
		if (settings != null) {
			state = settings.getStateForPlacement(state, pContext);
		}
		try {
			state = PlacementSystem.onPlace(state, pContext);
		} catch (Throwable t) {
			Kiwi.LOGGER.error("Failed to handle placement for %s".formatted(state), t);
		}
		return state;
	}
}
