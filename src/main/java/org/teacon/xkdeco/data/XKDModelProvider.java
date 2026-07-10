package org.teacon.xkdeco.data;

import static net.minecraft.client.data.models.model.TextureMapping.getBlockTexture;
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

import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.teacon.xkdeco.XKDeco;
import org.teacon.xkdeco.block.BlockDisplayBlock;
import org.teacon.xkdeco.block.HangingFasciaBlock;
import org.teacon.xkdeco.block.HologramBlock;
import org.teacon.xkdeco.block.ItemDisplayBlock;
import org.teacon.xkdeco.client.model.AirDuctModel;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import com.mojang.math.Quadrant;

import net.minecraft.client.color.item.GrassColorSource;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.BlockModelDefinitionGenerator;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.data.models.model.TexturedModel;
import net.minecraft.client.renderer.block.dispatch.Variant;
import net.minecraft.client.renderer.block.dispatch.VariantMutator;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.level.block.state.properties.WallSide;
import net.neoforged.neoforge.client.model.generators.blockstate.CustomBlockStateModelBuilder;
import snownee.kiwi.customization.block.KBlockSettings;
import snownee.kiwi.customization.block.component.LayeredComponent;
import snownee.kiwi.customization.block.loader.KBlockComponents;
import snownee.kiwi.util.GameObjectLookup;

@SuppressWarnings("SameParameterValue")
public class XKDModelProvider extends ModelProvider {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Identifier ROOF_INNER_TEXTURE = XKDeco.id("block/roof_inner");
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
			"block/glass_trapdoor_bottom",
			"block/glass_trapdoor_open",
			"block/glass_trapdoor_top",
			"block/hollow_steel_trapdoor_bottom",
			"block/hollow_steel_trapdoor_open",
			"block/hollow_steel_trapdoor_top",
			"block/steel_trapdoor_bottom",
			"block/steel_trapdoor_open",
			"block/steel_trapdoor_top",
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
	private static final Set<Block> LEGACY_ITEM_MODEL_BLOCKS = Set.of(
//			block("ebony_wardrobe"),
//			block("full_glass_wardrobe"),
//			block("glass_wardrobe"),
			block("hologram_dna"),
			block("hologram_message"),
			block("hologram_pictures"),
			block("hologram_planet"),
			block("hologram_xekr_logo"),
			block("hollow_steel_bars"),
//			block("iron_wardrobe"),
//			block("mahogany_wardrobe"),
//			block("varnished_wardrobe"),
			block("oil_lamp"));
	private static final List<String> FOLIAGE_TINTED_ITEM_MODELS = List.of(
			"willow_leaves",
			"plantable_leaves_dark");
	private static final List<String> GRASS_DIRECT_TINTED_ITEM_MODELS = List.of(
			"plantable_leaves",
			"grass_block_slab",
			"grass_cobblestone",
			"grass_cobblestone_path");
	private static final List<String> GRASS_FAMILY_TINTED_ITEM_MODELS = List.of(
			"grass_cobblestone_slab",
			"grass_cobblestone_stairs",
			"grass_cobblestone_path_slab",
			"grass_cobblestone_path_stairs");
	private static final List<String> WATER_TINTED_ITEM_MODELS = List.of(
			"stone_water_bowl",
			"stone_water_tank");
	private static final int DEFAULT_WATER_ITEM_COLOR = 0x3F76E4;
	private BlockModelGenerators generators;
	private static final Set<Block> generated = Sets.newHashSet();
	private final Identifier snowySlabDouble = Identifier.withDefaultNamespace("block/grass_block_snow");
	private final Identifier snowySlabTop = XKDeco.id("block/snowy_slab_top");

	public XKDModelProvider(PackOutput output) {
		super(output, XKDeco.ID);
	}

	@Override
	protected Stream<? extends Holder<Block>> getKnownBlocks() {
		return Stream.empty();
	}

	@Override
	protected Stream<? extends Holder<Item>> getKnownItems() {
		return super.getKnownItems();
	}

	private static MultiVariant plainVariant(Identifier model) {
		return BlockModelGenerators.plainVariant(model);
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
		Identifier bottom = ModelTemplates.SLAB_BOTTOM.create(block, mapping, generators.modelOutput);
		Identifier top = ModelTemplates.SLAB_TOP.create(block, mapping, generators.modelOutput);
		Identifier cube = ModelTemplates.CUBE_COLUMN.createWithOverride(block, "_full", mapping, generators.modelOutput);
		generators.blockStateOutput.accept(BlockModelGenerators.createSlab(
				block,
				plainVariant(bottom),
				plainVariant(top),
				plainVariant(cube)));
		return true;
	}

	public static boolean createIfSpecialTrapdoor(Block block, BlockModelGenerators generators, BlockFamily family) {
		Identifier id = BuiltInRegistries.BLOCK.getKey(family.getBaseBlock());
		if (id.getPath().startsWith("factory_")) {
			createTrapdoor(block, family.getBaseBlock(), generators);
			return true;
		}
		if (TREATED_WOOD_FAMILIES.contains(family)) {
			TextureMapping $$1 = TextureMapping.defaultTexture(block);
			Identifier $$2 = XKDModelTemplates.THIN_TRAPDOOR_TOP.create(block, $$1, generators.modelOutput);
			Identifier $$3 = XKDModelTemplates.THIN_TRAPDOOR_BOTTOM.create(block, $$1, generators.modelOutput);
			Identifier $$4 = XKDModelTemplates.THIN_TRAPDOOR_OPEN.create(block, $$1, generators.modelOutput);
			generators.blockStateOutput.accept(BlockModelGenerators.createOrientableTrapdoor(
					block,
					plainVariant($$2),
					plainVariant($$3),
					plainVariant($$4)));
			generators.registerSimpleItemModel(block, $$3);
			return true;
		}
		if (SKIPPED_TRAPDOORS.contains(block)) {
			Identifier model1 = ModelLocationUtils.getModelLocation(block, "_top");
			Identifier model2 = ModelLocationUtils.getModelLocation(block, "_bottom");
			Identifier model3 = ModelLocationUtils.getModelLocation(block, "_open");
			generators.blockStateOutput.accept(BlockModelGenerators.createTrapdoor(
					block,
					plainVariant(model1),
					plainVariant(model2),
					plainVariant(model3)));
			generators.registerSimpleItemModel(block, model2);
			return true;
		}
		return false;
	}

	private static void createTrapdoor(Block trapdoorBlock, Block fullBlock, BlockModelGenerators generators) {
		TextureMapping mapping = TextureMapping.defaultTexture(fullBlock);
		Identifier model1 = ModelTemplates.TRAPDOOR_TOP.create(trapdoorBlock, mapping, generators.modelOutput);
		Identifier model2 = ModelTemplates.TRAPDOOR_BOTTOM.create(trapdoorBlock, mapping, generators.modelOutput);
		Identifier model3 = ModelTemplates.TRAPDOOR_OPEN.create(trapdoorBlock, mapping, generators.modelOutput);
		generators.blockStateOutput.accept(BlockModelGenerators.createTrapdoor(
				trapdoorBlock,
				plainVariant(model1),
				plainVariant(model2),
				plainVariant(model3)));
		generators.registerSimpleItemModel(trapdoorBlock, model2);
	}

	public static boolean createIfSpecialFence(Block block, BlockModelGenerators generators, BlockFamily family) {
		if (!TREATED_WOOD_FAMILIES.contains(family)) {
			return false;
		}
		TextureMapping postTextures = TextureMapping.defaultTexture(getBlockTexture(block, "_post"));
		postTextures.put(TextureSlot.SIDE, TextureMapping.getBlockTexture(block));
		Identifier post = XKDModelTemplates.WOODEN_FENCE_POST.create(
				block,
				postTextures,
				generators.modelOutput);
		Identifier side = XKDModelTemplates.WOODEN_FENCE_SIDE.create(
				block,
				TextureMapping.defaultTexture(block),
				generators.modelOutput);
		Identifier inventory = XKDModelTemplates.WOODEN_FENCE_INVENTORY.create(
				block,
				postTextures,
				generators.modelOutput);
		generators.blockStateOutput.accept(createFenceNoUvLock(block, post, side));
		generators.registerSimpleItemModel(block, inventory);
		return true;
	}

