package org.teacon.xkdeco.data;

import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.teacon.xkdeco.XKDeco;
import org.teacon.xkdeco.init.XKDecoObjects;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Consumer;

public class XKDecoRecipeProvider extends RecipeProvider {
    private static final Logger LOGGER = LogManager.getLogger("XKDeco");
    private static final HashMap<String, Item> ITEMS = new HashMap<>();
    private static final HashMap<String, Builder> BUILDER = new HashMap<>();
    private static final Collection<String> SUFFIX = List.of(
            "s",
            "_wood",
            "_block"
    );

    static {
        BUILDER.put("_stairs", XKDecoRecipeProvider::stairBuilder);
        BUILDER.put("_slab", XKDecoRecipeProvider::slabBuilder);
        BUILDER.put("_small_eave", XKDecoRecipeProvider::eaveBuilder);
        BUILDER.put("_small_end", XKDecoRecipeProvider::eaveBuilder);
        BUILDER.put("_eave", XKDecoRecipeProvider::eaveBuilder);
        BUILDER.put("_flat", XKDecoRecipeProvider::eaveBuilder);
        BUILDER.put("_ridge", XKDecoRecipeProvider::eaveBuilder);
        BUILDER.put("_deco", XKDecoRecipeProvider::eaveBuilder);
        BUILDER.put("_end", XKDecoRecipeProvider::eaveBuilder);
        BUILDER.put("_tip", XKDecoRecipeProvider::eaveBuilder);
        BUILDER.put("_lamp", XKDecoRecipeProvider::deBuilder);
        BUILDER.put("_pavement", XKDecoRecipeProvider::deBuilder);
        BUILDER.put("_pillar", XKDecoRecipeProvider::deBuilder);
    }

    public XKDecoRecipeProvider(DataGenerator pGenerator) {
        super(pGenerator);
        for (var entry : XKDecoObjects.ITEMS.getEntries()) {
            ITEMS.put(entry.getId().getPath(), entry.get());
        }
    }

    public static void register(GatherDataEvent event) {
        var generator = event.getGenerator();
        generator.addProvider(new XKDecoRecipeProvider(generator));
    }

    protected void buildCraftingRecipes(@Nonnull Consumer<FinishedRecipe> consumer) {
        final int[] i = {0, 0};
        for (var entry : XKDecoObjects.ITEMS.getEntries()) {
            i[0]++;
            var item = entry.get();
            var id = entry.getId().getPath();
            var material = getMaterial(id);
            if (material == null && (id.endsWith("_stairs") || id.endsWith("_slab")))
                LOGGER.info("没有找到与%s相关的材料".formatted(id));
            if (material != null) {
                BUILDER.forEach((suffix, builder) -> {
                    if (id.endsWith(suffix)) {
                        i[1]++;
                        builder.build(consumer, item, material);
                    }
                });
            }
        }
        LOGGER.warn("共有%s项，生成了%s项".formatted(i[0], i[1]));
    }

    protected Item getMaterial(String id) {
        for (var suffix : BUILDER.keySet()) {
            if (id.endsWith(suffix)) {
                id = id.replace(suffix, "");
                break;
            }
        }
        var item = ITEMS.get(id);
        if (item == null) for (var suffix : SUFFIX) {
            item = ITEMS.get(id + suffix);
            if (item != null) break;
        }
        if (item == null)
            item = Registry.ITEM.get(new ResourceLocation("minecraft", id));
        if (item == Items.AIR) item = null;
        return item;
    }

    private static void stairBuilder(@Nonnull Consumer<FinishedRecipe> consumer, ItemLike stairs, ItemLike material) {
        stonecutterResultFromBase(consumer, stairs, material, 1);
        ShapedRecipeBuilder.shaped(stairs, 4).define('#', material).pattern("#  ").pattern("## ").pattern("###").unlockedBy(getHasName(material), has(material)).save(consumer);
    }

    private static void slabBuilder(Consumer<FinishedRecipe> consumer, ItemLike slab, ItemLike material) {
        stonecutterResultFromBase(consumer, slab, material, 2);
        ShapedRecipeBuilder.shaped(slab, 6).define('#', material).pattern("###").unlockedBy(getHasName(material), has(material)).save(consumer);
    }

    private static void eaveBuilder(Consumer<FinishedRecipe> consumer, ItemLike eave, ItemLike material) {
        stonecutterResultFromBase(consumer, eave, material, 2);
    }

    private static void deBuilder(Consumer<FinishedRecipe> consumer, ItemLike eave, ItemLike material) {
    }

    private static void stonecutterResultFromBase(Consumer<FinishedRecipe> consumer, ItemLike result, ItemLike material, int resultCount) {
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(material), result, resultCount).unlockedBy(getHasName(material), has(material)).save(consumer, getConversionRecipeName(result, material) + "_stonecutting");
    }

    private static String getHasName(ItemLike item) {
        return "has_" + getItemName(item);
    }

    private static String getItemName(ItemLike item) {
        return Objects.requireNonNull(item.asItem().getRegistryName()).getPath();
    }

    private static ResourceLocation getConversionRecipeName(ItemLike result, ItemLike material) {
        return new ResourceLocation(XKDeco.ID, getItemName(result) + "_from_" + getItemName(material));
    }

    @FunctionalInterface
    interface Builder {
        void build(@Nonnull Consumer<FinishedRecipe> consumer, ItemLike stairs, ItemLike material);
    }
}
