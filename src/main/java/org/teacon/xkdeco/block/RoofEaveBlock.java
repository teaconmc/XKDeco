package org.teacon.xkdeco.block;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
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
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

import static org.teacon.xkdeco.util.RoofUtil.*;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public final class RoofEaveBlock extends Block implements SimpleWaterloggedBlock, XKDecoBlock.Roof {
    public static final EnumProperty<RoofShape> SHAPE = EnumProperty.create("shape", RoofShape.class);
    public static final EnumProperty<RoofHalf> HALF = EnumProperty.create("half", RoofHalf.class);
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public static final VoxelShape ROOF_EAVE_TIP = Block.box(0, 0, 0, 16, 8, 16);
    public static final VoxelShape ROOF_EAVE_BASE = Block.box(0, 8, 0, 16, 16, 16);

    public RoofEaveBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(SHAPE, RoofShape.STRAIGHT).setValue(HALF, RoofHalf.TIP)
                .setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false));
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean useShapeForLightOcclusion(BlockState pState) {
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return switch (pState.getValue(HALF)) {
            case BASE -> ROOF_EAVE_BASE;
            case TIP -> ROOF_EAVE_TIP;
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
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(SHAPE, HALF, FACING, WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        var initialState = this.defaultBlockState()
                .setValue(FACING, pContext.getHorizontalDirection()).setValue(HALF, getPlacementHalf(pContext))
                .setValue(WATERLOGGED, pContext.getLevel().getFluidState(pContext.getClickedPos()).getType() == Fluids.WATER);

        for (var trial : List.of(
                // try to become a non-straight roof matching the triangular side of the roof at front/back
                tryConnectToEave(Rotation.FRONT,
                        (r, s, h) -> isHalfOpenClockWiseSide(s, r, Rotation.BACK),
                        (curr, tgt) -> curr.setValue(SHAPE, RoofShape.OUTER).setValue(HALF, tgt.getValue(HALF)).setValue(FACING, curr.getValue(FACING).getCounterClockWise())),
                tryConnectToEave(Rotation.FRONT,
                        (r, s, h) -> isHalfOpenCounterClockWiseSide(s, r, Rotation.BACK),
                        (curr, tgt) -> curr.setValue(SHAPE, RoofShape.OUTER).setValue(HALF, tgt.getValue(HALF)).setValue(FACING, curr.getValue(FACING))),
                tryConnectToEave(Rotation.BACK,
                        (r, s, h) -> isHalfOpenCounterClockWiseSide(s, r, Rotation.FRONT),
                        (curr, tgt) -> curr.setValue(SHAPE, RoofShape.INNER).setValue(HALF, tgt.getValue(HALF)).setValue(FACING, curr.getValue(FACING).getCounterClockWise())),
                tryConnectToEave(Rotation.BACK,
                        (r, s, h) -> isHalfOpenClockWiseSide(s, r, Rotation.FRONT),
                        (curr, tgt) -> curr.setValue(SHAPE, RoofShape.INNER).setValue(HALF, tgt.getValue(HALF)).setValue(FACING, curr.getValue(FACING)))
        )) {
            var result = trial.apply(pContext.getLevel(), pContext.getClickedPos(), pContext.getHorizontalDirection(), initialState);
            if (result.isPresent()) return result.get().setValue(HALF, getPlacementHalf(pContext));
        }

        return initialState;
    }
}