	public static BlockModelDefinitionGenerator createFenceNoUvLock(
			Block pFenceBlock,
			Identifier pFencePostModelLocation,
			Identifier pFenceSideModelLocation) {
		MultiVariant side = plainVariant(pFenceSideModelLocation);
		return MultiPartGenerator.multiPart(pFenceBlock)
				.with(plainVariant(pFencePostModelLocation))
				.with(BlockModelGenerators.condition().term(BlockStateProperties.NORTH, true), side)
				.with(BlockModelGenerators.condition().term(BlockStateProperties.EAST, true), side.with(BlockModelGenerators.Y_ROT_90))
				.with(BlockModelGenerators.condition().term(BlockStateProperties.SOUTH, true), side.with(BlockModelGenerators.Y_ROT_180))
				.with(BlockModelGenerators.condition().term(BlockStateProperties.WEST, true), side.with(BlockModelGenerators.Y_ROT_270));
	}

	public static boolean createIfSpecialFenceGate(Block block, BlockModelGenerators generators, BlockFamily family) {
		if (!TREATED_WOOD_FAMILIES.contains(family)) {
			return false;
		}
		TextureMapping textureMapping = TextureMapping.defaultTexture(block);
		Identifier $$1 = XKDModelTemplates.WOODEN_FENCE_GATE_OPEN.create(block, textureMapping, generators.modelOutput);
		Identifier $$2 = XKDModelTemplates.WOODEN_FENCE_GATE_CLOSED.create(block, textureMapping, generators.modelOutput);
		generators.blockStateOutput.accept(BlockModelGenerators.createFenceGate(
				block,
				plainVariant($$1),
				plainVariant($$2),
				plainVariant($$1),
				plainVariant($$2),
				false));
		return true;
	}

	public static boolean createIfSpecialStairs(Block block, TextureMapping mapping, BlockModelGenerators generators) {
		KBlockSettings settings = KBlockSettings.of(block);
		if (settings == null || settings.glassType == null) {
			return false;
		}
		mapping = mapping.copyAndUpdate(TextureSlot.SIDE, mapping.get(TextureSlot.ALL));
		Identifier straightModel = XKDModelTemplates.GLASS_STAIRS.create(block, mapping, generators.modelOutput);
		Identifier innerModel = XKDModelTemplates.GLASS_STAIRS_INNER.create(block, mapping, generators.modelOutput);
		Identifier outerModel = XKDModelTemplates.GLASS_STAIRS_OUTER.create(block, mapping, generators.modelOutput);
		generators.blockStateOutput.accept(BlockModelGenerators.createStairs(
				block,
				plainVariant(innerModel),
				plainVariant(straightModel),
				plainVariant(outerModel)));
		return true;
	}

	@Override
	protected void registerModels(BlockModelGenerators generators, ItemModelGenerators itemModelGenerators) {
		this.generators = generators;
		BuiltInRegistries.ITEM.listElements()
				.map(Holder::value)
				.filter(item -> BuiltInRegistries.ITEM.getKey(item).getNamespace().equals(XKDeco.ID))
				.filter(item -> !(item instanceof BlockItem))
				.forEach(itemModelGenerators::declareCustomModelItem);
		LEGACY_ITEM_MODEL_BLOCKS.forEach(block -> generators.registerSimpleItemModel(
				block,
				ModelLocationUtils.getModelLocation(block.asItem())));
		var originalBlockStateOutput = generators.blockStateOutput;
		generators.blockStateOutput = generator -> {
			generated.add(generator.block());
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
		var shapeConsumers = ImmutableMap.<BlockFamily.Variant, BiConsumer<BlockModelGenerators.BlockFamilyProvider, Block>>builder();
		BlockModelGenerators.SHAPE_CONSUMERS.forEach((variant, consumer) -> {
			if (variant != BlockFamily.Variant.SLAB
					&& variant != BlockFamily.Variant.STAIRS
					&& variant != BlockFamily.Variant.CUT
					&& variant != BlockFamily.Variant.POLISHED) {
				shapeConsumers.put(variant, consumer);
			}
		});
		BlockModelGenerators.SHAPE_CONSUMERS = shapeConsumers
				.put(BlockFamily.Variant.SLAB, (provider, block) -> slabWithGrassTintedItem(provider, block, generators))
				.put(BlockFamily.Variant.STAIRS, (provider, block) -> stairsWithGrassTintedItem(provider, block, generators))
				.put(BlockFamily.Variant.CUT, BlockModelGenerators.BlockFamilyProvider::fullBlockVariant)
				.put(BlockFamily.Variant.POLISHED, BlockModelGenerators.BlockFamilyProvider::fullBlockVariant)
				.build();

		var mayaCutStonebricks = block("maya_cut_stonebricks");
		var aztecCutStonebricks = block("aztec_cut_stonebricks");
		BlockModelGenerators.TEXTURED_MODELS = ImmutableMap.<Block, TexturedModel>builder()
				.putAll(BlockModelGenerators.TEXTURED_MODELS)
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
				Identifier blockModel = ModelTemplates.CUBE_COLUMN.create(
						baseBlock,
						textureMapping,
						generators.modelOutput);
				Identifier horizontalBlockModel = ModelTemplates.CUBE_COLUMN_HORIZONTAL.create(
						baseBlock,
						textureMapping,
						generators.modelOutput);
				BlockModelDefinitionGenerator generator = BlockModelGenerators.createRotatedPillarWithHorizontalVariant(
						baseBlock,
						plainVariant(blockModel),
						plainVariant(horizontalBlockModel));
				generators.blockStateOutput.accept(generator);
				provider.fullBlock = BlockModelGenerators.plainModel(blockModel);
			} else if (family == XKDBlockFamilies.CUT_BRONZE_BLOCK || family == XKDBlockFamilies.MAYA_POLISHED_STONEBRICKS) {
				// we have already generated the base model in other families
				TexturedModel texturedModel = BlockModelGenerators.TEXTURED_MODELS.getOrDefault(
						baseBlock,
						TexturedModel.CUBE.get(baseBlock));
				provider = generators.new BlockFamilyProvider(texturedModel.getMapping());
				provider.fullBlock = BlockModelGenerators.plainModel(ModelLocationUtils.getModelLocation(baseBlock));
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
		Identifier hangingWillowLeavesItem = generators.createFlatItemModelWithBlockTexture(
				block("hanging_willow_leaves").asItem(),
				block("hanging_willow_leaves"));
		registerTintedBlockItemModel("hanging_willow_leaves", hangingWillowLeavesItem, foliageItemTint());

		Material dirtTexture = getBlockTexture(Blocks.DIRT);
		Material netherrackTexture = getBlockTexture(Blocks.NETHERRACK);
		TextureMapping snowyMapping = new TextureMapping()
				.put(TextureSlot.BOTTOM, dirtTexture)
				.copyForced(TextureSlot.BOTTOM, TextureSlot.PARTICLE)
				.put(TextureSlot.TOP, getBlockTexture(Blocks.GRASS_BLOCK, "_top"))
				.put(TextureSlot.SIDE, getBlockTexture(Blocks.GRASS_BLOCK, "_snow"));
		ModelTemplates.SLAB_TOP.create(snowySlabTop, snowyMapping, generators.modelOutput);
		createSlab(Blocks.DIRT, false, false, UnaryOperator.identity());
		createSlab(Blocks.DIRT_PATH, true, true, $ -> $.put(TextureSlot.BOTTOM, dirtTexture));
		createSlab(Blocks.GRASS_BLOCK, true, true, UnaryOperator.identity());
		createSlab(
				Blocks.MYCELIUM, true, true, $ -> $
						.put(TextureSlot.BOTTOM, dirtTexture)
						.put(TextureSlot.TOP, getBlockTexture(Blocks.MYCELIUM, "_top")));
		createSlab(Blocks.NETHERRACK, false, false, UnaryOperator.identity());
		createSlab(
				Blocks.PODZOL, true, true, $ -> $
						.put(TextureSlot.BOTTOM, dirtTexture)
						.put(TextureSlot.TOP, getBlockTexture(Blocks.PODZOL, "_top")));
		createSlab(Blocks.CRIMSON_NYLIUM, true, true, $ -> $.put(TextureSlot.BOTTOM, netherrackTexture));
		createSlab(Blocks.WARPED_NYLIUM, true, true, $ -> $.put(TextureSlot.BOTTOM, netherrackTexture));
		createSlab(Blocks.END_STONE, false, false, UnaryOperator.identity());

		createTreatedWood("varnished");
		createTreatedWood("ebony");
		createTreatedWood("mahogany");
		AirDuctModel airDuctModel = new AirDuctModel(
				XKDeco.id("block/furniture/air_duct"),
				XKDeco.id("block/furniture/air_duct_corner"),
				XKDeco.id("block/furniture/air_duct_cover"),
				XKDeco.id("block/furniture/air_duct_frame"));
		generators.blockStateOutput.accept(MultiVariantGenerator.dispatch(
				block("air_duct"),
				MultiVariant.of(new CustomBlockStateModelBuilder.Simple(airDuctModel))));
		generators.registerSimpleItemModel(block("air_duct"), XKDeco.id("block/furniture/air_duct_corner"));
		createHorizontalShift("air_duct_oblique", "air_duct_oblique", null, false);
		generators.blockStateOutput.accept(BlockModelGenerators.createWall(
				block("hollow_steel_beam"),
				plainVariant(XKDeco.id("block/furniture/hollow_steel_beam_post")),
				plainVariant(XKDeco.id("block/furniture/hollow_steel_beam_side")),
				plainVariant(XKDeco.id("block/furniture/hollow_steel_beam_side_tall"))));
		generators.registerSimpleItemModel(block("hollow_steel_beam"), XKDeco.id("block/furniture/hollow_steel_beam_inventory"));
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
				plainVariant(XKDeco.id("block/furniture/dark_stone_handrail_post")),
				plainVariant(XKDeco.id("block/furniture/dark_stone_handrail")),
				plainVariant(XKDeco.id("block/furniture/dark_stone_handrail_side_tall"))));
		generators.registerSimpleItemModel(block("dark_stone_handrail"), XKDeco.id("block/furniture/dark_stone_handrail_inventory"));
		generators.blockStateOutput.accept(BlockModelGenerators.createWall(
				block("light_stone_handrail"),
				plainVariant(XKDeco.id("block/furniture/light_stone_handrail_post")),
				plainVariant(XKDeco.id("block/furniture/light_stone_handrail")),
				plainVariant(XKDeco.id("block/furniture/light_stone_handrail_side_tall"))));
		generators.registerSimpleItemModel(block("light_stone_handrail"), XKDeco.id("block/furniture/light_stone_handrail_inventory"));

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
		createBlockStateOnly("stone_water_bowl", "furniture/", false);
		createBlockStateOnly("stone_water_tank", "furniture/", false);
		createBlockStateOnly("candlestick", "furniture/", true);
		createBlockStateOnly("big_candlestick", "furniture/", true);
		createBlockStateOnly("tech_table", "furniture/", true);
		createBlockStateOnly("tech_table_circle", "furniture/", true);

