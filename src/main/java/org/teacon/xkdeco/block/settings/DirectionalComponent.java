package org.teacon.xkdeco.block.settings;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

public record DirectionalComponent(boolean customPlacement) implements XKBlockComponent {
	public static final DirectionProperty FACING = BlockStateProperties.FACING;
	private static final DirectionalComponent TRUE = new DirectionalComponent(true);
	private static final DirectionalComponent FALSE = new DirectionalComponent(false);
	public static final Type<DirectionalComponent> TYPE = XKBlockComponent.register(
			"directional",
			RecordCodecBuilder.create(instance -> instance.group(
					Codec.BOOL.optionalFieldOf("custom_placement", false).forGetter(DirectionalComponent::customPlacement)
			).apply(instance, DirectionalComponent::getInstance)));

	public static DirectionalComponent getInstance(boolean customPlacement) {
		return customPlacement ? TRUE : FALSE;
	}

	@Override
	public Type<?> type() {
		return TYPE;
	}

	@Override
	public void injectProperties(Block block, StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING);
	}

	@Override
	public BlockState registerDefaultState(BlockState state) {
		return state.setValue(FACING, Direction.DOWN);
	}

	@Override
	public @Nullable BlockState getStateForPlacement(BlockState state, BlockPlaceContext context) {
		if (!customPlacement) {
			for (Direction direction : context.getNearestLookingDirections()) {
				BlockState blockstate;
				blockstate = state.setValue(FACING, direction.getOpposite());
				if (blockstate.canSurvive(context.getLevel(), context.getClickedPos())) {
					return blockstate;
				}
			}
			return null;
		}
		return state;
	}

	@Override
	public BlockState rotate(BlockState pState, Rotation pRotation) {
		return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState pState, Mirror pMirror) {
		//noinspection deprecation
		return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
	}
}
