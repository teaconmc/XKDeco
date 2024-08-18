package org.teacon.xkdeco.item;

import java.util.function.Consumer;

import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

import org.teacon.xkdeco.block.MimicWallBlock;
import org.teacon.xkdeco.client.renderer.XKDecoWithoutLevelRenderer;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

@Deprecated
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
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
