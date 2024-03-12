package org.teacon.xkdeco.block;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class PlantSlabBlock extends SlabBlock {
	private static final VoxelShape PATH_TOP_AABB = Block.box(0, 8, 0, 16, 15, 16);
	private static final VoxelShape PATH_BOTTOM_AABB = Block.box(0, 0, 0, 16, 7, 16);
	private static final VoxelShape PATH_DOUBLE_AABB = Block.box(0, 0, 0, 16, 15, 16);

	private final boolean isPath;

	public PlantSlabBlock(Properties properties, boolean isPath) {
		super(properties);
		this.isPath = isPath;
	}

	@Override
	public boolean useShapeForLightOcclusion(BlockState state) {
		return this.isPath || state.getValue(TYPE) == SlabType.BOTTOM;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return switch (state.getValue(TYPE)) {
			case TOP -> this.isPath ? PATH_TOP_AABB : TOP_AABB;
			case BOTTOM -> this.isPath ? PATH_BOTTOM_AABB : BOTTOM_AABB;
			case DOUBLE -> this.isPath ? PATH_DOUBLE_AABB : Shapes.block();
		};
	}

	@Override
	public BlockState updateShape(
			BlockState state, Direction facing,
			BlockState facingState, LevelAccessor world, BlockPos pos, BlockPos facingPos) {
		if (facing.getAxis() == Direction.Axis.Y && !state.canSurvive(world, pos)) {
			world.scheduleTick(pos, this, 1);
		}
		return super.updateShape(state, facing, facingState, world, pos, facingPos);
	}


	@Override
	@SuppressWarnings("deprecation")
	public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
		if (this.isPath && state.getValue(TYPE) != SlabType.BOTTOM) {
			var aboveState = world.getBlockState(pos.above());
			return !aboveState.isSolid() || aboveState.getBlock() instanceof FenceGateBlock;
		}
		return true;
	}

	@Override
	public boolean isRandomlyTicking(BlockState pState) {
		return !this.isPath;
	}

}
