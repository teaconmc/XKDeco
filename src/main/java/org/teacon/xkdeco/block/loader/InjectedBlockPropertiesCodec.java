package org.teacon.xkdeco.block.loader;

import org.teacon.xkdeco.block.setting.KBlockSettings;
import org.teacon.xkdeco.duck.KBlockProperties;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

import net.minecraft.world.level.block.state.BlockBehaviour;

public record InjectedBlockPropertiesCodec(Codec<BlockBehaviour.Properties> delegate) implements Codec<BlockBehaviour.Properties> {
	public static final ThreadLocal<KBlockSettings> INJECTED = new ThreadLocal<>();

	@Override
	public <T> DataResult<Pair<BlockBehaviour.Properties, T>> decode(DynamicOps<T> ops, T input) {
		DataResult<Pair<BlockBehaviour.Properties, T>> result = delegate.decode(ops, input);
		KBlockSettings settings = INJECTED.get();
		if (settings != null) {
			INJECTED.remove();
			result.result().ifPresent(pair -> {
				((KBlockProperties) pair.getFirst()).xkdeco$setSettings(settings);
			});
		}
		return result;
	}

	@Override
	public <T> DataResult<T> encode(BlockBehaviour.Properties input, DynamicOps<T> ops, T prefix) {
		return delegate.encode(input, ops, prefix);
	}
}
