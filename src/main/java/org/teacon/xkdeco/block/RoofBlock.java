package org.teacon.xkdeco.block;

import static org.teacon.xkdeco.util.RoofUtil.RoofHalf;
import static org.teacon.xkdeco.util.RoofUtil.RoofShape;

import java.util.List;
import java.util.Locale;

import org.teacon.xkdeco.util.RoofUtil;
import org.teacon.xkdeco.util.StringProperty;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public final class RoofBlock extends BasicBlock {
	public static final StringProperty VARIANT = StringProperty.convert(XKDStateProperties.ROOF_VARIANT);
	public static final StringProperty SHAPE = StringProperty.convert(EnumProperty.create("shape", RoofShape.class));
	public static final StringProperty HALF = StringProperty.convert(XKDStateProperties.ROOF_HALF);

	public static final VoxelShape ROOF_E = Shapes.or(Block.box(0, 8, 0, 8, 16, 16), Block.box(0, 0, 0, 16, 8, 16));
	public static final VoxelShape ROOF_INNER_EN = Shapes.or(
			Block.box(0, 8, 0, 8, 16, 8),
			Block.box(0, 8, 8, 16, 16, 16),
			Block.box(0, 0, 0, 16, 8, 16));
	public static final VoxelShape ROOF_INNER_NW = Shapes.or(
			Block.box(0, 8, 8, 8, 16, 16),
			Block.box(8, 8, 0, 16, 16, 16),
			Block.box(0, 0, 0, 16, 8, 16));
	public static final VoxelShape ROOF_INNER_SE = Shapes.or(
			Block.box(8, 8, 0, 16, 16, 8),
			Block.box(0, 8, 0, 8, 16, 16),
			Block.box(0, 0, 0, 16, 8, 16));
	public static final VoxelShape ROOF_INNER_BASE_EN = Shapes.or(
			Block.box(0, 16, 0, 8, 24, 8),
			Block.box(0, 16, 8, 16, 24, 16),
			Block.box(0, 8, 0, 16, 16, 16));
	public static final VoxelShape ROOF_INNER_BASE_NW = Shapes.or(
			Block.box(0, 16, 8, 8, 24, 16),
			Block.box(8, 16, 0, 16, 24, 16),
			Block.box(0, 8, 0, 16, 16, 16));
	public static final VoxelShape ROOF_INNER_BASE_SE = Shapes.or(
			Block.box(8, 16, 0, 16, 24, 8),
			Block.box(0, 16, 0, 8, 24, 16),
			Block.box(0, 8, 0, 16, 16, 16));
	public static final VoxelShape ROOF_INNER_BASE_WS = Shapes.or(
			Block.box(8, 16, 8, 16, 24, 16),
			Block.box(0, 16, 0, 16, 24, 8),
			Block.box(0, 8, 0, 16, 16, 16));
	public static final VoxelShape ROOF_INNER_WS = Shapes.or(
			Block.box(8, 8, 8, 16, 16, 16),
			Block.box(0, 8, 0, 16, 16, 8),
			Block.box(0, 0, 0, 16, 8, 16));
	public static final VoxelShape ROOF_N = Shapes.or(Block.box(0, 8, 8, 16, 16, 16), Block.box(0, 0, 0, 16, 8, 16));
	public static final VoxelShape ROOF_OUTER_EN = Shapes.or(Block.box(0, 8, 8, 8, 16, 16), Block.box(0, 0, 0, 16, 8, 16));
	public static final VoxelShape ROOF_OUTER_NW = Shapes.or(Block.box(8, 8, 8, 16, 16, 16), Block.box(0, 0, 0, 16, 8, 16));
	public static final VoxelShape ROOF_OUTER_SE = Shapes.or(Block.box(0, 8, 0, 8, 16, 8), Block.box(0, 0, 0, 16, 8, 16));
	public static final VoxelShape ROOF_OUTER_BASE_EN = Shapes.or(Block.box(0, 16, 8, 8, 24, 16), Block.box(0, 8, 0, 16, 16, 16));
	public static final VoxelShape ROOF_OUTER_BASE_NW = Shapes.or(Block.box(8, 16, 8, 16, 24, 16), Block.box(0, 8, 0, 16, 16, 16));
	public static final VoxelShape ROOF_OUTER_BASE_SE = Shapes.or(Block.box(0, 16, 0, 8, 24, 8), Block.box(0, 8, 0, 16, 16, 16));
	public static final VoxelShape ROOF_OUTER_BASE_WS = Shapes.or(Block.box(8, 16, 0, 16, 24, 8), Block.box(0, 8, 0, 16, 16, 16));
	public static final VoxelShape ROOF_OUTER_WS = Shapes.or(Block.box(8, 8, 0, 16, 16, 8), Block.box(0, 0, 0, 16, 8, 16));
	public static final VoxelShape ROOF_S = Shapes.or(Block.box(0, 8, 0, 16, 16, 8), Block.box(0, 0, 0, 16, 8, 16));
	public static final VoxelShape ROOF_BASE_E = Shapes.or(Block.box(0, 16, 0, 8, 24, 16), Block.box(0, 8, 0, 16, 16, 16));
	public static final VoxelShape ROOF_BASE_N = Shapes.or(Block.box(0, 16, 8, 16, 24, 16), Block.box(0, 8, 0, 16, 16, 16));
	public static final VoxelShape ROOF_BASE_S = Shapes.or(Block.box(0, 16, 0, 16, 24, 8), Block.box(0, 8, 0, 16, 16, 16));
	public static final VoxelShape ROOF_BASE_W = Shapes.or(Block.box(8, 16, 0, 16, 24, 16), Block.box(0, 8, 0, 16, 16, 16));
	public static final VoxelShape ROOF_W = Shapes.or(Block.box(8, 8, 0, 16, 16, 16), Block.box(0, 0, 0, 16, 8, 16));
	public static final VoxelShape SLOW_ROOF_E = Shapes.or(Block.box(0, 4, 0, 8, 8, 16), Block.box(0, 0, 0, 16, 4, 16));
	public static final VoxelShape SLOW_ROOF_INNER_EN = Shapes.or(
			Block.box(0, 4, 8, 16, 8, 16),
			Block.box(0, 4, 0, 8, 8, 8),
			Block.box(0, 0, 0, 16, 4, 16));
	public static final VoxelShape SLOW_ROOF_INNER_NW = Shapes.or(
			Block.box(8, 4, 0, 16, 8, 16),
			Block.box(0, 4, 8, 8, 8, 16),
			Block.box(0, 0, 0, 16, 4, 16));
	public static final VoxelShape SLOW_ROOF_INNER_SE = Shapes.or(
			Block.box(0, 4, 0, 8, 8, 16),
			Block.box(8, 4, 0, 16, 8, 8),
			Block.box(0, 0, 0, 16, 4, 16));
	public static final VoxelShape SLOW_ROOF_INNER_BASE_EN = Shapes.or(
			Block.box(0, 12, 8, 16, 16, 16),
			Block.box(0, 12, 0, 8, 16, 8),
			Block.box(0, 0, 0, 16, 12, 16));
	public static final VoxelShape SLOW_ROOF_INNER_BASE_NW = Shapes.or(
			Block.box(8, 12, 0, 16, 16, 16),
			Block.box(0, 12, 8, 8, 16, 16),
			Block.box(0, 0, 0, 16, 12, 16));
	public static final VoxelShape SLOW_ROOF_INNER_BASE_SE = Shapes.or(
			Block.box(0, 12, 0, 8, 16, 16),
			Block.box(8, 12, 0, 16, 16, 8),
			Block.box(0, 0, 0, 16, 12, 16));
	public static final VoxelShape SLOW_ROOF_INNER_BASE_WS = Shapes.or(
			Block.box(0, 12, 0, 16, 16, 8),
			Block.box(8, 12, 8, 16, 16, 16),
			Block.box(0, 0, 0, 16, 12, 16));
	public static final VoxelShape SLOW_ROOF_INNER_WS = Shapes.or(
			Block.box(0, 4, 0, 16, 8, 8),
			Block.box(8, 4, 8, 16, 8, 16),
			Block.box(0, 0, 0, 16, 4, 16));
	public static final VoxelShape SLOW_ROOF_N = Shapes.or(Block.box(0, 4, 8, 16, 8, 16), Block.box(0, 0, 0, 16, 4, 16));
	public static final VoxelShape SLOW_ROOF_OUTER_EN = Shapes.or(Block.box(0, 4, 8, 8, 8, 16), Block.box(0, 0, 0, 16, 4, 16));
	public static final VoxelShape SLOW_ROOF_OUTER_NW = Shapes.or(Block.box(8, 4, 8, 16, 8, 16), Block.box(0, 0, 0, 16, 4, 16));
	public static final VoxelShape SLOW_ROOF_OUTER_SE = Shapes.or(Block.box(0, 4, 0, 8, 8, 8), Block.box(0, 0, 0, 16, 4, 16));
	public static final VoxelShape SLOW_ROOF_OUTER_BASE_EN = Shapes.or(Block.box(0, 12, 8, 8, 16, 16), Block.box(0, 0, 0, 16, 12, 16));
	public static final VoxelShape SLOW_ROOF_OUTER_BASE_NW = Shapes.or(Block.box(8, 12, 8, 16, 16, 16), Block.box(0, 0, 0, 16, 12, 16));
	public static final VoxelShape SLOW_ROOF_OUTER_BASE_SE = Shapes.or(Block.box(0, 12, 0, 8, 16, 8), Block.box(0, 0, 0, 16, 12, 16));
	public static final VoxelShape SLOW_ROOF_OUTER_BASE_WS = Shapes.or(Block.box(8, 12, 0, 16, 16, 8), Block.box(0, 0, 0, 16, 12, 16));
	public static final VoxelShape SLOW_ROOF_OUTER_WS = Shapes.or(Block.box(8, 4, 0, 16, 8, 8), Block.box(0, 0, 0, 16, 4, 16));
	public static final VoxelShape SLOW_ROOF_S = Shapes.or(Block.box(0, 4, 0, 16, 8, 8), Block.box(0, 0, 0, 16, 4, 16));
	public static final VoxelShape SLOW_ROOF_BASE_E = Shapes.or(Block.box(0, 12, 0, 8, 16, 16), Block.box(0, 0, 0, 16, 12, 16));
	public static final VoxelShape SLOW_ROOF_BASE_N = Shapes.or(Block.box(0, 12, 8, 16, 16, 16), Block.box(0, 0, 0, 16, 12, 16));
	public static final VoxelShape SLOW_ROOF_BASE_S = Shapes.or(Block.box(0, 12, 0, 16, 16, 8), Block.box(0, 0, 0, 16, 12, 16));
	public static final VoxelShape SLOW_ROOF_BASE_W = Shapes.or(Block.box(8, 12, 0, 16, 16, 16), Block.box(0, 0, 0, 16, 12, 16));
	public static final VoxelShape SLOW_ROOF_W = Shapes.or(Block.box(8, 4, 0, 16, 8, 16), Block.box(0, 0, 0, 16, 4, 16));
	public static final VoxelShape STEEP_ROOF_E = Shapes.or(Block.box(0, 8, 0, 4, 16, 16), Block.box(0, 0, 0, 8, 8, 16));
	public static final VoxelShape STEEP_ROOF_INNER_EN = Shapes.or(Block.box(0, 8, 12, 16, 16, 16), Block.box(0, 8, 0, 4, 16, 12));
	public static final VoxelShape STEEP_ROOF_INNER_NW = Shapes.or(Block.box(12, 8, 0, 16, 16, 16), Block.box(0, 8, 12, 12, 16, 16));
	public static final VoxelShape STEEP_ROOF_INNER_SE = Shapes.or(Block.box(0, 8, 0, 4, 16, 16), Block.box(4, 8, 0, 16, 16, 4));
	public static final VoxelShape STEEP_ROOF_INNER_BASE_EN = Shapes.or(
			Block.box(0, 8, 4, 16, 16, 16),
			Block.box(0, 8, 0, 12, 16, 4),
			Block.box(0, 0, 0, 16, 8, 16));
	public static final VoxelShape STEEP_ROOF_INNER_BASE_NW = Shapes.or(
			Block.box(4, 8, 0, 16, 16, 16),
			Block.box(0, 8, 4, 4, 16, 16),
			Block.box(0, 0, 0, 16, 8, 16));
	public static final VoxelShape STEEP_ROOF_INNER_BASE_SE = Shapes.or(
			Block.box(0, 8, 0, 12, 16, 16),
			Block.box(12, 8, 0, 16, 16, 12),
			Block.box(0, 0, 0, 16, 8, 16));
	public static final VoxelShape STEEP_ROOF_INNER_BASE_WS = Shapes.or(
			Block.box(0, 8, 0, 16, 16, 12),
			Block.box(4, 8, 12, 16, 16, 16),
			Block.box(0, 0, 0, 16, 8, 16));
	public static final VoxelShape STEEP_ROOF_INNER_WS = Shapes.or(Block.box(0, 8, 0, 16, 16, 4), Block.box(12, 8, 4, 16, 16, 16));
	public static final VoxelShape STEEP_ROOF_N = Shapes.or(Block.box(0, 8, 12, 16, 16, 16), Block.box(0, 0, 8, 16, 8, 16));
	public static final VoxelShape STEEP_ROOF_OUTER_EN = Shapes.or(Block.box(0, 0, 8, 8, 8, 16), Block.box(0, 8, 12, 4, 16, 16));
	public static final VoxelShape STEEP_ROOF_OUTER_NW = Shapes.or(Block.box(8, 0, 8, 16, 8, 16), Block.box(12, 8, 12, 16, 16, 16));
	public static final VoxelShape STEEP_ROOF_OUTER_SE = Shapes.or(Block.box(0, 0, 0, 8, 8, 8), Block.box(0, 8, 0, 4, 16, 4));
	public static final VoxelShape STEEP_ROOF_OUTER_BASE_EN = Shapes.or(Block.box(0, 8, 4, 12, 16, 16), Block.box(0, 0, 0, 16, 8, 16));
	public static final VoxelShape STEEP_ROOF_OUTER_BASE_NW = Shapes.or(Block.box(4, 8, 4, 16, 16, 16), Block.box(0, 0, 0, 16, 8, 16));
	public static final VoxelShape STEEP_ROOF_OUTER_BASE_SE = Shapes.or(Block.box(0, 8, 0, 12, 16, 12), Block.box(0, 0, 0, 16, 8, 16));
	public static final VoxelShape STEEP_ROOF_OUTER_BASE_WS = Shapes.or(Block.box(4, 8, 0, 16, 16, 12), Block.box(0, 0, 0, 16, 8, 16));
	public static final VoxelShape STEEP_ROOF_OUTER_WS = Shapes.or(Block.box(8, 0, 0, 16, 8, 8), Block.box(12, 8, 0, 16, 16, 4));
	public static final VoxelShape STEEP_ROOF_S = Shapes.or(Block.box(0, 8, 0, 16, 16, 4), Block.box(0, 0, 0, 16, 8, 8));
	public static final VoxelShape STEEP_ROOF_BASE_E = Shapes.or(Block.box(0, 8, 0, 12, 16, 16), Block.box(0, 0, 0, 16, 8, 16));
	public static final VoxelShape STEEP_ROOF_BASE_N = Shapes.or(Block.box(0, 8, 4, 16, 16, 16), Block.box(0, 0, 0, 16, 8, 16));
	public static final VoxelShape STEEP_ROOF_BASE_S = Shapes.or(Block.box(0, 8, 0, 16, 16, 12), Block.box(0, 0, 0, 16, 8, 16));
	public static final VoxelShape STEEP_ROOF_BASE_W = Shapes.or(Block.box(4, 8, 0, 16, 16, 16), Block.box(0, 0, 0, 16, 8, 16));
	public static final VoxelShape STEEP_ROOF_W = Shapes.or(Block.box(12, 8, 0, 16, 16, 16), Block.box(8, 0, 0, 16, 8, 16));

	public static final List<VoxelShape> ROOF_SHAPES = List.of(
			Shapes.block(), ROOF_OUTER_EN, ROOF_E, ROOF_INNER_SE,
			ROOF_INNER_WS, Shapes.block(), ROOF_OUTER_SE, ROOF_S,
			ROOF_W, ROOF_INNER_NW, Shapes.block(), ROOF_OUTER_WS,
			ROOF_OUTER_NW, ROOF_N, ROOF_INNER_EN, Shapes.block(),
			Shapes.block(), SLOW_ROOF_OUTER_EN, SLOW_ROOF_E, SLOW_ROOF_INNER_SE,
			SLOW_ROOF_INNER_WS, Shapes.block(), SLOW_ROOF_OUTER_SE, SLOW_ROOF_S,
			SLOW_ROOF_W, SLOW_ROOF_INNER_NW, Shapes.block(), SLOW_ROOF_OUTER_WS,
			SLOW_ROOF_OUTER_NW, SLOW_ROOF_N, SLOW_ROOF_INNER_EN, Shapes.block(),
			Shapes.block(), STEEP_ROOF_OUTER_EN, STEEP_ROOF_E, STEEP_ROOF_INNER_SE,
			STEEP_ROOF_INNER_WS, Shapes.block(), STEEP_ROOF_OUTER_SE, STEEP_ROOF_S,
			STEEP_ROOF_W, STEEP_ROOF_INNER_NW, Shapes.block(), STEEP_ROOF_OUTER_WS,
			STEEP_ROOF_OUTER_NW, STEEP_ROOF_N, STEEP_ROOF_INNER_EN, Shapes.block());
	public static final List<VoxelShape> ROOF_BASE_SHAPES = List.of(
			Shapes.block(), ROOF_OUTER_BASE_EN, ROOF_BASE_E, ROOF_INNER_BASE_SE,
			ROOF_INNER_BASE_WS, Shapes.block(), ROOF_OUTER_BASE_SE, ROOF_BASE_S,
			ROOF_BASE_W, ROOF_INNER_BASE_NW, Shapes.block(), ROOF_OUTER_BASE_WS,
			ROOF_OUTER_BASE_NW, ROOF_BASE_N, ROOF_INNER_BASE_EN, Shapes.block(),
			Shapes.block(), SLOW_ROOF_OUTER_BASE_EN, SLOW_ROOF_BASE_E, SLOW_ROOF_INNER_BASE_SE,
			SLOW_ROOF_INNER_BASE_WS, Shapes.block(), SLOW_ROOF_OUTER_BASE_SE, SLOW_ROOF_BASE_S,
			SLOW_ROOF_BASE_W, SLOW_ROOF_INNER_BASE_NW, Shapes.block(), SLOW_ROOF_OUTER_BASE_WS,
			SLOW_ROOF_OUTER_BASE_NW, SLOW_ROOF_BASE_N, SLOW_ROOF_INNER_BASE_EN, Shapes.block(),
			Shapes.block(), STEEP_ROOF_OUTER_BASE_EN, STEEP_ROOF_BASE_E, STEEP_ROOF_INNER_BASE_SE,
			STEEP_ROOF_INNER_BASE_WS, Shapes.block(), STEEP_ROOF_OUTER_BASE_SE, STEEP_ROOF_BASE_S,
			STEEP_ROOF_BASE_W, STEEP_ROOF_INNER_BASE_NW, Shapes.block(), STEEP_ROOF_OUTER_BASE_WS,
			STEEP_ROOF_OUTER_BASE_NW, STEEP_ROOF_BASE_N, STEEP_ROOF_INNER_BASE_EN, Shapes.block());

	public RoofBlock(Properties properties) {
		super(properties);
	}

	@Override
	@SuppressWarnings("deprecation")
	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		var facing = pState.getValue(HorizontalDirectionalBlock.FACING);
		var roofHalf = RoofHalf.valueOf(pState.getValue(HALF).toUpperCase(Locale.ENGLISH));
		var roofVariant = RoofUtil.RoofVariant.valueOf(pState.getValue(VARIANT).toUpperCase(Locale.ENGLISH));
		var roofShape = RoofShape.valueOf(pState.getValue(SHAPE).toUpperCase(Locale.ENGLISH));
		return RoofUtil.getShape(roofShape, facing, roofHalf, roofVariant);
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {
		return false;
	}
}
