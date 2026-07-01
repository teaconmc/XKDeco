package org.teacon.xkdeco.client.model;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;
import org.teacon.xkdeco.XKDeco;
import org.teacon.xkdeco.block.MimicWallBlock;
import org.teacon.xkdeco.init.MimicWallsLoader;

import com.mojang.serialization.MapCodec;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.dispatch.BlockModelRotation;
import net.minecraft.client.renderer.item.CuboidItemModelWrapper;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.item.ModelRenderProperties;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.client.resources.model.cuboid.ItemTransform;
import net.minecraft.client.resources.model.cuboid.ItemTransforms;
import net.minecraft.client.resources.model.sprite.TextureSlots;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.WallBlock;

public final class MimicWallItemModel implements ItemModel {
	public static final Identifier ID = XKDeco.id("mimic_wall");
	private static final ItemTransforms POST_TRANSFORMS = new ItemTransforms(
			transform(75, 45, 0, 0, 2.5F, 0, 0.375F, 0.375F, 0.375F),
			transform(75, 45, 0, 0, 2.5F, 0, 0.375F, 0.375F, 0.375F),
			transform(0, 225, 0, 0, 0, 0, 0.4F, 0.4F, 0.4F),
			transform(0, 45, 0, 0, 0, 0, 0.4F, 0.4F, 0.4F),
			ItemTransform.NO_TRANSFORM,
			transform(30, 135, 0, 0, 0, 0, 0.625F, 0.625F, 0.625F),
			transform(0, 0, 0, 0, 3, 0, 0.25F, 0.25F, 0.25F),
			transform(0, 90, 0, 0, 0, 0, 0.5F, 0.5F, 0.5F),
			transform(0, 90, 0, 0, 0, 0, 1, 1, 1));

	private final Map<Item, ItemModel> models;
	private final ItemModel fallback;

	private MimicWallItemModel(Map<Item, ItemModel> models, ItemModel fallback) {
		this.models = Map.copyOf(models);
		this.fallback = fallback;
	}

	@Override
	public void update(
			ItemStackRenderState output,
			ItemStack item,
			ItemModelResolver resolver,
			ItemDisplayContext displayContext,
			@Nullable ClientLevel level,
			@Nullable ItemOwner owner,
			int seed) {
		output.appendModelIdentityElement(this);
		this.models.getOrDefault(item.getItem(), this.fallback)
				.update(output, item, resolver, displayContext, level, owner, seed);
	}

	private static ItemTransform transform(
			float rotationX,
			float rotationY,
			float rotationZ,
			float translationX,
			float translationY,
			float translationZ,
			float scaleX,
			float scaleY,
			float scaleZ) {
		return new ItemTransform(
				new Vector3f(rotationX, rotationY, rotationZ),
				new Vector3f(translationX / 16.0F, translationY / 16.0F, translationZ / 16.0F),
				new Vector3f(scaleX, scaleY, scaleZ));
	}

	private static Identifier postModel(WallBlock wall) {
		return BuiltInRegistries.BLOCK.getKey(wall)
				.withPrefix("block/")
				.withSuffix("_post");
	}

	private static ItemModel bakePostModel(ModelBaker baker, Matrix4fc transformation, MimicWallBlock mimicWall) {
		ResolvedModel resolvedModel = baker.getModel(postModel(mimicWall.getWallDelegate()));
		TextureSlots textureSlots = resolvedModel.getTopTextureSlots();
		QuadCollection quads = resolvedModel.bakeTopGeometry(textureSlots, baker, BlockModelRotation.IDENTITY);
		ModelRenderProperties resolvedProperties = ModelRenderProperties.fromResolvedModel(baker, resolvedModel, textureSlots);
		ModelRenderProperties properties = new ModelRenderProperties(true, resolvedProperties.particleMaterial(), POST_TRANSFORMS);
		return new CuboidItemModelWrapper(List.of(), quads, properties, transformation);
	}

	private record BakedModelsKey(Matrix4fc transformation) implements ModelBaker.SharedOperationKey<Map<Item, ItemModel>> {
		@Override
		public Map<Item, ItemModel> compute(ModelBaker baker) {
			Map<Item, ItemModel> models = new IdentityHashMap<>();
			for (MimicWallBlock mimicWall : MimicWallsLoader.mimicWalls()) {
				models.put(mimicWall.asItem(), bakePostModel(baker, this.transformation, mimicWall));
			}
			return Map.copyOf(models);
		}
	}

	public record Unbaked() implements ItemModel.Unbaked {
		public static final MapCodec<MimicWallItemModel.Unbaked> MAP_CODEC = MapCodec.unit(MimicWallItemModel.Unbaked::new);

		@Override
		public MapCodec<MimicWallItemModel.Unbaked> type() {
			return MAP_CODEC;
		}

		@Override
		public void resolveDependencies(ResolvableModel.Resolver resolver) {
			for (MimicWallBlock mimicWall : MimicWallsLoader.mimicWalls()) {
				resolver.markDependency(postModel(mimicWall.getWallDelegate()));
			}
		}

		@Override
		public ItemModel bake(ItemModel.BakingContext context, Matrix4fc transformation) {
			Map<Item, ItemModel> models = context.blockModelBaker().compute(new BakedModelsKey(transformation));
			return new MimicWallItemModel(models, context.missingItemModel(transformation));
		}
	}
}
