package snownee.kiwi.customization.block.family;

import java.util.List;
import java.util.stream.Stream;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.SlabBlock;
import snownee.kiwi.customization.util.codec.CompactListCodec;

public class BlockFamily {
	public static final Codec<BlockFamily> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ExtraCodecs.nonEmptyList(BuiltInRegistries.BLOCK.holderByNameCodec().listOf())
					.fieldOf("values")
					.forGetter(BlockFamily::holders),
			new CompactListCodec<>(BuiltInRegistries.BLOCK.holderByNameCodec())
					.optionalFieldOf("exchange_inputs_in_viewer", List.of())
					.forGetter(BlockFamily::exchangeInputsInViewer),
			Codec.BOOL.optionalFieldOf("stonecutter_exchange", false).forGetter(BlockFamily::stonecutterExchange),
			BuiltInRegistries.ITEM.byNameCodec().optionalFieldOf("stonecutter_from", Items.AIR).forGetter(BlockFamily::stonecutterSource),
			Codec.BOOL.optionalFieldOf("quick_switch", false).forGetter(BlockFamily::quickSwitch)
	).apply(instance, BlockFamily::new));

	private final List<Holder<Block>> blocks;
	private final List<Holder<Block>> exchangeInputsInViewer;
	private final boolean stonecutterExchange;
	private final Item stonecutterFrom;
	private final boolean quickSwitch;
	private Ingredient ingredient;
	private Ingredient ingredientInViewer;

	public BlockFamily(
			List<Holder<Block>> blocks,
			List<Holder<Block>> exchangeInputsInViewer,
			boolean stonecutterExchange,
			Item stonecutterFrom,
			boolean quickSwitch) {
		this.blocks = blocks;
		this.exchangeInputsInViewer = exchangeInputsInViewer;
		this.stonecutterExchange = stonecutterExchange;
		this.stonecutterFrom = stonecutterFrom;
		this.quickSwitch = quickSwitch;
		Preconditions.checkArgument(
				blocks.stream().map(Holder::value).distinct().count() == blocks.size(),
				"Duplicate blocks found in family %s",
				this);
	}

	public List<Holder<Block>> holders() {
		return blocks;
	}

	public List<Holder<Block>> exchangeInputsInViewer() {
		return exchangeInputsInViewer;
	}

	public Stream<Block> values() {
		return blocks.stream().map(Holder::value);
	}

	public boolean stonecutterExchange() {
		return stonecutterExchange;
	}

	public Item stonecutterSource() {
		return stonecutterFrom;
	}

	public Ingredient stonecutterSourceIngredient() {
		return stonecutterFrom == Items.AIR ? Ingredient.EMPTY : Ingredient.of(stonecutterFrom);
	}

	public Boolean quickSwitch() {
		return quickSwitch;
	}

	protected Ingredient toIngredient(List<Holder<Block>> blocks) {
		return Ingredient.of(blocks.stream().map(Holder::value).filter(block -> {
			if (block instanceof SlabBlock || block instanceof DoorBlock) {
				return false;
			}
			return true;
		}).toArray(ItemLike[]::new));
	}

	public Ingredient ingredient() {
		if (ingredient == null) {
			ingredient = toIngredient(blocks);
		}
		return ingredient;
	}

	public Ingredient ingredientInViewer() {
		if (ingredientInViewer == null) {
			if (exchangeInputsInViewer.isEmpty()) {
				ingredientInViewer = ingredient();
			} else {
				ingredientInViewer = toIngredient(exchangeInputsInViewer);
			}
		}
		return ingredientInViewer;
	}

	public boolean contains(ItemLike item) {
		return blocks.stream().anyMatch(h -> h.value().asItem() == item);
	}
}
