package org.teacon.xkdeco.block;

import org.teacon.xkdeco.XKDeco;
import org.teacon.xkdeco.util.XKDPlatformBlock;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.customization.block.BasicBlock;

public class XKDBlock extends BasicBlock implements XKDPlatformBlock {
	public static final TagKey<Block> AIR_DUCTS = AbstractModule.blockTag(XKDeco.ID, "air_ducts");
	public static final TagKey<Block> ROOF_RIDGE = AbstractModule.blockTag(XKDeco.ID, "roof_ridge");

	public XKDBlock(Properties properties) {
		super(properties);
	}
}
