package org.teacon.xkdeco.data;

import com.google.common.collect.ImmutableSet;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.data.DataGenerator;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.teacon.xkdeco.XKDeco;
import org.teacon.xkdeco.block.IsotropicRoofBlock;
import org.teacon.xkdeco.block.SpecialWardrobeBlock;
import org.teacon.xkdeco.init.XKDecoObjects;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public final class XKDecoBlockStateProvider extends BlockStateProvider {
    private static final Logger LOGGER = LogManager.getLogger("XKDeco");

    private static final Set<String> BLOCK_STATES_RANDOMIZED = ImmutableSet.of(
            "calligraphy", "cup", "ebony_shelf", "ink_painting",
            "inscription_bronze_block", "mahogany_shelf",
            "maya_pictogram_stone", "refreshments",
            "varnished_shelf", "weiqi_board", "xiangqi_board"
    );
    private static final Set<String> BLOCK_STATES_SKIP = ImmutableSet.of(
            "cup", "fruit_platter", "maya_single_screw_thread_stone",
            "refreshments", "screw_thread_bronze_block", "black_roof_ridge",
            "empty_candlestick", "oil_lamp", "empty_bottle_stack", "bottle_stack",
            "mechanical_console", "tech_console", "factory_lamp", "factory_lamp_broken",
            "factory_warning_lamp", "hologram_base"
    );
    private static final Set<String> BLOCK_ITEMS_SKIP = ImmutableSet.of(
            "cup", "item_projector", "refreshments",
            "varnished_wardrobe", "ebony_wardrobe", "mahogany_wardrobe",
            "iron_wardrobe", "glass_wardrobe", "full_glass_wardrobe"
    );

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
            var tabKey = ((TranslatableComponent) block.asItem().getCreativeTabs().iterator().next().getDisplayName()).getKey();
            var path = tabKey.endsWith("_basic") ? "" : tabKey.substring(tabKey.lastIndexOf('_') + 1);
            var randomized = BLOCK_STATES_RANDOMIZED.contains(id);


            if (!BLOCK_STATES_SKIP.contains(id)) {
                if (block instanceof SlabBlock slabBlock) {
                    this.slabBlock(slabBlock, model(id, path, ""), model(id, path, "_top"), getDoubleSlabModel(id, path));
                } else if (block instanceof StairBlock stairBlock) {
                    this.stairsBlock(stairBlock, model(id, path, ""), model(id, path, "_inner"), model(id, path, "_outer"));
                } else if (block instanceof RotatedPillarBlock rotatedPillarBlock) {
                    this.axisBlock(rotatedPillarBlock, model(id, path, ""), model(getHorizontalPillarBlockId(id), path, ""));
                } else if (block instanceof IsotropicRoofBlock roof) {
                    this.getVariantBuilder(roof).forAllStatesExcept(state -> {
                        var prefix = switch (state.getValue(IsotropicRoofBlock.VARIANT)) {
                            case NORMAL -> id.replace(XKDecoObjects.ROOF_SUFFIX, "_roof");
                            case SLOW -> id.replace(XKDecoObjects.ROOF_SUFFIX, "_slow_roof");
                            case STEEP -> id.replace(XKDecoObjects.ROOF_SUFFIX, "_steep_roof");
                        };
                        var shapeSuffix = switch (state.getValue(IsotropicRoofBlock.SHAPE)) {
                            case STRAIGHT -> "";
                            case INNER -> "_inner";
                            case OUTER -> "_outer";
                        };
                        var halfSuffix = switch (state.getValue(IsotropicRoofBlock.HALF)) {
                            case TIP -> "";
                            case BASE -> "_top";
                        };
                        var model = model(prefix, path, shapeSuffix + halfSuffix);
                        var facing2d = state.getValue(IsotropicRoofBlock.FACING).get2DDataValue();
                        return ConfiguredModel.builder().modelFile(model).rotationY((1 + facing2d) % 4 * 90).build();
                    }, IsotropicRoofBlock.WATERLOGGED);
                } else if (block instanceof SpecialWardrobeBlock) {
                    this.getVariantBuilder(block).forAllStates(state -> {
                        var modelName = new StringBuilder(id);
                        if (state.getValue(SpecialWardrobeBlock.DOUBLE)) {
                            modelName.insert(modelName.indexOf(XKDecoObjects.WARDROBE_SUFFIX), "_double");
                        }
                        modelName.append("_").append(state.getValue(SpecialWardrobeBlock.HINGE).getSerializedName());
                        modelName.append(switch (state.getValue(SpecialWardrobeBlock.HALF)) {
                            case UPPER -> "_top";
                            case LOWER -> "_bottom";
                        });
                        if (state.getValue(SpecialWardrobeBlock.OPEN)) {
                            modelName.append("_open");
                        }

                        var model = model(modelName.toString(), path, "");
                        var facing2d = state.getValue(SpecialWardrobeBlock.FACING).get2DDataValue();
                        return ConfiguredModel.builder().modelFile(model).rotationY(facing2d % 4 * 90 + 180).build();
                    });
                } else if (block.defaultBlockState().hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
                    if (randomized) {
                        this.randomizedHorizontalBlock(block, collectRandomizedModels(id, path));
                    } else {
                        this.horizontalBlock(block, model(id, path, ""));
                    }
                } else {
                    if (randomized) {
                        this.simpleBlock(block, collectRandomizedModels(id, path).map(ConfiguredModel::new).toArray(ConfiguredModel[]::new));
                    } else {
                        this.simpleBlock(block, model(id, path, ""));
                    }
                }
            }

            if (!BLOCK_ITEMS_SKIP.contains(id)) {
                if (randomized) {
                    this.simpleBlockItem(block, collectRandomizedModels(id, path).findFirst().orElseThrow());
                } else {
                    this.simpleBlockItem(block, model(id, path, ""));
                }
            }

            var blockClassName = block.getClass().getName();
            var propertyNames = block.defaultBlockState().getProperties().stream().map(Property::getName).toList();
            LOGGER.info("Block [{}] uses [{}] with {} as state property collection", id, blockClassName, propertyNames);
        }
    }

    private void randomizedHorizontalBlock(Block block, Stream<ModelFile> models) {
        var modelList = models.toList();
        getVariantBuilder(block).forAllStates(
                state -> modelList.stream().map(m -> ConfiguredModel.builder().modelFile(m)
                                .rotationY(((int) state.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot() + 180) % 360)
                                .buildLast()
                        )
                        .toArray(ConfiguredModel[]::new)
        );
    }

    private Stream<ModelFile> collectRandomizedModels(String id, String path) {
        Collection<ModelFile> models = new ArrayList<>();
        var m = new ExistingModelFileProxy(new ResourceLocation(XKDeco.ID, Path.of("block/", path, id).toString().replace('\\', '/')), this.models().existingFileHelper);
        if (m.exists()) {
            models.add(m);
        }
        for (var i = 1; ; i++) {
            m = new ExistingModelFileProxy(new ResourceLocation(XKDeco.ID, Path.of("block/", path, id + i).toString().replace('\\', '/')), this.models().existingFileHelper);
            if (m.exists()) models.add(m);
            else break;
        }
        return models.stream();
    }

    private String getHorizontalPillarBlockId(String pillarId) {
        if (pillarId.endsWith("_wood")) {
            return pillarId;
        } else {
            return pillarId + "_horizontal";
        }
    }

    private ModelFile getDoubleSlabModel(String slabId, String path) {
        var helper = this.models().existingFileHelper;
        // _slab_full has higher priority in searching
        var searchTargets = Stream.of("_slab_full", "", "s", "_block", "_planks")
                .map(s -> slabId.replace(XKDecoObjects.SLAB_SUFFIX, s))
                .flatMap(s -> Stream.of( // search XKDeco models first
                        new ResourceLocation(XKDeco.ID, Path.of("block/", path, s).toString().replace('\\', '/')),
                        new ResourceLocation("minecraft", "block/" + s)
                ))
                .toList();
        return searchTargets.stream()
                .map(location -> new ExistingModelFileProxy(location, helper))
                .filter(ExistingModelFileProxy::exists)
                .findFirst()
                .orElseThrow(() -> new RuntimeException(new FileNotFoundException(
                        searchTargets.stream().map(ResourceLocation::toString).collect(Collectors.joining(" or "))
                )));
    }

    private ModelFile model(String id, String path, String suffix) {
        return new ModelFile.UncheckedModelFile(new ResourceLocation(XKDeco.ID, Path.of("block/", path, id + suffix).toString().replace('\\', '/')));
    }

    private static class ExistingModelFileProxy extends ModelFile.ExistingModelFile {
        public ExistingModelFileProxy(ResourceLocation location, ExistingFileHelper existingHelper) {
            super(location, existingHelper);
        }

        @Override
        protected boolean exists() {
            return super.exists();
        }

        public static boolean modelExists(ResourceLocation location, ExistingFileHelper existingFileHelper) {
            return new ExistingModelFileProxy(location, existingFileHelper).exists();
        }
    }
}
