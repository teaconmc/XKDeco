package org.teacon.xkdeco.client.renderer;

import org.teacon.xkdeco.blockentity.WallBlockEntity;
import org.teacon.xkdeco.item.SpecialWallItem;

import com.mojang.blaze3d.vertex.PoseStack;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public final class XKDecoWithoutLevelRenderer extends BlockEntityWithoutLevelRenderer {
	public static final XKDecoWithoutLevelRenderer INSTANCE = new XKDecoWithoutLevelRenderer(Minecraft.getInstance());

	private final BlockEntityRenderDispatcher dispatcher;

	private XKDecoWithoutLevelRenderer(Minecraft mc) {
		super(mc.getBlockEntityRenderDispatcher(), mc.getEntityModels());
		this.dispatcher = mc.getBlockEntityRenderDispatcher();
	}

	@Override
	public void renderByItem(
			ItemStack pStack, ItemDisplayContext pDisplayContext,
			PoseStack pPose, MultiBufferSource pBuffer, int pLight, int pOverlay) {
		if (pStack.getItem() instanceof SpecialWallItem item) {
			var state = item.getBlock().defaultBlockState().setValue(BlockStateProperties.UP, true);
			this.dispatcher.renderItem(new WallBlockEntity(BlockPos.ZERO, state), pPose, pBuffer, pLight, pOverlay);
		} else {
			super.renderByItem(pStack, pDisplayContext, pPose, pBuffer, pLight, pOverlay);
		}
	}
}
