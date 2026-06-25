package org.teacon.xkdeco.client.renderer;

import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import org.teacon.xkdeco.util.NotNullByDefault;

@NotNullByDefault
public class BlockDisplayRenderState extends BlockEntityRenderState {
	public final BlockModelRenderState blockModel = new BlockModelRenderState();
	public boolean hasBlock;
	public int displayLightCoords;
}
