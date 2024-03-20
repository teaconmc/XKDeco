package org.teacon.xkdeco.util;

import org.teacon.xkdeco.block.setting.BlockRenderSettings;
import org.teacon.xkdeco.blockentity.BlockDisplayBlockEntity;
import org.teacon.xkdeco.blockentity.ItemDisplayBlockEntity;
import org.teacon.xkdeco.blockentity.MimicWallBlockEntity;
import org.teacon.xkdeco.client.forge.UnbakedGeometryWrapper;
import org.teacon.xkdeco.client.model.AirDuctModel;
import org.teacon.xkdeco.client.renderer.BlockDisplayRenderer;
import org.teacon.xkdeco.client.renderer.ItemDisplayRenderer;
import org.teacon.xkdeco.client.renderer.MimicWallRenderer;
import org.teacon.xkdeco.client.renderer.XKDecoWithoutLevelRenderer;
import org.teacon.xkdeco.entity.CushionEntity;
import org.teacon.xkdeco.init.XKDecoObjects;
import org.teacon.xkdeco.resource.MimicWallResources;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegistryObject;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public final class ClientProxy {

	public static void setItemColors(RegisterColorHandlersEvent.Item event) {
		var blockColors = event.getBlockColors();
		var blockItemColor = (ItemColor) (stack, tintIndex) -> {
			var state = ((BlockItem) stack.getItem()).getBlock().defaultBlockState();
			return blockColors.getColor(state, null, null, tintIndex);
		};
		var waterItemColor = (ItemColor) (stack, tintIndex) -> 0x3f76e4;
		event.register(blockItemColor, XKDecoObjects.ITEMS.getEntries().stream().filter(r -> r.getId()
				.getPath()
				.contains(XKDecoObjects.GRASS_PREFIX)).map(RegistryObject::get).toArray(Item[]::new));
		event.register(blockItemColor, XKDecoObjects.ITEMS.getEntries().stream().filter(r -> r.getId()
				.getPath()
				.contains(XKDecoObjects.PLANTABLE_PREFIX)).map(RegistryObject::get).toArray(Item[]::new));
		event.register(blockItemColor, XKDecoObjects.ITEMS.getEntries().stream().filter(r -> r.getId()
				.getPath()
				.contains(XKDecoObjects.WILLOW_PREFIX)).map(RegistryObject::get).toArray(Item[]::new));
		event.register(blockItemColor, XKDecoObjects.ITEMS.getEntries().stream().filter(r -> r.getId()
				.getPath()
				.contains(XKDecoObjects.LEAVES_DARK_SUFFIX)).map(RegistryObject::get).toArray(Item[]::new));
		event.register(waterItemColor, XKDecoObjects.ITEMS.getEntries().stream().filter(r -> r.getId()
				.getPath()
				.contains(XKDecoObjects.STONE_WATER_PREFIX)).map(RegistryObject::get).toArray(Item[]::new));
	}

	public static void setBlockColors(RegisterColorHandlersEvent.Block event) {
		var grassBlockColor = (BlockColor) (state, world, pos, tintIndex) -> {
			if (pos != null && world != null) {
				return BiomeColors.getAverageGrassColor(world, pos);
			}
			return GrassColor.get(0.5, 1.0);
		};
		var leavesBlockColor = (BlockColor) (state, world, pos, tintIndex) -> {
			if (pos != null && world != null) {
				return BiomeColors.getAverageFoliageColor(world, pos);
			}
			return FoliageColor.getDefaultColor();
		};
		var waterBlockColor = (BlockColor) (state, world, pos, tintIndex) -> {
			if (pos != null && world != null) {
				return BiomeColors.getAverageWaterColor(world, pos);
			}
			return 0x3f76e4;
		};
		event.register(grassBlockColor, XKDecoObjects.BLOCKS.getEntries().stream().filter(r -> r.getId()
				.getPath()
				.contains(XKDecoObjects.GRASS_PREFIX)).map(RegistryObject::get).toArray(Block[]::new));
		event.register(grassBlockColor, XKDecoObjects.BLOCKS.getEntries().stream().filter(r -> r.getId()
				.getPath()
				.contains(XKDecoObjects.PLANTABLE_PREFIX)).map(RegistryObject::get).toArray(Block[]::new));
		event.register(leavesBlockColor, XKDecoObjects.BLOCKS.getEntries().stream().filter(r -> r.getId()
				.getPath()
				.contains(XKDecoObjects.WILLOW_PREFIX)).map(RegistryObject::get).toArray(Block[]::new));
		event.register(leavesBlockColor, XKDecoObjects.BLOCKS.getEntries().stream().filter(r -> r.getId()
				.getPath()
				.contains(XKDecoObjects.LEAVES_DARK_SUFFIX)).map(RegistryObject::get).toArray(Block[]::new));
		event.register(waterBlockColor, XKDecoObjects.BLOCKS.getEntries().stream().filter(r -> r.getId()
				.getPath()
				.contains(XKDecoObjects.STONE_WATER_PREFIX)).map(RegistryObject::get).toArray(Block[]::new));
	}

	public static void setItemRenderers(RegisterClientReloadListenersEvent event) {
		event.registerReloadListener(XKDecoWithoutLevelRenderer.INSTANCE);
	}

	public static void setEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(CushionEntity.TYPE.get(), NoopRenderer::new);

		event.registerBlockEntityRenderer(MimicWallBlockEntity.TYPE.get(), MimicWallRenderer::new);
		event.registerBlockEntityRenderer(ItemDisplayBlockEntity.TYPE.get(), ItemDisplayRenderer::new);
		event.registerBlockEntityRenderer(BlockDisplayBlockEntity.TYPE.get(), BlockDisplayRenderer::new);
	}

	public static void setAdditionalPackFinder(AddPackFindersEvent event) {
		event.addRepositorySource(consumer -> consumer.accept(MimicWallResources.create()));
	}

	public static void init() {
		var modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		modEventBus.addListener(ClientProxy::setItemColors);
		modEventBus.addListener(ClientProxy::setBlockColors);
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
		modEventBus.addListener((FMLClientSetupEvent event) -> {
			event.enqueueWork(() -> {
//				for (String s : List.of("")) {
//					RenderType cutout = RenderType.cutout();
//					ItemBlockRenderTypes.setRenderLayer(BuiltInRegistries.BLOCK.get(XKDeco.id(s)), cutout);
//				}

				//TODO temporary implementation. data-gen it in the future
				for (RegistryObject<Block> registryObject : XKDecoObjects.BLOCKS.getEntries()) {
					Block block = registryObject.get();
					if (block instanceof DoorBlock || block instanceof TrapDoorBlock) {
						ItemBlockRenderTypes.setRenderLayer(block, RenderType.cutout());
					}
				}

				BlockRenderSettings.finalizeLoading();
			});
		});
	}
}
