package org.teacon.xkdeco.block.setting;

import net.minecraft.world.level.block.state.properties.IntegerProperty;

public interface LayeredComponent {
	IntegerProperty getLayerProperty();

	int getDefaultLayer();
}
