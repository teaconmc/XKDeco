package org.teacon.xkdeco.block.loader;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import org.teacon.xkdeco.block.BasicBlock;
import org.teacon.xkdeco.block.setting.KBlockSettings;
import org.teacon.xkdeco.duck.KBlockProperties;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.SandBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;

public class BlockCodecs {
	private static final Map<ResourceLocation, MapCodec<Block>> CODECS = Maps.newHashMap();

	public static final String BLOCK_PROPERTIES_KEY = "properties";
	private static final Codec<BlockBehaviour.Properties> BLOCK_PROPERTIES = new InjectedBlockPropertiesCodec(Codec.unit(BlockBehaviour.Properties::of));

	public static final Codec<BlockSetType> BLOCK_SET_TYPE = ExtraCodecs.stringResolverCodec(
			BlockSetType::name,
			s -> BlockSetType.values().filter(e -> e.name().equals(s)).findFirst().orElseThrow());

	public static final Codec<WoodType> WOOD_TYPE = ExtraCodecs.stringResolverCodec(
			WoodType::name,
			s -> WoodType.values().filter(e -> e.name().equals(s)).findFirst().orElseThrow());

	public static <B extends Block> RecordCodecBuilder<B, BlockBehaviour.Properties> propertiesCodec() {
		return BLOCK_PROPERTIES.fieldOf(BLOCK_PROPERTIES_KEY).forGetter(block -> block.properties);
	}

	public static <B extends Block> MapCodec<B> simpleCodec(Function<BlockBehaviour.Properties, B> function) {
		return RecordCodecBuilder.mapCodec(instance -> instance.group(propertiesCodec()).apply(instance, function));
	}

	public static final MapCodec<Block> BLOCK = RecordCodecBuilder.mapCodec(instance -> instance.group(
			propertiesCodec()
	).apply(instance, properties -> {
		KBlockSettings settings = ((KBlockProperties) properties).kiwi$getSettings();
		if (settings != null && settings.hasComponent(KBlockComponents.WATER_LOGGABLE.getOrCreate())) {
			return new BasicBlock(properties);
		} else {
			return new Block(properties);
		}
	}));

	public static final MapCodec<StairBlock> STAIR = RecordCodecBuilder.mapCodec(instance -> instance.group(
			BlockState.CODEC.optionalFieldOf("base_state", Blocks.AIR.defaultBlockState()).forGetter(block -> block.baseState),
			propertiesCodec()
	).apply(instance, StairBlock::new));

	public static final MapCodec<DoorBlock> DOOR = RecordCodecBuilder.mapCodec(instance -> instance.group(
			propertiesCodec(),
			BLOCK_SET_TYPE.fieldOf("block_set_type").forGetter(DoorBlock::type)
	).apply(instance, DoorBlock::new));

	public static final MapCodec<TrapDoorBlock> TRAPDOOR = RecordCodecBuilder.mapCodec(instance -> instance.group(
			propertiesCodec(),
			BLOCK_SET_TYPE.fieldOf("block_set_type").forGetter(block -> block.type)
	).apply(instance, TrapDoorBlock::new));

	public static final MapCodec<FenceGateBlock> FENCE_GATE = RecordCodecBuilder.mapCodec(instance -> instance.group(
			propertiesCodec(),
			WOOD_TYPE.optionalFieldOf("wood_type", WoodType.OAK).forGetter($ -> WoodType.OAK)
	).apply(instance, FenceGateBlock::new));

	public static final MapCodec<SandBlock> SAND = RecordCodecBuilder.mapCodec(instance -> instance.group(
			Codec.INT.optionalFieldOf("falling_dust_color", 14406560).forGetter($ -> 14406560),
			propertiesCodec()
	).apply(instance, SandBlock::new));

	static {
		register(new ResourceLocation("block"), BLOCK);
		register(new ResourceLocation("stair"), STAIR);
		register(new ResourceLocation("door"), DOOR);
		register(new ResourceLocation("trapdoor"), TRAPDOOR);
		register(new ResourceLocation("fence_gate"), FENCE_GATE);
		register(new ResourceLocation("colored_falling"), SAND);
	}

	public static void register(ResourceLocation key, MapCodec<? extends Block> codec) {
		//noinspection unchecked
		CODECS.put(key, (MapCodec<Block>) codec);
	}

	public static MapCodec<Block> get(ResourceLocation key) {
		return Objects.requireNonNull(CODECS.get(key), key::toString);
	}
}
