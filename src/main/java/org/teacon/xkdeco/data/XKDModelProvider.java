package org.teacon.xkdeco.data;

import static net.minecraft.data.models.model.TextureMapping.getBlockTexture;
import static org.teacon.xkdeco.block.XKDStateProperties.HALF;
import static org.teacon.xkdeco.block.XKDStateProperties.ROOF_EAVE_SHAPE;
import static org.teacon.xkdeco.block.XKDStateProperties.ROOF_END_SHAPE;
import static org.teacon.xkdeco.block.XKDStateProperties.ROOF_SHAPE;
import static org.teacon.xkdeco.block.XKDStateProperties.ROOF_VARIANT;
import static org.teacon.xkdeco.block.XKDStateProperties.ROOF_VARIANT_WITHOUT_SLOW;

import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.teacon.xkdeco.XKDeco;
import org.teacon.xkdeco.block.BlockDisplayBlock;
import org.teacon.xkdeco.block.HangingFasciaBlock;
import org.teacon.xkdeco.block.ItemDisplayBlock;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
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
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.level.block.state.properties.WallSide;

import org.teacon.xkdeco.block.XKDStateProperties;

import snownee.kiwi.customization.block.KBlockSettings;
import snownee.kiwi.customization.block.component.LayeredComponent;
import snownee.kiwi.customization.block.loader.KBlockComponents;
import snownee.kiwi.datagen.GameObjectLookup;
import snownee.kiwi.util.NotNullByDefault;

