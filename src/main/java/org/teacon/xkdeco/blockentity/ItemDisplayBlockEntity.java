/**
 * block entity part of Item Display Block
 * it should only be responsible for data storage, verification and sync
 */
package org.teacon.xkdeco.blockentity;

import org.teacon.xkdeco.block.ItemDisplayBlock;
import org.teacon.xkdeco.init.XKDecoEntityTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

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
	protected void readPacketData(ValueInput input) {
		super.readPacketData(input);
		this.fixedSpin = input.getFloatOr(SPIN_KEY, 0.0F);
	}

	@Override
	protected void writePacketData(ValueOutput output) {
		super.writePacketData(output);
		output.putFloat(SPIN_KEY, fixedSpin);
	}
}
