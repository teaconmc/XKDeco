package org.teacon.xkdeco.mixin;

import java.util.List;
import java.util.Map;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.teacon.xkdeco.block.XKDBlock;
import org.teacon.xkdeco.init.MimicWallsLoader;

import com.google.common.collect.ImmutableMap;
import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.TagLoader;

@Mixin(MappedRegistry.class)
public abstract class MappedRegistryMixin<T> {
	@Shadow
	@Final
	private ResourceKey<? extends Registry<T>> key;

	@Shadow
	private HolderSet.Named<T> getOrCreateTagForRegistration(TagKey<T> tag) {
		throw new AssertionError();
	}

	@Inject(
			method = "prepareTagReload",
			at = @At(
					value = "INVOKE",
					target = "Lcom/google/common/collect/ImmutableMap$Builder;build()Lcom/google/common/collect/ImmutableMap;"))
	private void xkdeco$addMimicWallTags(
			TagLoader.LoadResult<T> tags,
			CallbackInfoReturnable<Registry.PendingTags<T>> cir,
			@Local ImmutableMap.Builder<TagKey<T>, HolderSet.Named<T>> pendingTagsBuilder,
			@Local Map<TagKey<T>, List<Holder<T>>> pendingContents) {
		if (this.key.equals(Registries.BLOCK)) {
			ensurePendingTag(pendingTagsBuilder, pendingContents, cast(BlockTags.WALLS));
			ensurePendingTag(pendingTagsBuilder, pendingContents, cast(BlockTags.MINEABLE_WITH_PICKAXE));
			ensurePendingTag(pendingTagsBuilder, pendingContents, cast(XKDBlock.NON_DIAGONAL_WALLS));
			MimicWallsLoader.addMimicWallBlockPendingTags(castMap(pendingContents));
		} else if (this.key.equals(Registries.ITEM)) {
			ensurePendingTag(pendingTagsBuilder, pendingContents, cast(ItemTags.WALLS));
			MimicWallsLoader.addMimicWallItemPendingTags(castMap(pendingContents));
		}
	}

	private void ensurePendingTag(
			ImmutableMap.Builder<TagKey<T>, HolderSet.Named<T>> pendingTagsBuilder,
			Map<TagKey<T>, List<Holder<T>>> pendingContents,
			TagKey<T> key) {
		if (!pendingContents.containsKey(key)) {
			pendingTagsBuilder.put(key, getOrCreateTagForRegistration(key));
			pendingContents.put(key, List.of());
		}
	}

	@SuppressWarnings("unchecked")
	private static <T, V> TagKey<T> cast(TagKey<V> key) {
		return (TagKey<T>) key;
	}

	@SuppressWarnings("unchecked")
	private static <T, V> Map<TagKey<T>, List<Holder<T>>> castMap(Map<TagKey<V>, List<Holder<V>>> map) {
		return (Map<TagKey<T>, List<Holder<T>>>) (Object) map;
	}
}
