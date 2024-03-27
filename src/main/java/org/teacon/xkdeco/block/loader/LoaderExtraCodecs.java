package org.teacon.xkdeco.block.loader;

import java.util.Locale;
import java.util.Map;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import snownee.kiwi.KiwiModule;

public class LoaderExtraCodecs {
	public static final BiMap<ResourceLocation, SoundType> SOUND_TYPES = HashBiMap.create();
	public static final Codec<SoundType> SOUND_TYPE_CODEC = simpleByNameCodec(SOUND_TYPES);
	public static final BiMap<ResourceLocation, NoteBlockInstrument> INSTRUMENTS = HashBiMap.create();
	public static final Codec<NoteBlockInstrument> INSTRUMENT_CODEC = simpleByNameCodec(INSTRUMENTS);
	public static final BiMap<ResourceLocation, MapColor> MAP_COLORS = HashBiMap.create();
	public static final Codec<MapColor> MAP_COLOR_CODEC = simpleByNameCodec(MAP_COLORS);
	public static final Codec<KiwiModule.RenderLayer.Layer> RENDER_TYPE = ExtraCodecs.stringResolverCodec(e -> {
		return e.name().toLowerCase(Locale.ENGLISH);
	}, s -> {
		return KiwiModule.RenderLayer.Layer.valueOf(s.toUpperCase(Locale.ENGLISH));
	});

	static {
		SOUND_TYPES.put(new ResourceLocation("empty"), SoundType.EMPTY);
		SOUND_TYPES.put(new ResourceLocation("copper"), SoundType.COPPER);
		SOUND_TYPES.put(new ResourceLocation("crop"), SoundType.CROP);
		SOUND_TYPES.put(new ResourceLocation("glass"), SoundType.GLASS);
		SOUND_TYPES.put(new ResourceLocation("gravel"), SoundType.GRAVEL);
		SOUND_TYPES.put(new ResourceLocation("ladder"), SoundType.LADDER);
		SOUND_TYPES.put(new ResourceLocation("metal"), SoundType.METAL);
		SOUND_TYPES.put(new ResourceLocation("sand"), SoundType.SAND);
		SOUND_TYPES.put(new ResourceLocation("snow"), SoundType.SNOW);
		SOUND_TYPES.put(new ResourceLocation("stone"), SoundType.STONE);
		SOUND_TYPES.put(new ResourceLocation("wood"), SoundType.WOOD);

		INSTRUMENTS.put(new ResourceLocation(NoteBlockInstrument.HARP.getSerializedName()), NoteBlockInstrument.HARP);
		INSTRUMENTS.put(new ResourceLocation(NoteBlockInstrument.BASS.getSerializedName()), NoteBlockInstrument.BASS);
		INSTRUMENTS.put(new ResourceLocation(NoteBlockInstrument.BASEDRUM.getSerializedName()), NoteBlockInstrument.BASEDRUM);
		INSTRUMENTS.put(new ResourceLocation(NoteBlockInstrument.HAT.getSerializedName()), NoteBlockInstrument.HAT);

		MAP_COLORS.put(new ResourceLocation("none"), MapColor.NONE);
		MAP_COLORS.put(new ResourceLocation("metal"), MapColor.METAL);
		MAP_COLORS.put(new ResourceLocation("grass"), MapColor.GRASS);
		MAP_COLORS.put(new ResourceLocation("sand"), MapColor.SAND);
		MAP_COLORS.put(new ResourceLocation("wool"), MapColor.WOOL);
		MAP_COLORS.put(new ResourceLocation("ice"), MapColor.ICE);
		MAP_COLORS.put(new ResourceLocation("plant"), MapColor.PLANT);
		MAP_COLORS.put(new ResourceLocation("quartz"), MapColor.QUARTZ);
		MAP_COLORS.put(new ResourceLocation("snow"), MapColor.SNOW);
		MAP_COLORS.put(new ResourceLocation("clay"), MapColor.CLAY);
		MAP_COLORS.put(new ResourceLocation("dirt"), MapColor.DIRT);
		MAP_COLORS.put(new ResourceLocation("stone"), MapColor.STONE);
	}

	public static <T> Codec<T> simpleByNameCodec(Map<ResourceLocation, T> map) {
		return ResourceLocation.CODEC.flatXmap(key -> {
			T value = map.get(key);
			if (value == null) {
				return DataResult.error(() -> "Unknown key: " + key);
			}
			return DataResult.success(value);
		}, value -> {
			return DataResult.error(() -> "Unsupported operation");
		});
	}

	public static <T> Codec<T> simpleByNameCodec(BiMap<ResourceLocation, T> map) {
		return ResourceLocation.CODEC.flatXmap(key -> {
			T value = map.get(key);
			if (value == null) {
				return DataResult.error(() -> "Unknown key: " + key);
			}
			return DataResult.success(value);
		}, value -> {
			ResourceLocation key = map.inverse().get(value);
			if (key == null) {
				return DataResult.error(() -> "Unknown value: " + value);
			}
			return DataResult.success(key);
		});
	}

	public static <T> Codec<T> withAlternative(Codec<T> codec, Codec<? extends T> codec2) {
		return Codec.either(codec, codec2).xmap(either -> either.map(object -> object, object -> object), Either::left);
	}
}
