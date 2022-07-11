package org.teacon.xkdeco.block;

import com.google.common.collect.Sets;
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
import java.util.Set;

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
                .setValue(HALF, RoofHalf.TIP).setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false));
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
        pBuilder.add(VARIANT, SHAPE, HALF, FACING, WATERLOGGED);
    }

    private BlockState updateHalfVariant(BlockState state, LevelAccessor level, BlockPos pos, boolean isPassive) {
        var isNotStraight = !state.getValue(SHAPE).equals(RoofShape.STRAIGHT);
        if (isNotStraight) {
            if (this.isSideMatched(state, level, pos, RoofHalf.BASE, RoofVariant.SLOW)) {
                return state.setValue(HALF, RoofHalf.BASE).setValue(VARIANT, RoofVariant.SLOW);
            }
            if (this.isSideMatched(state, level, pos, RoofHalf.BASE, RoofVariant.STEEP)) {
                return state.setValue(HALF, RoofHalf.BASE).setValue(VARIANT, RoofVariant.STEEP);
            }
            if (this.isSideMatched(state, level, pos, RoofHalf.TIP, RoofVariant.SLOW)) {
                return state.setValue(HALF, RoofHalf.TIP).setValue(VARIANT, RoofVariant.SLOW);
            }
            if (this.isSideMatched(state, level, pos, RoofHalf.TIP, RoofVariant.STEEP)) {
                return state.setValue(HALF, RoofHalf.TIP).setValue(VARIANT, RoofVariant.STEEP);
            }
        }
        var isCurrentNormal = state.getValue(VARIANT).equals(RoofVariant.NORMAL);
        if (isCurrentNormal ? isPassive : state.getValue(HALF).equals(RoofHalf.BASE)) {
            if (this.isBackwardSlowTip(state, level, pos)) {
                return state.setValue(HALF, RoofHalf.BASE).setValue(VARIANT, RoofVariant.SLOW);
            }
            if (this.isAboveSteepTip(state, level, pos)) {
                return state.setValue(HALF, RoofHalf.BASE).setValue(VARIANT, RoofVariant.STEEP);
            }
        } else {
            if (this.isForwardSlowBase(state, level, pos)) {
                return state.setValue(HALF, RoofHalf.TIP).setValue(VARIANT, RoofVariant.SLOW);
            }
            if (this.isBelowSteepBase(state, level, pos)) {
                return state.setValue(HALF, RoofHalf.TIP).setValue(VARIANT, RoofVariant.STEEP);
            }
        }
        return state.setValue(HALF, RoofHalf.TIP).setValue(VARIANT, RoofVariant.NORMAL);
    }

    private boolean isSideMatched(BlockState state, LevelAccessor level, BlockPos pos, RoofHalf half, RoofVariant var) {
        var currentLeftRight = getConnectionLeftRight(state.getValue(FACING), state.getValue(SHAPE));
        var stateLeft = level.getBlockState(pos.relative(currentLeftRight.getLeft()));
        var stateRight = level.getBlockState(pos.relative(currentLeftRight.getRight()));
        if (stateLeft.getValue(SHAPE).equals(RoofShape.STRAIGHT) && this.isConnected(stateLeft, state)) {
            if (stateRight.getValue(SHAPE).equals(RoofShape.STRAIGHT) && this.isConnected(state, stateRight)) {
                var sameVariant = stateLeft.getValue(VARIANT).equals(var) && stateRight.getValue(VARIANT).equals(var);
                var sameHalf = stateLeft.getValue(HALF).equals(half) && stateRight.getValue(HALF).equals(half);
                return sameVariant && sameHalf;
            }
        }
        return false;
    }

    private boolean isAboveSteepTip(BlockState state, LevelAccessor level, BlockPos pos) {
        var stateAbove = level.getBlockState(pos.above());
        if (isRoof(stateAbove)) {
            var aboveVariant = stateAbove.getValue(VARIANT);
            var tipHalf = stateAbove.getValue(HALF).equals(RoofHalf.TIP);
            var sameShape = stateAbove.getValue(SHAPE).equals(RoofShape.STRAIGHT);
            var sameFacing = stateAbove.getValue(FACING).equals(state.getValue(FACING));
            return tipHalf && sameShape && sameFacing && aboveVariant.equals(RoofVariant.STEEP);
        }
        return false;
    }

    private boolean isBelowSteepBase(BlockState state, LevelAccessor level, BlockPos pos) {
        var stateBelow = level.getBlockState(pos.below());
        if (isRoof(stateBelow)) {
            var belowVariant = stateBelow.getValue(VARIANT);
            var sameShape = stateBelow.getValue(SHAPE).equals(RoofShape.STRAIGHT);
            var sameFacing = stateBelow.getValue(FACING).equals(state.getValue(FACING));
            return sameShape && sameFacing && !belowVariant.equals(RoofVariant.SLOW);
        }
        return false;
    }

    private boolean isBackwardSlowTip(BlockState state, LevelAccessor level, BlockPos pos) {
        var currentLeftRight = getConnectionLeftRight(state.getValue(FACING), state.getValue(SHAPE));
        var leftBackward = currentLeftRight.getLeft().getCounterClockWise();
        var rightBackward = currentLeftRight.getRight().getClockWise();
        for (var facing : Set.of(leftBackward, rightBackward)) {
            var stateBackward = level.getBlockState(pos.relative(facing));
            if (isRoof(stateBackward)) {
                var backwardShape = stateBackward.getValue(SHAPE);
                var backwardFacing = stateBackward.getValue(FACING);
                var backwardVariant = stateBackward.getValue(VARIANT);
                var tipHalf = stateBackward.getValue(HALF).equals(RoofHalf.TIP);
                var backwardLeftRight = getConnectionLeftRight(backwardFacing, backwardShape);
                var sameLeft = backwardLeftRight.getLeft().equals(currentLeftRight.getLeft());
                var sameRight = backwardLeftRight.getRight().equals(currentLeftRight.getRight());
                if (tipHalf && (sameLeft || sameRight) && backwardVariant.equals(RoofVariant.SLOW)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isForwardSlowBase(BlockState state, LevelAccessor level, BlockPos pos) {
        var currentLeftRight = getConnectionLeftRight(state.getValue(FACING), state.getValue(SHAPE));
        var rightForward = currentLeftRight.getRight().getCounterClockWise();
        var leftForward = currentLeftRight.getLeft().getClockWise();
        for (var facing : Sets.newHashSet(rightForward, leftForward)) {
            var stateForward = level.getBlockState(pos.relative(facing));
            if (isRoof(stateForward)) {
                var forwardShape = stateForward.getValue(SHAPE);
                var forwardFacing = stateForward.getValue(FACING);
                var forwardVariant = stateForward.getValue(VARIANT);
                var forwardLeftRight = getConnectionLeftRight(forwardFacing, forwardShape);
                var sameLeft = forwardLeftRight.getLeft().equals(currentLeftRight.getLeft());
                var sameRight = forwardLeftRight.getRight().equals(currentLeftRight.getRight());
                if ((sameLeft || sameRight) && !forwardVariant.equals(RoofVariant.STEEP)) {
                    return true;
                }
            }
        }
        return false;
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
