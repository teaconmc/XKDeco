package org.teacon.xkdeco.block.place;

import org.teacon.xkdeco.XKDeco;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class PlaceDebugFeature {
	public static boolean isEnabled(Player player) {
		return player != null && player.isCreative() && player.getOffhandItem().is(Items.CHAINMAIL_HELMET);
	}

	public static void removeDebugBlocks(Level level, BlockPos start) {
		BlockPos.MutableBlockPos pos = start.mutable();
		pos.move(Direction.UP);
		while (isBlockToRemove(level.getBlockState(pos))) {
			level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
			pos.move(Direction.UP);
		}
	}

	private static boolean isBlockToRemove(BlockState blockState) {
		if (blockState.is(Blocks.BEDROCK)) {
			return true;
		}
		String namespace = BuiltInRegistries.BLOCK.getKey(blockState.getBlock()).getNamespace();
		return XKDeco.ID.equals(namespace);
	}
}
