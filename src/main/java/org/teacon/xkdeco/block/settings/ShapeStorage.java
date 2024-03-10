package org.teacon.xkdeco.block.settings;

import static net.minecraft.world.level.block.Block.box;

import java.util.Map;

import org.jetbrains.annotations.Nullable;
import org.teacon.xkdeco.XKDeco;
import org.teacon.xkdeco.block.HollowBlock;

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
		put("big_table", HollowBlock.BIG_TABLE_SHAPE);
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
