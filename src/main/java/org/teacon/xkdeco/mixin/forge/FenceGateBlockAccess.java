package org.teacon.xkdeco.mixin.forge;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.FenceGateBlock;

@Mixin(FenceGateBlock.class)
public interface FenceGateBlockAccess {
	@Accessor
	SoundEvent getOpenSound();

	@Accessor
	SoundEvent getCloseSound();
}
