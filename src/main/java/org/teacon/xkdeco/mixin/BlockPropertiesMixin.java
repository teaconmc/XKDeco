package org.teacon.xkdeco.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.teacon.xkdeco.block.setting.XKBlockSettings;
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
}
