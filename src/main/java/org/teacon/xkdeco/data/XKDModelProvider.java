package org.teacon.xkdeco.data;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.teacon.xkdeco.XKDeco;
import org.teacon.xkdeco.block.BasicBlock;
import org.teacon.xkdeco.block.RoofBlock;
import org.teacon.xkdeco.block.RoofEaveBlock;
import org.teacon.xkdeco.block.RoofEndBlock;
import org.teacon.xkdeco.block.RoofFlatBlock;
import org.teacon.xkdeco.block.RoofRidgeEndAsianBlock;
import org.teacon.xkdeco.block.RoofTipBlock;
import org.teacon.xkdeco.block.settings.HorizontalComponent;
import org.teacon.xkdeco.block.settings.XKBlockSettings;
import org.teacon.xkdeco.init.XKDecoProperties;
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
import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.data.models.model.TexturedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.WallSide;
import net.minecraftforge.registries.RegistryObject;

public class XKDModelProvider extends FabricModelProvider {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final ResourceLocation ROOF_INNER_TEXTURE = XKDeco.id("block/roof_inner");
	private static final Set<Block> ROTATED_PILLARS = Set.of(
			block("chiseled_bronze_block"),
			block("chiseled_steel_block"),
			block("maya_chiseled_stonebricks"));
	private static final Set<Block> SPECIAL_DOUBLE_SLABS = Set.of(
			block("polished_sandstone_slab"),
			block("polished_red_sandstone_slab"),
			block("maya_polished_stonebrick_slab"));
	private static final Set<String> SKIPPED_MODELS = Set.of(
			"block/dirt_path_slab",
			"block/dirt_path_slab_top",
			"block/grass_block_slab",
			"block/grass_block_slab_top",
			"block/air_duct_oblique",
			"block/air_duct_oblique_top");
	private static final Set<Block> THIN_TRAPDOORS = Set.of(
			block("varnished_trapdoor"),
			block("ebony_trapdoor"),
			block("mahogany_trapdoor"));
	private static final Set<Block> WOODEN_FENCE_GATES = Set.of(
			block("varnished_fence_gate"),
			block("ebony_fence_gate"),
			block("mahogany_fence_gate"));
	private static final List<String> GADGET_SKIP_PREFIXES = List.of(
			"varnished_",
			"ebony_",
			"mahogany_");
	private static final Set<Block> GADGET_SKIP_BLOCKS = Set.of(
			block("bottle_stack"),
			block("empty_bottle_stack"));
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

	public static boolean createIfSpecialDoubleSlabs(Block block, BlockModelGenerators generators) {
		if (!SPECIAL_DOUBLE_SLABS.contains(block)) {
			return false;
		}
		ResourceLocation texture = TextureMapping.getBlockTexture(block);
		TextureMapping mapping = TextureMapping.column(texture, texture.withPath(s -> s.substring(0, s.length() - 5)));
		ResourceLocation $$2 = ModelTemplates.SLAB_BOTTOM.create(block, mapping, generators.modelOutput);
		ResourceLocation $$3 = ModelTemplates.SLAB_TOP.create(block, mapping, generators.modelOutput);
		ResourceLocation $$4 = ModelTemplates.CUBE_COLUMN.createWithOverride(block, "_full", mapping, generators.modelOutput);
		generators.blockStateOutput.accept(BlockModelGenerators.createSlab(block, $$2, $$3, $$4));
		return true;
	}

	public static boolean createIfSpecialTrapdoor(Block block, BlockModelGenerators generators) {
		if (!THIN_TRAPDOORS.contains(block)) {
			return false;
		}
		TextureMapping $$1 = TextureMapping.defaultTexture(block);
		ResourceLocation $$2 = XKDModelTemplates.THIN_TRAPDOOR_TOP.create(block, $$1, generators.modelOutput);
		ResourceLocation $$3 = XKDModelTemplates.THIN_TRAPDOOR_BOTTOM.create(block, $$1, generators.modelOutput);
		ResourceLocation $$4 = XKDModelTemplates.THIN_TRAPDOOR_OPEN.create(block, $$1, generators.modelOutput);
		generators.blockStateOutput.accept(BlockModelGenerators.createOrientableTrapdoor(block, $$2, $$3, $$4));
		generators.delegateItemModel(block, $$3);
		return true;
	}

