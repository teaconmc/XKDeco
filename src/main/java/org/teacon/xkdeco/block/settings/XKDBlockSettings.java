package org.teacon.xkdeco.block.settings;

import org.teacon.xkdeco.duck.XKDBlock;

import net.minecraft.world.level.block.Block;

public class XKDBlockSettings {
	private final boolean sustainsPlant;

	private XKDBlockSettings(Builder builder) {
		this.sustainsPlant = builder.sustainsPlant;
	}

	public static XKDBlockSettings.Builder builder() {
		return new Builder();
	}

	public void setTo(Block block) {
		((XKDBlock) block).xkdeco$setSettings(this);
	}

	public boolean sustainsPlant() {
		return sustainsPlant;
	}

	public static class Builder {
		private boolean sustainsPlant = false;

		public Builder sustainsPlant() {
			this.sustainsPlant = true;
			return this;
		}

		public XKDBlockSettings build() {
			return new XKDBlockSettings(this);
		}
	}
}
