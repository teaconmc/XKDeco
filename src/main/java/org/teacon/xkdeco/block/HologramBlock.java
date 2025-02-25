package org.teacon.xkdeco.block;

import org.jetbrains.annotations.Nullable;
import org.teacon.xkdeco.blockentity.HologramBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import snownee.kiwi.block.ModBlock;
import snownee.kiwi.util.NotNullByDefault;

@NotNullByDefault
public class HologramBlock extends ModBlock implements EntityBlock {
	public HologramBlock(Properties properties) {
		super(properties);
	}

	@Override
	public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
		return new HologramBlockEntity(blockPos, blockState);
	}

}
