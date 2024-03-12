package org.teacon.xkdeco.block;

import org.teacon.xkdeco.block.settings.CheckedWaterloggedBlock;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.level.block.Block;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BasicBlock extends Block implements CheckedWaterloggedBlock {
	public BasicBlock(Properties properties) {
		super(properties);
	}
}
