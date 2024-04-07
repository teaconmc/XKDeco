package org.teacon.xkdeco.util;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import org.teacon.xkdeco.XKDeco;
import org.teacon.xkdeco.block.SpecialSlabBlock;
import org.teacon.xkdeco.block.behavior.BlockBehaviorRegistry;
import org.teacon.xkdeco.block.loader.BlockCodecs;
import org.teacon.xkdeco.block.loader.KBlockDefinition;
import org.teacon.xkdeco.block.loader.KBlockTemplate;
import org.teacon.xkdeco.block.loader.KCreativeTab;
import org.teacon.xkdeco.block.loader.KMaterial;
import org.teacon.xkdeco.block.loader.LoaderExtraCodecs;
import org.teacon.xkdeco.block.loader.LoaderExtraRegistries;
import org.teacon.xkdeco.block.place.PlaceChoices;
import org.teacon.xkdeco.block.place.PlaceSlotProvider;
import org.teacon.xkdeco.block.place.PlacementSystem;
import org.teacon.xkdeco.block.place.SlotLink;
import org.teacon.xkdeco.block.setting.BlockRenderSettings;
import org.teacon.xkdeco.block.setting.KBlockComponent;
import org.teacon.xkdeco.block.setting.KBlockSettings;
import org.teacon.xkdeco.data.XKDDataGen;
import org.teacon.xkdeco.entity.CushionEntity;
import org.teacon.xkdeco.init.XKDecoObjects;
import org.teacon.xkdeco.util.resource.OneTimeLoader;
import org.teacon.xkdeco.util.resource.RequiredFolderRepositorySource;

import com.google.common.collect.Lists;
import com.mojang.serialization.MapCodec;

import javax.annotation.ParametersAreNonnullByDefault;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.loading.ClientModLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Tags;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.ModLoadingStage;
import net.minecraftforge.fml.ModLoadingWarning;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.forgespi.locating.IModFile;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.resource.DelegatingPackResources;
import net.minecraftforge.resource.PathPackResources;
import net.minecraftforge.resource.ResourcePackLoader;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.Kiwi;
import snownee.kiwi.datagen.GameObjectLookup;
import snownee.kiwi.loader.Platform;

