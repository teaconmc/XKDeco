package org.teacon.xkdeco.data;

import java.util.Map;
import java.util.Optional;

import org.teacon.xkdeco.XKDeco;
import org.teacon.xkdeco.util.RoofUtil;

import com.google.common.collect.Maps;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.models.model.ModelTemplate;
import net.minecraft.data.models.model.TextureMapping;
import net.minecraft.data.models.model.TextureSlot;
import net.minecraft.data.models.model.TexturedModel;
import net.minecraft.resources.ResourceLocation;

public class XKDModelTemplates {
	public static final TextureSlot SLOT_ROOF = TextureSlot.create("roof", TextureSlot.PARTICLE);
	public static final TextureSlot SLOT_RIDGE = TextureSlot.create("ridge");
	public static final TextureSlot SLOT_INNER = TextureSlot.create("inner");
	public static final Map<String, ModelTemplate> MAP = Maps.newHashMap();
	public static final ModelTemplate ROOF_RIDGE = create("template_roof_ridge", TextureSlot.PARTICLE, SLOT_ROOF, SLOT_INNER, SLOT_RIDGE);
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
	public static final ModelTemplate ROOF_RIDGE_SIDE_TALL = create(
			"template_roof_ridge_side_tall",
			"_side_tall",
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
	public static final ModelTemplate ROOF_FLAT = create("template_roof_flat", TextureSlot.PARTICLE, SLOT_ROOF, SLOT_INNER);
	public static final ModelTemplate ROOF_FLAT_TOP = create("template_roof_flat_top", "_top", TextureSlot.PARTICLE, SLOT_ROOF, SLOT_INNER);
	public static final ModelTemplate ROOF_TIP = create("template_roof_tip", TextureSlot.PARTICLE);
	public static final ModelTemplate ROOF_TIP_TOP = create("template_roof_tip_top", "_top", TextureSlot.PARTICLE);
	public static final ModelTemplate FALLEN_LEAVES = create("template_fallen_leaves", TextureSlot.ALL);
	public static final TexturedModel.Provider FALLEN_LEAVES_PROVIDER = block -> {
		ResourceLocation id = BuiltInRegistries.BLOCK.getKey(block);
		if (id.getPath().startsWith("fallen_")) {
			block = BuiltInRegistries.BLOCK.get(id.withPath(id.getPath().substring(7)));
		}
		return new TexturedModel(TextureMapping.cube(block), FALLEN_LEAVES);
	};

	static {
		bootstrap();
	}

	private static void bootstrap() {
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
					if (half == RoofUtil.RoofHalf.BASE) {
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
				if (half == RoofUtil.RoofHalf.BASE) {
					suffix += "_top";
				}
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
					if (half == RoofUtil.RoofHalf.BASE) {
						suffix += "_top";
					}
					suffix += "_" + shape;
					create("template_roof_end" + suffix, suffix, TextureSlot.PARTICLE, SLOT_ROOF, SLOT_INNER, SLOT_RIDGE);
				}
			}
		}
	}

	private static ModelTemplate create(String pBlockModelLocation, TextureSlot... pRequiredSlots) {
		return create(pBlockModelLocation, null, pRequiredSlots);
	}

	private static ModelTemplate createItem(String pItemModelLocation, TextureSlot... pRequiredSlots) {
		return new ModelTemplate(
				Optional.of(new ResourceLocation(XKDeco.ID, "item/" + pItemModelLocation)),
				Optional.empty(),
				pRequiredSlots);
	}

	private static ModelTemplate create(String pBlockModelLocation, String pSuffix, TextureSlot... pRequiredSlots) {
		ModelTemplate template = new ModelTemplate(
				Optional.of(new ResourceLocation(XKDeco.ID, "block/" + pBlockModelLocation)),
				Optional.ofNullable(pSuffix),
				pRequiredSlots);
		MAP.put(pBlockModelLocation, template);
		return template;
	}
}
