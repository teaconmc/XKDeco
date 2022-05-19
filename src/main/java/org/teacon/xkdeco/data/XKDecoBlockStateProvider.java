package org.teacon.xkdeco.data;

import com.google.common.collect.Lists;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.teacon.xkdeco.XKDeco;
import org.teacon.xkdeco.init.XKDecoObjects;

import javax.annotation.ParametersAreNonnullByDefault;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public final class XKDecoBlockStateProvider extends BlockStateProvider {
    private static final Logger LOGGER = LogManager.getLogger("XKDeco");

    Set<ResourceLocation> skipBlockState = Lists.newArrayList(
            "calligraphy",
            "crimson_nylium_slab",
            "crossed_mud_wall_slab",
            "cup",
            "dirt_path_slab",
            "dirt_slab",
            "ebony_shelf",
            "ebony_slab",
            "ebony_wood",
            "end_stone_slab",
            "fruit_platter",
            "grass_block_slab",
            "ink_painting",
            "inscription_bronze_block",
            "mahogany_shelf",
            "mahogany_slab",
            "mahogany_wood",
            "maya_pictogram_stone",
            "maya_polished_stonebrick_slab",
            "maya_single_screw_thread_stone",
            "mycelium_slab",
            "netherrack_slab",
            "podzol_slab",
            "polished_red_sandstone_slab",
            "polished_sandstone_slab",
            "refreshments",
            "screw_thread_bronze_block",
            "varnished_slab",
            "varnished_wood",
            "varnished_shelf",
            "warped_nylium_slab",
            "weiqi_board",
            "xiangqi_board"
    ).stream().map(str -> new ResourceLocation(XKDeco.ID, str)).collect(Collectors.toUnmodifiableSet());
    Set<ResourceLocation> skipBlockItem = Lists.newArrayList(
            "calligraphy",
            "cup",
            "ebony_shelf",
            "ink_painting",
            "mahogany_shelf",
            "refreshments",
            "varnished_shelf",
            "weiqi_board",
            "white_cherry_blossom",
            "white_cherry_blossom_leaves",
            "xiangqi_board"
    ).stream().map(str -> new ResourceLocation(XKDeco.ID, str)).collect(Collectors.toUnmodifiableSet());

    private XKDecoBlockStateProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, XKDeco.ID, existingFileHelper);
    }

    public static void register(GatherDataEvent event) {
        var generator = event.getGenerator();
        var existingFileHelper = event.getExistingFileHelper();
        generator.addProvider(new XKDecoBlockStateProvider(generator, existingFileHelper));
    }

    @Override
    protected void registerStatesAndModels() {
        for (var entry : XKDecoObjects.BLOCKS.getEntries()) {
            var block = entry.get();
            var id = entry.getId().getPath();
            var tabs = block.asItem().getCreativeTabs();
            String path;
            if (tabs.contains(XKDecoObjects.TAB_NATURE)) {
                path = "nature";
            } else if (tabs.contains(XKDecoObjects.TAB_FURNITURE)) {
                path = "furniture";
            } else {
                path = "";
            }

            if (!skipBlockState.contains(block.getRegistryName())) {
                if (block instanceof SlabBlock slabBlock) {
                    this.slabBlock(slabBlock, unchecked(id, path, ""), unchecked(id, path, "_top"), unchecked(getDoubleSlabId(id), path, ""));
                } else if (block instanceof StairBlock stairBlock) {
                    this.stairsBlock(stairBlock, unchecked(id, path, ""), unchecked(id, path, "_inner"), unchecked(id, path, "_outer"));
                } else if (block instanceof RotatedPillarBlock rotatedPillarBlock) {
                    this.axisBlock(rotatedPillarBlock, unchecked(id, path, ""), unchecked(id, path, "_horizontal"));
                } else if (block.defaultBlockState().hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
                    this.horizontalBlock(block, unchecked(id, path, ""));
                } else {
                    this.simpleBlock(block, unchecked(id, path, ""));
                }
            }

            if (!skipBlockItem.contains(block.getRegistryName())) {
                this.simpleBlockItem(block, unchecked(id, path, ""));
            }

            var blockClassName = block.getClass().getName();
            var propertyNames = block.defaultBlockState().getProperties().stream().map(Property::getName).toList();
            LOGGER.info("Block [{}] uses [{}] with {} as state property collection", id, blockClassName, propertyNames);
        }
    }

    private static String getDoubleSlabId(String slabId) {
        var doubleSlabs = Stream.of("", "s", "_block").map(s -> slabId.replace(XKDecoObjects.SLAB_SUFFIX, s)).toList();
        for (var entry : XKDecoObjects.BLOCKS.getEntries()) {
            var path = entry.getId().getPath();
            if (doubleSlabs.contains(path)) {
                return path;
            }
        }
        return doubleSlabs.get(doubleSlabs.size() - 1);
    }

    private static ModelFile unchecked(String id, String path, String suffix) {
        return new ModelFile.UncheckedModelFile(new ResourceLocation(XKDeco.ID, Path.of("block/", path, id + suffix).toString()));
    }
}
