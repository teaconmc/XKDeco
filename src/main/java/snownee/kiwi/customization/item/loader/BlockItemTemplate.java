package snownee.kiwi.customization.item.loader;

import java.util.Optional;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BedItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DoubleHighBlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ScaffoldingBlockItem;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.ScaffoldingBlock;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public final class BlockItemTemplate extends KItemTemplate {
	private final Optional<ResourceLocation> block;

	public BlockItemTemplate(Optional<ItemDefinitionProperties> properties, Optional<ResourceLocation> block) {
		super(properties);
		this.block = block;
	}

	public static Codec<BlockItemTemplate> directCodec() {
		return RecordCodecBuilder.create(instance -> instance.group(
				ItemDefinitionProperties.mapCodecField().forGetter(BlockItemTemplate::properties),
				ResourceLocation.CODEC.optionalFieldOf("block").forGetter(BlockItemTemplate::block)
		).apply(instance, BlockItemTemplate::new));
	}

	@Override
	public Type<?> type() {
		return KItemTemplates.BLOCK.getOrCreate();
	}

	@Override
	public void resolve(ResourceLocation key) {
	}

	@Override
	public Item createItem(ResourceLocation id, Item.Properties properties, JsonObject json) {
		Block block = BuiltInRegistries.BLOCK.get(this.block.orElse(id));
		Preconditions.checkState(block != Blocks.AIR, "Block %s not found", this.block);
		if (block instanceof DoorBlock || block instanceof DoublePlantBlock) {
			return new DoubleHighBlockItem(block, properties);
		} else if (block instanceof BedBlock) {
			return new BedItem(block, properties);
		} else if (block instanceof ScaffoldingBlock) {
			return new ScaffoldingBlockItem(block, properties);
		}
		return new BlockItem(block, properties);
	}

	public Optional<ResourceLocation> block() {
		return block;
	}

	@Override
	public String toString() {
		return "BlockItemTemplate[" + "properties=" + properties + ", " + "block=" + block + ']';
	}

}
