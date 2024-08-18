package org.teacon.xkdeco.block;

import org.teacon.xkdeco.XKDeco;
import org.teacon.xkdeco.util.XKDPlatformBlock;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import snownee.kiwi.AbstractModule;

public class XKDBlock extends Block implements CheckedWaterloggedBlock, XKDPlatformBlock {
	public static final TagKey<Block> AIR_DUCTS = AbstractModule.blockTag(XKDeco.ID, "air_ducts");

	public XKDBlock(Properties properties) {
		super(properties);
	}
}
