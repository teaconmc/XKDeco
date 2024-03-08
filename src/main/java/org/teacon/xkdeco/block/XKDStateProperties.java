package org.teacon.xkdeco.block;

import java.util.List;

import org.teacon.xkdeco.util.RoofUtil;

import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public interface XKDStateProperties {
	EnumProperty<RoofUtil.RoofVariant> ROOF_VARIANT = EnumProperty.create("variant", RoofUtil.RoofVariant.class);
	EnumProperty<RoofUtil.RoofHalf> ROOF_HALF = EnumProperty.create("half", RoofUtil.RoofHalf.class);
	List<BooleanProperty> DIRECTION_PROPERTIES = List.of(
			BlockStateProperties.DOWN,
			BlockStateProperties.UP,
			BlockStateProperties.NORTH,
			BlockStateProperties.SOUTH,
			BlockStateProperties.WEST,
			BlockStateProperties.EAST);
}
