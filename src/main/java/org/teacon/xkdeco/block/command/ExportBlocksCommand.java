package org.teacon.xkdeco.block.command;

import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import org.teacon.xkdeco.XKDeco;
import org.teacon.xkdeco.XKDecoClientConfig;
import org.teacon.xkdeco.block.AirDuctBlock;
import org.teacon.xkdeco.block.BlockDisplayBlock;
import org.teacon.xkdeco.block.FallenLeavesBlock;
import org.teacon.xkdeco.block.HangingFasciaBlock;
import org.teacon.xkdeco.block.HollowSteelHalfBeamBlock;
import org.teacon.xkdeco.block.HorizontalShiftBlock;
import org.teacon.xkdeco.block.ItemDisplayBlock;
import org.teacon.xkdeco.block.MimicWallBlock;
import org.teacon.xkdeco.block.RoofBlock;
import org.teacon.xkdeco.block.RoofEaveBlock;
import org.teacon.xkdeco.block.RoofEndBlock;
import org.teacon.xkdeco.block.RoofFlatBlock;
import org.teacon.xkdeco.block.RoofHorizontalShiftBlock;
import org.teacon.xkdeco.block.RoofRidgeBlock;
import org.teacon.xkdeco.block.RoofRidgeEndAsianBlock;
import org.teacon.xkdeco.block.RoofTipBlock;
import org.teacon.xkdeco.block.SnowySlabBlock;
import org.teacon.xkdeco.block.SpecialSlabBlock;
import org.teacon.xkdeco.block.WardrobeBlock;
import org.teacon.xkdeco.block.loader.BlockCodecs;
import org.teacon.xkdeco.block.loader.KBlockComponents;
import org.teacon.xkdeco.block.loader.LoaderExtraRegistries;
import org.teacon.xkdeco.block.setting.KBlockComponent;
import org.teacon.xkdeco.block.setting.KBlockSettings;

import com.google.common.base.Suppliers;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.CsvOutput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SandBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import snownee.kiwi.KiwiModule;
import snownee.kiwi.datagen.GameObjectLookup;

public class ExportBlocksCommand {
	public static final Supplier<Map<Class<? extends Block>, String>> TEMPLATE_MAPPING = Suppliers.memoize(() -> {
		Map<Class<? extends Block>, String> map = Maps.newHashMap();
		map.put(StairBlock.class, "stair");
		map.put(SlabBlock.class, "slab");
		map.put(WallBlock.class, "wall");
		map.put(FenceBlock.class, "fence");
		map.put(FenceGateBlock.class, "fence_gate");
		map.put(DoorBlock.class, "door");
		map.put(TrapDoorBlock.class, "trapdoor");
		map.put(IronBarsBlock.class, "iron_bars");
		map.put(RotatedPillarBlock.class, "rotated_pillar");
		map.put(LeavesBlock.class, "leaves");
		map.put(SandBlock.class, "colored_falling");

		map.put(FallenLeavesBlock.class, "xkdeco:fallen_leaves");
		map.put(SpecialSlabBlock.class, "xkdeco:special_slab");
		map.put(SnowySlabBlock.class, "xkdeco:snowy_slab");
		map.put(BlockDisplayBlock.class, "xkdeco:block_display");
		map.put(ItemDisplayBlock.class, "xkdeco:item_display");
		map.put(HangingFasciaBlock.class, "xkdeco:hanging_fascia");
		map.put(WardrobeBlock.class, "xkdeco:wardrobe");
		map.put(HorizontalShiftBlock.class, "xkdeco:horizontal_shift");
		map.put(AirDuctBlock.class, "xkdeco:air_duct");
		map.put(HollowSteelHalfBeamBlock.class, "xkdeco:hollow_steel_half_beam");
		map.put(RoofBlock.class, "xkdeco:roof");
		map.put(RoofEaveBlock.class, "xkdeco:roof_eave");
		map.put(RoofEndBlock.class, "xkdeco:roof_end");
		map.put(RoofFlatBlock.class, "xkdeco:roof_flat");
		map.put(RoofHorizontalShiftBlock.class, "xkdeco:roof_horizontal_shift");
		map.put(RoofRidgeBlock.class, "xkdeco:roof_ridge");
		map.put(RoofRidgeEndAsianBlock.class, "xkdeco:roof_ridge_end_asian");
		map.put(RoofTipBlock.class, "xkdeco:roof_tip");
		map.put(MimicWallBlock.class, "ignore");
		return map;
	});

