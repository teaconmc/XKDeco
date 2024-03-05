package org.teacon.xkdeco.data;

import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.teacon.xkdeco.XKDeco;

import com.google.common.collect.Maps;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.BlockFamily;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public class XKDBlockFamilies {
	private static final Set<String> FULL_NAME_BLOCKS = Set.of("cut_gold_block", "cut_bronze_block");
	private static final Map<Block, BlockFamily> MAP = Maps.newHashMap();
	public static final BlockFamily BLACK_TILES = basicSetup("black_tiles").getFamily();
	public static final BlockFamily CYAN_TILES = basicSetup("cyan_tiles").getFamily();
	public static final BlockFamily YELLOW_TILES = basicSetup("yellow_tiles").getFamily();
	public static final BlockFamily BLUE_TILES = basicSetup("blue_tiles").getFamily();
	public static final BlockFamily GREEN_TILES = basicSetup("green_tiles").getFamily();
	public static final BlockFamily RED_TILES = basicSetup("red_tiles").getFamily();
	public static final BlockFamily STEEL_TILES = basicSetup("steel_tiles").getFamily();
	public static final BlockFamily COPPER_TILES = basicSetup("copper_tiles").getFamily();
	public static final BlockFamily GLASS_TILES = basicSetup("glass_tiles").getFamily();
	public static final BlockFamily MUD_WALL = basicSetup("mud_wall_block").getFamily();
	public static final BlockFamily LINED_MUD_WALL = basicSetup("lined_mud_wall_block").getFamily();
	public static final BlockFamily CROSSED_MUD_WALL = basicSetup("crossed_mud_wall_block").getFamily();
	public static final BlockFamily DIRTY_MUD_WALL = basicSetup("dirty_mud_wall_block").getFamily();
	public static final BlockFamily CYAN_BRICKS = basicSetup("cyan_bricks").getFamily();
	public static final BlockFamily BLACK_BRICKS = basicSetup("black_bricks").getFamily();
	public static final BlockFamily VARNISHED_PLANKS = basicSetup("varnished_planks").getFamily();
	public static final BlockFamily EBONY_PLANKS = basicSetup("ebony_planks").getFamily();
	public static final BlockFamily MAHOGANY_PLANKS = basicSetup("mahogany_planks").getFamily();
	public static final BlockFamily POLISHED_SANDSTONE = blockAndSlab("polished_sandstone").getFamily();
	public static final BlockFamily SANDSTONE_BRICKS = basicSetup("sandstone_bricks").getFamily();
	public static final BlockFamily SANDSTONE_SMALL_BRICKS = basicSetup("sandstone_small_bricks").getFamily();
	public static final BlockFamily POLISHED_RED_SANDSTONE = blockAndSlab("polished_red_sandstone").getFamily();
	public static final BlockFamily RED_SANDSTONE_BRICKS = basicSetup("red_sandstone_bricks").getFamily();
	public static final BlockFamily RED_SANDSTONE_SMALL_BRICKS = basicSetup("red_sandstone_small_bricks").getFamily();
	public static final BlockFamily STONE_BRICK_PAVEMENT = blockAndSlab("stone_brick_pavement").getFamily();
	public static final BlockFamily DEEPSLATE_PAVEMENT = blockAndSlab("deepslate_pavement").getFamily();
	public static final BlockFamily MOSSY_DEEPSLATE_BRICKS = basicSetup("mossy_deepslate_bricks").getFamily();
	public static final BlockFamily BLACKSTONE_PAVEMENT = blockAndSlab("blackstone_pavement").getFamily();
	public static final BlockFamily GILDED_BLACKSTONE_BRICKS = basicSetup("gilded_blackstone_bricks").getFamily();
	public static final BlockFamily MAYA_STONE = basicSetup("maya_stone").getFamily();
	public static final BlockFamily MAYA_STONEBRICKS = basicSetup("maya_stonebricks")
			.polished(block("maya_polished_stonebricks"))
			.chiseled(block("maya_chiseled_stonebricks"))
			.cut(block("maya_cut_stonebricks"))
			.getFamily();
	public static final BlockFamily MAYA_BRICKS = basicSetup("maya_bricks").getFamily();
	public static final BlockFamily MAYA_POLISHED_STONEBRICKS = basicSetup("maya_polished_stonebricks").getFamily();
	public static final BlockFamily MAYA_MOSSY_STONEBRICKS = basicSetup("maya_mossy_stonebricks").getFamily();
	public static final BlockFamily MAYA_MOSSY_BRICKS = basicSetup("maya_mossy_bricks").getFamily();
	public static final BlockFamily AZTEC_STONEBRICKS = basicSetup("aztec_stonebricks")
			.chiseled(block("aztec_chiseled_stonebricks"))
			.cut(block("aztec_cut_stonebricks"))
			.getFamily();
	public static final BlockFamily AZTEC_MOSSY_STONEBRICKS = basicSetup("aztec_mossy_stonebricks").getFamily();
	public static final BlockFamily INCA_STONE = basicSetup("inca_stone").getFamily();
	public static final BlockFamily INCA_STONEBRICKS = basicSetup("inca_stonebricks").getFamily();
	public static final BlockFamily INCA_BRICKS = basicSetup("inca_bricks").getFamily();
	public static final BlockFamily CUT_OBSIDIAN = blockAndSlab("cut_obsidian").getFamily();
	public static final BlockFamily CUT_OBSIDIAN_BRICKS = basicSetup("cut_obsidian_bricks").getFamily();
	public static final BlockFamily CRYING_OBSIDIAN_BRICKS = basicSetup("crying_obsidian_bricks").getFamily();
	public static final BlockFamily CUT_GOLD_BLOCK = basicSetup("cut_gold_block").getFamily();
	public static final BlockFamily GOLD_BRICKS = basicSetup("gold_bricks").getFamily();
	public static final BlockFamily BRONZE_BLOCK = familyBuilder("bronze_block")
			.polished(block("smooth_bronze_block"))
			.cut(block("cut_bronze_block"))
			.chiseled(block("chiseled_bronze_block"))
			.getFamily();
	public static final BlockFamily CUT_BRONZE_BLOCK = basicSetup("cut_bronze_block").getFamily();
	public static final BlockFamily STEEL_BLOCK = familyBuilder("steel_block")
			.polished(block("smooth_steel_block"))
			.chiseled(block("chiseled_steel_block"))
			.getFamily();
	public static final BlockFamily STEEL_FLOOR = basicSetup("steel_floor").getFamily();
	public static final BlockFamily FACTORY_BLOCK = basicSetup("factory_block").getFamily();
	public static final BlockFamily FACTORY_BLOCK_RUSTING = familyBuilder("factory_block_rusting")
			.slab(block("factory_slab_rusting"))
			.stairs(block("factory_stairs_rusting"))
			.getFamily();
	public static final BlockFamily FACTORY_BLOCK_RUSTED = familyBuilder("factory_block_rusted")
			.slab(block("factory_slab_rusted"))
			.stairs(block("factory_stairs_rusted"))
			.getFamily();
	public static final BlockFamily FACTORY_LAMP_BLOCK = basicSetup("factory_lamp_block").getFamily();
	public static final BlockFamily TECH_LAMP_BLOCK = basicSetup("tech_lamp_block").getFamily();
	public static final BlockFamily TRANSLUCENT_LAMP_BLOCK = basicSetup("translucent_lamp_block").getFamily();
	public static final BlockFamily QUARTZ_GLASS = basicSetup("quartz_glass").getFamily();
	public static final BlockFamily TOUGHENED_GLASS = basicSetup("toughened_glass").getFamily();
	public static final BlockFamily DIRT_COBBLESTONE = basicSetup("dirt_cobblestone").getFamily();
	public static final BlockFamily GRASS_COBBLESTONE = basicSetup("grass_cobblestone").getFamily();
	public static final BlockFamily SANDY_COBBLESTONE = basicSetup("sandy_cobblestone").getFamily();
	public static final BlockFamily SNOWY_COBBLESTONE = basicSetup("snowy_cobblestone").getFamily();
	public static final BlockFamily COBBLESTONE_PATH = basicSetup("cobblestone_path").getFamily();
	public static final BlockFamily DIRT_COBBLESTONE_PATH = basicSetup("dirt_cobblestone_path").getFamily();
	public static final BlockFamily GRASS_COBBLESTONE_PATH = basicSetup("grass_cobblestone_path").getFamily();
	public static final BlockFamily SANDY_COBBLESTONE_PATH = basicSetup("sandy_cobblestone_path").getFamily();
	public static final BlockFamily SNOWY_COBBLESTONE_PATH = basicSetup("snowy_cobblestone_path").getFamily();

	private static BlockFamily.Builder basicSetup(String id) {
		BlockFamily.Builder builder = familyBuilder(id);
		if (id.endsWith("_tiles") || id.endsWith("bricks")) {
			id = id.substring(0, id.length() - 1);
		} else if (id.endsWith("_block") && !FULL_NAME_BLOCKS.contains(id)) {
			id = id.substring(0, id.length() - 6);
		} else if (id.endsWith("_planks")) {
			id = id.substring(0, id.length() - 7);
		}
		return builder.slab(block(id + "_slab")).stairs(block(id + "_stairs"));
	}

	private static BlockFamily.Builder blockAndSlab(String id) {
		BlockFamily.Builder builder = familyBuilder(id);
		return builder.slab(block(id + "_slab"));
	}

	private static BlockFamily.Builder familyBuilder(String id) {
		Block baseBlock = block(id);
		BlockFamily.Builder $$1 = new BlockFamily.Builder(baseBlock);
		BlockFamily $$2 = MAP.put(baseBlock, $$1.getFamily());
		if ($$2 != null) {
			throw new IllegalStateException("Duplicate family definition for " + BuiltInRegistries.BLOCK.getKey(baseBlock));
		} else {
			return $$1;
		}
	}

	public static Stream<BlockFamily> getAllFamilies() {
		return MAP.values().stream();
	}

	private static Block block(String id) {
		ResourceLocation resourceLocation = new ResourceLocation(XKDeco.ID, id);
		return BuiltInRegistries.BLOCK.getOptional(resourceLocation).orElseThrow(() -> {
			return new IllegalStateException("Missing block: " + resourceLocation);
		});
	}
}
