package org.teacon.xkdeco.data;

import java.io.IOException;
import java.nio.file.Path;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import snownee.kiwi.datagen.KiwiLanguageProvider;

public class XKDLanguageProvider extends KiwiLanguageProvider {
	//TODO zh_cn handling
	public XKDLanguageProvider(FabricDataOutput dataOutput, String languageCode) {
		super(dataOutput, languageCode);
	}

	@Override
	public void generateTranslations(FabricLanguageProvider.TranslationBuilder translationBuilder) {
		Path path = createPath(languageCode + ".kiwi");
		try {
			translationBuilder.add(path);
		} catch (IOException e) {
			throw new RuntimeException("Failed to write to " + path, e);
		}
	}
}
