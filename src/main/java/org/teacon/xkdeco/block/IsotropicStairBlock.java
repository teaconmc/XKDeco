package org.teacon.xkdeco.block;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.phys.shapes.VoxelShape;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public final class IsotropicStairBlock extends StairBlock implements XKDecoBlockIsotropic {
	public static final int[] SHAPE_BY_STATE = new int[]{12, 5, 3, 10, 14, 13, 7, 11, 13, 7, 11, 14, 8, 4, 1, 2, 4, 1, 2, 8};

	private final boolean isGlass;

	public IsotropicStairBlock(Properties properties, boolean isGlass) {
		super(Blocks.AIR::defaultBlockState, properties);
		this.isGlass = isGlass;
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean skipRendering(BlockState pState, BlockState pAdjacentBlockState, Direction pDirection) {
		boolean faceBlocked = false;
		var block = pAdjacentBlockState.getBlock();
		if (block instanceof XKDecoBlockIsotropic ib && ib.isGlass()) {
			var shape1 = ib.getShapeStatic(pAdjacentBlockState);
			var shape2 = this.getShapeStatic(pState);
			if ((Block.isFaceFull(shape1, pDirection) && Block.isFaceFull(shape2, pDirection.getOpposite()))) {
				faceBlocked = true;
			}
		}

		return (this.isGlass && faceBlocked) || super.skipRendering(pState, pAdjacentBlockState, pDirection);
	}

	public VoxelShape getShapeStatic(BlockState pState) {
		return (pState.getValue(HALF) == Half.TOP ? TOP_SHAPES : BOTTOM_SHAPES)[SHAPE_BY_STATE[getShapeIndexS(pState)]];
	}

	private static int getShapeIndexS(BlockState pState) {
		return pState.getValue(SHAPE).ordinal() * 4 + pState.getValue(FACING).get2DDataValue();
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
