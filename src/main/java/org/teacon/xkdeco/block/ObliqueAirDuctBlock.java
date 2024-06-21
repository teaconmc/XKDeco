package org.teacon.xkdeco.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import snownee.kiwi.customization.block.BasicBlock;

public class ObliqueAirDuctBlock extends BasicBlock {
	public ObliqueAirDuctBlock(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult use(
			BlockState pState,
			Level pLevel,
			BlockPos pPos,
			Player pPlayer,
			InteractionHand pHand,
			BlockHitResult pHit) {
		ItemStack itemStack = pPlayer.getItemInHand(pHand);
		if (itemStack.is(asItem())) {
			pLevel.setBlockAndUpdate(pPos, pState.cycle(XKDStateProperties.HALF));
			return InteractionResult.sidedSuccess(pLevel.isClientSide);
		}
		return InteractionResult.PASS;
	}
}
