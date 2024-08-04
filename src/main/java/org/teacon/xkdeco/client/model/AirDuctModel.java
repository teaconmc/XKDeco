/*
package org.teacon.xkdeco.client.model;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;
import org.teacon.xkdeco.XKDeco;

import com.google.common.collect.ImmutableList;

import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;

public class AirDuctModel implements UnbakedModel {
	private final ResourceLocation straight;
	private final ResourceLocation corner;
	private final ResourceLocation cover;
	private final ResourceLocation frame;

	public AirDuctModel(ResourceLocation straight, ResourceLocation corner, ResourceLocation cover, ResourceLocation frame) {
		this.straight = straight;
		this.corner = corner;
		this.cover = cover;
		this.frame = frame;
	}

	@Override
	public Collection<ResourceLocation> getDependencies() {
		return List.of(straight, corner, cover, frame);
	}

	@Override
	public void resolveParents(Function<ResourceLocation, UnbakedModel> pResolver) {
		getDependencies().stream().map(pResolver).forEach($ -> $.resolveParents(pResolver));
	}

	@SuppressWarnings("deprecation")
	@Nullable
	@Override
	public BakedModel bake(
			ModelBaker pBaker,
			Function<Material, TextureAtlasSprite> pSpriteGetter,
			ModelState pState,
			ResourceLocation pLocation) {
		var straightModels = ImmutableList.<BakedModel>builder();
		for (BlockModelRotation rotation : List.of(
				BlockModelRotation.X90_Y90,
				BlockModelRotation.X0_Y0,
				BlockModelRotation.X90_Y0)) {
			BakedModel baked = pBaker.bake(straight, rotation);
			if (baked == null) {
				XKDeco.LOGGER.warn("Failed to bake air duct model: {}", straight);
				return null;
			}
			straightModels.add(baked);
		}
		var cornerModels = ImmutableList.<BakedModel>builder();
		for (int x : IntList.of(0, 90, 270)) {
			for (int y : IntList.of(0, 90, 180, 270)) {
				BakedModel baked = pBaker.bake(corner, BlockModelRotation.by(x, y));
				if (baked == null) {
					XKDeco.LOGGER.warn("Failed to bake air duct model: {}", corner);
					return null;
				}
				cornerModels.add(baked);
			}
		}
		var coverModels = ImmutableList.<BakedModel>builder();
		for (int x : IntList.of(270, 90)) {
			BakedModel baked = pBaker.bake(cover, BlockModelRotation.by(x, 0));
			if (baked == null) {
				XKDeco.LOGGER.warn("Failed to bake air duct model: {}", cover);
				return null;
			}
			coverModels.add(baked);
		}
		for (int y : IntList.of(180, 0, 90, 270)) {
			BakedModel baked = pBaker.bake(cover, BlockModelRotation.by(0, y));
			if (baked == null) {
				XKDeco.LOGGER.warn("Failed to bake air duct model: {}", cover);
				return null;
			}
			coverModels.add(baked);
		}
		var frameModel = pBaker.bake(frame, BlockModelRotation.X0_Y0);
		if (frameModel == null) {
			XKDeco.LOGGER.warn("Failed to bake air duct model: {}", frame);
			return null;
		}
		return new AirDuctBakedModel(straightModels.build(), cornerModels.build(), coverModels.build(), frameModel);
	}
}
*/
