package org.teacon.xkdeco.block.loader;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.KeyDispatchCodec;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;

public record ConfiguredBlockTemplate(KBlockTemplate template, Optional<BlockDefinitionProperties> properties, JsonObject json) {
	public static final JsonObject DEFAULT_JSON = new JsonObject();

	static {
		DEFAULT_JSON.add(BlockCodecs.BLOCK_PROPERTIES_KEY, new JsonObject());
	}

	public ConfiguredBlockTemplate(KBlockTemplate template) {
		this(template, Optional.empty(), DEFAULT_JSON);
	}

	public static Codec<ConfiguredBlockTemplate> codec(
			Map<ResourceLocation, KBlockTemplate> templates,
			Codec<BlockDefinitionProperties> propertiesCodec) {
		Function<ConfiguredBlockTemplate, DataResult<KBlockTemplate>> type = $ -> DataResult.success($.template());
		Function<KBlockTemplate, DataResult<Codec<ConfiguredBlockTemplate>>> codec = $ -> DataResult.success(ExtraCodecs.JSON.flatXmap(json -> {
			Optional<BlockDefinitionProperties> properties = Optional.empty();
			JsonObject jsonObject = json.getAsJsonObject();
			JsonObject propertiesJson;
			if (jsonObject.has(BlockCodecs.BLOCK_PROPERTIES_KEY) &&
					(propertiesJson = jsonObject.getAsJsonObject(BlockCodecs.BLOCK_PROPERTIES_KEY)).size() > 0) {
				DataResult<BlockDefinitionProperties> result = propertiesCodec.parse(JsonOps.INSTANCE, propertiesJson);
				if (result.result().isPresent()) {
					properties = result.result();
				} else {
					return DataResult.error(() -> "Failed to parse properties: " + result.error().orElseThrow().message());
				}
			}
			return DataResult.success(new ConfiguredBlockTemplate($, properties, jsonObject));
		}, template -> {
			return DataResult.error(() -> "Unsupported operation");
		}));
		return LoaderExtraCodecs.withAlternative(
				KeyDispatchCodec.unsafe(
						"kiwi:type",
						LoaderExtraCodecs.simpleByNameCodec(templates),
						type,
						codec,
						v -> getCodec(type, codec, v)
				).codec(),
				ResourceLocation.CODEC.flatXmap(
						id -> {
							KBlockTemplate template = templates.get(id);
							if (template == null) {
								return DataResult.error(() -> "Unknown template: " + id);
							}
							return DataResult.success(new ConfiguredBlockTemplate(template));
						},
						template -> DataResult.error(() -> "Unsupported operation"))
		);
	}

	@SuppressWarnings("unchecked")
	private static <K, V> DataResult<? extends Encoder<V>> getCodec(
			final Function<? super V, ? extends DataResult<? extends K>> type,
			final Function<? super K, ? extends DataResult<? extends Encoder<? extends V>>> encoder,
			final V input) {
		return type.apply(input).<Encoder<? extends V>>flatMap(k -> encoder.apply(k).map(Function.identity())).map(c -> ((Encoder<V>) c));
	}
}
