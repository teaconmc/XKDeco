package org.teacon.xkdeco.data;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.teacon.xkdeco.XKDeco;
import org.teacon.xkdeco.block.RoofBlock;
import org.teacon.xkdeco.block.RoofEaveBlock;
import org.teacon.xkdeco.block.RoofEndBlock;
import org.teacon.xkdeco.block.RoofFlatBlock;
import org.teacon.xkdeco.block.RoofTipBlock;
import org.teacon.xkdeco.util.RoofUtil;

import com.google.common.collect.Maps;
import com.mojang.logging.LogUtils;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.blockstates.BlockStateGenerator;
import net.minecraft.data.models.blockstates.Condition;
import net.minecraft.data.models.blockstates.MultiPartGenerator;
import net.minecraft.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.data.models.blockstates.PropertyDispatch;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TexturedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.WallSide;

public class XKDModelProvider extends FabricModelProvider {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final ResourceLocation ROOF_INNER_TEXTURE = new ResourceLocation(XKDeco.ID, "block/roof_inner");
	private static final Set<Block> ROTATED_PILLARS = Set.of(
			block("chiseled_bronze_block"),
			block("chiseled_steel_block"),
			block("maya_chiseled_stonebricks"));
	private static final Set<Block> SPECIAL_DOUBLE_SLABS = Set.of(
			block("polished_sandstone_slab"),
			block("polished_red_sandstone_slab"),
			block("maya_polished_stonebrick_slab"));
	private BlockModelGenerators generators;

	public XKDModelProvider(FabricDataOutput output) {
		super(output);
	}

	public static boolean createIfRotatedPillar(Block block, BlockModelGenerators generators) {
		if (!ROTATED_PILLARS.contains(block)) {
			return false;
		}
		generators.createRotatedPillarWithHorizontalVariant(block, TexturedModel.COLUMN, TexturedModel.COLUMN_HORIZONTAL);
		return true;
	}

	public static boolean createIfSpecialDoubleSlabs(Block pBlock, BlockModelGenerators generators) {
		if (!SPECIAL_DOUBLE_SLABS.contains(pBlock)) {
			return false;
		}
		ResourceLocation texture = TextureMapping.getBlockTexture(pBlock);
		TextureMapping $$1 = TextureMapping.column(texture, texture.withPath(s -> s.substring(0, s.length() - 5)));
		ResourceLocation $$2 = ModelTemplates.SLAB_BOTTOM.create(pBlock, $$1, generators.modelOutput);
		ResourceLocation $$3 = ModelTemplates.SLAB_TOP.create(pBlock, $$1, generators.modelOutput);
		ResourceLocation $$4 = ModelTemplates.CUBE_COLUMN.createWithOverride(pBlock, "_full", $$1, generators.modelOutput);
		generators.blockStateOutput.accept(BlockModelGenerators.createSlab(pBlock, $$2, $$3, $$4));
		return true;
	}

