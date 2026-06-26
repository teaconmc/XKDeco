package org.teacon.xkdeco.block;

import java.util.Map;

import org.jspecify.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.WallSide;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import snownee.kiwi.customization.placement.PlaceSlot;

public class RoofRidgeBlock extends WallBlock {
	public static final Map<Direction, EnumProperty<WallSide>> DIRECTION_TO_PROPERTY = Map.of(
			Direction.NORTH,
			BlockStateProperties.NORTH_WALL,
			Direction.EAST,
			BlockStateProperties.EAST_WALL,
			Direction.SOUTH,
			BlockStateProperties.SOUTH_WALL,
			Direction.WEST,
			BlockStateProperties.WEST_WALL);

	public RoofRidgeBlock(Properties properties) {
		super(properties);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		LevelReader level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
		BlockState blockState = defaultBlockState().setValue(WATERLOGGED, fluidState.is(Fluids.WATER));
		BlockPos.MutableBlockPos mutable = pos.mutable();
		@Nullable WallSide[] wallSides = new WallSide[4];
		boolean hasTall = false;
		int i = 0;
		for (Direction direction : Direction.Plane.HORIZONTAL) {
			mutable.setWithOffset(pos, direction);
			BlockState offsetBlock = level.getBlockState(mutable);
			WallSide wallSide = makeSide(level, mutable, offsetBlock, direction);
			wallSides[i] = wallSide;
			if (wallSide == WallSide.TALL) {
				hasTall = true;
			}
			++i;
		}
		i = 0;
		for (Direction direction : Direction.Plane.HORIZONTAL) {
			WallSide wallSide = wallSides[i];
			if (wallSide == null) {
				wallSide = hasTall ? WallSide.TALL : WallSide.LOW;
			}
			EnumProperty<WallSide> property = DIRECTION_TO_PROPERTY.get(direction);
			blockState = blockState.setValue(property, wallSide);
			++i;
		}
		return blockState;
	}

	@Nullable
	public static WallSide makeSide(LevelReader level, BlockPos pos, BlockState offsetBlock, Direction direction) {
		if (offsetBlock.getBlock().getClass() == RoofRidgeBlock.class) {
			Direction direction1 = direction.getOpposite();
			for (int j = 0; j < 4; j++) {
				WallSide wallSide = offsetBlock.getValue(DIRECTION_TO_PROPERTY.get(direction1));
				if (wallSide != WallSide.NONE) {
					return wallSide;
				}
				direction1 = direction1.getClockWise();
			}
			return WallSide.LOW;
		} else if (offsetBlock.hasProperty(XKDStateProperties.ROOF_VARIANT_WITHOUT_SLOW) && PlaceSlot.find(
				offsetBlock,
				direction.getOpposite(),
				"*roof_ridge_end").isPresent()) {
			return "steep".equals(offsetBlock.getValue(XKDStateProperties.ROOF_VARIANT_WITHOUT_SLOW)) ? WallSide.TALL : WallSide.LOW;
		} else if (!Block.isExceptionForConnection(offsetBlock) && offsetBlock.isFaceSturdy(level, pos, direction.getOpposite())) {
			return null;
		}
		return WallSide.NONE;
	}
}
