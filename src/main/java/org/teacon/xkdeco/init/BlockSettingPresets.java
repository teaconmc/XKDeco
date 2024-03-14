package org.teacon.xkdeco.init;

import java.util.List;
import java.util.Map;

import org.teacon.xkdeco.XKDeco;
import org.teacon.xkdeco.block.FallenLeavesBlock;
import org.teacon.xkdeco.block.impl.BlockPredicateCanSurviveHandler;
import org.teacon.xkdeco.block.settings.CanSurviveHandler;
import org.teacon.xkdeco.block.settings.GlassType;
import org.teacon.xkdeco.block.settings.ShapeGenerator;
import org.teacon.xkdeco.block.settings.ShapeStorage;
import org.teacon.xkdeco.block.settings.XKBlockSettings;
import org.teacon.xkdeco.util.RoofUtil;

import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import snownee.kiwi.KiwiModule;

public interface BlockSettingPresets {
	MapColor TODO = MapColor.GLOW_LICHEN;
	CanSurviveHandler FALLEN_LEAVES_CAN_SURVIVE = CanSurviveHandler.any(List.of(
			CanSurviveHandler.checkFloor(),
			new BlockPredicateCanSurviveHandler(state -> state.is(BlockTags.SLABS), Direction.DOWN)));

	static XKBlockSettings.Builder fallenLeaves(MapColor mapColor) {
		return XKBlockSettings.builder().configure($ -> $.mapColor(mapColor)
						.strength(0.1F)
						.sound(SoundType.CHERRY_LEAVES)
						.noOcclusion()
						.noCollission()
						.pushReaction(PushReaction.DESTROY)
						.ignitedByLava())
				.shape(ShapeGenerator.shifted(FallenLeavesBlock.HALF, Map.of(
						RoofUtil.RoofHalf.TIP, ShapeGenerator.unit(ShapeStorage.getInstance().get(XKDeco.id("fallen_leaves"))),
						RoofUtil.RoofHalf.BASE, ShapeGenerator.unit(ShapeStorage.getInstance().get("empty")))))
				.renderType(KiwiModule.RenderLayer.Layer.CUTOUT_MIPPED)
				.canSurviveHandler(FALLEN_LEAVES_CAN_SURVIVE);
	}

	static XKBlockSettings.Builder thingy(MapColor mapColor) {
		return thingy(mapColor, SoundType.WOOL);
	}

	static XKBlockSettings.Builder thingy(MapColor mapColor, SoundType soundType) {
		return XKBlockSettings.builder().waterLoggable().configure($ -> $.mapColor(mapColor == null ? TODO : mapColor)
				.strength(0.5F)
				.noOcclusion()
				.sound(soundType));
	}

	static XKBlockSettings.Builder lightThingy(MapColor mapColor) {
		return lightThingy(mapColor, SoundType.STONE);
	}

	static XKBlockSettings.Builder lightThingy(MapColor mapColor, SoundType soundType) {
		return lightThingy(mapColor, soundType, 15);
	}

	static XKBlockSettings.Builder lightThingy(MapColor mapColor, SoundType soundType, int light) {
		return XKBlockSettings.builder().waterLoggable().configure($ -> $.mapColor(mapColor == null ? TODO : mapColor)
				.strength(0.5F)
				.noOcclusion()
				.sound(soundType)
				.lightLevel($$ -> light));
	}

	static XKBlockSettings.Builder steel() {
		return XKBlockSettings.copyProperties(Blocks.IRON_BLOCK, MapColor.COLOR_GRAY);
	}

	static XKBlockSettings.Builder hollowSteel() {
		return steel().noOcclusion().glassType(GlassType.HOLLOW_STEEL).configure($ -> $.strength(3, 6));
	}

	static XKBlockSettings.Builder mudWall() {
		return XKBlockSettings.builder().configure($ -> $.mapColor(MapColor.STONE).sound(SoundType.STONE).strength(1.5f, 3f));
	}

	static XKBlockSettings.Builder blackTiles() {
		return XKBlockSettings.copyProperties(Blocks.STONE_BRICKS, MapColor.COLOR_BLACK);
	}

	static XKBlockSettings.Builder cyanTiles() {
		return XKBlockSettings.copyProperties(Blocks.STONE_BRICKS, MapColor.COLOR_CYAN);
	}

	static XKBlockSettings.Builder yellowTiles() {
		return XKBlockSettings.copyProperties(Blocks.STONE_BRICKS, MapColor.COLOR_YELLOW);
	}

	static XKBlockSettings.Builder blueTiles() {
		return XKBlockSettings.copyProperties(Blocks.STONE_BRICKS, MapColor.COLOR_BLUE);
	}

	static XKBlockSettings.Builder greenTiles() {
		return XKBlockSettings.copyProperties(Blocks.STONE_BRICKS, MapColor.COLOR_GREEN);
	}

	static XKBlockSettings.Builder redTiles() {
		return XKBlockSettings.copyProperties(Blocks.STONE_BRICKS, MapColor.COLOR_RED);
	}

	static XKBlockSettings.Builder hardenedGlass() {
		return XKBlockSettings.copyProperties(Blocks.GLASS).configure($ -> $.strength(2f, 10f));
	}

	static XKBlockSettings.Builder lampBlock() {
		return XKBlockSettings.copyProperties(Blocks.REDSTONE_LAMP).configure($ -> $.strength(2f, 10f).lightLevel($$ -> 15));
	}

	static XKBlockSettings.Builder stoneColumn(Block base, MapColor mapColor) {
		return XKBlockSettings.copyProperties(base, mapColor == null ? TODO : mapColor).waterLoggable().shape(XKDeco.id("stone_column"));
	}
}