	@Override
	public void generateBlockStateModels(BlockModelGenerators generators) {
		this.generators = generators;
		BlockModelGenerators.SHAPE_CONSUMERS = Maps.newHashMap(BlockModelGenerators.SHAPE_CONSUMERS);
		BlockModelGenerators.SHAPE_CONSUMERS.put(BlockFamily.Variant.CUT, BlockModelGenerators.BlockFamilyProvider::fullBlockVariant);
		BlockModelGenerators.SHAPE_CONSUMERS.put(BlockFamily.Variant.POLISHED, BlockModelGenerators.BlockFamilyProvider::fullBlockVariant);
		XKDBlockFamilies.getAllFamilies().filter(BlockFamily::shouldGenerateModel).forEach(family -> {
			Block baseBlock = family.getBaseBlock();
			LOGGER.info("Generating models for block family {}", baseBlock);
			BlockModelGenerators.BlockFamilyProvider provider;
			if (family == XKDBlockFamilies.LINED_MUD_WALL) {
				TextureMapping textureMapping = TextureMapping.column(
						TextureMapping.getBlockTexture(block("lined_mud_wall_block")),
						TextureMapping.getBlockTexture(block("crossed_mud_wall_block")));
				provider = generators.new BlockFamilyProvider(textureMapping);
				ResourceLocation blockModel = ModelTemplates.CUBE_COLUMN.create(
						baseBlock,
						textureMapping,
						generators.modelOutput);
				ResourceLocation horizontalBlockModel = ModelTemplates.CUBE_COLUMN_HORIZONTAL.create(
						baseBlock,
						textureMapping,
						generators.modelOutput);
				BlockStateGenerator generator = BlockModelGenerators.createRotatedPillarWithHorizontalVariant(
						baseBlock,
						blockModel,
						horizontalBlockModel);
				generators.blockStateOutput.accept(generator);
				provider.fullBlock = blockModel;
			} else if (family == XKDBlockFamilies.CUT_BRONZE_BLOCK || family == XKDBlockFamilies.MAYA_POLISHED_STONEBRICKS) {
				// we have already generated the base model in other families
				TexturedModel texturedModel = generators.texturedModels.getOrDefault(baseBlock, TexturedModel.CUBE.get(baseBlock));
				provider = generators.new BlockFamilyProvider(texturedModel.getMapping());
				provider.fullBlock = ModelLocationUtils.getModelLocation(baseBlock);
			} else {
				provider = generators.family(baseBlock);
			}
			provider.generateFor(family);
		});
		createRoof("black_roof");
		for (String s : List.of("danger", "attention", "electricity", "toxic", "radiation", "biohazard")) {
			createRustingBlock("factory_" + s);
		}
		createTrivialCube("maya_quad_screw_thread_stone");
		createTrivialCube("maya_pictogram_stone");
		createTrivialCube("maya_skull_stone");
		createTrivialCube("aztec_sculpture_stone");
		createTrivialCube("framed_mud_wall_block");
		createTrivialCube("hollow_steel_block");
		createTrivialCube("framed_steel_block");
		createTrivialCube("steel_filings");
		createTrivialCube("quartz_sand");
		createTrivialCube("toughened_sand");

		createTrivialCube("ginkgo_leaves");
		createTrivialCube("orange_maple_leaves");
		createTrivialCube("red_maple_leaves");
		createTrivialCube("peach_blossom");
		createTrivialCube("peach_blossom_leaves");
		createTrivialCube("cherry_blossom");
		createTrivialCube("cherry_blossom_leaves");
		createTrivialCube("white_cherry_blossom");
		createTrivialCube("white_cherry_blossom_leaves");
		generators.createTrivialBlock(block("plantable_leaves"), TexturedModel.LEAVES);
		generators.createTrivialBlock(block("plantable_leaves_dark"), TexturedModel.LEAVES);
		generators.createTrivialBlock(block("willow_leaves"), TexturedModel.LEAVES);
		generators.createTrivialBlock(block("fallen_ginkgo_leaves"), XKDModelTemplates.FALLEN_LEAVES_PROVIDER);
		generators.createTrivialBlock(block("fallen_orange_maple_leaves"), XKDModelTemplates.FALLEN_LEAVES_PROVIDER);
		generators.createTrivialBlock(block("fallen_red_maple_leaves"), XKDModelTemplates.FALLEN_LEAVES_PROVIDER);
		generators.createTrivialBlock(block("fallen_peach_blossom"), XKDModelTemplates.FALLEN_LEAVES_PROVIDER);
		generators.createTrivialBlock(block("fallen_cherry_blossom"), XKDModelTemplates.FALLEN_LEAVES_PROVIDER);
		generators.createTrivialBlock(block("fallen_white_cherry_blossom"), XKDModelTemplates.FALLEN_LEAVES_PROVIDER);
		createSimpleBlockState("hanging_willow_leaves");
		generators.createSimpleFlatItemModel(block("hanging_willow_leaves"));
	}

