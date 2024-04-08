package org.teacon.xkdeco.block.place;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.jetbrains.annotations.Nullable;
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
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
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
			Map<KBlockTemplate, PlaceChoices> byTemplate,
			Map<ResourceLocation, PlaceChoices> byBlock) {
		public static ParsedResult of(
				Map<ResourceLocation, PlaceChoices> choices,
				Map<ResourceLocation, KBlockTemplate> templates) {
			Map<KBlockTemplate, PlaceChoices> byTemplate = Maps.newHashMap();
			Map<ResourceLocation, PlaceChoices> byBlock = Maps.newHashMap();
			for (PlaceChoices provider : choices.values()) {
				for (PlaceTarget target : provider.target) {
					switch (target.type()) {
						case TEMPLATE -> {
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
						case BLOCK -> {
							PlaceChoices oldChoices = byBlock.put(target.id(), provider);
							if (oldChoices != null) {
								Kiwi.LOGGER.error("Duplicate place choices for block {}: {} and {}", target.id(), oldChoices, provider);
							}
						}
					}
				}
			}
			return new ParsedResult(choices, byTemplate, byBlock);
		}

		public boolean attachChoicesA(Block block, KBlockDefinition definition) {
			PlaceChoices choices = byTemplate.get(definition.template().template());
			setTo(block, choices);
			return choices != null;
		}

		public int attachChoicesB() {
			AtomicInteger counter = new AtomicInteger();
			byBlock.forEach((blockId, choices) -> {
				Block block = BuiltInRegistries.BLOCK.get(blockId);
				if (block == Blocks.AIR) {
					Kiwi.LOGGER.error("Block %s not found for place choices %s".formatted(blockId, choices));
					return;
				}
				setTo(block, choices);
				counter.incrementAndGet();
			});
			return counter.get();
		}
	}

	public static void setTo(Block block, @Nullable PlaceChoices choices) {
		KBlockSettings settings = KBlockSettings.of(block);
		if (settings == null && choices != null) {
			((KBlockProperties) block.properties).kiwi$setSettings(settings = KBlockSettings.empty());
		}
		if (settings != null) {
			settings.placeChoices = choices;
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
