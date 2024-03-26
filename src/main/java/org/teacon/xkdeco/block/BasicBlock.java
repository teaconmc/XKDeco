package org.teacon.xkdeco.block;

import org.teacon.xkdeco.block.setting.CheckedWaterloggedBlock;
import org.teacon.xkdeco.util.KBlockUtils;
import org.teacon.xkdeco.util.XKDPlatformBlock;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.level.block.Block;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BasicBlock extends Block implements CheckedWaterloggedBlock, XKDPlatformBlock, KBlockUtils {
	public BasicBlock(Properties properties) {
		super(properties);
	}
}
