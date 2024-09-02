package org.teacon.xkdeco.util;

import org.teacon.xkdeco.XKDeco;
import org.teacon.xkdeco.block.AirDuctBlock;
import org.teacon.xkdeco.block.ItemDisplayBlock;
import org.teacon.xkdeco.block.OneDirectionFenceGateBlock;
import org.teacon.xkdeco.block.SpecialSlabBlock;
import org.teacon.xkdeco.block.XKDBlock;
import org.teacon.xkdeco.duck.XKDPlayer;
import org.teacon.xkdeco.init.MimicWallsLoader;
import org.teacon.xkdeco.mixin.forge.FenceGateBlockAccess;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import snownee.kiwi.customization.block.loader.BlockCodecs;

@Mod(XKDeco.ID)
public class CommonProxy {

	public CommonProxy(IEventBus modEventBus) {
		modEventBus.addListener(EventPriority.LOWEST, MimicWallsLoader::addMimicWallBlocks);
		modEventBus.addListener(EventPriority.LOWEST, MimicWallsLoader::addMimicWallItems);
		modEventBus.addListener(MimicWallsLoader::addMimicWallsToTab);
		//modEventBus.addListener((GatherDataEvent event) -> {
		//	FabricDataGenerator dataGenerator = FabricDataGenerator.create(XKDeco.ID, event);
		//	new XKDDataGen().onInitializeDataGenerator(dataGenerator);
		//});

		BlockCodecs.register(XKDeco.id("block"), Block.simpleCodec(XKDBlock::new));
		BlockCodecs.register(XKDeco.id("special_slab"), SpecialSlabBlock.CODEC);
		BlockCodecs.register(XKDeco.id("one_direction_fence_gate"), OneDirectionFenceGateBlock.CODEC);
		BlockCodecs.register(XKDeco.id("item_display"), ItemDisplayBlock.CODEC);
	}

	public static boolean isLadder(BlockState blockState, LevelReader world, BlockPos pos) {
		return blockState.isLadder(world, pos, null);
	}

	public static SoundEvent getFenceGateSound(FenceGateBlock block, boolean open) {
		return open ? ((FenceGateBlockAccess) block).getOpenSound() : ((FenceGateBlockAccess) block).getCloseSound();
	}

	public static void moveEntity(XKDPlayer player, Entity entity, Vec3 pPos) {
		if (entity.noPhysics || !entity.horizontalCollision || entity.minorHorizontalCollision) {
			return;
		}
		if (!entity.hasPose(Pose.STANDING) && !entity.hasPose(Pose.CROUCHING) && !entity.hasPose(Pose.SWIMMING)) {
			return;
		}
		AABB box = entity.getBoundingBox();
		if (Mth.floor(box.maxX) != Mth.floor(box.minX) && Mth.floor(box.maxZ) != Mth.floor(box.minZ)) {
			return;
		}
		Direction direction = Direction.getNearest(pPos.x, 0, pPos.z);
		if (Direction.Axis.Y.test(direction)) {
			return;
		}
		Level level = entity.level();
		BlockPos pos = entity.blockPosition().relative(direction);
		BlockState blockState = level.getBlockState(pos);
		if (blockState.is(XKDBlock.AIR_DUCTS) && AirDuctBlock.isAirDuctSlot(blockState, direction.getOpposite()) &&
				!blockState.isFaceSturdy(level, pos, direction.getOpposite())) {
			player.xkdeco$collideWithAirDuctHorizontally();
		}
	}
}
