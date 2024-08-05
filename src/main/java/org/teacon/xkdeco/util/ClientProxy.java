package org.teacon.xkdeco.util;

import java.io.BufferedReader;
import java.util.Optional;

import org.teacon.xkdeco.XKDeco;
import org.teacon.xkdeco.client.model.AirDuctModel;
import org.teacon.xkdeco.client.renderer.BlockDisplayRenderer;
import org.teacon.xkdeco.client.renderer.ItemDisplayRenderer;
import org.teacon.xkdeco.init.XKDecoEntityTypes;

import com.google.gson.JsonObject;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.GsonHelper;
import snownee.kiwi.util.NotNullByDefault;

@NotNullByDefault
public final class ClientProxy implements ClientModInitializer {

	public static void init() {
		BlockEntityRenderers.register(XKDecoEntityTypes.ITEM_DISPLAY.getOrCreate(), ItemDisplayRenderer::new);
		BlockEntityRenderers.register(XKDecoEntityTypes.ITEM_PROJECTOR.getOrCreate(), ItemDisplayRenderer::new);
		BlockEntityRenderers.register(XKDecoEntityTypes.BLOCK_DISPLAY.getOrCreate(), BlockDisplayRenderer::new);

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
					if (!GsonHelper.getAsString(jsonObject, "loader").equals("xkdeco:air_duct")) {
						return null;
					}
					return new AirDuctModel(
							new ResourceLocation(GsonHelper.getAsString(jsonObject, "straight")),
							new ResourceLocation(GsonHelper.getAsString(jsonObject, "corner")),
							new ResourceLocation(GsonHelper.getAsString(jsonObject, "cover")),
							new ResourceLocation(GsonHelper.getAsString(jsonObject, "frame")));
				} catch (Exception e) {
					XKDeco.LOGGER.error("Failed to load air duct model", e);
					return null;
				}
			});
		});
	}

	@Override
	public void onInitializeClient() {

	}
}
