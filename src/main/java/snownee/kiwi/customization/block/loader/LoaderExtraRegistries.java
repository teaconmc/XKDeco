package snownee.kiwi.customization.block.loader;

import snownee.kiwi.customization.block.component.KBlockComponent;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class LoaderExtraRegistries {
	public static final ResourceKey<Registry<KBlockComponent.Type<?>>> BLOCK_COMPONENT_KEY = ResourceKey.createRegistryKey(new ResourceLocation(
			"kiwi:block_component"));
	public static Registry<KBlockComponent.Type<?>> BLOCK_COMPONENT;
	public static final ResourceKey<Registry<KBlockTemplate.Type<?>>> BLOCK_TEMPLATE_KEY = ResourceKey.createRegistryKey(new ResourceLocation(
			"kiwi:block_template"));
	public static Registry<KBlockTemplate.Type<?>> BLOCK_TEMPLATE;
}
