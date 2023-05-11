package org.teacon.xkdeco.item;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.teacon.xkdeco.XKDeco;
import org.teacon.xkdeco.block.SpecialWallBlock;
import org.teacon.xkdeco.client.renderer.XKDecoWithoutLevelRenderer;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Consumer;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public final class SpecialWallItem extends BlockItem {
    public SpecialWallItem(SpecialWallBlock pBlock, Properties pProperties) {
        super(pBlock, pProperties);
    }

    @Override
    public Component getName(ItemStack pStack) {
        return Component.translatable("block." + XKDeco.ID + ".special_wall", super.getName(pStack));
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
