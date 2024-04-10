package snownee.kiwi.customization.block.loader;

import net.minecraft.resources.ResourceLocation;

// provide ID information to make us easier when debugging
public record Holder<T>(ResourceLocation key, T value) {
}
