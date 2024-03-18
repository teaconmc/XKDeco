package org.teacon.xkdeco.block.settings;

import com.mojang.serialization.Codec;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public record HorizontalAxisComponent() implements XKBlockComponent {
	public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;
	private static final HorizontalAxisComponent INSTANCE = new HorizontalAxisComponent();
	public static final Type<HorizontalAxisComponent> TYPE = XKBlockComponent.register(
			"horizontal_axis",
			Codec.unit(INSTANCE));

	public static HorizontalAxisComponent getInstance() {
		return INSTANCE;
	}

	@Override
	public Type<?> type() {
		return TYPE;
	}

	@Override
	public void injectProperties(Block block, StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(AXIS);
	}

	@Override
	public BlockState registerDefaultState(BlockState state) {
		return state.setValue(AXIS, Direction.Axis.X);
	}

	@Override
	public BlockState getStateForPlacement(XKBlockSettings settings, BlockState state, BlockPlaceContext context) {
		if (settings.customPlacement) {
			return state;
		}
		for (Direction direction : context.getNearestLookingDirections()) {
			if (direction.getAxis().isVertical()) {
				continue;
			}
			BlockState blockstate = state.setValue(AXIS, direction.getClockWise().getAxis());
			if (blockstate.canSurvive(context.getLevel(), context.getClickedPos())) {
				return blockstate;
			}
		}
		return null;
	}

	public BlockState rotate(BlockState pState, Rotation pRot) {
		switch (pRot) {
			case COUNTERCLOCKWISE_90:
			case CLOCKWISE_90:
				switch (pState.getValue(AXIS)) {
					case Z:
						return pState.setValue(AXIS, Direction.Axis.X);
					case X:
						return pState.setValue(AXIS, Direction.Axis.Z);
					default:
						return pState;
				}
			default:
				return pState;
		}
	}
}
