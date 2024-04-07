package org.teacon.xkdeco.duck;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.teacon.xkdeco.block.setting.KBlockSettings;

public interface KBlockProperties {
	@Nullable
	@ApiStatus.NonExtendable
	KBlockSettings kiwi$getSettings();

	@ApiStatus.NonExtendable
	void kiwi$setSettings(KBlockSettings settings);
}
