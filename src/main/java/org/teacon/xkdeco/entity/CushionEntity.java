package org.teacon.xkdeco.entity;

import java.util.Objects;

import org.teacon.xkdeco.XKDeco;
import org.teacon.xkdeco.init.XKDecoObjects;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public final class CushionEntity extends Entity {
	private static final EntityDataSerializer<Vec3> LOCATION_DATA_SERIALIZER;
	public static final RegistryObject<EntityType<CushionEntity>> TYPE;
	private static final EntityDataAccessor<Vec3> DATA_DIFF_LOCATION;
	static final double MAX_DISTANCE = 3.0;

	static {
		LOCATION_DATA_SERIALIZER = new Vec3Serializer();
		EntityDataSerializers.registerSerializer(LOCATION_DATA_SERIALIZER);
		TYPE = RegistryObject.create(new ResourceLocation(XKDeco.ID, XKDecoObjects.CUSHION_ENTITY), ForgeRegistries.ENTITY_TYPES);
		DATA_DIFF_LOCATION = SynchedEntityData.defineId(CushionEntity.class, LOCATION_DATA_SERIALIZER);
	}

	public CushionEntity(EntityType<CushionEntity> type, Level world) {
		super(type, world);
		this.noPhysics = true;
	}

	public CushionEntity(BlockPos pos, Player player) {
		super(TYPE.get(), player.level());
		this.noPhysics = true;
		this.setPos(pos.getX() + 0.5, pos.getY() + 0.375, pos.getZ() + 0.5);
		this.setStandingDiffLocation(this.calculateStandingDiff(player));
		player.setPos(this.position());
		player.startRiding(this);
	}

	private Vec3 calculateStandingDiff(Entity entity) {
		if (!entity.isPassenger()) {
			var diff = entity.position().add(0.0, 0.5, 0.0).subtract(this.position());
			return diff.lengthSqr() > MAX_DISTANCE ? diff.normalize().scale(Math.sqrt(MAX_DISTANCE)) : diff;
		}
		return Vec3.ZERO;
	}

	public Vec3 getStandingDiffLocation() {
		return this.entityData.get(DATA_DIFF_LOCATION);
	}

	public void setStandingDiffLocation(Vec3 value) {
		this.entityData.set(DATA_DIFF_LOCATION, value);
	}

	@Override
	protected void defineSynchedData() {
		this.entityData.define(DATA_DIFF_LOCATION, Vec3.ZERO);
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag tag) {
		var standingDiffX = tag.getDouble("StandingDiffX");
		var standingDiffY = tag.getDouble("StandingDiffY");
		var standingDiffZ = tag.getDouble("StandingDiffZ");
		this.setStandingDiffLocation(new Vec3(standingDiffX, standingDiffY, standingDiffZ));
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag tag) {
		var standingDiff = this.getStandingDiffLocation();
		tag.putDouble("StandingDiffX", standingDiff.x);
		tag.putDouble("StandingDiffY", standingDiff.y);
		tag.putDouble("StandingDiffZ", standingDiff.z);
	}

	@Override
	public Packet<ClientGamePacketListener> getAddEntityPacket() {
		return new ClientboundAddEntityPacket(this);
	}

	@Override
	public Vec3 getDismountLocationForPassenger(LivingEntity entity) {
		var targetPosition = this.position().add(this.getStandingDiffLocation());
		var targetBelow = new BlockPos((int) targetPosition.x, (int) (targetPosition.y - 1.0), (int) targetPosition.z);
		var canStand = entity.level().getBlockState(targetBelow).isFaceSturdy(entity.level(), targetBelow, Direction.UP);
		return canStand ? targetPosition : targetPosition.add(0.0, 1.0, 0.0);
	}

	@Override
	public void tick() {
		super.tick();
		if (!this.level().isClientSide()) {
			if (this.getPassengers().isEmpty() || !canBlockBeSeated(this.getBlockStateOn())) {
				this.remove(RemovalReason.DISCARDED);
			}
		}
	}

	public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
		var world = event.getLevel();
		if (!world.isClientSide()) {
			var pos = event.getPos();
			var player = event.getEntity();
			if (event.getFace() == Direction.UP && !player.isShiftKeyDown()) {
				var cushions = world.getEntitiesOfClass(CushionEntity.class, new AABB(pos));
				if (cushions.isEmpty() && canBlockBeSeated(world.getBlockState(pos))) {
					world.addFreshEntity(new CushionEntity(pos, player));
				}
			}
		}
	}

	private static boolean canBlockBeSeated(BlockState state) {
		var name = Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(state.getBlock()));
		if (XKDeco.ID.equals(name.getNamespace())) {
			var id = name.getPath();
			return id.contains(XKDecoObjects.CHAIR_SUFFIX) || id.contains(XKDecoObjects.STOOL_SUFFIX);
		}
		return false;
	}

	public static void onBreakBlock(BlockEvent.BreakEvent event) {
		final var world = event.getLevel();
		if (!world.isClientSide()) {
			var pos = event.getPos();
			var cushions = world.getEntitiesOfClass(CushionEntity.class, new AABB(pos));
			for (var cushion : cushions) {
				cushion.remove(RemovalReason.DISCARDED);
			}
		}
	}

	private static final class Vec3Serializer implements EntityDataSerializer<Vec3> {
		@Override
		public void write(FriendlyByteBuf buffer, Vec3 value) {
			buffer.writeDouble(value.x).writeDouble(value.y).writeDouble(value.z);
		}

		@Override
		public Vec3 read(FriendlyByteBuf buffer) {
			return new Vec3(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
		}

		@Override
		public Vec3 copy(Vec3 value) {
			return value;
		}
	}
}
