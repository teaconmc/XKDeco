package org.teacon.xkdeco.client.model;

import org.teacon.xkdeco.XKDeco;
import org.teacon.xkdeco.util.NotNullByDefault;

import com.google.common.collect.ImmutableList;
import com.mojang.math.Quadrant;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import it.unimi.dsi.fastutil.ints.IntList;
import net.fabricmc.fabric.api.client.model.loading.v1.CustomUnbakedBlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockModelRotation;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.block.dispatch.ModelState;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.SimpleModelWrapper;
import net.minecraft.resources.Identifier;

@NotNullByDefault
public record AirDuctModel(Identifier straight, Identifier corner, Identifier cover, Identifier frame)
		implements CustomUnbakedBlockStateModel {
	public static final Identifier ID = XKDeco.id("air_duct");
	public static final MapCodec<AirDuctModel> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			Identifier.CODEC.fieldOf("straight").forGetter(AirDuctModel::straight),
			Identifier.CODEC.fieldOf("corner").forGetter(AirDuctModel::corner),
			Identifier.CODEC.fieldOf("cover").forGetter(AirDuctModel::cover),
			Identifier.CODEC.fieldOf("frame").forGetter(AirDuctModel::frame)
	).apply(instance, AirDuctModel::new));

	private static ModelState rotation(int xDeg, int yDeg) {
		return BlockModelRotation.get(Quadrant.fromXYAngles(Quadrant.parseJson(xDeg), Quadrant.parseJson(yDeg)));
	}

	private static BlockStateModelPart bake(ModelBaker baker, Identifier location, int xDeg, int yDeg) {
		return SimpleModelWrapper.bake(baker, location, rotation(xDeg, yDeg));
	}

	@Override
	public BlockStateModel bake(ModelBaker baker) {
		var straightModels = ImmutableList.<BlockStateModelPart>builder();
		straightModels.add(bake(baker, straight, 90, 90));
		straightModels.add(bake(baker, straight, 0, 0));
		straightModels.add(bake(baker, straight, 90, 0));

		var cornerModels = ImmutableList.<BlockStateModelPart>builder();
		for (int x : IntList.of(0, 90, 270)) {
			for (int y : IntList.of(0, 90, 180, 270)) {
				cornerModels.add(bake(baker, corner, x, y));
			}
		}

		var coverModels = ImmutableList.<BlockStateModelPart>builder();
		coverModels.add(bake(baker, cover, 270, 0));
		coverModels.add(bake(baker, cover, 90, 0));
		coverModels.add(bake(baker, cover, 0, 180));
		coverModels.add(bake(baker, cover, 0, 0));
		coverModels.add(bake(baker, cover, 0, 90));
		coverModels.add(bake(baker, cover, 0, 270));

		return new AirDuctBakedModel(
				straightModels.build(),
				cornerModels.build(),
				coverModels.build(),
				bake(baker, frame, 0, 0));
	}

	@Override
	public void resolveDependencies(Resolver resolver) {
		resolver.markDependency(straight);
		resolver.markDependency(corner);
		resolver.markDependency(cover);
		resolver.markDependency(frame);
	}

	@Override
	public MapCodec<? extends CustomUnbakedBlockStateModel> codec() {
		return MAP_CODEC;
	}
}
