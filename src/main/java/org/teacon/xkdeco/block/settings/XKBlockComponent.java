package org.teacon.xkdeco.block.settings;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.Codec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public interface XKBlockComponent {
	static <T extends XKBlockComponent> Type<T> register(String name, Codec<T> codec) {
		//TODO
		return new Type<>(new ResourceLocation(name), codec);
	}

	Type<?> type();

	void injectProperties(Block block, StateDefinition.Builder<Block, BlockState> builder);

	BlockState registerDefaultState(BlockState state);

	default @Nullable BlockState getStateForPlacement(BlockState state, BlockPlaceContext context) {
		return state;
	}

	default BlockState updateShape(
			BlockState pState,
			Direction pDirection,
			BlockState pNeighborState,
			LevelAccessor pLevel,
			BlockPos pPos,
			BlockPos pNeighborPos) {
		return pState;
	}

	default BlockState rotate(BlockState pState, Rotation pRotation) {
		return pState;
	}

	default BlockState mirror(BlockState pState, Mirror pMirror) {
		return pState;
	}

	default boolean useShapeForLightOcclusion(BlockState pState) {
		return false;
	}

	record Type<T extends XKBlockComponent>(ResourceLocation name, Codec<T> codec) {
		@Override
		public String toString() {
			return "XKBlockComponent.Type[" + this.name + "]";
		}
	}
}
