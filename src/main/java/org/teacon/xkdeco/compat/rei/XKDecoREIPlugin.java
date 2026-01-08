package org.teacon.xkdeco.compat.rei;

import java.util.List;

import org.teacon.xkdeco.XKDeco;
import org.teacon.xkdeco.init.MimicWallsLoader;

import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.entry.CollapsibleEntryRegistry;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class XKDecoREIPlugin implements REIClientPlugin {
	@SuppressWarnings("UnstableApiUsage")
	@Override
	public void registerCollapsibleEntries(CollapsibleEntryRegistry registry) {
		List<EntryStack<ItemStack>> list = MimicWallsLoader.mimicWalls().stream().map(EntryStacks::of).toList();
		registry.group(XKDeco.id("mimic_wall"), Component.translatable("xkdeco.mimic_walls"), list);
	}
}
