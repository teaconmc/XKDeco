package org.teacon.xkdeco.client.renderer;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;

public class ItemDisplayRenderState extends BlockEntityRenderState {
	public final ItemStackRenderState item = new ItemStackRenderState();
	public boolean projector;
	public float spin;
	public int amount = 1;
	public long seed;
	public int displayLightCoords;
}