@SuppressWarnings({"deprecation", "SameParameterValue"})
@NotNullByDefault
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
			"item/item_projector",
			"block/dirt_path_slab",
			"block/dirt_path_slab_top",
			"block/grass_block_slab",
			"block/grass_block_slab_top",
			"block/air_duct_oblique",
			"block/air_duct_oblique_top",
			"block/hollow_steel_beam_oblique",
			"block/hollow_steel_beam_oblique_top",
			"block/hollow_steel_beam_oblique_slow",
			"block/hollow_steel_beam_oblique_slow_top",
			"block/hollow_steel_beam_oblique_steep",
			"block/hollow_steel_beam_oblique_steep_top",
			"block/quartz_wall_post");
	private static final Set<Block> SKIPPED_TRAPDOORS = Set.of(
			block("glass_trapdoor"),
			block("steel_trapdoor"),
			block("hollow_steel_trapdoor"));
	private static final Set<BlockFamily> TREATED_WOOD_FAMILIES = Set.of(
			XKDBlockFamilies.VARNISHED_PLANKS,
			XKDBlockFamilies.EBONY_PLANKS,
			XKDBlockFamilies.MAHOGANY_PLANKS);
	private static final List<String> GADGET_SKIP_PREFIXES = List.of(
			"varnished_",
			"ebony_",
			"mahogany_");
	private static final Set<Block> GADGET_SKIP_BLOCKS = Set.of(
			block("empty_candlestick"),
			block("oil_lamp"));
	private BlockModelGenerators generators;
	private static final Set<Block> generated = Sets.newHashSet();
	private final ResourceLocation snowySlabDouble = new ResourceLocation("block/grass_block_snow");
	private final ResourceLocation snowySlabTop = XKDeco.id("block/snowy_slab_top");

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

	public static boolean createIfSpecialDoubleSlabs(Block block, BlockModelGenerators generators, BlockFamily family) {
		if (!SPECIAL_DOUBLE_SLABS.contains(block)) {
			return false;
		}
		TextureMapping mapping = TextureMapping.column(
				getBlockTexture(block),
				getBlockTexture(family.getBaseBlock()));
		ResourceLocation bottom = ModelTemplates.SLAB_BOTTOM.create(block, mapping, generators.modelOutput);
		ResourceLocation top = ModelTemplates.SLAB_TOP.create(block, mapping, generators.modelOutput);
		ResourceLocation cube = ModelTemplates.CUBE_COLUMN.createWithOverride(block, "_full", mapping, generators.modelOutput);
		generators.blockStateOutput.accept(BlockModelGenerators.createSlab(block, bottom, top, cube));
		return true;
	}

	public static boolean createIfSpecialTrapdoor(Block block, BlockModelGenerators generators, BlockFamily family) {
		ResourceLocation id = BuiltInRegistries.BLOCK.getKey(family.getBaseBlock());
		if (id.getPath().startsWith("factory_")) {
			createTrapdoor(block, family.getBaseBlock(), generators);
			return true;
		}
		if (TREATED_WOOD_FAMILIES.contains(family)) {
			TextureMapping $$1 = TextureMapping.defaultTexture(block);
			ResourceLocation $$2 = XKDModelTemplates.THIN_TRAPDOOR_TOP.create(block, $$1, generators.modelOutput);
			ResourceLocation $$3 = XKDModelTemplates.THIN_TRAPDOOR_BOTTOM.create(block, $$1, generators.modelOutput);
			ResourceLocation $$4 = XKDModelTemplates.THIN_TRAPDOOR_OPEN.create(block, $$1, generators.modelOutput);
			generators.blockStateOutput.accept(BlockModelGenerators.createOrientableTrapdoor(block, $$2, $$3, $$4));
			generators.delegateItemModel(block, $$3);
			return true;
		}
		if (SKIPPED_TRAPDOORS.contains(block)) {
			ResourceLocation model1 = ModelLocationUtils.getModelLocation(block, "_top");
			ResourceLocation model2 = ModelLocationUtils.getModelLocation(block, "_bottom");
			ResourceLocation model3 = ModelLocationUtils.getModelLocation(block, "_open");
			generators.blockStateOutput.accept(BlockModelGenerators.createTrapdoor(block, model1, model2, model3));
			generators.delegateItemModel(block, model2);
			return true;
		}
		return false;
	}

	private static void createTrapdoor(Block trapdoorBlock, Block fullBlock, BlockModelGenerators generators) {
		TextureMapping mapping = TextureMapping.defaultTexture(fullBlock);
		ResourceLocation model1 = ModelTemplates.TRAPDOOR_TOP.create(trapdoorBlock, mapping, generators.modelOutput);
		ResourceLocation model2 = ModelTemplates.TRAPDOOR_BOTTOM.create(trapdoorBlock, mapping, generators.modelOutput);
		ResourceLocation model3 = ModelTemplates.TRAPDOOR_OPEN.create(trapdoorBlock, mapping, generators.modelOutput);
		generators.blockStateOutput.accept(BlockModelGenerators.createTrapdoor(trapdoorBlock, model1, model2, model3));
		generators.delegateItemModel(trapdoorBlock, model2);
	}

	public static boolean createIfSpecialFence(Block block, BlockModelGenerators generators, BlockFamily family) {
		if (!TREATED_WOOD_FAMILIES.contains(family)) {
			return false;
		}
		TextureMapping postTextures = TextureMapping.defaultTexture(getBlockTexture(block, "_post"));
		postTextures.put(TextureSlot.SIDE, TextureMapping.getBlockTexture(block));
		ResourceLocation post = XKDModelTemplates.WOODEN_FENCE_POST.create(
				block,
				postTextures,
				generators.modelOutput);
		ResourceLocation side = XKDModelTemplates.WOODEN_FENCE_SIDE.create(
				block,
				TextureMapping.defaultTexture(block),
				generators.modelOutput);
		ResourceLocation inventory = XKDModelTemplates.WOODEN_FENCE_INVENTORY.create(
				block,
				postTextures,
				generators.modelOutput);
		generators.blockStateOutput.accept(createFenceNoUvLock(block, post, side));
		generators.delegateItemModel(block, inventory);
		return true;
	}

	public static BlockStateGenerator createFenceNoUvLock(
			Block pFenceBlock,
			ResourceLocation pFencePostModelLocation,
			ResourceLocation pFenceSideModelLocation) {
		return MultiPartGenerator.multiPart(pFenceBlock)
				.with(Variant.variant().with(VariantProperties.MODEL, pFencePostModelLocation))
				.with(Condition.condition().term(BlockStateProperties.NORTH, true), Variant.variant()
						.with(VariantProperties.MODEL, pFenceSideModelLocation))
				.with(Condition.condition().term(BlockStateProperties.EAST, true), Variant.variant()
						.with(VariantProperties.MODEL, pFenceSideModelLocation)
						.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
				.with(Condition.condition().term(BlockStateProperties.SOUTH, true), Variant.variant()
						.with(VariantProperties.MODEL, pFenceSideModelLocation)
						.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180))
				.with(Condition.condition().term(BlockStateProperties.WEST, true), Variant.variant()
						.with(VariantProperties.MODEL, pFenceSideModelLocation)
						.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270));
	}

	public static boolean createIfSpecialFenceGate(Block block, BlockModelGenerators generators, BlockFamily family) {
		if (!TREATED_WOOD_FAMILIES.contains(family)) {
			return false;
		}
		TextureMapping textureMapping = TextureMapping.defaultTexture(block);
		ResourceLocation $$1 = XKDModelTemplates.WOODEN_FENCE_GATE_OPEN.create(block, textureMapping, generators.modelOutput);
		ResourceLocation $$2 = XKDModelTemplates.WOODEN_FENCE_GATE_CLOSED.create(block, textureMapping, generators.modelOutput);
		generators.blockStateOutput.accept(BlockModelGenerators.createFenceGate(block, $$1, $$2, $$1, $$2, true));
		return true;
	}

	public static boolean createIfSpecialStairs(Block block, TextureMapping mapping, BlockModelGenerators generators) {
		KBlockSettings settings = KBlockSettings.of(block);
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
		var originalBlockStateOutput = generators.blockStateOutput;
		generators.blockStateOutput = generator -> {
			generated.add(generator.getBlock());
			originalBlockStateOutput.accept(generator);
		};
		var originalModelOutput = generators.modelOutput;
		generators.modelOutput = (modelLocation, json) -> {
			if (modelLocation.getPath().startsWith("block/grass_cobblestone")) {
				return;
			}
			if (!SKIPPED_MODELS.contains(modelLocation.getPath())) {
				originalModelOutput.accept(modelLocation, json);
			}
		};
		BlockModelGenerators.SHAPE_CONSUMERS = ImmutableMap.<BlockFamily.Variant, BiConsumer<BlockModelGenerators.BlockFamilyProvider, Block>>builder()
				.putAll(BlockModelGenerators.SHAPE_CONSUMERS)
				.put(BlockFamily.Variant.CUT, BlockModelGenerators.BlockFamilyProvider::fullBlockVariant)
				.put(BlockFamily.Variant.POLISHED, BlockModelGenerators.BlockFamilyProvider::fullBlockVariant)
				.build();

		var mayaCutStonebricks = block("maya_cut_stonebricks");
		var aztecCutStonebricks = block("aztec_cut_stonebricks");
		generators.texturedModels = ImmutableMap.<Block, TexturedModel>builder()
				.putAll(generators.texturedModels)
				.put(
						mayaCutStonebricks,
						new TexturedModel(
								new TextureMapping().put(TextureSlot.SIDE, getBlockTexture(mayaCutStonebricks, "_side"))
										.put(TextureSlot.END, getBlockTexture(block("maya_chiseled_stonebricks"), "_top")),
								ModelTemplates.CUBE_COLUMN))
				.put(
						aztecCutStonebricks,
						new TexturedModel(
								new TextureMapping()
										.put(TextureSlot.SIDE, getBlockTexture(aztecCutStonebricks, "_side"))
										.put(TextureSlot.TOP, getBlockTexture(block("aztec_chiseled_stonebricks")))
										.put(TextureSlot.BOTTOM, getBlockTexture(aztecCutStonebricks, "_bottom")),
								ModelTemplates.CUBE_BOTTOM_TOP))
				.build();

		XKDBlockFamilies.getAllFamilies().filter(BlockFamily::shouldGenerateModel).forEach(family -> {
			Block baseBlock = family.getBaseBlock();
			LOGGER.info("Generating models for block family {}", baseBlock);
			BlockModelGenerators.BlockFamilyProvider provider;
			if (family == XKDBlockFamilies.LINED_MUD_WALL) {
				TextureMapping textureMapping = TextureMapping.column(
						getBlockTexture(block("lined_mud_wall_block")),
						getBlockTexture(block("crossed_mud_wall_block")));
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
		createFallenLeaves("ginkgo_leaves");
		createFallenLeaves("orange_maple_leaves");
		createFallenLeaves("red_maple_leaves");
		createFallenLeaves("peach_blossom");
		createFallenLeaves("cherry_blossom");
		createFallenLeaves("white_cherry_blossom");
		createBlockStateOnly("hanging_willow_leaves", false);
		generators.createSimpleFlatItemModel(block("hanging_willow_leaves"));

		ResourceLocation dirtTexture = getBlockTexture(Blocks.DIRT);
		ResourceLocation netherrackTexture = getBlockTexture(Blocks.NETHERRACK);
		TextureMapping snowyMapping = new TextureMapping()
				.put(TextureSlot.BOTTOM, dirtTexture)
				.copyForced(TextureSlot.BOTTOM, TextureSlot.PARTICLE)
				.put(TextureSlot.TOP, getBlockTexture(Blocks.GRASS_BLOCK, "_top"))
				.put(TextureSlot.SIDE, getBlockTexture(Blocks.GRASS_BLOCK, "_snow"));
		ModelTemplates.SLAB_TOP.create(snowySlabTop, snowyMapping, generators.modelOutput);
		createSlab(Blocks.DIRT, false, false, UnaryOperator.identity());
		createSlab(Blocks.DIRT_PATH, true, true, $ -> $.put(TextureSlot.BOTTOM, dirtTexture));
		createSlab(Blocks.GRASS_BLOCK, true, true, UnaryOperator.identity());
		createSlab(Blocks.MYCELIUM, true, true, $ -> $
				.put(TextureSlot.BOTTOM, dirtTexture)
				.put(TextureSlot.TOP, getBlockTexture(Blocks.MYCELIUM, "_top")));
		createSlab(Blocks.NETHERRACK, false, false, UnaryOperator.identity());
		createSlab(Blocks.PODZOL, true, true, $ -> $
				.put(TextureSlot.BOTTOM, dirtTexture)
				.put(TextureSlot.TOP, getBlockTexture(Blocks.PODZOL, "_top")));
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
		createHorizontalShift("hollow_steel_beam_oblique", "hollow_steel_beam_oblique", null, false);
		createHorizontalShift("hollow_steel_beam_oblique_slow", "hollow_steel_beam_oblique_slow", null, false);
		createHorizontalShift("hollow_steel_beam_oblique_steep", "hollow_steel_beam_oblique_steep", null, false);
		createBlockStateOnly("steel_safety_ladder", true);
		createBlockStateOnly("steel_ladder", false);
		ModelTemplates.FLAT_ITEM.create(
				ModelLocationUtils.getModelLocation(block("steel_ladder").asItem()),
				TextureMapping.layer0(getBlockTexture(block("steel_safety_ladder"), "_side")),
				generators.modelOutput);

		generators.blockStateOutput.accept(BlockModelGenerators.createWall(
				block("dark_stone_handrail"),
				XKDeco.id("block/furniture/dark_stone_handrail_post"),
				XKDeco.id("block/furniture/dark_stone_handrail"),
				XKDeco.id("block/furniture/dark_stone_handrail_side_tall")));
		generators.delegateItemModel(block("dark_stone_handrail"), XKDeco.id("block/furniture/dark_stone_handrail_inventory"));
		generators.blockStateOutput.accept(BlockModelGenerators.createWall(
				block("light_stone_handrail"),
				XKDeco.id("block/furniture/light_stone_handrail_post"),
				XKDeco.id("block/furniture/light_stone_handrail"),
				XKDeco.id("block/furniture/light_stone_handrail_side_tall")));
		generators.delegateItemModel(block("light_stone_handrail"), XKDeco.id("block/furniture/light_stone_handrail_inventory"));

		createBlockStateOnly("factory_ceiling_lamp", "furniture/", true);
		createBlockStateOnly("factory_pendant", "furniture/", true);
		createBlockStateOnly("empty_fish_tank", "furniture/", true);
		createBlockStateOnly("covered_lamp", "furniture/", true);
		createBlockStateOnly("festival_lantern", "furniture/", true);
		createBlockStateOnly("paper_lantern", "furniture/", true);
		createBlockStateOnly("red_lantern", "furniture/", true);
		createBlockStateOnly("roofed_lamp", "furniture/", true);
		createBlockStateOnly("stone_lamp", "furniture/", true);
		createBlockStateOnly("deepslate_lamp", "furniture/", true);
		createBlockStateOnly("blackstone_lamp", "furniture/", true);
		createBlockStateOnly("stone_water_bowl", "furniture/", true);
		createBlockStateOnly("stone_water_tank", "furniture/", true);
		createBlockStateOnly("candlestick", "furniture/", true);
		createBlockStateOnly("big_candlestick", "furniture/", true);
		createBlockStateOnly("tech_table", "furniture/", true);
		createBlockStateOnly("tech_table_circle", "furniture/", true);

		createMoulding("egyptian_moulding", "furniture/egyptian_moulding", false, true);
		createMoulding("egyptian_moulding2", "furniture/egyptian_moulding", false, true);
		createMoulding("greek_moulding", "furniture/greek_moulding", false, true);
		createMoulding("greek_moulding2", "furniture/greek_moulding", false, true);
		createMoulding("roman_moulding", "furniture/roman_moulding", false, true);
		createMoulding("roman_moulding2", "furniture/roman_moulding", false, true);
		createMoulding("factory_light_bar", "furniture/factory_light_bar", false, true);
		createMoulding("dark_wall_base", "furniture/dark_wall_base", true, true);
		createMoulding("light_wall_base", "furniture/light_wall_base", true, true);
		createMoulding("mechanical_console", "furniture/mechanical_console", false, true);
		createMoulding("tech_console", "furniture/tech_console", false, true);
		createIronBarsLike("hollow_steel_bars", "hollow_steel_block", "steel_column_wall");

		createPillar("sandstone_pillar");
		createPillar("red_sandstone_pillar");
		createPillar("stone_brick_pillar");
		createPillar("deepslate_pillar");
		createPillar("blackstone_pillar");
		createPillar("gilded_blackstone_brick_pillar");
		createPillar("chiseled_gilded_blackstone");
		createPillar("luxury_gilded_blackstone");
		createPillar("maya_double_screw_thread_stone");
		createPillar("maya_pillar");
		createPillar("maya_mossy_pillar");
		createPillar("cut_obsidian_pillar");
		createPillar("gold_pillar");
		createPillar("chiseled_gold_block");
		createPillar("painted_gold_block");
		createPillar("bronze_pillar");
		createPillar("steel_pillar");

		createSingleScrewState("maya_single_screw_thread_stone");
		createSingleScrewState("screw_thread_bronze_block");

		createInscriptionBronzeBlock();

		createWall("quartz_wall", "quartz_wall_side");

		createNonRotatedPillar("dark_column_base");
		createNonRotatedPillar("light_column_base");

		generators.delegateItemModel(block("empty_candlestick"), XKDeco.id("block/furniture/empty_candlestick"));

		generators.skipAutoItemBlock(block("item_projector"));

		createBlockStateOnly("calligraphy", 2);
		createBlockStateOnly("ink_painting", 2);
		createBlockStateOnly("weiqi_board", 2);
		createBlockStateOnly("xiangqi_board", 2);

		outer:
		for (Item item : GameObjectLookup.all(Registries.ITEM, XKDeco.ID).toList()) {
			Block block = Block.byItem(item);
			if (block == Blocks.AIR || GADGET_SKIP_BLOCKS.contains(block) || generated.contains(block)) {
				continue;
			}
			var id = BuiltInRegistries.BLOCK.getKey(block);
			if (block instanceof ItemDisplayBlock || block instanceof BlockDisplayBlock) {
				createBlockStateOnly(id.getPath(), "furniture/", true);
				continue;
			}
			if (id.getPath().endsWith("column_base") || id.getPath().endsWith("column_head")) {
				createBlockStateOnly(id.getPath(), "furniture/", true);
				continue;
			}
			if (id.getPath().endsWith("column") && !id.getPath().endsWith("with_column")) {
				createBlockStateOnly(id.getPath(), "furniture/", true);
				continue;
			}
			for (String prefix : GADGET_SKIP_PREFIXES) {
				if (id.getPath().startsWith(prefix)) {
					continue outer;
				}
			}
			createGadget(block);
		}
	}

	private void createNonRotatedPillar(String id) {
		Block block = block(id);
		TextureMapping mapping = TextureMapping.column(block);
		ResourceLocation model = ModelTemplates.CUBE_COLUMN.create(block, mapping, generators.modelOutput);
		createBlockStateOnly(id, true);
	}

	private void createWall(String id, String texture) {
		Block block = block(id);
		TextureMapping mapping = new TextureMapping().put(TextureSlot.WALL, XKDeco.id("block/" + texture));
		ResourceLocation $$1 = ModelTemplates.WALL_POST.create(block, mapping, generators.modelOutput);
		ResourceLocation $$2 = ModelTemplates.WALL_LOW_SIDE.create(block, mapping, generators.modelOutput);
		ResourceLocation $$3 = ModelTemplates.WALL_TALL_SIDE.create(block, mapping, generators.modelOutput);
		generators.blockStateOutput.accept(BlockModelGenerators.createWall(block, $$1, $$2, $$3));
		ResourceLocation $$4 = ModelTemplates.WALL_INVENTORY.create(block, mapping, generators.modelOutput);
		generators.delegateItemModel(block, $$4);
	}

	private void createInscriptionBronzeBlock() {
		ModelTemplates.CUBE_ALL.create(
				XKDeco.id("block/inscription_bronze_block"),
				TextureMapping.cube(XKDeco.id("block/inscription_bronze_block")),
				generators.modelOutput);

		ModelTemplates.CUBE_ALL.create(
				XKDeco.id("block/inscription_bronze_block1"),
				TextureMapping.cube(XKDeco.id("block/inscription_bronze_block1")),
				generators.modelOutput);

		ModelTemplates.CUBE_ALL.create(
				XKDeco.id("block/inscription_bronze_block2"),
				TextureMapping.cube(XKDeco.id("block/inscription_bronze_block2")),
				generators.modelOutput);

		generators.blockStateOutput.accept(MultiPartGenerator.multiPart(block("inscription_bronze_block"))
				.with(List.of(
						Variant.variant().with(VariantProperties.MODEL, XKDeco.id("block/inscription_bronze_block")),
						Variant.variant().with(VariantProperties.MODEL, XKDeco.id("block/inscription_bronze_block1")),
						Variant.variant().with(VariantProperties.MODEL, XKDeco.id("block/inscription_bronze_block2")))));
	}

	private void createSingleScrewState(String id) {
		var mayaSingleScrewThreadStone = block(id);
		generators.blockStateOutput.accept(MultiVariantGenerator.multiVariant(mayaSingleScrewThreadStone)
				.with(PropertyDispatch.property(BlockStateProperties.HORIZONTAL_FACING)
						.select(
								Direction.NORTH,
								Variant.variant().with(VariantProperties.MODEL, XKDeco.id("block/" + id)))
						.select(
								Direction.SOUTH,
								Variant.variant()
										.with(VariantProperties.MODEL, XKDeco.id("block/" + id + "_s"))
										.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180))
						.select(
								Direction.WEST,
								Variant.variant()
										.with(VariantProperties.MODEL, XKDeco.id("block/" + id + "_w"))
										.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))
						.select(
								Direction.EAST,
								Variant.variant()
										.with(VariantProperties.MODEL, XKDeco.id("block/" + id + "_e"))
										.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
				));
	}

	private void createFallenLeaves(String id) {
		Block leaves = block(id);
		Block fallenLeaves = block("fallen_" + id);
		TextureMapping textureMapping = TextureMapping.cube(leaves);
		ResourceLocation model0 = XKDModelTemplates.FALLEN_LEAVES.create(fallenLeaves, textureMapping, generators.modelOutput);
		ResourceLocation model1 = XKDModelTemplates.FALLEN_LEAVES_SLAB.create(fallenLeaves, textureMapping, generators.modelOutput);
		MultiVariantGenerator generator = MultiVariantGenerator.multiVariant(fallenLeaves)
				.with(PropertyDispatch.property(HALF)
						.select(XKDStateProperties.RoofHalf.UPPER, Variant.variant().with(VariantProperties.MODEL, model0))
						.select(XKDStateProperties.RoofHalf.LOWER, Variant.variant().with(VariantProperties.MODEL, model1)));
		generators.blockStateOutput.accept(generator);
	}

	private void createIronBarsLike(String id, String paneTexture, String edgeTexture) {
		Block block = block(id);
		TextureMapping texturemapping = new TextureMapping()
				.put(TextureSlot.PANE, XKDeco.id(paneTexture).withPrefix("block/"))
				.put(TextureSlot.EDGE, XKDeco.id(edgeTexture).withPrefix("block/"));
		ResourceLocation resourcelocation = ModelTemplates.STAINED_GLASS_PANE_POST.create(block, texturemapping, generators.modelOutput);
		ResourceLocation resourcelocation1 = ModelTemplates.STAINED_GLASS_PANE_SIDE.create(block, texturemapping, generators.modelOutput);
		ResourceLocation resourcelocation2 = ModelTemplates.STAINED_GLASS_PANE_SIDE_ALT.create(
				block,
				texturemapping,
				generators.modelOutput);
		ResourceLocation resourcelocation3 = ModelTemplates.STAINED_GLASS_PANE_NOSIDE.create(block, texturemapping, generators.modelOutput);
		ResourceLocation resourcelocation4 = ModelTemplates.STAINED_GLASS_PANE_NOSIDE_ALT.create(
				block,
				texturemapping,
				generators.modelOutput);
		Item item = block.asItem();
		ModelTemplates.FLAT_ITEM.create(
				ModelLocationUtils.getModelLocation(item),
				TextureMapping.layer0(texturemapping.get(TextureSlot.PANE)),
				generators.modelOutput);
		generators.blockStateOutput.accept(MultiPartGenerator.multiPart(block)
				.with(Variant.variant()
						.with(VariantProperties.MODEL, resourcelocation))
				.with(
						Condition.condition().term(BlockStateProperties.NORTH, true),
						Variant.variant().with(VariantProperties.MODEL, resourcelocation1))
				.with(
						Condition.condition()
								.term(BlockStateProperties.EAST, true),
						Variant.variant()
								.with(VariantProperties.MODEL, resourcelocation1)
								.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
				.with(Condition.condition()
						.term(BlockStateProperties.SOUTH, true), Variant.variant().with(VariantProperties.MODEL, resourcelocation2))
				.with(
						Condition.condition().term(BlockStateProperties.WEST, true),
						Variant.variant()
								.with(VariantProperties.MODEL, resourcelocation2)
								.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
				.with(
						Condition.condition().term(BlockStateProperties.NORTH, false),
						Variant.variant().with(VariantProperties.MODEL, resourcelocation3))
				.with(
						Condition.condition().term(BlockStateProperties.EAST, false),
						Variant.variant().with(VariantProperties.MODEL, resourcelocation4))
				.with(
						Condition.condition().term(BlockStateProperties.SOUTH, false),
						Variant.variant()
								.with(VariantProperties.MODEL, resourcelocation4)
								.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))
				.with(
						Condition.condition().term(BlockStateProperties.WEST, false),
						Variant.variant()
								.with(VariantProperties.MODEL, resourcelocation3)
								.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)));
	}

	private void createMouldingWithModels(String id, String template, TextureMapping mapping, boolean itemModel) {
		Block block = block(id);
		XKDModelTemplates.MAP.get(template).create(block, mapping, generators.modelOutput);
		XKDModelTemplates.MAP.get(template + "_inner").create(block, mapping, generators.modelOutput);
		XKDModelTemplates.MAP.get(template + "_outer").create(block, mapping, generators.modelOutput);
		createMoulding(id, id, false, itemModel);
	}

	private void createMoulding(String id, String model, boolean uvLock, boolean itemModel, Variant... baseVariants) {
		if (baseVariants.length == 0) {
			baseVariants = new Variant[]{Variant.variant()};
		}
		Block block = block(id);
		ResourceLocation pStraightModelLocation = XKDeco.id("block/" + model);
		ResourceLocation pOuterModelLocation = XKDeco.id("block/" + model + "_outer");
		ResourceLocation pInnerModelLocation = XKDeco.id("block/" + model + "_inner");
		if (itemModel) {
			generators.delegateItemModel(block, pStraightModelLocation);
		}
		var generator = MultiVariantGenerator.multiVariant(block, baseVariants).with(PropertyDispatch.properties(
						BlockStateProperties.HORIZONTAL_FACING,
						BlockStateProperties.STAIRS_SHAPE)
				.select(
						Direction.EAST,
						StairsShape.STRAIGHT,
						Variant.variant().with(VariantProperties.MODEL, pStraightModelLocation))
				.select(
						Direction.WEST,
						StairsShape.STRAIGHT,
						Variant.variant()
								.with(VariantProperties.MODEL, pStraightModelLocation)
								.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
								.with(VariantProperties.UV_LOCK, uvLock))
				.select(
						Direction.SOUTH,
						StairsShape.STRAIGHT,
						Variant.variant()
								.with(VariantProperties.MODEL, pStraightModelLocation)
								.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
								.with(VariantProperties.UV_LOCK, uvLock))
				.select(
						Direction.NORTH,
						StairsShape.STRAIGHT,
						Variant.variant()
								.with(VariantProperties.MODEL, pStraightModelLocation)
								.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
								.with(VariantProperties.UV_LOCK, uvLock))
				.select(
						Direction.EAST,
						StairsShape.OUTER_RIGHT,
						Variant.variant().with(VariantProperties.MODEL, pOuterModelLocation))
				.select(
						Direction.WEST,
						StairsShape.OUTER_RIGHT,
						Variant.variant()
								.with(VariantProperties.MODEL, pOuterModelLocation)
								.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
								.with(VariantProperties.UV_LOCK, uvLock))
				.select(
						Direction.SOUTH,
						StairsShape.OUTER_RIGHT,
						Variant.variant()
								.with(VariantProperties.MODEL, pOuterModelLocation)
								.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
								.with(VariantProperties.UV_LOCK, uvLock))
				.select(
						Direction.NORTH,
						StairsShape.OUTER_RIGHT,
						Variant.variant()
								.with(VariantProperties.MODEL, pOuterModelLocation)
								.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
								.with(VariantProperties.UV_LOCK, uvLock))
				.select(
						Direction.EAST,
						StairsShape.OUTER_LEFT,
						Variant.variant()
								.with(VariantProperties.MODEL, pOuterModelLocation)
								.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
								.with(VariantProperties.UV_LOCK, uvLock))
				.select(
						Direction.WEST,
						StairsShape.OUTER_LEFT,
						Variant.variant()
								.with(VariantProperties.MODEL, pOuterModelLocation)
								.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
								.with(VariantProperties.UV_LOCK, uvLock))
				.select(
						Direction.SOUTH,
						StairsShape.OUTER_LEFT,
						Variant.variant().with(VariantProperties.MODEL, pOuterModelLocation))
				.select(
						Direction.NORTH,
						StairsShape.OUTER_LEFT,
						Variant.variant()
								.with(VariantProperties.MODEL, pOuterModelLocation)
								.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
								.with(VariantProperties.UV_LOCK, uvLock))
				.select(
						Direction.EAST,
						StairsShape.INNER_RIGHT,
						Variant.variant().with(VariantProperties.MODEL, pInnerModelLocation))
				.select(
						Direction.WEST,
						StairsShape.INNER_RIGHT,
						Variant.variant()
								.with(VariantProperties.MODEL, pInnerModelLocation)
								.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
								.with(VariantProperties.UV_LOCK, uvLock))
				.select(
						Direction.SOUTH,
						StairsShape.INNER_RIGHT,
						Variant.variant()
								.with(VariantProperties.MODEL, pInnerModelLocation)
								.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
								.with(VariantProperties.UV_LOCK, uvLock))
				.select(
						Direction.NORTH,
						StairsShape.INNER_RIGHT,
						Variant.variant()
								.with(VariantProperties.MODEL, pInnerModelLocation)
								.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
								.with(VariantProperties.UV_LOCK, uvLock))
				.select(
						Direction.EAST,
						StairsShape.INNER_LEFT,
						Variant.variant()
								.with(VariantProperties.MODEL, pInnerModelLocation)
								.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270)
								.with(VariantProperties.UV_LOCK, uvLock))
				.select(
						Direction.WEST,
						StairsShape.INNER_LEFT,
						Variant.variant()
								.with(VariantProperties.MODEL, pInnerModelLocation)
								.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)
								.with(VariantProperties.UV_LOCK, uvLock))
				.select(
						Direction.SOUTH,
						StairsShape.INNER_LEFT,
						Variant.variant().with(VariantProperties.MODEL, pInnerModelLocation))
				.select(
						Direction.NORTH,
						StairsShape.INNER_LEFT,
						Variant.variant()
								.with(VariantProperties.MODEL, pInnerModelLocation)
								.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180)
								.with(VariantProperties.UV_LOCK, uvLock)));
		generators.blockStateOutput.accept(generator);
	}

	private void createTreatedWood(String id) {
		Block log = block(id + "_log");
		TextureMapping logMapping = generators.woodProvider(log).log(log).wood(block(id + "_wood")).logMapping;
		Block slab = block(id + "_log_slab");
		ResourceLocation $$2 = ModelTemplates.SLAB_BOTTOM.create(slab, logMapping, generators.modelOutput);
		ResourceLocation $$3 = ModelTemplates.SLAB_TOP.create(slab, logMapping, generators.modelOutput);
		generators.blockStateOutput.accept(BlockModelGenerators.createSlab(slab, $$2, $$3, ModelLocationUtils.getModelLocation(log)));

		createTrivialBlock(id + "_table", XKDModelTemplates.WOODEN_TABLE_PROVIDER);
		createTrivialBlock(id + "_big_table", XKDModelTemplates.WOODEN_BIG_TABLE_PROVIDER);
		createTrivialBlock(id + "_tall_table", XKDModelTemplates.WOODEN_TALL_TABLE_PROVIDER);
		createHorizontalAxis(id + "_desk", XKDModelTemplates.WOODEN_DESK_PROVIDER);
//		createHorizontallyRotatedBlock(id + "_desk", XKDModelTemplates.WOODEN_DESK_PROVIDER);
		createGadget(block(id + "_chair"));
		createHorizontalAxis(id + "_stool", XKDModelTemplates.WOODEN_STOOL_PROVIDER);

		TextureMapping textureMapping = logMapping.copyAndUpdate(TextureSlot.WALL, XKDeco.id("block/" + id + "_smooth"));
		textureMapping.put(XKDModelTemplates.PLANKS, XKDeco.id("block/" + id + "_planks"));
		createWoodenWall(id + "_column_wall", "wooden_column_wall", textureMapping);
		createWoodenWall("hollow_" + id + "_column_wall", "hollow_wooden_column_wall", textureMapping);
		createWoodenWall(id + "_wall", "wooden_wall", textureMapping);

		createMoulding(id + "_meiren_kao", "furniture/" + id + "_meiren_kao", false, true);
		createMoulding(id + "_meiren_kao_with_column", "furniture/" + id + "_meiren_kao_with_column", false, true);

		textureMapping = new TextureMapping().put(TextureSlot.SIDE, XKDeco.id("block/" + id + "_smooth"));
		createMouldingWithModels(id + "_dougong", "template_dougong", textureMapping, true);
		createMouldingWithModels(id + "_dougong_connection", "template_dougong_connection", textureMapping, true);
		createMouldingWithModels(id + "_dougong_hollow_connection", "template_dougong_hollow_connection", textureMapping, true);

		createWoodenFenceHead(id);
		createHangingFascia(id);
		createHorizontallyRotatedBlock(id + "_oblique_brace", XKDModelTemplates.WOODEN_OBLIQUE_BRACE_PROVIDER);

		textureMapping = TextureMapping.defaultTexture(block(id + "_trapdoor"));
		textureMapping.put(TextureSlot.PARTICLE, getBlockTexture(block(id + "_window")));
		textureMapping.put(TextureSlot.TOP, XKDeco.id("block/" + id + "_narrow_doors_top"));
		textureMapping.put(TextureSlot.BOTTOM, XKDeco.id("block/"+ id + "_narrow_doors_bottom"));
		createWoodenFenceGate(id + "_window", "wooden_window", textureMapping);
		createWoodenFenceGate(id + "_awning_window", "wooden_awning_window", textureMapping);
		createWoodenFenceGate(id + "_narrow_doors", "wooden_narrow_doors", textureMapping);
		ResourceLocation narrowDoors = ModelLocationUtils.getModelLocation(block(id + "_narrow_doors").asItem());
		ModelTemplates.FLAT_ITEM.create(
				narrowDoors,
				TextureMapping.layer0(narrowDoors),
				generators.modelOutput);

		Block columnHead = block(id + "_column_head");
		XKDModelTemplates.WOODEN_COLUMN_HEAD.create(columnHead, TextureMapping.particle(columnHead), generators.modelOutput);
		createDirectional(id + "_column_head", "");
		generators.createSimpleFlatItemModel(block(id + "_screen").asItem());

		Block fenceOblique = block(id + "_fence_oblique");
		textureMapping = TextureMapping.particle(fenceOblique).put(XKDModelTemplates.POST, XKDeco.id("block/" + id + "_fence_post"));
		XKDModelTemplates.WOODEN_FENCE_OBLIQUE.create(fenceOblique, textureMapping, generators.modelOutput);
		createHorizontal(id + "_fence_oblique", "");

		Block fenceObliqueSteep = block(id + "_fence_oblique_steep");
		XKDModelTemplates.WOODEN_FENCE_OBLIQUE_STEEP.create(fenceObliqueSteep, textureMapping, generators.modelOutput);
		createHorizontal(id + "_fence_oblique_steep", "");

		createWoodenShelf(id, 6);

		Block emptyShelf = block(id + "_empty_shelf");
		XKDModelTemplates.WOODEN_EMPTY_SHELF.create(emptyShelf, TextureMapping.particle(block(id + "_shelf")), generators.modelOutput);
		createHorizontal(id + "_empty_shelf", "");

		Block dividedShelf = block(id + "_divided_shelf");
		XKDModelTemplates.WOODEN_DIVIDED_SHELF.create(dividedShelf, TextureMapping.particle(block(id + "_shelf")), generators.modelOutput);
		createHorizontal(id + "_divided_shelf", "");

	}

	private void createWoodenShelf(String id, int randomVariants) {
		Block block = block(id + "_shelf");
		TextureMapping mapping = TextureMapping.particle(getBlockTexture(block));
		ResourceLocation modelLocation = BuiltInRegistries.BLOCK.getKey(block).withPrefix("block/" );
		List<Variant> variants = Lists.newArrayList(Variant.variant().with(VariantProperties.MODEL, modelLocation));
		List<ModelTemplate> templates = Lists.newArrayList(
				XKDModelTemplates.WOODEN_SHELF,
				XKDModelTemplates.WOODEN_SHELF_2,
				XKDModelTemplates.WOODEN_SHELF_3,
				XKDModelTemplates.WOODEN_SHELF_4,
				XKDModelTemplates.WOODEN_SHELF_5,
				XKDModelTemplates.WOODEN_SHELF_6);
		if (randomVariants > 1) {
			for (int i = 0; i < templates.size(); i++) {
				String suffix = i == 0 ? "" : "_" + (i + 1);
				templates.get(i).create(modelLocation.withSuffix(suffix), mapping, generators.modelOutput);
				variants.add(Variant.variant().with(VariantProperties.MODEL, modelLocation.withSuffix(suffix)));
			}
		}
		generators.blockStateOutput.accept(MultiVariantGenerator.multiVariant(
						block,
						variants.toArray(Variant[]::new))
				.with(BlockModelGenerators.createHorizontalFacingDispatch()));
		generators.delegateItemModel(block, modelLocation);
	}

	private void createWoodenFenceGate(String id, String template, TextureMapping mapping) {
		Block block = block(id);
		ResourceLocation open = XKDModelTemplates.MAP.get(template + "_open").create(block, mapping, generators.modelOutput);
		ResourceLocation closed = XKDModelTemplates.MAP.get(template).create(block, mapping, generators.modelOutput);
		generators.blockStateOutput.accept(BlockModelGenerators.createFenceGate(block, open, closed, open, closed, false));
	}

	private void createHorizontalAxis(String id, TexturedModel.Provider provider) {
		Block block = block(id);
		ResourceLocation model = provider.create(block, generators.modelOutput);
		generators.blockStateOutput.accept(MultiVariantGenerator.multiVariant(
						block,
						Variant.variant().with(VariantProperties.MODEL, model))
				.with(PropertyDispatch.property(BlockStateProperties.HORIZONTAL_AXIS)
						.select(Direction.Axis.X, Variant.variant())
						.select(Direction.Axis.Z, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90))));
	}

	private void createHangingFascia(String id) {
		Block block = block(id + "_hanging_fascia");
		TextureMapping mapping = TextureMapping.particle(getBlockTexture(block, "_side"));
		ResourceLocation sideModel = XKDModelTemplates.HANGING_FASCIA_SIDE.create(block, mapping, generators.modelOutput);
		mapping = TextureMapping.particle(getBlockTexture(block, "_middle"));
		ResourceLocation middleModel = XKDModelTemplates.HANGING_FASCIA_MIDDLE.create(block, mapping, generators.modelOutput);
		MultiVariantGenerator generator = MultiVariantGenerator.multiVariant(block)
				.with(PropertyDispatch.properties(HangingFasciaBlock.SIDE, BlockStateProperties.HORIZONTAL_AXIS)
						.generate((side, axis) -> {
							int rotation = 0;
							var variant = Variant.variant();
							if (side == HangingFasciaBlock.Side.NONE) {
								variant.with(VariantProperties.MODEL, middleModel);
							} else {
								variant.with(VariantProperties.MODEL, sideModel);
								if (side == HangingFasciaBlock.Side.POSITIVE) {
									rotation += 180;
								}
							}
							if (axis == Direction.Axis.Z) {
								rotation += 90;
							}
							variant.with(VariantProperties.Y_ROT, VariantProperties.Rotation.valueOf("R" + rotation));
							return variant;
						}));
		generators.blockStateOutput.accept(generator);
		generators.delegateItemModel(block, sideModel);
	}

	private void createWoodenFenceHead(String id) {
		Block block = block(id + "_fence_head");
		TextureMapping mapping = TextureMapping.particle(getBlockTexture(block(id + "_fence"), "_post"));
		ResourceLocation model = XKDModelTemplates.WOODEN_FENCE_HEAD.create(block, mapping, generators.modelOutput);
		ResourceLocation flipModel = XKDModelTemplates.WOODEN_FENCE_HEAD_FLIP.create(block, mapping, generators.modelOutput);
		MultiVariantGenerator generator = MultiVariantGenerator.multiVariant(block)
				.with(PropertyDispatch.property(BlockStateProperties.FACING)
						.select(Direction.DOWN, Variant.variant()
								.with(VariantProperties.MODEL, flipModel)
								.with(VariantProperties.X_ROT, VariantProperties.Rotation.R90))
						.select(Direction.UP, Variant.variant()
								.with(VariantProperties.MODEL, model)
								.with(VariantProperties.X_ROT, VariantProperties.Rotation.R270))
						.select(Direction.NORTH, Variant.variant()
								.with(VariantProperties.MODEL, model))
						.select(Direction.SOUTH, Variant.variant()
								.with(VariantProperties.MODEL, model)
								.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R180))
						.select(Direction.WEST, Variant.variant()
								.with(VariantProperties.MODEL, model)
								.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R270))
						.select(Direction.EAST, Variant.variant()
								.with(VariantProperties.MODEL, model)
								.with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)));
		generators.blockStateOutput.accept(generator);
		generators.delegateItemModel(block, model);
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
		KBlockSettings settings = KBlockSettings.of(block);
		if (settings == null || settings.components.isEmpty()) {
			return;
		}
		LayeredComponent layered = (LayeredComponent) settings.components.values()
				.stream()
				.filter($ -> $ instanceof LayeredComponent)
				.findAny()
				.orElse(null);
		if (settings.hasComponent(KBlockComponents.HORIZONTAL.get())) {
			ResourceLocation model = id.withPrefix("block/furniture/");
			MultiVariantGenerator generator = MultiVariantGenerator.multiVariant(
							block,
							layered == null ? Variant.variant().with(VariantProperties.MODEL, model) : Variant.variant())
					.with(BlockModelGenerators.createHorizontalFacingDispatch());
			if (layered != null) {
				generator.with(PropertyDispatch.property(layered.getLayerProperty())
						.generate(layer -> Variant.variant().with(VariantProperties.MODEL, model.withSuffix("_" + layer))));
				generators.delegateItemModel(block, model.withSuffix("_" + layered.getDefaultLayer()));
			} else {
				generators.delegateItemModel(block, model);
			}
			generators.blockStateOutput.accept(generator);
		} else if (settings.hasComponent(KBlockComponents.DIRECTIONAL.get())) {
			createDirectional(id.getPath(), "furniture/");
		} else if (settings.hasComponent(KBlockComponents.FRONT_AND_TOP.get())) {
			ResourceLocation model;
			if (id.getPath().startsWith("screen_")) {
				TextureMapping mapping = TextureMapping.particle(block);
				model = XKDModelTemplates.SCREEN.create(block, mapping, generators.modelOutput);
			} else {
				model = id.withPrefix("block/furniture/");
			}
			MultiVariantGenerator generator = MultiVariantGenerator.multiVariant(
							block,
							Variant.variant().with(VariantProperties.MODEL, model))
					.with(PropertyDispatch.property(BlockStateProperties.ORIENTATION).generate(frontAndTop -> {
						return generators.applyRotation(frontAndTop, Variant.variant());
					}));
			generators.delegateItemModel(block, model);
			generators.blockStateOutput.accept(generator);
		} else if (layered != null) {
			ResourceLocation model = id.withPrefix("block/furniture/");
			MultiVariantGenerator generator = MultiVariantGenerator.multiVariant(block)
					.with(PropertyDispatch.property(layered.getLayerProperty())
							.generate(layer -> Variant.variant().with(VariantProperties.MODEL, model.withSuffix("_" + layer))));
			generators.delegateItemModel(block, model.withSuffix("_" + layered.getDefaultLayer()));
			generators.blockStateOutput.accept(generator);
		}
	}

	private void createDirectional(String id, String prefix) {
		Block block = block(id);
		ResourceLocation model = XKDeco.id(id).withPrefix("block/" + prefix);
		generators.blockStateOutput.accept(MultiVariantGenerator.multiVariant(
						block,
						Variant.variant().with(VariantProperties.MODEL, model))
				.with(BlockModelGenerators.createFacingDispatch()));
		generators.delegateItemModel(block, model);
	}

	private void createHorizontal(String id, String prefix) {
		Block block = block(id);
		ResourceLocation model = XKDeco.id(id).withPrefix("block/" + prefix);
		generators.blockStateOutput.accept(MultiVariantGenerator.multiVariant(
						block,
						Variant.variant().with(VariantProperties.MODEL, model))
				.with(BlockModelGenerators.createHorizontalFacingDispatch()));
		generators.delegateItemModel(block, model);
	}

	private void createSlab(Block fullBlock, boolean sided, boolean natural, UnaryOperator<TextureMapping> textureMappingOperator) {
		ResourceLocation id = BuiltInRegistries.BLOCK.getKey(fullBlock);
		Block slab = block(id.getPath() + "_slab");
		TextureMapping textureMapping = TextureMapping.cube(fullBlock);
		if (sided) {
			textureMapping.put(TextureSlot.SIDE, getBlockTexture(fullBlock, "_side"));
		}
		textureMapping = textureMappingOperator.apply(textureMapping);
		ModelTemplate bottomTemplate = natural ? XKDModelTemplates.NATURAL_SLAB : ModelTemplates.SLAB_BOTTOM;
		ResourceLocation bottomModel = bottomTemplate.create(slab, textureMapping, generators.modelOutput);
		ResourceLocation topModel = ModelTemplates.SLAB_TOP.create(slab, textureMapping, generators.modelOutput);
		ResourceLocation fullModel = ModelLocationUtils.getModelLocation(fullBlock);
		BlockStateGenerator generator;
		if (slab.defaultBlockState().hasProperty(BlockStateProperties.SNOWY)) {
			generator = MultiVariantGenerator.multiVariant(slab).with(PropertyDispatch.properties(
							BlockStateProperties.SNOWY,
							BlockStateProperties.SLAB_TYPE)
					.select(true, SlabType.BOTTOM, Variant.variant().with(VariantProperties.MODEL, bottomModel))
					.select(true, SlabType.TOP, Variant.variant().with(VariantProperties.MODEL, snowySlabTop))
					.select(true, SlabType.DOUBLE, Variant.variant().with(VariantProperties.MODEL, snowySlabDouble))
					.select(false, SlabType.BOTTOM, Variant.variant().with(VariantProperties.MODEL, bottomModel))
					.select(false, SlabType.TOP, Variant.variant().with(VariantProperties.MODEL, topModel))
					.select(false, SlabType.DOUBLE, Variant.variant().with(VariantProperties.MODEL, fullModel)));
		} else {
			generator = BlockModelGenerators.createSlab(slab, bottomModel, topModel, fullModel);
		}
		generators.blockStateOutput.accept(generator);
	}

	private void createBlockStateOnly(String id, boolean delegateItem) {
		createBlockStateOnly(id, "", delegateItem);
	}

	private void createBlockStateOnly(String id, int randomVariants) {
		createBlockStateOnly(id, "furniture/", true, randomVariants);
	}

	private void createBlockStateOnly(String id, String prefix, boolean delegateItem) {
		createBlockStateOnly(id, prefix, delegateItem, 1);
	}

	private void createBlockStateOnly(String id, String prefix, boolean delegateItem, int randomVariants) {
		Block block = block(id);
		KBlockSettings settings = KBlockSettings.of(block);
		ResourceLocation modelLocation = BuiltInRegistries.BLOCK.getKey(block).withPrefix("block/" + prefix);
		List<Variant> variants = Lists.newArrayList(Variant.variant().with(VariantProperties.MODEL, modelLocation));
		if (randomVariants > 1) {
			for (int i = 1; i < randomVariants; i++) {
				variants.add(Variant.variant().with(VariantProperties.MODEL, modelLocation.withSuffix("_" + (i + 1))));
			}
		}
		if (settings != null && settings.hasComponent(KBlockComponents.HORIZONTAL.get())) {
			generators.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block, variants.toArray(Variant[]::new))
					.with(BlockModelGenerators.createHorizontalFacingDispatch()));
		} else {
			generators.blockStateOutput.accept(MultiVariantGenerator.multiVariant(block, variants.toArray(Variant[]::new)));
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
		ResourceLocation roofTexture = getBlockTexture(block(id));
		ResourceLocation ridgeTexture = getBlockTexture(block(id), "_ridge");
		ResourceLocation smallRidgeTexture = getBlockTexture(block(id), "_small_ridge");
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
		generators.delegateItemModel(block(id + "_deco"), ModelLocationUtils.getModelLocation(block(id + "_deco")));
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
				.with(PropertyDispatch.property(HALF)
						.select(XKDStateProperties.RoofHalf.LOWER, Variant.variant().with(VariantProperties.MODEL, model0))
						.select(XKDStateProperties.RoofHalf.UPPER, Variant.variant().with(VariantProperties.MODEL, model1)));
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
				.with(PropertyDispatch.properties(ROOF_VARIANT_WITHOUT_SLOW, HALF)
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
							if (!variant.equals("normal")) {
								path += "_" + variant;
							}
							if (half.equals("upper") && !variant.equals("steep")) {
								path += "_top";
							} else if (half.equals("lower") && variant.equals("steep")) {
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
				.with(PropertyDispatch.properties(ROOF_VARIANT, ROOF_END_SHAPE, HALF)
						.generate((variant, shape, half) -> {
							String path = narrow ? "template_roof_small_end" : "template_roof_end";
							if (!"normal".equals(variant)) {
								path += "_" + variant;
							}
							path += "_" + shape;
							if ("upper".equals(half) && !"steep".equals(variant)) {
								path += "_top";
							} else if ("lower".equals(half) && "steep".equals(variant)) {
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
		generators.delegateItemModel(block, ModelLocationUtils.getModelLocation(block).withSuffix("_left"));
		generators.blockStateOutput.accept(generator);
	}

	private void createRoofEave(String id, ResourceLocation roofTexture, ResourceLocation ridgeTexture, boolean narrow) {
		Block block = block(id);
		MultiVariantGenerator generator = MultiVariantGenerator.multiVariant(block)
				.with(PropertyDispatch.properties(ROOF_EAVE_SHAPE, HALF).generate((shape, half) -> {
					String path = narrow ? "template_roof_small_eave" : "template_roof_eave";
					if (!shape.equals("straight")) {
						path += "_" + shape;
					}
					if (half.equals("upper")) {
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
				.with(PropertyDispatch.property(BlockStateProperties.HORIZONTAL_AXIS)
						.select(Direction.Axis.Z, Variant.variant())
						.select(Direction.Axis.X, Variant.variant().with(VariantProperties.Y_ROT, VariantProperties.Rotation.R90)))
				.with(PropertyDispatch.property(HALF)
						.select(XKDStateProperties.RoofHalf.LOWER, Variant.variant().with(VariantProperties.MODEL, model0))
						.select(XKDStateProperties.RoofHalf.UPPER, Variant.variant().with(VariantProperties.MODEL, model1)));
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
				.with(PropertyDispatch.properties(ROOF_VARIANT, ROOF_SHAPE, HALF)
						.generate((variant, shape, half) -> {
							String path = "template_roof";
							if (!"normal".equals(variant)) {
								path += "_" + variant;
							}
							if (!"straight".equals(shape)) {
								path += "_" + shape;
							}
							if ("upper".equals(half) && !"steep".equals(variant)) {
								path += "_top";
							} else if ("lower".equals(half) && "steep".equals(variant)) {
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

	private void createPillar(String id) {
		generators.createAxisAlignedPillarBlock(block(id), TexturedModel.COLUMN);
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
				.with(PropertyDispatch.property(HALF)
						.select(XKDStateProperties.RoofHalf.LOWER, Variant.variant().with(VariantProperties.MODEL, model0))
						.select(XKDStateProperties.RoofHalf.UPPER, Variant.variant().with(VariantProperties.MODEL, model1)));
		generators.blockStateOutput.accept(generator);
	}

	@Override
	public void generateItemModels(ItemModelGenerators generators) {
	}

	private static Block block(String id) {
		ResourceLocation resourceLocation = XKDeco.id(id);
		return BuiltInRegistries.BLOCK.getOptional(resourceLocation).orElseThrow(() -> new IllegalStateException(
				"Missing block: " + resourceLocation));
	}
}
