package org.teacon.xkdeco.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import snownee.kiwi.customization.block.CheckedWaterloggedBlock;


public class TallTableBlock extends Block implements CheckedWaterloggedBlock {
	private static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	public TallTableBlock(Properties properties) {
		super(properties);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		BlockPos blockpos = context.getClickedPos();
		Level level = context.getLevel();
		if (blockpos.getY() < level.getMaxBuildHeight() - 1 && level.getBlockState(blockpos.above()).canBeReplaced(context)) {
			return this.defaultBlockState();
		} else {
			return null;
		}
	}

	public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if (!level.isClientSide()) {
			BlockPos above = pos.above();
			if (level.isWaterAt(pos)) {
				level.setBlock(pos, Blocks.WATER.defaultBlockState(), 3);
				level.setBlock(above, state.setValue(WATERLOGGED, true), 3);
			} else {
				level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
				level.setBlock(above, state, 3);
			}
		}
	}

}