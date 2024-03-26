package org.teacon.xkdeco.block.loader;

import org.teacon.xkdeco.block.setting.KBlockSettings;

import snownee.kiwi.KiwiModule;

public record KBlockDefinition(KBlockSettings settings, KMaterial material, KiwiModule.RenderLayer.Layer renderType) {

}
