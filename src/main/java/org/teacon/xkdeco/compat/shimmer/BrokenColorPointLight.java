/*
package org.teacon.xkdeco.compat.shimmer;

import java.util.Objects;

import com.lowdragmc.shimmer.client.light.ColorPointLight;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;

public class BrokenColorPointLight extends ColorPointLight {
	private final float originalRadius;

	public BrokenColorPointLight(BlockPos pos, Template template, boolean uv) {
		super(pos, template, uv);
		originalRadius = template.radius;
	}

	@Override
	public void update() {
		float strength = Objects.requireNonNull(Minecraft.getInstance().level).getGameTime() % 32L / 32F;
		this.radius = originalRadius * (strength);
		super.update();
	}
}
*/