		createMoulding("egyptian_moulding", "furniture/egyptian_moulding", false, true);
		createMoulding("egyptian_moulding2", "furniture/egyptian_moulding2", false, true);
		createMoulding("greek_moulding", "furniture/greek_moulding", false, true);
		createMoulding("greek_moulding2", "furniture/greek_moulding2", false, true);
		createMoulding("roman_moulding", "furniture/roman_moulding", false, true);
		createMoulding("roman_moulding2", "furniture/roman_moulding2", false, true);
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

		generators.registerSimpleItemModel(block("empty_candlestick"), XKDeco.id("block/furniture/empty_candlestick"));

		createBlockStateOnly("calligraphy", 2);
		createBlockStateOnly("ink_painting", 2);
		createBlockStateOnly("weiqi_board", 2);
		createBlockStateOnly("xiangqi_board", 2);
		registerColorProviderItemModels();

		outer:
		for (Item item : GameObjectLookup.all(BuiltInRegistries.ITEM, XKDeco.ID).toList()) {
			Block block = Block.byItem(item);
			if (block == Blocks.AIR || GADGET_SKIP_BLOCKS.contains(block) || generated.contains(block)) {
				continue;
			}
			var id = BuiltInRegistries.BLOCK.getKey(block);
			if (block instanceof ItemDisplayBlock || block instanceof BlockDisplayBlock) {
				createBlockStateOnly(id.getPath(), "furniture/", true);
				continue;
			}
			if (block instanceof HologramBlock) {
				generators.createParticleOnlyBlock(block);
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

	private void registerColorProviderItemModels() {
		ItemTintSource foliageTint = foliageItemTint();
		ItemTintSource grassTint = new GrassColorSource();
		ItemTintSource waterTint = ItemModelUtils.constantTint(DEFAULT_WATER_ITEM_COLOR);
		FOLIAGE_TINTED_ITEM_MODELS.forEach(id -> registerTintedBlockItemModel(id, "", foliageTint));
		GRASS_DIRECT_TINTED_ITEM_MODELS.forEach(id -> registerTintedBlockItemModel(id, "", grassTint));
		WATER_TINTED_ITEM_MODELS.forEach(id -> registerTintedBlockItemModel(id, "furniture/", waterTint));
	}

	private static void slabWithGrassTintedItem(BlockModelGenerators.BlockFamilyProvider provider, Block slab, BlockModelGenerators generators) {
		if (!isGrassFamilyTintedItemModel(slab)) {
			provider.slab(slab);
			return;
		}
		String path = BuiltInRegistries.BLOCK.getKey(slab).getPath();
		Identifier bottom = ModelLocationUtils.getModelLocation(slab);
		Identifier top = ModelLocationUtils.getModelLocation(slab, "_top");
		Identifier full = XKDeco.id(path.substring(0, path.length() - "_slab".length())).withPrefix("block/");
		generators.blockStateOutput.accept(BlockModelGenerators.createSlab(slab, plainVariant(bottom), plainVariant(top), plainVariant(full)));
		generators.registerSimpleTintedItemModel(slab, bottom, new GrassColorSource());
	}

	private static void stairsWithGrassTintedItem(BlockModelGenerators.BlockFamilyProvider provider, Block stairs, BlockModelGenerators generators) {
		if (!isGrassFamilyTintedItemModel(stairs)) {
			provider.stairs(stairs);
			return;
		}
		Identifier inner = ModelLocationUtils.getModelLocation(stairs, "_inner");
		Identifier straight = ModelLocationUtils.getModelLocation(stairs);
		Identifier outer = ModelLocationUtils.getModelLocation(stairs, "_outer");
		generators.blockStateOutput.accept(BlockModelGenerators.createStairs(
				stairs,
				plainVariant(inner),
				plainVariant(straight),
				plainVariant(outer)));
		generators.registerSimpleTintedItemModel(stairs, straight, new GrassColorSource());
	}

	private static boolean isGrassFamilyTintedItemModel(Block block) {
		return GRASS_FAMILY_TINTED_ITEM_MODELS.contains(BuiltInRegistries.BLOCK.getKey(block).getPath());
	}

	private static ItemTintSource foliageItemTint() {
		return ItemModelUtils.constantTint(FoliageColor.FOLIAGE_DEFAULT);
	}

	private void registerTintedBlockItemModel(String id, String prefix, ItemTintSource tint) {
		registerTintedBlockItemModel(id, XKDeco.id(id).withPrefix("block/" + prefix), tint);
	}

	private void registerTintedBlockItemModel(String id, Identifier modelLocation, ItemTintSource tint) {
		generators.registerSimpleTintedItemModel(block(id), modelLocation, tint);
	}

	private void createNonRotatedPillar(String id) {
		Block block = block(id);
		TextureMapping mapping = TextureMapping.column(block);
		ModelTemplates.CUBE_COLUMN.create(block, mapping, generators.modelOutput);
		createBlockStateOnly(id, true);
	}

	private void createWall(String id, String texture) {
		Block block = block(id);
		TextureMapping mapping = new TextureMapping().put(TextureSlot.WALL, new Material(XKDeco.id("block/" + texture)));
		Identifier $$1 = ModelTemplates.WALL_POST.create(block, mapping, generators.modelOutput);
		Identifier $$2 = ModelTemplates.WALL_LOW_SIDE.create(block, mapping, generators.modelOutput);
		Identifier $$3 = ModelTemplates.WALL_TALL_SIDE.create(block, mapping, generators.modelOutput);
		generators.blockStateOutput.accept(BlockModelGenerators.createWall(block, plainVariant($$1), plainVariant($$2), plainVariant($$3)));
		Identifier $$4 = ModelTemplates.WALL_INVENTORY.create(block, mapping, generators.modelOutput);
		generators.registerSimpleItemModel(block, $$4);
	}

	private void createInscriptionBronzeBlock() {
		ModelTemplates.CUBE_ALL.create(
				XKDeco.id("block/inscription_bronze_block"),
				TextureMapping.cube(new Material(XKDeco.id("block/inscription_bronze_block"))),
				generators.modelOutput);

		ModelTemplates.CUBE_ALL.create(
				XKDeco.id("block/inscription_bronze_block1"),
				TextureMapping.cube(new Material(XKDeco.id("block/inscription_bronze_block1"))),
				generators.modelOutput);

		ModelTemplates.CUBE_ALL.create(
				XKDeco.id("block/inscription_bronze_block2"),
				TextureMapping.cube(new Material(XKDeco.id("block/inscription_bronze_block2"))),
				generators.modelOutput);

		generators.blockStateOutput.accept(MultiPartGenerator.multiPart(block("inscription_bronze_block"))
				.with(plainVariant(XKDeco.id("block/inscription_bronze_block")))
				.with(plainVariant(XKDeco.id("block/inscription_bronze_block1")))
				.with(plainVariant(XKDeco.id("block/inscription_bronze_block2"))));
	}

	private void createSingleScrewState(String id) {
		var mayaSingleScrewThreadStone = block(id);
		generators.blockStateOutput.accept(MultiVariantGenerator.dispatch(mayaSingleScrewThreadStone)
				.with(PropertyDispatch.initial(BlockStateProperties.HORIZONTAL_FACING)
						.select(
								Direction.NORTH,
								plainVariant(XKDeco.id("block/" + id)))
						.select(
								Direction.SOUTH,
								plainVariant(XKDeco.id("block/" + id + "_s")).with(BlockModelGenerators.Y_ROT_180))
						.select(
								Direction.WEST,
								plainVariant(XKDeco.id("block/" + id + "_w")).with(BlockModelGenerators.Y_ROT_270))
						.select(
								Direction.EAST,
								plainVariant(XKDeco.id("block/" + id + "_e")).with(BlockModelGenerators.Y_ROT_90))
				));
	}

	private void createFallenLeaves(String id) {
		Block leaves = block(id);
		Block fallenLeaves = block("fallen_" + id);
		TextureMapping textureMapping = TextureMapping.cube(leaves);
		Identifier model0 = XKDModelTemplates.FALLEN_LEAVES.create(fallenLeaves, textureMapping, generators.modelOutput);
		Identifier model1 = XKDModelTemplates.FALLEN_LEAVES_SLAB.create(fallenLeaves, textureMapping, generators.modelOutput);
		var generator = MultiVariantGenerator.dispatch(fallenLeaves)
				.with(PropertyDispatch.initial(property(fallenLeaves, HALF))
						.select("upper", plainVariant(model0))
						.select("lower", plainVariant(model1)));
		generators.blockStateOutput.accept(generator);
	}

	private void createIronBarsLike(String id, String paneTexture, String edgeTexture) {
		Block block = block(id);
		TextureMapping texturemapping = new TextureMapping()
				.put(TextureSlot.PANE, new Material(XKDeco.id(paneTexture).withPrefix("block/")))
				.put(TextureSlot.EDGE, new Material(XKDeco.id(edgeTexture).withPrefix("block/")));
		Identifier resourcelocation = ModelTemplates.STAINED_GLASS_PANE_POST.create(block, texturemapping, generators.modelOutput);
		Identifier resourcelocation1 = ModelTemplates.STAINED_GLASS_PANE_SIDE.create(block, texturemapping, generators.modelOutput);
		Identifier resourcelocation2 = ModelTemplates.STAINED_GLASS_PANE_SIDE_ALT.create(
				block,
				texturemapping,
				generators.modelOutput);
		Identifier resourcelocation3 = ModelTemplates.STAINED_GLASS_PANE_NOSIDE.create(block, texturemapping, generators.modelOutput);
		Identifier resourcelocation4 = ModelTemplates.STAINED_GLASS_PANE_NOSIDE_ALT.create(
				block,
				texturemapping,
				generators.modelOutput);
		Item item = block.asItem();
		ModelTemplates.FLAT_ITEM.create(
				ModelLocationUtils.getModelLocation(item),
				TextureMapping.layer0(texturemapping.get(TextureSlot.PANE)),
				generators.modelOutput);
		generators.blockStateOutput.accept(MultiPartGenerator.multiPart(block)
				.with(plainVariant(resourcelocation))
				.with(
						BlockModelGenerators.condition().term(BlockStateProperties.NORTH, true),
						plainVariant(resourcelocation1))
				.with(
						BlockModelGenerators.condition().term(BlockStateProperties.EAST, true),
						plainVariant(resourcelocation1).with(BlockModelGenerators.Y_ROT_90))
				.with(
						BlockModelGenerators.condition().term(BlockStateProperties.SOUTH, true),
						plainVariant(resourcelocation2))
				.with(
						BlockModelGenerators.condition().term(BlockStateProperties.WEST, true),
						plainVariant(resourcelocation2).with(BlockModelGenerators.Y_ROT_90))
				.with(
						BlockModelGenerators.condition().term(BlockStateProperties.NORTH, false),
						plainVariant(resourcelocation3))
				.with(
						BlockModelGenerators.condition().term(BlockStateProperties.EAST, false),
						plainVariant(resourcelocation4))
				.with(
						BlockModelGenerators.condition().term(BlockStateProperties.SOUTH, false),
						plainVariant(resourcelocation4).with(BlockModelGenerators.Y_ROT_90))
				.with(
						BlockModelGenerators.condition().term(BlockStateProperties.WEST, false),
						plainVariant(resourcelocation3).with(BlockModelGenerators.Y_ROT_270)));
	}

	private void createMouldingWithModels(String id, String template, TextureMapping mapping, boolean itemModel) {
		Block block = block(id);
		XKDModelTemplates.MAP.get(template).create(block, mapping, generators.modelOutput);
		XKDModelTemplates.MAP.get(template + "_inner").create(block, mapping, generators.modelOutput);
		XKDModelTemplates.MAP.get(template + "_outer").create(block, mapping, generators.modelOutput);
		createMoulding(id, id, false, itemModel);
	}

	private void createMoulding(String id, String model, boolean uvLock, boolean itemModel, VariantMutator... baseMutators) {
		Block block = block(id);
		Identifier pStraightModelLocation = XKDeco.id("block/" + model);
		Identifier pOuterModelLocation = XKDeco.id("block/" + model + "_outer");
		Identifier pInnerModelLocation = XKDeco.id("block/" + model + "_inner");
		if (itemModel) {
			generators.registerSimpleItemModel(block, pStraightModelLocation);
		}
		VariantMutator base = combine(baseMutators);
		MultiVariant straight = plainVariant(pStraightModelLocation).with(base);
		MultiVariant outer = plainVariant(pOuterModelLocation).with(base);
		MultiVariant inner = plainVariant(pInnerModelLocation).with(base);
		var generator = MultiVariantGenerator.dispatch(block)
				.with(PropertyDispatch.initial(
								BlockStateProperties.HORIZONTAL_FACING,
								BlockStateProperties.STAIRS_SHAPE)
						.select(Direction.EAST, StairsShape.STRAIGHT, straight)
						.select(Direction.WEST, StairsShape.STRAIGHT, withUvLock(straight.with(BlockModelGenerators.Y_ROT_180), uvLock))
						.select(Direction.SOUTH, StairsShape.STRAIGHT, withUvLock(straight.with(BlockModelGenerators.Y_ROT_90), uvLock))
						.select(Direction.NORTH, StairsShape.STRAIGHT, withUvLock(straight.with(BlockModelGenerators.Y_ROT_270), uvLock))
						.select(Direction.EAST, StairsShape.OUTER_RIGHT, outer)
						.select(Direction.WEST, StairsShape.OUTER_RIGHT, withUvLock(outer.with(BlockModelGenerators.Y_ROT_180), uvLock))
						.select(Direction.SOUTH, StairsShape.OUTER_RIGHT, withUvLock(outer.with(BlockModelGenerators.Y_ROT_90), uvLock))
						.select(Direction.NORTH, StairsShape.OUTER_RIGHT, withUvLock(outer.with(BlockModelGenerators.Y_ROT_270), uvLock))
						.select(Direction.EAST, StairsShape.OUTER_LEFT, withUvLock(outer.with(BlockModelGenerators.Y_ROT_270), uvLock))
						.select(Direction.WEST, StairsShape.OUTER_LEFT, withUvLock(outer.with(BlockModelGenerators.Y_ROT_90), uvLock))
						.select(Direction.SOUTH, StairsShape.OUTER_LEFT, outer)
						.select(Direction.NORTH, StairsShape.OUTER_LEFT, withUvLock(outer.with(BlockModelGenerators.Y_ROT_180), uvLock))
						.select(Direction.EAST, StairsShape.INNER_RIGHT, inner)
						.select(Direction.WEST, StairsShape.INNER_RIGHT, withUvLock(inner.with(BlockModelGenerators.Y_ROT_180), uvLock))
						.select(Direction.SOUTH, StairsShape.INNER_RIGHT, withUvLock(inner.with(BlockModelGenerators.Y_ROT_90), uvLock))
						.select(Direction.NORTH, StairsShape.INNER_RIGHT, withUvLock(inner.with(BlockModelGenerators.Y_ROT_270), uvLock))
						.select(Direction.EAST, StairsShape.INNER_LEFT, withUvLock(inner.with(BlockModelGenerators.Y_ROT_270), uvLock))
						.select(Direction.WEST, StairsShape.INNER_LEFT, withUvLock(inner.with(BlockModelGenerators.Y_ROT_90), uvLock))
						.select(Direction.SOUTH, StairsShape.INNER_LEFT, inner)
						.select(Direction.NORTH, StairsShape.INNER_LEFT, withUvLock(inner.with(BlockModelGenerators.Y_ROT_180), uvLock)));
		generators.blockStateOutput.accept(generator);
	}

	private static VariantMutator combine(VariantMutator... mutators) {
		VariantMutator result = BlockModelGenerators.NOP;
		for (VariantMutator mutator : mutators) {
			result = result.then(mutator);
		}
		return result;
	}

	private static MultiVariant withUvLock(MultiVariant variant, boolean uvLock) {
		return uvLock ? variant.with(BlockModelGenerators.UV_LOCK) : variant;
	}

	private void createTreatedWood(String id) {
		Block log = block(id + "_log");
		TextureMapping logMapping = generators.woodProvider(log).log(log).wood(block(id + "_wood")).logMapping;
		Block slab = block(id + "_log_slab");
		Identifier $$2 = ModelTemplates.SLAB_BOTTOM.create(slab, logMapping, generators.modelOutput);
		Identifier $$3 = ModelTemplates.SLAB_TOP.create(slab, logMapping, generators.modelOutput);
		generators.blockStateOutput.accept(BlockModelGenerators.createSlab(
				slab,
				plainVariant($$2),
				plainVariant($$3),
				plainVariant(ModelLocationUtils.getModelLocation(log))));

		createTrivialBlock(id + "_table", XKDModelTemplates.WOODEN_TABLE_PROVIDER);
		createTrivialBlock(id + "_big_table", XKDModelTemplates.WOODEN_BIG_TABLE_PROVIDER);
		createTrivialBlock(id + "_tall_table", XKDModelTemplates.WOODEN_TALL_TABLE_PROVIDER);
		createHorizontalAxis(id + "_desk", XKDModelTemplates.WOODEN_DESK_PROVIDER);
//		createHorizontallyRotatedBlock(id + "_desk", XKDModelTemplates.WOODEN_DESK_PROVIDER);
		createGadget(block(id + "_chair"));
		createHorizontalAxis(id + "_stool", XKDModelTemplates.WOODEN_STOOL_PROVIDER);

		TextureMapping textureMapping = logMapping.copyAndUpdate(TextureSlot.WALL, new Material(XKDeco.id("block/" + id + "_smooth")));
		textureMapping.put(XKDModelTemplates.PLANKS, new Material(XKDeco.id("block/" + id + "_planks")));
		createWoodenWall(id + "_column_wall", "wooden_column_wall", textureMapping);
		createWoodenWall("hollow_" + id + "_column_wall", "hollow_wooden_column_wall", textureMapping);
		createWoodenWall(id + "_wall", "wooden_wall", textureMapping);

		createMoulding(id + "_meiren_kao", "furniture/" + id + "_meiren_kao", false, true);
		createMoulding(id + "_meiren_kao_with_column", "furniture/" + id + "_meiren_kao_with_column", false, true);

		textureMapping = new TextureMapping().put(TextureSlot.SIDE, new Material(XKDeco.id("block/" + id + "_smooth")));
		createMouldingWithModels(id + "_dougong", "template_dougong", textureMapping, true);
		createMouldingWithModels(id + "_dougong_connection", "template_dougong_connection", textureMapping, true);
		createMouldingWithModels(id + "_dougong_hollow_connection", "template_dougong_hollow_connection", textureMapping, true);

		createWoodenFenceHead(id);
		createHangingFascia(id);
		createHorizontallyRotatedBlock(id + "_oblique_brace", XKDModelTemplates.WOODEN_OBLIQUE_BRACE_PROVIDER);

		textureMapping = TextureMapping.defaultTexture(block(id + "_trapdoor"));
		textureMapping.put(TextureSlot.PARTICLE, getBlockTexture(block(id + "_window")));
		textureMapping.put(TextureSlot.TOP, new Material(XKDeco.id("block/" + id + "_narrow_doors_top")));
		textureMapping.put(TextureSlot.BOTTOM, new Material(XKDeco.id("block/" + id + "_narrow_doors_bottom")));
		createWoodenFenceGate(id + "_window", "wooden_window", textureMapping);
		createWoodenFenceGate(id + "_awning_window", "wooden_awning_window", textureMapping);
		createWoodenFenceGate(id + "_narrow_doors", "wooden_narrow_doors", textureMapping);
		Identifier narrowDoors = ModelLocationUtils.getModelLocation(block(id + "_narrow_doors").asItem());
		ModelTemplates.FLAT_ITEM.create(
				narrowDoors,
				TextureMapping.layer0(new Material(narrowDoors)),
				generators.modelOutput);

		Block columnHead = block(id + "_column_head");
		XKDModelTemplates.WOODEN_COLUMN_HEAD.create(columnHead, TextureMapping.particle(columnHead), generators.modelOutput);
		createDirectional(id + "_column_head", "");
		generators.registerSimpleFlatItemModel(block(id + "_screen").asItem());

		Block fenceOblique = block(id + "_fence_oblique");
		textureMapping = TextureMapping.particle(fenceOblique).put(
				XKDModelTemplates.POST,
				new Material(XKDeco.id("block/" + id + "_fence_post")));
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
		Identifier modelLocation = BuiltInRegistries.BLOCK.getKey(block).withPrefix("block/");
		List<Variant> variants = Lists.newArrayList(BlockModelGenerators.plainModel(modelLocation));
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
				variants.add(BlockModelGenerators.plainModel(modelLocation.withSuffix(suffix)));
			}
		}
		generators.blockStateOutput.accept(MultiVariantGenerator.dispatch(
						block,
						BlockModelGenerators.variants(variants.toArray(Variant[]::new)))
				.with(BlockModelGenerators.ROTATION_HORIZONTAL_FACING));
		generators.registerSimpleItemModel(block, modelLocation);
	}