	private static final Map<KBlockSettings, KBlockSettings.MoreInfo> MORE_INFO = Maps.newHashMap();

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands
				.literal("exportBlocks")
				.requires(source -> source.hasPermission(2))
				.then(Commands.argument("modId", StringArgumentType.string())
						.executes(ctx -> exportBlocks(ctx.getSource(), StringArgumentType.getString(ctx, "modId"))))
		);
	}

	@SuppressWarnings("deprecation")
	private static int exportBlocks(CommandSourceStack source, String modId) {
		LanguageManager languageManager = Minecraft.getInstance().getLanguageManager();
		Language language = null;
		String languageCode = languageManager.getSelected();
		if (!"en_us".equals(languageCode)) {
			languageManager.setSelected("en_us");
			ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
			languageManager.onResourceManagerReload(resourceManager);
			language = Language.getInstance();
			languageManager.setSelected(languageCode);
			languageManager.onResourceManagerReload(resourceManager);
		}
		try (BufferedWriter writer = Files.newBufferedWriter(Paths.get("exported_blocks_" + modId + ".csv"))) {
			LinkedHashMap<String, String> row = Maps.newLinkedHashMap();
			row.put("ID", "");
			if (language != null) {
				row.put("Name:en_us", "");
			}
			row.put("Name:" + languageCode, "");
			row.put("Template", "");
			row.put("RenderType", "");
			row.put("LightEmission", "");
			row.put("GlassType", "");
			row.put("SustainsPlant", "");
			row.put("WaterLoggable", "");
			row.put("BaseComponent", "");
			row.put("ExtraComponents", "");
			KBlockSettings.MoreInfo fallbackMoreInfo = new KBlockSettings.MoreInfo(null, null, null);
			if (XKDecoClientConfig.exportBlocksMore) {
				row.put("Shape", "");
				row.put("CollisionShape", "");
				row.put("InteractionShape", "");
			}
			row.put("NoCollision", "");
			row.put("NoOcclusion", "");
			Set<KBlockComponent.Type<?>> baseComponents = Set.of(
					KBlockComponents.DIRECTIONAL.get(),
					KBlockComponents.HORIZONTAL.get(),
					KBlockComponents.MOULDING.get(),
					KBlockComponents.HORIZONTAL_AXIS.get(),
					KBlockComponents.FRONT_AND_TOP.get()
			);
			Codec<List<KBlockComponent>> componentsCodec = Codec.list(LoaderExtraRegistries.BLOCK_COMPONENT.byNameCodec()
					.dispatch("type", KBlockComponent::type, KBlockComponent.Type::codec));
			CsvOutput csvOutput = row.keySet().stream().reduce(
					CsvOutput.builder(), CsvOutput.Builder::addColumn,
					(builder1, builder2) -> {
						throw new UnsupportedOperationException();
					}).build(writer);
			for (Block block : GameObjectLookup.all(Registries.BLOCK, modId).toList()) {
				String template = TEMPLATE_MAPPING.get().getOrDefault(block.getClass(), "block");
				if ("ignore".equals(template)) {
					continue;
				}
				if ("door".equals(template) || "trapdoor".equals(template) || "xkdeco:special_slab".equals(template)) {
					Codec<Block> codec = BlockCodecs.get(new ResourceLocation(template)).codec();
					JsonObject json = codec.encodeStart(JsonOps.INSTANCE, block).result().orElseThrow().getAsJsonObject();
					json.remove(BlockCodecs.BLOCK_PROPERTIES_KEY);
					if (json.size() > 0) {
						template += json.toString();
					}
				}
				row.put("Template", template);
				row.put("ID", BuiltInRegistries.BLOCK.getKey(block).getPath());
				row.put("Name:" + languageCode, block.getName().getString());
				if (language != null && language.has(block.getDescriptionId())) {
					String englishName = language.getOrDefault(block.getDescriptionId());
					row.put("Name:en_us", englishName);
					if (englishName.equals(block.getName().getString())) {
						row.put("Name:" + languageCode, "");
					}
				}
				KiwiModule.RenderLayer.Layer layer = null;
				RenderType renderType = ItemBlockRenderTypes.getChunkRenderType(block.defaultBlockState());
				if (renderType == RenderType.cutout()) {
					layer = KiwiModule.RenderLayer.Layer.CUTOUT;
				} else if (renderType == RenderType.cutoutMipped()) {
					layer = KiwiModule.RenderLayer.Layer.CUTOUT_MIPPED;
				} else if (renderType == RenderType.translucent()) {
					layer = KiwiModule.RenderLayer.Layer.TRANSLUCENT;
				}
				row.put("RenderType", layer == null ? "solid" : layer.name().toLowerCase(Locale.ENGLISH));
				int lightEmission = -1;
				for (BlockState blockState : block.getStateDefinition().getPossibleStates()) {
					if (lightEmission == -1) {
						lightEmission = blockState.getLightEmission();
					} else if (lightEmission != blockState.getLightEmission()) {
						lightEmission = -2;
						row.put("LightEmission", "custom");
						break;
					}
				}
				if (lightEmission >= 0) {
					row.put("LightEmission", Integer.toString(lightEmission));
				}
				KBlockSettings settings = KBlockSettings.of(block);
				if (settings == null) {
					settings = KBlockSettings.EMPTY;
				}
				if (settings.glassType == null) {
					row.put("GlassType", "");
				} else {
					row.put("GlassType", settings.glassType.name());
				}
				row.put("SustainsPlant", Boolean.toString(settings.sustainsPlant));
				row.put("WaterLoggable", Boolean.toString(settings.hasComponent(KBlockComponents.WATER_LOGGABLE.get())));
				KBlockComponent.Type<?> baseComponent = settings.components.keySet()
						.stream()
						.filter(baseComponents::contains)
						.findFirst()
						.orElse(null);
				row.put(
						"BaseComponent",
						baseComponent == null ? "" : LoaderExtraRegistries.BLOCK_COMPONENT.getKey(baseComponent).getPath());
				List<KBlockComponent> components = settings.components.values().stream().filter(component -> {
					return !KBlockComponents.WATER_LOGGABLE.is(component.type()) && !baseComponents.contains(component.type());
				}).toList();
				if (components.isEmpty()) {
					row.put("ExtraComponents", "");
				} else {
					row.put("ExtraComponents", componentsCodec.encodeStart(JsonOps.INSTANCE, components)
							.result()
							.orElseThrow()
							.toString());
				}
				if (XKDecoClientConfig.exportBlocksMore) {
					KBlockSettings.MoreInfo moreInfo = MORE_INFO.getOrDefault(settings, fallbackMoreInfo);
					row.put("Shape", Optional.ofNullable(moreInfo.shape()).map(Object::toString).orElse(""));
					row.put("CollisionShape", Optional.ofNullable(moreInfo.collisionShape()).map(Object::toString).orElse(""));
					row.put("InteractionShape", Optional.ofNullable(moreInfo.interactionShape()).map(Object::toString).orElse(""));
				}
				BlockBehaviour.Properties properties = block.properties;
				row.put("NoCollision", Boolean.toString(!properties.hasCollision));
				row.put("NoOcclusion", Boolean.toString(!properties.canOcclude));
				csvOutput.writeRow(row.values().toArray(Object[]::new));
			}
		} catch (Exception e) {
			XKDeco.LOGGER.error("Failed to export blocks", e);
			source.sendFailure(Component.literal("Failed to export blocks: " + e.getMessage()));
		}
		source.sendSuccess(() -> Component.literal("Blocks exported"), false);
		return 0;
	}

	public static void putMoreInfo(KBlockSettings settings, KBlockSettings.MoreInfo moreInfo) {
		MORE_INFO.put(settings, moreInfo);
	}
}
