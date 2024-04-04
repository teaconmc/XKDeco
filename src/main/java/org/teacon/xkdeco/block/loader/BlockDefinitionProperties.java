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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;
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
		PartialVanillaProperties vanillaProperties) {
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
				PartialVanillaProperties.MAP_CODEC.forGetter(BlockDefinitionProperties::vanillaProperties)
		).apply(instance, BlockDefinitionProperties::new));
	}

	public static MapCodec<Optional<BlockDefinitionProperties>> mapCodecField(MapCodec<Optional<KMaterial>> materialCodec) {
		return mapCodec(materialCodec).codec().optionalFieldOf(BlockCodecs.BLOCK_PROPERTIES_KEY);
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
		return new BlockDefinitionProperties(
				components,
				material,
				glassType,
				renderType,
				colorProvider,
				shape,
				collisionShape,
				interactionShape,
				vanillaProperties.merge(templateProps.vanillaProperties));
	}

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	private static <T> Optional<T> or(Optional<T> a, Optional<T> b) {
		return a.isPresent() ? a : b;
	}

	public record PartialVanillaProperties(
			Optional<Boolean> noCollision,
			Optional<Boolean> isRandomlyTicking,
			Optional<Integer> lightEmission,
			Optional<Boolean> dynamicShape,
			Optional<Boolean> noOcclusion,
			Optional<PushReaction> pushReaction,
			Optional<BlockBehaviour.OffsetType> offsetType,
			Optional<Boolean> replaceable,
			Optional<BlockBehaviour.StateArgumentPredicate<EntityType<?>>> isValidSpawn,
			Optional<BlockBehaviour.StatePredicate> isRedstoneConductor,
			Optional<BlockBehaviour.StatePredicate> isSuffocating,
			Optional<BlockBehaviour.StatePredicate> isViewBlocking,
			Optional<BlockBehaviour.StatePredicate> hasPostProcess,
			Optional<BlockBehaviour.StatePredicate> emissiveRendering) {
		public static final MapCodec<PartialVanillaProperties> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
				Codec.BOOL.optionalFieldOf("no_collision").forGetter(PartialVanillaProperties::noCollision),
				Codec.BOOL.optionalFieldOf("is_randomly_ticking").forGetter(PartialVanillaProperties::isRandomlyTicking),
				Codec.INT.optionalFieldOf("light_emission").forGetter(PartialVanillaProperties::lightEmission),
				Codec.BOOL.optionalFieldOf("dynamic_shape").forGetter(PartialVanillaProperties::dynamicShape),
				Codec.BOOL.optionalFieldOf("no_occlusion").forGetter(PartialVanillaProperties::noOcclusion),
				LoaderExtraCodecs.PUSH_REACTION_CODEC.optionalFieldOf("push_reaction").forGetter(PartialVanillaProperties::pushReaction),
				LoaderExtraCodecs.OFFSET_TYPE_CODEC.optionalFieldOf("offset_function")
						.forGetter(PartialVanillaProperties::offsetType),
				Codec.BOOL.optionalFieldOf("replaceable").forGetter(PartialVanillaProperties::replaceable),
				LoaderExtraCodecs.<EntityType<?>>stateArgumentPredicate().optionalFieldOf("is_valid_spawn")
						.forGetter(PartialVanillaProperties::isValidSpawn),
				LoaderExtraCodecs.STATE_PREDICATE.optionalFieldOf("is_redstone_conductor")
						.forGetter(PartialVanillaProperties::isRedstoneConductor),
				LoaderExtraCodecs.STATE_PREDICATE.optionalFieldOf("is_suffocating").forGetter(PartialVanillaProperties::isSuffocating),
				LoaderExtraCodecs.STATE_PREDICATE.optionalFieldOf("is_view_blocking").forGetter(PartialVanillaProperties::isViewBlocking),
				LoaderExtraCodecs.STATE_PREDICATE.optionalFieldOf("has_post_process").forGetter(PartialVanillaProperties::hasPostProcess),
				LoaderExtraCodecs.STATE_PREDICATE.optionalFieldOf("emissive_rendering")
						.forGetter(PartialVanillaProperties::emissiveRendering)
		).apply(instance, PartialVanillaProperties::new));

		public PartialVanillaProperties merge(PartialVanillaProperties templateProps) {
			Optional<Boolean> noCollision = or(this.noCollision, templateProps.noCollision);
			Optional<Boolean> isRandomlyTicking = or(this.isRandomlyTicking, templateProps.isRandomlyTicking);
			Optional<Integer> lightEmission = or(this.lightEmission, templateProps.lightEmission);
			Optional<Boolean> dynamicShape = or(this.dynamicShape, templateProps.dynamicShape);
			Optional<Boolean> noOcclusion = or(this.noOcclusion, templateProps.noOcclusion);
			Optional<PushReaction> pushReaction = or(this.pushReaction, templateProps.pushReaction);
			Optional<BlockBehaviour.OffsetType> offsetType = or(this.offsetType, templateProps.offsetType);
			Optional<Boolean> replaceable = or(this.replaceable, templateProps.replaceable);
			Optional<BlockBehaviour.StateArgumentPredicate<EntityType<?>>> isValidSpawn = or(this.isValidSpawn, templateProps.isValidSpawn);
			Optional<BlockBehaviour.StatePredicate> isRedstoneConductor = or(this.isRedstoneConductor, templateProps.isRedstoneConductor);
			Optional<BlockBehaviour.StatePredicate> isSuffocating = or(this.isSuffocating, templateProps.isSuffocating);
			Optional<BlockBehaviour.StatePredicate> isViewBlocking = or(this.isViewBlocking, templateProps.isViewBlocking);
			Optional<BlockBehaviour.StatePredicate> hasPostProcess = or(this.hasPostProcess, templateProps.hasPostProcess);
			Optional<BlockBehaviour.StatePredicate> emissiveRendering = or(this.emissiveRendering, templateProps.emissiveRendering);
			return new PartialVanillaProperties(
					noCollision,
					isRandomlyTicking,
					lightEmission,
					dynamicShape,
					noOcclusion,
					pushReaction,
					offsetType,
					replaceable,
					isValidSpawn,
					isRedstoneConductor,
					isSuffocating,
					isViewBlocking,
					hasPostProcess,
					emissiveRendering);
		}
	}
}
