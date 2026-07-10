package org.teacon.xkdeco.util;

import java.util.List;
import java.util.Set;

import org.teacon.xkdeco.data.XKDBlockLootTableProvider;
import org.teacon.xkdeco.data.XKDBlockTagsProvider;
import org.teacon.xkdeco.data.XKDItemTagsProvider;
import org.teacon.xkdeco.data.XKDLanguageProvider;
import org.teacon.xkdeco.data.XKDModelProvider;
import org.teacon.xkdeco.data.XKDRecipeProvider;

import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.data.event.GatherDataEvent;

public final class ForgeXKDDataGen {
	public static void init(IEventBus modEventBus) {
		modEventBus.addListener(ForgeXKDDataGen::gather);
	}

	private static void gather(GatherDataEvent.Client event) {
		event.createProvider(XKDLanguageProvider::new);
		event.createProvider(XKDModelProvider::new);
		event.createProvider(XKDBlockTagsProvider::new);
		event.createProvider(XKDItemTagsProvider::new);
		event.createProvider(XKDRecipeProvider.Runner::new);
		event.addProvider(new LootTableProvider(
				event.getGenerator().getPackOutput(),
				Set.of(),
				List.of(new LootTableProvider.SubProviderEntry(
						XKDBlockLootTableProvider::new,
						LootContextParamSets.BLOCK)),
				event.getLookupProvider()));
	}
}
