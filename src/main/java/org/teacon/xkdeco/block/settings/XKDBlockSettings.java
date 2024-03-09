package org.teacon.xkdeco.block.settings;

import org.jetbrains.annotations.Nullable;
import org.teacon.xkdeco.duck.XKDBlock;

import net.minecraft.world.level.block.Block;

public class XKDBlockSettings {
	public final boolean sustainsPlant;
	@Nullable
	public final ShapeGenerator shape;
	@Nullable
	public final ShapeGenerator collisionShape;
	@Nullable
	public final ShapeGenerator interactionShape;

	private XKDBlockSettings(Builder builder) {
		this.sustainsPlant = builder.sustainsPlant;
		this.shape = builder.shape;
		this.collisionShape = builder.collisionShape;
		this.interactionShape = builder.interactionShape;
	}

	public static XKDBlockSettings.Builder builder() {
		return new Builder();
	}

	public XKDBlockSettings setTo(Block block) {
		((XKDBlock) block).xkdeco$setSettings(this);
		return this;
	}

	public static class Builder {
		private boolean sustainsPlant;
		@Nullable
		private ShapeGenerator shape;
		@Nullable
		private ShapeGenerator collisionShape;
		@Nullable
		private ShapeGenerator interactionShape;

		public Builder sustainsPlant() {
			this.sustainsPlant = true;
			return this;
		}

		public Builder shape(ShapeGenerator shape) {
			this.shape = shape;
			return this;
		}

		public Builder collisionShape(ShapeGenerator collisionShape) {
			this.collisionShape = collisionShape;
			return this;
		}

		public Builder interactionShape(ShapeGenerator interactionShape) {
			this.interactionShape = interactionShape;
			return this;
		}

		public XKDBlockSettings build() {
			return new XKDBlockSettings(this);
		}
	}
}
