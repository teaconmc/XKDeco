package org.teacon.xkdeco.block.loader;

import java.util.Optional;

import org.teacon.xkdeco.block.setting.KBlockSettings;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import snownee.kiwi.KiwiModule;

public record KBlockDefinition(KBlockSettings settings, KMaterial material, KiwiModule.RenderLayer.Layer renderType) {
	public static Codec<KBlockDefinition> codec(Codec<KMaterial> materialCodec) {
		return RecordCodecBuilder.create(instance -> instance.group(
						materialCodec.optionalFieldOf("material").forGetter($ -> Optional.ofNullable($.material())),
						LoaderExtraCodecs.RENDER_TYPE.optionalFieldOf("render_type").forGetter($ -> Optional.ofNullable($.renderType())))
				.apply(instance, KBlockDefinition::create));
	}

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	public static KBlockDefinition create(Optional<KMaterial> material, Optional<KiwiModule.RenderLayer.Layer> renderType) {
		return new KBlockDefinition(KBlockSettings.EMPTY, material.orElse(null), renderType.orElse(null));
	}
}