	private void createWoodenFenceGate(String id, String template, TextureMapping mapping) {
		Block block = block(id);
		Identifier open = XKDModelTemplates.MAP.get(template + "_open").create(block, mapping, generators.modelOutput);
		Identifier closed = XKDModelTemplates.MAP.get(template).create(block, mapping, generators.modelOutput);
		generators.blockStateOutput.accept(BlockModelGenerators.createFenceGate(
				block,
				plainVariant(open),
				plainVariant(closed),
				plainVariant(open),
				plainVariant(closed),
				false));
	}

	private void createHorizontalAxis(String id, TexturedModel.Provider provider) {
		Block block = block(id);
		Identifier model = provider.create(block, generators.modelOutput);
		generators.blockStateOutput.accept(MultiVariantGenerator.dispatch(
						block,
						plainVariant(model))
				.with(PropertyDispatch.modify(BlockStateProperties.HORIZONTAL_AXIS)
						.select(Direction.Axis.X, BlockModelGenerators.NOP)
						.select(Direction.Axis.Z, BlockModelGenerators.Y_ROT_90)));
	}

	private void createHangingFascia(String id) {
		Block block = block(id + "_hanging_fascia");
		TextureMapping mapping = TextureMapping.particle(getBlockTexture(block, "_side"));
		Identifier sideModel = XKDModelTemplates.HANGING_FASCIA_SIDE.create(block, mapping, generators.modelOutput);
		mapping = TextureMapping.particle(getBlockTexture(block, "_middle"));
		Identifier middleModel = XKDModelTemplates.HANGING_FASCIA_MIDDLE.create(block, mapping, generators.modelOutput);
		var generator = MultiVariantGenerator.dispatch(block)
				.with(PropertyDispatch.initial(HangingFasciaBlock.SIDE, BlockStateProperties.HORIZONTAL_AXIS)
						.generate((side, axis) -> {
							int rotation = 0;
							MultiVariant variant;
							if (side == HangingFasciaBlock.Side.NONE) {
								variant = plainVariant(middleModel);
							} else {
								variant = plainVariant(sideModel);
								if (side == HangingFasciaBlock.Side.POSITIVE) {
									rotation += 180;
								}
							}
							if (axis == Direction.Axis.Z) {
								rotation += 90;
							}
							return variant.with(VariantMutator.Y_ROT.withValue(quadrant(rotation)));
						}));
		generators.blockStateOutput.accept(generator);
		generators.registerSimpleItemModel(block, sideModel);
	}

