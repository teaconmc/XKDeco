package org.teacon.xkdeco.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.teacon.xkdeco.block.settings.XKBlockSettings;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockStateMixin {
	@Shadow
	public abstract Block getBlock();

	@Shadow
	protected abstract BlockState asState();

	@Inject(method = "canSurvive", at = @At("HEAD"), cancellable = true)
	private void xkdeco$canSurvive(LevelReader pLevel, BlockPos pPos, CallbackInfoReturnable<Boolean> cir) {
		XKBlockSettings settings = XKBlockSettings.of(getBlock());
		if (settings != null && settings.canSurviveHandler != null) {
			cir.setReturnValue(settings.canSurviveHandler.canSurvive(asState(), pLevel, pPos));
		}
	}

	@Inject(method = "updateShape", at = @At("HEAD"), cancellable = true)
	private void xkdeco$checkCanSurvive(
			Direction pDirection,
			BlockState pNeighborState,
			LevelAccessor pLevel,
			BlockPos pPos,
			BlockPos pNeighborPos,
			CallbackInfoReturnable<BlockState> cir) {
		XKBlockSettings settings = XKBlockSettings.of(getBlock());
		if (settings != null && settings.canSurviveHandler != null && settings.canSurviveHandler.isSensitiveSide(asState(), pDirection) &&
				!settings.canSurviveHandler.canSurvive(asState(), pLevel, pPos)) {
			cir.setReturnValue(Blocks.AIR.defaultBlockState());
		}
	}

	@Inject(method = "updateShape", at = @At("RETURN"), cancellable = true)
	private void xkdeco$updateShape(
			Direction pDirection,
			BlockState pNeighborState,
			LevelAccessor pLevel,
			BlockPos pPos,
			BlockPos pNeighborPos,
			CallbackInfoReturnable<BlockState> cir) {
		if (!cir.getReturnValue().is(getBlock())) {
			return;
		}
		XKBlockSettings settings = XKBlockSettings.of(getBlock());
		if (settings != null) {
			cir.setReturnValue(settings.updateShape(cir.getReturnValue(), pDirection, pNeighborState, pLevel, pPos, pNeighborPos));
		}
	}

	@Inject(method = "canBeReplaced(Lnet/minecraft/world/item/context/BlockPlaceContext;)Z", at = @At("HEAD"), cancellable = true)
	private void xkdeco$canBeReplaced(BlockPlaceContext pUseContext, CallbackInfoReturnable<Boolean> cir) {
		XKBlockSettings settings = XKBlockSettings.of(getBlock());
		if (settings == null) {
			return;
		}
		Boolean triState = settings.canBeReplaced(asState(), pUseContext);
		if (triState != null) {
			cir.setReturnValue(triState);
		}
	}
}
