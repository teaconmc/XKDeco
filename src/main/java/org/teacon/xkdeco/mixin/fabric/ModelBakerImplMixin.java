package org.teacon.xkdeco.mixin.fabric;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.teacon.xkdeco.util.IModelBakerExtension;

import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.UnbakedModel;

@Mixin(ModelBakery.ModelBakerImpl.class)
public class ModelBakerImplMixin implements IModelBakerExtension {
	@Shadow(aliases = "field_40571", remap = false)
	private ModelBakery this$0;

	@Override
	public @Nullable UnbakedModel xkdeco$getTopLevelModel(ModelResourceLocation id) {
		return this$0.topLevelModels.get(id);
	}
}
