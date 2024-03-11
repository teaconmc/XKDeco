package org.teacon.xkdeco.mixin.property_inject;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.teacon.xkdeco.block.settings.XKBlockSettings;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockStateMixin {
	@Shadow
	public abstract Block getBlock();

	@Shadow
	protected abstract BlockState asState();

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
			cir.setReturnValue(settings.updateShape(asState(), pDirection, pNeighborState, pLevel, pPos, pNeighborPos));
		}
	}
}
