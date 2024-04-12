package snownee.kiwi.customization.placement;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

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
import snownee.kiwi.customization.CustomizationHooks;
import snownee.kiwi.customization.block.KBlockSettings;
import snownee.kiwi.customization.block.loader.KBlockDefinition;
import snownee.kiwi.customization.block.loader.KBlockTemplate;
import snownee.kiwi.customization.block.loader.KHolder;
import snownee.kiwi.customization.block.loader.LoaderExtraCodecs;
import snownee.kiwi.customization.duck.KBlockProperties;
import snownee.kiwi.customization.util.codec.CompactListCodec;
import snownee.kiwi.loader.Platform;

public record PlaceChoices(
		List<PlaceTarget> target,
		List<Limit> limit,
		List<InterestProvider> interests,
		boolean skippable) {

	public static final Codec<PlaceChoices> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			new CompactListCodec<>(PlaceTarget.CODEC).fieldOf("target").forGetter(PlaceChoices::target),
			LoaderExtraCodecs.strictOptionalField(new CompactListCodec<>(Limit.CODEC), "limit", List.of()).forGetter(PlaceChoices::limit),
			LoaderExtraCodecs.strictOptionalField(new CompactListCodec<>(InterestProvider.CODEC), "interests", List.of())
					.forGetter(PlaceChoices::interests),
			Codec.BOOL.optionalFieldOf("skippable", true).forGetter(PlaceChoices::skippable)
	).apply(instance, PlaceChoices::new));

	public record Preparation(
			Map<ResourceLocation, PlaceChoices> choices,
			Map<KBlockTemplate, KHolder<PlaceChoices>> byTemplate,
			Map<ResourceLocation, KHolder<PlaceChoices>> byBlock) {
		public static Preparation of(
				Supplier<Map<ResourceLocation, PlaceChoices>> choicesSupplier,
				Map<ResourceLocation, KBlockTemplate> templates) {
			Map<ResourceLocation, PlaceChoices> choices = Platform.isDataGen() ? Map.of() : choicesSupplier.get();
			Map<KBlockTemplate, KHolder<PlaceChoices>> byTemplate = Maps.newHashMap();
			Map<ResourceLocation, KHolder<PlaceChoices>> byBlock = Maps.newHashMap();
			for (var entry : choices.entrySet()) {
				KHolder<PlaceChoices> holder = new KHolder<>(entry.getKey(), entry.getValue());
				for (PlaceTarget target : holder.value().target) {
					switch (target.type()) {
						case TEMPLATE -> {
							KBlockTemplate template = templates.get(target.id());
							if (template == null) {
								Kiwi.LOGGER.error("Template {} not found for place choices {}", target.id(), holder);
								continue;
							}
							KHolder<PlaceChoices> oldChoices = byTemplate.put(template, holder);
							if (oldChoices != null) {
								Kiwi.LOGGER.error("Duplicate place choices for template {}: {} and {}", template, oldChoices, holder);
							}
						}
						case BLOCK -> {
							KHolder<PlaceChoices> oldChoices = byBlock.put(target.id(), holder);
							if (oldChoices != null) {
								Kiwi.LOGGER.error("Duplicate place choices for block {}: {} and {}", target.id(), oldChoices, holder);
							}
						}
					}
				}
			}
			return new Preparation(choices, byTemplate, byBlock);
		}

		public boolean attachChoicesA(Block block, KBlockDefinition definition) {
			KHolder<PlaceChoices> choices = byTemplate.get(definition.template().template());
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

	public static void setTo(Block block, @Nullable KHolder<PlaceChoices> holder) {
		KBlockSettings settings = KBlockSettings.of(block);
		if (settings == null && holder != null) {
			((KBlockProperties) block.properties).kiwi$setSettings(settings = KBlockSettings.empty());
		}
		if (settings != null) {
			settings.placeChoices = holder == null ? null : holder.value();
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
						for (Direction direction : CustomizationHooks.DIRECTIONS) {
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