	private void createWoodenFenceHead(String id) {
		Block block = block(id + "_fence_head");
		TextureMapping mapping = TextureMapping.particle(getBlockTexture(block(id + "_fence"), "_post"));
		Identifier model = XKDModelTemplates.WOODEN_FENCE_HEAD.create(block, mapping, generators.modelOutput);
		Identifier flipModel = XKDModelTemplates.WOODEN_FENCE_HEAD_FLIP.create(block, mapping, generators.modelOutput);
		var generator = MultiVariantGenerator.dispatch(block)
				.with(PropertyDispatch.initial(BlockStateProperties.FACING)
						.select(Direction.DOWN, plainVariant(flipModel).with(BlockModelGenerators.X_ROT_90))
						.select(Direction.UP, plainVariant(model).with(BlockModelGenerators.X_ROT_270))
						.select(Direction.NORTH, plainVariant(model))
						.select(Direction.SOUTH, plainVariant(model).with(BlockModelGenerators.Y_ROT_180))
						.select(Direction.WEST, plainVariant(model).with(BlockModelGenerators.Y_ROT_270))
						.select(Direction.EAST, plainVariant(model).with(BlockModelGenerators.Y_ROT_90)));
		generators.blockStateOutput.accept(generator);
		generators.registerSimpleItemModel(block, model);
	}

