package org.teacon.xkdeco.util;

import org.teacon.xkdeco.XKDeco;
import org.teacon.xkdeco.block.MimicWallBlock;
import org.teacon.xkdeco.client.model.AirDuctModel;
import org.teacon.xkdeco.client.model.MimicWallItemModel;
import org.teacon.xkdeco.client.model.MimicWallModel;
import org.teacon.xkdeco.client.renderer.BlockDisplayRenderer;
import org.teacon.xkdeco.client.renderer.HologramRenderer;
import org.teacon.xkdeco.client.renderer.ItemDisplayRenderer;
import org.teacon.xkdeco.client.renderer.ItemProjectorRenderer;
import org.teacon.xkdeco.init.MimicWallsLoader;
import org.teacon.xkdeco.init.XKDecoEntityTypes;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.BlockStateResolver;
import net.fabricmc.fabric.api.client.model.loading.v1.CustomUnbakedBlockStateModel;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModels;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.state.BlockState;

public final class ClientProxy implements ClientModInitializer {

	public static void init() {
		BlockEntityRenderers.register(XKDecoEntityTypes.ITEM_DISPLAY.getOrCreate(), ItemDisplayRenderer::new);
		BlockEntityRenderers.register(XKDecoEntityTypes.ITEM_PROJECTOR.getOrCreate(), ItemProjectorRenderer::new);
		BlockEntityRenderers.register(XKDecoEntityTypes.BLOCK_DISPLAY.getOrCreate(), BlockDisplayRenderer::new);
		BlockEntityRenderers.register(XKDecoEntityTypes.HOLOGRAM.getOrCreate(), HologramRenderer::new);

		CustomUnbakedBlockStateModel.register(AirDuctModel.ID, AirDuctModel.MAP_CODEC);
		ItemModels.ID_MAPPER.put(MimicWallItemModel.ID, MimicWallItemModel.Unbaked.MAP_CODEC);

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

	@Override
	public void onInitializeClient() {
		init();
	}
}
