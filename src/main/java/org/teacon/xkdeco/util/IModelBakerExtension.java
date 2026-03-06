package org.teacon.xkdeco.util;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.UnbakedModel;

public interface IModelBakerExtension {
	@Nullable UnbakedModel xkdeco$getTopLevelModel(ModelResourceLocation id);
}
