package org.teacon.xkdeco.block.loader;

import java.util.Map;
import java.util.Optional;

import org.teacon.xkdeco.block.setting.KBlockSettings;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import snownee.kiwi.KiwiModule;

public record KBlockDefinition(
		ConfiguredBlockTemplate template,
		KBlockSettings settings,
		KMaterial material,
		KiwiModule.RenderLayer.Layer renderType) {
	public static Codec<KBlockDefinition> codec(
			Map<ResourceLocation, KBlockTemplate> templates,
			MapCodec<Optional<KMaterial>> materialCodec) {
		KBlockTemplate defaultTemplate = templates.get(new ResourceLocation("block"));
		Preconditions.checkNotNull(defaultTemplate);
		ConfiguredBlockTemplate defaultConfiguredTemplate = new ConfiguredBlockTemplate(defaultTemplate);
		return RecordCodecBuilder.create(instance -> instance.group(
				ConfiguredBlockTemplate.codec(templates)
						.optionalFieldOf("template", defaultConfiguredTemplate)
						.forGetter(KBlockDefinition::template),
				materialCodec.forGetter($ -> Optional.ofNullable($.material())),
				LoaderExtraCodecs.RENDER_TYPE.optionalFieldOf("render_type").forGetter($ -> Optional.ofNullable($.renderType()))
		).apply(instance, KBlockDefinition::create));
	}

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	public static KBlockDefinition create(
			ConfiguredBlockTemplate template,
			Optional<KMaterial> material,
			Optional<KiwiModule.RenderLayer.Layer> renderType) {
		return new KBlockDefinition(
				template,
				KBlockSettings.EMPTY,
				material.orElse(null),
				renderType.orElse(null));
	}

	public Block createBlock() {
		return this.template.template().createBlock(settings, template.json());
	}
}
