package org.teacon.xkdeco.block.place;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public record PlaceSlot(Direction side, SortedSet<String> tags) {
	private static Interner<PlaceSlot> INTERNER = Interners.newStrongInterner();
	private static final Comparator<String> TAG_COMPARATOR = (a, b) -> {
		// make sure that the tag prefixed with * comes first
		boolean aStar = a.charAt(0) == '*';
		boolean bStar = b.charAt(0) == '*';
		if (aStar != bStar) {
			return aStar ? -1 : 1;
		}
		return a.compareTo(b);
	};
	private static final Map<Pair<BlockState, Direction>, Collection<PlaceSlot>> BLOCK_STATE_LOOKUP = Maps.newHashMap();

	public static Stream<PlaceSlot> find(BlockState blockState, Direction side) {
		return BLOCK_STATE_LOOKUP.getOrDefault(Pair.of(blockState, side), List.of()).stream();
	}

	public static PlaceSlot create(Direction side, Iterable<String> tags) {
		ImmutableSortedSet.Builder<String> builder = ImmutableSortedSet.orderedBy(TAG_COMPARATOR);
		boolean hasPrimary = false;
		for (String tag : tags) {
			if (tag.charAt(0) == '*') {
				if (hasPrimary) {
					throw new IllegalArgumentException("Only one primary tag is allowed");
				}
				hasPrimary = true;
			}
			builder.add(tag);
		}
		return INTERNER.intern(new PlaceSlot(side, builder.build()));
	}

	public static void register(BlockState blockState, PlaceSlot placeSlot) {
		BLOCK_STATE_LOOKUP.computeIfAbsent(Pair.of(blockState, placeSlot.side), key -> Lists.newArrayList()).add(placeSlot);
	}

	public static void clear() {
		BLOCK_STATE_LOOKUP.clear();
		INTERNER = Interners.newStrongInterner();
	}
}
