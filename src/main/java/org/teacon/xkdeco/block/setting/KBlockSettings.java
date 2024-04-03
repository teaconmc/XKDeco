package org.teacon.xkdeco.block.setting;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.ToIntFunction;

import org.jetbrains.annotations.Nullable;
import org.teacon.xkdeco.XKDeco;
import org.teacon.xkdeco.XKDecoClientConfig;
import org.teacon.xkdeco.block.command.ExportBlocksCommand;
import org.teacon.xkdeco.block.loader.KBlockComponents;
import org.teacon.xkdeco.duck.KBlockProperties;

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
import snownee.kiwi.loader.Platform;
import snownee.kiwi.util.VoxelUtil;

public class KBlockSettings {
	public static final KBlockSettings EMPTY = new KBlockSettings(builder());
	public final boolean customPlacement;
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
	public final Map<KBlockComponent.Type<?>, KBlockComponent> components;

	private KBlockSettings(Builder builder) {
		this.customPlacement = builder.customPlacement;
		this.glassType = builder.glassType;
		this.shape = builder.shape != null ? builder.shape : builder.getShape(builder.shapeId);
		this.collisionShape = builder.collisionShape != null ? builder.collisionShape : builder.getShape(builder.collisionShapeId);
		this.interactionShape = builder.interactionShape != null ? builder.interactionShape : builder.getShape(builder.interactionShapeId);
		this.canSurviveHandler = builder.canSurviveHandler;
		this.analogOutputSignal = builder.getAnalogOutputSignal();
		this.components = Map.copyOf(builder.components);
		if (Platform.isPhysicalClient() && XKDecoClientConfig.exportBlocksMore) {
			if (builder.shapeId != null || builder.collisionShapeId != null || builder.interactionShapeId != null) {
				ExportBlocksCommand.putMoreInfo(this, new MoreInfo(builder.shapeId, builder.collisionShapeId, builder.interactionShapeId));
			}
		}
	}

	public static KBlockSettings.Builder builder() {
		return new Builder(BlockBehaviour.Properties.of());
	}

	public static KBlockSettings.Builder copyProperties(Block block) {
		return new Builder(BlockBehaviour.Properties.copy(block));
	}

	public static KBlockSettings.Builder copyProperties(Block block, MapColor mapColor) {
		return new Builder(BlockBehaviour.Properties.copy(block).mapColor(mapColor));
	}

	public static KBlockSettings of(Object block) {
		return ((KBlockProperties) ((BlockBehaviour) block).properties).xkdeco$getSettings();
	}

	public static VoxelShape getGlassFaceShape(BlockState blockState, Direction direction) {
		KBlockSettings settings = of(blockState.getBlock());
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

	public boolean hasComponent(KBlockComponent.Type<?> type) {
		return components.containsKey(type);
	}

	public void injectProperties(Block block, StateDefinition.Builder<Block, BlockState> builder) {
		for (KBlockComponent component : components.values()) {
			component.injectProperties(block, builder);
		}
	}

	public BlockState registerDefaultState(BlockState state) {
		for (KBlockComponent component : components.values()) {
			state = component.registerDefaultState(state);
		}
		return state;
	}

	public BlockState getStateForPlacement(BlockState state, BlockPlaceContext context) {
		for (KBlockComponent component : components.values()) {
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
		for (KBlockComponent component : components.values()) {
			pState = component.updateShape(pState, pDirection, pNeighborState, pLevel, pPos, pNeighborPos);
		}
		return pState;
	}

	public BlockState rotate(BlockState pState, Rotation pRotation) {
		for (KBlockComponent component : components.values()) {
			pState = component.rotate(pState, pRotation);
		}
		return pState;
	}

	public BlockState mirror(BlockState pState, Mirror pMirror) {
		for (KBlockComponent component : components.values()) {
			pState = component.mirror(pState, pMirror);
		}
		return pState;
	}

	public boolean useShapeForLightOcclusion(BlockState pState) {
		for (KBlockComponent component : components.values()) {
			if (component.useShapeForLightOcclusion(pState)) {
				return true;
			}
		}
		return false;
	}

	@Nullable
	public Boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
		for (KBlockComponent component : components.values()) {
			Boolean result = component.canBeReplaced(state, context);
			if (result != null) {
				return result;
			}
		}
		return null;
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
		private final Map<KBlockComponent.Type<?>, KBlockComponent> components = Maps.newLinkedHashMap();
		@Nullable
		private ToIntFunction<BlockState> analogOutputSignal;

		private Builder(BlockBehaviour.Properties properties) {
			this.properties = properties;
		}

		public BlockBehaviour.Properties get() {
			KBlockSettings settings = new KBlockSettings(this);
			((KBlockProperties) properties).xkdeco$setSettings(settings);
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

		public Builder noCollision() {
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

		public Builder component(KBlockComponent component) {
			KBlockComponent before = this.components.put(component.type(), component);
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

		public boolean hasComponent(KBlockComponent.Type<?> type) {
			return components.containsKey(type);
		}

		public Builder removeComponent(KBlockComponent.Type<?> type) {
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
			if (hasComponent(KBlockComponents.HORIZONTAL.getOrCreate())) {
				return ShapeGenerator.horizontal(shape);
			} else if (hasComponent(KBlockComponents.DIRECTIONAL.getOrCreate())) {
				return ShapeGenerator.directional(shape, state -> state.getValue(BlockStateProperties.FACING));
			} else if (hasComponent(KBlockComponents.MOULDING.getOrCreate())) {
				return ShapeGenerator.moulding(shape);
			} else if (hasComponent(KBlockComponents.FRONT_AND_TOP.getOrCreate())) {
				return ShapeGenerator.directional(shape, JigsawBlock::getFrontFacing);
			} else if (hasComponent(KBlockComponents.HORIZONTAL_AXIS.getOrCreate())) {
				return ShapeGenerator.shifted(
						state -> state.getValue(BlockStateProperties.HORIZONTAL_AXIS) == Direction.Axis.X,
						ShapeGenerator.unit(shape),
						ShapeGenerator.unit(VoxelUtil.rotateHorizontal(shape, Direction.EAST)));
			}
			return ShapeGenerator.unit(shape);
		}

		public @Nullable ToIntFunction<BlockState> getAnalogOutputSignal() {
			if (analogOutputSignal != null) {
				return analogOutputSignal;
			}
			for (KBlockComponent component : components.values()) {
				if (component.hasAnalogOutputSignal()) {
					return component::getAnalogOutputSignal;
				}
			}
			return null;
		}
	}

	public record MoreInfo(ResourceLocation shape, ResourceLocation collisionShape, ResourceLocation interactionShape) {
	}
}
