package org.teacon.xkdeco.block.loader;

import java.util.function.Function;

import org.apache.commons.lang3.mutable.MutableObject;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public record SimpleBlockTemplate(
		String clazz,
		MutableObject<Function<BlockBehaviour.Properties, Block>> constructor) implements KBlockTemplate {
	public static final Codec<SimpleBlockTemplate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.fieldOf("class").forGetter(SimpleBlockTemplate::clazz)
	).apply(instance, SimpleBlockTemplate::new));

	public SimpleBlockTemplate(String clazz) {
		this(clazz, new MutableObject<>());
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
