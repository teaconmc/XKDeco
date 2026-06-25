package org.teacon.xkdeco.util;

import java.util.List;
import java.util.Set;

import org.teacon.xkdeco.XKDeco;
import org.teacon.xkdeco.data.XKDBlockLootTableProvider;
import org.teacon.xkdeco.data.XKDBlockTagsProvider;
import org.teacon.xkdeco.data.XKDItemTagsProvider;
import org.teacon.xkdeco.data.XKDModelProvider;
import org.teacon.xkdeco.data.XKDRecipeProvider;

import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import snownee.kiwi.datagen.KiwiLanguageProvider;

/**
 * Native NeoForge data-generation entry (replaces the old Forgified-Fabric-API bridge).
 *
 * <p>In 26.1 GatherDataEvent is split into Client and Server sub-events fired by SEPARATE runs
 * (clientData()/serverData()). The IDE/build "data" run is a clientData() run, so it only fires
 * GatherDataEvent.Client — registering the tag/loot/recipe providers under Server meant they never
 * ran, and the client run then deleted their output as stale. So ALL providers are registered under
 * the Client event; a single {@code runData} regenerates everything (assets + data).
 */
public final class ForgeXKDDataGen {
	public static void init(IEventBus modEventBus) {
		modEventBus.addListener(ForgeXKDDataGen::gather);
	}

	private static void gather(GatherDataEvent.Client event) {
		// Client assets: blockstates + models.
		event.createProvider(XKDModelProvider::new);
		// Block/item display names: Kiwi's base provider auto-generates them from the customization
		// data. Must pass our mod id explicitly — the 2-arg constructor defaults to "kiwi" and would
		// write to assets/kiwi/lang instead of assets/xkdeco/lang. Dropping this is what left most
		// blocks unnamed after the FFAPI->native migration.
		event.createProvider((output, lookup) -> new KiwiLanguageProvider(output, XKDeco.ID, lookup));
		// Server data: tags, recipes, loot. Generated in the same run so nothing is pruned as stale.
		event.createProvider(XKDBlockTagsProvider::new);
		event.createProvider(XKDItemTagsProvider::new);
		event.createProvider(XKDRecipeProvider.Runner::new);
		event.addProvider(new LootTableProvider(
				event.getGenerator().getPackOutput(),
				Set.of(),
				List.of(new LootTableProvider.SubProviderEntry(XKDBlockLootTableProvider::new, LootContextParamSets.BLOCK)),
				event.getLookupProvider()));
	}
}
