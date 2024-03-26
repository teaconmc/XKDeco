package org.teacon.xkdeco.util;

import java.util.Map;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;

import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

public class JsonLoader {
	private static final Gson GSON = new GsonBuilder().create();

	public static <T> Map<ResourceLocation, T> load(ResourceManager resourceManager, String directory, Codec<T> codec) {
		FileToIdConverter fileToIdConverter = FileToIdConverter.json(directory);
		Map<ResourceLocation, T> results = Maps.newHashMap();
		for (Map.Entry<ResourceLocation, Resource> entry : fileToIdConverter.listMatchingResources(resourceManager).entrySet()) {
			ResourceLocation id = fileToIdConverter.fileToId(entry.getKey());
			try {
				JsonObject json = GSON.fromJson(entry.getValue().openAsReader(), JsonObject.class);
				T value = codec.parse(JsonOps.INSTANCE, json).result().orElseThrow();
				results.put(id, value);
			} catch (Exception e) {
				throw new IllegalStateException("Failed to load " + id, e);
			}
		}
		return results;
	}
}
