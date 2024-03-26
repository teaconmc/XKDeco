package org.teacon.xkdeco.duck;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.teacon.xkdeco.block.setting.KBlockSettings;

public interface XKBlockProperties {
	@Nullable
	@ApiStatus.NonExtendable
	KBlockSettings xkdeco$getSettings();

	@ApiStatus.NonExtendable
	void xkdeco$setSettings(KBlockSettings settings);
}
