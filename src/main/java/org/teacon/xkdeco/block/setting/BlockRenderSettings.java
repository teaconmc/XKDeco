package org.teacon.xkdeco.block.setting;

import java.util.Map;

import org.teacon.xkdeco.block.loader.KBlockDefinition;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public interface BlockRenderSettings {
	static void init(Map<ResourceLocation, KBlockDefinition> blocks) {
		for (var entry : blocks.entrySet()) {
			KBlockDefinition definition = entry.getValue();
			var renderType = definition.renderType();
			if (renderType == null && definition.settings().glassType != null) {
				renderType = definition.settings().glassType.renderType();
			}
			if (renderType != null) {
				Block block = BuiltInRegistries.BLOCK.get(entry.getKey());
				ItemBlockRenderTypes.setRenderLayer(block, (RenderType) renderType.value);
//				XKDeco.LOGGER.info("Set render renderType for block {} to {}", entry.getKey(), definition.renderType());
			}
		}
	}
}
