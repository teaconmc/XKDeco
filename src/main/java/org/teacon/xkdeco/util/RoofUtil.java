package org.teacon.xkdeco.util;

import java.util.EnumMap;
import java.util.Locale;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teacon.xkdeco.block.RoofBlock;
import org.teacon.xkdeco.block.XKDecoBlock;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.VoxelShape;

public class RoofUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger("RoofUtil");

	public static BlockState getStateForPlacement(XKDecoBlock.Roof block, Level level, BlockPos clickedPos, Direction[] directions) {
		var maxWeakCount = -1;
		var maxStrongCount = -1;
		var maxMatchedState = (BlockState) null;
		var facingStates = new EnumMap<Direction, BlockState>(Direction.class);
		var waterlogged = level.getFluidState(clickedPos).getType() == Fluids.WATER;
		for (var updateSide : new boolean[]{true, false}) {
			for (var matchedState : block.getPlacementChoices(waterlogged, updateSide, directions)) {
				var weakCount = 0;
				var strongCount = 0;
				for (var side : directions) {
					var sideState = facingStates.computeIfAbsent(side, d -> level.getBlockState(clickedPos.relative(d)));
					if (matchFacing(matchedState, sideState, side, updateSide, false)) {
						LOGGER.debug("{}: strong facing {} <=> {}", matchedState, side, sideState);
						strongCount += 1;
					} else if (matchFacing(matchedState, sideState, side, updateSide, true)) {
						LOGGER.debug("{}: weak facing {} <=> {}", matchedState, side, sideState);
						weakCount += 1;
					}
				}
				LOGGER.debug("{}: strong {} weak {}", matchedState, strongCount, weakCount);
				if (maxStrongCount < strongCount || maxStrongCount == strongCount && maxWeakCount < weakCount) {
					maxWeakCount = weakCount;
					maxStrongCount = strongCount;
					maxMatchedState = matchedState;
				}
			}
		}
		return Objects.requireNonNull(maxMatchedState);
	}

	public static boolean matchFacing(BlockState state, BlockState facingState, Direction facing, boolean updateSide, boolean lenient) {
		var stateBlock = state.getBlock();
		var facingBlock = facingState.getBlock();
		return switch (facing) {
			case DOWN, UP -> Direction.Plane.HORIZONTAL.stream().allMatch(side -> {
				IntTriple back = stateBlock instanceof XKDecoBlock.Roof r ? r.getSideHeight(state, side) : null;
				IntTriple front;
				if (updateSide) {
					if (facingBlock instanceof XKDecoBlock.Roof r) {
						var choice = r.getUpdateShapeChoice(facingState, facing.getOpposite());
						front = choice.map(c -> r.getSideHeight(c, side)).orElse(null);
					} else {
						front = null;
					}
				} else {
					front = facingBlock instanceof XKDecoBlock.Roof r ? r.getSideHeight(facingState, side) : null;
				}
				return back != null && front != null && switch (facing.getAxisDirection()) {
					case NEGATIVE -> IntTriple.matchDownUp(front, back);
					case POSITIVE -> IntTriple.matchDownUp(back, front);
				};
			});
			case NORTH, SOUTH, WEST, EAST -> {
				IntTriple back = stateBlock instanceof XKDecoBlock.Roof r ? r.getSideHeight(state, facing) : lenient ? IntTriple.of(
						0,
						0,
						0) : null;
				IntTriple front;
				if (updateSide) {
					if (facingBlock instanceof XKDecoBlock.Roof r) {
						var choice = r.getUpdateShapeChoice(facingState, facing.getOpposite());
						front = choice.map(c -> r.getSideHeight(c, facing.getOpposite())).orElse(null);
					} else {
						front = null;
					}
				} else {
					front = facingBlock instanceof XKDecoBlock.Roof r ? r.getSideHeight(facingState, facing.getOpposite()) : null;
				}
				yield back != null && front != null && IntTriple.matchFrontBack(front, back);
			}
		};
	}

	public static BlockState updateShape(BlockState oldState, BlockState fromState, Direction fromDirection) {
		if (oldState.getBlock() instanceof XKDecoBlock.Roof roof) {
			var newState = roof.getUpdateShapeChoice(oldState, fromDirection);
			return newState.filter(s -> matchFacing(s, fromState, fromDirection, false, false)).orElse(oldState);
		}
		return oldState;
	}

	public static VoxelShape getShape(RoofShape shape, Direction facing, RoofHalf half, RoofVariant variant) {
		var indexLeftRight = switch (shape) {
			case STRAIGHT -> facing.getCounterClockWise().get2DDataValue() * 4 + facing.getClockWise().get2DDataValue();
			case INNER -> facing.getCounterClockWise().get2DDataValue() * 4 + facing.getOpposite().get2DDataValue();
			case OUTER -> facing.get2DDataValue() * 4 + facing.getClockWise().get2DDataValue();
		};
		return switch (half) {
			case TIP -> RoofBlock.ROOF_SHAPES.get(variant.ordinal() * 16 + indexLeftRight);
			case BASE -> RoofBlock.ROOF_BASE_SHAPES.get(variant.ordinal() * 16 + indexLeftRight);
		};
	}

	@MethodsReturnNonnullByDefault
	@ParametersAreNonnullByDefault
	public enum RoofHalf implements StringRepresentable {
		BASE, TIP;

		@Override
		public String getSerializedName() {
			return this.name().toLowerCase(Locale.ROOT);
		}

		@Override
		public String toString() {
			return this.name().toLowerCase(Locale.ROOT);
		}
	}

	@MethodsReturnNonnullByDefault
	@ParametersAreNonnullByDefault
	public enum RoofShape implements StringRepresentable {
		STRAIGHT, INNER, OUTER;

		@Override
		public String getSerializedName() {
			return this.name().toLowerCase(Locale.ROOT);
		}

		@Override
		public String toString() {
			return this.name().toLowerCase(Locale.ROOT);
		}
	}

	@MethodsReturnNonnullByDefault
	@ParametersAreNonnullByDefault
	public enum RoofEndShape implements StringRepresentable {
		LEFT, RIGHT;

		@Override
		public String getSerializedName() {
			return this.name().toLowerCase(Locale.ROOT);
		}

		@Override
		public String toString() {
			return this.name().toLowerCase(Locale.ROOT);
		}
	}

	@MethodsReturnNonnullByDefault
	@ParametersAreNonnullByDefault
	public enum RoofEaveShape implements StringRepresentable {
		STRAIGHT, INNER, OUTER, LEFT_END, RIGHT_END;

		@Override
		public String getSerializedName() {
			return this.name().toLowerCase(Locale.ROOT);
		}

		@Override
		public String toString() {
			return this.name().toLowerCase(Locale.ROOT);
		}
	}

	@MethodsReturnNonnullByDefault
	@ParametersAreNonnullByDefault
	public enum RoofVariant implements StringRepresentable {
		NORMAL, SLOW, STEEP;

		@Override
		public String getSerializedName() {
			return this.name().toLowerCase(Locale.ROOT);
		}

		@Override
		public String toString() {
			return this.name().toLowerCase(Locale.ROOT);
		}
	}
}
