package org.teacon.xkdeco.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.teacon.xkdeco.XKDeco;
import org.teacon.xkdeco.init.XKDecoProperties;

import java.util.Objects;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = XKDeco.ID)
public final class XKDecoCreativeModTab {

    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, XKDeco.ID);

    public static final RegistryObject<CreativeModeTab> BASIC_TAB = TABS.register("basic", () -> CreativeModeTab.builder()
            .icon(Lazy.of(() -> Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation(XKDeco.ID, "black_tiles"))).getDefaultInstance()))
            .title(Component.translatable("itemGroup.xkdeco_basic"))
            .build());
    public static final RegistryObject<CreativeModeTab> STRUCTURE_TAB = TABS.register("structure", () -> CreativeModeTab.builder()
            .icon(Lazy.of(() -> Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation(XKDeco.ID, "special_wall_minecraft_cobblestone_wall"))).getDefaultInstance()))
            .title(Component.translatable("itemGroup.xkdeco_structure"))
            .build());
    public static final RegistryObject<CreativeModeTab> NATURE_TAB = TABS.register("nature", () -> CreativeModeTab.builder()
            .icon(Lazy.of(() -> Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation(XKDeco.ID, "grass_block_slab"))).getDefaultInstance()))
            .title(Component.translatable("itemGroup.xkdeco_nature"))
            .build());
    public static final RegistryObject<CreativeModeTab> FURNITURE_TAB = TABS.register("furniture", () -> CreativeModeTab.builder()
            .icon(Lazy.of(() -> Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation(XKDeco.ID, "varnished_big_table"))).getDefaultInstance()))
            .title(Component.translatable("itemGroup.xkdeco_furniture"))
            .build());
    public static final RegistryObject<CreativeModeTab> FUNCTIONAL_TAB = TABS.register("functional", () -> CreativeModeTab.builder()
            .icon(Lazy.of(() -> Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation(XKDeco.ID, "tech_item_display"))).getDefaultInstance()))
            .title(Component.translatable("itemGroup.xkdeco_functional"))
            .build());


    @SubscribeEvent
    public static void tabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTab() == BASIC_TAB.get()) {
            for (var regObj : XKDecoProperties.TAB_BASIC_CONTENTS) {
                event.accept(regObj.get());
            }
        } else if (event.getTab() == STRUCTURE_TAB.get()) {
            for (var regObj : XKDecoProperties.TAB_STRUCTURE_CONTENTS) {
                event.accept(regObj.get());
            }
        } else if (event.getTab() == NATURE_TAB.get()) {
            for (var regObj : XKDecoProperties.TAB_NATURE_CONTENTS) {
                event.accept(regObj.get());
            }
        } else if (event.getTab() == FURNITURE_TAB.get()) {
            for (var regObj : XKDecoProperties.TAB_FURNITURE_CONTENTS) {
                event.accept(regObj.get());
            }
        } else if (event.getTab() == FUNCTIONAL_TAB.get()) {
            for (var regObj : XKDecoProperties.TAB_FUNCTIONAL_CONTENTS) {
                event.accept(regObj.get());
            }
        }
    }

}
