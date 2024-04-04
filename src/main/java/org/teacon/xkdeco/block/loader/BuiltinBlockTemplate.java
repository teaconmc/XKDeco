package org.teacon.xkdeco.block.loader;

import java.util.Optional;

import org.apache.commons.lang3.mutable.MutableObject;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public record BuiltinBlockTemplate(
		Optional<BlockDefinitionProperties> properties,
		Optional<ResourceLocation> key,
		MutableObject<MapCodec<Block>> codec) implements KBlockTemplate {
	public static Codec<BuiltinBlockTemplate> codec(MapCodec<Optional<KMaterial>> materialCodec) {
		return RecordCodecBuilder.create(instance -> instance.group(
				BlockDefinitionProperties.mapCodecField(materialCodec).forGetter(BuiltinBlockTemplate::properties),
				ResourceLocation.CODEC.optionalFieldOf("codec").forGetter(BuiltinBlockTemplate::key)
		).apply(instance, BuiltinBlockTemplate::new));
	}

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	public BuiltinBlockTemplate(Optional<BlockDefinitionProperties> properties, Optional<ResourceLocation> key) {
		this(properties, key, new MutableObject<>());
	}

	@Override
	public Type<?> type() {
		return KBlockTemplates.BUILTIN.getOrCreate();
	}

	@Override
	public void resolve(ResourceLocation key) {
		codec.setValue(BlockCodecs.get(this.key.orElse(key)));
	}

	@Override
	public Block createBlock(BlockBehaviour.Properties properties, JsonObject json) {
		if (!json.has(BlockCodecs.BLOCK_PROPERTIES_KEY)) {
			json.add(BlockCodecs.BLOCK_PROPERTIES_KEY, new JsonObject());
		}
		InjectedBlockPropertiesCodec.INJECTED.set(properties);
		DataResult<Block> result = codec.getValue().decode(JsonOps.INSTANCE, JsonOps.INSTANCE.getMap(json).result().orElseThrow());
		if (result.error().isPresent()) {
			throw new IllegalStateException(result.error().get().message());
		}
		return result.result().orElseThrow();
	}
}
