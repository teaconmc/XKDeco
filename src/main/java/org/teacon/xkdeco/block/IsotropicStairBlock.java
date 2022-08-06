package org.teacon.xkdeco.block;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public final class IsotropicStairBlock extends StairBlock implements XKDecoBlock.Isotropic {
    private final boolean isGlass;

    public IsotropicStairBlock(Properties properties, boolean isGlass) {
        super(Blocks.AIR::defaultBlockState, properties);
        this.isGlass = isGlass;
    }

    @Override
    public boolean skipRendering(@NotNull BlockState pState, @NotNull BlockState pAdjacentBlockState, @NotNull Direction pDirection) {
        return (this.isGlass && pAdjacentBlockState.is(this)) || super.skipRendering(pState, pAdjacentBlockState, pDirection);
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
}
