package org.teacon.xkdeco.util;

import java.util.Locale;

import org.teacon.xkdeco.block.RoofBlock;

import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.phys.shapes.VoxelShape;
import snownee.kiwi.util.NotNullByDefault;

@NotNullByDefault
public class RoofUtil {
	public static VoxelShape getShape(RoofShape shape, Direction facing, RoofHalf half, RoofVariant variant) {
		var indexLeftRight = switch (shape) {
			case STRAIGHT -> facing.getCounterClockWise().get2DDataValue() * 4 + facing.getClockWise().get2DDataValue();
			case INNER -> facing.getCounterClockWise().get2DDataValue() * 4 + facing.getOpposite().get2DDataValue();
			case OUTER -> facing.get2DDataValue() * 4 + facing.getClockWise().get2DDataValue();
		};
		return switch (half) {
			case LOWER -> RoofBlock.ROOF_SHAPES.get(variant.ordinal() * 16 + indexLeftRight);
			case UPPER -> RoofBlock.ROOF_BASE_SHAPES.get(variant.ordinal() * 16 + indexLeftRight);
		};
	}

	public enum RoofHalf implements StringRepresentable {
		UPPER, LOWER;

		@Override
		public String getSerializedName() {
			return this.name().toLowerCase(Locale.ENGLISH);
		}

		@Override
		public String toString() {
			return this.name().toLowerCase(Locale.ENGLISH);
		}
	}

	public enum RoofShape implements StringRepresentable {
		STRAIGHT, INNER, OUTER;

		@Override
		public String getSerializedName() {
			return this.name().toLowerCase(Locale.ENGLISH);
		}

		@Override
		public String toString() {
			return this.name().toLowerCase(Locale.ENGLISH);
		}
	}

	public enum RoofEndShape implements StringRepresentable {
		LEFT, RIGHT;

		@Override
		public String getSerializedName() {
			return this.name().toLowerCase(Locale.ENGLISH);
		}

		@Override
		public String toString() {
			return this.name().toLowerCase(Locale.ENGLISH);
		}
	}

	public enum RoofEaveShape implements StringRepresentable {
		STRAIGHT, INNER, OUTER, LEFT_END, RIGHT_END;

		@Override
		public String getSerializedName() {
			return this.name().toLowerCase(Locale.ENGLISH);
		}

		@Override
		public String toString() {
			return this.name().toLowerCase(Locale.ENGLISH);
		}
	}

	public enum RoofVariant implements StringRepresentable {
		NORMAL, SLOW, STEEP;

		@Override
		public String getSerializedName() {
			return this.name().toLowerCase(Locale.ENGLISH);
		}

		@Override
		public String toString() {
			return this.name().toLowerCase(Locale.ENGLISH);
		}
	}
}
