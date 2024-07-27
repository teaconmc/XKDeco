/**
 * block entity part of Item Display Block
 * it should only be responsible for data storage, verification and sync
 */
package org.teacon.xkdeco.blockentity;

import org.jetbrains.annotations.NotNull;
import org.teacon.xkdeco.block.ItemDisplayBlock;
import org.teacon.xkdeco.init.XKDecoEntityTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import snownee.kiwi.util.NotNullByDefault;

@NotNullByDefault
public class ItemDisplayBlockEntity extends SingleSlotContainerBlockEntity {
	public static final String SPIN_KEY = "FixedSpin";
	private float fixedSpin;

	public ItemDisplayBlockEntity(BlockPos blockPos, BlockState blockState, boolean projector) {
		super(
				projector ? XKDecoEntityTypes.ITEM_PROJECTOR.getOrCreate() : XKDecoEntityTypes.ITEM_DISPLAY.getOrCreate(),
				blockPos,
				blockState);
	}

	@Override
	public AABB getRenderBoundingBox() {
		if (isProjector()) {
			return AABB.ofSize(Vec3.atBottomCenterOf(this.getBlockPos().above(9)), 16, 16, 16);
		} else {
			return AABB.unitCubeFromLowerCorner(Vec3.atLowerCornerOf(this.getBlockPos().above()));
		}
	}

	public boolean isProjector() {
		return XKDecoEntityTypes.ITEM_PROJECTOR.is(getType());
	}

	public float getSpin() {
		if (hasFixedSpin()) {
			return fixedSpin;
		}
		return level == null ? 0 : level.getGameTime();
	}

	public boolean hasFixedSpin() {
		return getBlockState().getValue(ItemDisplayBlock.POWERED);
	}

	public void setFixedSpin(float fixedSpin) {
		this.fixedSpin = fixedSpin;
	}

	@Override
	protected void readPacketData(CompoundTag pTag) {
		super.readPacketData(pTag);
		this.fixedSpin = pTag.getFloat(SPIN_KEY);
	}

	@NotNull
	@Override
	protected CompoundTag writePacketData(CompoundTag pTag) {
		pTag.putFloat(SPIN_KEY, fixedSpin);
		return super.writePacketData(pTag);
	}
}
