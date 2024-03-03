package org.teacon.xkdeco.block;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.level.block.LeavesBlock;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public final class PlantLeavesBlock extends LeavesBlock implements XKDecoBlock.Plant {
	public PlantLeavesBlock(Properties properties) {
		super(properties);
	}
}
