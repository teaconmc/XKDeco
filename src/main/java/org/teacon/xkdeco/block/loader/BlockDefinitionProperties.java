package org.teacon.xkdeco.block.loader;

import java.util.List;
import java.util.Optional;

import org.teacon.xkdeco.block.setting.GlassType;
import org.teacon.xkdeco.block.setting.KBlockComponent;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceLocation;
import snownee.kiwi.KiwiModule;

public record BlockDefinitionProperties(
		List<Either<KBlockComponent, String>> components,
		Optional<KMaterial> material,
		Optional<GlassType> glassType,
		Optional<KiwiModule.RenderLayer.Layer> renderType,
		Optional<ResourceLocation> colorProvider,
		Optional<ResourceLocation> shape,
		Optional<ResourceLocation> collisionShape,
		Optional<ResourceLocation> interactionShape,
		int lightEmission,
		boolean noCollision,
		boolean noOcclusion) {
	public static MapCodec<BlockDefinitionProperties> mapCodec(MapCodec<Optional<KMaterial>> materialCodec) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(
				Codec.either(KBlockComponent.DIRECT_CODEC, Codec.STRING)
						.listOf()
						.optionalFieldOf("components", List.of())
						.forGetter(BlockDefinitionProperties::components),
				materialCodec.forGetter(BlockDefinitionProperties::material),
				LoaderExtraCodecs.GLASS_TYPE_CODEC.optionalFieldOf("glass_type").forGetter(BlockDefinitionProperties::glassType),
				LoaderExtraCodecs.RENDER_TYPE.optionalFieldOf("render_type").forGetter(BlockDefinitionProperties::renderType),
				ResourceLocation.CODEC.optionalFieldOf("color_provider").forGetter(BlockDefinitionProperties::colorProvider),
				ResourceLocation.CODEC.optionalFieldOf("shape").forGetter(BlockDefinitionProperties::shape),
				ResourceLocation.CODEC.optionalFieldOf("collision_shape").forGetter(BlockDefinitionProperties::collisionShape),
				ResourceLocation.CODEC.optionalFieldOf("interaction_shape").forGetter(BlockDefinitionProperties::interactionShape),
				Codec.INT.optionalFieldOf("light_emission", 0).forGetter(BlockDefinitionProperties::lightEmission),
				Codec.BOOL.optionalFieldOf("no_collision", false).forGetter(BlockDefinitionProperties::noCollision),
				Codec.BOOL.optionalFieldOf("no_occlusion", false).forGetter(BlockDefinitionProperties::noOcclusion)
		).apply(instance, BlockDefinitionProperties::new));
	}

	public BlockDefinitionProperties merge(BlockDefinitionProperties templateProps) {
		List<Either<KBlockComponent, String>> components;
		if (this.components.isEmpty()) {
			components = templateProps.components;
		} else if (templateProps.components.isEmpty()) {
			components = this.components;
		} else {
			components = Lists.newArrayListWithExpectedSize(this.components.size() + templateProps.components.size());
			components.addAll(this.components);
			components.addAll(templateProps.components);
		}
		Optional<KMaterial> material = or(this.material, templateProps.material);
		Optional<GlassType> glassType = or(this.glassType, templateProps.glassType);
		Optional<KiwiModule.RenderLayer.Layer> renderType = or(this.renderType, templateProps.renderType);
		Optional<ResourceLocation> colorProvider = or(this.colorProvider, templateProps.colorProvider);
		Optional<ResourceLocation> shape = or(this.shape, templateProps.shape);
		Optional<ResourceLocation> collisionShape = or(this.collisionShape, templateProps.collisionShape);
		Optional<ResourceLocation> interactionShape = or(this.interactionShape, templateProps.interactionShape);
		int lightEmission = this.lightEmission > 0 ? this.lightEmission : templateProps.lightEmission;
		boolean noCollision = this.noCollision || templateProps.noCollision;
		boolean noOcclusion = this.noOcclusion || templateProps.noOcclusion;
		return new BlockDefinitionProperties(
				components,
				material,
				glassType,
				renderType,
				colorProvider,
				shape,
				collisionShape,
				interactionShape,
				lightEmission,
				noCollision,
				noOcclusion);
	}

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	private static <T> Optional<T> or(Optional<T> a, Optional<T> b) {
		return a.isPresent() ? a : b;
	}

//	public record PartialVanillaProperties(
//			int lightEmission,
//			boolean noCollision,
//			boolean noOcclusion) {
//	}
}
