package org.teacon.xkdeco.block.loader;

import java.util.Map;
import java.util.Optional;

import org.teacon.xkdeco.block.setting.KBlockComponent;
import org.teacon.xkdeco.block.setting.KBlockSettings;

import com.google.common.base.Preconditions;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import snownee.kiwi.util.VanillaActions;

public record KBlockDefinition(ConfiguredBlockTemplate template, BlockDefinitionProperties properties) {
	public KBlockDefinition(ConfiguredBlockTemplate template, BlockDefinitionProperties properties) {
		this.template = template;
		this.properties = template.template().properties().map(properties::merge).orElse(properties);
	}

	public static Codec<KBlockDefinition> codec(
			Map<ResourceLocation, KBlockTemplate> templates,
			MapCodec<Optional<KMaterial>> materialCodec) {
		KBlockTemplate defaultTemplate = templates.get(new ResourceLocation("block"));
		Preconditions.checkNotNull(defaultTemplate);
		ConfiguredBlockTemplate defaultConfiguredTemplate = new ConfiguredBlockTemplate(defaultTemplate);
		return RecordCodecBuilder.create(instance -> instance.group(
				LoaderExtraCodecs.strictOptionalField(
								ConfiguredBlockTemplate.codec(templates),
								"template",
								defaultConfiguredTemplate)
						.forGetter(KBlockDefinition::template),
				BlockDefinitionProperties.mapCodec(materialCodec).forGetter(KBlockDefinition::properties)
		).apply(instance, KBlockDefinition::new));
	}

	public Block createBlock(ResourceLocation id) {
		KBlockSettings.Builder builder = KBlockSettings.builder();
		properties.glassType().ifPresent(builder::glassType);
		BlockDefinitionProperties.PartialVanillaProperties vanilla = properties.vanillaProperties();
		builder.configure($ -> {
			vanilla.lightEmission().ifPresent(i -> $.lightLevel($$ -> i));
			vanilla.pushReaction().ifPresent($::pushReaction);
			vanilla.emissiveRendering().ifPresent($::emissiveRendering);
			vanilla.hasPostProcess().ifPresent($::hasPostProcess);
			vanilla.isRedstoneConductor().ifPresent($::isRedstoneConductor);
			vanilla.isSuffocating().ifPresent($::isSuffocating);
			vanilla.isViewBlocking().ifPresent($::isViewBlocking);
			vanilla.isValidSpawn().ifPresent($::isValidSpawn);
			vanilla.offsetType().ifPresent($::offsetType);
			if (vanilla.noCollision().orElse(false)) {
				$.noCollission();
			}
			if (vanilla.noOcclusion().orElse(false)) {
				$.noOcclusion();
			}
			if (vanilla.isRandomlyTicking().orElse(false)) {
				$.randomTicks();
			}
			if (vanilla.dynamicShape().orElse(false)) {
				$.dynamicShape();
			}
			if (vanilla.replaceable().orElse(false)) {
				$.replaceable();
			}
		});
		properties.material().ifPresent(mat -> {
			builder.configure($ -> {
				$.strength(mat.destroyTime(), mat.explosionResistance());
				$.sound(mat.soundType());
				$.instrument(mat.instrument());
				$.mapColor(mat.defaultMapColor());
				if (mat.ignitedByLava()) {
					$.ignitedByLava();
				}
				if (mat.requiresCorrectToolForDrops()) {
					$.requiresCorrectToolForDrops();
				}
			});
		});
		properties.shape().ifPresent(builder::shape);
		properties.collisionShape().ifPresent(builder::collisionShape);
		properties.interactionShape().ifPresent(builder::interactionShape);
		properties.canSurviveHandler().ifPresent(builder::canSurviveHandler);
		for (Either<KBlockComponent, String> component : properties.components()) {
			if (component.left().isPresent()) {
				builder.component(component.left().get());
			} else {
				String s = component.right().orElseThrow();
				boolean remove = s.startsWith("-");
				if (remove) {
					s = s.substring(1);
				}
				KBlockComponent.Type<?> type = LoaderExtraRegistries.BLOCK_COMPONENT.get(new ResourceLocation(s));
				if (remove) {
					builder.removeComponent(type);
				} else {
					builder.component(KBlockComponents.getSimpleInstance(type));
				}
			}
		}
		Block block = template.template().createBlock(builder.get(), template.json());
		properties.material().ifPresent(mat -> {
			VanillaActions.setFireInfo(block, mat.igniteOdds(), mat.burnOdds());
		});
		return block;
	}
}
