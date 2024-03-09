package org.teacon.xkdeco.block;

import java.util.Optional;

import org.teacon.xkdeco.util.IntTriple;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;

// roof related blocks which have complex connection logic
public interface XKDecoBlockRoof extends SimpleWaterloggedBlock {
	Iterable<BlockState> getPlacementChoices(boolean waterlogged, boolean updateSide, Direction... lookingSides);

	Optional<BlockState> getUpdateShapeChoice(BlockState state, Direction side);

	IntTriple getSideHeight(BlockState state, Direction horizontalSide);
}
