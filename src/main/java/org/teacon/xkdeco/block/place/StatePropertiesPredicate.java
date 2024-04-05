package org.teacon.xkdeco.block.place;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import org.teacon.xkdeco.block.loader.LoaderExtraCodecs;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;

import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public record StatePropertiesPredicate(List<PropertyMatcher> properties) implements Predicate<BlockState> {

	public static final Codec<StatePropertiesPredicate> CODEC = Codec.compoundList(Codec.STRING, Codec.either(
			Codec.STRING,
			LoaderExtraCodecs.INT_BOUNDS)
	).xmap($ -> new StatePropertiesPredicate($.stream().map(pair -> {
		return new PropertyMatcher(pair.getFirst(), pair.getSecond());
	}).toList()), $ -> $.properties.stream().map(matcher -> {
		return Pair.of(matcher.key, matcher.value);
	}).toList());

	public static Property<?> getProperty(BlockState blockState, String key) {
		Property<?> property = blockState.getBlock().getStateDefinition().getProperty(key);
		if (property == null) {
			throw new IllegalStateException("Property %s not found".formatted(key));
		}
		return property;
	}

	@Override
	public boolean test(BlockState blockState) {
		for (PropertyMatcher matcher : this.properties) {
			if (!matcher.matches(blockState)) {
				return false;
			}
		}
		return true;
	}

	public record PropertyMatcher(String key, Either<String, MinMaxBounds.Ints> value) {

		public boolean matches(BlockState blockState) {
			Property<?> property = getProperty(blockState, key);
			Optional<String> s = value.left();
			boolean isInteger = property.getValueClass() == Integer.class;
			if (s.isEmpty() != isInteger) {
				throw new IllegalStateException("Property value type mismatch");
			}
			if (isInteger) {
				return value.right().orElseThrow().matches((Integer) blockState.getValue(property));
			} else {
				return blockState.getValue(property).toString().equals(s.get());
			}
		}
	}
}
