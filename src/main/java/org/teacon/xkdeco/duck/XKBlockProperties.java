package org.teacon.xkdeco.duck;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.teacon.xkdeco.block.setting.XKBlockSettings;

public interface XKBlockProperties {
	@Nullable
	@ApiStatus.NonExtendable
	XKBlockSettings xkdeco$getSettings();

	@ApiStatus.NonExtendable
	void xkdeco$setSettings(XKBlockSettings settings);
}
