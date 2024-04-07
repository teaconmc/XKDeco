package org.teacon.xkdeco.block.place;

import java.util.BitSet;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.state.BlockState;

public record PlaceMatchResult(
		BlockState blockState,
		int interest,
		List<SlotLink> links,
		List<Vec3i> offsets,
		BitSet uprightStatus) implements Comparable<PlaceMatchResult> {
	@Override
	public int compareTo(@NotNull PlaceMatchResult o) {
		return Integer.compare(o.interest, this.interest);
	}
}
