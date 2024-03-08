package org.teacon.xkdeco.client.model;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;
import org.teacon.xkdeco.block.XKDStateProperties;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

@SuppressWarnings("deprecation")
public class AirDuctBakedModel implements BakedModel {
	private final Cache<Pair<BlockState, Direction>, List<BakedQuad>> cache = CacheBuilder.newBuilder().expireAfterAccess(
			5,
			TimeUnit.MINUTES).build();
	private final List<BakedModel> straight;
	private final List<BakedModel> corner;
	private final List<BakedModel> cover;
	private final BakedModel frame;

	public AirDuctBakedModel(List<BakedModel> straight, List<BakedModel> corner, List<BakedModel> cover, BakedModel frame) {
		this.straight = straight;
		this.corner = corner;
		this.cover = cover;
		this.frame = frame;
	}

	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState blockState, @Nullable Direction direction, RandomSource randomSource) {
		if (blockState == null) {
			return straight.get(0).getQuads(null, direction, randomSource);
		}
		try {
			return cache.get(Pair.of(blockState, direction), () -> {
				List<Direction> trueDirections = Lists.newArrayListWithExpectedSize(6);
				for (int i = 0; i < 6; i++) {
					if (blockState.getValue(XKDStateProperties.DIRECTION_PROPERTIES.get(i))) {
						trueDirections.add(Direction.from3DDataValue(i));
					}
				}
				if (trueDirections.size() == 2) {
					var direction1 = trueDirections.get(0);
					var direction2 = trueDirections.get(1);
					if (direction1.getOpposite() == direction2) {
						return straight.get(direction1.getAxis().ordinal()).getQuads(blockState, direction, randomSource);
					} else {
						int index;
						if (direction1 == Direction.DOWN) {
							index = 4 + direction2.getCounterClockWise().get2DDataValue();
						} else if (direction1 == Direction.UP) {
							index = 8 + direction2.getCounterClockWise().get2DDataValue();
						} else if (direction1 == Direction.SOUTH && direction2 == Direction.EAST) {
							index = 2;
						} else if (direction1.get2DDataValue() < direction2.get2DDataValue()) {
							index = direction1.getCounterClockWise().get2DDataValue();
						} else {
							index = direction2.getCounterClockWise().get2DDataValue();
						}
						return corner.get(index).getQuads(blockState, direction, randomSource);
					}
				}
				var quads = ImmutableList.<BakedQuad>builder();
				quads.addAll(frame.getQuads(blockState, direction, randomSource));
				for (int i = 0; i < 6; i++) {
					if (!blockState.getValue(XKDStateProperties.DIRECTION_PROPERTIES.get(i))) {
						quads.addAll(cover.get(i).getQuads(blockState, direction, randomSource));
					}
				}
				return quads.build();
			});
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean useAmbientOcclusion() {
		return straight.get(0).useAmbientOcclusion();
	}

	@Override
	public boolean isGui3d() {
		return false;
	}

	@Override
	public boolean usesBlockLight() {
		return straight.get(0).usesBlockLight();
	}

	@Override
	public boolean isCustomRenderer() {
		return false;
	}

	@Override
	public TextureAtlasSprite getParticleIcon() {
		return straight.get(0).getParticleIcon();
	}

	@Override
	public ItemOverrides getOverrides() {
		return ItemOverrides.EMPTY;
	}
}
