package org.teacon.xkdeco.data;

import java.util.concurrent.CompletableFuture;

import org.teacon.xkdeco.XKDeco;
import org.teacon.xkdeco.block.RoofRidgeBlock;
import org.teacon.xkdeco.block.XKDBlock;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagBuilder;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import snownee.kiwi.util.GameObjectLookup;

public class XKDBlockTagsProvider extends BlockTagsProvider {

	public XKDBlockTagsProvider(
			PackOutput output,
			CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(output, registriesFuture, XKDeco.ID);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		getOrCreateRawBuilder(BlockTags.CLIMBABLE)
				.addElement(XKDeco.id("steel_ladder"))
				.addElement(XKDeco.id("steel_safety_ladder"))
				.addElement(XKDeco.id("air_duct"));

		TagBuilder tagBuilder = getOrCreateRawBuilder(XKDBlock.NON_DIAGONAL_WALLS);
		GameObjectLookup.allHolders(provider, Registries.BLOCK, XKDeco.ID)
				.filter(holder -> holder.value() instanceof RoofRidgeBlock)
				.forEach(holder -> tagBuilder.addElement(holder.key().identifier()));
	}
}
