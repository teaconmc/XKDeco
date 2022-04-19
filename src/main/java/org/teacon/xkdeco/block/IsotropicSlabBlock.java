package org.teacon.xkdeco.block;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public final class IsotropicSlabBlock extends SlabBlock implements XKDecoBlock {
    private static final VoxelShape PATH_TOP_AABB = Block.box(0, 8, 0, 16, 15, 16);
    private static final VoxelShape PATH_BOTTOM_AABB = Block.box(0, 0, 0, 16, 7, 16);
    private static final VoxelShape PATH_DOUBLE_AABB = Block.box(0, 0, 0, 16, 15, 16);

    private final boolean isGlass;
    private final boolean isPath;

    public IsotropicSlabBlock(Properties properties, boolean isPath, boolean isGlass) {
        super(properties);
        this.isPath = isPath;
        this.isGlass = isGlass;
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter world, BlockPos pos, PathComputationType type) {
        return !this.isPath && super.isPathfindable(state, world, pos, type);
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return this.isPath || super.useShapeForLightOcclusion(state);
    }

    @Override
    @SuppressWarnings("deprecation")
    public float getShadeBrightness(BlockState state, BlockGetter world, BlockPos pos) {
        return this.isGlass ? 1.0F : super.getShadeBrightness(state, world, pos);
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter world, BlockPos pos) {
        return this.isGlass || super.propagatesSkylightDown(state, world, pos);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(TYPE)) {
            case DOUBLE -> this.isPath ? PATH_DOUBLE_AABB : Shapes.block();
            case BOTTOM -> this.isPath ? PATH_BOTTOM_AABB : BOTTOM_AABB;
            case TOP -> this.isPath ? PATH_TOP_AABB : TOP_AABB;
        };
    }
}
