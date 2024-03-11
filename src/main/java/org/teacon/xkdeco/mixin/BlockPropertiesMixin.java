package org.teacon.xkdeco.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.teacon.xkdeco.block.settings.XKBlockSettings;
import org.teacon.xkdeco.duck.XKBlockProperties;

import net.minecraft.world.level.block.state.BlockBehaviour;

@Mixin(BlockBehaviour.Properties.class)
public class BlockPropertiesMixin implements XKBlockProperties {
	@Unique
	private XKBlockSettings settings;

	@Override
	public @Nullable XKBlockSettings xkdeco$getSettings() {
		return settings;
	}

	@Override
	public void xkdeco$setSettings(XKBlockSettings settings) {
		this.settings = settings;
	}

	@Inject(method = "copy", at = @At("RETURN"))
	private static void xkdeco$copy(BlockBehaviour pBlockBehaviour, CallbackInfoReturnable<BlockBehaviour.Properties> cir) {
		((XKBlockProperties) cir.getReturnValue()).xkdeco$setSettings(XKBlockSettings.of(pBlockBehaviour));
	}
}
