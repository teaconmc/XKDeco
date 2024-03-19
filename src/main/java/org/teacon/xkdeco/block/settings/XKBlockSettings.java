package org.teacon.xkdeco.block.settings;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.ToIntFunction;

import org.jetbrains.annotations.Nullable;
import org.teacon.xkdeco.XKDeco;
import org.teacon.xkdeco.duck.XKBlockProperties;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import snownee.kiwi.KiwiModule;
import snownee.kiwi.loader.Platform;
import snownee.kiwi.util.VoxelUtil;

public class XKBlockSettings {
	public static final XKBlockSettings EMPTY = new XKBlockSettings(builder());
	public final boolean customPlacement;
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
	@Nullable
	public final ToIntFunction<BlockState> analogOutputSignal;
	public final Map<XKBlockComponent.Type<?>, XKBlockComponent> components;

	private XKBlockSettings(Builder builder) {
		this.customPlacement = builder.customPlacement;
		this.sustainsPlant = builder.sustainsPlant;
		this.glassType = builder.glassType;
		this.shape = builder.shape != null ? builder.shape : builder.getShape(builder.shapeId);
		this.collisionShape = builder.collisionShape != null ? builder.collisionShape : builder.getShape(builder.collisionShapeId);
		this.interactionShape = builder.interactionShape != null ? builder.interactionShape : builder.getShape(builder.interactionShapeId);
		this.canSurviveHandler = builder.canSurviveHandler;
		this.analogOutputSignal = builder.getAnalogOutputSignal();
		this.components = Map.copyOf(builder.components);
		if (Platform.isPhysicalClient()) {
			KiwiModule.RenderLayer.Layer renderType = builder.renderType;
			if (renderType == null && glassType != null) {
				renderType = glassType.renderType();
			}
			BlockRenderSettings renderSettings = new BlockRenderSettings(renderType);
			BlockRenderSettings.putSettings(this, renderSettings);
		}
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
			state = component.getStateForPlacement(this, state, context);
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

	public boolean useShapeForLightOcclusion(BlockState pState) {
		for (XKBlockComponent component : components.values()) {
			if (component.useShapeForLightOcclusion(pState)) {
				return true;
			}
		}
		return false;
	}

	public static class Builder {
		private final BlockBehaviour.Properties properties;
		private boolean customPlacement;
		private boolean sustainsPlant;
		@Nullable
		private GlassType glassType;
		@Nullable
		private ShapeGenerator shape;
		@Nullable
		private ResourceLocation shapeId;
		@Nullable
		private ShapeGenerator collisionShape;
		@Nullable
		private ResourceLocation collisionShapeId;
		@Nullable
		private ShapeGenerator interactionShape;
		@Nullable
		private ResourceLocation interactionShapeId;
		@Nullable
		private CanSurviveHandler canSurviveHandler;
		private final Map<XKBlockComponent.Type<?>, XKBlockComponent> components = Maps.newLinkedHashMap();
		@Nullable
		private KiwiModule.RenderLayer.Layer renderType;
		@Nullable
		private ToIntFunction<BlockState> analogOutputSignal;

		private Builder(BlockBehaviour.Properties properties) {
			this.properties = properties;
		}

		public BlockBehaviour.Properties get() {
			XKBlockSettings settings = new XKBlockSettings(this);
			((XKBlockProperties) properties).xkdeco$setSettings(settings);
			return properties;
		}

		public Builder configure(Consumer<BlockBehaviour.Properties> configurator) {
			configurator.accept(properties);
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

		public Builder customPlacement() {
			this.customPlacement = true;
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

		public Builder shape(ResourceLocation shapeId) {
			this.shapeId = shapeId;
			return this;
		}

		public Builder collisionShape(ResourceLocation shapeId) {
			this.collisionShapeId = shapeId;
			return this;
		}

		public Builder interactionShape(ResourceLocation shapeId) {
			this.interactionShapeId = shapeId;
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
			return component(HorizontalComponent.getInstance());
		}

		public Builder directional() {
			return component(DirectionalComponent.getInstance());
		}

		public Builder horizontalAxis() {
			return component(HorizontalAxisComponent.getInstance());
		}

		public boolean hasComponent(XKBlockComponent.Type<?> type) {
			return components.containsKey(type);
		}

		public Builder removeComponent(XKBlockComponent.Type<?> type) {
			components.remove(type);
			return this;
		}

		@Nullable
		public ShapeGenerator getShape(ResourceLocation shapeId) {
			if (shapeId == null) {
				return null;
			}
			VoxelShape shape = ShapeStorage.getInstance().get(shapeId);
//			Preconditions.checkNotNull(shape, "Shape %s is not registered", shapeId);
			if (shape == null) {
				shape = Shapes.block();
				XKDeco.LOGGER.warn("Shape {} is not registered", shapeId);
			}
			if (hasComponent(HorizontalComponent.TYPE)) {
				return ShapeGenerator.horizontal(shape);
			} else if (hasComponent(DirectionalComponent.TYPE)) {
				return ShapeGenerator.directional(shape, state -> state.getValue(BlockStateProperties.FACING));
			} else if (hasComponent(MouldingComponent.TYPE)) {
				return ShapeGenerator.moulding(shape);
			} else if (hasComponent(FrontAndTopComponent.TYPE)) {
				return ShapeGenerator.directional(shape, JigsawBlock::getFrontFacing);
			} else if (hasComponent(HorizontalAxisComponent.TYPE)) {
				return ShapeGenerator.shifted(
						state -> state.getValue(BlockStateProperties.HORIZONTAL_AXIS) == Direction.Axis.X,
						ShapeGenerator.unit(shape),
						ShapeGenerator.unit(VoxelUtil.rotateHorizontal(shape, Direction.EAST)));
			}
			return ShapeGenerator.unit(shape);
		}

		public Builder renderType(KiwiModule.RenderLayer.Layer renderType) {
			this.renderType = renderType;
			return this;
		}

		public @Nullable ToIntFunction<BlockState> getAnalogOutputSignal() {
			if (analogOutputSignal != null) {
				return analogOutputSignal;
			}
			for (XKBlockComponent component : components.values()) {
				if (component.hasAnalogOutputSignal()) {
					return component::getAnalogOutputSignal;
				}
			}
			return null;
		}
	}
}
