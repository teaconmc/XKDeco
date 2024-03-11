package org.teacon.xkdeco.block;

import java.util.Arrays;

import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BasicCubeBlock extends HorizontalDirectionalBlock {

	public BasicCubeBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.getStateDefinition().any().setValue(FACING, Direction.NORTH));
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		var directions = context.getNearestLookingDirections();
		var direction = Arrays.stream(directions).filter(Direction.Plane.HORIZONTAL).findFirst();
		return this.defaultBlockState().setValue(FACING, direction.orElse(Direction.SOUTH).getOpposite());
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}
}
