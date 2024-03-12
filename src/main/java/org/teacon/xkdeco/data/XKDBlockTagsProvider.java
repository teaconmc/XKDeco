package org.teacon.xkdeco.data;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.teacon.xkdeco.XKDeco;
import org.teacon.xkdeco.block.settings.GlassType;
import org.teacon.xkdeco.block.settings.XKBlockSettings;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.WallBlock;
import snownee.kiwi.datagen.GameObjectLookup;

public class XKDBlockTagsProvider extends FabricTagProvider.BlockTagProvider {
	public XKDBlockTagsProvider(
			FabricDataOutput output,
			CompletableFuture<HolderLookup.Provider> registriesFuture) {
		super(output, registriesFuture);
	}

	@Override
	protected void addTags(HolderLookup.Provider provider) {
		GameObjectLookup.all(Registries.BLOCK, XKDeco.ID).forEach(block -> {
			Optional<XKBlockSettings> settings = Optional.ofNullable(XKBlockSettings.of(block));
			if (block instanceof WallBlock) {
				getOrCreateTagBuilder(BlockTags.WALLS).add(block);
			} else if (block.getClass() == StairBlock.class) {
				getOrCreateTagBuilder(BlockTags.STAIRS).add(block); //TODO BlockTags.WOODEN_STAIRS
			} else if (block instanceof SlabBlock) {
				getOrCreateTagBuilder(BlockTags.SLABS).add(block); //TODO
			} else if (block.getClass() == TrapDoorBlock.class) {
				getOrCreateTagBuilder(BlockTags.TRAPDOORS).add(block); //TODO
			}
			if (settings.map($ -> $.glassType).filter(GlassType::skipRendering).isPresent()) {
				getOrCreateTagBuilder(BlockTags.IMPERMEABLE).add(block);
			}
		});

		getOrCreateTagBuilder(BlockTags.CLIMBABLE).add(XKDeco.id("steel_ladder"));
		getOrCreateTagBuilder(BlockTags.CLIMBABLE).add(XKDeco.id("steel_safety_ladder"));
	}
}
