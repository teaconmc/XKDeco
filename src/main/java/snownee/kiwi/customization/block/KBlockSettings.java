package snownee.kiwi.customization.block;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.ToIntFunction;

import org.jetbrains.annotations.Nullable;
import snownee.kiwi.customization.block.behavior.CanSurviveHandler;
import snownee.kiwi.customization.block.component.DirectionalComponent;
import snownee.kiwi.customization.block.component.HorizontalComponent;
import snownee.kiwi.customization.block.component.KBlockComponent;
import snownee.kiwi.customization.block.component.WaterLoggableComponent;
import snownee.kiwi.customization.placement.PlaceChoices;
import snownee.kiwi.customization.shape.ShapeGenerator;
import snownee.kiwi.customization.duck.KBlockProperties;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
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

public class KBlockSettings {
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
	public PlaceChoices placeChoices;

	private KBlockSettings(Builder builder) {
		this.customPlacement = builder.customPlacement;
		this.glassType = builder.glassType;
		this.shape = builder.shape;
		this.collisionShape = builder.collisionShape;
		this.interactionShape = builder.interactionShape;
		this.canSurviveHandler = builder.canSurviveHandler;
		this.analogOutputSignal = builder.getAnalogOutputSignal();
		this.components = Map.copyOf(builder.components);
//		if (Platform.isPhysicalClient() && XKDecoClientConfig.exportBlocksMore) {
//			if (builder.shapeId != null || builder.collisionShapeId != null || builder.interactionShapeId != null) {
//				ExportBlocksCommand.putMoreInfo(this, new MoreInfo(builder.shapeId, builder.collisionShapeId, builder.interactionShapeId));
//			}
//		}
	}

	public static KBlockSettings empty() {
		return new KBlockSettings(builder());
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
		return ((KBlockProperties) ((BlockBehaviour) block).properties).kiwi$getSettings();
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
		@Nullable
		private GlassType glassType;
		@Nullable
		private ShapeGenerator shape;
		@Nullable
		private ShapeGenerator collisionShape;
		@Nullable
		private ShapeGenerator interactionShape;
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
			((KBlockProperties) properties).kiwi$setSettings(settings);
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

		public Builder component(KBlockComponent component) {
			KBlockComponent before = this.components.put(component.type(), component);
			Preconditions.checkState(before == null, "Component %s is already present", component.type());
			return this;
		}

		public Builder waterLoggable() {
			return component(WaterLoggableComponent.getInstance());
		}

		public Builder horizontal() {
			return component(HorizontalComponent.getInstance(false));
		}

		public Builder directional() {
			return component(DirectionalComponent.getInstance(false));
		}

		public boolean hasComponent(KBlockComponent.Type<?> type) {
			return components.containsKey(type);
		}

		public Builder removeComponent(KBlockComponent.Type<?> type) {
			components.remove(type);
			return this;
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

	@Deprecated
	public record MoreInfo(ResourceLocation shape, ResourceLocation collisionShape, ResourceLocation interactionShape) {
	}
}
