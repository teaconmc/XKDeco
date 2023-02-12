package org.teacon.xkdeco.util;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.util.TriPredicate;
import org.teacon.xkdeco.block.RoofBlock;
import org.teacon.xkdeco.block.RoofEaveBlock;
import org.teacon.xkdeco.block.RoofFlatBlock;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Locale;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

public class RoofUtil {
    /*** Test Roof Type ***/
    public static boolean isRoof(BlockState state) {
        return state.getBlock() instanceof RoofBlock;
    }

    public static boolean isFlatRoof(BlockState state) {
        return state.getBlock() instanceof RoofFlatBlock;
    }

    public static boolean isEave(BlockState state) {
        return state.getBlock() instanceof RoofEaveBlock;
    }

    /*** Test Open Shape ***/
    public static boolean isOpenSide(RoofShape shape, Rotation actual, Rotation expected) {
        return switch (shape) {
            case STRAIGHT -> actual == expected;
            case INNER -> actual == expected || actual.getClockWise() == expected;
            case OUTER -> false;
        };
    }

    public static boolean isHalfOpenClockWiseSide(RoofShape shape, Rotation actual, Rotation expected) {
        return switch (shape) {
            case STRAIGHT, INNER -> actual.getCounterClockWise() == expected;
            case OUTER -> actual == expected;
        };
    }

    public static boolean isHalfOpenCounterClockWiseSide(RoofShape shape, Rotation actual, Rotation expected) {
        return switch (shape) {
            case STRAIGHT, OUTER -> actual.getClockWise() == expected;
            case INNER -> actual.get2DOpposite() == expected;
        };
    }

    public static boolean isClosedSide(RoofShape shape, Rotation actual, Rotation expected) {
        return switch (shape) {
            case STRAIGHT -> actual.get2DOpposite() == expected;
            case INNER -> false;
            case OUTER -> actual.getCounterClockWise() == expected || actual.get2DOpposite() == expected;
        };
    }

    public interface PlacementCheckerModifier extends QuadFunction<
            // placement Level, placement BlockPos, placement Direction, initial BlockState
            Level, BlockPos, Direction, BlockState,
            // modified BlockState, if passed the checker, otherwise is empty
            Optional<BlockState>> {
    }

    /*** Test Neighbor Roof State ***/
    public static PlacementCheckerModifier tryConnectTo(
            Rotation target,
            QuadPredicate<Rotation, RoofShape, RoofHalf, RoofVariant> rotHalfVariantPredicate,
            BinaryOperator<BlockState> thenSet) {

        return (level, placePos, placeDirection, initialState) -> {
            var targetPosDirection = target.rotate(placeDirection);
            var targetPos = placePos.relative(targetPosDirection);
            var targetState = level.getBlockState(targetPos);

            if (isRoof(targetState) && rotHalfVariantPredicate.test(
                    Rotation.fromDirections(placeDirection, targetState.getValue(RoofBlock.FACING)),
                    targetState.getValue(RoofBlock.SHAPE),
                    targetState.getValue(RoofBlock.HALF),
                    targetState.getValue(RoofBlock.VARIANT))
            ) {
                return Optional.of(thenSet.apply(initialState, targetState));
            } else {
                return Optional.empty();
            }
        };
    }

    public static PlacementCheckerModifier tryConnectToFlat(
            Rotation target,
            BiPredicate<Boolean, RoofHalf> parallelHalfPredicate,
            BinaryOperator<BlockState> thenSet) {

        return (level, placePos, placeDirection, initialState) -> {
            var targetPosDirection = target.rotate(placeDirection);
            var targetPos = placePos.relative(targetPosDirection);
            var targetState = level.getBlockState(targetPos);

            if (isFlatRoof(targetState) && parallelHalfPredicate.test(
                    targetState.getValue(RoofFlatBlock.AXIS) == placeDirection.getAxis(),
                    targetState.getValue(RoofFlatBlock.HALF))
            ) {
                return Optional.of(thenSet.apply(initialState, targetState));
            } else {
                return Optional.empty();
            }
        };
    }

    public static PlacementCheckerModifier tryConnectToEave(
            Rotation target,
            TriPredicate<Rotation, RoofShape, RoofHalf> rotHalfPredicate,
            BinaryOperator<BlockState> thenSet) {

        return (level, placePos, placeDirection, initialState) -> {
            var targetPosDirection = target.rotate(placeDirection);
            var targetPos = placePos.relative(targetPosDirection);
            var targetState = level.getBlockState(targetPos);

            if (isEave(targetState) && rotHalfPredicate.test(
                    Rotation.fromDirections(placeDirection, targetState.getValue(RoofEaveBlock.FACING)),
                    targetState.getValue(RoofEaveBlock.SHAPE),
                    targetState.getValue(RoofEaveBlock.HALF))
            ) {
                return Optional.of(thenSet.apply(initialState, targetState));
            } else {
                return Optional.empty();
            }
        };
    }

    public static RoofHalf getPlacementHalf(BlockPlaceContext pContext) {
        return switch (pContext.getClickedFace()) {
            case UP -> RoofHalf.TIP;
            case DOWN -> RoofHalf.BASE;
            case EAST, WEST, NORTH, SOUTH ->
                    pContext.getClickLocation().y() - Math.floor(pContext.getClickLocation().y()) > 0.5
                            ? RoofHalf.BASE : RoofHalf.TIP;
        };
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

    public enum Rotation {
        LEFT(Direction::getCounterClockWise),
        FRONT(UnaryOperator.identity()),
        RIGHT(Direction::getClockWise),
        BACK(Direction::getOpposite),
        UP(d -> Direction.UP),
        DOWN(direction -> Direction.DOWN);

        private final UnaryOperator<Direction> rotator;

        Rotation(UnaryOperator<Direction> rotator) {
            this.rotator = rotator;
        }

        public Direction rotate(Direction direction) {
            return this.rotator.apply(direction);
        }

        public Rotation getClockWise() {
            return switch (this) {
                case LEFT -> FRONT;
                case FRONT -> RIGHT;
                case RIGHT -> BACK;
                case BACK -> LEFT;
                case UP, DOWN -> this;
            };
        }

        public Rotation getCounterClockWise() {
            return switch (this) {
                case LEFT -> BACK;
                case FRONT -> LEFT;
                case RIGHT -> FRONT;
                case BACK -> RIGHT;
                case UP, DOWN -> this;
            };
        }

        public Rotation get2DOpposite() {
            return switch (this) {
                case LEFT -> RIGHT;
                case FRONT -> BACK;
                case RIGHT -> LEFT;
                case BACK -> FRONT;
                case UP, DOWN -> this;
            };
        }

        public static Rotation fromDirections(Direction start, Direction end) {
            for (var r : Rotation.values()) if (end == r.rotate(start)) return r;
            throw new IllegalStateException("wtf why none of the rotations matches");
        }
    }

    @MethodsReturnNonnullByDefault
    @ParametersAreNonnullByDefault
    public enum RoofHalf implements StringRepresentable {
        BASE, TIP;

        public RoofHalf otherHalf() {
            return this == BASE ? TIP : BASE;
        }

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
