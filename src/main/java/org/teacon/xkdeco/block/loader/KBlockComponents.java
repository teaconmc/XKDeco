package org.teacon.xkdeco.block.loader;

import org.teacon.xkdeco.block.setting.ConsumableComponent;
import org.teacon.xkdeco.block.setting.CycleVariantsComponent;
import org.teacon.xkdeco.block.setting.DirectionalComponent;
import org.teacon.xkdeco.block.setting.FrontAndTopComponent;
import org.teacon.xkdeco.block.setting.HorizontalAxisComponent;
import org.teacon.xkdeco.block.setting.HorizontalComponent;
import org.teacon.xkdeco.block.setting.KBlockComponent;
import org.teacon.xkdeco.block.setting.MouldingComponent;
import org.teacon.xkdeco.block.setting.StackableComponent;
import org.teacon.xkdeco.block.setting.WaterLoggableComponent;

import com.mojang.serialization.Codec;

import snownee.kiwi.AbstractModule;
import snownee.kiwi.KiwiGO;
import snownee.kiwi.KiwiModule;

@KiwiModule("block_components")
public class KBlockComponents extends AbstractModule {
	@KiwiModule.Name("minecraft:directional")
	public static final KiwiGO<KBlockComponent.Type<DirectionalComponent>> DIRECTIONAL = register(Codec.unit(DirectionalComponent.getInstance()));
	@KiwiModule.Name("minecraft:horizontal")
	public static final KiwiGO<KBlockComponent.Type<HorizontalComponent>> HORIZONTAL = register(Codec.unit(HorizontalComponent.getInstance()));
	@KiwiModule.Name("minecraft:horizontal_axis")
	public static final KiwiGO<KBlockComponent.Type<HorizontalAxisComponent>> HORIZONTAL_AXIS = register(Codec.unit(HorizontalAxisComponent.getInstance()));
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

	private static <T extends KBlockComponent> KiwiGO<KBlockComponent.Type<T>> register(Codec<T> codec) {
		return go(() -> new KBlockComponent.Type<>(codec));
	}
}
