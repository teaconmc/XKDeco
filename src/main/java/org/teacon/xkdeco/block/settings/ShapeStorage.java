package org.teacon.xkdeco.block.settings;

import static net.minecraft.world.level.block.Block.box;

import java.util.Map;

import org.jetbrains.annotations.Nullable;
import org.teacon.xkdeco.XKDeco;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ShapeStorage {
	private static final ShapeStorage INSTANCE = new ShapeStorage();

	public static ShapeStorage getInstance() {
		return INSTANCE;
	}

	private static void put(String id, VoxelShape shape) {
		getInstance().register(XKDeco.id(id), shape);
	}

	static {
		getInstance().register(new ResourceLocation("carpet"), box(0, 0, 0, 16, 1, 16));
		put("small_book_stack", box(2, 0, 2, 14, 8, 14));
		put("big_book_stack", box(0, 0, 0, 16, 10, 16));
		put("bottle_stack", box(2, 0, 2, 14, 8, 14));
		put("solar_system_model", box(0, 0, 0, 16, 10, 16));
		put("factory_ceiling_lamp", box(0, 12, 0, 16, 16, 16));
		put("factory_pendant", box(2, 4, 2, 14, 16, 14));

		VoxelShape TABLE_BASE = box(4, 0, 4, 12, 3, 12);
		VoxelShape TABLE_LEG = box(6, 3, 6, 10, 13, 10);
		VoxelShape TABLE_TOP = box(0, 13, 0, 16, 16, 16);
		put("table", Shapes.or(TABLE_BASE, TABLE_LEG, TABLE_TOP));

		VoxelShape BIG_TABLE_TOP = box(0, 8, 0, 16, 16, 16);
		VoxelShape BIG_TABLE_LEG_NN = box(0, 0, 0, 2, 8, 2);
		VoxelShape BIG_TABLE_LEG_NP = box(0, 0, 14, 2, 8, 16);
		VoxelShape BIG_TABLE_LEG_PN = box(14, 0, 0, 16, 8, 2);
		VoxelShape BIG_TABLE_LEG_PP = box(14, 0, 14, 16, 8, 16);
		put("big_table", Shapes.or(BIG_TABLE_TOP, BIG_TABLE_LEG_PP, BIG_TABLE_LEG_PN, BIG_TABLE_LEG_NP, BIG_TABLE_LEG_NN));

		put("long_stool", box(0, 0, 3, 16, 10, 13));
		put("chair", Shapes.or(box(2, 0, 2, 14, 10, 14), box(2, 10, 12, 14, 16, 14)));
		put("shelf", Shapes.or(
				box(15, 0, 0, 16, 16, 16),
				box(0, 0, 0, 1, 16, 16),
				box(1, 15, 0, 15, 16, 16),
				box(1, 0, 0, 15, 1, 16)));
		put("miniature", box(0, 0, 3, 16, 6, 13));
		put("teapot", box(4, 0, 4, 12, 6, 12));
		put("tea_ware", box(0, 0, 3, 16, 2, 13));
		put("board", box(1, 0, 1, 15, 1, 15));
		put("porcelain", box(2, 0, 2, 14, 16, 14));
		put("porcelain_small", box(5, 0, 5, 11, 12, 11));
		put("lantern", Shapes.or(box(2, 2, 2, 14, 14, 14), box(5, 0, 5, 11, 16, 11)));
		put("festival_lantern", Shapes.or(box(2, 2, 2, 14, 14, 14), box(5, 0, 5, 11, 16, 11), box(0, 3, 0, 16, 13, 16)));
		put("candlestick", box(5, 0, 5, 11, 13, 11));
		put("big_candlestick", box(2, 0, 2, 14, 14, 14));
		put("covered_lamp", box(4, 0, 4, 12, 16, 12));
		put("stone_lamp", box(3, 0, 3, 13, 16, 13));
		put("water_bowl", box(0, 0, 0, 16, 5, 16));
		put("fish_bowl", box(1, 0, 1, 15, 6, 15));
		put("fish_tank", Shapes.join(Shapes.block(), box(1, 1, 1, 15, 16, 15), BooleanOp.ONLY_FIRST));
		put("water_tank", Shapes.join(box(1, 0, 1, 15, 16, 15), box(3, 3, 3, 13, 16, 13), BooleanOp.ONLY_FIRST));
		put("oil_lamp", box(5, 4, 8, 11, 12, 16));
		put("empty_candlestick", box(5, 5, 5, 11, 16, 16));
		put("factory_lamp", box(4, 4, 8, 12, 12, 16));
		put("fan", box(1, 1, 10, 15, 15, 16));
		put("screen", box(0, 0, 14, 16, 16, 16));
		put("screen2", box(0, 0, 13, 16, 16, 14));
		put("vent_fan", box(0, 0, 2, 16, 16, 14));
		put("tech_table", Shapes.or(box(2, 0, 2, 14, 10, 14), box(0, 10, 0, 16, 16, 16)));
		put("hologram_base", box(1, 0, 1, 15, 2, 15));
		put("air_duct", Shapes.join(
				Shapes.or(
						box(0, 0, 12, 16, 16, 16),
						box(0, -2, 10, 16, 16, 12),
						box(0, -4, 8, 16, 16, 10),
						box(0, -6, 6, 16, 16, 8),
						box(0, -8, 4, 16, 16, 6),
						box(0, -10, 2, 16, 14, 4),
						box(0, -12, 0, 16, 12, 2)),
				Shapes.or(
						box(2, 2, 12, 14, 14, 16),
						box(2, 0, 10, 14, 14, 12),
						box(2, -2, 8, 14, 14, 10),
						box(2, -4, 6, 14, 14, 8),
						box(2, -6, 4, 14, 14, 6),
						box(2, -8, 2, 14, 12, 4),
						box(2, -10, 0, 14, 10, 2)),
				BooleanOp.ONLY_FIRST));
		put("air_duct2", Shapes.join(
				Shapes.or(
						box(0, 0, 12, 16, 16, 16),
						box(0, 0, 10, 16, 18, 12),
						box(0, 0, 8, 16, 20, 10),
						box(0, 0, 6, 16, 22, 8),
						box(0, 0, 4, 16, 24, 6),
						box(0, 2, 2, 16, 26, 4),
						box(0, 4, 0, 16, 28, 2)),
				Shapes.or(
						box(2, 2, 12, 14, 14, 16),
						box(2, 2, 10, 14, 16, 12),
						box(2, 2, 8, 14, 18, 10),
						box(2, 2, 6, 14, 20, 8),
						box(2, 2, 4, 14, 22, 6),
						box(2, 4, 2, 14, 24, 4),
						box(2, 6, 0, 14, 26, 2)),
				BooleanOp.ONLY_FIRST));
		put("maya_crystal_skull", Shapes.or(box(2, 0, 2, 14, 2, 14), box(4, 2, 4, 12, 10, 12)));
		put("dessert", box(1, 0, 1, 15, 2, 15));
		put("ladder", box(0, 0, 13, 16, 16, 16));
		put("safety_ladder", Shapes.join(Shapes.block(), box(1, 0, 1, 15, 16, 13), BooleanOp.ONLY_FIRST));
		put("hollow_steel_half_beam_floor", box(3, -4, 0, 13, 4, 16));
		put("hollow_steel_half_beam_ceiling", box(3, 12, 0, 13, 20, 16));
		put("hollow_steel_half_beam_wall", box(3, 0, 12, 13, 16, 20));
		put("factory_light_bar", box(13, 2, 0, 16, 6, 16));
		put("wall_base", Shapes.or(
				box(2, 0, 0, 16, 8, 16),
				box(5, 8, 0, 16, 13, 16),
				box(3, 13, 0, 16, 16, 16)));
		put("wall_base2", Shapes.or(
				box(2, 8, 0, 16, 16, 16),
				box(5, 3, 0, 16, 8, 16),
				box(3, 0, 0, 16, 3, 16)));
		put("meiren_kao", Shapes.or(
				box(0, 0, 0, 16, 8, 16),
				box(8, 8, 0, 16, 16, 16)));
		put("column", box(4, 0, 4, 12, 16, 12));
		//noinspection DataFlowIssue
		put("meiren_kao_with_column", Shapes.or(getInstance().get(XKDeco.id("meiren_kao")), getInstance().get(XKDeco.id("column"))));
	}

	private final Map<ResourceLocation, VoxelShape> shapes = Maps.newHashMap();

	public ShapeStorage() {
		register(new ResourceLocation("empty"), Shapes.empty());
		register(new ResourceLocation("block"), Shapes.block());
	}

	@Nullable
	public VoxelShape get(ResourceLocation id) {
		return this.shapes.get(id);
	}

	@Nullable
	public VoxelShape get(String id) {
		return get(new ResourceLocation(id));
	}

	private void register(ResourceLocation id, VoxelShape shape) {
		Preconditions.checkNotNull(shape, "Shape cannot be null");
		Preconditions.checkState(shapes.put(id, shape) == null, "Duplicate shape id: %s", id);
	}
}
