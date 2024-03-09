package org.teacon.xkdeco.duck;

import org.jetbrains.annotations.Nullable;
import org.teacon.xkdeco.block.settings.XKDBlockSettings;

public interface XKDBlock {
	@Nullable
	XKDBlockSettings xkdeco$getSettings();

	void xkdeco$setSettings(XKDBlockSettings settings);
}
