package org.teacon.xkdeco.block;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public final class IsotropicSlabBlock extends SlabBlock implements XKDecoBlockIsotropic {
	private final boolean isGlass;

	public IsotropicSlabBlock(Properties properties, boolean isGlass) {
		super(properties);
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
			if ((
					(pAdjacentBlockState.is(this) && pAdjacentBlockState.getValue(TYPE) == pState.getValue(TYPE)) ||
							pAdjacentBlockState.getBlock() instanceof IsotropicCubeBlock)
					&& pDirection.getAxis() != Direction.Axis.Y) {
				faceBlocked = true;
			}
		}

		return (this.isGlass && faceBlocked) || super.skipRendering(pState, pAdjacentBlockState, pDirection);
	}

	@Override
	public VoxelShape getShapeStatic(BlockState pState) {
		SlabType slabtype = pState.getValue(TYPE);
		return switch (slabtype) {
			case DOUBLE -> Shapes.block();
			case TOP -> TOP_AABB;
			default -> BOTTOM_AABB;
		};
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
