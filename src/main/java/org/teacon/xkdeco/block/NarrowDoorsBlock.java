package org.teacon.xkdeco.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;

import org.teacon.xkdeco.util.CommonProxy;

import snownee.kiwi.customization.block.CheckedWaterloggedBlock;
import snownee.kiwi.customization.block.loader.BlockCodecs;
import snownee.kiwi.util.codec.CustomizationCodecs;


public class NarrowDoorsBlock extends FenceGateBlock implements CheckedWaterloggedBlock {
	private static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	public static final MapCodec<NarrowDoorsBlock> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
			BlockCodecs.propertiesCodec(),
			CustomizationCodecs.WOOD_TYPE.optionalFieldOf("wood_type", WoodType.OAK).forGetter($ -> WoodType.OAK)
	).apply(instance, NarrowDoorsBlock::new));

	public NarrowDoorsBlock(Properties pProperties, WoodType pType) {
		super(pProperties, pType);
	}

	@Override
	public InteractionResult use(
			BlockState pState,
			Level pLevel,
			BlockPos pPos,
			Player pPlayer,
			InteractionHand pHand,
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