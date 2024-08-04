package org.teacon.xkdeco.util;

import org.teacon.xkdeco.client.renderer.BlockDisplayRenderer;
import org.teacon.xkdeco.client.renderer.ItemDisplayRenderer;
import org.teacon.xkdeco.init.XKDecoEntityTypes;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import snownee.kiwi.util.NotNullByDefault;

@NotNullByDefault
public final class ClientProxy implements ClientModInitializer {

	public static void init() {
		BlockEntityRenderers.register(XKDecoEntityTypes.ITEM_DISPLAY.getOrCreate(), ItemDisplayRenderer::new);
		BlockEntityRenderers.register(XKDecoEntityTypes.ITEM_PROJECTOR.getOrCreate(), ItemDisplayRenderer::new);
		BlockEntityRenderers.register(XKDecoEntityTypes.BLOCK_DISPLAY.getOrCreate(), BlockDisplayRenderer::new);

/*		modEventBus.addListener((ModelEvent.RegisterGeometryLoaders event) -> {
			event.register("air_duct", new IGeometryLoader<UnbakedGeometryWrapper>() {
				@Override
				public UnbakedGeometryWrapper read(
						JsonObject jsonObject,
						JsonDeserializationContext deserializationContext) throws JsonParseException {
					var straight = new ResourceLocation(GsonHelper.getAsString(jsonObject, "straight"));
					var corner = new ResourceLocation(GsonHelper.getAsString(jsonObject, "corner"));
					var cover = new ResourceLocation(GsonHelper.getAsString(jsonObject, "cover"));
					var frame = new ResourceLocation(GsonHelper.getAsString(jsonObject, "frame"));
					return new UnbakedGeometryWrapper(new AirDuctModel(straight, corner, cover, frame));
				}
			});
		});*/
	}

	@Override
	public void onInitializeClient() {

	}
}
