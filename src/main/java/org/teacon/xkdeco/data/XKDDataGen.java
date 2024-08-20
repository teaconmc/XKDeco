package org.teacon.xkdeco.data;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
//import snownee.kiwi.datagen.KiwiLanguageProvider;

public class XKDDataGen implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator generator) {
		FabricDataGenerator.Pack pack = generator.createPack();
		//pack.addProvider((FabricDataOutput output) -> new KiwiLanguageProvider(output));
		pack.addProvider(XKDItemTagsProvider::new);
		pack.addProvider(XKDBlockTagsProvider::new);
		pack.addProvider(XKDModelProvider::new);
		pack.addProvider(XKDBlockLootTableProvider::new);
		pack.addProvider(XKDRecipeProvider::new);
	}
}
