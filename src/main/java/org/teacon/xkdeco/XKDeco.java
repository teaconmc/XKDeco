package org.teacon.xkdeco;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.resources.ResourceLocation;

public final class XKDeco {
	public static final String ID = "xkdeco";
	public static final Logger LOGGER = LogUtils.getLogger();

	public static ResourceLocation id(String path) {
		return new ResourceLocation(ID, path);
	}
}
