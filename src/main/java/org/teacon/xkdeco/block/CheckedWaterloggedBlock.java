package org.teacon.xkdeco.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public interface CheckedWaterloggedBlock extends SimpleWaterloggedBlock {
	@Override
	default boolean canPlaceLiquid(Player p, BlockGetter level, BlockPos pos, BlockState state, Fluid fluid) {
		return state.hasProperty(BlockStateProperties.WATERLOGGED) && SimpleWaterloggedBlock.super.canPlaceLiquid(
				p, level, pos, state, fluid);
	}

	@Override
	default boolean placeLiquid(LevelAccessor pLevel, BlockPos pPos, BlockState pState, FluidState pFluidState) {
		return pState.hasProperty(BlockStateProperties.WATERLOGGED) && SimpleWaterloggedBlock.super.placeLiquid(
				pLevel,
				pPos,
				pState,
				pFluidState);
	}

	@Override
	default @NotNull ItemStack pickupBlock(@Nullable Player player, LevelAccessor pLevel, BlockPos pPos, BlockState pState) {
		if (!pState.hasProperty(BlockStateProperties.WATERLOGGED)) {
			return ItemStack.EMPTY;
		}
		return SimpleWaterloggedBlock.super.pickupBlock(player, pLevel, pPos, pState);
	}
}
