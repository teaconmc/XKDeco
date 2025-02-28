package org.teacon.xkdeco.recipe;

import java.util.List;

import org.teacon.xkdeco.block.MimicWallBlock;
import org.teacon.xkdeco.init.MimicWallsLoader;
import org.teacon.xkdeco.init.XKDecoRecipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.block.Block;
import snownee.kiwi.util.NotNullByDefault;

@NotNullByDefault
public class MimicWallRecipe extends ShapelessRecipe {
	private final Ingredient another;

	public MimicWallRecipe(String group, CraftingBookCategory category, Ingredient another) {
		super(group, category, ItemStack.EMPTY, NonNullList.copyOf(List.of(makeWallIngredient(), another)));
		this.another = another;
	}

	public static Ingredient makeWallIngredient() {
		return Ingredient.of(MimicWallsLoader.MIMIC_WALLS.stream()
				.map(MimicWallBlock::getWallDelegate)
				.map(Block::asItem)
				.toArray(Item[]::new));
	}

	@Override
	public boolean isSpecial() {
		return true;
	}

	@Override
	public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
		for (ItemStack itemStack : input.items()) {
			if (another.test(itemStack)) {
				continue;
			}
			for (MimicWallBlock mimicWall : MimicWallsLoader.MIMIC_WALLS) {
				if (itemStack.is(mimicWall.getWallDelegate().asItem())) {
					return new ItemStack(mimicWall);
				}
			}
		}
		return ItemStack.EMPTY;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return XKDecoRecipes.MIMIC_WALL.get();
	}

	public static class Serializer implements RecipeSerializer<MimicWallRecipe> {
		private static final MapCodec<MimicWallRecipe> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
				Codec.STRING.optionalFieldOf("group", "").forGetter(MimicWallRecipe::getGroup),
				CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(MimicWallRecipe::category),
				Ingredient.CODEC_NONEMPTY.fieldOf("another").forGetter($ -> $.another)).apply(i, MimicWallRecipe::new));

		public static final StreamCodec<RegistryFriendlyByteBuf, MimicWallRecipe> STREAM_CODEC = StreamCodec.composite(
				ByteBufCodecs.STRING_UTF8,
				MimicWallRecipe::getGroup,
				CraftingBookCategory.STREAM_CODEC,
				MimicWallRecipe::category,
				Ingredient.CONTENTS_STREAM_CODEC,
				$ -> $.another,
				MimicWallRecipe::new);

		@Override
		public MapCodec<MimicWallRecipe> codec() {
			return CODEC;
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, MimicWallRecipe> streamCodec() {
			return STREAM_CODEC;
		}
	}
}
