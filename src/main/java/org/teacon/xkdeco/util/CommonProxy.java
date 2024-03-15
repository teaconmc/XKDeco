package org.teacon.xkdeco.util;

import org.teacon.xkdeco.XKDeco;
import org.teacon.xkdeco.block.settings.XKBlockSettings;
import org.teacon.xkdeco.data.XKDDataGen;
import org.teacon.xkdeco.data.XKDecoEnUsLangProvider;
import org.teacon.xkdeco.duck.XKBlockProperties;
import org.teacon.xkdeco.entity.CushionEntity;
import org.teacon.xkdeco.init.XKDecoObjects;
import org.teacon.xkdeco.item.XKDecoCreativeModTab;

import javax.annotation.ParametersAreNonnullByDefault;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Tags;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.RegisterEvent;
import snownee.kiwi.datagen.GameObjectLookup;

@Mod(XKDeco.ID)
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class CommonProxy {
	public CommonProxy() {
		var modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

		XKDecoObjects.ENTITIES.register(modEventBus);
		XKDecoObjects.BLOCKS.register(modEventBus);
		XKDecoObjects.ITEMS.register(modEventBus);
		XKDecoObjects.BLOCK_ENTITY.register(modEventBus);
		XKDecoCreativeModTab.TABS.register(modEventBus);

		modEventBus.addListener(EventPriority.LOWEST, XKDecoObjects::addMimicWallBlocks);
		modEventBus.addListener(EventPriority.LOWEST, XKDecoObjects::addMimicWallItems);
		modEventBus.addListener(EventPriority.LOWEST, XKDecoObjects::addMimicWallBlockEntity);

		modEventBus.addListener(XKDecoEnUsLangProvider::register);
		modEventBus.addListener((GatherDataEvent event) -> {
			FabricDataGenerator dataGenerator = FabricDataGenerator.create(XKDeco.ID, event);
			new XKDDataGen().onInitializeDataGenerator(dataGenerator);
		});
		modEventBus.addListener((RegisterEvent event) -> {
			if (!Registries.BLOCK.equals(event.getRegistryKey())) {
				return;
			}
			// set an empty settings to all blocks, to make them water-loggable correctly
			GameObjectLookup.all(Registries.BLOCK, XKDeco.ID).forEach(block -> {
				XKBlockSettings settings = XKBlockSettings.of(block);
				if (settings == null) {
					((XKBlockProperties) block.properties).xkdeco$setSettings(XKBlockSettings.EMPTY);
				}
			});
		});

		if (FMLEnvironment.dist.isClient()) {
			ClientProxy.init();
		}

		var forgeEventBus = MinecraftForge.EVENT_BUS;

		forgeEventBus.addListener(XKDecoObjects::addMimicWallTags);

		forgeEventBus.addListener(CushionEntity::onRightClickBlock);
		forgeEventBus.addListener(CushionEntity::onBreakBlock);
	}

	public static boolean isColorlessGlass(BlockState blockState) {
		return blockState.is(Tags.Blocks.GLASS_COLORLESS);
	}

	public static boolean isLadder(BlockState blockState, LevelReader world, BlockPos pos) {
		return blockState.isLadder(world, pos, null);
	}
}
