package org.teacon.xkdeco.client.model;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;
import org.teacon.xkdeco.util.IModelBakerExtension;

import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.WallBlock;
import snownee.kiwi.util.NotNullByDefault;

@NotNullByDefault
public class MimicWallModel implements UnbakedModel {
	private final WallBlock base;

	public MimicWallModel(WallBlock base) {
		this.base = base;
	}

	@Override
	public Collection<ResourceLocation> getDependencies() {
		return List.of();
	}

	@Override
	public void resolveParents(Function<ResourceLocation, UnbakedModel> resolver) {
	}

	@Override
	public @Nullable BakedModel bake(ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState state) {
		UnbakedModel topLevelModel = Objects.requireNonNull(((IModelBakerExtension) baker).xkdeco$getTopLevelModel(BlockModelShaper.stateToModelLocation(
				base.defaultBlockState())));
		BakedModel baked = Objects.requireNonNull(topLevelModel.bake(baker, spriteGetter, state));
		return new MimicWallBakedModel(base, baked);
	}
}
