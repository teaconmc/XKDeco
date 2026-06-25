package org.teacon.xkdeco.util;

import org.teacon.xkdeco.XKDeco;
import org.teacon.xkdeco.block.MimicWallBlock;
import org.teacon.xkdeco.client.model.AirDuctModel;
import org.teacon.xkdeco.client.model.MimicWallModel;
import org.teacon.xkdeco.client.renderer.BlockDisplayRenderer;
import org.teacon.xkdeco.client.renderer.HologramRenderer;
import org.teacon.xkdeco.client.renderer.ItemDisplayRenderer;
import org.teacon.xkdeco.init.MimicWallsLoader;
import org.teacon.xkdeco.init.XKDecoEntityTypes;

import javax.annotation.ParametersAreNonnullByDefault;
import net.fabricmc.fabric.api.client.model.loading.v1.BlockStateResolver;
import net.fabricmc.fabric.api.client.model.loading.v1.CustomUnbakedBlockStateModel;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@Mod(value = XKDeco.ID, dist = Dist.CLIENT)
@ParametersAreNonnullByDefault
public final class ClientProxy {
	public static void setEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerBlockEntityRenderer(XKDecoEntityTypes.ITEM_DISPLAY.getOrCreate(), ItemDisplayRenderer::new);
		event.registerBlockEntityRenderer(XKDecoEntityTypes.ITEM_PROJECTOR.getOrCreate(), ItemDisplayRenderer::new);
		event.registerBlockEntityRenderer(XKDecoEntityTypes.BLOCK_DISPLAY.getOrCreate(), BlockDisplayRenderer::new);
		event.registerBlockEntityRenderer(XKDecoEntityTypes.HOLOGRAM.getOrCreate(), HologramRenderer::new);
	}

	public ClientProxy(IEventBus modEventBus) {
		modEventBus.addListener(ClientProxy::setEntityRenderers);

		CustomUnbakedBlockStateModel.register(AirDuctModel.ID, AirDuctModel.MAP_CODEC);
		ModelLoadingPlugin.register(ctx -> {
			BuiltInRegistries.BLOCK.getOptional(XKDeco.id("air_duct"))
					.ifPresent(block -> ctx.registerBlockStateResolver(block, ClientProxy::resolveAirDuct));
			for (MimicWallBlock block : MimicWallsLoader.mimicWalls()) {
				ctx.registerBlockStateResolver(block, ClientProxy::resolveMimicWall);
			}
		});
	}

	private static void resolveAirDuct(BlockStateResolver.Context context) {
		var model = new AirDuctModel(
				XKDeco.id("block/furniture/air_duct"),
				XKDeco.id("block/furniture/air_duct_corner"),
				XKDeco.id("block/furniture/air_duct_cover"),
				XKDeco.id("block/furniture/air_duct_frame")).asRoot();
		for (BlockState blockState : context.block().getStateDefinition().getPossibleStates()) {
			context.setModel(blockState, model);
		}
	}

	private static void resolveMimicWall(BlockStateResolver.Context context) {
		var model = new MimicWallModel(((MimicWallBlock) context.block()).getWallDelegate());
		for (BlockState blockState : context.block().getStateDefinition().getPossibleStates()) {
			context.setModel(blockState, model);
		}
	}
}
