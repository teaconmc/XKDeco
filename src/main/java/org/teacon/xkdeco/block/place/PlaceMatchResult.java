package org.teacon.xkdeco.block.place;

import org.jetbrains.annotations.NotNull;

import net.minecraft.world.level.block.state.BlockState;

public record PlaceMatchResult(BlockState blockState, int interest) implements Comparable<PlaceMatchResult> {
	@Override
	public int compareTo(@NotNull PlaceMatchResult o) {
		return Integer.compare(o.interest, this.interest);
	}
}
