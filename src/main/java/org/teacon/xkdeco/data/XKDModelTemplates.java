package org.teacon.xkdeco.data;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.teacon.xkdeco.XKDeco;
import org.teacon.xkdeco.util.RoofUtil;

import com.google.common.collect.Maps;

import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.data.models.model.TexturedModel;
import net.minecraft.world.level.block.Block;

public class XKDModelTemplates {
	public static final TextureSlot SLOT_ROOF = TextureSlot.create("roof", TextureSlot.PARTICLE);
	public static final TextureSlot SLOT_RIDGE = TextureSlot.create("ridge");
	public static final TextureSlot SLOT_RIDGE2 = TextureSlot.create("ridge2");
	public static final TextureSlot SLOT_INNER = TextureSlot.create("inner");
	public static final TextureSlot PLANKS = TextureSlot.create("planks");
	public static final Map<String, ModelTemplate> MAP = Maps.newHashMap();
	public static final ModelTemplate ROOF_RIDGE = create("template_roof_ridge", TextureSlot.PARTICLE, SLOT_ROOF, SLOT_INNER, SLOT_RIDGE);
	public static final ModelTemplate ROOF_RIDGE_ASIAN = create(
			"template_roof_ridge_asian",
			TextureSlot.PARTICLE,
			SLOT_ROOF,
			SLOT_INNER,
			SLOT_RIDGE);
	public static final ModelTemplate ROOF_RIDGE_CORNER = create(
			"template_roof_ridge_corner",
			"_corner",
			TextureSlot.PARTICLE,
			SLOT_ROOF,
			SLOT_INNER,
			SLOT_RIDGE);
	public static final ModelTemplate ROOF_RIDGE_POST = create(
			"template_roof_ridge_post",
			"_post",
			TextureSlot.PARTICLE,
			SLOT_ROOF,
			SLOT_INNER,
			SLOT_RIDGE);
	public static final ModelTemplate ROOF_RIDGE_ASIAN_POST = create(
			"template_roof_ridge_asian_post",
			"_post",
			TextureSlot.PARTICLE,
			SLOT_ROOF,
			SLOT_INNER,
			SLOT_RIDGE);
	public static final ModelTemplate ROOF_RIDGE_ASIAN_STEEP = create(
			"template_roof_ridge_asian_steep",
			"_steep",
			TextureSlot.PARTICLE,
			SLOT_ROOF,
			SLOT_INNER,
			SLOT_RIDGE);
	public static final ModelTemplate ROOF_RIDGE_INVENTORY = create(
			"template_roof_ridge_inventory",
			"_inventory",
			TextureSlot.PARTICLE,
			SLOT_ROOF,
			SLOT_INNER,
			SLOT_RIDGE);
	public static final ModelTemplate ROOF_RIDGE_ASIAN_INVENTORY = create(
			"template_roof_ridge_asian_inventory",
			"_inventory",
			TextureSlot.PARTICLE,
			SLOT_ROOF,
			SLOT_INNER,
			SLOT_RIDGE);
	public static final ModelTemplate FALLEN_LEAVES = create("template_fallen_leaves", TextureSlot.ALL);
	public static final ModelTemplate FALLEN_LEAVES_SLAB = create("template_fallen_leaves_slab", "_slab", TextureSlot.ALL);
	public static final ModelTemplate NATURAL_SLAB = create("template_natural_slab", TextureSlot.BOTTOM, TextureSlot.TOP, TextureSlot.SIDE);
	public static final TexturedModel.Provider WOODEN_TABLE_PROVIDER = createDefault(
			XKDModelTemplates::allAndSide,
			create("wooden_table", TextureSlot.ALL, TextureSlot.SIDE));
	public static final TexturedModel.Provider WOODEN_BIG_TABLE_PROVIDER = createDefault(
			XKDModelTemplates::allAndSide,
			create("wooden_big_table", TextureSlot.ALL, TextureSlot.SIDE));
	public static final TexturedModel.Provider WOODEN_TALL_TABLE_PROVIDER = createDefault(
			XKDModelTemplates::allAndSide,
			create("wooden_tall_table", TextureSlot.ALL, TextureSlot.SIDE));
	public static final TexturedModel.Provider WOODEN_DESK_PROVIDER = createDefault(
			block -> {
				return TextureMapping.cube(block).put(
						TextureSlot.SIDE,
						TextureMapping.getBlockTexture(block).withPath(s -> s.replace("desk", "tall_table_side")));
			},
			create("wooden_desk", TextureSlot.ALL, TextureSlot.SIDE));
	public static final TexturedModel.Provider WOODEN_STOOL_PROVIDER = createDefault(
			block -> {
				TextureMapping mapping = new TextureMapping();
				mapping.put(
						TextureSlot.END,
						TextureMapping.getBlockTexture(block).withPath(s -> s.replace("stool", "table")));
				mapping.put(
						TextureSlot.SIDE,
						TextureMapping.getBlockTexture(block).withPath(s -> s.replace("stool", "table_side")));
				return mapping;
			},
			create("wooden_stool", TextureSlot.END, TextureSlot.SIDE));
	public static final TexturedModel.Provider WOODEN_OBLIQUE_BRACE_PROVIDER = createDefault(
			TextureMapping::particle,
			create("wooden_oblique_brace", TextureSlot.PARTICLE));
	public static final ModelTemplate THIN_TRAPDOOR_TOP = create("thin_trapdoor_top", "_top", TextureSlot.TEXTURE);
	public static final ModelTemplate THIN_TRAPDOOR_BOTTOM = create("thin_trapdoor_bottom", "_bottom", TextureSlot.TEXTURE);
	public static final ModelTemplate THIN_TRAPDOOR_OPEN = create("thin_trapdoor_open", "_open", TextureSlot.TEXTURE);
	public static final ModelTemplate WOODEN_FENCE_POST = create("wooden_fence_post", "_post", TextureSlot.TEXTURE);
	public static final ModelTemplate WOODEN_FENCE_SIDE = create("wooden_fence_side", "_side", TextureSlot.TEXTURE);
	public static final ModelTemplate WOODEN_FENCE_HEAD = create("wooden_fence_head", TextureSlot.PARTICLE);
	public static final ModelTemplate WOODEN_FENCE_HEAD_FLIP = create("wooden_fence_head_flip", "_flip", TextureSlot.PARTICLE);
	public static final ModelTemplate WOODEN_COLUMN_HEAD = create("wooden_column_head", TextureSlot.PARTICLE);
	public static final ModelTemplate WOODEN_FENCE_INVENTORY = create(
			"wooden_fence_inventory",
			"_inventory",
			TextureSlot.TEXTURE,
			TextureSlot.SIDE);
	public static final ModelTemplate WOODEN_FENCE_GATE_OPEN = create("wooden_fence_gate_open", "_open", TextureSlot.TEXTURE);
	public static final ModelTemplate WOODEN_FENCE_GATE_CLOSED = create("wooden_fence_gate", TextureSlot.TEXTURE);
	public static final ModelTemplate WOODEN_NARROW_DOORS_OPEN = create(
			"wooden_narrow_doors_open",
			"_open",
			TextureSlot.TOP,
			TextureSlot.BOTTOM);
	public static final ModelTemplate WOODEN_NARROW_DOORS_CLOSED = create("wooden_narrow_doors", TextureSlot.TOP, TextureSlot.BOTTOM);
	public static final ModelTemplate WOODEN_WINDOW_OPEN = create("wooden_window_open", "_open", TextureSlot.PARTICLE);
	public static final ModelTemplate WOODEN_WINDOW_CLOSED = create("wooden_window", TextureSlot.PARTICLE);
	public static final ModelTemplate WOODEN_AWNING_WINDOW_OPEN = create("wooden_awning_window_open", "_open", TextureSlot.TEXTURE);
	public static final ModelTemplate WOODEN_AWNING_WINDOW_CLOSED = create("wooden_awning_window", TextureSlot.TEXTURE);
	public static final ModelTemplate GLASS_STAIRS = create("glass_stairs", TextureSlot.SIDE);
	public static final ModelTemplate GLASS_STAIRS_INNER = create("glass_stairs_inner", "_inner", TextureSlot.SIDE);
	public static final ModelTemplate GLASS_STAIRS_OUTER = create("glass_stairs_outer", "_outer", TextureSlot.SIDE);
	public static final ModelTemplate SCREEN = create("template_screen", TextureSlot.PARTICLE);
	public static final ModelTemplate HANGING_FASCIA_SIDE = create("hanging_fascia_side", "_side", TextureSlot.PARTICLE);
	public static final ModelTemplate HANGING_FASCIA_MIDDLE = create("hanging_fascia_middle", "_middle", TextureSlot.PARTICLE);

