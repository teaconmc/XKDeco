package org.teacon.xkdeco.block.place;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.teacon.xkdeco.block.loader.KBlockDefinition;
import org.teacon.xkdeco.block.loader.KBlockTemplate;
import org.teacon.xkdeco.block.setting.KBlockSettings;
import org.teacon.xkdeco.duck.KBlockProperties;
import org.teacon.xkdeco.util.CommonProxy;
import org.teacon.xkdeco.util.codec.CompactListCodec;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import snownee.kiwi.Kiwi;

public record PlaceChoices(
		List<PlaceTarget> target,
		List<Limit> limit,
		List<InterestProvider> interests) {

	public static final Codec<PlaceChoices> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			new CompactListCodec<>(PlaceTarget.CODEC).fieldOf("target").forGetter(PlaceChoices::target),
			new CompactListCodec<>(Limit.CODEC).optionalFieldOf("limit", List.of()).forGetter(PlaceChoices::limit),
			new CompactListCodec<>(InterestProvider.CODEC).optionalFieldOf("interests", List.of()).forGetter(PlaceChoices::interests)
	).apply(instance, PlaceChoices::new));

	public record ParsedResult(
			Map<ResourceLocation, PlaceChoices> choices,
			Map<KBlockTemplate, PlaceChoices> byTemplate) {
		public static ParsedResult of(
				Map<ResourceLocation, PlaceChoices> choices,
				Map<ResourceLocation, KBlockTemplate> templates) {
			Map<KBlockTemplate, PlaceChoices> byTemplate = Maps.newHashMap();
			for (PlaceChoices provider : choices.values()) {
				for (PlaceTarget target : provider.target) {
					if (target.type() != PlaceTarget.Type.TEMPLATE) {
						continue;
					}
					KBlockTemplate template = templates.get(target.id());
					if (template == null) {
						Kiwi.LOGGER.error("Template {} not found for place choices {}", target.id(), provider);
						continue;
					}
					PlaceChoices oldChoices = byTemplate.put(template, provider);
					if (oldChoices != null) {
						Kiwi.LOGGER.error("Duplicate place choices for template {}: {} and {}", template, oldChoices, provider);
					}
				}
			}
			return new ParsedResult(choices, byTemplate);
		}

		public boolean attachChoices(Block block, KBlockDefinition definition) {
			PlaceChoices choices = byTemplate.get(definition.template().template());
			KBlockSettings settings = KBlockSettings.of(block);
			if (settings == null && choices != null) {
				((KBlockProperties) block.properties).xkdeco$setSettings(settings = KBlockSettings.empty());
			}
			if (settings != null) {
				settings.placeChoices = choices;
			}
			return choices != null;
		}
	}

	public int test(BlockState baseState, BlockState targetState) {
		for (Limit limit : this.limit) {
			if (!limit.test(baseState, targetState)) {
				return Integer.MIN_VALUE;
			}
		}
		int interest = 0;
		for (InterestProvider provider : this.interests) {
			if (provider.when().smartTest(baseState, targetState)) {
				interest += provider.bonus;
			}
		}
		return interest;
	}

	public record Limit(String type, List<ParsedProtoTag> tags) {
		public static final Codec<Limit> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Codec.STRING.fieldOf("type").forGetter(Limit::type),
				new CompactListCodec<>(ParsedProtoTag.CODEC).fieldOf("tags").forGetter(Limit::tags)
		).apply(instance, Limit::new));

		public boolean test(BlockState baseState, BlockState targetState) {
			for (ParsedProtoTag tag : tags) {
				ParsedProtoTag resolvedTag = tag.resolve(baseState, Rotation.NONE);
				//noinspection SwitchStatementWithTooFewBranches
				boolean pass = switch (this.type) {
					case "has_tags" -> {
						for (Direction direction : CommonProxy.DIRECTIONS) {
							Collection<PlaceSlot> slots = PlaceSlot.find(targetState, direction);
							for (PlaceSlot slot : slots) {
								if (slot.hasTag(resolvedTag)) {
									yield true;
								}
							}
						}
						yield false;
					}
					default -> false;
				};
				if (!pass) {
					return false;
				}
			}
			return true;
		}
	}

	public record InterestProvider(StatePropertiesPredicate when, int bonus) {
		public static final Codec<InterestProvider> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				StatePropertiesPredicate.CODEC.fieldOf("when").forGetter(InterestProvider::when),
				Codec.INT.fieldOf("bonus").forGetter(InterestProvider::bonus)
		).apply(instance, InterestProvider::new));
	}
}