	public static boolean createIfSpecialFenceGate(Block block, BlockModelGenerators generators) {
		if (!WOODEN_FENCE_GATES.contains(block)) {
			return false;
		}
		TextureMapping textureMapping = TextureMapping.defaultTexture(block);
		ResourceLocation $$1 = XKDModelTemplates.WOODEN_FENCE_GATE_OPEN.create(block, textureMapping, generators.modelOutput);
		ResourceLocation $$2 = XKDModelTemplates.WOODEN_FENCE_GATE_CLOSED.create(block, textureMapping, generators.modelOutput);
		generators.blockStateOutput.accept(BlockModelGenerators.createFenceGate(block, $$1, $$2, $$1, $$2, true));
		return true;
	}

	public static boolean createIfSpecialStairs(Block block, TextureMapping mapping, BlockModelGenerators generators) {
		XKBlockSettings settings = XKBlockSettings.of(block);
		if (settings == null || settings.glassType == null) {
			return false;
		}
		mapping = mapping.copyAndUpdate(TextureSlot.SIDE, mapping.get(TextureSlot.ALL));
		ResourceLocation straightModel = XKDModelTemplates.GLASS_STAIRS.create(block, mapping, generators.modelOutput);
		ResourceLocation innerModel = XKDModelTemplates.GLASS_STAIRS_INNER.create(block, mapping, generators.modelOutput);
		ResourceLocation outerModel = XKDModelTemplates.GLASS_STAIRS_OUTER.create(block, mapping, generators.modelOutput);
		generators.blockStateOutput.accept(BlockModelGenerators.createStairs(block, innerModel, straightModel, outerModel));
		return true;
	}