	static {
		bootstrap();
	}

	private static void bootstrap() {
		createShift("template_roof_flat", TextureSlot.PARTICLE, SLOT_ROOF, SLOT_INNER);
		createShift("template_roof_small_flat_end", TextureSlot.PARTICLE, SLOT_ROOF, SLOT_RIDGE);
		createShift("template_roof_deco", TextureSlot.ALL, SLOT_RIDGE);
		createShift("template_roof_deco_oblique", TextureSlot.ALL);
		createShift("template_roof_tip", TextureSlot.ALL);
		createShift("air_duct_oblique", TextureSlot.ALL);

		for (RoofUtil.RoofVariant variant : RoofUtil.RoofVariant.values()) {
			for (RoofUtil.RoofShape shape : RoofUtil.RoofShape.values()) {
				for (RoofUtil.RoofHalf half : RoofUtil.RoofHalf.values()) {
					String suffix = "";
					if (variant != RoofUtil.RoofVariant.NORMAL) {
						suffix += "_" + variant;
					}
					if (shape != RoofUtil.RoofShape.STRAIGHT) {
						suffix += "_" + shape;
					}
					if (half == RoofUtil.RoofHalf.UPPER) {
						suffix += "_top";
					}
					if (shape != RoofUtil.RoofShape.STRAIGHT) {
						create("template_roof" + suffix, suffix, TextureSlot.PARTICLE, SLOT_ROOF, SLOT_INNER, SLOT_RIDGE);
					} else {
						create("template_roof" + suffix, suffix, TextureSlot.PARTICLE, SLOT_ROOF, SLOT_INNER);
					}
				}
			}
		}
		for (RoofUtil.RoofEaveShape shape : RoofUtil.RoofEaveShape.values()) {
			for (RoofUtil.RoofHalf half : RoofUtil.RoofHalf.values()) {
				String suffix = "";
				if (shape != RoofUtil.RoofEaveShape.STRAIGHT) {
					suffix += "_" + shape;
				}
				if (half == RoofUtil.RoofHalf.UPPER) {
					suffix += "_top";
				}
				create("template_roof_small_eave" + suffix, suffix, TextureSlot.PARTICLE, SLOT_ROOF, SLOT_RIDGE);
				create("template_roof_eave" + suffix, suffix, TextureSlot.PARTICLE, SLOT_ROOF, SLOT_INNER, SLOT_RIDGE);
			}
		}
		for (RoofUtil.RoofVariant variant : RoofUtil.RoofVariant.values()) {
			for (RoofUtil.RoofEndShape shape : RoofUtil.RoofEndShape.values()) {
				for (RoofUtil.RoofHalf half : RoofUtil.RoofHalf.values()) {
					String suffix = "";
					if (variant != RoofUtil.RoofVariant.NORMAL) {
						suffix += "_" + variant;
					}
					suffix += "_" + shape;
					if (half == RoofUtil.RoofHalf.UPPER) {
						suffix += "_top";
					}
					create("template_roof_end" + suffix, suffix, TextureSlot.PARTICLE, SLOT_ROOF, SLOT_INNER, SLOT_RIDGE);
					create("template_roof_small_end" + suffix, suffix, TextureSlot.PARTICLE, SLOT_ROOF, SLOT_RIDGE);
					create("template_roof_small_end" + suffix, suffix, TextureSlot.PARTICLE, SLOT_ROOF, SLOT_RIDGE);
				}
			}
		}
		for (RoofUtil.RoofVariant variant : RoofUtil.RoofVariant.values()) {
			if (variant == RoofUtil.RoofVariant.SLOW) {
				continue;
			}
			for (RoofUtil.RoofHalf half : RoofUtil.RoofHalf.values()) {
				String suffix = "";
				if (variant != RoofUtil.RoofVariant.NORMAL) {
					suffix += "_" + variant;
				}
				if (half == RoofUtil.RoofHalf.UPPER) {
					suffix += "_top";
				}
				create("template_roof_ridge_end" + suffix, suffix, TextureSlot.PARTICLE, SLOT_ROOF, SLOT_RIDGE, SLOT_INNER);
				create("template_roof_small_ridge_end_asian" + suffix, suffix, TextureSlot.PARTICLE, SLOT_ROOF, SLOT_RIDGE, SLOT_RIDGE2);
				if (variant == RoofUtil.RoofVariant.NORMAL) {
					create("template_roof_small_ridge_end" + suffix, suffix, TextureSlot.PARTICLE, SLOT_ROOF, SLOT_RIDGE2);
				}
			}
		}
		for (String s : List.of("_inventory", "_post", "_side", "_side_tall")) {
			TextureSlot[] slots;
			if (s.startsWith("_side")) {
				slots = new TextureSlot[]{TextureSlot.WALL};
				create("wooden_wall" + s, s, PLANKS);
			} else {
				slots = new TextureSlot[]{TextureSlot.WALL, TextureSlot.SIDE, TextureSlot.END};
				create("wooden_wall" + s, s, PLANKS, TextureSlot.SIDE, TextureSlot.END);
			}
			create("wooden_column_wall" + s, s, slots);
			create("hollow_wooden_column_wall" + s, s, slots);
		}
		for (String template : List.of("", "_connection", "_hollow_connection")) {
			for (String s : List.of("", "_inner", "_outer")) {
				create("template_dougong" + template + s, s, TextureSlot.SIDE);
			}
		}
	}

