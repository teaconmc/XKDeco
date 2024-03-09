package org.teacon.xkdeco.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.teacon.xkdeco.block.settings.XKDBlockSettings;
import org.teacon.xkdeco.duck.XKDBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

@Mixin(BlockBehaviour.class)
public class BlockBehaviourMixin implements XKDBlock {
	@Shadow
	@Final
	protected boolean hasCollision;
	@Unique
	private XKDBlockSettings settings;

	@Override
	public @Nullable XKDBlockSettings xkdeco$getSettings() {
		return settings;
	}

	@Override
	public void xkdeco$setSettings(XKDBlockSettings settings) {
		this.settings = settings;
	}

	@Inject(method = "getShape", at = @At("HEAD"), cancellable = true)
	private void xkdeco$getShape(
			BlockState pState,
			BlockGetter pLevel,
			BlockPos pPos,
			CollisionContext pContext,
			CallbackInfoReturnable<VoxelShape> cir) {
		XKDBlockSettings settings = xkdeco$getSettings();
		if (settings != null && settings.shape != null) {
			cir.setReturnValue(settings.shape.getShape(pState, pContext));
		}
	}

	@Inject(method = "getCollisionShape", at = @At("HEAD"), cancellable = true)
	private void xkdeco$getCollisionShape(
			BlockState pState,
			BlockGetter pLevel,
			BlockPos pPos,
			CollisionContext pContext,
			CallbackInfoReturnable<VoxelShape> cir) {
		XKDBlockSettings settings = xkdeco$getSettings();
		if (hasCollision && settings != null && settings.collisionShape != null) {
			cir.setReturnValue(settings.collisionShape.getShape(pState, pContext));
		}
	}

	@Inject(method = "getInteractionShape", at = @At("HEAD"), cancellable = true)
	private void xkdeco$getInteractionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CallbackInfoReturnable<VoxelShape> cir) {
		XKDBlockSettings settings = xkdeco$getSettings();
		if (settings != null && settings.interactionShape != null) {
			cir.setReturnValue(settings.interactionShape.getShape(pState, CollisionContext.empty()));
		}
	}
}
