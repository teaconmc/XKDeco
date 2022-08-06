package org.teacon.xkdeco.block;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public final class IsotropicSlabBlock extends SlabBlock implements XKDecoBlock.Isotropic {
    private final boolean isGlass;

    public IsotropicSlabBlock(Properties properties, boolean isGlass) {
        super(properties);
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
