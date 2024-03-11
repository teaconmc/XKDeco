package org.teacon.xkdeco.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.teacon.xkdeco.block.settings.XKDBlockSettings;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockStateMixin {
	@Shadow
	public abstract Block getBlock();

	@Shadow
	protected abstract BlockState asState();

	@Inject(method = "canSurvive", at = @At("HEAD"), cancellable = true)
	private void xkdeco$canSurvive(LevelReader pLevel, BlockPos pPos, CallbackInfoReturnable<Boolean> cir) {
		XKDBlockSettings settings = XKDBlockSettings.of(getBlock());
		if (settings != null && settings.canSurviveHandler != null) {
			cir.setReturnValue(settings.canSurviveHandler.canSurvive(asState(), pLevel, pPos));
		}
	}

	@Inject(method = "updateShape", at = @At("HEAD"), cancellable = true)
	private void xkdeco$updateShape(
			Direction pDirection,
			BlockState pNeighborState,
			LevelAccessor pLevel,
			BlockPos pPos,
			BlockPos pNeighborPos,
			CallbackInfoReturnable<BlockState> cir) {
		XKDBlockSettings settings = XKDBlockSettings.of(getBlock());
		if (settings != null && settings.canSurviveHandler != null && settings.canSurviveHandler.isSensitiveSide(asState(), pDirection) &&
				!settings.canSurviveHandler.canSurvive(asState(), pLevel, pPos)) {
			cir.setReturnValue(Blocks.AIR.defaultBlockState());
		}
	}

	@Inject(method = "updateShape", at = @At("RETURN"))
	private void xkdeco$tickLoggedWater(
			Direction pDirection,
			BlockState pNeighborState,
			LevelAccessor pLevel,
			BlockPos pPos,
			BlockPos pNeighborPos,
			CallbackInfoReturnable<BlockState> cir) {
		if (!cir.getReturnValue().is(getBlock())) {
			return;
		}
		XKDBlockSettings settings = XKDBlockSettings.of(getBlock());
		if (settings != null && cir.getReturnValue().hasProperty(BlockStateProperties.WATERLOGGED) && cir.getReturnValue().getValue(
				BlockStateProperties.WATERLOGGED)) {
			pLevel.scheduleTick(pPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
		}
	}
}
