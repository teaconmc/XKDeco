package org.teacon.xkdeco.util;

import java.io.BufferedReader;
import java.util.Map;
import java.util.Optional;

import org.teacon.xkdeco.XKDeco;
import org.teacon.xkdeco.block.MimicWallBlock;
import org.teacon.xkdeco.client.model.AirDuctModel;
import org.teacon.xkdeco.client.model.MimicWallModel;
import org.teacon.xkdeco.client.renderer.BlockDisplayRenderer;
import org.teacon.xkdeco.client.renderer.HologramRenderer;
import org.teacon.xkdeco.client.renderer.ItemDisplayRenderer;
import org.teacon.xkdeco.init.MimicWallsLoader;
import org.teacon.xkdeco.init.XKDecoEntityTypes;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;

import net.fabricmc.fabric.api.client.model.loading.v1.BlockStateResolver;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import snownee.kiwi.util.NotNullByDefault;

@NotNullByDefault
public final class ClientProxy {

	public static void setEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerBlockEntityRenderer(XKDecoEntityTypes.ITEM_DISPLAY.getOrCreate(), ItemDisplayRenderer::new);
		event.registerBlockEntityRenderer(XKDecoEntityTypes.ITEM_PROJECTOR.getOrCreate(), ItemDisplayRenderer::new);
		event.registerBlockEntityRenderer(XKDecoEntityTypes.BLOCK_DISPLAY.getOrCreate(), BlockDisplayRenderer::new);
		event.registerBlockEntityRenderer(XKDecoEntityTypes.HOLOGRAM.getOrCreate(), HologramRenderer::new);
	}

	public static void init() {
		var modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		modEventBus.addListener(ClientProxy::setEntityRenderers);

		ModelLoadingPlugin.register(ctx -> {
			ResourceLocation airDuctModel = XKDeco.id("block/air_duct");
			ctx.resolveModel().register(context -> {
				if (!context.id().equals(airDuctModel)) {
					return null;
				}
				ResourceLocation file = ModelBakery.MODEL_LISTER.idToFile(context.id());
				Optional<Resource> resource = Minecraft.getInstance().getResourceManager().getResource(file);
				if (resource.isEmpty()) {
					return null;
				}
				try (BufferedReader reader = resource.get().openAsReader()) {
					JsonObject jsonObject = GsonHelper.parse(reader);
					if (!GsonHelper.getAsString(jsonObject, "xkdeco:loader").equals("xkdeco:air_duct")) {
						return null;
					}
					return new AirDuctModel(
							ResourceLocation.tryParse(GsonHelper.getAsString(jsonObject, "straight")),
							ResourceLocation.tryParse(GsonHelper.getAsString(jsonObject, "corner")),
							ResourceLocation.tryParse(GsonHelper.getAsString(jsonObject, "cover")),
							ResourceLocation.tryParse(GsonHelper.getAsString(jsonObject, "frame")));
				} catch (Exception e) {
					XKDeco.LOGGER.error("Failed to load air duct model", e);
					return null;
				}
			});
			Map<WallBlock, MimicWallModel> wallModels = Maps.newHashMap();
			BlockStateResolver resolver = context -> {
				MimicWallModel wallModel = wallModels.computeIfAbsent(
						((MimicWallBlock) context.block()).getWallDelegate(),
						MimicWallModel::new);
				for (BlockState blockState : context.block().getStateDefinition().getPossibleStates()) {
					context.setModel(blockState, wallModel);
				}
			};
			for (MimicWallBlock block : MimicWallsLoader.mimicWalls()) {
				ctx.registerBlockStateResolver(block, resolver);
			}
			ctx.resolveModel().register(context -> {
				ResourceLocation modelId = context.id();
				if (!modelId.getNamespace().equals(XKDeco.ID)) {
					return null;
				}
				if (!modelId.getPath().startsWith("block/mimic/") && !modelId.getPath().startsWith("item/mimic/")) {
					return null;
				}
				var id = XKDeco.id(modelId.getPath().substring(modelId.getPath().indexOf('/') + 1));
				Block block = BuiltInRegistries.BLOCK.getOptional(id).orElse(null);
				if (block instanceof MimicWallBlock mimicWallBlock) {
					return wallModels.computeIfAbsent(mimicWallBlock.getWallDelegate(), MimicWallModel::new);
				}
				return null;
			});
		});
	}
}
