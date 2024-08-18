/**
 * block entity part of Item Display Block
 * it should only be responsible for data storage, verification and sync
 */
package org.teacon.xkdeco.blockentity;

import net.minecraft.core.HolderLookup;

import org.jetbrains.annotations.NotNull;
import org.teacon.xkdeco.block.ItemDisplayBlock;
import org.teacon.xkdeco.init.XKDecoEntityTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;

//@NotNullByDefault
public class ItemDisplayBlockEntity extends SingleSlotContainerBlockEntity {
	public static final String SPIN_KEY = "FixedSpin";
	private float fixedSpin;

	public ItemDisplayBlockEntity(BlockPos blockPos, BlockState blockState, boolean projector) {
		super(
				projector ? XKDecoEntityTypes.ITEM_PROJECTOR.get() : XKDecoEntityTypes.ITEM_DISPLAY.get(),
				blockPos,
				blockState);
	}

	// getRenderBoundingBox moved to ItemDisplayRenderer. See there for more info.

	public boolean isProjector() {
		return XKDecoEntityTypes.ITEM_PROJECTOR.get() == this.getType();
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
	protected void readPacketData(CompoundTag pTag, HolderLookup.Provider registries) {
		super.readPacketData(pTag, registries);
		this.fixedSpin = pTag.getFloat(SPIN_KEY);
	}

	@NotNull
	@Override
	protected CompoundTag writePacketData(CompoundTag pTag, HolderLookup.Provider registries) {
		pTag.putFloat(SPIN_KEY, fixedSpin);
		return super.writePacketData(pTag, registries);
	}
}
