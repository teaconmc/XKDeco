package snownee.kiwi.customization.mixin.xkdeco;

import java.util.Collection;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.teacon.xkdeco.init.MimicWallsLoader;

import com.google.common.collect.Maps;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.tags.TagManager;
import net.minecraft.world.level.block.Block;

@Mixin(ReloadableServerResources.class)
public class ReloadableServerResourcesMixin {
	@SuppressWarnings("unchecked")
	@Inject(method = "updateRegistryTags(Lnet/minecraft/core/RegistryAccess;Lnet/minecraft/tags/TagManager$LoadResult;)V", at = @At("HEAD"))
	private static <T> void xkdeco$addDynamicTags(
			RegistryAccess pRegistryAccess,
			TagManager.LoadResult<T> pLoadResult,
			CallbackInfo ci,
			@Local(argsOnly = true) LocalRef<TagManager.LoadResult<T>> resultRef) {
		if (pLoadResult.key().equals(Registries.BLOCK)) {
			var tags = (Map<ResourceLocation, Collection<Holder<Block>>>) (Object) Maps.newHashMap(pLoadResult.tags());
			MimicWallsLoader.addMimicWallTags(tags);
			resultRef.set((TagManager.LoadResult<T>) new TagManager.LoadResult<>(Registries.BLOCK, tags));
		}
	}
}