	private void createSimpleBlockState(String id) {
		Block block = block(id);
		generators.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(block, ModelLocationUtils.getModelLocation(block)));
	}

	private void createRustingBlock(String id) {
		createTrivialCube(id);
		createTrivialCube(id + "_rusting");
		createTrivialCube(id + "_rusted");
	}

	private void createTrivialCube(String id) {
		generators.createTrivialCube(block(id));
	}

	private void createRoof(String id) {
		ResourceLocation roofTexture = TextureMapping.getBlockTexture(block(id));
		ResourceLocation ridgeTexture = TextureMapping.getBlockTexture(block(id), "_ridge");
		createRoofNormal(id, roofTexture, ridgeTexture);
		createRoofRidge(id + "_ridge", roofTexture, ridgeTexture);
		createRoofFlat(id + "_flat", roofTexture);
		createRoofEave(id + "_eave", roofTexture, ridgeTexture);
		createRoofEnd(id + "_end", roofTexture, ridgeTexture);
		createRoofTip(id + "_tip");
	}

	private void createRoofEnd(String id, ResourceLocation roofTexture, ResourceLocation ridgeTexture) {
		Block block = block(id);
		MultiVariantGenerator generator = MultiVariantGenerator.multiVariant(block)
				.with(PropertyDispatch.properties(RoofEndBlock.VARIANT, RoofEndBlock.SHAPE, RoofEndBlock.HALF)
						.generate((variant, shape, half) -> {
							String path = "template_roof_end";
							if (variant != RoofUtil.RoofVariant.NORMAL) {
								path += "_" + variant;
							}
							if (half == RoofUtil.RoofHalf.BASE) {
								path += "_top";
							}
							path += "_" + shape;
							ResourceLocation model = XKDModelTemplates.MAP.get(path).create(
									block,
									TextureMapping.particle(roofTexture)
											.put(XKDModelTemplates.SLOT_INNER, ROOF_INNER_TEXTURE)
											.put(XKDModelTemplates.SLOT_RIDGE, ridgeTexture),
									generators.modelOutput);
							return Variant.variant().with(VariantProperties.MODEL, model);
						}))
				.with(createHorizontalFacingDispatchAlt());
		generators.delegateItemModel(block, ModelLocationUtils.getModelLocation(block).withSuffix("_left"));
		generators.blockStateOutput.accept(generator);
	}

	private void createRoofEave(String id, ResourceLocation roofTexture, ResourceLocation ridgeTexture) {
		Block block = block(id);
		MultiVariantGenerator generator = MultiVariantGenerator.multiVariant(block)
				.with(PropertyDispatch.properties(RoofEaveBlock.SHAPE, RoofEaveBlock.HALF).generate((shape, half) -> {
					String path = "template_roof_eave";
					if (shape != RoofUtil.RoofEaveShape.STRAIGHT) {
						path += "_" + shape;
					}
					if (half == RoofUtil.RoofHalf.BASE) {
						path += "_top";
					}
					ResourceLocation model = XKDModelTemplates.MAP.get(path).create(
							block,
							TextureMapping.particle(roofTexture)
									.put(XKDModelTemplates.SLOT_INNER, ROOF_INNER_TEXTURE)
									.put(XKDModelTemplates.SLOT_RIDGE, ridgeTexture),
							generators.modelOutput);
					return Variant.variant().with(VariantProperties.MODEL, model);
				}))
				.with(createHorizontalFacingDispatchAlt());
		generators.blockStateOutput.accept(generator);
	}

	private void createRoofFlat(String id, ResourceLocation roofTexture) {
		Block block = block(id);
		ResourceLocation model0 = XKDModelTemplates.ROOF_FLAT.create(
				block,
				TextureMapping.particle(roofTexture).put(XKDModelTemplates.SLOT_INNER, ROOF_INNER_TEXTURE),
				generators.modelOutput);
		ResourceLocation model1 = XKDModelTemplates.ROOF_FLAT_TOP.create(
				block,
				TextureMapping.particle(roofTexture).put(XKDModelTemplates.SLOT_INNER, ROOF_INNER_TEXTURE),
				generators.modelOutput);
		MultiVariantGenerator generator = MultiVariantGenerator.multiVariant(block)
				.with(PropertyDispatch.property(RoofFlatBlock.AXIS)
						.select(Direction.Axis.Z, Variant.variant())
						.select(Direction.Axis.X, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)))
				.with(PropertyDispatch.property(RoofFlatBlock.HALF)
						.select(RoofUtil.RoofHalf.TIP, Variant.variant().with(VariantProperties.MODEL, model0))
						.select(RoofUtil.RoofHalf.BASE, Variant.variant().with(VariantProperties.MODEL, model1)));
		generators.blockStateOutput.accept(generator);
	}

	private void createRoofRidge(String id, ResourceLocation roofTexture, ResourceLocation ridgeTexture) {
		Block block = block(id);
		List<ResourceLocation> models = Stream.of(
				XKDModelTemplates.ROOF_RIDGE,
				XKDModelTemplates.ROOF_RIDGE_CORNER,
				XKDModelTemplates.ROOF_RIDGE_POST,
				XKDModelTemplates.ROOF_RIDGE_SIDE_TALL,
				XKDModelTemplates.ROOF_RIDGE_INVENTORY).map(template -> template.create(
				block,
				TextureMapping.particle(roofTexture)
						.put(XKDModelTemplates.SLOT_INNER, ROOF_INNER_TEXTURE)
						.put(XKDModelTemplates.SLOT_RIDGE, ridgeTexture),
				generators.modelOutput)).toList();
		ResourceLocation normalModel = models.get(0);
		ResourceLocation cornerModel = models.get(1);
		ResourceLocation postModel = models.get(2);
		ResourceLocation sideModel = models.get(3);
		generators.delegateItemModel(block, models.get(4));
		MultiPartGenerator generator = MultiPartGenerator.multiPart(block)
				.with(
						Condition.condition()
								.term(BlockStateProperties.NORTH_WALL, WallSide.LOW)
								.term(BlockStateProperties.EAST_WALL, WallSide.LOW)
								.term(BlockStateProperties.SOUTH_WALL, WallSide.LOW)
								.term(BlockStateProperties.WEST_WALL, WallSide.LOW),
						Variant.variant().with(VariantProperties.MODEL, postModel))
				.with(
						Condition.condition()
								.term(BlockStateProperties.WEST_WALL, WallSide.NONE)
								.term(BlockStateProperties.NORTH_WALL, WallSide.NONE),
						Variant.variant().with(VariantProperties.MODEL, cornerModel))
				.with(
						Condition.condition()
								.term(BlockStateProperties.WEST_WALL, WallSide.NONE)
								.term(BlockStateProperties.SOUTH_WALL, WallSide.NONE),
						Variant.variant()
								.with(VariantProperties.MODEL, cornerModel)
								.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))
				.with(
						Condition.condition()
								.term(BlockStateProperties.EAST_WALL, WallSide.NONE)
								.term(BlockStateProperties.SOUTH_WALL, WallSide.NONE),
						Variant.variant()
								.with(VariantProperties.MODEL, cornerModel)
								.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180))
				.with(
						Condition.condition()
								.term(BlockStateProperties.EAST_WALL, WallSide.NONE)
								.term(BlockStateProperties.NORTH_WALL, WallSide.NONE),
						Variant.variant()
								.with(VariantProperties.MODEL, cornerModel)
								.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
				.with(
						Condition.condition().term(BlockStateProperties.UP, true),
						Variant.variant().with(VariantProperties.MODEL, postModel))
				.with(
						Condition.condition().term(BlockStateProperties.NORTH_WALL, WallSide.LOW),
						Variant.variant()
								.with(VariantProperties.MODEL, normalModel))
				.with(
						Condition.condition().term(BlockStateProperties.EAST_WALL, WallSide.LOW),
						Variant.variant()
								.with(VariantProperties.MODEL, normalModel)
								.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
				.with(
						Condition.condition().term(BlockStateProperties.SOUTH_WALL, WallSide.LOW),
						Variant.variant()
								.with(VariantProperties.MODEL, normalModel)
								.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180))
				.with(
						Condition.condition().term(BlockStateProperties.WEST_WALL, WallSide.LOW),
						Variant.variant()
								.with(VariantProperties.MODEL, normalModel)
								.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))
				.with(
						Condition.condition().term(BlockStateProperties.NORTH_WALL, WallSide.TALL),
						Variant.variant()
								.with(VariantProperties.MODEL, sideModel))
				.with(
						Condition.condition().term(BlockStateProperties.EAST_WALL, WallSide.TALL),
						Variant.variant()
								.with(VariantProperties.MODEL, sideModel)
								.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
				.with(
						Condition.condition().term(BlockStateProperties.SOUTH_WALL, WallSide.TALL),
						Variant.variant()
								.with(VariantProperties.MODEL, sideModel)
								.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180))
				.with(
						Condition.condition().term(BlockStateProperties.WEST_WALL, WallSide.TALL),
						Variant.variant()
								.with(VariantProperties.MODEL, sideModel)
								.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270));
		generators.blockStateOutput.accept(generator);
	}

	private void createRoofNormal(String id, ResourceLocation roofTexture, ResourceLocation ridgeTexture) {
		Block block = block(id);
		MultiVariantGenerator generator = MultiVariantGenerator.multiVariant(block)
				.with(PropertyDispatch.properties(RoofBlock.VARIANT, RoofBlock.SHAPE, RoofBlock.HALF).generate((variant, shape, half) -> {
					String path = "template_roof";
					if (variant != RoofUtil.RoofVariant.NORMAL) {
						path += "_" + variant;
					}
					if (shape != RoofUtil.RoofShape.STRAIGHT) {
						path += "_" + shape;
					}
					if (half == RoofUtil.RoofHalf.BASE) {
						path += "_top";
					}
					ResourceLocation model = XKDModelTemplates.MAP.get(path).create(
							block,
							TextureMapping.particle(roofTexture)
									.put(XKDModelTemplates.SLOT_INNER, ROOF_INNER_TEXTURE)
									.put(XKDModelTemplates.SLOT_RIDGE, ridgeTexture),
							generators.modelOutput);
					return Variant.variant().with(VariantProperties.MODEL, model);
				}))
				.with(createHorizontalFacingDispatchAlt());
		generators.blockStateOutput.accept(generator);
	}

	public static PropertyDispatch createHorizontalFacingDispatchAlt() {
		return PropertyDispatch.property(BlockStateProperties.HORIZONTAL_FACING)
				.select(Direction.EAST, Variant.variant())
				.select(Direction.SOUTH, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
				.select(Direction.WEST, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180))
				.select(Direction.NORTH, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270));
	}

	private void createRoofTip(String id) {
		Block block = block(id);
		ResourceLocation model0 = XKDModelTemplates.ROOF_TIP.create(
				block,
				TextureMapping.particle(block),
				generators.modelOutput);
		ResourceLocation model1 = XKDModelTemplates.ROOF_TIP_TOP.create(
				block,
				TextureMapping.particle(block),
				generators.modelOutput);
		MultiVariantGenerator generator = MultiVariantGenerator.multiVariant(block)
				.with(PropertyDispatch.property(RoofTipBlock.HALF)
						.select(RoofUtil.RoofHalf.BASE, Variant.variant().with(VariantProperties.MODEL, model0))
						.select(RoofUtil.RoofHalf.TIP, Variant.variant().with(VariantProperties.MODEL, model1)));
		generators.blockStateOutput.accept(generator);
	}

	@Override
	public void generateItemModels(ItemModelGenerators generators) {
	}

	private static Block block(String id) {
		ResourceLocation resourceLocation = new ResourceLocation(XKDeco.ID, id);
		return BuiltInRegistries.BLOCK.getOptional(resourceLocation).orElseThrow(() -> {
			return new IllegalStateException("Missing block: " + resourceLocation);
		});
	}
}
