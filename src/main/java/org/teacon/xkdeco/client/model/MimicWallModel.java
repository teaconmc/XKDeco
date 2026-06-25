package org.teacon.xkdeco.client.model;

import org.teacon.xkdeco.util.NotNullByDefault;

import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;

@NotNullByDefault
public final class MimicWallModel implements BlockStateModel.UnbakedRoot {
	private final WallBlock base;

	public MimicWallModel(WallBlock base) {
		this.base = base;
	}

	@Override
	public void resolveDependencies(ResolvableModel.Resolver resolver) {
	}

	@Override
	public BlockStateModel bake(BlockState blockState, ModelBaker modelBakery) {
		return new MimicWallBakedModel(base);
	}

	@Override
	public Object visualEqualityGroup(BlockState blockState) {
		return this;
	}
}
