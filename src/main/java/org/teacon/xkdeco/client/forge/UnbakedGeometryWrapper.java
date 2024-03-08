package org.teacon.xkdeco.client.forge;

import java.util.function.Function;

import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;

public class UnbakedGeometryWrapper implements IUnbakedGeometry<UnbakedGeometryWrapper> {
	protected final UnbakedModel wrapped;

	public UnbakedGeometryWrapper(UnbakedModel wrapped) {this.wrapped = wrapped;}

	@Override
	public BakedModel bake(
			IGeometryBakingContext context,
			ModelBaker baker,
			Function<Material, TextureAtlasSprite> spriteGetter,
			ModelState modelState,
			ItemOverrides overrides,
			ResourceLocation modelLocation) {
		return wrapped.bake(baker, spriteGetter, modelState, modelLocation);
	}

	@Override
	public void resolveParents(Function<ResourceLocation, UnbakedModel> modelGetter, IGeometryBakingContext context) {
		wrapped.resolveParents(modelGetter);
	}
}
