package snownee.kiwi.customization;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;
import org.teacon.xkdeco.XKDeco;
import org.teacon.xkdeco.data.XKDDataGen;

import com.google.common.collect.Lists;

import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.SharedConstants;
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
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
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
import snownee.kiwi.customization.block.BlockFundamentals;
import snownee.kiwi.customization.block.BlockRenderSettings;
import snownee.kiwi.customization.block.GlassType;
import snownee.kiwi.customization.block.KBlockSettings;
import snownee.kiwi.customization.block.behavior.BlockBehaviorRegistry;
import snownee.kiwi.customization.block.behavior.SitManager;
import snownee.kiwi.customization.block.component.KBlockComponent;
import snownee.kiwi.customization.block.family.BlockFamilies;
import snownee.kiwi.customization.block.loader.KBlockTemplate;
import snownee.kiwi.customization.block.loader.KCreativeTab;
import snownee.kiwi.customization.block.loader.LoaderExtraRegistries;
import snownee.kiwi.customization.builder.BuilderRules;
import snownee.kiwi.customization.placement.PlacementSystem;
import snownee.kiwi.customization.util.resource.OneTimeLoader;
import snownee.kiwi.customization.util.resource.RequiredFolderRepositorySource;
import snownee.kiwi.datagen.GameObjectLookup;
import snownee.kiwi.loader.Platform;

public final class CustomizationHooks {
	public static final Path PACK_DIRECTORY = FMLPaths.GAMEDIR.get().resolve("kiwipacks");
	public static final List<Direction> DIRECTIONS = Direction.stream().toList();

	private CustomizationHooks() {
	}

	// a custom implementation of the Block.shouldRenderFace
	private static final int CACHE_SIZE = 512;
	private static final ThreadLocal<Object2ByteLinkedOpenHashMap<Block.BlockStatePairKey>> OCCLUSION_CACHE = ThreadLocal.withInitial(() -> {
		Object2ByteLinkedOpenHashMap<Block.BlockStatePairKey> object2bytelinkedopenhashmap = new Object2ByteLinkedOpenHashMap<>(
				CACHE_SIZE,
				0.25F) {
			@Override
			protected void rehash(int needed) {
			}
		};
		object2bytelinkedopenhashmap.defaultReturnValue((byte) 127);
		return object2bytelinkedopenhashmap;
	});

	public static boolean skipGlassRendering(BlockState pState, BlockState pAdjacentBlockState, Direction pDirection) {
		if (KBlockSettings.of(pState.getBlock()) == null && KBlockSettings.of(pAdjacentBlockState.getBlock()) == null) {
			return false;
		}
		GlassType glassType = getGlassType(pState);
		if (glassType == null || !glassType.skipRendering()) {
			return false;
		}
		if (!pState.is(pAdjacentBlockState.getBlock()) && glassType != getGlassType(pAdjacentBlockState)) {
			return false;
		}
		Block.BlockStatePairKey key = new Block.BlockStatePairKey(pState, pAdjacentBlockState, pDirection);
		Object2ByteLinkedOpenHashMap<Block.BlockStatePairKey> map = OCCLUSION_CACHE.get();
		byte b0 = map.getAndMoveToFirst(key);
		if (b0 != 127) {
			return b0 == 0;
		}
		VoxelShape shape1 = KBlockSettings.getGlassFaceShape(pState, pDirection);
		if (shape1.isEmpty()) {
			return true;
		}
		VoxelShape shape2 = KBlockSettings.getGlassFaceShape(pAdjacentBlockState, pDirection.getOpposite());
		boolean flag = Shapes.joinIsNotEmpty(shape1, shape2, BooleanOp.ONLY_FIRST);
		if (map.size() == CACHE_SIZE) {
			map.removeLastByte();
		}
		map.putAndMoveToFirst(key, (byte) (flag ? 1 : 0));
		return !flag;
	}

	@Nullable
	public static GlassType getGlassType(BlockState blockState) {
		KBlockSettings settings = KBlockSettings.of(blockState.getBlock());
		if (settings != null && settings.glassType != null) {
			return settings.glassType;
		}
		if (isColorlessGlass(blockState)) {
			return GlassType.CLEAR;
		}
		return null;
	}

	public static void init() {
		var modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		var forgeEventBus = MinecraftForge.EVENT_BUS;
		modEventBus.addListener((GatherDataEvent event) -> {
			FabricDataGenerator dataGenerator = FabricDataGenerator.create(XKDeco.ID, event);
			new XKDDataGen().onInitializeDataGenerator(dataGenerator);
		});
		modEventBus.addListener(EventPriority.LOW, (RegisterEvent event) -> {
			if (!Registries.BLOCK.equals(event.getRegistryKey())) {
				return;
			}
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

			CustomizationHooks.initLoader();
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
		modEventBus.addListener((AddPackFindersEvent event) -> {
			event.addRepositorySource(new RequiredFolderRepositorySource(
					CustomizationHooks.PACK_DIRECTORY,
					event.getPackType(),
					PackSource.BUILT_IN
			));
		});
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
		forgeEventBus.addListener((PlayerInteractEvent.RightClickBlock event) -> {
			if (event.getHand() == InteractionHand.MAIN_HAND && SitManager.sit(event.getEntity(), event.getHitVec())) {
				event.setCancellationResult(InteractionResult.sidedSuccess(event.getLevel().isClientSide));
				event.setCanceled(true);
			}
		});
		if (Platform.isPhysicalClient()) {
			CustomizationClient.init();
		}
	}

	public static void initLoader() {
		ResourceManager resourceManager = collectKiwiPacks();
		BlockFundamentals fundamentals = BlockFundamentals.reload(resourceManager, true);
		fundamentals.blocks().forEach((id, definition) -> {
			Block block = definition.createBlock(id, fundamentals.shapes());
			if (block != null) {
				ForgeRegistries.BLOCKS.register(id, block);
				ForgeRegistries.ITEMS.register(id, new BlockItem(block, new Item.Properties()));
				fundamentals.slotProviders().attachSlotsA(block, definition);
				fundamentals.placeChoices().attachChoicesA(block, definition);
			}
		});
		fundamentals.slotProviders().attachSlotsB();
		fundamentals.placeChoices().attachChoicesB();
		fundamentals.slotLinks().finish();
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
		BlockFamilies.reload(resourceManager);
		BuilderRules.reload(resourceManager);
	}

	public static ResourceManager collectKiwiPacks() {
		//noinspection ResultOfMethodCallIgnored
		PACK_DIRECTORY.toFile().mkdirs();
		var folderRepositorySource = new RequiredFolderRepositorySource(PACK_DIRECTORY, PackType.CLIENT_RESOURCES, PackSource.BUILT_IN);
		PackRepository packRepository = new PackRepository(folderRepositorySource);
		ResourcePackLoader.loadResourcePacks(packRepository, CustomizationHooks::buildPackFinder);
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

}
