package org.teacon.xkdeco.client;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.client.event.CustomizeGuiOverlayEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.registries.RegistryObject;
import org.teacon.xkdeco.block.XKDecoBlock;
import org.teacon.xkdeco.blockentity.BlockDisplayBlockEntity;
import org.teacon.xkdeco.blockentity.ItemDisplayBlockEntity;
import org.teacon.xkdeco.blockentity.WallBlockEntity;
import org.teacon.xkdeco.client.renderer.BlockDisplayRenderer;
import org.teacon.xkdeco.client.renderer.ItemDisplayRenderer;
import org.teacon.xkdeco.client.renderer.WallRenderer;
import org.teacon.xkdeco.client.renderer.XKDecoWithoutLevelRenderer;
import org.teacon.xkdeco.entity.CushionEntity;
import org.teacon.xkdeco.init.XKDecoObjects;
import org.teacon.xkdeco.resource.SpecialWallResources;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public final class XKDecoClient {

    // https://github.com/MinecraftForge/MinecraftForge/pull/8786
    // https://forge-render-refactor.notion.site/forge-render-refactor/ecd69011d01042c48a0fca80696cb4da?v=47332327988948ebaf911b06b2aa1f5d&p=d3a706981ffd43768885e9a9b6163964&pm=s
    public static void addDebugText(CustomizeGuiOverlayEvent.DebugText event) {
        var mc = Minecraft.getInstance();
        var cameraEntity = mc.getCameraEntity();
        if (mc.options.renderDebug && cameraEntity != null) {
            var block = cameraEntity.pick(ForgeGui.rayTraceDistance, 0F, false);
            if (block.getType() == HitResult.Type.BLOCK) {
                var direction = ((BlockHitResult) block).getDirection();
                var pos = ((BlockHitResult) block).getBlockPos();
                if (Direction.Plane.HORIZONTAL.test(direction)) {
                    var state = cameraEntity.level().getBlockState(pos);
                    if (state.getBlock() instanceof XKDecoBlock.Roof roof) {
                        var sideHeight = roof.getSideHeight(state, direction);
                        event.getRight().add("Roof Side Height L: %d M: %d R: %d"
                                .formatted(sideHeight.getLeft(), sideHeight.getMiddle(), sideHeight.getRight()));
                    }
                }
            }
        }
    }

    public static void setItemColors(RegisterColorHandlersEvent.Item event) {
        var blockColors = event.getBlockColors();
        var blockItemColor = (ItemColor) (stack, tintIndex) -> {
            var state = ((BlockItem) stack.getItem()).getBlock().defaultBlockState();
            return blockColors.getColor(state, null, null, tintIndex);
        };
        var waterItemColor = (ItemColor) (stack, tintIndex) -> 0x3f76e4;
        event.register(blockItemColor, XKDecoObjects.ITEMS.getEntries().stream().filter(r -> r.getId()
                .getPath().contains(XKDecoObjects.GRASS_PREFIX)).map(RegistryObject::get).toArray(Item[]::new));
        event.register(blockItemColor, XKDecoObjects.ITEMS.getEntries().stream().filter(r -> r.getId()
                .getPath().contains(XKDecoObjects.PLANTABLE_PREFIX)).map(RegistryObject::get).toArray(Item[]::new));
        event.register(blockItemColor, XKDecoObjects.ITEMS.getEntries().stream().filter(r -> r.getId()
                .getPath().contains(XKDecoObjects.WILLOW_PREFIX)).map(RegistryObject::get).toArray(Item[]::new));
        event.register(blockItemColor, XKDecoObjects.ITEMS.getEntries().stream().filter(r -> r.getId()
                .getPath().contains(XKDecoObjects.LEAVES_DARK_SUFFIX)).map(RegistryObject::get).toArray(Item[]::new));
        event.register(waterItemColor, XKDecoObjects.ITEMS.getEntries().stream().filter(r -> r.getId()
                .getPath().contains(XKDecoObjects.STONE_WATER_PREFIX)).map(RegistryObject::get).toArray(Item[]::new));
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
                .getPath().contains(XKDecoObjects.GRASS_PREFIX)).map(RegistryObject::get).toArray(Block[]::new));
        event.register(grassBlockColor, XKDecoObjects.BLOCKS.getEntries().stream().filter(r -> r.getId()
                .getPath().contains(XKDecoObjects.PLANTABLE_PREFIX)).map(RegistryObject::get).toArray(Block[]::new));
        event.register(leavesBlockColor, XKDecoObjects.BLOCKS.getEntries().stream().filter(r -> r.getId()
                .getPath().contains(XKDecoObjects.WILLOW_PREFIX)).map(RegistryObject::get).toArray(Block[]::new));
        event.register(leavesBlockColor, XKDecoObjects.BLOCKS.getEntries().stream().filter(r -> r.getId()
                .getPath().contains(XKDecoObjects.LEAVES_DARK_SUFFIX)).map(RegistryObject::get).toArray(Block[]::new));
        event.register(waterBlockColor, XKDecoObjects.BLOCKS.getEntries().stream().filter(r -> r.getId()
                .getPath().contains(XKDecoObjects.STONE_WATER_PREFIX)).map(RegistryObject::get).toArray(Block[]::new));
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

    public static void setAdditionalPackFinder(AddPackFindersEvent event) {
        event.addRepositorySource(consumer -> consumer.accept(SpecialWallResources.create()));
    }
}
