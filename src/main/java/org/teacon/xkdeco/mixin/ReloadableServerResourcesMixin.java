package org.teacon.xkdeco.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.server.ReloadableServerResources;

@Mixin(ReloadableServerResources.class)
public class ReloadableServerResourcesMixin {
//	@SuppressWarnings("unchecked")
//	@Inject(method = "updateRegistryTags(Lnet/minecraft/core/RegistryAccess;Lnet/minecraft/tags/TagManager$LoadResult;)V", at = @At("HEAD"))
//	private static <T> void xkdeco$addDynamicTags(
//			RegistryAccess pRegistryAccess,
//			TagManager.LoadResult<T> pLoadResult,
//			CallbackInfo ci,
//			@Local(argsOnly = true) LocalRef<TagManager.LoadResult<T>> resultRef) {
//		if (pLoadResult.key().equals(Registries.BLOCK)) {
//			var tags = (Map<ResourceLocation, Collection<Holder<Block>>>) (Object) Maps.newHashMap(pLoadResult.tags());
//			MimicWallsLoader.addMimicWallTags(tags);
//			resultRef.set((TagManager.LoadResult<T>) new TagManager.LoadResult<>(Registries.BLOCK, tags));
//		}
//	}
}