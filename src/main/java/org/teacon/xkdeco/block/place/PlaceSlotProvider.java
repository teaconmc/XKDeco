package org.teacon.xkdeco.block.place;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import org.apache.commons.lang3.mutable.MutableObject;
import org.teacon.xkdeco.block.loader.KBlockDefinition;
import org.teacon.xkdeco.block.loader.KBlockTemplate;
import org.teacon.xkdeco.block.loader.LoaderExtraCodecs;
import org.teacon.xkdeco.util.CommonProxy;
import org.teacon.xkdeco.util.KBlockUtils;
import org.teacon.xkdeco.util.codec.CompactListCodec;
import org.teacon.xkdeco.util.resource.OneTimeLoader;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Streams;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import snownee.kiwi.Kiwi;
import snownee.kiwi.loader.Platform;

public record PlaceSlotProvider(
		List<PlaceTarget> target,
		Optional<String> transformWith,
		List<String> tag,
		List<Slot> slots) {
	public static final Predicate<String> TAG_PATTERN = Pattern.compile("^[*@]?(?:[a-z0-9_/.]+:)?[a-z0-9_/.]+$").asPredicate();
	public static final Codec<String> TAG_CODEC = ExtraCodecs.validate(Codec.STRING, s -> {
		if (TAG_PATTERN.test(s)) {
			return DataResult.success(s);
		} else {
			return DataResult.error(() -> "Bad tag format: " + s);
		}
	});
	public static final Codec<PlaceSlotProvider> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			new CompactListCodec<>(PlaceTarget.CODEC).fieldOf("target").forGetter(PlaceSlotProvider::target),
			LoaderExtraCodecs.strictOptionalField(Codec.STRING, "transform_with").forGetter(PlaceSlotProvider::transformWith),
			LoaderExtraCodecs.strictOptionalField(TAG_CODEC.listOf(), "tag", List.of()).forGetter(PlaceSlotProvider::tag),
			Slot.CODEC.listOf().fieldOf("slots").forGetter(PlaceSlotProvider::slots)
	).apply(instance, PlaceSlotProvider::new));

	public static ParsedResult reload(ResourceManager resourceManager, Map<ResourceLocation, KBlockTemplate> templates) {
		PlaceSlot.clear();
		return ParsedResult.of(
				Platform.isDataGen() ?
						Map.of() :
						OneTimeLoader.load(
								resourceManager,
								"kiwi/place_slot/provider",
								PlaceSlotProvider.CODEC),
				templates);
	}

	public record Slot(
			List<StatePropertiesPredicate> when,
			Optional<String> transformWith,
			List<String> tag,
			Map<Direction, Side> sides) {
		public static final Codec<Slot> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				LoaderExtraCodecs.strictOptionalField(new CompactListCodec<>(StatePropertiesPredicate.CODEC, false), "when", List.of())
						.forGetter(Slot::when),
				LoaderExtraCodecs.strictOptionalField(Codec.STRING, "transform_with").forGetter(Slot::transformWith),
				LoaderExtraCodecs.strictOptionalField(TAG_CODEC.listOf(), "tag", List.of()).forGetter(Slot::tag),
				Codec.unboundedMap(LoaderExtraCodecs.DIRECTION, Side.CODEC)
						.xmap(Map::copyOf, Function.identity())
						.fieldOf("sides")
						.forGetter(Slot::sides)
		).apply(instance, Slot::new));
	}

	public record Side(List<String> tag) {
		public static final Codec<Side> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				LoaderExtraCodecs.strictOptionalField(TAG_CODEC.listOf(), "tag", List.of()).forGetter(Side::tag)
		).apply(instance, Side::new));
	}

	public record ParsedResult(
			Map<ResourceLocation, PlaceSlotProvider> providers,
			ListMultimap<KBlockTemplate, PlaceSlotProvider> byTemplate,
			ListMultimap<ResourceLocation, PlaceSlotProvider> byBlock) {
		public static ParsedResult of(Map<ResourceLocation, PlaceSlotProvider> providers, Map<ResourceLocation, KBlockTemplate> templates) {
			ListMultimap<KBlockTemplate, PlaceSlotProvider> byTemplate = ArrayListMultimap.create();
			ListMultimap<ResourceLocation, PlaceSlotProvider> byBlock = ArrayListMultimap.create();
			for (PlaceSlotProvider provider : providers.values()) {
				for (PlaceTarget target : provider.target) {
					switch (target.type()) {
						case TEMPLATE -> {
							KBlockTemplate template = templates.get(target.id());
							if (template == null) {
								Kiwi.LOGGER.error("Template {} not found for slot provider {}", target.id(), provider);
								continue;
							}
							byTemplate.put(template, provider);
						}
						case BLOCK -> byBlock.put(target.id(), provider);
					}
				}
			}
			return new ParsedResult(providers, byTemplate, byBlock);
		}

		public void attachSlotsA(Block block, KBlockDefinition definition) {
			for (PlaceSlotProvider provider : byTemplate.get(definition.template().template())) {
				try {
					provider.attachSlots(block);
				} catch (Exception e) {
					Kiwi.LOGGER.error("Failed to attach slots for block %s with provider %s".formatted(block, provider), e);
				}
			}
		}

		public void attachSlotsB() {
			byBlock.asMap().forEach((blockId, providers) -> {
				Block block = BuiltInRegistries.BLOCK.get(blockId);
				if (block == Blocks.AIR) {
					Kiwi.LOGGER.error("Block %s not found for slot providers %s".formatted(blockId, providers));
					return;
				}
				for (PlaceSlotProvider provider : providers) {
					try {
						provider.attachSlots(block);
					} catch (Exception e) {
						Kiwi.LOGGER.error("Failed to attach slots for block %s with provider %s".formatted(block, provider), e);
					}
				}
			});
		}
	}

	private void attachSlots(Block block) {
		for (Slot slot : this.slots) {
			for (BlockState blockState : block.getStateDefinition().getPossibleStates()) {
				if (!slot.when.isEmpty() && slot.when.stream().noneMatch(predicate -> predicate.test(blockState))) {
					continue;
				}
				for (Direction direction : CommonProxy.DIRECTIONS) {
					Side side = slot.sides.get(direction);
					if (side == null) {
						continue;
					}
					PlaceSlot placeSlot = PlaceSlot.create(direction, generateTags(slot, side, blockState, Rotation.NONE));
					PlaceSlot.register(blockState, placeSlot);
				}
				String transformWith = (slot.transformWith.isPresent() ? slot.transformWith : this.transformWith).orElse("none");
				if (!"none".equals(transformWith)) {
					Property<?> property = KBlockUtils.getProperty(blockState, transformWith);
					if (!(property instanceof DirectionProperty directionProperty)) {
						throw new IllegalArgumentException("Invalid transform_with property: " + transformWith);
					}
					attachSlotWithTransformation(slot, blockState, directionProperty);
				}
			}
		}
	}

	private void attachSlotWithTransformation(Slot slot, BlockState blockState, DirectionProperty property) {
		Direction baseDirection = blockState.getValue(property);
		BlockState rotatedState = blockState;
		while ((rotatedState = rotatedState.cycle(property)) != blockState) {
			Direction newDirection = rotatedState.getValue(property);
			if (Direction.Plane.VERTICAL.test(newDirection)) {
				continue;
			}
			Rotation rotation = null;
			for (Rotation value : Rotation.values()) {
				if (value.rotate(baseDirection) == newDirection) {
					rotation = value;
					break;
				}
			}
			if (rotation == null) {
				throw new IllegalStateException("Invalid direction: " + newDirection);
			}
			for (Direction direction : CommonProxy.DIRECTIONS) {
				Side side = slot.sides.get(direction);
				if (side == null) {
					continue;
				}
				PlaceSlot placeSlot = PlaceSlot.create(rotation.rotate(direction), generateTags(slot, side, rotatedState, rotation));
				PlaceSlot.register(rotatedState, placeSlot);
			}
		}
	}

	private ImmutableSortedMap<String, String> generateTags(Slot slot, Side side, BlockState rotatedState, Rotation rotation) {
		Map<String, String> map = Maps.newHashMap();
		MutableObject<String> primaryKey = new MutableObject<>();
		Streams.concat(tag.stream(), slot.tag.stream(), side.tag.stream()).forEach(s -> {
			ParsedProtoTag tag = ParsedProtoTag.of(s).resolve(rotatedState, rotation);
			if (tag.prefix().equals("*")) {
				if (primaryKey.getValue() == null) {
					primaryKey.setValue(tag.key());
				} else if (!Objects.equals(primaryKey.getValue(), tag.key())) {
					throw new IllegalArgumentException("Only one primary tag is allowed");
				}
			}
			map.put(tag.key(), tag.value());
		});
		if (primaryKey.getValue() == null) {
			throw new IllegalArgumentException("Primary tag is required");
		}
		String primaryValue = map.get(primaryKey.getValue());
		map.remove(primaryKey.getValue());
		if (primaryValue.isEmpty()) {
			map.put("*%s".formatted(primaryKey.getValue()), "");
		} else {
			map.put("*%s:%s".formatted(primaryKey.getValue(), primaryValue), "");
		}
		return ImmutableSortedMap.copyOf(map, PlaceSlot.TAG_COMPARATOR);
	}
}
