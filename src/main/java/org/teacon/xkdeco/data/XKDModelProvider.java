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
			block("chiseled_steel_block"));

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

	@Override
	public void generateBlockStateModels(BlockModelGenerators generators) {
		XKDBlockFamilies.getAllFamilies().filter(BlockFamily::shouldGenerateModel).forEach(family -> {
			LOGGER.info("Generating models for block family {}", family.getBaseBlock());
			BlockModelGenerators.BlockFamilyProvider provider;
			if (family == XKDBlockFamilies.LINED_MUD_WALL) {
				TextureMapping textureMapping = TextureMapping.column(
						TextureMapping.getBlockTexture(block("lined_mud_wall_block")),
						TextureMapping.getBlockTexture(block("crossed_mud_wall_block")));
				provider = generators.new BlockFamilyProvider(textureMapping);
				ResourceLocation blockModel = ModelTemplates.CUBE_COLUMN.create(
						family.getBaseBlock(),
						textureMapping,
						generators.modelOutput);
				ResourceLocation horizontalBlockModel = ModelTemplates.CUBE_COLUMN_HORIZONTAL.create(
						family.getBaseBlock(),
						textureMapping,
						generators.modelOutput);
				BlockStateGenerator generator = BlockModelGenerators.createRotatedPillarWithHorizontalVariant(
						family.getBaseBlock(),
						blockModel,
						horizontalBlockModel);
				generators.blockStateOutput.accept(generator);
				provider.fullBlock = blockModel;
			} else {
				provider = generators.family(family.getBaseBlock());
			}
			provider.generateFor(family);
		});
		createRoof(generators, "black_roof");
		generators.createTrivialCube(block("framed_mud_wall_block"));
	}

	private static void createLinedMudWallBlock(BlockModelGenerators generators) {
//		TextureMapping textureMapping = TextureMapping.column(
//				TextureMapping.getBlockTexture(block("lined_mud_wall_block")),
//				TextureMapping.getBlockTexture(block("crossed_mud_wall_block")));
//		ResourceLocation blockModel = ModelTemplates.CUBE_COLUMN.create(
//				block("lined_mud_wall_block"),
//				textureMapping,
//				generators.modelOutput);
//		ResourceLocation horizontalBlockModel = ModelTemplates.CUBE_COLUMN_HORIZONTAL.create(
//				block("lined_mud_wall_block"),
//				textureMapping,
//				generators.modelOutput);
//		BlockModelGenerators.createRotatedPillarWithHorizontalVariant(block("lined_mud_wall_block"), blockModel, horizontalBlockModel);
//		ResourceLocation bottomSlabModel = ModelTemplates.SLAB_BOTTOM.create(
//				block("mud_wall_block_slab"),
//				textureMapping,
//				generators.modelOutput);
//		ResourceLocation topSlabModel = ModelTemplates.SLAB_TOP.create(
//				block("mud_wall_block_slab"),
//				textureMapping,
//				generators.modelOutput);
//		BlockModelGenerators.createSlab(block("lined_mud_wall_block_slab"), bottomSlabModel, topSlabModel, blockModel);
	}

	private static void createRoof(BlockModelGenerators generators, String id) {
		ResourceLocation roofTexture = TextureMapping.getBlockTexture(block(id));
		ResourceLocation ridgeTexture = TextureMapping.getBlockTexture(block(id), "_ridge");
		createRoofNormal(generators, id, roofTexture, ridgeTexture);
		createRoofRidge(generators, id + "_ridge", roofTexture, ridgeTexture);
		createRoofFlat(generators, id + "_flat", roofTexture);
		createRoofEave(generators, id + "_eave", roofTexture, ridgeTexture);
		createRoofEnd(generators, id + "_end", roofTexture, ridgeTexture);
		createRoofTip(generators, id + "_tip");
	}

	private static void createRoofEnd(
			BlockModelGenerators generators,
			String id,
			ResourceLocation roofTexture,
			ResourceLocation ridgeTexture) {
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

	private static void createRoofEave(
			BlockModelGenerators generators,
			String id,
			ResourceLocation roofTexture,
			ResourceLocation ridgeTexture) {
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

	private static void createRoofFlat(BlockModelGenerators generators, String id, ResourceLocation roofTexture) {
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

	private static void createRoofRidge(
			BlockModelGenerators generators,
			String id,
			ResourceLocation roofTexture,
			ResourceLocation ridgeTexture) {
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

	private static void createRoofNormal(
			BlockModelGenerators generators,
			String id,
			ResourceLocation roofTexture,
			ResourceLocation ridgeTexture) {
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

	private static void createRoofTip(BlockModelGenerators generators, String id) {
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
