/**
 * block entity part of Block Display Block
 * it should only be responsible for data storage, verification and sync
 */
package org.teacon.xkdeco.blockentity;

import org.jspecify.annotations.Nullable;
import org.teacon.xkdeco.XKDeco;
import org.teacon.xkdeco.init.XKDecoEntityTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public final class BlockDisplayBlockEntity extends SingleSlotContainerBlockEntity {
	private static final String BLOCK_STATE_KEY = "BlockState";
	private static final String SELECTED_PROPERTY_KEY = "SelectedProperty";
	private static final BlockState EMPTY = Blocks.AIR.defaultBlockState();

	private BlockState blockState = EMPTY;
	@Nullable
	private Property<?> selectedProperty = null;

	public BlockDisplayBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
		super(XKDecoEntityTypes.BLOCK_DISPLAY.get(), pWorldPosition, pBlockState);
	}

	@Override
	public int getMaxStackSize() {
		return 1;
	}

	@Override
	public boolean canPlaceItem(int pIndex, ItemStack pStack) {
		return pStack.getItem() instanceof BlockItem;
	}

	@Override
	public void setTheItem(ItemStack itemStack) {
		super.setTheItem(itemStack);
		if (itemStack.getItem() instanceof BlockItem blockItem) {
			setStoredBlockState(blockItem.getBlock().defaultBlockState());
		} else {
			setStoredBlockState(EMPTY);
		}
	}

	public BlockState getStoredBlockState() {
		return blockState;
	}

	public void setStoredBlockState(BlockState blockState) {
		this.blockState = blockState;
		getSelectedProperty();
		refresh();
	}

	@Nullable
	public Property<?> getSelectedProperty() {
		if (blockState != EMPTY && selectedProperty == null) {
			var properties = blockState.getProperties();
			selectedProperty = properties.isEmpty() ? null : properties.iterator().next();
		}
		return selectedProperty;
	}

	public void setSelectedProperty(@Nullable Property<?> selectedProperty) {
		this.selectedProperty = selectedProperty;
		this.setChanged();
	}

	@Override
	public void loadAdditional(ValueInput input) {
		super.loadAdditional(input); // base reads item + blockState via readPacketData
		if (input.getString(SELECTED_PROPERTY_KEY).isPresent()) {
			try {
				selectedProperty = blockState.getBlock().getStateDefinition().getProperty(SELECTED_PROPERTY_KEY);
			} catch (Exception e) {
				XKDeco.LOGGER.error("", e);
			}
		}
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output); // base writes item + blockState via writePacketData
		if (selectedProperty != null) {
			output.putString(SELECTED_PROPERTY_KEY, selectedProperty.getName());
		}
	}

	@Override
	protected void readPacketData(ValueInput input) {
		super.readPacketData(input);
		input.read(BLOCK_STATE_KEY, BlockState.CODEC).ifPresent(bs -> blockState = bs);
	}

	@Override
	protected void writePacketData(ValueOutput output) {
		super.writePacketData(output);
		output.store(BLOCK_STATE_KEY, BlockState.CODEC, blockState);
	}
}
