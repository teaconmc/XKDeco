package org.teacon.xkdeco.init;

import org.teacon.xkdeco.block.settings.XKBlockSettings;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public interface BlockSettingPresets {
	MapColor TODO = MapColor.GLOW_LICHEN;

	static XKBlockSettings.Builder fallenLeaves(MapColor mapColor) {
		return XKBlockSettings.builder().configure($ -> $.mapColor(mapColor)
				.strength(0.1F)
				.sound(SoundType.CHERRY_LEAVES)
				.noOcclusion()
				.pushReaction(PushReaction.DESTROY)
				.ignitedByLava());
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
		return steel().noOcclusion().configure($ -> $.strength(3, 6));
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
		return hardenedGlass().configure($ -> $.lightLevel($$ -> 15));
	}
}
