package org.teacon.xkdeco.util;

import org.teacon.xkdeco.XKDeco;
import org.teacon.xkdeco.data.XKDBlockLootTableProvider;
import org.teacon.xkdeco.data.XKDBlockTagsProvider;
import org.teacon.xkdeco.data.XKDItemTagsProvider;
import org.teacon.xkdeco.data.XKDModelProvider;
import org.teacon.xkdeco.data.XKDRecipeProvider;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.loader.api.FabricLoader;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import snownee.kiwi.datagen.KiwiLanguageProvider;

public class ForgeXKDDataGen {
	public static void init(IEventBus modEventBus) {
		modEventBus.addListener(ForgeXKDDataGen::gather);
	}

	private static void gather(GatherDataEvent.Client event) {
		var modContainer = FabricLoader.getInstance().getModContainer(XKDeco.ID).orElseThrow();
		var output = new FabricPackOutput(
				modContainer,
				event.getGenerator().getPackOutput().getOutputFolder(),
				event.validate());
		var registries = event.getLookupProvider();

		event.addProvider(new KiwiLanguageProvider(output, registries));
		event.addProvider(new XKDItemTagsProvider(output, registries));
		event.addProvider(new XKDBlockTagsProvider(output, registries));
		event.addProvider(new XKDModelProvider(output));
		event.addProvider(new XKDBlockLootTableProvider(output, registries));
		event.addProvider(new XKDRecipeProvider.Runner(output, registries));
	}
}
