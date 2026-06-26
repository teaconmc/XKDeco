package org.teacon.xkdeco.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.teacon.xkdeco.block.RoofRidgeBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.WallSide;
import net.minecraft.world.phys.shapes.VoxelShape;
import snownee.kiwi.customization.placement.PlaceSlot;

@Mixin(WallBlock.class)
public abstract class WallBlockMixin {
	@Unique
	private boolean xkdeco$isRoofRidge() {
		return ((Object) this).getClass() == RoofRidgeBlock.class;
	}

	@Inject(method = "updateSides", at = @At("HEAD"), cancellable = true)
	private void xkdeco$updateSides(
			BlockState state,
			boolean northConnection,
			boolean eastConnection,
			boolean southConnection,
			boolean westConnection,
			VoxelShape aboveShape,
			CallbackInfoReturnable<BlockState> cir) {
		if (xkdeco$isRoofRidge()) {
			cir.setReturnValue(state);
		}
	}

	@Inject(method = "connectsTo", at = @At("HEAD"), cancellable = true)
	private void xkdeco$connectsTo(BlockState state, boolean faceSolid, Direction direction, CallbackInfoReturnable<Boolean> cir) {
		if (xkdeco$isRoofRidge()) {
			cir.setReturnValue(
					state.getBlock().getClass() == RoofRidgeBlock.class || !Block.isExceptionForConnection(state) && faceSolid ||
							PlaceSlot.find(state, direction, "*roof_ridge_end").isPresent());
		}
	}

	@Inject(method = "sideUpdate", at = @At("HEAD"), cancellable = true)
	private void xkdeco$sideUpdate(
			LevelReader level,
			BlockPos pos,
			BlockState firstState,
			BlockPos neighbourPos,
			BlockState neighbour,
			Direction direction,
			CallbackInfoReturnable<BlockState> cir) {
		if (xkdeco$isRoofRidge()) {
			WallSide wallSide = RoofRidgeBlock.makeSide(level, neighbourPos, neighbour, direction);
			if (wallSide == null) {
				wallSide = WallSide.LOW;
			}
			firstState = firstState.setValue(RoofRidgeBlock.DIRECTION_TO_PROPERTY.get(direction), wallSide);
			firstState = firstState.setValue(WallBlock.UP, xkdeco$shouldRaisePost(firstState));
			cir.setReturnValue(firstState);
		}
	}

	@Unique
	private boolean xkdeco$shouldRaisePost(BlockState state) {
		WallSide wallside = state.getValue(BlockStateProperties.NORTH_WALL);
		WallSide wallside1 = state.getValue(BlockStateProperties.SOUTH_WALL);
		WallSide wallside2 = state.getValue(BlockStateProperties.EAST_WALL);
		WallSide wallside3 = state.getValue(BlockStateProperties.WEST_WALL);
		boolean flag1 = wallside1 == WallSide.NONE;
		boolean flag2 = wallside3 == WallSide.NONE;
		boolean flag3 = wallside2 == WallSide.NONE;
		boolean flag4 = wallside == WallSide.NONE;
		return flag4 && flag1 && flag2 && flag3 || flag4 != flag1 || flag2 != flag3;
	}
}
