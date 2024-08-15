package org.teacon.xkdeco.data;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class XKDDataGen implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator generator) {
		FabricDataGenerator.Pack pack = generator.createPack();
		pack.addProvider(XKDLanguageProvider::new);
		pack.addProvider(XKDItemTagsProvider::new);
		pack.addProvider(XKDBlockTagsProvider::new);
		pack.addProvider(XKDModelProvider::new);
		pack.addProvider(XKDBlockLootTableProvider::new);
		pack.addProvider(XKDRecipeProvider::new);
	}
}
