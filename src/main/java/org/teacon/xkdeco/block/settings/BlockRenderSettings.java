package org.teacon.xkdeco.block.settings;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import snownee.kiwi.KiwiModule;

public record BlockRenderSettings(KiwiModule.RenderLayer.Layer renderType) {
	private static final List<Pair<Block, RenderType>> RENDER_LAYER = Lists.newArrayList();
	private static final Map<XKBlockSettings, BlockRenderSettings> BUILD_CACHE = Maps.newIdentityHashMap();

	public static void finalizeLoading() {
		for (Pair<Block, RenderType> pair : RENDER_LAYER) {
			ItemBlockRenderTypes.setRenderLayer(pair.getLeft(), pair.getRight());
		}
		RENDER_LAYER.clear();
		BUILD_CACHE.clear();
	}

	public static void onBlockInit(Block block, XKBlockSettings settings) {
		var builder = BUILD_CACHE.get(settings);
		if (builder == null) {
			return;
		}
		if (builder.renderType != null) {
			RENDER_LAYER.add(Pair.of(block, (RenderType) builder.renderType.value));
		}
	}

	public static void putSettings(XKBlockSettings settings, BlockRenderSettings builder) {
		BUILD_CACHE.put(settings, builder);
	}
}
