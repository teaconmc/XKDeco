package org.teacon.xkdeco.block.loader;

import java.util.Optional;
import java.util.function.Function;

import org.apache.commons.lang3.mutable.MutableObject;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public record SimpleBlockTemplate(
		Optional<BlockDefinitionProperties> properties,
		String clazz,
		MutableObject<Function<BlockBehaviour.Properties, Block>> constructor) implements KBlockTemplate {
	public static Codec<SimpleBlockTemplate> codec(MapCodec<Optional<KMaterial>> materialCodec) {
		return RecordCodecBuilder.create(instance -> instance.group(
				BlockDefinitionProperties.mapCodecField(materialCodec).forGetter(SimpleBlockTemplate::properties),
				Codec.STRING.fieldOf("class").forGetter(SimpleBlockTemplate::clazz)
		).apply(instance, SimpleBlockTemplate::new));
	}

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	public SimpleBlockTemplate(Optional<BlockDefinitionProperties> properties, String clazz) {
		this(properties, clazz, new MutableObject<>());
	}

	@Override
	public Type<?> type() {
		return KBlockTemplates.SIMPLE.getOrCreate();
	}

	@Override
	public void resolve(ResourceLocation key) {
		try {
			Class<?> clazz = Class.forName(this.clazz);
			this.constructor.setValue($ -> {
				try {
					return (Block) clazz.getConstructor(BlockBehaviour.Properties.class).newInstance($);
				} catch (Throwable e) {
					throw new RuntimeException(e);
				}
			});
		} catch (Throwable e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public Block createBlock(BlockBehaviour.Properties settings, JsonObject input) {
		return this.constructor.getValue().apply(settings);
	}
}
