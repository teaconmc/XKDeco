package org.teacon.xkdeco.data;

import java.util.concurrent.CompletableFuture;

import org.teacon.xkdeco.XKDeco;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;

public class XKDBlockTagsProvider extends FabricTagProvider.BlockTagProvider {
	public XKDBlockTagsProvider(
			FabricDataOutput output,
			CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(output, registriesFuture);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		getOrCreateRawBuilder(BlockTags.CLIMBABLE)
				.addElement(XKDeco.id("steel_ladder"))
				.addElement(XKDeco.id("steel_safety_ladder"))
				.addElement(XKDeco.id("air_duct"));
	}
}
