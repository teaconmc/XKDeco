package org.teacon.xkdeco.util;

import java.util.List;

import org.teacon.xkdeco.block.command.ExportBlocksCommand;
import org.teacon.xkdeco.block.command.ExportCreativeTabsCommand;
import org.teacon.xkdeco.client.forge.UnbakedGeometryWrapper;
import org.teacon.xkdeco.client.model.AirDuctModel;
import org.teacon.xkdeco.client.renderer.BlockDisplayRenderer;
import org.teacon.xkdeco.client.renderer.ItemDisplayRenderer;
import org.teacon.xkdeco.client.renderer.MimicWallRenderer;
import org.teacon.xkdeco.client.renderer.XKDecoWithoutLevelRenderer;
import org.teacon.xkdeco.init.XKDecoEntityTypes;
import org.teacon.xkdeco.resource.MimicWallResources;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public final class ClientProxy {

	public static void setItemRenderers(RegisterClientReloadListenersEvent event) {
		event.registerReloadListener(XKDecoWithoutLevelRenderer.INSTANCE);
	}

	public static void setEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(XKDecoEntityTypes.CUSHION.getOrCreate(), NoopRenderer::new);

		event.registerBlockEntityRenderer(XKDecoEntityTypes.MIMIC_WALL.getOrCreate(), MimicWallRenderer::new);
		event.registerBlockEntityRenderer(XKDecoEntityTypes.ITEM_DISPLAY.getOrCreate(), ItemDisplayRenderer::new);
		event.registerBlockEntityRenderer(XKDecoEntityTypes.BLOCK_DISPLAY.getOrCreate(), BlockDisplayRenderer::new);
	}

	public static void setAdditionalPackFinder(AddPackFindersEvent event) {
		event.addRepositorySource(consumer -> consumer.accept(MimicWallResources.create()));
	}

	public static void init() {
		var modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		modEventBus.addListener(ClientProxy::setItemRenderers);
		modEventBus.addListener(ClientProxy::setEntityRenderers);
		modEventBus.addListener(ClientProxy::setAdditionalPackFinder);

		modEventBus.addListener((ModelEvent.RegisterGeometryLoaders event) -> {
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
		});
//		modEventBus.addListener((FMLClientSetupEvent event) -> {
//			event.enqueueWork(() -> {
////				for (String s : List.of("")) {
////					RenderType cutout = RenderType.cutout();
////					ItemBlockRenderTypes.setRenderLayer(BuiltInRegistries.BLOCK.get(XKDeco.id(s)), cutout);
////				}
//
//				//TODO temporary implementation. data-gen it in the future
//				for (RegistryObject<Block> registryObject : XKDecoObjects.BLOCKS.getEntries()) {
//					Block block = registryObject.get();
//					if (block instanceof DoorBlock || block instanceof TrapDoorBlock) {
//						ItemBlockRenderTypes.setRenderLayer(block, RenderType.cutout());
//					}
//				}
//			});
//		});

		var forgeEventBus = MinecraftForge.EVENT_BUS;
		forgeEventBus.addListener((RegisterCommandsEvent event) -> {
			ExportBlocksCommand.register(event.getDispatcher());
			ExportCreativeTabsCommand.register(event.getDispatcher());
		});
	}

	public static void registerColors(List<Pair<Block, BlockColor>> blocksToAdd, List<Pair<Item, ItemColor>> itemsToAdd) {
		var modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		if (!blocksToAdd.isEmpty()) {
			modEventBus.addListener((RegisterColorHandlersEvent.Block event) -> {
				for (var pair : blocksToAdd) {
					event.register(pair.getSecond(), pair.getFirst());
				}
			});
		}
		if (!itemsToAdd.isEmpty()) {
			modEventBus.addListener((RegisterColorHandlersEvent.Item event) -> {
				for (var pair : itemsToAdd) {
					event.register(pair.getSecond(), pair.getFirst());
				}
			});
		}
	}
}
