package org.teacon.xkdeco.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.teacon.xkdeco.block.settings.XKDBlockSettings;
import org.teacon.xkdeco.duck.XKDBlock;

import net.minecraft.world.level.block.state.BlockBehaviour;

@Mixin(BlockBehaviour.class)
public class BlockBehaviourMixin implements XKDBlock {
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
}
