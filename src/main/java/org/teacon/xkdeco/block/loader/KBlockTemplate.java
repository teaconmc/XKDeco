package org.teacon.xkdeco.block.loader;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public interface KBlockTemplate {
	Codec<KBlockTemplate> DIRECT_CODEC = ExtraCodecs.lazyInitializedCodec(() -> LoaderExtraRegistries.BLOCK_TEMPLATE.byNameCodec().dispatch(
			"type",
			KBlockTemplate::type,
			KBlockTemplate.Type::codec));

	Type<?> type();

	void resolve(ResourceLocation key);

	Block createBlock(BlockBehaviour.Properties properties, JsonObject input);

	record Type<T extends KBlockTemplate>(Codec<T> codec) {}
}
