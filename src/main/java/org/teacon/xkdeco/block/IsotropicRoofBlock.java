package org.teacon.xkdeco.block;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public final class IsotropicRoofBlock extends Block implements SimpleWaterloggedBlock, XKDecoBlock.Isotropic {
    public static final EnumProperty<RoofVariant> VARIANT = EnumProperty.create("variant", RoofVariant.class);
    public static final EnumProperty<RoofShape> SHAPE = EnumProperty.create("shape", RoofShape.class);
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<Half> HALF = BlockStateProperties.HALF;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public static final VoxelShape ROOF_E = Shapes.or(Block.box(0, 8, 0, 8, 16, 16), Block.box(0, 0, 0, 16, 8, 16));
    public static final VoxelShape ROOF_INNER_EN = Shapes.or(Block.box(0, 8, 0, 8, 16, 8), Block.box(0, 8, 8, 16, 16, 16), Block.box(0, 0, 0, 16, 8, 16));
    public static final VoxelShape ROOF_INNER_NW = Shapes.or(Block.box(0, 8, 8, 8, 16, 16), Block.box(8, 8, 0, 16, 16, 16), Block.box(0, 0, 0, 16, 8, 16));
    public static final VoxelShape ROOF_INNER_SE = Shapes.or(Block.box(8, 8, 0, 16, 16, 8), Block.box(0, 8, 0, 8, 16, 16), Block.box(0, 0, 0, 16, 8, 16));
    public static final VoxelShape ROOF_INNER_TOP_EN = Shapes.or(Block.box(0, 16, 0, 8, 24, 8), Block.box(0, 16, 8, 16, 24, 16), Block.box(0, 8, 0, 16, 16, 16));
    public static final VoxelShape ROOF_INNER_TOP_NW = Shapes.or(Block.box(0, 16, 8, 8, 24, 16), Block.box(8, 16, 0, 16, 24, 16), Block.box(0, 8, 0, 16, 16, 16));
    public static final VoxelShape ROOF_INNER_TOP_SE = Shapes.or(Block.box(8, 16, 0, 16, 24, 8), Block.box(0, 16, 0, 8, 24, 16), Block.box(0, 8, 0, 16, 16, 16));
    public static final VoxelShape ROOF_INNER_TOP_WS = Shapes.or(Block.box(8, 16, 8, 16, 24, 16), Block.box(0, 16, 0, 16, 24, 8), Block.box(0, 8, 0, 16, 16, 16));
    public static final VoxelShape ROOF_INNER_WS = Shapes.or(Block.box(8, 8, 8, 16, 16, 16), Block.box(0, 8, 0, 16, 16, 8), Block.box(0, 0, 0, 16, 8, 16));
    public static final VoxelShape ROOF_N = Shapes.or(Block.box(0, 8, 8, 16, 16, 16), Block.box(0, 0, 0, 16, 8, 16));
    public static final VoxelShape ROOF_OUTER_EN = Shapes.or(Block.box(0, 8, 8, 8, 16, 16), Block.box(0, 0, 0, 16, 8, 16));
    public static final VoxelShape ROOF_OUTER_NW = Shapes.or(Block.box(8, 8, 8, 16, 16, 16), Block.box(0, 0, 0, 16, 8, 16));
    public static final VoxelShape ROOF_OUTER_SE = Shapes.or(Block.box(0, 8, 0, 8, 16, 8), Block.box(0, 0, 0, 16, 8, 16));
    public static final VoxelShape ROOF_OUTER_TOP_EN = Shapes.or(Block.box(0, 16, 8, 8, 24, 16), Block.box(0, 8, 0, 16, 16, 16));
    public static final VoxelShape ROOF_OUTER_TOP_NW = Shapes.or(Block.box(8, 16, 8, 16, 24, 16), Block.box(0, 8, 0, 16, 16, 16));
    public static final VoxelShape ROOF_OUTER_TOP_SE = Shapes.or(Block.box(0, 16, 0, 8, 24, 8), Block.box(0, 8, 0, 16, 16, 16));
    public static final VoxelShape ROOF_OUTER_TOP_WS = Shapes.or(Block.box(8, 16, 0, 16, 24, 8), Block.box(0, 8, 0, 16, 16, 16));
    public static final VoxelShape ROOF_OUTER_WS = Shapes.or(Block.box(8, 8, 0, 16, 16, 8), Block.box(0, 0, 0, 16, 8, 16));
    public static final VoxelShape ROOF_S = Shapes.or(Block.box(0, 8, 0, 16, 16, 8), Block.box(0, 0, 0, 16, 8, 16));
    public static final VoxelShape ROOF_TOP_E = Shapes.or(Block.box(0, 16, 0, 8, 24, 16), Block.box(0, 8, 0, 16, 16, 16));
    public static final VoxelShape ROOF_TOP_N = Shapes.or(Block.box(0, 16, 8, 16, 24, 16), Block.box(0, 8, 0, 16, 16, 16));
    public static final VoxelShape ROOF_TOP_S = Shapes.or(Block.box(0, 16, 0, 16, 24, 8), Block.box(0, 8, 0, 16, 16, 16));
    public static final VoxelShape ROOF_TOP_W = Shapes.or(Block.box(8, 16, 0, 16, 24, 16), Block.box(0, 8, 0, 16, 16, 16));
    public static final VoxelShape ROOF_W = Shapes.or(Block.box(8, 8, 0, 16, 16, 16), Block.box(0, 0, 0, 16, 8, 16));
    public static final VoxelShape SLOW_ROOF_E = Shapes.or(Block.box(0, 4, 0, 8, 8, 16), Block.box(0, 0, 0, 16, 4, 16));
    public static final VoxelShape SLOW_ROOF_INNER_EN = Shapes.or(Block.box(0, 4, 8, 16, 8, 16), Block.box(0, 4, 0, 8, 8, 8), Block.box(0, 0, 0, 16, 4, 16));
    public static final VoxelShape SLOW_ROOF_INNER_NW = Shapes.or(Block.box(8, 4, 0, 16, 8, 16), Block.box(0, 4, 8, 8, 8, 16), Block.box(0, 0, 0, 16, 4, 16));
    public static final VoxelShape SLOW_ROOF_INNER_SE = Shapes.or(Block.box(0, 4, 0, 8, 8, 16), Block.box(8, 4, 0, 16, 8, 8), Block.box(0, 0, 0, 16, 4, 16));
    public static final VoxelShape SLOW_ROOF_INNER_TOP_EN = Shapes.or(Block.box(0, 12, 8, 16, 16, 16), Block.box(0, 12, 0, 8, 16, 8), Block.box(0, 0, 0, 16, 12, 16));
    public static final VoxelShape SLOW_ROOF_INNER_TOP_NW = Shapes.or(Block.box(8, 12, 0, 16, 16, 16), Block.box(0, 12, 8, 8, 16, 16), Block.box(0, 0, 0, 16, 12, 16));
    public static final VoxelShape SLOW_ROOF_INNER_TOP_SE = Shapes.or(Block.box(0, 12, 0, 8, 16, 16), Block.box(8, 12, 0, 16, 16, 8), Block.box(0, 0, 0, 16, 12, 16));
    public static final VoxelShape SLOW_ROOF_INNER_TOP_WS = Shapes.or(Block.box(0, 12, 0, 16, 16, 8), Block.box(8, 12, 8, 16, 16, 16), Block.box(0, 0, 0, 16, 12, 16));
    public static final VoxelShape SLOW_ROOF_INNER_WS = Shapes.or(Block.box(0, 4, 0, 16, 8, 8), Block.box(8, 4, 8, 16, 8, 16), Block.box(0, 0, 0, 16, 4, 16));
    public static final VoxelShape SLOW_ROOF_N = Shapes.or(Block.box(0, 4, 8, 16, 8, 16), Block.box(0, 0, 0, 16, 4, 16));
    public static final VoxelShape SLOW_ROOF_OUTER_EN = Shapes.or(Block.box(0, 4, 8, 8, 8, 16), Block.box(0, 0, 0, 16, 4, 16));
    public static final VoxelShape SLOW_ROOF_OUTER_NW = Shapes.or(Block.box(8, 4, 8, 16, 8, 16), Block.box(0, 0, 0, 16, 4, 16));
    public static final VoxelShape SLOW_ROOF_OUTER_SE = Shapes.or(Block.box(0, 4, 0, 8, 8, 8), Block.box(0, 0, 0, 16, 4, 16));
    public static final VoxelShape SLOW_ROOF_OUTER_TOP_EN = Shapes.or(Block.box(0, 12, 8, 8, 16, 16), Block.box(0, 0, 0, 16, 12, 16));
    public static final VoxelShape SLOW_ROOF_OUTER_TOP_NW = Shapes.or(Block.box(8, 12, 8, 16, 16, 16), Block.box(0, 0, 0, 16, 12, 16));
    public static final VoxelShape SLOW_ROOF_OUTER_TOP_SE = Shapes.or(Block.box(0, 12, 0, 8, 16, 8), Block.box(0, 0, 0, 16, 12, 16));
    public static final VoxelShape SLOW_ROOF_OUTER_TOP_WS = Shapes.or(Block.box(8, 12, 0, 16, 16, 8), Block.box(0, 0, 0, 16, 12, 16));
    public static final VoxelShape SLOW_ROOF_OUTER_WS = Shapes.or(Block.box(8, 4, 0, 16, 8, 8), Block.box(0, 0, 0, 16, 4, 16));
    public static final VoxelShape SLOW_ROOF_S = Shapes.or(Block.box(0, 4, 0, 16, 8, 8), Block.box(0, 0, 0, 16, 4, 16));
    public static final VoxelShape SLOW_ROOF_TOP_E = Shapes.or(Block.box(0, 12, 0, 8, 16, 16), Block.box(0, 0, 0, 16, 12, 16));
    public static final VoxelShape SLOW_ROOF_TOP_N = Shapes.or(Block.box(0, 12, 8, 16, 16, 16), Block.box(0, 0, 0, 16, 12, 16));
    public static final VoxelShape SLOW_ROOF_TOP_S = Shapes.or(Block.box(0, 12, 0, 16, 16, 8), Block.box(0, 0, 0, 16, 12, 16));
    public static final VoxelShape SLOW_ROOF_TOP_W = Shapes.or(Block.box(8, 12, 0, 16, 16, 16), Block.box(0, 0, 0, 16, 12, 16));
    public static final VoxelShape SLOW_ROOF_W = Shapes.or(Block.box(8, 4, 0, 16, 8, 16), Block.box(0, 0, 0, 16, 4, 16));
    public static final VoxelShape STEEP_ROOF_E = Shapes.or(Block.box(0, 8, 0, 4, 16, 16), Block.box(0, 0, 0, 8, 8, 16));
    public static final VoxelShape STEEP_ROOF_INNER_EN = Shapes.or(Block.box(0, 8, 12, 16, 16, 16), Block.box(0, 8, 0, 4, 16, 12));
    public static final VoxelShape STEEP_ROOF_INNER_NW = Shapes.or(Block.box(12, 8, 0, 16, 16, 16), Block.box(0, 8, 12, 12, 16, 16));
    public static final VoxelShape STEEP_ROOF_INNER_SE = Shapes.or(Block.box(0, 8, 0, 4, 16, 16), Block.box(4, 8, 0, 16, 16, 4));
    public static final VoxelShape STEEP_ROOF_INNER_TOP_EN = Shapes.or(Block.box(0, 8, 4, 16, 16, 16), Block.box(0, 8, 0, 12, 16, 4), Block.box(0, 0, 0, 16, 8, 16));
    public static final VoxelShape STEEP_ROOF_INNER_TOP_NW = Shapes.or(Block.box(4, 8, 0, 16, 16, 16), Block.box(0, 8, 4, 4, 16, 16), Block.box(0, 0, 0, 16, 8, 16));
    public static final VoxelShape STEEP_ROOF_INNER_TOP_SE = Shapes.or(Block.box(0, 8, 0, 12, 16, 16), Block.box(12, 8, 0, 16, 16, 12), Block.box(0, 0, 0, 16, 8, 16));
    public static final VoxelShape STEEP_ROOF_INNER_TOP_WS = Shapes.or(Block.box(0, 8, 0, 16, 16, 12), Block.box(4, 8, 12, 16, 16, 16), Block.box(0, 0, 0, 16, 8, 16));
    public static final VoxelShape STEEP_ROOF_INNER_WS = Shapes.or(Block.box(0, 8, 0, 16, 16, 4), Block.box(12, 8, 4, 16, 16, 16));
    public static final VoxelShape STEEP_ROOF_N = Shapes.or(Block.box(0, 8, 12, 16, 16, 16), Block.box(0, 0, 8, 16, 8, 16));
    public static final VoxelShape STEEP_ROOF_OUTER_EN = Shapes.or(Block.box(0, 0, 8, 8, 8, 16), Block.box(0, 8, 12, 4, 16, 16));
    public static final VoxelShape STEEP_ROOF_OUTER_NW = Shapes.or(Block.box(8, 0, 8, 16, 8, 16), Block.box(12, 8, 12, 16, 16, 16));
    public static final VoxelShape STEEP_ROOF_OUTER_SE = Shapes.or(Block.box(0, 0, 0, 8, 8, 8), Block.box(0, 8, 0, 4, 16, 4));
    public static final VoxelShape STEEP_ROOF_OUTER_TOP_EN = Shapes.or(Block.box(0, 8, 4, 12, 16, 16), Block.box(0, 0, 0, 16, 8, 16));
    public static final VoxelShape STEEP_ROOF_OUTER_TOP_NW = Shapes.or(Block.box(4, 8, 4, 16, 16, 16), Block.box(0, 0, 0, 16, 8, 16));
    public static final VoxelShape STEEP_ROOF_OUTER_TOP_SE = Shapes.or(Block.box(0, 8, 0, 12, 16, 12), Block.box(0, 0, 0, 16, 8, 16));
    public static final VoxelShape STEEP_ROOF_OUTER_TOP_WS = Shapes.or(Block.box(4, 8, 0, 16, 16, 12), Block.box(0, 0, 0, 16, 8, 16));
    public static final VoxelShape STEEP_ROOF_OUTER_WS = Shapes.or(Block.box(8, 0, 0, 16, 8, 8), Block.box(12, 8, 0, 16, 16, 4));
    public static final VoxelShape STEEP_ROOF_S = Shapes.or(Block.box(0, 8, 0, 16, 16, 4), Block.box(0, 0, 0, 16, 8, 8));
    public static final VoxelShape STEEP_ROOF_TOP_E = Shapes.or(Block.box(0, 8, 0, 12, 16, 16), Block.box(0, 0, 0, 16, 8, 16));
    public static final VoxelShape STEEP_ROOF_TOP_N = Shapes.or(Block.box(0, 8, 4, 16, 16, 16), Block.box(0, 0, 0, 16, 8, 16));
    public static final VoxelShape STEEP_ROOF_TOP_S = Shapes.or(Block.box(0, 8, 0, 16, 16, 12), Block.box(0, 0, 0, 16, 8, 16));
    public static final VoxelShape STEEP_ROOF_TOP_W = Shapes.or(Block.box(4, 8, 0, 16, 16, 16), Block.box(0, 0, 0, 16, 8, 16));
    public static final VoxelShape STEEP_ROOF_W = Shapes.or(Block.box(12, 8, 0, 16, 16, 16), Block.box(8, 0, 0, 16, 8, 16));

    public static final List<VoxelShape> ROOFS = List.of(
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
    public static final List<VoxelShape> ROOF_TOPS = List.of(
            Shapes.block(), ROOF_OUTER_TOP_EN, ROOF_TOP_E, ROOF_INNER_TOP_SE,
            ROOF_INNER_TOP_WS, Shapes.block(), ROOF_OUTER_TOP_SE, ROOF_TOP_S,
            ROOF_TOP_W, ROOF_INNER_TOP_NW, Shapes.block(), ROOF_OUTER_TOP_WS,
            ROOF_OUTER_TOP_NW, ROOF_TOP_N, ROOF_INNER_TOP_EN, Shapes.block(),
            Shapes.block(), SLOW_ROOF_OUTER_TOP_EN, SLOW_ROOF_TOP_E, SLOW_ROOF_INNER_TOP_SE,
            SLOW_ROOF_INNER_TOP_WS, Shapes.block(), SLOW_ROOF_OUTER_TOP_SE, SLOW_ROOF_TOP_S,
            SLOW_ROOF_TOP_W, SLOW_ROOF_INNER_TOP_NW, Shapes.block(), SLOW_ROOF_OUTER_TOP_WS,
            SLOW_ROOF_OUTER_TOP_NW, SLOW_ROOF_TOP_N, SLOW_ROOF_INNER_TOP_EN, Shapes.block(),
            Shapes.block(), STEEP_ROOF_OUTER_TOP_EN, STEEP_ROOF_TOP_E, STEEP_ROOF_INNER_TOP_SE,
            STEEP_ROOF_INNER_TOP_WS, Shapes.block(), STEEP_ROOF_OUTER_TOP_SE, STEEP_ROOF_TOP_S,
            STEEP_ROOF_TOP_W, STEEP_ROOF_INNER_TOP_NW, Shapes.block(), STEEP_ROOF_OUTER_TOP_WS,
            STEEP_ROOF_OUTER_TOP_NW, STEEP_ROOF_TOP_N, STEEP_ROOF_INNER_TOP_EN, Shapes.block());

    public IsotropicRoofBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(VARIANT, RoofVariant.NORMAL).setValue(SHAPE, RoofShape.STRAIGHT)
                .setValue(FACING, Direction.NORTH).setValue(HALF, Half.BOTTOM).setValue(WATERLOGGED, false));
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
            case BOTTOM -> ROOFS.get(pState.getValue(VARIANT).ordinal() * 16 + leftRightIndex);
            case TOP -> ROOF_TOPS.get(pState.getValue(VARIANT).ordinal() * 16 + leftRightIndex);
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
        var level = pContext.getLevel();
        var fluidState = level.getFluidState(pContext.getClickedPos()).getType();

        var front = pContext.getHorizontalDirection();
        var left = front.getCounterClockWise();
        var right = front.getClockWise();

        for (var connection : List.of(front, front.getOpposite())) {
            var state = level.getBlockState(pContext.getClickedPos().relative(connection));
            if (isRoof(state)) {
                var stateLeftRight = getConnectionLeftRight(state.getValue(FACING), state.getValue(SHAPE));
                if (stateLeftRight.getRight().getOpposite().equals(connection)) {
                    left = connection;
                    break;
                }
                if (stateLeftRight.getLeft().getOpposite().equals(connection)) {
                    right = connection;
                    break;
                }
            }
        }

        var facingShape = setConnectionLeftRight(left, right);

        return this.updateHalfVariant(this.defaultBlockState()
                .setValue(SHAPE, facingShape.getRight()).setValue(FACING, facingShape.getLeft())
                .setValue(WATERLOGGED, fluidState == Fluids.WATER), level, pContext.getClickedPos(), false);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState,
                                  LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        if (pState.getValue(WATERLOGGED)) {
            pLevel.scheduleTick(pCurrentPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
        }

        if (isRoof(pFacingState)) {
            var facingLeftRight = getConnectionLeftRight(pFacingState.getValue(FACING), pFacingState.getValue(SHAPE));
            // calculate left and right choices
            var leftChoices = new ArrayList<Direction>(3);
            var rightChoices = new ArrayList<Direction>(3);
            for (var connection : Direction.Plane.HORIZONTAL) {
                var state = pLevel.getBlockState(pCurrentPos.relative(connection));
                if (!connection.equals(pFacing) && isRoof(state)) {
                    var leftRight = getConnectionLeftRight(state.getValue(FACING), state.getValue(SHAPE));
                    if (leftRight.getRight().getOpposite().equals(connection)) {
                        leftChoices.add(connection);
                    }
                    if (leftRight.getLeft().getOpposite().equals(connection)) {
                        rightChoices.add(connection);
                    }
                }
            }
            // connection from right
            if (facingLeftRight.getLeft().getOpposite().equals(pFacing)) {
                if (leftChoices.size() == 0 && rightChoices.size() == 0) {
                    leftChoices.add(pFacing.getOpposite());
                }
                if (leftChoices.size() == 1) {
                    var facingShape = setConnectionLeftRight(leftChoices.iterator().next(), pFacing);
                    pState = pState.setValue(FACING, facingShape.getLeft()).setValue(SHAPE, facingShape.getRight());
                }
            }
            // connection from left
            if (facingLeftRight.getRight().getOpposite().equals(pFacing)) {
                if (leftChoices.size() == 0 && rightChoices.size() == 0) {
                    rightChoices.add(pFacing.getOpposite());
                }
                if (rightChoices.size() == 1) {
                    var facingShape = setConnectionLeftRight(pFacing, rightChoices.iterator().next());
                    pState = pState.setValue(FACING, facingShape.getLeft()).setValue(SHAPE, facingShape.getRight());
                }
            }
        }

        return this.updateHalfVariant(pState, pLevel, pCurrentPos, true);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(VARIANT, SHAPE, FACING, HALF, WATERLOGGED);
    }

    private BlockState updateHalfVariant(BlockState state, LevelAccessor level, BlockPos pos, boolean isPassive) {
        var currentFacing = state.getValue(FACING);
        var currentShape = state.getValue(SHAPE);
        switch (currentShape) {
            case STRAIGHT -> {
                var isCurrentNormal = state.getValue(VARIANT).equals(RoofVariant.NORMAL);
                if (isCurrentNormal ? isPassive : state.getValue(HALF).equals(Half.TOP)) {
                    var stateBackward = level.getBlockState(pos.relative(currentFacing.getOpposite()));
                    if (isRoof(stateBackward)) {
                        var bottomHalf = stateBackward.getValue(HALF).equals(Half.BOTTOM);
                        var sameFacing = stateBackward.getValue(FACING).equals(currentFacing);
                        var sameShape = stateBackward.getValue(SHAPE).equals(RoofShape.STRAIGHT);
                        var slowVariant = stateBackward.getValue(VARIANT).equals(RoofVariant.SLOW);
                        if (bottomHalf && sameFacing && sameShape && slowVariant) {
                            return state.setValue(HALF, Half.TOP).setValue(VARIANT, RoofVariant.SLOW);
                        }
                    }
                    var stateAbove = level.getBlockState(pos.above());
                    if (isRoof(stateAbove)) {
                        var bottomHalf = stateAbove.getValue(HALF).equals(Half.BOTTOM);
                        var sameFacing = stateAbove.getValue(FACING).equals(currentFacing);
                        var sameShape = stateAbove.getValue(SHAPE).equals(RoofShape.STRAIGHT);
                        var steepVariant = stateAbove.getValue(VARIANT).equals(RoofVariant.STEEP);
                        if (bottomHalf && sameFacing && sameShape && steepVariant) {
                            return state.setValue(HALF, Half.TOP).setValue(VARIANT, RoofVariant.STEEP);
                        }
                    }
                    return state.setValue(HALF, Half.BOTTOM).setValue(VARIANT, RoofVariant.NORMAL);
                } else {
                    var stateForward = level.getBlockState(pos.relative(currentFacing));
                    if (isRoof(stateForward)) {
                        var forwardShape = stateForward.getValue(SHAPE);
                        var forwardFacing = stateForward.getValue(FACING);
                        var forwardVariant = stateForward.getValue(VARIANT);
                        var forwardLeftRight = getConnectionLeftRight(forwardFacing, forwardShape);
                        var currentLeftRight = getConnectionLeftRight(currentFacing, currentShape);
                        var sameLeft = forwardLeftRight.getLeft().equals(currentLeftRight.getLeft());
                        var sameRight = forwardLeftRight.getRight().equals(currentLeftRight.getRight());
                        if ((sameLeft || sameRight) && !forwardVariant.equals(RoofVariant.STEEP)) {
                            return state.setValue(HALF, Half.BOTTOM).setValue(VARIANT, RoofVariant.SLOW);
                        }
                    }
                    var stateBelow = level.getBlockState(pos.below());
                    if (isRoof(stateBelow)) {
                        var belowVariant = stateBelow.getValue(VARIANT);
                        var sameFacing = stateBelow.getValue(FACING).equals(currentFacing);
                        var sameShape = stateBelow.getValue(SHAPE).equals(RoofShape.STRAIGHT);
                        if (sameFacing && sameShape && !belowVariant.equals(RoofVariant.SLOW)) {
                            return state.setValue(HALF, Half.BOTTOM).setValue(VARIANT, RoofVariant.STEEP);
                        }
                    }
                    return state.setValue(HALF, Half.BOTTOM).setValue(VARIANT, RoofVariant.NORMAL);
                }
            }
            case INNER, OUTER -> {
                var currentLeftRight = getConnectionLeftRight(currentFacing, currentShape);
                var stateLeft = level.getBlockState(pos.relative(currentLeftRight.getLeft()));
                var stateRight = level.getBlockState(pos.relative(currentLeftRight.getRight()));
                if (isConnected(stateLeft, state) && isConnected(state, stateRight)) {
                    var leftHalf = stateLeft.getValue(HALF);
                    var rightHalf = stateRight.getValue(HALF);
                    var leftVariant = stateLeft.getValue(VARIANT);
                    var rightVariant = stateRight.getValue(VARIANT);
                    if (leftHalf.equals(rightHalf) && leftVariant.equals(rightVariant)) {
                        return state.setValue(HALF, leftHalf).setValue(VARIANT, rightVariant);
                    }
                }
                return state.setValue(HALF, Half.BOTTOM).setValue(VARIANT, RoofVariant.NORMAL);
            }
        }
        return state;
    }

    private static Pair<Direction, RoofShape> setConnectionLeftRight(Direction left, Direction right) {
        return switch ((4 + left.get2DDataValue() - right.get2DDataValue()) % 4) {
            case 1 -> Pair.of(left.getClockWise(), RoofShape.INNER);
            case 2 -> Pair.of(left.getClockWise(), RoofShape.STRAIGHT);
            case 3 -> Pair.of(right.getCounterClockWise(), RoofShape.OUTER);
            default -> throw new IllegalStateException("Unexpected directions: " + left + " and " + right);
        };
    }

    private static Pair<Direction, Direction> getConnectionLeftRight(Direction facing, RoofShape shape) {
        return switch (shape) {
            case STRAIGHT -> Pair.of(facing.getCounterClockWise(), facing.getClockWise());
            case INNER -> Pair.of(facing.getCounterClockWise(), facing.getOpposite());
            case OUTER -> Pair.of(facing, facing.getClockWise());
        };
    }

    private boolean isConnected(BlockState left, BlockState right) {
        if (isRoof(left) && isRoof(right)) {
            var rightLeft = getConnectionLeftRight(right.getValue(FACING), right.getValue(SHAPE)).getLeft();
            var leftRight = getConnectionLeftRight(left.getValue(FACING), left.getValue(SHAPE)).getRight();
            return leftRight.getOpposite().equals(rightLeft);
        }
        return false;
    }

    private static boolean isRoof(BlockState state) {
        return state.getBlock() instanceof IsotropicRoofBlock;
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
