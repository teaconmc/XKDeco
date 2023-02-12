package org.teacon.xkdeco.block;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
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
import org.teacon.xkdeco.util.RoofUtil;
import org.teacon.xkdeco.util.RoofUtil.RoofEndShape;
import org.teacon.xkdeco.util.RoofUtil.RoofHalf;
import org.teacon.xkdeco.util.RoofUtil.RoofShape;
import org.teacon.xkdeco.util.RoofUtil.RoofVariant;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public final class RoofEndBlock extends Block implements XKDecoBlock.Roof {
    public static final EnumProperty<RoofVariant> VARIANT = EnumProperty.create("variant", RoofVariant.class);
    public static final EnumProperty<RoofEndShape> SHAPE = EnumProperty.create("shape", RoofEndShape.class);
    public static final EnumProperty<RoofHalf> HALF = EnumProperty.create("half", RoofHalf.class);
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public RoofEndBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(VARIANT, RoofVariant.NORMAL).setValue(SHAPE, RoofEndShape.LEFT)
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
        var facing = pState.getValue(FACING);
        var roofHalf = pState.getValue(HALF);
        var roofVariant = pState.getValue(VARIANT);
        return RoofUtil.getShape(RoofShape.STRAIGHT, facing, roofHalf, roofVariant);
    }

    @Override
    @SuppressWarnings("deprecation")
    public FluidState getFluidState(BlockState pState) {
        return pState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState()
                .setValue(FACING, pContext.getHorizontalDirection())
                .setValue(WATERLOGGED, pContext.getLevel().getFluidState(pContext.getClickedPos()).getType() == Fluids.WATER);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {
        return false;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(VARIANT, SHAPE, HALF, FACING, WATERLOGGED);
    }
}
