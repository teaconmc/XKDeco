package org.teacon.xkdeco.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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

public final class IsotropicRoofEaveBlock extends Block implements SimpleWaterloggedBlock, XKDecoBlock.Isotropic {
    public static final EnumProperty<IsotropicRoofBlock.RoofShape> SHAPE = EnumProperty.create("shape", IsotropicRoofBlock.RoofShape.class);
    public static final EnumProperty<IsotropicRoofBlock.RoofHalf> HALF = EnumProperty.create("half", IsotropicRoofBlock.RoofHalf.class);
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public static final VoxelShape ROOF_EAVE_TIP = Block.box(0, 0, 0, 16, 8, 16);
    public static final VoxelShape ROOF_EAVE_BASE = Block.box(0, 8, 0, 16, 16, 16);

    public IsotropicRoofEaveBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(SHAPE, IsotropicRoofBlock.RoofShape.STRAIGHT).setValue(HALF, IsotropicRoofBlock.RoofHalf.TIP)
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
        return getShapeStatic(pState);
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
    public boolean isGlass() {
        return false;
    }

    @Override
    public VoxelShape getShapeStatic(BlockState state) {
        return switch (state.getValue(HALF)) {
            case BASE -> ROOF_EAVE_BASE;
            case TIP -> ROOF_EAVE_TIP;
        };
    }
}
