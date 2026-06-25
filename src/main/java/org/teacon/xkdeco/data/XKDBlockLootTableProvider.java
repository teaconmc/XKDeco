package org.teacon.xkdeco.data;

import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import org.teacon.xkdeco.XKDeco;
import org.teacon.xkdeco.block.MimicWallBlock;

import net.minecraft.advancements.criterion.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import snownee.kiwi.customization.block.KBlockSettings;
import snownee.kiwi.customization.block.component.LayeredComponent;
import snownee.kiwi.customization.block.loader.KBlockComponents;
import snownee.kiwi.util.GameObjectLookup;

public class XKDBlockLootTableProvider extends net.minecraft.data.loot.BlockLootSubProvider {
	public XKDBlockLootTableProvider(HolderLookup.Provider registries) {
		super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
	}

	private List<Block> knownBlocks() {
		return GameObjectLookup.all(this.registries, Registries.BLOCK, XKDeco.ID)
				.filter($ -> !($ instanceof MimicWallBlock))
				.toList();
	}

	@Override
	protected Iterable<Block> getKnownBlocks() {
		return knownBlocks();
	}

	@Override
	public void generate() {
		for (Block block : knownBlocks()) {
			if (block.asItem() == Items.AIR) {
				add(block, noDrop());
				continue;
			}
			if (block instanceof SlabBlock) {
				add(block, createSlabItemTable(block));
				continue;
			}
			if (block instanceof DoorBlock) {
				add(block, createDoorTable(block));
				continue;
			}
			KBlockSettings settings = KBlockSettings.of(block);
			if (settings != null) {
				if (settings.hasComponent(KBlockComponents.STACKABLE.get())) {
					LayeredComponent layered = (LayeredComponent) settings.components.values()
							.stream()
							.filter($ -> $ instanceof LayeredComponent)
							.findAny()
							.orElseThrow();
					IntegerProperty property = layered.getLayerProperty();
					add(block, $ -> LootTable.lootTable().withPool(LootPool.lootPool()
							.setRolls(ConstantValue.exactly(1))
							.add(applyExplosionDecay($, LootItem.lootTableItem($).apply(
									IntStream.rangeClosed(property.min, property.max).boxed().toList(),
									i -> SetItemCountFunction.setCount(ConstantValue.exactly(i))
											.when(LootItemBlockStatePropertyCondition.hasBlockStateProperties($)
													.setProperties(StatePropertiesPredicate.Builder.properties()
															.hasProperty(property, i))))))));
					continue;
				}
				if (settings.hasComponent(KBlockComponents.CONSUMABLE.get())) {
					LayeredComponent layered = (LayeredComponent) settings.components.values()
							.stream()
							.filter($ -> $ instanceof LayeredComponent)
							.findAny()
							.orElseThrow();
					IntegerProperty property = layered.getLayerProperty();
					add(block, $ -> LootTable.lootTable().withPool(LootPool.lootPool()
							.setRolls(ConstantValue.exactly(1))
							.add(LootItem.lootTableItem(block)).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties($)
									.setProperties(StatePropertiesPredicate.Builder.properties()
											.hasProperty(property, property.max)))));
					continue;
				}
			}
			dropSelf(block);
		}
	}
}
