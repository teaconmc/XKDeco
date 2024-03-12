package org.teacon.xkdeco.block.settings;

import snownee.kiwi.KiwiModule;

public record GlassType(boolean skipRendering, float shadeBrightness, KiwiModule.RenderLayer.Layer renderType) {
	public static final GlassType CLEAR = new GlassType(true, 1F, KiwiModule.RenderLayer.Layer.CUTOUT);
	public static final GlassType TRANSLUCENT = new GlassType(true, 1F, KiwiModule.RenderLayer.Layer.TRANSLUCENT);
	public static final GlassType QUARTZ = new GlassType(true, 1F, KiwiModule.RenderLayer.Layer.TRANSLUCENT);
	public static final GlassType TOUGHENED = new GlassType(true, 1F, KiwiModule.RenderLayer.Layer.TRANSLUCENT);
	public static final GlassType HOLLOW_STEEL = new GlassType(false, 0.9F, KiwiModule.RenderLayer.Layer.CUTOUT);
}
