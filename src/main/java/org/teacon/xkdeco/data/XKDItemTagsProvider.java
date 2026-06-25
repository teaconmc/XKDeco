package org.teacon.xkdeco.data;

import java.util.concurrent.CompletableFuture;

import org.teacon.xkdeco.XKDeco;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ItemTagsProvider;

public class XKDItemTagsProvider extends ItemTagsProvider {
	public XKDItemTagsProvider(
			PackOutput output,
			CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(output, registriesFuture, XKDeco.ID);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {

	}
}
