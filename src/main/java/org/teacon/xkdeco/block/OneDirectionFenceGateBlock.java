package org.teacon.xkdeco.block;

import org.teacon.xkdeco.util.CommonProxy;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;

public class OneDirectionFenceGateBlock extends FenceGateBlock {
	// If you declare a specific type, that means your type is invariant.
	// No ? extends nor ? super for you.
	// To comply this constraint, we have to make this a MapCodec<FenceGateBlock>,
	// instead of the more proper MapCodec<OneDirectionFenceGateBlock>.
	public static final MapCodec<FenceGateBlock> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
			propertiesCodec(),
			WoodType.CODEC.optionalFieldOf("wood_type", WoodType.OAK).forGetter($ -> WoodType.OAK)
	).apply(instance, OneDirectionFenceGateBlock::new));

	public OneDirectionFenceGateBlock(Properties pProperties, WoodType pType) {
		super(pType, pProperties);
	}

	@Override
	public MapCodec<FenceGateBlock> codec() {
		return CODEC;
	}

	@Override
	protected InteractionResult useWithoutItem(
			BlockState pState,
			Level pLevel,
			BlockPos pPos,
			Player pPlayer,
			BlockHitResult pHit) {
		pLevel.setBlock(pPos, pState.cycle(OPEN), 10);
		boolean flag = pState.getValue(OPEN);
		SoundEvent sound = CommonProxy.getFenceGateSound(this, flag);
		pLevel.playSound(
				pPlayer,
				pPos,
				sound,
				SoundSource.BLOCKS,
				1.0F,
				pLevel.getRandom().nextFloat() * 0.1F + 0.9F);
		pLevel.gameEvent(pPlayer, flag ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pPos);
		return InteractionResult.sidedSuccess(pLevel.isClientSide);
	}
}
