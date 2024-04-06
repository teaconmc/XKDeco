package org.teacon.xkdeco.block.place;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public record PlaceSlot(Direction side, SortedMap<String, String> tags) {
	private static Interner<PlaceSlot> INTERNER = Interners.newStrongInterner();
	public static final Comparator<String> TAG_COMPARATOR = (a, b) -> {
		// make sure that the tag prefixed with * comes first
		boolean aStar = a.charAt(0) == '*';
		boolean bStar = b.charAt(0) == '*';
		if (aStar != bStar) {
			return aStar ? -1 : 1;
		}
		return a.compareTo(b);
	};
	private static final Map<Pair<BlockState, Direction>, Collection<PlaceSlot>> BLOCK_STATE_LOOKUP = Maps.newHashMap();

	public static Collection<PlaceSlot> find(BlockState blockState, Direction side) {
		return BLOCK_STATE_LOOKUP.getOrDefault(Pair.of(blockState, side), List.of());
	}

	public static PlaceSlot create(Direction side, ImmutableSortedMap<String, String> tags) {
		return INTERNER.intern(new PlaceSlot(side, tags));
	}

	public static void register(BlockState blockState, PlaceSlot placeSlot) {
		Collection<PlaceSlot> slots = BLOCK_STATE_LOOKUP.computeIfAbsent(Pair.of(blockState, placeSlot.side), key -> Lists.newArrayList());
		if (!slots.isEmpty()) {
			String primaryTag = placeSlot.primaryTag();
			if (slots.stream().anyMatch(slot -> slot.primaryTag().equals(primaryTag))) {
				throw new IllegalArgumentException("Primary tag conflict: " + primaryTag);
			}
		}
		slots.add(placeSlot);
	}

	public static void clear() {
		BLOCK_STATE_LOOKUP.clear();
		INTERNER = Interners.newStrongInterner();
	}

	public String primaryTag() {
		return tags.firstKey();
	}

	public List<String> tagList() {
		List<String> list = Lists.newArrayListWithCapacity(tags.size());
		tags.forEach((k, v) -> {
			if (v.isEmpty()) {
				list.add(k);
			} else {
				list.add(k + "=" + v);
			}
		});
		return list;
	}
}
