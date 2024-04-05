package org.teacon.xkdeco.util.resource;

import java.io.BufferedReader;
import java.util.Map;
import java.util.SortedMap;

import org.teacon.xkdeco.util.JavaOps;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import snownee.kiwi.Kiwi;
import snownee.kiwi.shadowed.org.yaml.snakeyaml.Yaml;

public class OneTimeLoader {
	private static final Gson GSON = new GsonBuilder().create();
	private static final Yaml YAML = new Yaml();

	public static <T> Map<ResourceLocation, T> load(ResourceManager resourceManager, String directory, Codec<T> codec) {
		var fileToIdConverter = AlternativesFileToIdConverter.yamlOrJson(directory);
		Map<ResourceLocation, T> results = Maps.newHashMap();
		for (Map.Entry<ResourceLocation, Resource> entry : fileToIdConverter.listMatchingResources(resourceManager).entrySet()) {
			String path = entry.getKey().getPath();
			String ext = path.substring(path.length() - 5);
			ResourceLocation id = fileToIdConverter.fileToId(entry.getKey());
			try (BufferedReader reader = entry.getValue().openAsReader()) {
				DataResult<T> result;
				if (ext.equals(".json")) {
					result = codec.parse(JsonOps.INSTANCE, GSON.fromJson(reader, JsonObject.class));
				} else if (ext.equals(".yaml")) {
					result = codec.parse(JavaOps.INSTANCE, YAML.loadAs(reader, SortedMap.class));
				} else {
					throw new IllegalStateException("Unknown extension: " + ext);
				}
//				XKDeco.LOGGER.info(entry.getKey() + " " + json);
				if (result.error().isPresent()) {
					Kiwi.LOGGER.error("Failed to parse " + id + ": " + result.error().get());
					continue;
				}
				results.put(id, result.result().orElseThrow());
			} catch (Exception e) {
				throw new IllegalStateException("Failed to load " + id, e);
			}
		}
		return results;
	}
}
