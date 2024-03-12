package org.teacon.xkdeco.mixin.property_inject;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.teacon.xkdeco.block.settings.XKBlockSettings;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;

@Mixin(StateDefinition.Builder.class)
public abstract class StateDefinitionBuilderMixin<O, S extends StateHolder<O, S>> {
	@Shadow
	public abstract StateDefinition.Builder<O, S> add(Property<?>... pProperties);

	@Inject(method = "<init>", at = @At("RETURN"))
	private void xkdeco$init(O pOwner, CallbackInfo ci) {
		if (!(pOwner instanceof Block block)) {
			return;
		}
		XKBlockSettings settings = XKBlockSettings.of(block);
		if (settings == null) {
			return;
		}
		//noinspection unchecked
		settings.injectProperties(block, (StateDefinition.Builder<Block, BlockState>) (Object) this);
	}
}