	private void createWoodenWall(String id, String templateId, TextureMapping textureMapping) {
		Block block = block(id);
		Identifier side = XKDModelTemplates.MAP.get(templateId + "_side").create(block, textureMapping, generators.modelOutput);
		Identifier post = XKDModelTemplates.MAP.get(templateId + "_post").create(block, textureMapping, generators.modelOutput);
		Identifier tallSide = XKDModelTemplates.MAP.get(templateId + "_side_tall").create(
				block,
				textureMapping,
				generators.modelOutput);
		Identifier inventory = XKDModelTemplates.MAP.get(templateId + "_inventory").create(
				block,
				textureMapping,
				generators.modelOutput);
		generators.blockStateOutput.accept(BlockModelGenerators.createWall(
				block,
				plainVariant(post),
				plainVariant(side),
				plainVariant(tallSide)));
		generators.registerSimpleItemModel(block, inventory);
	}

	private void createTrivialBlock(String id, TexturedModel.Provider provider) {
		Block block = block(id);
		Identifier model = provider.create(block, generators.modelOutput);
		generators.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(block, plainVariant(model)));
	}

	private void createHorizontallyRotatedBlock(String id, TexturedModel.Provider provider) {
		Block block = block(id);
		Identifier model = provider.create(block, generators.modelOutput);
		generators.blockStateOutput.accept(MultiVariantGenerator.dispatch(
				block,
				plainVariant(model)).with(BlockModelGenerators.ROTATION_HORIZONTAL_FACING));
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
			Identifier model = id.withPrefix("block/furniture/");
			MultiVariantGenerator generator;
			if (layered == null) {
				generator = MultiVariantGenerator.dispatch(block, plainVariant(model))
						.with(BlockModelGenerators.ROTATION_HORIZONTAL_FACING);
				generators.registerSimpleItemModel(block, model);
			} else {
				generator = MultiVariantGenerator.dispatch(block)
						.with(PropertyDispatch.initial(layered.getLayerProperty())
								.generate(layer -> plainVariant(model.withSuffix("_" + layer))))
						.with(BlockModelGenerators.ROTATION_HORIZONTAL_FACING);
				generators.registerSimpleItemModel(block, model.withSuffix("_" + layered.getDefaultLayer()));
			}
			generators.blockStateOutput.accept(generator);
		} else if (settings.hasComponent(KBlockComponents.DIRECTIONAL.get())) {
			createDirectional(id.getPath(), "furniture/");
		} else if (settings.hasComponent(KBlockComponents.FRONT_AND_TOP.get())) {
			Identifier model;
			if (id.getPath().startsWith("screen_")) {
				TextureMapping mapping = TextureMapping.particle(block);
				model = XKDModelTemplates.SCREEN.create(block, mapping, generators.modelOutput);
			} else {
				model = id.withPrefix("block/furniture/");
			}
			var generator = MultiVariantGenerator.dispatch(block, plainVariant(model))
					.with(PropertyDispatch.modify(BlockStateProperties.ORIENTATION)
							.generate(BlockModelGenerators::applyRotation));
			generators.registerSimpleItemModel(block, model);
			generators.blockStateOutput.accept(generator);
		} else if (layered != null) {
			Identifier model = id.withPrefix("block/furniture/");
			var generator = MultiVariantGenerator.dispatch(block)
					.with(PropertyDispatch.initial(layered.getLayerProperty())
							.generate(layer -> plainVariant(model.withSuffix("_" + layer))));
			generators.registerSimpleItemModel(block, model.withSuffix("_" + layered.getDefaultLayer()));
			generators.blockStateOutput.accept(generator);
		}
	}

	private void createDirectional(String id, String prefix) {
		Block block = block(id);
		Identifier model = XKDeco.id(id).withPrefix("block/" + prefix);
		generators.blockStateOutput.accept(MultiVariantGenerator.dispatch(
						block,
						plainVariant(model))
				.with(BlockModelGenerators.ROTATION_FACING));
		generators.registerSimpleItemModel(block, model);
	}

	private void createHorizontal(String id, String prefix) {
		Block block = block(id);
		Identifier model = XKDeco.id(id).withPrefix("block/" + prefix);
		generators.blockStateOutput.accept(MultiVariantGenerator.dispatch(
						block,
						plainVariant(model))
				.with(BlockModelGenerators.ROTATION_HORIZONTAL_FACING));
		generators.registerSimpleItemModel(block, model);
	}

	private void createSlab(Block fullBlock, boolean sided, boolean natural, UnaryOperator<TextureMapping> textureMappingOperator) {
		Identifier id = BuiltInRegistries.BLOCK.getKey(fullBlock);
		Block slab = block(id.getPath() + "_slab");
		TextureMapping textureMapping = TextureMapping.cube(fullBlock);
		if (sided) {
			textureMapping.put(TextureSlot.SIDE, getBlockTexture(fullBlock, "_side"));
		}
		textureMapping = textureMappingOperator.apply(textureMapping);
		ModelTemplate bottomTemplate = natural ? XKDModelTemplates.NATURAL_SLAB : ModelTemplates.SLAB_BOTTOM;
		Identifier bottomModel = bottomTemplate.create(slab, textureMapping, generators.modelOutput);
		Identifier topModel = ModelTemplates.SLAB_TOP.create(slab, textureMapping, generators.modelOutput);
		Identifier fullModel = ModelLocationUtils.getModelLocation(fullBlock);
		BlockModelDefinitionGenerator generator;
		if (slab.defaultBlockState().hasProperty(BlockStateProperties.SNOWY)) {
			generator = MultiVariantGenerator.dispatch(slab).with(PropertyDispatch.initial(
							BlockStateProperties.SNOWY,
							BlockStateProperties.SLAB_TYPE)
					.select(true, SlabType.BOTTOM, plainVariant(bottomModel))
					.select(true, SlabType.TOP, plainVariant(snowySlabTop))
					.select(true, SlabType.DOUBLE, plainVariant(snowySlabDouble))
					.select(false, SlabType.BOTTOM, plainVariant(bottomModel))
					.select(false, SlabType.TOP, plainVariant(topModel))
					.select(false, SlabType.DOUBLE, plainVariant(fullModel)));
		} else {
			generator = BlockModelGenerators.createSlab(slab, plainVariant(bottomModel), plainVariant(topModel), plainVariant(fullModel));
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
		Identifier modelLocation = BuiltInRegistries.BLOCK.getKey(block).withPrefix("block/" + prefix);
		List<Variant> variants = Lists.newArrayList(BlockModelGenerators.plainModel(modelLocation));
		if (randomVariants > 1) {
			for (int i = 1; i < randomVariants; i++) {
				variants.add(BlockModelGenerators.plainModel(modelLocation.withSuffix("_" + (i + 1))));
			}
		}
		MultiVariant multiVariant = BlockModelGenerators.variants(variants.toArray(Variant[]::new));
		if (settings != null && settings.hasComponent(KBlockComponents.HORIZONTAL.get())) {
			generators.blockStateOutput.accept(MultiVariantGenerator.dispatch(block, multiVariant)
					.with(BlockModelGenerators.ROTATION_HORIZONTAL_FACING));
		} else {
			generators.blockStateOutput.accept(MultiVariantGenerator.dispatch(block, multiVariant));
		}
		if (delegateItem) {
			generators.registerSimpleItemModel(block, modelLocation);
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
		Material roofTexture = getBlockTexture(block(id));
		Material ridgeTexture = getBlockTexture(block(id), "_ridge");
		Material smallRidgeTexture = getBlockTexture(block(id), "_small_ridge");
		createRoofNormal(id, roofTexture, ridgeTexture);
		createRoofRidge(id + "_ridge", roofTexture, ridgeTexture, asian);
		createRoofFlat(id + "_flat", roofTexture);
		createRoofEave(id + "_small_eave", roofTexture, ridgeTexture, true);
		createRoofEnd(id + "_small_end", roofTexture, asian ? smallRidgeTexture : ridgeTexture, true);
		createRoofRidgeEnd(id + "_small_ridge_end", roofTexture, ridgeTexture, smallRidgeTexture, true, asian);
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
		createRoofRidgeEnd(id + "_ridge_end", roofTexture, ridgeTexture, smallRidgeTexture, false, asian);
		generators.registerSimpleItemModel(block(id + "_deco"), ModelLocationUtils.getModelLocation(block(id + "_deco")));
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
		Identifier model0 = XKDModelTemplates.MAP.get(templateId).create(
				block,
				textureMapping,
				generators.modelOutput);
		Identifier model1 = XKDModelTemplates.MAP.get(templateId + "_top").create(
				block,
				textureMapping,
				generators.modelOutput);
		var half = property(block, HALF);
		var generator = MultiVariantGenerator.dispatch(block)
				.with(PropertyDispatch.initial(half)
						.select("lower", plainVariant(model0))
						.select("upper", plainVariant(model1)))
				.with(altRotation ? rotationHorizontalFacingAlt() : BlockModelGenerators.ROTATION_HORIZONTAL_FACING);
		generators.blockStateOutput.accept(generator);
	}

	private void createRoofRidgeEnd(
			String id,
			Material roofTexture,
			Material ridgeTexture,
			Material smallRidgeTexture,
			boolean narrow,
			boolean asian) {
		Block block = block(id);
		String pathBase;
		if (asian) {
			pathBase = narrow ? "template_roof_small_ridge_end_asian" : "template_roof_ridge_end";
		} else {
			pathBase = "template_roof_small_ridge_end";
		}
		var variantProperty = property(block, ROOF_VARIANT_WITHOUT_SLOW);
		var generator = MultiVariantGenerator.dispatch(block)
				.with(PropertyDispatch.initial(variantProperty)
						.generate(variant -> {
							String path = pathBase;
							TextureMapping textureMapping = TextureMapping.particle(roofTexture);
							if (narrow) {
								textureMapping.put(XKDModelTemplates.SLOT_RIDGE, smallRidgeTexture);
								textureMapping.put(XKDModelTemplates.SLOT_RIDGE2, ridgeTexture);
							} else {
								textureMapping.put(XKDModelTemplates.SLOT_INNER, new Material(ROOF_INNER_TEXTURE));
								textureMapping.put(XKDModelTemplates.SLOT_RIDGE, ridgeTexture);
							}
							if (!variant.equals("normal")) {
								path += "_" + variant;
							}
							Identifier model = XKDModelTemplates.MAP.get(path).create(
									block,
									textureMapping,
									generators.modelOutput);
							return plainVariant(model);
						}))
				.with(rotationHorizontalFacingAlt());
		generators.blockStateOutput.accept(generator);
	}

	private void createRoofEnd(String id, Material roofTexture, Material ridgeTexture, boolean narrow) {
		Block block = block(id);
		var variantProperty = property(block, ROOF_VARIANT);
		var shapeProperty = property(block, ROOF_END_SHAPE);
		var halfProperty = property(block, HALF);
		var generator = MultiVariantGenerator.dispatch(block)
				.with(PropertyDispatch.initial(variantProperty, shapeProperty, halfProperty)
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
							Identifier model = XKDModelTemplates.MAP.get(path).create(
									block,
									TextureMapping.particle(roofTexture)
											.put(XKDModelTemplates.SLOT_INNER, new Material(ROOF_INNER_TEXTURE))
											.put(XKDModelTemplates.SLOT_RIDGE, ridgeTexture),
									generators.modelOutput);
							return plainVariant(model);
						}))
				.with(rotationHorizontalFacingAlt());
		generators.registerSimpleItemModel(block, ModelLocationUtils.getModelLocation(block).withSuffix("_left"));
		generators.blockStateOutput.accept(generator);
	}

	private void createRoofEave(String id, Material roofTexture, Material ridgeTexture, boolean narrow) {
		Block block = block(id);
		var shapeProperty = property(block, ROOF_EAVE_SHAPE);
		var halfProperty = property(block, HALF);
		var generator = MultiVariantGenerator.dispatch(block)
				.with(PropertyDispatch.initial(shapeProperty, halfProperty).generate((shape, half) -> {
					String path = narrow ? "template_roof_small_eave" : "template_roof_eave";
					if (!shape.equals("straight")) {
						path += "_" + shape;
					}
					if (half.equals("upper")) {
						path += "_top";
					}
					Identifier model = XKDModelTemplates.MAP.get(path).create(
							block,
							TextureMapping.particle(roofTexture)
									.put(XKDModelTemplates.SLOT_INNER, new Material(ROOF_INNER_TEXTURE))
									.put(XKDModelTemplates.SLOT_RIDGE, ridgeTexture),
							generators.modelOutput);
					return plainVariant(model);
				}))
				.with(rotationHorizontalFacingAlt());
		generators.blockStateOutput.accept(generator);
	}

	private void createRoofFlat(String id, Material roofTexture) {
		Block block = block(id);
		Identifier model0 = XKDModelTemplates.MAP.get("template_roof_flat").create(
				block,
				TextureMapping.particle(roofTexture).put(XKDModelTemplates.SLOT_INNER, new Material(ROOF_INNER_TEXTURE)),
				generators.modelOutput);
		Identifier model1 = XKDModelTemplates.MAP.get("template_roof_flat_top").create(
				block,
				TextureMapping.particle(roofTexture).put(XKDModelTemplates.SLOT_INNER, new Material(ROOF_INNER_TEXTURE)),
				generators.modelOutput);
		var half = property(block, HALF);
		var generator = MultiVariantGenerator.dispatch(block)
				.with(PropertyDispatch.initial(half)
						.select("lower", plainVariant(model0))
						.select("upper", plainVariant(model1)))
				.with(PropertyDispatch.modify(BlockStateProperties.HORIZONTAL_AXIS)
						.select(Direction.Axis.Z, BlockModelGenerators.NOP)
						.select(Direction.Axis.X, BlockModelGenerators.Y_ROT_90));
		generators.blockStateOutput.accept(generator);
	}

	private void createRoofRidge(String id, Material roofTexture, Material ridgeTexture, boolean asian) {
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
		List<Identifier> models = templateStream.map(template -> template.create(
				block,
				TextureMapping.particle(roofTexture)
						.put(XKDModelTemplates.SLOT_INNER, new Material(ROOF_INNER_TEXTURE))
						.put(XKDModelTemplates.SLOT_RIDGE, ridgeTexture),
				generators.modelOutput)).toList();
		Identifier normalModel = models.get(0);
		Identifier cornerModel = models.get(1);
		Identifier postModel = models.get(2);
		Identifier steepModel = asian ? models.get(4) : normalModel;
		generators.registerSimpleItemModel(block, models.get(3));
		MultiVariant post = plainVariant(postModel);
		MultiVariant normal = plainVariant(normalModel);
		MultiVariant corner = plainVariant(cornerModel);
		MultiVariant steep = plainVariant(steepModel);
		var generator = MultiPartGenerator.multiPart(block)
				.with(
						BlockModelGenerators.condition()
								.term(BlockStateProperties.NORTH_WALL, WallSide.LOW)
								.term(BlockStateProperties.EAST_WALL, WallSide.LOW)
								.term(BlockStateProperties.SOUTH_WALL, WallSide.LOW)
								.term(BlockStateProperties.WEST_WALL, WallSide.LOW),
						post)
				.with(
						BlockModelGenerators.condition()
								.term(BlockStateProperties.WEST_WALL, WallSide.NONE)
								.term(BlockStateProperties.NORTH_WALL, WallSide.NONE),
						corner)
				.with(
						BlockModelGenerators.condition()
								.term(BlockStateProperties.WEST_WALL, WallSide.NONE)
								.term(BlockStateProperties.SOUTH_WALL, WallSide.NONE),
						corner.with(BlockModelGenerators.Y_ROT_270))
				.with(
						BlockModelGenerators.condition()
								.term(BlockStateProperties.EAST_WALL, WallSide.NONE)
								.term(BlockStateProperties.SOUTH_WALL, WallSide.NONE),
						corner.with(BlockModelGenerators.Y_ROT_180))
				.with(
						BlockModelGenerators.condition()
								.term(BlockStateProperties.EAST_WALL, WallSide.NONE)
								.term(BlockStateProperties.NORTH_WALL, WallSide.NONE),
						corner.with(BlockModelGenerators.Y_ROT_90))
				.with(
						BlockModelGenerators.condition().term(BlockStateProperties.UP, true),
						post)
				.with(
						BlockModelGenerators.condition().term(BlockStateProperties.NORTH_WALL, WallSide.LOW),
						normal)
				.with(
						BlockModelGenerators.condition().term(BlockStateProperties.EAST_WALL, WallSide.LOW),
						normal.with(BlockModelGenerators.Y_ROT_90))
				.with(
						BlockModelGenerators.condition().term(BlockStateProperties.SOUTH_WALL, WallSide.LOW),
						normal.with(BlockModelGenerators.Y_ROT_180))
				.with(
						BlockModelGenerators.condition().term(BlockStateProperties.WEST_WALL, WallSide.LOW),
						normal.with(BlockModelGenerators.Y_ROT_270))
				.with(
						BlockModelGenerators.condition().term(BlockStateProperties.NORTH_WALL, WallSide.TALL),
						steep)
				.with(
						BlockModelGenerators.condition().term(BlockStateProperties.EAST_WALL, WallSide.TALL),
						steep.with(BlockModelGenerators.Y_ROT_90))
				.with(
						BlockModelGenerators.condition().term(BlockStateProperties.SOUTH_WALL, WallSide.TALL),
						steep.with(BlockModelGenerators.Y_ROT_180))
				.with(
						BlockModelGenerators.condition().term(BlockStateProperties.WEST_WALL, WallSide.TALL),
						steep.with(BlockModelGenerators.Y_ROT_270));
		generators.blockStateOutput.accept(generator);
	}

	private void createRoofNormal(String id, Material roofTexture, Material ridgeTexture) {
		Block block = block(id);
		var variantProperty = property(block, ROOF_VARIANT);
		var shapeProperty = property(block, ROOF_SHAPE);
		var halfProperty = property(block, HALF);
		var generator = MultiVariantGenerator.dispatch(block)
				.with(PropertyDispatch.initial(variantProperty, shapeProperty, halfProperty)
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
							Identifier model = XKDModelTemplates.MAP.get(path).create(
									block,
									TextureMapping.particle(roofTexture)
											.put(XKDModelTemplates.SLOT_INNER, new Material(ROOF_INNER_TEXTURE))
											.put(XKDModelTemplates.SLOT_RIDGE, ridgeTexture),
									generators.modelOutput);
							return plainVariant(model);
						}))
				.with(rotationHorizontalFacingAlt());
		generators.blockStateOutput.accept(generator);
	}

	private void createPillar(String id) {
		generators.createAxisAlignedPillarBlock(block(id), TexturedModel.COLUMN);
	}

	public static PropertyDispatch<VariantMutator> rotationHorizontalFacingAlt() {
		return PropertyDispatch.modify(BlockStateProperties.HORIZONTAL_FACING)
				.select(Direction.EAST, BlockModelGenerators.NOP)
				.select(Direction.SOUTH, BlockModelGenerators.Y_ROT_90)
				.select(Direction.WEST, BlockModelGenerators.Y_ROT_180)
				.select(Direction.NORTH, BlockModelGenerators.Y_ROT_270);
	}

	private void createRoofTip(String id) {
		Block block = block(id);
		Identifier model0 = XKDModelTemplates.MAP.get("template_roof_tip").create(
				block,
				TextureMapping.cube(block),
				generators.modelOutput);
		Identifier model1 = XKDModelTemplates.MAP.get("template_roof_tip_top").create(
				block,
				TextureMapping.cube(block),
				generators.modelOutput);
		var half = property(block, HALF);
		var generator = MultiVariantGenerator.dispatch(block)
				.with(PropertyDispatch.initial(half)
						.select("lower", plainVariant(model0))
						.select("upper", plainVariant(model1)));
		generators.blockStateOutput.accept(generator);
	}

	private static Quadrant quadrant(int degrees) {
		return switch (((degrees % 360) + 360) % 360) {
			case 90 -> Quadrant.R90;
			case 180 -> Quadrant.R180;
			case 270 -> Quadrant.R270;
			default -> Quadrant.R0;
		};
	}

	private static Block block(String id) {
		Identifier resourceLocation = XKDeco.id(id);
		return BuiltInRegistries.BLOCK.getOptional(resourceLocation).orElseThrow(() -> new IllegalStateException(
				"Missing block: " + resourceLocation));
	}

	@SuppressWarnings("unchecked")
	private static <P extends Property<?>> P property(Block block, P expected) {
		Property<?> property = block.getStateDefinition().getProperty(expected.getName());
		if (property == null) {
			throw new IllegalStateException("Missing property %s for block %s".formatted(expected.getName(), block));
		}
		return (P) property;
	}
}
