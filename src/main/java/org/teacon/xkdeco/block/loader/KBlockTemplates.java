package org.teacon.xkdeco.block.loader;

import com.mojang.serialization.Codec;

import snownee.kiwi.AbstractModule;
import snownee.kiwi.KiwiGO;
import snownee.kiwi.KiwiModule;

@KiwiModule("block_templates")
public class KBlockTemplates extends AbstractModule {
	@KiwiModule.Name("minecraft:simple")
	public static final KiwiGO<KBlockTemplate.Type<SimpleBlockTemplate>> SIMPLE = register(SimpleBlockTemplate.CODEC);
	@KiwiModule.Name("minecraft:builtin")
	public static final KiwiGO<KBlockTemplate.Type<BuiltinBlockTemplate>> BUILTIN = register(BuiltinBlockTemplate.CODEC);

	private static <T extends KBlockTemplate> KiwiGO<KBlockTemplate.Type<T>> register(Codec<T> codec) {
		return go(() -> new KBlockTemplate.Type<>(codec));
	}
}
