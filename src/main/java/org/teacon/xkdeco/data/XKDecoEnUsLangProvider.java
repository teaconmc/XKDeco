package org.teacon.xkdeco.data;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.data.DataGenerator;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryObject;
import org.teacon.xkdeco.XKDeco;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import static org.teacon.xkdeco.init.XKDecoObjects.*;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public final class XKDecoEnUsLangProvider extends LanguageProvider {
    public XKDecoEnUsLangProvider(DataGenerator gen, String modid, String locale) {
        super(gen, modid, locale);
    }

    // most of them have not been added into game yet
    private static final Collection<String> EXTRA_KEYS = List.of(
            "block.xkdeco.varnished_wardrobe",
            "block.xkdeco.ebony_wardrobe",
            "block.xkdeco.mahogany_wardrobe",
            "block.xkdeco.iron_wardrobe",
            "block.xkdeco.glass_wardrobe",
            "block.xkdeco.full_glass_wardrobe",
            "block.xkdeco.gilded_blackstone_pillar",
            "block.xkdeco.blue_roof",
            "block.xkdeco.blue_roof_ridge",
            "block.xkdeco.blue_roof_flat",
            "block.xkdeco.blue_roof_eave",
            "block.xkdeco.blue_roof_end",
            "block.xkdeco.blue_roof_small_eave",
            "block.xkdeco.blue_roof_small_end",
            "block.xkdeco.blue_roof_deco",
            "block.xkdeco.blue_roof_tip",
            "block.xkdeco.green_roof",
            "block.xkdeco.green_roof_ridge",
            "block.xkdeco.green_roof_flat",
            "block.xkdeco.green_roof_eave",
            "block.xkdeco.green_roof_end",
            "block.xkdeco.green_roof_small_eave",
            "block.xkdeco.green_roof_small_end",
            "block.xkdeco.green_roof_deco",
            "block.xkdeco.green_roof_tip",
            "block.xkdeco.red_roof",
            "block.xkdeco.red_roof_ridge",
            "block.xkdeco.red_roof_flat",
            "block.xkdeco.red_roof_eave",
            "block.xkdeco.red_roof_end",
            "block.xkdeco.red_roof_small_eave",
            "block.xkdeco.red_roof_small_end",
            "block.xkdeco.red_roof_deco",
            "block.xkdeco.red_roof_tip",
            "block.xkdeco.ginkgo_leaves_shatter",
            "block.xkdeco.orange_maple_leaves_shatter",
            "block.xkdeco.red_maple_leaves_shatter",
            "block.xkdeco.peach_blossom_shatter",
            "block.xkdeco.cherry_blossom_shatter",
            "block.xkdeco.white_cherry_blossom_shatter"
    );

    private static final Map<String, String> EXTRA_ENTRIES = Map.ofEntries(
            Map.entry("block.xkdeco.special_wall", "%s (Column)")
    );

    public static void register(GatherDataEvent event) {
        var generator = event.getGenerator();
        generator.addProvider(new XKDecoEnUsLangProvider(generator, XKDeco.ID, "en_us"));
    }

    @Override
    protected void addTranslations() {
        Stream.<DeferredRegister<?>>of(BLOCKS, ITEMS, ENTITIES)
                .flatMap(deferredRegister -> deferredRegister.getEntries().stream())
                .map(RegistryObject::get)
                .filter(obj -> !(obj instanceof BlockItem))
                .forEach(this::translate);
        Stream.of(TAB_BASIC, TAB_FUNCTIONAL, TAB_FURNITURE, TAB_NATURE, TAB_STRUCTURE)
                .map(tab -> ((TranslatableComponent) tab.getDisplayName()).getKey())
                .forEach(this::translateCreativeTab);
        EXTRA_KEYS.forEach(this::translateKey);
        EXTRA_ENTRIES.forEach(this::add);
    }

    private void translateCreativeTab(String key) {
        add(key, "XKDeco: " + snakeToSpace(
                key.substring(key.lastIndexOf('.') + "xkdeco_".length() + 1)
        ));
    }

    private void translateKey(String key) {
        add(key, snakeToSpace(
                key.substring(key.lastIndexOf('.') + 1)
        ));
    }

    // borrowed from mod uusi-aurinko
    private void translate(IForgeRegistryEntry<?> obj) {
        var translation = snakeToSpace(Objects.requireNonNull(obj.getRegistryName()).getPath());
        if (obj instanceof Block) {
            add((Block) obj, translation);
        } else if (obj instanceof Item) {
            add((Item) obj, translation);
        } else if (obj instanceof Enchantment) {
            add((Enchantment) obj, translation);
        } else if (obj instanceof MobEffect) {
            add((MobEffect) obj, translation);
        } else if (obj instanceof EntityType) {
            add((EntityType<?>) obj, translation);
        } else if (obj instanceof SoundEvent) {
            add("subtitles." + obj.getRegistryName().getNamespace() + "." + obj.getRegistryName().getPath(), translation);
        } else {
            throw new RuntimeException("Unsupported registry object type '" + obj.getClass() + "'");
        }
    }

    // borrowed from mod uusi-aurinko
    private static String snakeToSpace(String str) {
        var chars = str.toCharArray();
        for (var i = 0; i < chars.length; i++) {
            var c = chars[i];
            if (c == '_') chars[i] = ' ';
            if ((i == 0 || chars[i - 1] == ' ') && c >= 'a' && c <= 'z') chars[i] -= 32;
        }
        return String.valueOf(chars);
    }
}
