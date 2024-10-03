package org.teacon.xkdeco.item;

import org.teacon.xkdeco.block.MimicWallBlock;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import snownee.kiwi.util.NotNullByDefault;

@Deprecated
@NotNullByDefault
public final class MimicWallItem extends BlockItem {
	public MimicWallItem(MimicWallBlock pBlock, Properties pProperties) {
		super(pBlock, pProperties);
	}

	@Override
	public Component getName(ItemStack pStack) {
		return getBlock().getName();
	}
}
