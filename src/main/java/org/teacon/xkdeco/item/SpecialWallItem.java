package org.teacon.xkdeco.item;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.IItemRenderProperties;
import org.teacon.xkdeco.XKDeco;
import org.teacon.xkdeco.block.SpecialWallBlock;
import org.teacon.xkdeco.client.renderer.blockentity.XKDecoWithoutLevelRenderer;

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
        return new TranslatableComponent("block." + XKDeco.ID + ".special_wall", super.getName(pStack));
    }

    @Override
    public void initializeClient(Consumer<IItemRenderProperties> consumer) {
        consumer.accept(new IItemRenderProperties() {
            @Override
            public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
                return XKDecoWithoutLevelRenderer.INSTANCE;
            }
        });
    }
}
