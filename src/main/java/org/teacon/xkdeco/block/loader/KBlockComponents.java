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
	public static final KiwiGO<KBlockComponent.Type<DirectionalComponent>> DIRECTIONAL = register(Codec.unit(DirectionalComponent.getInstance()));

	public static final KiwiGO<KBlockComponent.Type<HorizontalComponent>> HORIZONTAL = register(Codec.unit(HorizontalComponent.getInstance()));

	public static final KiwiGO<KBlockComponent.Type<HorizontalAxisComponent>> HORIZONTAL_AXIS = register(Codec.unit(HorizontalAxisComponent.getInstance()));

	public static final KiwiGO<KBlockComponent.Type<FrontAndTopComponent>> FRONT_AND_TOP = register(Codec.unit(FrontAndTopComponent.getInstance()));

	public static final KiwiGO<KBlockComponent.Type<MouldingComponent>> MOULDING = register(Codec.unit(MouldingComponent.getInstance()));

	public static final KiwiGO<KBlockComponent.Type<WaterLoggableComponent>> WATER_LOGGABLE = register(Codec.unit(WaterLoggableComponent.getInstance()));

	public static final KiwiGO<KBlockComponent.Type<ConsumableComponent>> CONSUMABLE = register(ConsumableComponent.CODEC);

	public static final KiwiGO<KBlockComponent.Type<StackableComponent>> STACKABLE = register(StackableComponent.CODEC);

	public static final KiwiGO<KBlockComponent.Type<CycleVariantsComponent>> CYCLE_VARIANTS = register(CycleVariantsComponent.CODEC);

	private static <T extends KBlockComponent> KiwiGO<KBlockComponent.Type<T>> register(Codec<T> codec) {
		return go(() -> new KBlockComponent.Type<>(codec));
	}
}
