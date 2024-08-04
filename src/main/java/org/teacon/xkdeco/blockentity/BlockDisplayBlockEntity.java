/**
 * block entity part of Block Display Block
 * it should only be responsible for data storage, verification and sync
 */
package org.teacon.xkdeco.blockentity;

import org.jetbrains.annotations.Nullable;
import org.teacon.xkdeco.XKDeco;
import org.teacon.xkdeco.init.XKDecoEntityTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import snownee.kiwi.customization.block.KBlockUtils;
import snownee.kiwi.util.NotNullByDefault;

@NotNullByDefault
public final class BlockDisplayBlockEntity extends SingleSlotContainerBlockEntity {
	private static final String BLOCK_STATE_KEY = "BlockState";
	private static final String SELECTED_PROPERTY_KEY = "SelectedProperty";
	private static final BlockState EMPTY = Blocks.AIR.defaultBlockState();

	private BlockState blockState = EMPTY;
	@Nullable
	private Property<?> selectedProperty = null;

	public BlockDisplayBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
		super(XKDecoEntityTypes.BLOCK_DISPLAY.getOrCreate(), pWorldPosition, pBlockState);
	}

//	@Override
	public AABB getRenderBoundingBox() {
		return AABB.unitCubeFromLowerCorner(Vec3.atLowerCornerOf(this.getBlockPos().above()));
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
	public void setItem(int pSlot, ItemStack pStack) {
		if (pStack.getItem() instanceof BlockItem blockItem) {
			setStoredBlockState(blockItem.getBlock().defaultBlockState());
		} else {
			setStoredBlockState(EMPTY);
		}
		super.setItem(pSlot, pStack);
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
	public void load(CompoundTag pTag) {
		super.load(pTag);
		super.readPacketData(pTag);
		readPacketData(pTag);
		if (pTag.contains(SELECTED_PROPERTY_KEY)) {
			try {
				selectedProperty = KBlockUtils.getProperty(blockState, pTag.getString(SELECTED_PROPERTY_KEY));
			} catch (Exception e) {
				XKDeco.LOGGER.error("", e);
			}
		}
	}

	@Override
	protected void saveAdditional(CompoundTag pTag) {
		super.saveAdditional(pTag);
		super.writePacketData(pTag);
		writePacketData(pTag);
		if (selectedProperty != null) {
			pTag.putString(SELECTED_PROPERTY_KEY, selectedProperty.getName());
		}
	}

	@Override
	protected void readPacketData(CompoundTag pTag) {
		blockState = NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), pTag.getCompound(BLOCK_STATE_KEY));
	}

	@Override
	protected CompoundTag writePacketData(CompoundTag pTag) {
		pTag.put(BLOCK_STATE_KEY, NbtUtils.writeBlockState(blockState));
		return pTag;
	}
}
