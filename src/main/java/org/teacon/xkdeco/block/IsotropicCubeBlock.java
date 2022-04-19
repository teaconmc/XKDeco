package org.teacon.xkdeco.block;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public final class IsotropicCubeBlock extends Block implements XKDecoBlock {
    private static final VoxelShape PATH_AABB = Block.box(0, 0, 0, 16, 15, 16);

    private final boolean isGlass;
    private final boolean isPath;

    public IsotropicCubeBlock(Properties properties, boolean isPath, boolean isGlass) {
        super(properties);
        this.isPath = isPath;
        this.isGlass = isGlass;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isPathfindable(BlockState state, BlockGetter world, BlockPos pos, PathComputationType type) {
        return !this.isPath && super.isPathfindable(state, world, pos, type);
    }

    @Override
    @SuppressWarnings("deprecation")
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
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return this.isPath ? PATH_AABB : Shapes.block();
    }
}
