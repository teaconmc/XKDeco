package org.teacon.xkdeco.client.renderer;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import org.teacon.xkdeco.util.NotNullByDefault;

@NotNullByDefault
public class HologramRenderState extends BlockEntityRenderState {
	public final ItemStackRenderState item = new ItemStackRenderState();
	public float rotation = 180F;
}
