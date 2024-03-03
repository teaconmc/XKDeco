package org.teacon.xkdeco.data;

import static org.teacon.xkdeco.init.XKDecoObjects.BLOCKS;
import static org.teacon.xkdeco.init.XKDecoObjects.ENTITIES;
import static org.teacon.xkdeco.init.XKDecoObjects.ITEMS;
import static org.teacon.xkdeco.init.XKDecoObjects.ROOF_FLAT_SUFFIX;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.teacon.xkdeco.XKDeco;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.data.PackOutput;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public final class XKDecoEnUsLangProvider extends LanguageProvider {
	public XKDecoEnUsLangProvider(PackOutput gen, String modid, String locale) {
		super(gen, modid, locale);
	}

	// most of them have not been added into game yet
	private static final Collection<String> EXTRA_KEYS = List.of(
			"block.xkdeco.gilded_blackstone_pillar",
			"block.xkdeco.blue_roof",
			"block.xkdeco.blue_roof_ridge",
			"block.xkdeco.blue_roof_eave",
			"block.xkdeco.blue_roof_end",
			"block.xkdeco.blue_roof_small_eave",
			"block.xkdeco.blue_roof_small_end",
			"block.xkdeco.blue_roof_deco",
			"block.xkdeco.blue_roof_tip",
			"block.xkdeco.green_roof",
			"block.xkdeco.green_roof_ridge",
			"block.xkdeco.green_roof_eave",
			"block.xkdeco.green_roof_end",
			"block.xkdeco.green_roof_small_eave",
			"block.xkdeco.green_roof_small_end",
			"block.xkdeco.green_roof_deco",
			"block.xkdeco.green_roof_tip",
			"block.xkdeco.red_roof",
			"block.xkdeco.red_roof_ridge",
			"block.xkdeco.red_roof_eave",
			"block.xkdeco.red_roof_end",
			"block.xkdeco.red_roof_small_eave",
			"block.xkdeco.red_roof_small_end",
			"block.xkdeco.red_roof_deco",
			"block.xkdeco.red_roof_tip"
//            "block.xkdeco.ginkgo_leaves_shatter",
//            "block.xkdeco.orange_maple_leaves_shatter",
//            "block.xkdeco.red_maple_leaves_shatter",
//            "block.xkdeco.peach_blossom_shatter",
//            "block.xkdeco.cherry_blossom_shatter",
//            "block.xkdeco.white_cherry_blossom_shatter"
	);

	private static final Map<String, String> EXTRA_ENTRIES = Map.ofEntries(
			Map.entry("block.xkdeco.special_wall", "%s (Column)"),
			Map.entry("block.xkdeco.blue_roof_flat", "Blue Flat Roof"),
			Map.entry("block.xkdeco.green_roof_flat", "Green Flat Roof"),
			Map.entry("block.xkdeco.red_roof_flat", "Red Flat Roof"),
			Map.entry("itemGroup.xkdeco_basic", "XKDeco: Basic"),
			Map.entry("itemGroup.xkdeco_functional", "XKDeco: Functional"),
			Map.entry("itemGroup.xkdeco_furniture", "XKDeco: Furniture"),
			Map.entry("itemGroup.xkdeco_nature", "XKDeco: Nature"),
			Map.entry("itemGroup.xkdeco_structure", "XKDeco: Structure"),
			Map.entry("pack.xkdeco_special_wall", "XKDeco: Special Walls")
	);

	public static void register(GatherDataEvent event) {
		var generator = event.getGenerator();
		generator.addProvider(event.includeClient(), new XKDecoEnUsLangProvider(generator.getPackOutput(), XKDeco.ID, "en_us"));
	}

	@Override
	protected void addTranslations() {
		Stream.<DeferredRegister<?>>of(BLOCKS, ITEMS, ENTITIES)
				.flatMap(deferredRegister -> deferredRegister.getEntries().stream())
				.filter(obj -> !(obj.get() instanceof BlockItem))
				.forEach(this::translate);
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
	private void translate(RegistryObject<?> regObj) {
		var id = regObj.getId().getPath();
		id = id.replace(ROOF_FLAT_SUFFIX, "_flat_roof");
		var translation = snakeToSpace(id);
		var obj = regObj.get();
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
			add("subtitles." + regObj.getId().getNamespace() + "." + regObj.getId().getPath(), translation);
		} else {
			throw new RuntimeException("Unsupported registry object type '" + obj.getClass() + "'");
		}
	}

	// borrowed from mod uusi-aurinko
	private static String snakeToSpace(String str) {
		var chars = str.toCharArray();
		for (var i = 0; i < chars.length; i++) {
			var c = chars[i];
			if (c == '_') {
				chars[i] = ' ';
			}
			if ((i == 0 || chars[i - 1] == ' ') && c >= 'a' && c <= 'z') {
				chars[i] -= 32;
			}
		}
		return String.valueOf(chars);
	}
}
