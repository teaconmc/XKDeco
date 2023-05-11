package org.teacon.xkdeco.item;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import org.teacon.xkdeco.XKDeco;
import org.teacon.xkdeco.init.XKDecoProperties;

import java.util.Objects;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = XKDeco.ID)
public final class XKDecoCreativeModTab {

    @SubscribeEvent
    public static void tabs(CreativeModeTabEvent.Register event) {
        event.registerCreativeModeTab(new ResourceLocation(XKDeco.ID, "basic"), builder -> builder
                .icon(Lazy.of(() -> Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation(XKDeco.ID, "black_tiles"))).getDefaultInstance()))
                .title(Component.translatable("itemGroup.xkdeco_basic"))
                .displayItems((param, output) -> {
                    for (var regObj : XKDecoProperties.TAB_BASIC_CONTENTS) {
                        output.accept(regObj.get());
                    }
                })
        );
        event.registerCreativeModeTab(new ResourceLocation(XKDeco.ID, "structure"), builder -> builder
                .icon(Lazy.of(() -> Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation(XKDeco.ID, "special_wall_minecraft_cobblestone_wall"))).getDefaultInstance()))
                .title(Component.translatable("itemGroup.xkdeco_structure"))
                .displayItems((param, output) -> {
                    for (var regObj : XKDecoProperties.TAB_STRUCTURE_CONTENTS) {
                        output.accept(regObj.get());
                    }
                })
        );
        event.registerCreativeModeTab(new ResourceLocation(XKDeco.ID, "nature"), builder -> builder
                .icon(Lazy.of(() -> Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation(XKDeco.ID, "grass_block_slab"))).getDefaultInstance()))
                .title(Component.translatable("itemGroup.xkdeco_nature"))
                .displayItems((param, output) -> {
                    for (var regObj : XKDecoProperties.TAB_NATURE_CONTENTS) {
                        output.accept(regObj.get());
                    }
                })
        );
        event.registerCreativeModeTab(new ResourceLocation(XKDeco.ID, "furniture"), builder -> builder
                .icon(Lazy.of(() -> Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation(XKDeco.ID, "varnished_big_table"))).getDefaultInstance()))
                .title(Component.translatable("itemGroup.xkdeco_furniture"))
                .displayItems((param, output) -> {
                    for (var regObj : XKDecoProperties.TAB_FURNITURE_CONTENTS) {
                        output.accept(regObj.get());
                    }
                })
        );
        event.registerCreativeModeTab(new ResourceLocation(XKDeco.ID, "functional"), builder -> builder
                .icon(Lazy.of(() -> Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation(XKDeco.ID, "tech_item_display"))).getDefaultInstance()))
                .title(Component.translatable("itemGroup.xkdeco_functional"))
                .displayItems((param, output) -> {
                    for (var regObj : XKDecoProperties.TAB_FUNCTIONAL_CONTENTS) {
                        output.accept(regObj.get());
                    }
                })
        );
    }

}
