package org.teacon.xkdeco.block;

import java.util.List;

import org.teacon.xkdeco.util.RoofUtil;
import snownee.kiwi.customization.block.StringProperty;

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
	StringProperty HALF = StringProperty.convert(EnumProperty.create("half", RoofUtil.RoofHalf.class));
	StringProperty ROOF_VARIANT = StringProperty.convert(EnumProperty.create("variant", RoofUtil.RoofVariant.class));
	StringProperty ROOF_VARIANT_WITHOUT_SLOW = StringProperty.convert(EnumProperty.create(
			"variant",
			RoofUtil.RoofVariant.class,
			RoofUtil.RoofVariant.NORMAL,
			RoofUtil.RoofVariant.STEEP));
	StringProperty ROOF_SHAPE = StringProperty.convert(EnumProperty.create("shape", RoofUtil.RoofShape.class));
	StringProperty ROOF_EAVE_SHAPE = StringProperty.convert(EnumProperty.create("shape", RoofUtil.RoofEaveShape.class));
	StringProperty ROOF_END_SHAPE = StringProperty.convert(EnumProperty.create("shape", RoofUtil.RoofEndShape.class));
}
