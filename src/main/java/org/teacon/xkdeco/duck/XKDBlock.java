package org.teacon.xkdeco.duck;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.teacon.xkdeco.block.settings.XKDBlockSettings;

public interface XKDBlock {
	@Nullable
	@ApiStatus.NonExtendable
	XKDBlockSettings xkdeco$getSettings();

	@ApiStatus.NonExtendable
	void xkdeco$setSettings(XKDBlockSettings settings);
}
