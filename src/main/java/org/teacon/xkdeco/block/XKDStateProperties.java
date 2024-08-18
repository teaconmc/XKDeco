package org.teacon.xkdeco.block;

import java.util.List;
import java.util.Locale;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public interface XKDStateProperties {
	List<BooleanProperty> DIRECTION_PROPERTIES = List.of(
			BlockStateProperties.DOWN,
			BlockStateProperties.UP,
			BlockStateProperties.NORTH,
			BlockStateProperties.SOUTH,
			BlockStateProperties.WEST,
			BlockStateProperties.EAST);
	EnumProperty<RoofHalf> HALF = EnumProperty.create("half", RoofHalf.class);
	EnumProperty<RoofVariant> ROOF_VARIANT = EnumProperty.create("variant", RoofVariant.class);
	EnumProperty<RoofVariant> ROOF_VARIANT_WITHOUT_SLOW = EnumProperty.create(
			"variant",
			RoofVariant.class,
			RoofVariant.NORMAL,
			RoofVariant.STEEP);
	EnumProperty<RoofShape> ROOF_SHAPE = EnumProperty.create("shape", RoofShape.class);
	EnumProperty<RoofEaveShape> ROOF_EAVE_SHAPE = EnumProperty.create("shape", RoofEaveShape.class);
	EnumProperty<RoofEndShape> ROOF_END_SHAPE = EnumProperty.create("shape", RoofEndShape.class);

	enum RoofHalf implements StringRepresentable {
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

	enum RoofShape implements StringRepresentable {
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

	enum RoofEndShape implements StringRepresentable {
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

	enum RoofEaveShape implements StringRepresentable {
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

	enum RoofVariant implements StringRepresentable {
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
