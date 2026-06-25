package org.teacon.xkdeco.client.renderer;

import org.teacon.xkdeco.blockentity.ItemDisplayBlockEntity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import org.teacon.xkdeco.util.NotNullByDefault;

@NotNullByDefault
public final class ItemDisplayRenderer implements BlockEntityRenderer<ItemDisplayBlockEntity, ItemDisplayRenderState> {
	private final ItemModelResolver itemModelResolver;
	private final RandomSource random = RandomSource.create();

	public ItemDisplayRenderer(BlockEntityRendererProvider.Context context) {
		this.itemModelResolver = context.itemModelResolver();
	}

	@Override
	public boolean shouldRender(ItemDisplayBlockEntity pBlockEntity, Vec3 pCameraPos) {
		return pBlockEntity.isProjector()
				? Vec3.atCenterOf(pBlockEntity.getBlockPos()).closerThan(
				pCameraPos,
				Minecraft.getInstance().options.getEffectiveRenderDistance() * 16)
				: BlockEntityRenderer.super.shouldRender(pBlockEntity, pCameraPos);
	}

	@Override
	public AABB getRenderBoundingBox(ItemDisplayBlockEntity be) {
		if (be.isProjector()) {
			return AABB.ofSize(Vec3.atBottomCenterOf(be.getBlockPos().above(9)), 16, 16, 16);
		} else {
			return AABB.unitCubeFromLowerCorner(Vec3.atLowerCornerOf(be.getBlockPos().above()));
		}
	}

	@Override
	public ItemDisplayRenderState createRenderState() {
		return new ItemDisplayRenderState();
	}

	@Override
	public void extractRenderState(
			ItemDisplayBlockEntity blockEntity,
			ItemDisplayRenderState state,
			float partialTicks,
			Vec3 cameraPosition,
			ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
		BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
		// borrowed from ItemEntityRenderer

		ItemStack itemstack = blockEntity.getFirstItem();
		state.projector = blockEntity.isProjector();

		if (itemstack.isEmpty()) {
			state.item.clear();
			state.amount = 0;
			return;
		}

		float spin = blockEntity.getSpin();
		if (!blockEntity.hasFixedSpin()) {
			spin += partialTicks;
		}
		state.spin = spin * 0.05F;
		state.amount = getRenderAmount(itemstack);
		state.seed = Item.getId(itemstack.getItem()) + itemstack.getDamageValue();

		this.itemModelResolver.updateForTopItem(
				state.item,
				itemstack,
				ItemDisplayContext.GROUND,
				blockEntity.getLevel(),
				null,
				1);

		Level level = blockEntity.getLevel();
		BlockPos above = blockEntity.getBlockPos().above();
		state.displayLightCoords = level != null
				? LightCoordsUtil.pack(
				level.getBrightness(LightLayer.BLOCK, above),
				level.getBrightness(LightLayer.SKY, above))
				: state.lightCoords;
	}

	@Override
	public void submit(
			ItemDisplayRenderState state,
			PoseStack poseStack,
			SubmitNodeCollector submitNodeCollector,
			CameraRenderState camera) {
		if (state.item.isEmpty() || state.amount <= 0) {
			return;
		}

		this.random.setSeed(state.seed);

		AABB modelBoundingBox = state.item.getModelBoundingBox();
		// in 26.1 the model "depth" replaces the old isGui3d() check
		boolean gui3d = modelBoundingBox.getZsize() > 0.0625F;

		// In 1.21.1 this used bakedmodel.getTransforms().getTransform(GROUND).scale.y(). In 26.1 the
		// GROUND transform is baked into the render state, so recover that vertical scale from the
		// post-transform model bounding box height (equivalent for standard item/block models).
		double modelScale = modelBoundingBox.getYsize();

		poseStack.pushPose();
		poseStack.translate(0.5, 1 + 0.1F + 0.25 * modelScale * (state.projector ? 24 : 1), 0.5);
		poseStack.mulPose(Axis.YP.rotation(state.spin));

		if (state.projector) {
			poseStack.scale(16, 16, 16);
		}

		if (!gui3d) {
			poseStack.translate(
					-0.0F * (float) (state.amount - 1) * 0.5F,
					-0.0F * (float) (state.amount - 1) * 0.5F,
					-0.09375F * (float) (state.amount - 1) * 0.5F);
		}

		for (var k = 0; k < state.amount; ++k) {
			poseStack.pushPose();
			if (k > 0) {
				if (gui3d) {
					poseStack.translate(
							(this.random.nextFloat() * 2.0F - 1.0F) * 0.15F,
							(this.random.nextFloat() * 2.0F - 1.0F) * 0.15F,
							(this.random.nextFloat() * 2.0F - 1.0F) * 0.15F);
				} else {
					poseStack.translate(
							(this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F,
							(this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F,
							0.0D);
				}
			}

			state.item.submit(
					poseStack,
					submitNodeCollector,
					state.displayLightCoords,
					OverlayTexture.NO_OVERLAY,
					0);
			poseStack.popPose();
			if (!gui3d) {
				poseStack.translate(0.0, 0.0, 0.09375F);
			}
		}

		poseStack.popPose();
	}

	private static int getRenderAmount(ItemStack pStack) {
		var i = 1;
		if (pStack.getCount() > 48) {
			i = 5;
		} else if (pStack.getCount() > 32) {
			i = 4;
		} else if (pStack.getCount() > 16) {
			i = 3;
		} else if (pStack.getCount() > 1) {
			i = 2;
		}

		return i;
	}
}
