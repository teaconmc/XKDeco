package org.teacon.xkdeco.blockentity;

import org.teacon.xkdeco.init.XKDecoEntityTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class HologramBlockEntity extends BlockEntity {
	public HologramBlockEntity(BlockPos pos, BlockState blockState) {
		super(XKDecoEntityTypes.HOLOGRAM.get(), pos, blockState);
	}
}
