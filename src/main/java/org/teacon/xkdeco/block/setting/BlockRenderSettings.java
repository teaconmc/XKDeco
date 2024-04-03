package org.teacon.xkdeco.block.setting;

import java.util.List;
import java.util.Map;

import org.teacon.xkdeco.XKDeco;
import org.teacon.xkdeco.block.loader.BlockDefinitionProperties;
import org.teacon.xkdeco.block.loader.KBlockDefinition;
import org.teacon.xkdeco.util.ClientProxy;
import org.teacon.xkdeco.util.ColorProviderUtil;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public interface BlockRenderSettings {
	static void init(Map<ResourceLocation, KBlockDefinition> blocks, boolean canSetRenderLayer) {
		Map<Block, BlockColor> blockColors = Maps.newHashMap();
		Map<Item, ItemColor> itemColors = Maps.newHashMap();
		List<Pair<Block, BlockColor>> blocksToAdd = Lists.newArrayList();
		List<Pair<Item, ItemColor>> itemsToAdd = Lists.newArrayList();
		for (var entry : blocks.entrySet()) {
			BlockDefinitionProperties properties = entry.getValue().properties();
			if (canSetRenderLayer) {
				var renderType = properties.renderType().orElse(null);
				if (renderType == null) {
					renderType = properties.glassType().map(GlassType::renderType).orElse(null);
				}
				if (renderType != null) {
					Block block = BuiltInRegistries.BLOCK.get(entry.getKey());
					ItemBlockRenderTypes.setRenderLayer(block, (RenderType) renderType.value);
				}
			}
			if (properties.colorProvider().isPresent()) {
				Block block = BuiltInRegistries.BLOCK.get(entry.getKey());
				Block providerBlock = BuiltInRegistries.BLOCK.get(properties.colorProvider().get());
				if (providerBlock == Blocks.AIR) {
					XKDeco.LOGGER.warn("Cannot find color provider block %s for block %s".formatted(
							properties.colorProvider().get(),
							entry.getKey()));
				} else {
					blocksToAdd.add(Pair.of(block, blockColors.computeIfAbsent(providerBlock, ColorProviderUtil::delegate)));
				}
				Item item = block.asItem();
				Item providerItem = providerBlock.asItem();
				if (item != Items.AIR && providerItem != Items.AIR) {
					itemsToAdd.add(Pair.of(item, itemColors.computeIfAbsent(providerItem, ColorProviderUtil::delegate)));
				}
			}
		}
		ClientProxy.registerColors(blocksToAdd, itemsToAdd);
	}
}
