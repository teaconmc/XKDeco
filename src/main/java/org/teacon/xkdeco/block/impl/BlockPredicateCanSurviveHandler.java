package org.teacon.xkdeco.block.impl;

import java.util.function.Predicate;

import org.teacon.xkdeco.block.setting.CanSurviveHandler;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

public record BlockPredicateCanSurviveHandler(Predicate<BlockState> predicate, Direction direction) implements CanSurviveHandler {
	@Override
	public boolean isSensitiveSide(BlockState state, Direction side) {
		return side == this.direction;
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
		return this.predicate.test(world.getBlockState(pos.relative(this.direction)));
	}
}
