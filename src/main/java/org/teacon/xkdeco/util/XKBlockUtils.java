package org.teacon.xkdeco.util;

import org.jetbrains.annotations.Nullable;
import org.teacon.xkdeco.block.setting.XKBlockSettings;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public interface XKBlockUtils {
	default @Nullable BlockState componentsUpdateShape(
			BlockState pState,
			Direction pDirection,
			BlockState pNeighborState,
			LevelAccessor pLevel,
			BlockPos pPos,
			BlockPos pNeighborPos) {
		XKBlockSettings settings = XKBlockSettings.of(this);
		if (settings == null) {
			return pState;
		}
		return settings.updateShape(pState, pDirection, pNeighborState, pLevel, pPos, pNeighborPos);
	}

	default @Nullable BlockState componentsGetStateForPlacement(BlockState pState, BlockPlaceContext pContext) {
		XKBlockSettings settings = XKBlockSettings.of(this);
		if (settings == null) {
			return pState;
		}
		return settings.getStateForPlacement(pState, pContext);
	}
}