@Mod(XKDeco.ID)
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class CommonProxy {
	public static final Path PACK_DIRECTORY = FMLPaths.GAMEDIR.get().resolve("kiwipacks");
	public static final List<Direction> DIRECTIONS = Direction.stream().toList();

	public CommonProxy() {
		var modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

		XKDecoObjects.BLOCKS.register(modEventBus);
		XKDecoObjects.ITEMS.register(modEventBus);

		modEventBus.addListener(EventPriority.LOWEST, XKDecoObjects::addMimicWallBlocks);
		modEventBus.addListener(EventPriority.LOWEST, XKDecoObjects::addMimicWallItems);

		modEventBus.addListener((GatherDataEvent event) -> {
			FabricDataGenerator dataGenerator = FabricDataGenerator.create(XKDeco.ID, event);
			new XKDDataGen().onInitializeDataGenerator(dataGenerator);
		});
		modEventBus.addListener(EventPriority.LOW, (RegisterEvent event) -> {
			if (!Registries.BLOCK.equals(event.getRegistryKey())) {
				return;
			}
			// set an empty settings to all blocks, to make them water-loggable correctly
			// seems unnecessary anymore
			GameObjectLookup.all(Registries.BLOCK, XKDeco.ID).forEach(block -> {
				KBlockSettings settings = KBlockSettings.of(block);
				if (settings != null) {
					BlockBehaviorRegistry behaviorRegistry = BlockBehaviorRegistry.getInstance();
					for (KBlockComponent component : settings.components.values()) {
						behaviorRegistry.setContext(block);
						component.addBehaviors(behaviorRegistry);
					}
					behaviorRegistry.setContext(null);
				}
			});

			initLoader();
		});
		modEventBus.addListener((NewRegistryEvent event) -> {
			event.create(
					new RegistryBuilder<>().setName(LoaderExtraRegistries.BLOCK_COMPONENT_KEY.location())
							.disableOverrides()
							.disableSaving()
							.hasTags(),
					$ -> {
						//noinspection unchecked
						LoaderExtraRegistries.BLOCK_COMPONENT = (Registry<KBlockComponent.Type<?>>) BuiltInRegistries.REGISTRY.get(
								LoaderExtraRegistries.BLOCK_COMPONENT_KEY.location());
						Kiwi.registerRegistry($, KBlockComponent.Type.class);
					});
			event.create(
					new RegistryBuilder<>().setName(LoaderExtraRegistries.BLOCK_TEMPLATE_KEY.location())
							.disableOverrides()
							.disableSaving()
							.hasTags(),
					$ -> {
						//noinspection unchecked
						LoaderExtraRegistries.BLOCK_TEMPLATE = (Registry<KBlockTemplate.Type<?>>) BuiltInRegistries.REGISTRY.get(
								LoaderExtraRegistries.BLOCK_TEMPLATE_KEY.location());
						Kiwi.registerRegistry($, KBlockTemplate.Type.class);
					});
		});
		modEventBus.addListener(XKDecoObjects::addMimicWallsToTab);
		modEventBus.addListener((AddPackFindersEvent event) -> {
			event.addRepositorySource(new RequiredFolderRepositorySource(
					PACK_DIRECTORY,
					event.getPackType(),
					PackSource.BUILT_IN
			));
		});

		if (Platform.isPhysicalClient()) {
			ClientProxy.init();
		}

		var forgeEventBus = MinecraftForge.EVENT_BUS;

		forgeEventBus.addListener(XKDecoObjects::addMimicWallTags);

		forgeEventBus.addListener(CushionEntity::onRightClickBlock);
		forgeEventBus.addListener(CushionEntity::onBreakBlock);
		forgeEventBus.addListener((BlockEvent.BreakEvent event) -> {
			if (PlacementSystem.isDebugEnabled(event.getPlayer())) {
				PlacementSystem.removeDebugBlocks(event.getPlayer().level(), event.getPos());
			}
		});

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

		BlockCodecs.register(XKDeco.id("special_slab"), SpecialSlabBlock.CODEC);
	}

	public static void initLoader() {
		ResourceManager resourceManager = collectKiwiPacks();
		BlockFundamentals fundamentals = BlockFundamentals.reload(resourceManager);
		fundamentals.blocks().forEach((id, definition) -> {
//			if (definition.template() == KBlockDefinition.DEFAULT_TEMPLATE.getValue()) {
//				return;
//			}
			Block block = definition.createBlock(id);
			if (block != null) {
				fundamentals.slotProviders().attachSlots(block, definition);
				fundamentals.placeChoices().attachChoices(block, definition);
				ForgeRegistries.BLOCKS.register(id, block);
				ForgeRegistries.ITEMS.register(id, new BlockItem(block, new Item.Properties()));
			}
		});
		if (Platform.isPhysicalClient()) {
			BlockRenderSettings.init(fundamentals.blocks(), ClientModLoader.isLoading());
		}
		var tabs = OneTimeLoader.load(resourceManager, "kiwi/creative_tab", KCreativeTab.CODEC);
		tabs.entrySet().stream().sorted(Comparator.comparingInt($ -> $.getValue()
				.order())).forEach(entry -> {
			ResourceLocation key = entry.getKey();
			KCreativeTab value = entry.getValue();
			CreativeModeTab tab = AbstractModule.itemCategory(key.getNamespace(), key.getPath(), () -> BuiltInRegistries.ITEM.getOptional(
							value.icon()).orElse(Items.BARRIER).getDefaultInstance())
					.displayItems((params, output) -> {
						output.acceptAll(value.contents()
								.stream()
								.map(BuiltInRegistries.ITEM::get)
								.filter(Objects::nonNull)
								.map(Item::getDefaultInstance)
								.toList());
					})
					.build();
			Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, key, tab);
		});
	}

	public record BlockFundamentals(
			Map<ResourceLocation, KMaterial> materials,
			Map<ResourceLocation, KBlockTemplate> templates,
			PlaceSlotProvider.ParsedResult slotProviders,
			Map<ResourceLocation, SlotLink> slotLinks,
			PlaceChoices.ParsedResult placeChoices,
			Map<ResourceLocation, KBlockDefinition> blocks,
			MapCodec<Optional<KMaterial>> materialCodec) {
		public static BlockFundamentals reload(ResourceManager resourceManager) {
			var materials = OneTimeLoader.load(resourceManager, "kiwi/material", KMaterial.DIRECT_CODEC);
			MapCodec<Optional<KMaterial>> materialCodec = LoaderExtraCodecs.simpleByNameCodec(materials).optionalFieldOf("material");
			var templates = OneTimeLoader.load(resourceManager, "kiwi/template/block", KBlockTemplate.codec(materialCodec));
			templates.forEach((key, value) -> value.resolve(key));
			var slotProviders = PlaceSlotProvider.reload(resourceManager, templates);
			boolean isDataGen = Platform.isDataGen();
			Map<ResourceLocation, SlotLink> slotLinks = isDataGen ? Map.of() : OneTimeLoader.load(
					resourceManager,
					"kiwi/place_slot/link",
					SlotLink.CODEC);
			SlotLink.clear();
			slotLinks.values().forEach(SlotLink::register);
			var placeChoices = PlaceChoices.ParsedResult.of(isDataGen ? Map.of() : OneTimeLoader.load(
					resourceManager,
					"kiwi/place_slot/choices",
					PlaceChoices.CODEC), templates);
			var blocks = OneTimeLoader.load(
					resourceManager,
					"kiwi/block",
					KBlockDefinition.codec(templates, materialCodec));
			return new BlockFundamentals(materials, templates, slotProviders, slotLinks, placeChoices, blocks, materialCodec);
		}
	}

	public static ResourceManager collectKiwiPacks() {
		//noinspection ResultOfMethodCallIgnored
		PACK_DIRECTORY.toFile().mkdirs();
		var folderRepositorySource = new RequiredFolderRepositorySource(PACK_DIRECTORY, PackType.CLIENT_RESOURCES, PackSource.BUILT_IN);
		PackRepository packRepository = new PackRepository(folderRepositorySource);
		ResourcePackLoader.loadResourcePacks(packRepository, CommonProxy::buildPackFinder);
		packRepository.reload();
		List<String> selected = Lists.newArrayList(packRepository.getAvailableIds());
		selected.remove("mod_resources");
		selected.add(0, "mod_resources");
		packRepository.setSelected(selected);
		return new MultiPackResourceManager(PackType.CLIENT_RESOURCES, packRepository.openAllSelected());
	}

	private static RepositorySource buildPackFinder(Map<IModFile, ? extends PathPackResources> modResourcePacks) {
		return packAcceptor -> clientPackFinder(modResourcePacks, packAcceptor);
	}

	private static void clientPackFinder(Map<IModFile, ? extends PathPackResources> modResourcePacks, Consumer<Pack> packAcceptor) {
		var hiddenPacks = new ArrayList<PathPackResources>();
		for (Map.Entry<IModFile, ? extends PathPackResources> e : modResourcePacks.entrySet()) {
			IModInfo mod = e.getKey().getModInfos().get(0);
			final String name = "mod:" + mod.getModId();
			final Pack modPack = Pack.readMetaAndCreate(
					name,
					Component.literal(e.getValue().packId()),
					false,
					id -> e.getValue(),
					PackType.CLIENT_RESOURCES,
					Pack.Position.BOTTOM,
					PackSource.DEFAULT);
			if (modPack == null) {
				// Vanilla only logs an error, instead of propagating, so handle null and warn that something went wrong
				ModLoader.get().addWarning(new ModLoadingWarning(mod, ModLoadingStage.ERROR, "fml.modloading.brokenresources", e.getKey()));
				continue;
			}
			XKDeco.LOGGER.debug("Generating PackInfo named {} for mod file {}", name, e.getKey().getFilePath());
			if (mod.getOwningFile().showAsResourcePack()) {
				packAcceptor.accept(modPack);
			} else {
				hiddenPacks.add(e.getValue());
			}
		}

		// Create a resource pack merging all mod resources that should be hidden
		final Pack modResourcesPack = Pack.readMetaAndCreate("mod_resources", Component.literal("Mod Resources"), true,
				id -> new DelegatingPackResources(id, false, new PackMetadataSection(
						Component.translatable("fml.resources.modresources", hiddenPacks.size()),
						SharedConstants.getCurrentVersion().getPackVersion(PackType.CLIENT_RESOURCES)), hiddenPacks),
				PackType.CLIENT_RESOURCES, Pack.Position.BOTTOM, PackSource.DEFAULT);
		packAcceptor.accept(modResourcesPack);
	}

	public static boolean isColorlessGlass(BlockState blockState) {
		return blockState.is(Tags.Blocks.GLASS_COLORLESS);
	}

	public static boolean isLadder(BlockState blockState, LevelReader world, BlockPos pos) {
		return blockState.isLadder(world, pos, null);
	}
}
