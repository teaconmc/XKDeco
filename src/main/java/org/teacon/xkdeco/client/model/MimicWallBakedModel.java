package org.teacon.xkdeco.client.model;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;

import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.fabricmc.fabric.api.renderer.v1.model.ModelHelper;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.MultiPartBakedModel;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.WallSide;
import snownee.kiwi.util.NotNullByDefault;

@NotNullByDefault
public class MimicWallBakedModel extends ForwardingBakedModel {
	private static final List<EnumProperty<WallSide>> WALL_SIDE_PROPERTIES = List.of(
			WallBlock.NORTH_WALL,
			WallBlock.EAST_WALL,
			WallBlock.SOUTH_WALL,
			WallBlock.WEST_WALL);
	private final WallBlock base;

	public MimicWallBakedModel(WallBlock base, BakedModel baked) {
		this.base = base;
		wrapped = baked;
		if (wrapped instanceof MultiPartBakedModel multiPart) {
			RandomSource random = RandomSource.create(42L);
			BlockState blockState = base.defaultBlockState();
			EnumMap<Direction, List<BakedQuad>> culledFaces = Maps.newEnumMap(Direction.class);
			for (Direction direction : Direction.values()) {
				culledFaces.put(direction, multiPart.getQuads(blockState, direction, random));
			}
			wrapped = new SimpleBakedModel(
					wrapped.getQuads(blockState, null, random),
					culledFaces,
					wrapped.useAmbientOcclusion(),
					wrapped.usesBlockLight(),
					wrapped.isGui3d(),
					wrapped.getParticleIcon(),
					wrapped.getTransforms(),
					wrapped.getOverrides());
		}
	}

	@Override
	public void emitBlockQuads(
			BlockAndTintGetter blockView,
			BlockState state,
			BlockPos pos,
			Supplier<RandomSource> randomSupplier,
			RenderContext context) {
		((FabricBakedModel) wrapped).emitBlockQuads(blockView, base.defaultBlockState(), pos, randomSupplier, context);

		BlockPos.MutableBlockPos mutable = pos.mutable();
		for (Direction direction : Direction.Plane.HORIZONTAL) {
			BlockState blockState = blockView.getBlockState(mutable.move(direction));
			mutable.set(pos);
			if (blockState.getBlock() instanceof WallBlock && blockState.is(BlockTags.WALLS)) {
				WallSide wallSide = blockState.getValue(WALL_SIDE_PROPERTIES.get(direction.get2DDataValue()));
				if (wallSide != WallSide.NONE) {
					blockState = blockState.getBlock().defaultBlockState().setValue(WallBlock.UP, false).setValue(
							WALL_SIDE_PROPERTIES.get(direction.getOpposite().get2DDataValue()), wallSide);
					var model = Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(blockState);
					((FabricBakedModel) model).emitBlockQuads(blockView, blockState, pos, randomSupplier, context);
				}
			}
		}
	}

	@Override
	public ItemTransforms getTransforms() {
		return ModelHelper.MODEL_TRANSFORM_BLOCK;
	}

	@Override
	public boolean isVanillaAdapter() {
		return false;
	}

	@Override
	public BakedModel applyTransform(ItemDisplayContext transformType, PoseStack poseStack, boolean applyLeftHandTransform) {
		getTransforms().getTransform(transformType).apply(applyLeftHandTransform, poseStack);
		return wrapped instanceof MultiPartBakedModel ? super.applyTransform(transformType, poseStack, applyLeftHandTransform) : this;
	}
}
