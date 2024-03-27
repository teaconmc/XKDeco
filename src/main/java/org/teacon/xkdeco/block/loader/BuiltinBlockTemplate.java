package org.teacon.xkdeco.block.loader;

import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.mutable.MutableObject;
import org.teacon.xkdeco.block.setting.KBlockSettings;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public record BuiltinBlockTemplate(Optional<ResourceLocation> key, MutableObject<MapCodec<Block>> codec) implements KBlockTemplate {
	public static final Codec<BuiltinBlockTemplate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ResourceLocation.CODEC.optionalFieldOf("codec").forGetter(BuiltinBlockTemplate::key)
	).apply(instance, BuiltinBlockTemplate::new));
	private static final MapLike<JsonElement> DEFAULT_PROPERTIES = MapLike.forMap(Map.of(
			new JsonPrimitive(BlockCodecs.BLOCK_PROPERTIES_KEY),
			new JsonObject()), JsonOps.INSTANCE);

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	public BuiltinBlockTemplate(Optional<ResourceLocation> key) {
		this(key, new MutableObject<>());
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
	public Block createBlock(KBlockSettings settings, JsonObject json) {
		if (!json.has(BlockCodecs.BLOCK_PROPERTIES_KEY)) {
			json.add(BlockCodecs.BLOCK_PROPERTIES_KEY, new JsonObject());
		}
		InjectedBlockPropertiesCodec.INJECTED.set(settings);
		DataResult<Block> result = codec.getValue().decode(JsonOps.INSTANCE, JsonOps.INSTANCE.getMap(json).result().orElseThrow());
		if (result.error().isPresent()) {
			throw new IllegalStateException(result.error().get().message());
		}
		return result.result().orElseThrow();
	}
}
