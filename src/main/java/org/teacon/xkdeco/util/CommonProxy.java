package org.teacon.xkdeco.util;

import org.teacon.xkdeco.XKDeco;
import org.teacon.xkdeco.block.AirDuctBlock;
import org.teacon.xkdeco.block.ItemDisplayBlock;
import org.teacon.xkdeco.block.MimicWallBlock;
import org.teacon.xkdeco.block.OneDirectionFenceGateBlock;
import org.teacon.xkdeco.block.SpecialSlabBlock;
import org.teacon.xkdeco.block.XKDBlock;
import org.teacon.xkdeco.duck.XKDPlayer;
import org.teacon.xkdeco.init.MimicWallsLoader;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BlockTags;
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
import snownee.kiwi.Mod;
import snownee.kiwi.customization.block.loader.BlockCodecs;
import snownee.kiwi.loader.Platform;
import snownee.kiwi.util.NotNullByDefault;

@Mod(XKDeco.ID)
@NotNullByDefault
public class CommonProxy implements ModInitializer {

	public static boolean isLadder(BlockState blockState, LevelReader world, BlockPos pos) {
		return blockState.is(BlockTags.CLIMBABLE);
	}

	public static SoundEvent getFenceGateSound(FenceGateBlock block, boolean open) {
		return open ? block.type.fenceGateOpen() : block.type.fenceGateClose();
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

	public static void initCodecs() {
		BlockCodecs.register(XKDeco.id("block"), BlockCodecs.simpleCodec(XKDBlock::new));
		BlockCodecs.register(XKDeco.id("special_slab"), SpecialSlabBlock.CODEC);
		BlockCodecs.register(XKDeco.id("one_direction_fence_gate"), OneDirectionFenceGateBlock.CODEC);
		BlockCodecs.register(XKDeco.id("item_display"), ItemDisplayBlock.CODEC);
	}

	public static void onRegistriesFrozen() {
		MimicWallsLoader.addMimicWallBlocks((id, block) -> Registry.register(BuiltInRegistries.BLOCK, id, block));
		MimicWallsLoader.addMimicWallItems((id, item) -> Registry.register(BuiltInRegistries.ITEM, id, item));
	}

	@Override
	public void onInitialize() {
		if (Platform.isPhysicalClient()) {
			ClientProxy.init();
		}

		ItemGroupEvents.modifyEntriesEvent(MimicWallsLoader.STRUCTURE_TAB_KEY).register(entries -> {
			for (Block block : BuiltInRegistries.BLOCK) {
				if (block instanceof MimicWallBlock) {
					entries.accept(block);
				}
			}
		});
	}
}
