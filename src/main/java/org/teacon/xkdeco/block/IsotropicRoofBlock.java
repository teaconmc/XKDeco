package org.teacon.xkdeco.block;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.function.TriFunction;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

import static org.teacon.xkdeco.block.IsotropicRoofBlock.RoofHalf.BASE;
import static org.teacon.xkdeco.block.IsotropicRoofBlock.RoofHalf.TIP;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public final class IsotropicRoofBlock extends Block implements SimpleWaterloggedBlock, XKDecoBlock.Isotropic {
    public static final EnumProperty<RoofVariant> VARIANT = EnumProperty.create("variant", RoofVariant.class);
    public static final EnumProperty<RoofShape> SHAPE = EnumProperty.create("shape", RoofShape.class);
    public static final EnumProperty<RoofHalf> HALF = EnumProperty.create("half", RoofHalf.class);
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public static final VoxelShape ROOF_E = Shapes.or(Block.box(0, 8, 0, 8, 16, 16), Block.box(0, 0, 0, 16, 8, 16));
    public static final VoxelShape ROOF_INNER_EN = Shapes.or(Block.box(0, 8, 0, 8, 16, 8), Block.box(0, 8, 8, 16, 16, 16), Block.box(0, 0, 0, 16, 8, 16));
    public static final VoxelShape ROOF_INNER_NW = Shapes.or(Block.box(0, 8, 8, 8, 16, 16), Block.box(8, 8, 0, 16, 16, 16), Block.box(0, 0, 0, 16, 8, 16));
    public static final VoxelShape ROOF_INNER_SE = Shapes.or(Block.box(8, 8, 0, 16, 16, 8), Block.box(0, 8, 0, 8, 16, 16), Block.box(0, 0, 0, 16, 8, 16));
    public static final VoxelShape ROOF_INNER_BASE_EN = Shapes.or(Block.box(0, 16, 0, 8, 24, 8), Block.box(0, 16, 8, 16, 24, 16), Block.box(0, 8, 0, 16, 16, 16));
    public static final VoxelShape ROOF_INNER_BASE_NW = Shapes.or(Block.box(0, 16, 8, 8, 24, 16), Block.box(8, 16, 0, 16, 24, 16), Block.box(0, 8, 0, 16, 16, 16));
    public static final VoxelShape ROOF_INNER_BASE_SE = Shapes.or(Block.box(8, 16, 0, 16, 24, 8), Block.box(0, 16, 0, 8, 24, 16), Block.box(0, 8, 0, 16, 16, 16));
    public static final VoxelShape ROOF_INNER_BASE_WS = Shapes.or(Block.box(8, 16, 8, 16, 24, 16), Block.box(0, 16, 0, 16, 24, 8), Block.box(0, 8, 0, 16, 16, 16));
    public static final VoxelShape ROOF_INNER_WS = Shapes.or(Block.box(8, 8, 8, 16, 16, 16), Block.box(0, 8, 0, 16, 16, 8), Block.box(0, 0, 0, 16, 8, 16));
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
    public static final VoxelShape SLOW_ROOF_INNER_EN = Shapes.or(Block.box(0, 4, 8, 16, 8, 16), Block.box(0, 4, 0, 8, 8, 8), Block.box(0, 0, 0, 16, 4, 16));
    public static final VoxelShape SLOW_ROOF_INNER_NW = Shapes.or(Block.box(8, 4, 0, 16, 8, 16), Block.box(0, 4, 8, 8, 8, 16), Block.box(0, 0, 0, 16, 4, 16));
    public static final VoxelShape SLOW_ROOF_INNER_SE = Shapes.or(Block.box(0, 4, 0, 8, 8, 16), Block.box(8, 4, 0, 16, 8, 8), Block.box(0, 0, 0, 16, 4, 16));
    public static final VoxelShape SLOW_ROOF_INNER_BASE_EN = Shapes.or(Block.box(0, 12, 8, 16, 16, 16), Block.box(0, 12, 0, 8, 16, 8), Block.box(0, 0, 0, 16, 12, 16));
    public static final VoxelShape SLOW_ROOF_INNER_BASE_NW = Shapes.or(Block.box(8, 12, 0, 16, 16, 16), Block.box(0, 12, 8, 8, 16, 16), Block.box(0, 0, 0, 16, 12, 16));
    public static final VoxelShape SLOW_ROOF_INNER_BASE_SE = Shapes.or(Block.box(0, 12, 0, 8, 16, 16), Block.box(8, 12, 0, 16, 16, 8), Block.box(0, 0, 0, 16, 12, 16));
    public static final VoxelShape SLOW_ROOF_INNER_BASE_WS = Shapes.or(Block.box(0, 12, 0, 16, 16, 8), Block.box(8, 12, 8, 16, 16, 16), Block.box(0, 0, 0, 16, 12, 16));
    public static final VoxelShape SLOW_ROOF_INNER_WS = Shapes.or(Block.box(0, 4, 0, 16, 8, 8), Block.box(8, 4, 8, 16, 8, 16), Block.box(0, 0, 0, 16, 4, 16));
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
    public static final VoxelShape STEEP_ROOF_INNER_BASE_EN = Shapes.or(Block.box(0, 8, 4, 16, 16, 16), Block.box(0, 8, 0, 12, 16, 4), Block.box(0, 0, 0, 16, 8, 16));
    public static final VoxelShape STEEP_ROOF_INNER_BASE_NW = Shapes.or(Block.box(4, 8, 0, 16, 16, 16), Block.box(0, 8, 4, 4, 16, 16), Block.box(0, 0, 0, 16, 8, 16));
    public static final VoxelShape STEEP_ROOF_INNER_BASE_SE = Shapes.or(Block.box(0, 8, 0, 12, 16, 16), Block.box(12, 8, 0, 16, 16, 12), Block.box(0, 0, 0, 16, 8, 16));
    public static final VoxelShape STEEP_ROOF_INNER_BASE_WS = Shapes.or(Block.box(0, 8, 0, 16, 16, 12), Block.box(4, 8, 12, 16, 16, 16), Block.box(0, 0, 0, 16, 8, 16));
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

    public IsotropicRoofBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(VARIANT, RoofVariant.NORMAL).setValue(SHAPE, RoofShape.STRAIGHT)
                .setValue(HALF, TIP).setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false));
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean useShapeForLightOcclusion(BlockState pState) {
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        var leftRight = getConnectionLeftRight(pState.getValue(FACING), pState.getValue(SHAPE));
        var leftRightIndex = leftRight.getLeft().get2DDataValue() * 4 + leftRight.getRight().get2DDataValue();
        return switch (pState.getValue(HALF)) {
            case TIP -> ROOF_SHAPES.get(pState.getValue(VARIANT).ordinal() * 16 + leftRightIndex);
            case BASE -> ROOF_BASE_SHAPES.get(pState.getValue(VARIANT).ordinal() * 16 + leftRightIndex);
        };
    }

    @Override
    @SuppressWarnings("deprecation")
    public FluidState getFluidState(BlockState pState) {
        return pState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {
        return false;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        for (var trial : List.of(
                // try to become a non-straight roof matching the triangular side of the roof at front/back
                tryConnectTo(Rotation.FRONT,
                        (r, s, h, v) -> isHalfOpenClockWiseSide(s, r, Rotation.BACK),
                        (curr, tgt) -> curr.setValue(VARIANT, tgt.getValue(VARIANT)).setValue(SHAPE, RoofShape.OUTER).setValue(HALF, tgt.getValue(HALF)).setValue(FACING, curr.getValue(FACING).getCounterClockWise())),
                tryConnectTo(Rotation.FRONT,
                        (r, s, h, v) -> isHalfOpenCounterClockWiseSide(s, r, Rotation.BACK),
                        (curr, tgt) -> curr.setValue(VARIANT, tgt.getValue(VARIANT)).setValue(SHAPE, RoofShape.OUTER).setValue(HALF, tgt.getValue(HALF)).setValue(FACING, curr.getValue(FACING))),
                tryConnectTo(Rotation.BACK,
                        (r, s, h, v) -> isHalfOpenCounterClockWiseSide(s, r, Rotation.FRONT),
                        (curr, tgt) -> curr.setValue(VARIANT, tgt.getValue(VARIANT)).setValue(SHAPE, RoofShape.INNER).setValue(HALF, tgt.getValue(HALF)).setValue(FACING, curr.getValue(FACING).getCounterClockWise())),
                tryConnectTo(Rotation.BACK,
                        (r, s, h, v) -> isHalfOpenClockWiseSide(s, r, Rotation.FRONT),
                        (curr, tgt) -> curr.setValue(VARIANT, tgt.getValue(VARIANT)).setValue(SHAPE, RoofShape.INNER).setValue(HALF, tgt.getValue(HALF)).setValue(FACING, curr.getValue(FACING))),

                // try to become a slow, non-straight roof matching the roof at left/right
                tryConnectTo(Rotation.LEFT,
                        (r, s, h, v) -> r == Rotation.RIGHT && s == RoofShape.STRAIGHT && h == TIP && v == RoofVariant.SLOW,
                        (curr, tgt) -> curr.setValue(SHAPE, RoofShape.OUTER).setValue(HALF, BASE).setValue(VARIANT, RoofVariant.SLOW).setValue(FACING, tgt.getValue(FACING).getCounterClockWise())),
                tryConnectTo(Rotation.RIGHT,
                        (r, s, h, v) -> r == Rotation.LEFT && s == RoofShape.STRAIGHT && h == TIP && v == RoofVariant.SLOW,
                        (curr, tgt) -> curr.setValue(SHAPE, RoofShape.OUTER).setValue(HALF, BASE).setValue(VARIANT, RoofVariant.SLOW).setValue(FACING, tgt.getValue(FACING))),
                tryConnectTo(Rotation.LEFT,
                        (r, s, h, v) -> r == Rotation.LEFT && s == RoofShape.STRAIGHT && h == BASE && v == RoofVariant.SLOW,
                        (curr, tgt) -> curr.setValue(SHAPE, RoofShape.INNER).setValue(HALF, TIP).setValue(VARIANT, RoofVariant.SLOW).setValue(FACING, tgt.getValue(FACING))),
                tryConnectTo(Rotation.RIGHT,
                        (r, s, h, v) -> r == Rotation.RIGHT && s == RoofShape.STRAIGHT && h == BASE && v == RoofVariant.SLOW,
                        (curr, tgt) -> curr.setValue(SHAPE, RoofShape.INNER).setValue(HALF, TIP).setValue(VARIANT, RoofVariant.SLOW).setValue(FACING, tgt.getValue(FACING).getCounterClockWise())),

                // adjust self to become a slow, straight roof
                tryConnectTo(Rotation.FRONT,
                        (r, s, h, v) -> r == Rotation.FRONT && s == RoofShape.STRAIGHT && h == TIP && v == RoofVariant.NORMAL
                                || isClosedSide(s, r, Rotation.BACK) && h == BASE && v == RoofVariant.SLOW,
                        (curr, tgt) -> curr.setValue(HALF, TIP).setValue(VARIANT, RoofVariant.SLOW)),
                tryConnectTo(Rotation.BACK,
                        (r, s, h, v) -> r == Rotation.FRONT && s == RoofShape.STRAIGHT && h == TIP && v == RoofVariant.NORMAL
                                || isOpenSide(s, r, Rotation.FRONT) && h == TIP && v == RoofVariant.SLOW,
                        (curr, tgt) -> curr.setValue(HALF, BASE).setValue(VARIANT, RoofVariant.SLOW)),

                // adjust self to become a steep, straight roof
                tryConnectTo(Rotation.UP,
                        (r, s, h, v) -> r == Rotation.FRONT && h == TIP && (v == RoofVariant.NORMAL || v == RoofVariant.STEEP),
                        (curr, tgt) -> curr.setValue(HALF, BASE).setValue(VARIANT, RoofVariant.STEEP)),
                tryConnectTo(Rotation.DOWN,
                        (r, s, h, v) -> r == Rotation.FRONT && (h == TIP && v == RoofVariant.NORMAL || h == BASE && v == RoofVariant.STEEP),
                        (curr, tgt) -> curr.setValue(HALF, TIP).setValue(VARIANT, RoofVariant.STEEP))
        )) {
            var result = trial.apply(pContext.getLevel(), pContext.getClickedPos(), pContext.getHorizontalDirection());
            if (result.isPresent()) return result.get();
        }

        return this.defaultBlockState()
                .setValue(FACING, pContext.getHorizontalDirection())
                .setValue(WATERLOGGED, pContext.getLevel().getFluidState(pContext.getClickedPos()).getType() == Fluids.WATER);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState,
                                  LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        if (pState.getValue(WATERLOGGED)) {
            pLevel.scheduleTick(pCurrentPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
        }

        if (!isRoof(pFacingState)) return pState;

        var currDirection = pState.getValue(FACING);
        var currHalf = pState.getValue(HALF);
        var currShape = pState.getValue(SHAPE);
        var currVariant = pState.getValue(VARIANT);

        var facingDirection = pFacingState.getValue(FACING);
        var facingHalf = pFacingState.getValue(HALF);
        var facingShape = pFacingState.getValue(SHAPE);
        var facingVariant = pFacingState.getValue(VARIANT);

        var rotation = Rotation.fromDirections(currDirection, pFacing);

        switch (rotation) {
            case LEFT -> {
            }
            case FRONT -> {
                if (facingVariant == RoofVariant.SLOW && facingShape == RoofShape.STRAIGHT && facingHalf == BASE
                        && currDirection == facingDirection
                        && currVariant == RoofVariant.NORMAL && currShape == RoofShape.STRAIGHT) {
                    return pState.setValue(VARIANT, RoofVariant.SLOW).setValue(HALF, TIP);
                }
            }
            case RIGHT -> {
            }
            case BACK -> {
                if (facingVariant == RoofVariant.SLOW && facingShape == RoofShape.STRAIGHT && facingHalf == TIP
                        && currDirection == facingDirection
                        && currVariant == RoofVariant.NORMAL && currShape == RoofShape.STRAIGHT) {
                    return pState.setValue(VARIANT, RoofVariant.SLOW).setValue(HALF, BASE);
                }
            }
            case UP -> {
                if (facingVariant == RoofVariant.STEEP && facingShape == RoofShape.STRAIGHT && facingHalf == TIP
                        && currVariant == RoofVariant.NORMAL && currShape == RoofShape.STRAIGHT) {
                    return pState.setValue(VARIANT, RoofVariant.STEEP).setValue(HALF, BASE);
                }
            }
            case DOWN -> {
                if (facingVariant == RoofVariant.STEEP && facingShape == RoofShape.STRAIGHT && facingHalf == BASE
                        && currVariant == RoofVariant.NORMAL && currShape == RoofShape.STRAIGHT) {
                    return pState.setValue(VARIANT, RoofVariant.STEEP).setValue(HALF, TIP);
                }
            }
        }

        return pState;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(VARIANT, SHAPE, HALF, FACING, WATERLOGGED);
    }

    private TriFunction<Level, BlockPos, Direction, Optional<BlockState>> tryConnectTo(
            Rotation target,
            QuadPredicate<Rotation, RoofShape, RoofHalf, RoofVariant> rotHalfVariantPredicate,
            BinaryOperator<BlockState> thenSet) {

        return (level, placePos, placeDirection) -> {
            var fluidType = level.getFluidState(placePos).getType();

            var targetPosDirection = target.rotate(placeDirection);
            var targetPos = placePos.relative(targetPosDirection);
            var targetState = level.getBlockState(targetPos);

            if (isRoof(targetState) && rotHalfVariantPredicate.test(
                    Rotation.fromDirections(placeDirection, targetState.getValue(FACING)),
                    targetState.getValue(SHAPE),
                    targetState.getValue(HALF),
                    targetState.getValue(VARIANT))
            ) {
                var initialState = this.defaultBlockState()
                        .setValue(FACING, placeDirection)
                        .setValue(WATERLOGGED, fluidType == Fluids.WATER);
                return Optional.of(thenSet.apply(initialState, targetState));
            } else {
                return Optional.empty();
            }
        };
    }

    private static boolean isOpenSide(RoofShape shape, Rotation actual, Rotation expected) {
        return switch (shape) {
            case STRAIGHT -> actual == expected;
            case INNER -> actual == expected || actual.getClockWise() == expected;
            case OUTER -> false;
        };
    }

    private static boolean isHalfOpenClockWiseSide(RoofShape shape, Rotation actual, Rotation expected) {
        return switch (shape) {
            case STRAIGHT, INNER -> actual.getCounterClockWise() == expected;
            case OUTER -> actual == expected;
        };
    }

    private static boolean isHalfOpenCounterClockWiseSide(RoofShape shape, Rotation actual, Rotation expected) {
        return switch (shape) {
            case STRAIGHT, OUTER -> actual.getClockWise() == expected;
            case INNER -> actual.get2DOpposite() == expected;
        };
    }

    private static boolean isClosedSide(RoofShape shape, Rotation actual, Rotation expected) {
        return switch (shape) {
            case STRAIGHT -> actual.get2DOpposite() == expected;
            case INNER -> false;
            case OUTER -> actual.getCounterClockWise() == expected || actual.get2DOpposite() == expected;
        };
    }
    
    @Override
    public boolean isGlass() {
        return false;
    }
    
    @Override
    public VoxelShape getShapeStatic(BlockState pState) {
        var leftRight = getConnectionLeftRight(pState.getValue(FACING), pState.getValue(SHAPE));
        var leftRightIndex = leftRight.getLeft().get2DDataValue() * 4 + leftRight.getRight().get2DDataValue();
        return switch (pState.getValue(HALF)) {
            case TIP -> ROOF_SHAPES.get(pState.getValue(VARIANT).ordinal() * 16 + leftRightIndex);
            case BASE -> ROOF_BASE_SHAPES.get(pState.getValue(VARIANT).ordinal() * 16 + leftRightIndex);
        };
    }
    
    @FunctionalInterface
    private interface QuadPredicate<A, B, C, D> {
        boolean test(A a, B b, C c, D d);
    }

    private enum Rotation {
        LEFT(Direction::getCounterClockWise),
        FRONT(UnaryOperator.identity()),
        RIGHT(Direction::getClockWise),
        BACK(Direction::getOpposite),
        UP(d -> Direction.UP),
        DOWN(direction -> Direction.DOWN);

        private final UnaryOperator<Direction> rotator;

        Rotation(UnaryOperator<Direction> rotator) {
            this.rotator = rotator;
        }

        public Direction rotate(Direction direction) {
            return this.rotator.apply(direction);
        }

        public Rotation getClockWise() {
            return switch (this) {
                case LEFT -> FRONT; case FRONT -> RIGHT; case RIGHT -> BACK; case BACK -> LEFT;
                case UP, DOWN -> this;
            };
        }

        public Rotation getCounterClockWise() {
            return switch (this) {
                case LEFT -> BACK; case FRONT -> LEFT; case RIGHT -> FRONT; case BACK -> RIGHT;
                case UP, DOWN -> this;
            };
        }

        public Rotation get2DOpposite() {
            return switch (this) {
                case LEFT -> RIGHT; case FRONT -> BACK; case RIGHT -> LEFT; case BACK -> FRONT;
                case UP, DOWN -> this;
            };
        }

        public static Rotation fromDirections(Direction start, Direction end) {
            for (var r : Rotation.values()) if (end == r.rotate(start)) return r;
            throw new IllegalStateException("wtf why none of the rotations matches");
        }
    }

    private static Pair<Direction, Direction> getConnectionLeftRight(Direction facing, RoofShape shape) {
        return switch (shape) {
            case STRAIGHT -> Pair.of(facing.getCounterClockWise(), facing.getClockWise());
            case INNER -> Pair.of(facing.getCounterClockWise(), facing.getOpposite());
            case OUTER -> Pair.of(facing, facing.getClockWise());
        };
    }

    private static boolean isRoof(BlockState state) {
        return state.getBlock() instanceof IsotropicRoofBlock;
    }

    @MethodsReturnNonnullByDefault
    @ParametersAreNonnullByDefault
    public enum RoofHalf implements StringRepresentable {
        BASE, TIP;

        @Override
        public String getSerializedName() {
            return this.name().toLowerCase(Locale.ROOT);
        }

        @Override
        public String toString() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }

    @MethodsReturnNonnullByDefault
    @ParametersAreNonnullByDefault
    public enum RoofShape implements StringRepresentable {
        STRAIGHT, INNER, OUTER;

        @Override
        public String getSerializedName() {
            return this.name().toLowerCase(Locale.ROOT);
        }

        @Override
        public String toString() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }

    @MethodsReturnNonnullByDefault
    @ParametersAreNonnullByDefault
    public enum RoofVariant implements StringRepresentable {
        NORMAL, SLOW, STEEP;

        @Override
        public String getSerializedName() {
            return this.name().toLowerCase(Locale.ROOT);
        }

        @Override
        public String toString() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }
}
