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

public class BlockFamily {
	public static final Codec<BlockFamily> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			ExtraCodecs.nonEmptyList(BuiltInRegistries.BLOCK.holderByNameCodec().listOf())
					.fieldOf("values")
					.forGetter(BlockFamily::holders),
			Codec.BOOL.optionalFieldOf("stonecutter_exchange", false).forGetter(BlockFamily::stonecutterExchange),
			BuiltInRegistries.ITEM.byNameCodec().optionalFieldOf("stonecutter_from", Items.AIR).forGetter(BlockFamily::stonecutterSource),
			Codec.BOOL.optionalFieldOf("quick_switch", false).forGetter(BlockFamily::quickSwitch)
	).apply(instance, BlockFamily::new));

	private final List<Holder<Block>> blocks;
	private final boolean stonecutterExchange;
	private final Item stonecutterFrom;
	private final boolean quickSwitch;
	private Ingredient ingredient;

	public BlockFamily(List<Holder<Block>> blocks, boolean stonecutterExchange, Item stonecutterFrom, boolean quickSwitch) {
		this.blocks = blocks;
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

	public Ingredient ingredient() {
		if (ingredient == null) {
			ingredient = Ingredient.of(blocks.stream().map(Holder::value).toArray(ItemLike[]::new));
		}
		return ingredient;
	}

	public boolean contains(ItemLike item) {
		return blocks.stream().anyMatch(h -> h.value().asItem() == item);
	}
}
