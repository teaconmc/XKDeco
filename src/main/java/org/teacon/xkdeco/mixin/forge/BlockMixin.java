package org.teacon.xkdeco.mixin.forge;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.teacon.xkdeco.block.settings.XKBlockSettings;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;

@Mixin(Block.class)
public abstract class BlockMixin {
	@Inject(method = "canSustainPlant", at = @At("HEAD"), cancellable = true, remap = false)
	private void xkdeco$canSustainPlant(
			BlockState state,
			BlockGetter world,
			BlockPos pos,
			Direction facing,
			IPlantable plantable,
			CallbackInfoReturnable<Boolean> cir) {
		XKBlockSettings settings = XKBlockSettings.of(this);
		if (settings != null && settings.sustainsPlant) {
			PlantType type = plantable.getPlantType(world, pos.relative(facing));
			if (type == PlantType.PLAINS) {
				cir.setReturnValue(true);
			}
		}
	}
}
