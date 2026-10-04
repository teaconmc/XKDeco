package org.teacon.xkdeco.init;

import org.teacon.xkdeco.XKDeco;
import org.teacon.xkdeco.block.ItemDisplayBlock;
import org.teacon.xkdeco.block.OneDirectionFenceGateBlock;
import org.teacon.xkdeco.block.SpecialSlabBlock;
import org.teacon.xkdeco.block.XKDBlock;

import net.minecraft.world.level.block.Block;
import snownee.kiwi.AbstractModule;
import snownee.kiwi.KiwiModule;
import snownee.kiwi.customization.block.loader.BlockCodecs;

@KiwiModule("codecs")
public class XKDecoCodecs extends AbstractModule {
	public XKDecoCodecs() {
		BlockCodecs.register(XKDeco.id("block"), Block.simpleCodec(XKDBlock::new));
		BlockCodecs.register(XKDeco.id("special_slab"), SpecialSlabBlock.CODEC);
		BlockCodecs.register(XKDeco.id("one_direction_fence_gate"), OneDirectionFenceGateBlock.CODEC);
		BlockCodecs.register(XKDeco.id("item_display"), ItemDisplayBlock.CODEC);
	}
}
