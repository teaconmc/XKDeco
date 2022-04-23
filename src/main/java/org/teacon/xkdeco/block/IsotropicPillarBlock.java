package org.teacon.xkdeco.block;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public final class IsotropicPillarBlock extends RotatedPillarBlock implements XKDecoBlock.Isotropic {
    private final boolean isGlass;

    public IsotropicPillarBlock(Properties properties, boolean isGlass) {
        super(properties);
        this.isGlass = isGlass;
    }

    @SuppressWarnings("deprecation")
    public float getShadeBrightness(BlockState state, BlockGetter world, BlockPos pos) {
        return this.isGlass ? 1.0F : super.getShadeBrightness(state, world, pos);
    }

    public boolean propagatesSkylightDown(BlockState state, BlockGetter world, BlockPos pos) {
        return this.isGlass || super.propagatesSkylightDown(state, world, pos);
    }
}
