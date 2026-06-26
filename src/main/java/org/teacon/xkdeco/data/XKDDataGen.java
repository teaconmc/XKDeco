package org.teacon.xkdeco.data;

import org.teacon.xkdeco.XKDeco;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import snownee.kiwi.datagen.KiwiLanguageProvider;

public class XKDDataGen implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator generator) {
		FabricDataGenerator.Pack pack = generator.createPack();
		pack.addProvider((output, registryLookup) -> new KiwiLanguageProvider(output, XKDeco.ID, registryLookup));
		pack.addProvider(XKDItemTagsProvider::new);
		pack.addProvider(XKDBlockTagsProvider::new);
		pack.addProvider(XKDModelProvider::new);
		pack.addProvider(XKDBlockLootTableProvider::new);
		pack.addProvider(XKDRecipeProvider.Runner::new);
	}
}
