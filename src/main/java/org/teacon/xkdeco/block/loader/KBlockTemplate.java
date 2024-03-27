package org.teacon.xkdeco.block.loader;

import org.teacon.xkdeco.block.setting.KBlockSettings;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public interface KBlockTemplate {
	Codec<KBlockTemplate> DIRECT_CODEC = LoaderExtraRegistries.BLOCK_TEMPLATE.byNameCodec().dispatch("type", KBlockTemplate::type, Type::codec);

	Type<?> type();

	void resolve(ResourceLocation key);

	Block createBlock(KBlockSettings settings, JsonObject input);

	record Type<T extends KBlockTemplate>(Codec<T> codec) {}
}
