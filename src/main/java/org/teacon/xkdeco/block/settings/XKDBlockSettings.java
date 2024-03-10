package org.teacon.xkdeco.block.settings;

import org.jetbrains.annotations.Nullable;
import org.teacon.xkdeco.duck.XKDBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class XKDBlockSettings {
	public final boolean sustainsPlant;
	public final GlassType glassType;
	@Nullable
	public final ShapeGenerator shape;
	@Nullable
	public final ShapeGenerator collisionShape;
	@Nullable
	public final ShapeGenerator interactionShape;

	private XKDBlockSettings(Builder builder) {
		this.sustainsPlant = builder.sustainsPlant;
		this.glassType = builder.glassType;
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

	public static XKDBlockSettings of(Block block) {
		return ((XKDBlock) block).xkdeco$getSettings();
	}

	public static VoxelShape getGlassFaceShape(BlockState blockState, Direction direction) {
		XKDBlockSettings settings = of(blockState.getBlock());
		if (settings == null) {
			VoxelShape shape = blockState.getShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO, CollisionContext.empty());
			return Block.isShapeFullBlock(shape) ? Shapes.block() : Shapes.empty();
		}
		if (settings.glassType == null) {
			return Shapes.empty();
		}
		VoxelShape shape = blockState.getShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO, CollisionContext.empty());
		if (shape.isEmpty()) {
			return Shapes.empty();
		}
		return Shapes.getFaceShape(shape, direction);
	}

	public static class Builder {
		private boolean sustainsPlant;
		private GlassType glassType;
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

		public Builder glassType(GlassType glassType) {
			this.glassType = glassType;
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
