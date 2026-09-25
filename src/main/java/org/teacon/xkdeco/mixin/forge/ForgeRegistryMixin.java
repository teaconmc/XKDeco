package org.teacon.xkdeco.mixin.forge;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.teacon.xkdeco.init.MimicWallsLoader;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistry;

@Mixin(value = ForgeRegistry.class, remap = false)
public abstract class ForgeRegistryMixin<V> {
	@Inject(method = "add(ILnet/minecraft/resources/ResourceLocation;Ljava/lang/Object;Ljava/lang/String;)I", at = @At("RETURN"))
	private void xkdeco$addMimicWall(
			int id,
			ResourceLocation key,
			V value,
			String owner,
			CallbackInfoReturnable<Integer> cir) {
		if (MimicWallsLoader.isListeningWallRegistration() && value instanceof Block block) {
			MimicWallsLoader.newBlockAdded(key, block);
		}
	}
}
