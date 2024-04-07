package org.teacon.xkdeco.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.teacon.xkdeco.block.setting.KBlockSettings;
import org.teacon.xkdeco.duck.KBlockProperties;

import net.minecraft.world.level.block.state.BlockBehaviour;

@Mixin(BlockBehaviour.Properties.class)
public class BlockPropertiesMixin implements KBlockProperties {
	@Unique
	private KBlockSettings settings;

	@Override
	public @Nullable KBlockSettings kiwi$getSettings() {
		return settings;
	}

	@Override
	public void kiwi$setSettings(KBlockSettings settings) {
		this.settings = settings;
	}
}
