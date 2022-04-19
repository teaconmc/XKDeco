package org.teacon.xkdeco.data;

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
import java.util.stream.Stream;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public final class XKDecoBlockStateProvider extends BlockStateProvider {
    private static final Logger LOGGER = LogManager.getLogger("XKDeco");

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
            if (block instanceof SlabBlock slabBlock) {
                this.slabBlock(slabBlock, unchecked(id, ""), unchecked(id, "_top"), unchecked(getDoubleSlabId(id), ""));
                this.simpleBlockItem(slabBlock, unchecked(id, ""));
            } else if (block instanceof StairBlock stairBlock) {
                this.stairsBlock(stairBlock, unchecked(id, ""), unchecked(id, "_inner"), unchecked(id, "_outer"));
                this.simpleBlockItem(stairBlock, unchecked(id, ""));
            } else if (block instanceof RotatedPillarBlock rotatedPillarBlock) {
                this.axisBlock(rotatedPillarBlock, unchecked(id, ""), unchecked(id, "_horizontal"));
                this.simpleBlockItem(rotatedPillarBlock, unchecked(id, ""));
            } else if (block.defaultBlockState().hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
                this.horizontalBlock(block, unchecked(id, ""));
                this.simpleBlockItem(block, unchecked(id, ""));
            } else {
                this.simpleBlock(block, unchecked(id, ""));
                this.simpleBlockItem(block, unchecked(id, ""));
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

    private static ModelFile unchecked(String id, String suffix) {
        return new ModelFile.UncheckedModelFile(new ResourceLocation(XKDeco.ID, "block/" + id + suffix));
    }
}
