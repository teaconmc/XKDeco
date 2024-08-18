package org.teacon.xkdeco.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.extensions.IBlockExtension;

public interface XKDPlatformBlock extends IBlockExtension {
	@Override
	default boolean makesOpenTrapdoorAboveClimbable(BlockState state, LevelReader level, BlockPos pos, BlockState trapdoorState) {
		return isLadder(state, level, pos, null) && state.hasProperty(LadderBlock.FACING) &&
				state.getValue(LadderBlock.FACING) == trapdoorState.getValue(TrapDoorBlock.FACING);
	}
}
