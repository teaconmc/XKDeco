package org.teacon.xkdeco.block.place;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.teacon.xkdeco.block.loader.KBlockDefinition;
import org.teacon.xkdeco.block.loader.KBlockTemplate;
import org.teacon.xkdeco.block.loader.LoaderExtraCodecs;
import org.teacon.xkdeco.util.resource.OneTimeLoader;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.ListMultimap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import snownee.kiwi.Kiwi;

public record PlaceSlotProvider(TargetType targetType, ResourceLocation targetId, List<Slot> slots) {
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
			Slot.CODEC.listOf().fieldOf("slots").forGetter(PlaceSlotProvider::slots)
	).apply(instance, PlaceSlotProvider::create));
	private static final List<Direction> DIRECTIONS = Direction.stream().toList();

	public static PlaceSlotProvider create(String target, List<Slot> slots) {
		TargetType targetType = target.startsWith("@") ? TargetType.TEMPLATE : TargetType.BLOCK;
		ResourceLocation targetId = new ResourceLocation(target.substring(targetType.prefix.length()));
		return new PlaceSlotProvider(targetType, targetId, slots);
	}

	public static ParsedResult reload(ResourceManager resourceManager, Map<ResourceLocation, KBlockTemplate> templates) {
		PlaceSlot.clear();
		return ParsedResult.of(
				OneTimeLoader.load(
						resourceManager,
						"kiwi/place_slot/provider",
						PlaceSlotProvider.CODEC),
				templates);
	}

	private String targetToString() {
		return this.targetType.prefix + this.targetId;
	}

	public enum TargetType {
		TEMPLATE("@"),
		BLOCK("");

		public final String prefix;

		TargetType(String prefix) {
			this.prefix = prefix;
		}
	}

	public record Slot(StatePropertiesPredicate when, Optional<String> transformWith, List<String> tag, EnumMap<Direction, Side> sides) {
		public static final Codec<Slot> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				StatePropertiesPredicate.CODEC.fieldOf("when").forGetter(Slot::when),
				Codec.STRING.optionalFieldOf("transform_with").forGetter(Slot::transformWith),
				TAG_CODEC.listOf().fieldOf("tag").forGetter(Slot::tag),
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
			List<PlaceSlotProvider> list = byTemplate.get(definition.template().template());
			for (PlaceSlotProvider provider : list) {
				provider.attachSlots(block);
			}
			return !list.isEmpty();
		}
	}

	private void attachSlots(Block block) {
		for (Slot slot : this.slots) {
			for (BlockState blockState : block.getStateDefinition().getPossibleStates()) {
				if (!slot.when.test(blockState)) {
					continue;
				}
				List<String> parsedTags = parseTags(slot.tag.stream(), blockState).toList();
				for (Direction direction : DIRECTIONS) {
					Side side = slot.sides.get(direction);
					if (side == null) {
						continue;
					}
					PlaceSlot placeSlot = PlaceSlot.create(
							direction,
							Iterables.concat(parsedTags, parseTags(side.tag.stream(), blockState).toList()));
					PlaceSlot.register(blockState, placeSlot);
				}
			}
		}
	}

	public static Stream<String> parseTags(Stream<String> tags, BlockState blockState) {
		return tags.map(tag -> {
			if (tag.startsWith("@")) {
				String key = tag.substring(1);
				Property<?> property = StatePropertiesPredicate.getProperty(blockState, key);
				String value = blockState.getValue(property).toString();
				return key + ":" + value;
			} else {
				return tag;
			}
		});
	}

}
