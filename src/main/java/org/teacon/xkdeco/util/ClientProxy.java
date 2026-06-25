package org.teacon.xkdeco.util;

import java.util.Map;
import java.util.function.Function;

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
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterBlockStateModels;

@Mod(value = XKDeco.ID, dist = Dist.CLIENT)
@ParametersAreNonnullByDefault
public final class ClientProxy {
	public ClientProxy(IEventBus modEventBus) {
		modEventBus.addListener(ClientProxy::setEntityRenderers);
		modEventBus.addListener(ClientProxy::registerBlockStateModels);
		modEventBus.addListener(ClientProxy::onModifyBakingResult);
	}

	public static void setEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerBlockEntityRenderer(XKDecoEntityTypes.ITEM_DISPLAY.getOrCreate(), ItemDisplayRenderer::new);
		event.registerBlockEntityRenderer(XKDecoEntityTypes.ITEM_PROJECTOR.getOrCreate(), ItemDisplayRenderer::new);
		event.registerBlockEntityRenderer(XKDecoEntityTypes.BLOCK_DISPLAY.getOrCreate(), BlockDisplayRenderer::new);
		event.registerBlockEntityRenderer(XKDecoEntityTypes.HOLOGRAM.getOrCreate(), HologramRenderer::new);
	}

	// Air duct: dynamic, connection-driven block-state model (replaces the Fabric custom model loader).
	public static void registerBlockStateModels(RegisterBlockStateModels event) {
		event.registerModel(AirDuctModel.ID, AirDuctModel.MAP_CODEC);
	}

	// Mimic walls are registered at runtime (incl. modded walls) so they have no static blockstate JSON.
	// Inject each mimic wall's BlockStateModel into the (mutable) baking result, wiring it to look up its
	// delegate + neighbor wall models from the same baked map. Replaces the Fabric BlockStateResolver.
	public static void onModifyBakingResult(ModelEvent.ModifyBakingResult event) {
		ModelBakery.BakingResult result = event.getBakingResult();
		Map<BlockState, BlockStateModel> models = result.blockStateModels();
		Function<BlockState, BlockStateModel> lookup = result::getBlockStateModel;

		for (MimicWallBlock mimic : MimicWallsLoader.mimicWalls()) {
			WallBlock delegateWall = mimic.getWallDelegate();
			// Own geometry = the delegate wall's POST only (default state). Connection arms are added
			// per-direction by MimicWallModel's neighbor loop. This matches the old MimicWallBakedModel,
			// which wrapped base.defaultBlockState(); binding the arm-bearing variant here would draw the
			// mimic's own arms AND the borrowed neighbor arms, double-rendering every connection.
			BlockStateModel delegateModel = result.getBlockStateModel(delegateWall.defaultBlockState());
			for (BlockState mimicState : mimic.getStateDefinition().getPossibleStates()) {
				models.put(mimicState, new MimicWallModel(delegateWall, delegateModel, lookup));
			}
		}
	}
}