	@Override
	public void generateBlockStateModels(BlockModelGenerators generators) {
		this.generators = generators;
		var originalModelOutput = generators.modelOutput;
		generators.modelOutput = (modelLocation, json) -> {
			if (!SKIPPED_MODELS.contains(modelLocation.getPath())) {
				originalModelOutput.accept(modelLocation, json);
			}
		};
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

		createRoof("black_roof", true);
		createRoof("cyan_roof", true);
		createRoof("yellow_roof", true);
		createRoof("blue_roof", false);
		createRoof("green_roof", false);
		createRoof("red_roof", false);

		for (String s : List.of("danger", "attention", "electricity", "toxic", "radiation", "biohazard")) {
			createRustingBlock("factory_" + s);
		}
		createTrivialCube("maya_quad_screw_thread_stone");
		createTrivialCube("maya_pictogram_stone");
		createTrivialCube("maya_skull_stone");
		createTrivialCube("aztec_sculpture_stone");
		createTrivialCube("framed_mud_wall_block");
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
		createBlockStateOnly("hanging_willow_leaves", false);
		generators.createSimpleFlatItemModel(block("hanging_willow_leaves"));

		ResourceLocation dirtTexture = TextureMapping.getBlockTexture(Blocks.DIRT);
		ResourceLocation netherrackTexture = TextureMapping.getBlockTexture(Blocks.NETHERRACK);
		createSlab(Blocks.DIRT, false, false, UnaryOperator.identity());
		createSlab(Blocks.DIRT_PATH, true, true, $ -> $.put(TextureSlot.BOTTOM, dirtTexture));
		createSlab(Blocks.GRASS_BLOCK, true, true, UnaryOperator.identity());
		createSlab(Blocks.MYCELIUM, true, true, $ -> $
				.put(TextureSlot.BOTTOM, dirtTexture)
				.put(TextureSlot.TOP, TextureMapping.getBlockTexture(Blocks.MYCELIUM, "_top")));
		createSlab(Blocks.NETHERRACK, false, false, UnaryOperator.identity());
		createSlab(Blocks.PODZOL, true, true, $ -> $
				.put(TextureSlot.BOTTOM, dirtTexture)
				.put(TextureSlot.TOP, TextureMapping.getBlockTexture(Blocks.PODZOL, "_top")));
		createSlab(Blocks.CRIMSON_NYLIUM, true, true, $ -> $.put(TextureSlot.BOTTOM, netherrackTexture));
		createSlab(Blocks.WARPED_NYLIUM, true, true, $ -> $.put(TextureSlot.BOTTOM, netherrackTexture));
		createSlab(Blocks.END_STONE, false, false, UnaryOperator.identity());

		createTreatedWood("varnished");
		createTreatedWood("ebony");
		createTreatedWood("mahogany");
		createBlockStateOnly("air_duct", false);
		generators.delegateItemModel(block("air_duct"), XKDeco.id("block/furniture/air_duct_corner"));
		createHorizontalShift("air_duct_oblique", "air_duct_oblique", null, false);
		generators.blockStateOutput.accept(BlockModelGenerators.createWall(
				block("hollow_steel_beam"),
				XKDeco.id("block/furniture/hollow_steel_beam_post"),
				XKDeco.id("block/furniture/hollow_steel_beam_side"),
				XKDeco.id("block/furniture/hollow_steel_beam_side_tall")));
		generators.delegateItemModel(block("hollow_steel_beam"), XKDeco.id("block/furniture/hollow_steel_beam_inventory"));
		createBlockStateOnly("steel_safety_ladder", true);
		createBlockStateOnly("steel_ladder", false);
		ModelTemplates.FLAT_ITEM.create(
				ModelLocationUtils.getModelLocation(block("steel_ladder").asItem()),
				TextureMapping.layer0(TextureMapping.getBlockTexture(block("steel_safety_ladder"), "_side")),
				generators.modelOutput);
		generators.blockStateOutput.accept(createFaceAttached("hollow_steel_half_beam"));

		outer:
		for (Item item : XKDecoProperties.TAB_FURNITURE_CONTENTS.stream().map(RegistryObject::get).toList()) {
			Block block = Block.byItem(item);
			if (block == Blocks.AIR || GADGET_SKIP_BLOCKS.contains(block)) {
				continue;
			}
			var id = BuiltInRegistries.BLOCK.getKey(block);
			for (String prefix : GADGET_SKIP_PREFIXES) {
				if (id.getPath().startsWith(prefix)) {
					continue outer;
				}
			}
			createGadget(block);
		}
	}

