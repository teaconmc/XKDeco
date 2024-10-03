package org.teacon.xkdeco.util;

import org.teacon.xkdeco.XKDeco;
import org.teacon.xkdeco.client.forge.UnbakedGeometryWrapper;
import org.teacon.xkdeco.client.model.AirDuctModel;
import org.teacon.xkdeco.client.renderer.BlockDisplayRenderer;
import org.teacon.xkdeco.client.renderer.ItemDisplayRenderer;
import org.teacon.xkdeco.client.renderer.MimicWallRenderer;
import org.teacon.xkdeco.client.renderer.XKDecoWithoutLevelRenderer;
import org.teacon.xkdeco.init.MimicWallsLoader;
import org.teacon.xkdeco.init.XKDecoEntityTypes;
import org.teacon.xkdeco.resource.MimicWallResources;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import net.neoforged.neoforge.event.AddPackFindersEvent;

@Mod(value = XKDeco.ID, dist = Dist.CLIENT)
@ParametersAreNonnullByDefault
public final class ClientProxy {

	public static void setItemRenderers(RegisterClientReloadListenersEvent event) {
		event.registerReloadListener(XKDecoWithoutLevelRenderer.INSTANCE);
	}

	public static void setEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerBlockEntityRenderer(XKDecoEntityTypes.MIMIC_WALL.getOrCreate(), MimicWallRenderer::new);
		event.registerBlockEntityRenderer(XKDecoEntityTypes.ITEM_DISPLAY.getOrCreate(), ItemDisplayRenderer::new);
		event.registerBlockEntityRenderer(XKDecoEntityTypes.ITEM_PROJECTOR.getOrCreate(), ItemDisplayRenderer::new);
		event.registerBlockEntityRenderer(XKDecoEntityTypes.BLOCK_DISPLAY.getOrCreate(), BlockDisplayRenderer::new);
	}

	public static void setAdditionalPackFinder(AddPackFindersEvent event) {
		event.addRepositorySource(consumer -> consumer.accept(MimicWallResources.create()));
	}

	public ClientProxy(IEventBus modEventBus) {
		modEventBus.addListener(ClientProxy::setItemRenderers);
		modEventBus.addListener(ClientProxy::setEntityRenderers);
		modEventBus.addListener(ClientProxy::setAdditionalPackFinder);
		modEventBus.addListener(MimicWallsLoader::registerClientExtensions);

		modEventBus.addListener((ModelEvent.RegisterGeometryLoaders event) -> {
			event.register(XKDeco.id("air_duct"), new IGeometryLoader<UnbakedGeometryWrapper>() {
				@Override
				public UnbakedGeometryWrapper read(
						JsonObject jsonObject,
						JsonDeserializationContext deserializationContext) throws JsonParseException {
					var straight = ResourceLocation.parse(GsonHelper.getAsString(jsonObject, "straight"));
					var corner = ResourceLocation.parse(GsonHelper.getAsString(jsonObject, "corner"));
					var cover = ResourceLocation.parse(GsonHelper.getAsString(jsonObject, "cover"));
					var frame = ResourceLocation.parse(GsonHelper.getAsString(jsonObject, "frame"));
					return new UnbakedGeometryWrapper(new AirDuctModel(straight, corner, cover, frame));
				}
			});
		});
	}

}