	private static ModelTemplate createShift(String pBlockModelLocation, TextureSlot... pRequiredSlots) {
		create(pBlockModelLocation + "_top", "_top", pRequiredSlots);
		return create(pBlockModelLocation, null, pRequiredSlots);
	}

	private static ModelTemplate create(String pBlockModelLocation, TextureSlot... pRequiredSlots) {
		return create(pBlockModelLocation, null, pRequiredSlots);
	}

	private static ModelTemplate createItem(String pItemModelLocation, TextureSlot... pRequiredSlots) {
		return new ModelTemplate(
				Optional.of(XKDeco.id("item/" + pItemModelLocation)),
				Optional.empty(),
				pRequiredSlots);
	}

	private static ModelTemplate create(String pBlockModelLocation, String pSuffix, TextureSlot... pRequiredSlots) {
		ModelTemplate template = new ModelTemplate(
				Optional.of(XKDeco.id("block/" + pBlockModelLocation)),
				Optional.ofNullable(pSuffix),
				pRequiredSlots);
		MAP.put(pBlockModelLocation, template);
		return template;
	}

	private static TexturedModel.Provider createDefault(
			Function<Block, TextureMapping> pBlockToTextureMapping,
			ModelTemplate pModelTemplate) {
		return block -> new TexturedModel(pBlockToTextureMapping.apply(block), pModelTemplate);
	}

	public static TextureMapping allAndSide(Block block) {
		return TextureMapping.cube(block).put(TextureSlot.SIDE, TextureMapping.getBlockTexture(block, "_side"));
	}
}
