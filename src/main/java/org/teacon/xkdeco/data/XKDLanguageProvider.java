package org.teacon.xkdeco.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import snownee.kiwi.datagen.KiwiLanguageProvider;

public class XKDLanguageProvider extends KiwiLanguageProvider {
	public XKDLanguageProvider(FabricDataOutput dataOutput) {
		super(dataOutput);
	}

	@Override
	public void generateTranslations(FabricLanguageProvider.TranslationBuilder translationBuilder) {
	}
}
