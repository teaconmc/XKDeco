package org.teacon.xkdeco.util;

import org.teacon.xkdeco.XKDeco;
import org.teacon.xkdeco.client.model.AirDuctModel;
import org.teacon.xkdeco.client.renderer.BlockDisplayRenderer;
import org.teacon.xkdeco.client.renderer.HologramRenderer;
import org.teacon.xkdeco.client.renderer.ItemDisplayRenderer;
import org.teacon.xkdeco.client.renderer.ItemProjectorRenderer;
import org.teacon.xkdeco.init.XKDecoEntityTypes;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterBlockStateModels;

@Mod(value = XKDeco.ID, dist = Dist.CLIENT)
public final class ClientProxy {
	public static void setEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerBlockEntityRenderer(XKDecoEntityTypes.ITEM_DISPLAY.getOrCreate(), ItemDisplayRenderer::new);
		event.registerBlockEntityRenderer(XKDecoEntityTypes.ITEM_PROJECTOR.getOrCreate(), ItemProjectorRenderer::new);
		event.registerBlockEntityRenderer(XKDecoEntityTypes.BLOCK_DISPLAY.getOrCreate(), BlockDisplayRenderer::new);
		event.registerBlockEntityRenderer(XKDecoEntityTypes.HOLOGRAM.getOrCreate(), HologramRenderer::new);
	}

	public ClientProxy(IEventBus modEventBus) {
		modEventBus.addListener(ClientProxy::setEntityRenderers);
		modEventBus.addListener(ClientProxy::registerBlockStateModels);
	}

	private static void registerBlockStateModels(RegisterBlockStateModels event) {
		event.registerModel(AirDuctModel.ID, AirDuctModel.MAP_CODEC);
	}
}
