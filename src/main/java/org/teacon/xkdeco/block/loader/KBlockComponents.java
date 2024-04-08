package org.teacon.xkdeco.block.loader;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.teacon.xkdeco.block.setting.ConsumableComponent;
import org.teacon.xkdeco.block.setting.CycleVariantsComponent;
import org.teacon.xkdeco.block.setting.DirectionalComponent;
import org.teacon.xkdeco.block.setting.FrontAndTopComponent;
import org.teacon.xkdeco.block.setting.HorizontalAxisComponent;
import org.teacon.xkdeco.block.setting.HorizontalComponent;
import org.teacon.xkdeco.block.setting.KBlockComponent;
import org.teacon.xkdeco.block.setting.MouldingComponent;
import org.teacon.xkdeco.block.setting.SimplePropertiesComponent;
import org.teacon.xkdeco.block.setting.StackableComponent;
import org.teacon.xkdeco.block.setting.WaterLoggableComponent;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;

import snownee.kiwi.AbstractModule;
import snownee.kiwi.KiwiGO;
import snownee.kiwi.KiwiModule;

@KiwiModule("block_components")
public class KBlockComponents extends AbstractModule {
	@KiwiModule.Name("minecraft:directional")
	public static final KiwiGO<KBlockComponent.Type<DirectionalComponent>> DIRECTIONAL = register(DirectionalComponent.CODEC);
	@KiwiModule.Name("minecraft:horizontal")
	public static final KiwiGO<KBlockComponent.Type<HorizontalComponent>> HORIZONTAL = register(HorizontalComponent.CODEC);
	@KiwiModule.Name("minecraft:horizontal_axis")
	public static final KiwiGO<KBlockComponent.Type<HorizontalAxisComponent>> HORIZONTAL_AXIS = register(HorizontalAxisComponent.CODEC);
	@KiwiModule.Name("minecraft:front_and_top")
	public static final KiwiGO<KBlockComponent.Type<FrontAndTopComponent>> FRONT_AND_TOP = register(Codec.unit(FrontAndTopComponent.getInstance()));
	@KiwiModule.Name("minecraft:moulding")
	public static final KiwiGO<KBlockComponent.Type<MouldingComponent>> MOULDING = register(Codec.unit(MouldingComponent.getInstance()));
	@KiwiModule.Name("minecraft:water_loggable")
	public static final KiwiGO<KBlockComponent.Type<WaterLoggableComponent>> WATER_LOGGABLE = register(Codec.unit(WaterLoggableComponent.getInstance()));
	@KiwiModule.Name("minecraft:consumable")
	public static final KiwiGO<KBlockComponent.Type<ConsumableComponent>> CONSUMABLE = register(ConsumableComponent.CODEC);
	@KiwiModule.Name("minecraft:stackable")
	public static final KiwiGO<KBlockComponent.Type<StackableComponent>> STACKABLE = register(StackableComponent.CODEC);
	@KiwiModule.Name("minecraft:cycle_variants")
	public static final KiwiGO<KBlockComponent.Type<CycleVariantsComponent>> CYCLE_VARIANTS = register(CycleVariantsComponent.CODEC);
	@KiwiModule.Name("minecraft:simple")
	public static final KiwiGO<KBlockComponent.Type<SimplePropertiesComponent>> SIMPLE_PROPERTIES = register(SimplePropertiesComponent.CODEC);
	private static Map<KBlockComponent.Type<?>, KBlockComponent> SIMPLE_INSTANCES;

	private static <T extends KBlockComponent> KiwiGO<KBlockComponent.Type<T>> register(Codec<T> codec) {
		return go(() -> new KBlockComponent.Type<>(codec));
	}

	public static KBlockComponent getSimpleInstance(KBlockComponent.Type<?> type) {
		if (SIMPLE_INSTANCES == null) {
			ImmutableMap.Builder<KBlockComponent.Type<?>, KBlockComponent> builder = ImmutableMap.builder();
			JsonObject json = new JsonObject();
			for (KBlockComponent.Type<? extends KBlockComponent> type1 : LoaderExtraRegistries.BLOCK_COMPONENT) {
				Optional<? extends KBlockComponent> component = type1.codec().parse(JsonOps.INSTANCE, json).result();
				component.ifPresent(kBlockComponent -> builder.put(type1, kBlockComponent));
			}
			SIMPLE_INSTANCES = builder.build();
		}
		return Objects.requireNonNull(SIMPLE_INSTANCES.get(type), () -> "No simple instance for " + type);
	}
}
