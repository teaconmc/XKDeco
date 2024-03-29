package org.teacon.xkdeco.blockentity;

import org.teacon.xkdeco.init.XKDecoEntityTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class WardrobeBlockEntity extends BlockEntity {
	public WardrobeBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
		super(XKDecoEntityTypes.WARDROBE.getOrCreate(), pWorldPosition, pBlockState);
	}
}
