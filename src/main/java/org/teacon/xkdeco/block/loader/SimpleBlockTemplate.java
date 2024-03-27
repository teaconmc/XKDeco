package org.teacon.xkdeco.block.loader;

import org.teacon.xkdeco.block.setting.KBlockSettings;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public record SimpleBlockTemplate(String clazz) implements KBlockTemplate {
	public static final Codec<SimpleBlockTemplate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.fieldOf("class").forGetter(SimpleBlockTemplate::clazz)
	).apply(instance, SimpleBlockTemplate::new));

	@Override
	public Type<?> type() {
		return KBlockTemplates.SIMPLE.getOrCreate();
	}

	@Override
	public void resolve(ResourceLocation key) {

	}

	@Override
	public Block createBlock(KBlockSettings settings, JsonObject input) {
		return null;
	}
}
