package org.teacon.xkdeco.client;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.model.ForgeModelBakery;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.teacon.xkdeco.XKDeco;
import org.teacon.xkdeco.block.SpecialWallBlock;
import org.teacon.xkdeco.block.XKDecoBlock;
import org.teacon.xkdeco.blockentity.BlockDisplayBlockEntity;
import org.teacon.xkdeco.blockentity.ItemDisplayBlockEntity;
import org.teacon.xkdeco.blockentity.WallBlockEntity;
import org.teacon.xkdeco.client.renderer.blockentity.BlockDisplayRenderer;
import org.teacon.xkdeco.client.renderer.blockentity.ItemDisplayRenderer;
import org.teacon.xkdeco.client.renderer.blockentity.WallRenderer;
import org.teacon.xkdeco.client.renderer.blockentity.XKDecoWithoutLevelRenderer;
import org.teacon.xkdeco.entity.CushionEntity;
import org.teacon.xkdeco.init.XKDecoObjects;

import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.Method;
import java.util.Objects;

import static net.minecraft.client.renderer.block.BlockModelShaper.stateToModelLocation;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public final class XKDecoClient {
    private static final Method CACHE_AND_QUEUE_DEPS = ObfuscationReflectionHelper.findMethod(ModelBakery.class, "m_119352_", ResourceLocation.class, UnbakedModel.class);

    public static void setItemColors(ColorHandlerEvent.Item event) {
        var blockColors = event.getBlockColors();
        var blockItemColor = (ItemColor) (stack, tintIndex) -> {
            var state = ((BlockItem) stack.getItem()).getBlock().defaultBlockState();
            return blockColors.getColor(state, null, null, tintIndex);
        };
        var itemColors = event.getItemColors();
        itemColors.register(blockItemColor, XKDecoObjects.ITEMS.getEntries().stream().filter(r -> r.getId()
                .getPath().contains(XKDecoObjects.GRASS_PREFIX)).map(RegistryObject::get).toArray(Item[]::new));
        itemColors.register(blockItemColor, XKDecoObjects.ITEMS.getEntries().stream().filter(r -> r.getId()
                .getPath().contains(XKDecoObjects.PLANTABLE_PREFIX)).map(RegistryObject::get).toArray(Item[]::new));
        itemColors.register(blockItemColor, XKDecoObjects.ITEMS.getEntries().stream().filter(r -> r.getId()
                .getPath().contains(XKDecoObjects.WILLOW_PREFIX)).map(RegistryObject::get).toArray(Item[]::new));
        itemColors.register(blockItemColor, XKDecoObjects.ITEMS.getEntries().stream().filter(r -> r.getId()
                .getPath().contains(XKDecoObjects.LEAVES_DARK_SUFFIX)).map(RegistryObject::get).toArray(Item[]::new));
    }

    public static void setBlockColors(ColorHandlerEvent.Block event) {
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
        var blockColors = event.getBlockColors();
        blockColors.register(grassBlockColor, XKDecoObjects.BLOCKS.getEntries().stream().filter(r -> r.getId()
                .getPath().contains(XKDecoObjects.GRASS_PREFIX)).map(RegistryObject::get).toArray(Block[]::new));
        blockColors.register(grassBlockColor, XKDecoObjects.BLOCKS.getEntries().stream().filter(r -> r.getId()
                .getPath().contains(XKDecoObjects.PLANTABLE_PREFIX)).map(RegistryObject::get).toArray(Block[]::new));
        blockColors.register(leavesBlockColor, XKDecoObjects.BLOCKS.getEntries().stream().filter(r -> r.getId()
                .getPath().contains(XKDecoObjects.WILLOW_PREFIX)).map(RegistryObject::get).toArray(Block[]::new));
        blockColors.register(leavesBlockColor, XKDecoObjects.BLOCKS.getEntries().stream().filter(r -> r.getId()
                .getPath().contains(XKDecoObjects.LEAVES_DARK_SUFFIX)).map(RegistryObject::get).toArray(Block[]::new));
    }

    public static void setCutoutBlocks(FMLClientSetupEvent event) {
        for (var entry : XKDecoObjects.BLOCKS.getEntries()) {
            var id = entry.getId().getPath();
            if (entry.get() instanceof XKDecoBlock.Basic) {
                ItemBlockRenderTypes.setRenderLayer(entry.get(), RenderType.cutout());
            } else if (entry.get() instanceof XKDecoBlock.Special) {
                ItemBlockRenderTypes.setRenderLayer(entry.get(), RenderType.cutout());
            } else if (id.contains(XKDecoObjects.GLASS_SUFFIX) || id.contains(XKDecoObjects.TRANSLUCENT_PREFIX)) {
                ItemBlockRenderTypes.setRenderLayer(entry.get(), RenderType.translucent());
            } else if (id.contains(XKDecoObjects.GLASS_PREFIX) || id.contains(XKDecoObjects.HOLLOW_PREFIX) || id.contains(XKDecoObjects.BIG_TABLE_SUFFIX) || id.contains(XKDecoObjects.TALL_TABLE_SUFFIX)) {
                ItemBlockRenderTypes.setRenderLayer(entry.get(), RenderType.cutout());
            } else if (id.contains(XKDecoObjects.GRASS_PREFIX) || id.contains(XKDecoObjects.LEAVES_SUFFIX) || id.contains(XKDecoObjects.BLOSSOM_SUFFIX)) {
                ItemBlockRenderTypes.setRenderLayer(entry.get(), RenderType.cutoutMipped());
            }
        }
    }

    public static void setItemRenderers(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(XKDecoWithoutLevelRenderer.INSTANCE);
    }

    public static void setEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(CushionEntity.TYPE.get(), NoopRenderer::new);

        event.registerBlockEntityRenderer(WallBlockEntity.TYPE.get(), WallRenderer::new);
        event.registerBlockEntityRenderer(ItemDisplayBlockEntity.TYPE.get(), ItemDisplayRenderer::new);
        event.registerBlockEntityRenderer(BlockDisplayBlockEntity.TYPE.get(), BlockDisplayRenderer::new);
    }

    public static void setAdditionalBakedModels(ModelRegistryEvent event) {
        var bakery = Objects.requireNonNull(ForgeModelBakery.instance());
        try {
            for (Block block : ForgeRegistries.BLOCKS.getValues()) {
                if (block instanceof SpecialWallBlock wall) {
                    var name = Objects.requireNonNull(wall.getRegistryName());
                    for (var state : wall.getStateDefinition().getPossibleStates()) {
                        var blockModelName = stateToModelLocation(state);
                        var modelName = new ResourceLocation(XKDeco.ID, "block/" + XKDecoObjects.WALL_BLOCK_ENTITY);
                        CACHE_AND_QUEUE_DEPS.invoke(bakery, blockModelName, bakery.getModel(modelName));
                    }
                    var itemModelName = new ModelResourceLocation(name, "inventory");
                    var modelName = new ResourceLocation(XKDeco.ID, "item/" + XKDecoObjects.WALL_BLOCK_ENTITY);
                    CACHE_AND_QUEUE_DEPS.invoke(bakery, itemModelName, bakery.getModel(modelName));
                }
            }
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
