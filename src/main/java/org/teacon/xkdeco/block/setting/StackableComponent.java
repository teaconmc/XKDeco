package org.teacon.xkdeco.block.setting;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.codecs.RecordCodecBuilder;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public record StackableComponent(IntegerProperty property) implements XKBlockComponent, LayeredComponent {
	private static final Int2ObjectOpenHashMap<IntegerProperty> PROPERTY_INTERN = new Int2ObjectOpenHashMap<>();
	public static final Type<StackableComponent> TYPE = XKBlockComponent.register(
			"stackable",
			RecordCodecBuilder.create(instance -> instance.group(
					ExtraCodecs.intRange(0, 1).optionalFieldOf("min", 1).forGetter(StackableComponent::minValue),
					ExtraCodecs.POSITIVE_INT.fieldOf("max").forGetter(StackableComponent::maxValue)
			).apply(instance, StackableComponent::create)));

	public static StackableComponent create(int max) {
		return create(1, max);
	}

	public static StackableComponent create(int min, int max) {
		IntegerProperty property = PROPERTY_INTERN.computeIfAbsent(min << 16 | max, key -> IntegerProperty.create("c", min, max));
		return new StackableComponent(property);
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
	public BlockState getStateForPlacement(XKBlockSettings settings, BlockState state, BlockPlaceContext context) {
		BlockState blockState = context.getLevel().getBlockState(context.getClickedPos());
		if (blockState.is(state.getBlock())) {
			return blockState.setValue(property, Math.min(maxValue(), blockState.getValue(property) + 1));
		}
		return state;
	}

	@Override
	public @Nullable Boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
		if (!context.isSecondaryUseActive() && context.getItemInHand().is(state.getBlock().asItem()) &&
				state.getValue(property) < maxValue()) {
			return Boolean.TRUE;
		}
		return null;
	}

	@Override
	public IntegerProperty getLayerProperty() {
		return property;
	}

	@Override
	public int getDefaultLayer() {
		return minValue();
	}
}
