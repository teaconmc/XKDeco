package org.teacon.xkdeco.init;

import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.Nullable;
import org.teacon.xkdeco.XKDeco;
import org.teacon.xkdeco.block.FallenLeavesBlock;
import org.teacon.xkdeco.block.impl.BlockPredicateCanSurviveHandler;
import org.teacon.xkdeco.block.loader.KBlockComponents;
import org.teacon.xkdeco.block.setting.CanSurviveHandler;
import org.teacon.xkdeco.block.setting.ConsumableComponent;
import org.teacon.xkdeco.block.setting.GlassType;
import org.teacon.xkdeco.block.setting.KBlockSettings;
import org.teacon.xkdeco.block.setting.ShapeGenerator;
import org.teacon.xkdeco.block.setting.ShapeStorage;
import org.teacon.xkdeco.util.RoofUtil;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.food.FoodProperties;
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

	static KBlockSettings.Builder fallenLeaves(MapColor mapColor) {
		return KBlockSettings.builder().configure($ -> $.mapColor(mapColor)
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

	static KBlockSettings.Builder thingy(MapColor mapColor) {
		return thingy(mapColor, SoundType.WOOL);
	}

	static KBlockSettings.Builder thingy(MapColor mapColor, SoundType soundType) {
		return KBlockSettings.builder().waterLoggable().configure($ -> $.mapColor(mapColor == null ? TODO : mapColor)
				.strength(0.5F)
				.noOcclusion()
				.sound(soundType));
	}

	static KBlockSettings.Builder lightThingy(MapColor mapColor) {
		return lightThingy(mapColor, SoundType.STONE);
	}

	static KBlockSettings.Builder lightThingy(MapColor mapColor, SoundType soundType) {
		return lightThingy(mapColor, soundType, 15);
	}

	static KBlockSettings.Builder lightThingy(MapColor mapColor, SoundType soundType, int light) {
		return KBlockSettings.builder().waterLoggable().configure($ -> $.mapColor(mapColor == null ? TODO : mapColor)
				.strength(0.5F)
				.noOcclusion()
				.sound(soundType)
				.lightLevel($$ -> light));
	}

	static KBlockSettings.Builder steel() {
		return KBlockSettings.copyProperties(Blocks.IRON_BLOCK, MapColor.COLOR_GRAY);
	}

	static KBlockSettings.Builder hollowSteel() {
		return steel().noOcclusion().glassType(GlassType.HOLLOW_STEEL).configure($ -> $.strength(3, 6));
	}

	static KBlockSettings.Builder mudWall() {
		return KBlockSettings.builder().configure($ -> $.mapColor(MapColor.STONE).sound(SoundType.STONE).strength(1.5f, 3f));
	}

	static KBlockSettings.Builder blackTiles() {
		return KBlockSettings.copyProperties(Blocks.STONE_BRICKS, MapColor.COLOR_BLACK);
	}

	static KBlockSettings.Builder cyanTiles() {
		return KBlockSettings.copyProperties(Blocks.STONE_BRICKS, MapColor.COLOR_CYAN);
	}

	static KBlockSettings.Builder yellowTiles() {
		return KBlockSettings.copyProperties(Blocks.STONE_BRICKS, MapColor.COLOR_YELLOW);
	}

	static KBlockSettings.Builder blueTiles() {
		return KBlockSettings.copyProperties(Blocks.STONE_BRICKS, MapColor.COLOR_BLUE);
	}

	static KBlockSettings.Builder greenTiles() {
		return KBlockSettings.copyProperties(Blocks.STONE_BRICKS, MapColor.COLOR_GREEN);
	}

	static KBlockSettings.Builder redTiles() {
		return KBlockSettings.copyProperties(Blocks.STONE_BRICKS, MapColor.COLOR_RED);
	}

	static KBlockSettings.Builder hardenedGlass() {
		return KBlockSettings.copyProperties(Blocks.GLASS).configure($ -> $.strength(2f, 10f));
	}

	static KBlockSettings.Builder lampBlock() {
		return KBlockSettings.copyProperties(Blocks.REDSTONE_LAMP).configure($ -> $.strength(2f, 10f).lightLevel($$ -> 15));
	}

	static KBlockSettings.Builder stoneColumn(Block base, MapColor mapColor) {
		return KBlockSettings.copyProperties(base, mapColor == null ? TODO : mapColor).waterLoggable().shape(XKDeco.id("stone_column"));
	}

	static KBlockSettings.Builder food(int min, int max, FoodProperties foodProperties, @Nullable ResourceLocation stat) {
		return thingy(null)
				.component(ConsumableComponent.create(min, max).withFood(foodProperties, stat))
				.removeComponent(KBlockComponents.WATER_LOGGABLE.getOrCreate())
				.shape(ShapeGenerator.unit(ShapeStorage.getInstance().get(XKDeco.id("dessert"))))
				.canSurviveHandler(CanSurviveHandler.checkFloor());
	}
}
