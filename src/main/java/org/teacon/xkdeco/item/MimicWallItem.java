/*
package org.teacon.xkdeco.item;

import java.util.function.Consumer;

import org.teacon.xkdeco.block.MimicWallBlock;
import org.teacon.xkdeco.client.renderer.XKDecoWithoutLevelRenderer;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
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

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		consumer.accept(new IClientItemExtensions() {
			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				return XKDecoWithoutLevelRenderer.INSTANCE;
			}
		});
	}
}
*/
