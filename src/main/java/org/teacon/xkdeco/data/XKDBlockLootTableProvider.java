package org.teacon.xkdeco.data;

import java.util.stream.IntStream;

import org.teacon.xkdeco.XKDeco;
import snownee.kiwi.customization.block.loader.KBlockComponents;
import snownee.kiwi.customization.block.KBlockSettings;
import snownee.kiwi.customization.block.component.LayeredComponent;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
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
import snownee.kiwi.datagen.GameObjectLookup;

public class XKDBlockLootTableProvider extends FabricBlockLootTableProvider {
	protected XKDBlockLootTableProvider(FabricDataOutput dataOutput) {
		super(dataOutput);
	}

	@Override
	public void generate() {
		for (Block block : GameObjectLookup.all(Registries.BLOCK, XKDeco.ID).toList()) {
			if (map.containsKey(BuiltInRegistries.BLOCK.getKey(block))) {
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
				if (settings.hasComponent(KBlockComponents.CONSUMABLE.get()) || settings.hasComponent(KBlockComponents.STACKABLE.get())) {
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
			}
			dropSelf(block);
		}
	}
}
