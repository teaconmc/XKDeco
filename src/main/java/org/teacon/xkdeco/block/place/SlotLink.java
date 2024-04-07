package org.teacon.xkdeco.block.place;

import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.Nullable;
import org.teacon.xkdeco.block.loader.LoaderExtraCodecs;
import org.teacon.xkdeco.util.KBlockUtils;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.state.BlockState;

public record SlotLink(
		String from,
		String to,
		int interest,
		List<TagTest> testTag,
		ResultAction onLinkFrom,
		ResultAction onLinkTo,
		ResultAction onUnlinkFrom,
		ResultAction onUnlinkTo) {
	public static final Codec<String> PRIMARY_TAG_CODEC = ExtraCodecs.validate(Codec.STRING, s -> {
		if (s.startsWith("*")) {
			return DataResult.success(s);
		}
		return DataResult.error(() -> "Primary tag must start with *");
	});
	public static final Codec<SlotLink> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			PRIMARY_TAG_CODEC.fieldOf("from").forGetter(SlotLink::from),
			PRIMARY_TAG_CODEC.fieldOf("to").forGetter(SlotLink::to),
			Codec.INT.optionalFieldOf("interest", 100).forGetter(SlotLink::interest),
			TagTest.CODEC.listOf().optionalFieldOf("test_tag", List.of()).forGetter(SlotLink::testTag),
			fromToPairCodec("on_link").forGetter($ -> Pair.of($.onLinkFrom, $.onLinkTo)),
			fromToPairCodec("on_unlink").forGetter($ -> Pair.of($.onUnlinkFrom, $.onUnlinkTo))
	).apply(instance, SlotLink::create));

	private static MapCodec<Pair<ResultAction, ResultAction>> fromToPairCodec(String fieldName) {
		Codec<Pair<ResultAction, ResultAction>> pairCodec = RecordCodecBuilder.create(instance -> instance.group(
				ResultAction.CODEC.optionalFieldOf("from", ResultAction.EMPTY).forGetter(Pair::getFirst),
				ResultAction.CODEC.optionalFieldOf("to", ResultAction.EMPTY).forGetter(Pair::getSecond)
		).apply(instance, Pair::of));
		return pairCodec.optionalFieldOf(fieldName, Pair.of(ResultAction.EMPTY, ResultAction.EMPTY));
	}

	public static SlotLink create(
			String from,
			String to,
			int interest,
			List<TagTest> testTag,
			Pair<ResultAction, ResultAction> onLink,
			Pair<ResultAction, ResultAction> onUnlink) {
		return new SlotLink(
				from,
				to,
				interest,
				testTag,
				onLink.getFirst(),
				onLink.getSecond(),
				onUnlink.getFirst(),
				onUnlink.getSecond());
	}

	public record TagTest(String key, TagTestOperator operator) {
		public static final Codec<TagTest> CODEC = LoaderExtraCodecs.withAlternative(RecordCodecBuilder.create(instance -> instance.group(
				Codec.STRING.fieldOf("key").forGetter(TagTest::key),
				TagTestOperator.CODEC.fieldOf("operator").forGetter(TagTest::operator)
		).apply(instance, TagTest::new)), ExtraCodecs.NON_EMPTY_STRING.xmap(s -> new TagTest(s, TagTestOperator.EQUAL), TagTest::key));
	}

	public record ResultAction(Map<String, String> setProperties) {
		public static final Codec<ResultAction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Codec.unboundedMap(Codec.STRING, Codec.STRING).fieldOf("set_properties").forGetter(ResultAction::setProperties)
		).apply(instance, ResultAction::new));

		private static final ResultAction EMPTY = new ResultAction(Map.of());

		public BlockState apply(BlockState blockState) {
			for (Map.Entry<String, String> entry : setProperties.entrySet()) {
				blockState = KBlockUtils.setValueByString(blockState, entry.getKey(), entry.getValue());
			}
			return blockState;
		}
	}

	public static final Map<Pair<String, String>, SlotLink> LOOKUP = Maps.newHashMap();

	@Nullable
	public static SlotLink find(PlaceSlot slot1, PlaceSlot slot2) {
		String key1 = slot1.primaryTag();
		String key2 = slot2.primaryTag();
		Pair<String, String> key = isUprightLink(slot1, slot2) ? Pair.of(key1, key2) : Pair.of(key2, key1);
		return LOOKUP.get(key);
	}

	public static boolean isUprightLink(PlaceSlot slot1, PlaceSlot slot2) {
		return slot1.primaryTag().compareTo(slot2.primaryTag()) <= 0;
	}

	public boolean matches(PlaceSlot slot1, PlaceSlot slot2) {
		for (TagTest test : testTag) {
			String s1 = slot1.tags().get(test.key);
			String s2 = slot2.tags().get(test.key);
			if (s1 == null || s2 == null) {
				return false;
			}
			if (!test.operator.test().test(s1, s2)) {
				return false;
			}
		}
		return true;
	}

	public static void register(SlotLink link) {
		Pair<String, String> key = link.from.compareTo(link.to) < 0 ? Pair.of(link.from, link.to) : Pair.of(link.to, link.from);
		SlotLink oldLink = LOOKUP.put(key, link);
		if (oldLink != null) {
			throw new IllegalArgumentException("Duplicate link: " + oldLink + " and " + link);
		}
	}

	public static void clear() {
		LOOKUP.clear();
	}
}
