package org.teacon.xkdeco.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;

public interface XKDPlatformBlock {
	default boolean xkdeco$makesOpenTrapdoorAboveClimbable(BlockState state, LevelReader level, BlockPos pos, BlockState trapdoorState) {
		return CommonProxy.isLadder(state, level, pos) && state.hasProperty(LadderBlock.FACING) &&
				state.getValue(LadderBlock.FACING) == trapdoorState.getValue(TrapDoorBlock.FACING);
	}
}
