package org.teacon.xkdeco.block.place;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import snownee.kiwi.Kiwi;
import snownee.kiwi.loader.Platform;

public record PlaceSlotProvider(
		PlaceTargetType targetType,
		ResourceLocation targetId,
		Optional<String> transformWith,
		List<String> tag,
		List<Slot> slots) {
	public static final Predicate<String> TAG_PATTERN = Pattern.compile("^[*@]?(?:[a-z_/]+:)?[a-z_/]+$").asPredicate();
	public static final Codec<String> TAG_CODEC = ExtraCodecs.validate(Codec.STRING, s -> {
		if (TAG_PATTERN.test(s)) {
			return DataResult.success(s);
		} else {
			return DataResult.error(() -> "Invalid tag: " + s);
		}
	});
	public static final Codec<PlaceSlotProvider> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ExtraCodecs.NON_EMPTY_STRING.fieldOf("target").forGetter(PlaceSlotProvider::targetToString),
			Codec.STRING.optionalFieldOf("transform_with").forGetter(PlaceSlotProvider::transformWith),
			TAG_CODEC.listOf().optionalFieldOf("tag", List.of()).forGetter(PlaceSlotProvider::tag),
			Slot.CODEC.listOf().fieldOf("slots").forGetter(PlaceSlotProvider::slots)
	).apply(instance, PlaceSlotProvider::create));

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	public static PlaceSlotProvider create(String target, Optional<String> transformWith, List<String> tag, List<Slot> slots) {
		PlaceTargetType targetType = target.startsWith("@") ? PlaceTargetType.TEMPLATE : PlaceTargetType.BLOCK;
		ResourceLocation targetId = new ResourceLocation(target.substring(targetType.prefix.length()));
		return new PlaceSlotProvider(targetType, targetId, transformWith, tag, slots);
	}

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

	public String targetToString() {
		return this.targetType.prefix + this.targetId;
	}

	public record Slot(
			List<StatePropertiesPredicate> when,
			Optional<String> transformWith,
			List<String> tag,
			EnumMap<Direction, Side> sides) {
		public static final Codec<Slot> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				new CompactListCodec<>(StatePropertiesPredicate.CODEC, false).fieldOf("when").forGetter(Slot::when),
				Codec.STRING.optionalFieldOf("transform_with").forGetter(Slot::transformWith),
				TAG_CODEC.listOf().optionalFieldOf("tag", List.of()).forGetter(Slot::tag),
				Codec.unboundedMap(LoaderExtraCodecs.DIRECTION, Side.CODEC)
						.xmap(EnumMap::new, $ -> $)
						.fieldOf("sides")
						.forGetter(Slot::sides)
		).apply(instance, Slot::new));
	}

	public record Side(List<String> tag) {
		public static final Codec<Side> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				TAG_CODEC.listOf().fieldOf("tag").forGetter(Side::tag)
		).apply(instance, Side::new));
	}

	public record ParsedResult(
			Map<ResourceLocation, PlaceSlotProvider> providers,
			ListMultimap<KBlockTemplate, PlaceSlotProvider> byTemplate) {
		public static ParsedResult of(Map<ResourceLocation, PlaceSlotProvider> providers, Map<ResourceLocation, KBlockTemplate> templates) {
			ListMultimap<KBlockTemplate, PlaceSlotProvider> byTemplate = ArrayListMultimap.create();
			for (PlaceSlotProvider provider : providers.values()) {
				KBlockTemplate template = templates.get(provider.targetId);
				if (template == null) {
					Kiwi.LOGGER.error("Template {} not found for slot provider {}", provider.targetId, provider);
					continue;
				}
				byTemplate.put(template, provider);
			}
			return new ParsedResult(providers, byTemplate);
		}

		public boolean attachSlots(Block block, KBlockDefinition definition) {
			boolean success = false;
			for (PlaceSlotProvider provider : byTemplate.get(definition.template().template())) {
				try {
					provider.attachSlots(block);
					success = true;
				} catch (Exception e) {
					Kiwi.LOGGER.error("Failed to attach slots for block %s with provider %s".formatted(block, provider), e);
				}
			}
			return success;
		}
	}

	private void attachSlots(Block block) {
		for (Slot slot : this.slots) {
			for (BlockState blockState : block.getStateDefinition().getPossibleStates()) {
				if (slot.when.stream().noneMatch(predicate -> predicate.test(blockState))) {
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
		if (primaryValue == null) {
			map.put("*%s".formatted(primaryKey.getValue()), "");
		} else {
			map.put("*%s:%s".formatted(primaryKey.getValue(), primaryValue), "");
		}
		return ImmutableSortedMap.copyOf(map, PlaceSlot.TAG_COMPARATOR);
	}
}
