package org.teacon.xkdeco.block.settings;

import java.util.Map;
import java.util.function.UnaryOperator;

import org.jetbrains.annotations.Nullable;
import org.teacon.xkdeco.duck.XKBlockProperties;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class XKBlockSettings {
	public static final XKBlockSettings EMPTY = new XKBlockSettings(builder());
	public final boolean sustainsPlant;
	public final GlassType glassType;
	@Nullable
	public final ShapeGenerator shape;
	@Nullable
	public final ShapeGenerator collisionShape;
	@Nullable
	public final ShapeGenerator interactionShape;
	@Nullable
	public final CanSurviveHandler canSurviveHandler;
	public final Map<XKBlockComponent.Type<?>, XKBlockComponent> components;

	private XKBlockSettings(Builder builder) {
		this.sustainsPlant = builder.sustainsPlant;
		this.glassType = builder.glassType;
		this.shape = builder.shape;
		this.collisionShape = builder.collisionShape;
		this.interactionShape = builder.interactionShape;
		this.canSurviveHandler = builder.canSurviveHandler;
		this.components = Map.copyOf(builder.components);
	}

	public static XKBlockSettings.Builder builder() {
		return new Builder(BlockBehaviour.Properties.of());
	}

	public static XKBlockSettings.Builder copyProperties(Block block) {
		return new Builder(BlockBehaviour.Properties.copy(block));
	}

	public static XKBlockSettings.Builder copyProperties(Block block, MapColor mapColor) {
		return new Builder(BlockBehaviour.Properties.copy(block).mapColor(mapColor));
	}

	public static XKBlockSettings of(Object block) {
		return ((XKBlockProperties) ((BlockBehaviour) block).properties).xkdeco$getSettings();
	}

	public static VoxelShape getGlassFaceShape(BlockState blockState, Direction direction) {
		XKBlockSettings settings = of(blockState.getBlock());
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

	public boolean hasComponent(XKBlockComponent.Type<?> type) {
		return components.containsKey(type);
	}

	public void injectProperties(Block block, StateDefinition.Builder<Block, BlockState> builder) {
		for (XKBlockComponent component : components.values()) {
			component.injectProperties(block, builder);
		}
	}

	public BlockState registerDefaultState(BlockState state) {
		for (XKBlockComponent component : components.values()) {
			state = component.registerDefaultState(state);
		}
		return state;
	}

	public BlockState getStateForPlacement(BlockState state, BlockPlaceContext context) {
		for (XKBlockComponent component : components.values()) {
			state = component.getStateForPlacement(state, context);
			if (state == null || !state.is(state.getBlock())) {
				return state;
			}
		}
		return state;
	}

	public BlockState updateShape(
			BlockState pState,
			Direction pDirection,
			BlockState pNeighborState,
			LevelAccessor pLevel,
			BlockPos pPos,
			BlockPos pNeighborPos) {
		for (XKBlockComponent component : components.values()) {
			pState = component.updateShape(pState, pDirection, pNeighborState, pLevel, pPos, pNeighborPos);
		}
		return pState;
	}

	public BlockState rotate(BlockState pState, Rotation pRotation) {
		for (XKBlockComponent component : components.values()) {
			pState = component.rotate(pState, pRotation);
		}
		return pState;
	}

	public BlockState mirror(BlockState pState, Mirror pMirror) {
		for (XKBlockComponent component : components.values()) {
			pState = component.mirror(pState, pMirror);
		}
		return pState;
	}

	public static class Builder {
		private final BlockBehaviour.Properties properties;
		private boolean sustainsPlant;
		private GlassType glassType;
		@Nullable
		private ShapeGenerator shape;
		@Nullable
		private ShapeGenerator collisionShape;
		@Nullable
		private ShapeGenerator interactionShape;
		@Nullable
		private CanSurviveHandler canSurviveHandler;
		private final Map<XKBlockComponent.Type<?>, XKBlockComponent> components = Maps.newIdentityHashMap();

		private Builder(BlockBehaviour.Properties properties) {
			this.properties = properties;
		}

		public Builder configure(UnaryOperator<BlockBehaviour.Properties> configurator) {
			configurator.apply(properties);
			return this;
		}

		public Builder noOcclusion() {
			properties.noOcclusion();
			return this;
		}

		public Builder noCollission() {
			properties.noCollission();
			return this;
		}

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

		public Builder canSurviveHandler(CanSurviveHandler canSurviveHandler) {
			this.canSurviveHandler = canSurviveHandler;
			return this;
		}

		public Builder component(XKBlockComponent component) {
			XKBlockComponent before = this.components.put(component.type(), component);
			Preconditions.checkState(before == null, "Component %s is already present", component.type());
			return this;
		}

		public Builder waterLoggable() {
			return component(WaterLoggableComponent.getInstance());
		}

		public Builder horizontal() {
			return horizontal(false);
		}

		public Builder horizontal(boolean customPlacement) {
			return component(HorizontalComponent.getInstance(customPlacement));
		}

		public Builder directional() {
			return directional(false);
		}

		public Builder directional(boolean customPlacement) {
			return component(DirectionalComponent.getInstance(customPlacement));
		}

		public BlockBehaviour.Properties get() {
			XKBlockSettings settings = new XKBlockSettings(this);
			((XKBlockProperties) properties).xkdeco$setSettings(settings);
			return properties;
		}

		public boolean hasComponent(XKBlockComponent.Type<?> type) {
			return components.containsKey(type);
		}

		public void removeComponent(XKBlockComponent.Type<?> type) {
			components.remove(type);
		}
	}
}
