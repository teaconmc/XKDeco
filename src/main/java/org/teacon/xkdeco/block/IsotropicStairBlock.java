package org.teacon.xkdeco.block;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.stream.IntStream;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public final class IsotropicStairBlock extends StairBlock implements XKDecoBlock {
    private static final VoxelShape PATH_TOP_AABB = Block.box(0, 8, 0, 16, 15, 16);
    private static final VoxelShape PATH_BOTTOM_AABB = Block.box(0, 0, 0, 16, 7, 16);

    private static final VoxelShape PATH_OCTET_NNN = Block.box(0, 0, 0, 8, 7, 8);
    private static final VoxelShape PATH_OCTET_NNP = Block.box(0, 0, 8, 8, 7, 16);
    private static final VoxelShape PATH_OCTET_NPN = Block.box(0, 7, 0, 8, 15, 8);
    private static final VoxelShape PATH_OCTET_NPP = Block.box(0, 7, 8, 8, 15, 16);

    private static final VoxelShape PATH_OCTET_PNN = Block.box(8, 0, 0, 16, 7, 8);
    private static final VoxelShape PATH_OCTET_PNP = Block.box(8, 0, 8, 16, 7, 16);
    private static final VoxelShape PATH_OCTET_PPN = Block.box(8, 7, 0, 16, 15, 8);
    private static final VoxelShape PATH_OCTET_PPP = Block.box(8, 7, 8, 16, 15, 16);

    private static final VoxelShape[] PATH_TOP_SHAPES = makeShapes(PATH_TOP_AABB, PATH_OCTET_NNN, PATH_OCTET_PNN, PATH_OCTET_NNP, PATH_OCTET_PNP);
    private static final VoxelShape[] PATH_BOTTOM_SHAPES = makeShapes(PATH_BOTTOM_AABB, PATH_OCTET_NPN, PATH_OCTET_PPN, PATH_OCTET_NPP, PATH_OCTET_PPP);

    private static final int[] SHAPE_BY_STATE = {12, 5, 3, 10, 14, 13, 7, 11, 13, 7, 11, 14, 8, 4, 1, 2, 4, 1, 2, 8};

    private final boolean isGlass;
    private final boolean isPath;

    private static VoxelShape[] makeShapes(VoxelShape slab, VoxelShape nw, VoxelShape ne, VoxelShape sw, VoxelShape se) {
        return IntStream.range(0, 16).mapToObj(i -> makeStairShape(i, slab, nw, ne, sw, se)).toArray(VoxelShape[]::new);
    }

    private static VoxelShape makeStairShape(int flag, VoxelShape slab, VoxelShape nw, VoxelShape ne, VoxelShape sw, VoxelShape se) {
        var shape = slab;
        if ((flag & 1) != 0) {
            shape = Shapes.or(shape, nw);
        }
        if ((flag & 2) != 0) {
            shape = Shapes.or(shape, ne);
        }
        if ((flag & 4) != 0) {
            shape = Shapes.or(shape, sw);
        }
        if ((flag & 8) != 0) {
            shape = Shapes.or(shape, se);
        }
        return shape;
    }

    public IsotropicStairBlock(Properties properties, boolean isPath, boolean isGlass) {
        super(Blocks.AIR::defaultBlockState, properties);
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
        var index = SHAPE_BY_STATE[(state.getValue(SHAPE).ordinal() << 2) + state.getValue(FACING).get2DDataValue()];
        return switch (state.getValue(HALF)) {
            case TOP -> (this.isPath ? PATH_TOP_SHAPES : TOP_SHAPES)[index];
            case BOTTOM -> (this.isPath ? PATH_BOTTOM_SHAPES : BOTTOM_SHAPES)[index];
        };
    }
}
