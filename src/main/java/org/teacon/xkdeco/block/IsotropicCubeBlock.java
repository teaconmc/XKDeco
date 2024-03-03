package org.teacon.xkdeco.block;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public final class IsotropicCubeBlock extends Block implements XKDecoBlock.Isotropic {
	private final boolean isGlass;

	public IsotropicCubeBlock(Properties properties, boolean isGlass) {
		super(properties);
		this.isGlass = isGlass;
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean skipRendering(BlockState pState, BlockState pAdjacentBlockState, Direction pDirection) {
		return cubeSkipRendering(pState, pAdjacentBlockState, pDirection) || super.skipRendering(pState, pAdjacentBlockState, pDirection);
	}

	public static boolean cubeSkipRendering(BlockState pState, BlockState pAdjacentBlockState, Direction pDirection) {
		var block1 = pState.getBlock();
		var block2 = pAdjacentBlockState.getBlock();
		if (block2 instanceof Isotropic ib2 && ib2.isGlass() && block1 instanceof Isotropic ib1 && ib1.isGlass()) {
			var shape1 = ib2.getShapeStatic(pAdjacentBlockState);
			var shape2 = ib1.getShapeStatic(pState);
			return (
					Block.isFaceFull(shape1, pDirection) && Block.isFaceFull(shape2, pDirection.getOpposite()) &&
							!(pAdjacentBlockState.getBlock() instanceof StairBlock))
					|| (pAdjacentBlockState.getBlock() instanceof StairBlock) && (
					(pAdjacentBlockState.getValue(StairBlock.HALF) == Half.BOTTOM && pDirection == Direction.UP)
							|| (pAdjacentBlockState.getValue(StairBlock.HALF) == Half.TOP && pDirection == Direction.DOWN));
		}

		return false;
	}

	public VoxelShape getShapeStatic(BlockState pState) {
		return Shapes.block();
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
	public boolean isGlass() {
		return isGlass;
	}
}
