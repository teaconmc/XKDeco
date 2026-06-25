package org.teacon.xkdeco.client.model;

import com.google.common.collect.ImmutableList;
import com.mojang.math.Quadrant;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.renderer.block.dispatch.BlockModelRotation;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.block.dispatch.ModelState;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.SimpleModelWrapper;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.model.block.CustomUnbakedBlockStateModel;
import org.teacon.xkdeco.XKDeco;
import org.teacon.xkdeco.util.NotNullByDefault;

/**
 * Custom block-state model for the air duct block. Ported from the 1.21.1 Fabric
 * {@code UnbakedModel} that produced an {@link AirDuctBakedModel}.
 *
 * <p>In 26.1 the dynamic, connection-driven block model lives in the block-state model
 * pipeline. This record is the {@link BlockStateModel.Unbaked} (a {@link CustomUnbakedBlockStateModel})
 * that resolves and bakes the four sub-models (straight/corner/cover/frame) into the rotated
 * {@link BlockStateModelPart} sets that {@link AirDuctBakedModel} selects from at render time.
 */
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
		// straight: indexed by Direction.Axis ordinal (X=0, Y=1, Z=2).
		// Old constants: X90_Y90, X0_Y0, X90_Y0.
		var straightModels = ImmutableList.<BlockStateModelPart>builder();
		straightModels.add(bake(baker, straight, 90, 90));
		straightModels.add(bake(baker, straight, 0, 0));
		straightModels.add(bake(baker, straight, 90, 0));

		// corner: x in {0, 90, 270}, y in {0, 90, 180, 270}; index = xIndex * 4 + yIndex.
		var cornerModels = ImmutableList.<BlockStateModelPart>builder();
		for (int x : IntList.of(0, 90, 270)) {
			for (int y : IntList.of(0, 90, 180, 270)) {
				cornerModels.add(bake(baker, corner, x, y));
			}
		}

		// cover: indexed by Direction.from3DDataValue (DOWN, UP, NORTH, SOUTH, WEST, EAST).
		var coverModels = ImmutableList.<BlockStateModelPart>builder();
		coverModels.add(bake(baker, cover, 270, 0)); // DOWN
		coverModels.add(bake(baker, cover, 90, 0)); // UP
		coverModels.add(bake(baker, cover, 0, 180)); // NORTH
		coverModels.add(bake(baker, cover, 0, 0)); // SOUTH
		coverModels.add(bake(baker, cover, 0, 90)); // WEST
		coverModels.add(bake(baker, cover, 0, 270)); // EAST

		var frameModel = bake(baker, frame, 0, 0);

		return new AirDuctBakedModel(
				straightModels.build(),
				cornerModels.build(),
				coverModels.build(),
				frameModel);
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
