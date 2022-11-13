package org.teacon.xkdeco;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.teacon.xkdeco.client.XKDecoClient;
import org.teacon.xkdeco.data.XKDecoBlockStateProvider;
import org.teacon.xkdeco.data.XKDecoEnUsLangProvider;
import org.teacon.xkdeco.data.XKDecoRecipeProvider;
import org.teacon.xkdeco.entity.CushionEntity;
import org.teacon.xkdeco.init.XKDecoObjects;

import javax.annotation.ParametersAreNonnullByDefault;

@Mod(XKDeco.ID)
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public final class XKDeco {
    public static final String ID = "xkdeco";

    public XKDeco() {
        var modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        XKDecoObjects.ENTITIES.register(modEventBus);
        XKDecoObjects.BLOCKS.register(modEventBus);
        XKDecoObjects.ITEMS.register(modEventBus);
        XKDecoObjects.BLOCK_ENTITY.register(modEventBus);

        modEventBus.addGenericListener(Block.class, EventPriority.LOWEST, XKDecoObjects::addSpecialWallBlocks);
        modEventBus.addGenericListener(Item.class, EventPriority.LOWEST, XKDecoObjects::addSpecialWallItems);
        modEventBus.addGenericListener(BlockEntityType.class, EventPriority.LOWEST, XKDecoObjects::addSpecialWallBlockEntity);

        modEventBus.addListener(XKDecoBlockStateProvider::register);
        modEventBus.addListener(XKDecoRecipeProvider::register);
        modEventBus.addListener(XKDecoEnUsLangProvider::register);

        if (FMLEnvironment.dist.isClient()) {
            modEventBus.addListener(XKDecoClient::setItemColors);
            modEventBus.addListener(XKDecoClient::setBlockColors);
            modEventBus.addListener(XKDecoClient::setCutoutBlocks);
            modEventBus.addListener(XKDecoClient::setItemRenderers);
            modEventBus.addListener(XKDecoClient::setEntityRenderers);
            modEventBus.addListener(XKDecoClient::setAdditionalPackFinder);
        }

        var forgeEventBus = MinecraftForge.EVENT_BUS;

        forgeEventBus.addListener(XKDecoObjects::addSpecialWallTags);

        forgeEventBus.addListener(CushionEntity::onRightClickBlock);
        forgeEventBus.addListener(CushionEntity::onBreakBlock);
    }
}