	private BlockStateGenerator createFaceAttached(String id) {
		Block block = block(id);
		return MultiVariantGenerator.multiVariant(
						block,
						Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(block)))
				.with(PropertyDispatch.properties(
								BlockStateProperties.ATTACH_FACE,
								BlockStateProperties.HORIZONTAL_FACING)
						.select(AttachFace.FLOOR, Direction.EAST, Variant.variant()
								.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
						.select(AttachFace.FLOOR, Direction.WEST, Variant.variant()
								.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))
						.select(AttachFace.FLOOR, Direction.SOUTH, Variant.variant()
								.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180))
						.select(AttachFace.FLOOR, Direction.NORTH, Variant.variant())
						.select(AttachFace.WALL, Direction.EAST, Variant.variant()
								.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
								.with(VariantProperties.X_ROT, VariantProperties.Rotation.R90))
						.select(AttachFace.WALL, Direction.WEST, Variant.variant()
								.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
								.with(VariantProperties.X_ROT, VariantProperties.Rotation.R90))
						.select(AttachFace.WALL, Direction.SOUTH, Variant.variant()
								.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
								.with(VariantProperties.X_ROT, VariantProperties.Rotation.R90))
						.select(AttachFace.WALL, Direction.NORTH, Variant.variant()
								.with(VariantProperties.X_ROT, VariantProperties.Rotation.R90))
						.select(AttachFace.CEILING, Direction.EAST, Variant.variant()
								.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
								.with(VariantProperties.X_ROT, VariantProperties.Rotation.R180))
						.select(AttachFace.CEILING, Direction.WEST, Variant.variant()
								.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
								.with(VariantProperties.X_ROT, VariantProperties.Rotation.R180))
						.select(AttachFace.CEILING, Direction.SOUTH, Variant.variant()
								.with(VariantProperties.X_ROT, VariantProperties.Rotation.R180))
						.select(AttachFace.CEILING, Direction.NORTH, Variant.variant()
								.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
								.with(VariantProperties.X_ROT, VariantProperties.Rotation.R180)));
	}

	private void createTreatedWood(String id) {
		Block log = block(id + "_log");
		TextureMapping logMapping = generators.woodProvider(log).log(log).wood(block(id + "_wood")).logMapping;
		Block slab = block(id + "_log_slab");
		ResourceLocation $$2 = ModelTemplates.SLAB_BOTTOM.create(slab, logMapping, generators.modelOutput);
		ResourceLocation $$3 = ModelTemplates.SLAB_TOP.create(slab, logMapping, generators.modelOutput);
		generators.blockStateOutput.accept(BlockModelGenerators.createSlab(slab, $$2, $$3, ModelLocationUtils.getModelLocation(log)));

		createTrivialBlock(id + "_table", XKDModelTemplates.WOODEN_TABLE_PROVIDER);
//		createTrivialBlock(id + "_big_table", XKDModelTemplates.BIG_TABLE_PROVIDER);
//		createTrivialBlock(id + "_tall_table", XKDModelTemplates.TALL_TABLE_PROVIDER);
		createHorizontallyRotatedBlock(id + "_desk", XKDModelTemplates.WOODEN_DESK_PROVIDER);

		TextureMapping textureMapping = logMapping.copyAndUpdate(TextureSlot.WALL, XKDeco.id("block/" + id + "_smooth"));
		createWoodenWall(id + "_column_wall", "wooden_column_wall", textureMapping);
		createWoodenWall("hollow_" + id + "_column_wall", "hollow_wooden_column_wall", textureMapping);
	}

	private void createWoodenWall(String id, String templateId, TextureMapping textureMapping) {
		Block block = block(id);
		ResourceLocation side = XKDModelTemplates.MAP.get(templateId + "_side").create(block, textureMapping, generators.modelOutput);
		ResourceLocation post = XKDModelTemplates.MAP.get(templateId + "_post").create(block, textureMapping, generators.modelOutput);
		ResourceLocation tallSide = XKDModelTemplates.MAP.get(templateId + "_side_tall").create(
				block,
				textureMapping,
				generators.modelOutput);
		ResourceLocation inventory = XKDModelTemplates.MAP.get(templateId + "_inventory").create(
				block,
				textureMapping,
				generators.modelOutput);
		generators.blockStateOutput.accept(BlockModelGenerators.createWall(block, post, side, tallSide));
		generators.delegateItemModel(block, inventory);
	}

	private void createTrivialBlock(String id, TexturedModel.Provider provider) {
		Block block = block(id);
		ResourceLocation model = provider.create(block, generators.modelOutput);
		generators.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(block, model));
	}

	private void createHorizontallyRotatedBlock(String id, TexturedModel.Provider provider) {
		Block block = block(id);
		ResourceLocation model = provider.create(block, generators.modelOutput);
		generators.blockStateOutput.accept(MultiVariantGenerator.multiVariant(
				block,
				Variant.variant().with(VariantProperties.MODEL, model)).with(BlockModelGenerators.createHorizontalFacingDispatch()));
	}

	private void createGadget(Block block) {
		var id = BuiltInRegistries.BLOCK.getKey(block);
		XKBlockSettings settings = XKBlockSettings.of(block);
		if (settings == null) {
			return;
		}
		if (block instanceof BasicBlock && settings.hasComponent(HorizontalComponent.TYPE)) {
			ResourceLocation model = id.withPrefix("block/furniture/");
			generators.blockStateOutput.accept(MultiVariantGenerator.multiVariant(
							block,
							Variant.variant().with(VariantProperties.MODEL, model))
					.with(BlockModelGenerators.createHorizontalFacingDispatch()));
			generators.delegateItemModel(block, model);
		}
	}

	private void createSlab(Block fullBlock, boolean sided, boolean natural, UnaryOperator<TextureMapping> textureMappingOperator) {
		ResourceLocation id = BuiltInRegistries.BLOCK.getKey(fullBlock);
		Block slab = block(id.getPath() + "_slab");
		TextureMapping textureMapping = TextureMapping.cube(fullBlock);
		if (sided) {
			textureMapping.put(TextureSlot.SIDE, TextureMapping.getBlockTexture(fullBlock, "_side"));
		}
		textureMapping = textureMappingOperator.apply(textureMapping);
		ModelTemplate bottomTemplate = natural ? XKDModelTemplates.NATURAL_SLAB : ModelTemplates.SLAB_BOTTOM;
		ResourceLocation bottomModel = bottomTemplate.create(slab, textureMapping, generators.modelOutput);
		ResourceLocation topModel = ModelTemplates.SLAB_TOP.create(slab, textureMapping, generators.modelOutput);
		ResourceLocation fullModel = ModelLocationUtils.getModelLocation(fullBlock);
		generators.blockStateOutput.accept(BlockModelGenerators.createSlab(slab, bottomModel, topModel, fullModel));
	}

	private void createBlockStateOnly(String id, boolean delegateItem) {
		Block block = block(id);
		XKBlockSettings settings = XKBlockSettings.of(block);
		ResourceLocation modelLocation = ModelLocationUtils.getModelLocation(block);
		if (settings != null && settings.hasComponent(HorizontalComponent.TYPE)) {
			generators.blockStateOutput.accept(MultiVariantGenerator.multiVariant(
							block,
							Variant.variant().with(VariantProperties.MODEL, modelLocation))
					.with(BlockModelGenerators.createHorizontalFacingDispatch()));
		} else {
			generators.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(block, modelLocation));
		}
		if (delegateItem) {
			generators.delegateItemModel(block, modelLocation);
		}
	}

	private void createRustingBlock(String id) {
		createTrivialCube(id);
		createTrivialCube(id + "_rusting");
		createTrivialCube(id + "_rusted");
	}

	private void createTrivialCube(String id) {
		generators.createTrivialCube(block(id));
	}

	private void createRoof(String id, boolean asian) {
		ResourceLocation roofTexture = TextureMapping.getBlockTexture(block(id));
		ResourceLocation ridgeTexture = TextureMapping.getBlockTexture(block(id), "_ridge");
		ResourceLocation smallRidgeTexture = TextureMapping.getBlockTexture(block(id), "_small_ridge");
		createRoofNormal(id, roofTexture, ridgeTexture);
		createRoofRidge(id + "_ridge", roofTexture, ridgeTexture, asian);
		createRoofFlat(id + "_flat", roofTexture);
		createRoofEave(id + "_small_eave", roofTexture, ridgeTexture, true);
		createRoofEnd(id + "_small_end", roofTexture, asian ? smallRidgeTexture : ridgeTexture, true);
		if (asian) {
			createAsianRoofRidgeEnd(id + "_small_ridge_end", roofTexture, ridgeTexture, smallRidgeTexture, true);
		} else {
			createHorizontalShift(
					id + "_small_ridge_end",
					"template_roof_small_ridge_end",
					$ -> TextureMapping.particle(roofTexture).put(XKDModelTemplates.SLOT_RIDGE2, ridgeTexture), true);
		}
		createHorizontalShift(
				id + "_small_flat_end",
				"template_roof_small_flat_end",
				$ -> TextureMapping.particle(roofTexture).put(XKDModelTemplates.SLOT_RIDGE, asian ? smallRidgeTexture : ridgeTexture),
				true);
		if (!asian) {
			return;
		}
		createRoofEave(id + "_eave", roofTexture, ridgeTexture, false);
		createRoofEnd(id + "_end", roofTexture, ridgeTexture, false);
		createAsianRoofRidgeEnd(id + "_ridge_end", roofTexture, ridgeTexture, smallRidgeTexture, false);
		createHorizontalShift(
				id + "_deco",
				"template_roof_deco",
				$ -> TextureMapping.cube($).put(XKDModelTemplates.SLOT_RIDGE, ridgeTexture), true);
		createHorizontalShift(id + "_deco_oblique", "template_roof_deco_oblique", null, true);
		createRoofTip(id + "_tip");
	}

	private void createHorizontalShift(
			String id,
			String templateId,
			@Nullable Function<Block, TextureMapping> textureMappingFactory,
			boolean altRotation) {
		Block block = block(id);
		TextureMapping textureMapping;
		if (textureMappingFactory == null) {
			textureMapping = TextureMapping.cube(block);
		} else {
			textureMapping = textureMappingFactory.apply(block);
		}
		ResourceLocation model0 = XKDModelTemplates.MAP.get(templateId).create(
				block,
				textureMapping,
				generators.modelOutput);
		ResourceLocation model1 = XKDModelTemplates.MAP.get(templateId + "_top").create(
				block,
				textureMapping,
				generators.modelOutput);
		MultiVariantGenerator generator = MultiVariantGenerator.multiVariant(block)
				.with(altRotation ? createHorizontalFacingDispatchAlt() : BlockModelGenerators.createHorizontalFacingDispatch())
				.with(PropertyDispatch.property(RoofTipBlock.HALF)
						.select(RoofUtil.RoofHalf.TIP, Variant.variant().with(VariantProperties.MODEL, model0))
						.select(RoofUtil.RoofHalf.BASE, Variant.variant().with(VariantProperties.MODEL, model1)));
		generators.blockStateOutput.accept(generator);
	}

	private void createAsianRoofRidgeEnd(
			String id,
			ResourceLocation roofTexture,
			ResourceLocation ridgeTexture,
			ResourceLocation smallRidgeTexture,
			boolean narrow) {
		Block block = block(id);
		MultiVariantGenerator generator = MultiVariantGenerator.multiVariant(block)
				.with(PropertyDispatch.properties(RoofRidgeEndAsianBlock.VARIANT, RoofRidgeEndAsianBlock.HALF)
						.generate((variant, half) -> {
							String path = narrow ? "template_roof_small_ridge_end_asian" : "template_roof_ridge_end";
							TextureMapping textureMapping = TextureMapping.particle(roofTexture);
							if (narrow) {
								textureMapping.put(XKDModelTemplates.SLOT_RIDGE, smallRidgeTexture);
								textureMapping.put(XKDModelTemplates.SLOT_RIDGE2, ridgeTexture);
							} else {
								textureMapping.put(XKDModelTemplates.SLOT_INNER, ROOF_INNER_TEXTURE);
								textureMapping.put(XKDModelTemplates.SLOT_RIDGE, ridgeTexture);
							}
							if (variant != RoofUtil.RoofVariant.NORMAL) {
								path += "_" + variant;
							}
							if (half == RoofUtil.RoofHalf.BASE) {
								path += "_top";
							}
							ResourceLocation model = XKDModelTemplates.MAP.get(path).create(
									block,
									textureMapping,
									generators.modelOutput);
							return Variant.variant().with(VariantProperties.MODEL, model);
						}))
				.with(createHorizontalFacingDispatchAlt());
		generators.blockStateOutput.accept(generator);
	}

	private void createRoofEnd(String id, ResourceLocation roofTexture, ResourceLocation ridgeTexture, boolean narrow) {
		Block block = block(id);
		MultiVariantGenerator generator = MultiVariantGenerator.multiVariant(block)
				.with(PropertyDispatch.properties(RoofEndBlock.VARIANT, RoofEndBlock.SHAPE, RoofEndBlock.HALF)
						.generate((variant, shape, half) -> {
							String path = narrow ? "template_roof_small_end" : "template_roof_end";
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

	private void createRoofEave(String id, ResourceLocation roofTexture, ResourceLocation ridgeTexture, boolean narrow) {
		Block block = block(id);
		MultiVariantGenerator generator = MultiVariantGenerator.multiVariant(block)
				.with(PropertyDispatch.properties(RoofEaveBlock.SHAPE, RoofEaveBlock.HALF).generate((shape, half) -> {
					String path = narrow ? "template_roof_small_eave" : "template_roof_eave";
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
		ResourceLocation model0 = XKDModelTemplates.MAP.get("template_roof_flat").create(
				block,
				TextureMapping.particle(roofTexture).put(XKDModelTemplates.SLOT_INNER, ROOF_INNER_TEXTURE),
				generators.modelOutput);
		ResourceLocation model1 = XKDModelTemplates.MAP.get("template_roof_flat_top").create(
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

	private void createRoofRidge(String id, ResourceLocation roofTexture, ResourceLocation ridgeTexture, boolean asian) {
		Block block = block(id);
		Stream<ModelTemplate> templateStream;
		if (asian) {
			templateStream = Stream.of(
					XKDModelTemplates.ROOF_RIDGE_ASIAN,
					XKDModelTemplates.ROOF_RIDGE_CORNER,
					XKDModelTemplates.ROOF_RIDGE_ASIAN_POST,
					XKDModelTemplates.ROOF_RIDGE_ASIAN_INVENTORY,
					XKDModelTemplates.ROOF_RIDGE_ASIAN_STEEP);
		} else {
			templateStream = Stream.of(
					XKDModelTemplates.ROOF_RIDGE,
					XKDModelTemplates.ROOF_RIDGE_CORNER,
					XKDModelTemplates.ROOF_RIDGE_POST,
					XKDModelTemplates.ROOF_RIDGE_INVENTORY);
		}
		List<ResourceLocation> models = templateStream.map(template -> template.create(
				block,
				TextureMapping.particle(roofTexture)
						.put(XKDModelTemplates.SLOT_INNER, ROOF_INNER_TEXTURE)
						.put(XKDModelTemplates.SLOT_RIDGE, ridgeTexture),
				generators.modelOutput)).toList();
		ResourceLocation normalModel = models.get(0);
		ResourceLocation cornerModel = models.get(1);
		ResourceLocation postModel = models.get(2);
		ResourceLocation steepModel = asian ? models.get(4) : normalModel;
		generators.delegateItemModel(block, models.get(3));
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
								.with(VariantProperties.MODEL, steepModel))
				.with(
						Condition.condition().term(BlockStateProperties.EAST_WALL, WallSide.TALL),
						Variant.variant()
								.with(VariantProperties.MODEL, steepModel)
								.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
				.with(
						Condition.condition().term(BlockStateProperties.SOUTH_WALL, WallSide.TALL),
						Variant.variant()
								.with(VariantProperties.MODEL, steepModel)
								.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180))
				.with(
						Condition.condition().term(BlockStateProperties.WEST_WALL, WallSide.TALL),
						Variant.variant()
								.with(VariantProperties.MODEL, steepModel)
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
		ResourceLocation model0 = XKDModelTemplates.MAP.get("template_roof_tip").create(
				block,
				TextureMapping.cube(block),
				generators.modelOutput);
		ResourceLocation model1 = XKDModelTemplates.MAP.get("template_roof_tip_top").create(
				block,
				TextureMapping.cube(block),
				generators.modelOutput);
		MultiVariantGenerator generator = MultiVariantGenerator.multiVariant(block)
				.with(PropertyDispatch.property(RoofTipBlock.HALF)
						.select(RoofUtil.RoofHalf.TIP, Variant.variant().with(VariantProperties.MODEL, model0))
						.select(RoofUtil.RoofHalf.BASE, Variant.variant().with(VariantProperties.MODEL, model1)));
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
