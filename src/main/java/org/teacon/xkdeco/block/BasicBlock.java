package org.teacon.xkdeco.block;

import org.teacon.xkdeco.block.setting.CheckedWaterloggedBlock;
import org.teacon.xkdeco.util.XKBlockUtils;
import org.teacon.xkdeco.util.XKDPlatformBlock;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.level.block.Block;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BasicBlock extends Block implements CheckedWaterloggedBlock, XKDPlatformBlock, XKBlockUtils {
	public BasicBlock(Properties properties) {
		super(properties);
	}
}
