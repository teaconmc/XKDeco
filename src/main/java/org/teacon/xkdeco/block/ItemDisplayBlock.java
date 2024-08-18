package org.teacon.xkdeco.block;

import net.minecraft.world.ItemInteractionResult;

import org.jetbrains.annotations.Nullable;
import org.teacon.xkdeco.blockentity.ItemDisplayBlockEntity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.SegmentedAnglePrecision;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import snownee.kiwi.util.NotNullByDefault;

@NotNullByDefault
public class ItemDisplayBlock extends DisplayBlock {
	private static final SegmentedAnglePrecision SEGMENTED_ANGLE8 = new SegmentedAnglePrecision(3);
	private static final float angleStep = Mth.PI / 4 * 20;
	public static final MapCodec<ItemDisplayBlock> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			propertiesCodec(),
			Codec.BOOL.optionalFieldOf("projector", false).forGetter(block -> block.projector)
	).apply(instance, ItemDisplayBlock::new));
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

	public final boolean projector;

	public ItemDisplayBlock(Properties properties, boolean projector) {
		super(properties);
		this.projector = projector;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		return new ItemDisplayBlockEntity(pPos, pState, projector);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		return this.defaultBlockState().setValue(POWERED, pContext.getLevel().hasNeighborSignal(pContext.getClickedPos()));
	}

	@Override
	public void neighborChanged(
			BlockState pState,
			Level pLevel,
			BlockPos pPos,
			Block pBlock,
			BlockPos pFromPos,
			boolean pIsMoving) {
		if (!pLevel.isClientSide && pState.getValue(POWERED) != pLevel.hasNeighborSignal(pPos)) {
			pLevel.setBlock(pPos, pState.cycle(POWERED), 2);
		}
	}

	@Override
	protected ItemInteractionResult useSide(
			ItemStack held,
			BlockState pState,
			Level pLevel,
			BlockPos pPos,
			Player pPlayer,
			InteractionHand pHand,
			BlockHitResult pHit) {
		if (!pState.getValue(POWERED)) {
			return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
		}
		if (!pLevel.isClientSide && pLevel.getBlockEntity(pPos) instanceof ItemDisplayBlockEntity blockEntity) {
			blockEntity.setFixedSpin(blockEntity.getSpin() + angleStep);
			blockEntity.refresh();
		}
		return ItemInteractionResult.sidedSuccess(pLevel.isClientSide);
	}

	@Override
	protected ItemInteractionResult useTop(
			ItemStack held,
			BlockState pState,
			Level pLevel,
			BlockPos pPos,
			Player pPlayer,
			InteractionHand pHand,
			BlockHitResult pHit) {
		setSpin(pLevel, pPos, pPlayer);
		return super.useTop(held, pState, pLevel, pPos, pPlayer, pHand, pHit);
	}

	private void setSpin(Level level, BlockPos pos, @Nullable LivingEntity placer) {
		if (!level.isClientSide && placer != null && level.getBlockEntity(pos) instanceof ItemDisplayBlockEntity be) {
			be.setFixedSpin(SEGMENTED_ANGLE8.toDegrees(SEGMENTED_ANGLE8.fromDegrees(-placer.getYRot() - 180)) / 45F * angleStep);
		}
	}

	@Override
	public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
		super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
		setSpin(pLevel, pPos, pPlacer);
	}
}
