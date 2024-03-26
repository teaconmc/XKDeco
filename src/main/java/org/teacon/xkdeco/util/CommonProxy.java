package org.teacon.xkdeco.util;

import java.util.Comparator;
import java.util.Objects;

import org.teacon.xkdeco.XKDeco;
import org.teacon.xkdeco.block.behavior.BlockBehaviorRegistry;
import org.teacon.xkdeco.block.loader.KCreativeTab;
import org.teacon.xkdeco.block.loader.KMaterial;
import org.teacon.xkdeco.block.loader.LoaderExtraRegistries;
import org.teacon.xkdeco.block.setting.KBlockComponent;
import org.teacon.xkdeco.block.setting.KBlockSettings;
import org.teacon.xkdeco.data.XKDDataGen;
import org.teacon.xkdeco.duck.XKBlockProperties;
import org.teacon.xkdeco.entity.CushionEntity;
import org.teacon.xkdeco.init.XKDecoObjects;

import javax.annotation.ParametersAreNonnullByDefault;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.CreativeModeTabRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Tags;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryBuilder;
import snownee.kiwi.Kiwi;
import snownee.kiwi.KiwiTabBuilder;
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

		modEventBus.addListener(EventPriority.LOWEST, XKDecoObjects::addMimicWallBlocks);
		modEventBus.addListener(EventPriority.LOWEST, XKDecoObjects::addMimicWallItems);
		modEventBus.addListener(EventPriority.LOWEST, XKDecoObjects::addMimicWallBlockEntity);

		modEventBus.addListener((GatherDataEvent event) -> {
			FabricDataGenerator dataGenerator = FabricDataGenerator.create(XKDeco.ID, event);
			new XKDDataGen().onInitializeDataGenerator(dataGenerator);
		});
		modEventBus.addListener((RegisterEvent event) -> {
			if (!Registries.BLOCK.equals(event.getRegistryKey())) {
				return;
			}
			// set an empty settings to all blocks, to make them water-loggable correctly
			// seems unnecessary anymore
			GameObjectLookup.all(Registries.BLOCK, XKDeco.ID).forEach(block -> {
				KBlockSettings settings = KBlockSettings.of(block);
				if (settings == null) {
					((XKBlockProperties) block.properties).xkdeco$setSettings(KBlockSettings.EMPTY);
				} else {
					BlockBehaviorRegistry behaviorRegistry = BlockBehaviorRegistry.getInstance();
					for (KBlockComponent component : settings.components.values()) {
						behaviorRegistry.setContext(block);
						component.addBehaviors(behaviorRegistry);
					}
					behaviorRegistry.setContext(null);
				}
			});
		});
		modEventBus.addListener((NewRegistryEvent event) -> {
			ResourceLocation registryKey = new ResourceLocation(Kiwi.ID, "block_component");
			event.create(
					new RegistryBuilder<>().setName(registryKey).disableOverrides().disableSaving().hasTags(),
					$ -> {
						//noinspection unchecked
						LoaderExtraRegistries.BLOCK_COMPONENT = (Registry<KBlockComponent.Type<?>>) BuiltInRegistries.REGISTRY.get(
								registryKey);
						Kiwi.registerRegistry($, KBlockComponent.Type.class);
					});
		});

		if (FMLEnvironment.dist.isClient()) {
			ClientProxy.init();
		}

		var forgeEventBus = MinecraftForge.EVENT_BUS;

		forgeEventBus.addListener(XKDecoObjects::addMimicWallTags);

		forgeEventBus.addListener(CushionEntity::onRightClickBlock);
		forgeEventBus.addListener(CushionEntity::onBreakBlock);

		forgeEventBus.addListener((PlayerInteractEvent.RightClickBlock event) -> {
			InteractionResult result = BlockBehaviorRegistry.getInstance().onUseBlock(
					event.getEntity(),
					event.getLevel(),
					event.getHand(),
					event.getHitVec());
			if (result.consumesAction()) {
				event.setCanceled(true);
				event.setCancellationResult(result);
			}
		});
	}

	public static void initLoader(ResourceManager resourceManager) {
		var materials = JsonLoader.load(resourceManager, "kiwi/material", KMaterial.CODEC);
		var tabs = JsonLoader.load(resourceManager, "kiwi/creative_tab", KCreativeTab.CODEC);
		tabs.entrySet().stream().sorted(Comparator.comparingInt($ -> $.getValue()
				.order())).forEach(entry -> {
			KCreativeTab value = entry.getValue();
			CreativeModeTab tab = new KiwiTabBuilder(entry.getKey())
					.icon(() -> BuiltInRegistries.ITEM.getOptional(value.icon()).orElse(Items.BARRIER).getDefaultInstance())
					.displayItems((params, output) -> {
						output.acceptAll(value.contents()
								.stream()
								.map(BuiltInRegistries.ITEM::get)
								.filter(Objects::nonNull)
								.map(Item::getDefaultInstance)
								.toList());
					})
					.build();
			Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, entry.getKey(), tab);
		});
		CreativeModeTabRegistry.sortTabs();
	}

	public static boolean isColorlessGlass(BlockState blockState) {
		return blockState.is(Tags.Blocks.GLASS_COLORLESS);
	}

	public static boolean isLadder(BlockState blockState, LevelReader world, BlockPos pos) {
		return blockState.isLadder(world, pos, null);
	}
}
