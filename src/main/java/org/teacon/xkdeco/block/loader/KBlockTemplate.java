package org.teacon.xkdeco.block.loader;

import java.util.Optional;
import java.util.function.Function;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public interface KBlockTemplate {
	static Codec<KBlockTemplate> codec(MapCodec<Optional<KMaterial>> materialCodec) {
		return LoaderExtraRegistries.BLOCK_TEMPLATE.byNameCodec().dispatch(
				"type",
				KBlockTemplate::type,
				type -> type.codec().apply(materialCodec));
	}

	Type<?> type();

	void resolve(ResourceLocation key);

	Block createBlock(BlockBehaviour.Properties properties, JsonObject input);

	Optional<BlockDefinitionProperties> properties();

	record Type<T extends KBlockTemplate>(Function<MapCodec<Optional<KMaterial>>, Codec<T>> codec) {}
}
