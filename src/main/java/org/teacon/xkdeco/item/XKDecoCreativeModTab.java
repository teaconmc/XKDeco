package org.teacon.xkdeco.item;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.registries.ForgeRegistries;
import org.teacon.xkdeco.XKDeco;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public final class XKDecoCreativeModTab extends CreativeModeTab {
    public static final CreativeModeTab TAB_BASIC = new XKDecoCreativeModTab(XKDeco.ID + "_basic", "black_tiles");
    public static final CreativeModeTab TAB_STRUCTURE = new XKDecoCreativeModTab(XKDeco.ID + "_structure", "special_wall_minecraft_cobblestone_wall");
    public static final CreativeModeTab TAB_NATURE = new XKDecoCreativeModTab(XKDeco.ID + "_nature", "grass_block_slab");
    public static final CreativeModeTab TAB_FURNITURE = new XKDecoCreativeModTab(XKDeco.ID + "_furniture", "varnished_big_table");
    public static final CreativeModeTab TAB_FUNCTIONAL = new XKDecoCreativeModTab(XKDeco.ID + "_functional", "tech_item_display");

    private final String itemId;

    private final Lazy<ItemStack> itemStackLazy;

    private XKDecoCreativeModTab(String label, String itemId) {
        super(label);
        this.itemId = itemId;
        this.itemStackLazy = Lazy.of(this::getItemStack);
    }

    @Override
    public ItemStack makeIcon() {
        return this.itemStackLazy.get();
    }

    private ItemStack getItemStack() {
        var resourceLocation = new ResourceLocation(XKDeco.ID, this.itemId);
        return Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(resourceLocation)).getDefaultInstance();
    }
}
