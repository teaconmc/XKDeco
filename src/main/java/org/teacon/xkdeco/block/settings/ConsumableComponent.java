package org.teacon.xkdeco.block.settings;

import org.jetbrains.annotations.Nullable;
import org.teacon.xkdeco.block.behavior.BlockBehaviorRegistry;

import com.mojang.serialization.codecs.RecordCodecBuilder;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;

public record ConsumableComponent(
		IntegerProperty property,
		@Nullable FoodProperties food,
		@Nullable ResourceLocation stat) implements XKBlockComponent, LayeredComponent {
	private static final Int2ObjectOpenHashMap<IntegerProperty> PROPERTY_INTERN = new Int2ObjectOpenHashMap<>();
	public static final Type<ConsumableComponent> TYPE = XKBlockComponent.register(
			"consumable",
			RecordCodecBuilder.create(instance -> instance.group(
					ExtraCodecs.intRange(0, 1).fieldOf("min").forGetter(ConsumableComponent::minValue),
					ExtraCodecs.POSITIVE_INT.fieldOf("max").forGetter(ConsumableComponent::maxValue)
			).apply(instance, ConsumableComponent::create)));

	public static ConsumableComponent create(int min, int max) {
		IntegerProperty property = PROPERTY_INTERN.computeIfAbsent(min << 16 | max, key -> IntegerProperty.create("uses", min, max));
		return new ConsumableComponent(property, null, null);
	}

	public ConsumableComponent withFood(FoodProperties food, @Nullable ResourceLocation stat) {
		return new ConsumableComponent(property, food, stat);
	}

	@Override
	public Type<?> type() {
		return TYPE;
	}

	@Override
	public void injectProperties(Block block, StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(property);
	}

	@Override
	public BlockState registerDefaultState(BlockState state) {
		return state.setValue(property, getDefaultLayer());
	}

	public int minValue() {
		return property.min;
	}

	public int maxValue() {
		return property.max;
	}

	@Override
	public boolean hasAnalogOutputSignal() {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState state) {
		return Math.min(state.getValue(property) - minValue() + 1, 15);
	}

	@Override
	public void addBehaviors(BlockBehaviorRegistry registry) {
		registry.addUseHandler((pState, pPlayer, pLevel, pHand, pHit) -> {
			int value = pState.getValue(property);
			if (value == 0) {
				return InteractionResult.PASS;
			}
			if (stat != null) {
				pPlayer.awardStat(stat);
			}
			BlockPos pos = pHit.getBlockPos();
			if (food != null) { //TODO block tag based drinking type
				if (!pPlayer.canEat(food.canAlwaysEat())) {
					return InteractionResult.FAIL;
				}
				Item item = pState.getBlock().asItem();
				pLevel.playSound(
						pPlayer,
						pPlayer.getX(),
						pPlayer.getY(),
						pPlayer.getZ(),
						item.getEatingSound(),
						SoundSource.NEUTRAL,
						1.0f,
						1.0f + (pLevel.random.nextFloat() - pLevel.random.nextFloat()) * 0.4f);
				if (!pLevel.isClientSide) {
					pPlayer.getFoodData().eat(food.getNutrition(), food.getSaturationModifier());
					for (var pair : food.getEffects()) {
						if (pair.getFirst() == null || !(pLevel.random.nextFloat() < pair.getSecond())) {
							continue;
						}
						pPlayer.addEffect(new MobEffectInstance(pair.getFirst()));
					}
				}
				pLevel.gameEvent(pPlayer, GameEvent.EAT, pos);
			}
			if (value == minValue()) {
				pLevel.removeBlock(pos, false);
			} else {
				pLevel.setBlockAndUpdate(pos, pState.setValue(property, value - 1));
			}
			return InteractionResult.sidedSuccess(pLevel.isClientSide);
		});
	}

	@Override
	public IntegerProperty getLayerProperty() {
		return property;
	}

	@Override
	public int getDefaultLayer() {
		return maxValue();
	}
}
