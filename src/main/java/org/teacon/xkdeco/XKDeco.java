package org.teacon.xkdeco;

import org.slf4j.Logger;
import org.teacon.xkdeco.client.XKDecoClient;
import org.teacon.xkdeco.data.XKDDataGen;
import org.teacon.xkdeco.data.XKDecoEnUsLangProvider;
import org.teacon.xkdeco.entity.CushionEntity;
import org.teacon.xkdeco.init.XKDecoObjects;
import org.teacon.xkdeco.item.XKDecoCreativeModTab;

import com.mojang.logging.LogUtils;

import javax.annotation.ParametersAreNonnullByDefault;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(XKDeco.ID)
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public final class XKDeco {
	public static final String ID = "xkdeco";
	public static final Logger LOGGER = LogUtils.getLogger();

	public XKDeco() {
		var modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

		XKDecoObjects.ENTITIES.register(modEventBus);
		XKDecoObjects.BLOCKS.register(modEventBus);
		XKDecoObjects.ITEMS.register(modEventBus);
		XKDecoObjects.BLOCK_ENTITY.register(modEventBus);
		XKDecoCreativeModTab.TABS.register(modEventBus);

		modEventBus.addListener(EventPriority.LOWEST, XKDecoObjects::addSpecialWallBlocks);
		modEventBus.addListener(EventPriority.LOWEST, XKDecoObjects::addSpecialWallItems);
		modEventBus.addListener(EventPriority.LOWEST, XKDecoObjects::addSpecialWallBlockEntity);

		modEventBus.addListener(XKDecoEnUsLangProvider::register);
		modEventBus.addListener((GatherDataEvent event) -> {
			FabricDataGenerator dataGenerator = FabricDataGenerator.create(ID, event);
			new XKDDataGen().onInitializeDataGenerator(dataGenerator);
		});

		if (FMLEnvironment.dist.isClient()) {
			modEventBus.addListener(XKDecoClient::setItemColors);
			modEventBus.addListener(XKDecoClient::setBlockColors);
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
