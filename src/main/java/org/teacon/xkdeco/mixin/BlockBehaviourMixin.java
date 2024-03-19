package org.teacon.xkdeco.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.teacon.xkdeco.Hooks;
import org.teacon.xkdeco.block.settings.XKBlockSettings;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

@Mixin(BlockBehaviour.class)
public class BlockBehaviourMixin {
	@Shadow
	@Final
	protected boolean hasCollision;
	@Shadow
	@Final
	public BlockBehaviour.Properties properties;

	@Inject(method = "getShape", at = @At("HEAD"), cancellable = true)
	private void xkdeco$getShape(
			BlockState pState,
			BlockGetter pLevel,
			BlockPos pPos,
			CollisionContext pContext,
			CallbackInfoReturnable<VoxelShape> cir) {
		XKBlockSettings settings = XKBlockSettings.of(this);
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
		if (!hasCollision) {
			return;
		}
		XKBlockSettings settings = XKBlockSettings.of(this);
		if (settings != null && settings.collisionShape != null) {
			cir.setReturnValue(settings.collisionShape.getShape(pState, pContext));
		}
	}

	@Inject(method = "getInteractionShape", at = @At("HEAD"), cancellable = true)
	private void xkdeco$getInteractionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CallbackInfoReturnable<VoxelShape> cir) {
		XKBlockSettings settings = XKBlockSettings.of(this);
		if (settings != null && settings.interactionShape != null) {
			cir.setReturnValue(settings.interactionShape.getShape(pState, CollisionContext.empty()));
		}
	}

	@Inject(method = "getShadeBrightness", at = @At("HEAD"), cancellable = true)
	private void xkdeco$getShadeBrightness(BlockState state, BlockGetter world, BlockPos pos, CallbackInfoReturnable<Float> cir) {
		XKBlockSettings settings = XKBlockSettings.of(this);
		if (settings != null && settings.glassType != null) {
			cir.setReturnValue(settings.glassType.shadeBrightness());
		}
	}

	@Inject(method = "skipRendering", at = @At("HEAD"), cancellable = true)
	private void xkdeco$skipRendering(
			BlockState pState,
			BlockState pAdjacentState,
			Direction pDirection,
			CallbackInfoReturnable<Boolean> cir) {
		XKBlockSettings settings = XKBlockSettings.of(this);
		if (settings != null && settings.glassType != null && Hooks.skipGlassRendering(pState, pAdjacentState, pDirection)) {
			cir.setReturnValue(true);
		}
	}

	@Inject(method = "getVisualShape", at = @At("HEAD"), cancellable = true)
	private void xkdeco$getVisualShape(
			BlockState pState,
			BlockGetter pLevel,
			BlockPos pPos,
			CollisionContext pContext,
			CallbackInfoReturnable<VoxelShape> cir) {
		XKBlockSettings settings = XKBlockSettings.of(this);
		if (settings != null && settings.glassType != null) {
			cir.setReturnValue(Shapes.empty());
		}
	}

	@Inject(method = "getFluidState", at = @At("HEAD"), cancellable = true)
	private void xkdeco$getFluidState(BlockState pState, CallbackInfoReturnable<FluidState> cir) {
		XKBlockSettings settings = XKBlockSettings.of(this);
		if (settings != null && pState.hasProperty(BlockStateProperties.WATERLOGGED)) {
			cir.setReturnValue(pState.getValue(BlockStateProperties.WATERLOGGED) ?
					Fluids.WATER.getSource(false) :
					Fluids.EMPTY.defaultFluidState());
		}
	}

	@Inject(method = "rotate", at = @At("HEAD"), cancellable = true)
	private void xkdeco$rotate(BlockState pState, Rotation pRotation, CallbackInfoReturnable<BlockState> cir) {
		XKBlockSettings settings = XKBlockSettings.of(this);
		if (settings != null) {
			cir.setReturnValue(settings.rotate(pState, pRotation));
		}
	}

	@Inject(method = "mirror", at = @At("HEAD"), cancellable = true)
	private void xkdeco$mirror(BlockState pState, Mirror pMirror, CallbackInfoReturnable<BlockState> cir) {
		XKBlockSettings settings = XKBlockSettings.of(this);
		if (settings != null) {
			cir.setReturnValue(settings.mirror(pState, pMirror));
		}
	}

	@Inject(method = "useShapeForLightOcclusion", at = @At("HEAD"), cancellable = true)
	private void xkdeco$useShapeForLightOcclusion(BlockState pState, CallbackInfoReturnable<Boolean> cir) {
		XKBlockSettings settings = XKBlockSettings.of(this);
		if (settings != null) {
			cir.setReturnValue(settings.useShapeForLightOcclusion(pState));
		}
	}

	@Inject(method = "hasAnalogOutputSignal", at = @At("HEAD"), cancellable = true)
	private void xkdeco$hasAnalogOutputSignal(BlockState pState, CallbackInfoReturnable<Boolean> cir) {
		XKBlockSettings settings = XKBlockSettings.of(this);
		if (settings != null && settings.analogOutputSignal != null) {
			cir.setReturnValue(true);
		}
	}

	@Inject(method = "getAnalogOutputSignal", at = @At("HEAD"), cancellable = true)
	private void xkdeco$getAnalogOutputSignal(BlockState pState, Level pLevel, BlockPos pPos, CallbackInfoReturnable<Integer> cir) {
		XKBlockSettings settings = XKBlockSettings.of(this);
		if (settings != null && settings.analogOutputSignal != null) {
			cir.setReturnValue(settings.analogOutputSignal.applyAsInt(pState));
		}
	}
}
