package snownee.kiwi.customization.block.family;

import java.util.List;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

public class BlockFamily {
	public static final Codec<BlockFamily> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ExtraCodecs.nonEmptyList(BuiltInRegistries.BLOCK.holderByNameCodec().listOf()).fieldOf("values").forGetter(BlockFamily::blocks),
			Codec.BOOL.optionalFieldOf("stonecutter_exchange", false).forGetter(BlockFamily::stonecutterExchange),
			BuiltInRegistries.ITEM.byNameCodec().optionalFieldOf("stonecutter_from", Items.AIR).forGetter(BlockFamily::stonecutterFrom)
	).apply(instance, BlockFamily::new));

	private final List<Holder<Block>> blocks;
	private final boolean stonecutterExchange;
	private final Item stonecutterFrom;

	public BlockFamily(List<Holder<Block>> blocks, boolean stonecutterExchange, Item stonecutterFrom) {
		this.blocks = blocks;
		this.stonecutterExchange = stonecutterExchange;
		this.stonecutterFrom = stonecutterFrom;
		Preconditions.checkArgument(
				blocks.stream().map(Holder::value).distinct().count() == blocks.size(),
				"Duplicate blocks found in family %s",
				this);
	}

	public List<Holder<Block>> blocks() {
		return blocks;
	}

	public boolean stonecutterExchange() {
		return stonecutterExchange;
	}

	public Item stonecutterFrom() {
		return stonecutterFrom;
	}

	public Ingredient stonecutterFromIngredient() {
		return stonecutterFrom == Items.AIR ? Ingredient.EMPTY : Ingredient.of(stonecutterFrom);
	}
}
