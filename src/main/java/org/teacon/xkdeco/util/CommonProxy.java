package org.teacon.xkdeco.util;

import java.util.List;

import org.teacon.xkdeco.XKDeco;
import org.teacon.xkdeco.block.AirDuctBlock;
import org.teacon.xkdeco.block.ItemDisplayBlock;
import org.teacon.xkdeco.block.MimicWallBlock;
import org.teacon.xkdeco.block.OneDirectionFenceGateBlock;
import org.teacon.xkdeco.block.SpecialSlabBlock;
import org.teacon.xkdeco.block.XKDBlock;
import org.teacon.xkdeco.duck.XKDPlayer;
import org.teacon.xkdeco.init.MimicWallsLoader;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
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
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import snownee.kiwi.customization.block.loader.BlockCodecs;
import snownee.kiwi.loader.Platform;

@Mod(XKDeco.ID)
public class CommonProxy {

	private static final ResourceKey<CreativeModeTab> FURNITURE_TAB_KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB, XKDeco.id("furniture"));
	private static final List<Identifier> HOLOGRAM_ITEMS = List.of(
			XKDeco.id("hologram_base"),
			XKDeco.id("hologram_planet"),
			XKDeco.id("hologram_dna"),
			XKDeco.id("hologram_pictures"),
			XKDeco.id("hologram_message"),
			XKDeco.id("hologram_xekr_logo"));

	public CommonProxy(IEventBus modEventBus) {
		modEventBus.addListener(
				EventPriority.LOWEST, (RegisterEvent event) -> {
					if (event.getRegistryKey().equals(Registries.BLOCK)) {
						MimicWallsLoader.addMimicWalls(event);
					} else if (event.getRegistryKey().equals(Registries.ITEM)) {
						MimicWallsLoader.addMimicWallItems(event);
						ensureHologramBlockItems(event);
					}
				});

		modEventBus.addListener((BuildCreativeModeTabContentsEvent event) -> {
			if (event.getTabKey().equals(MimicWallsLoader.STRUCTURE_TAB_KEY)) {
				for (Block block : BuiltInRegistries.BLOCK) {
					if (block instanceof MimicWallBlock) {
						event.accept(block);
					}
				}
			}
			if (event.getTabKey().equals(FURNITURE_TAB_KEY)) {
				HOLOGRAM_ITEMS.forEach(id -> BuiltInRegistries.ITEM.getOptional(id).ifPresent(item -> acceptIfMissing(event, item)));
			}
		});

		if (Platform.isDataGen()) {
			ForgeXKDDataGen.init(modEventBus);
		}

		BlockCodecs.register(XKDeco.id("block"), Block.simpleCodec(XKDBlock::new));
		BlockCodecs.register(XKDeco.id("special_slab"), SpecialSlabBlock.CODEC);
		BlockCodecs.register(XKDeco.id("one_direction_fence_gate"), OneDirectionFenceGateBlock.CODEC);
		BlockCodecs.register(XKDeco.id("item_display"), ItemDisplayBlock.CODEC);
	}

	private static void acceptIfMissing(BuildCreativeModeTabContentsEvent event, Item item) {
		var parentMissing = event.getParentEntries().stream().noneMatch(stack -> stack.getItem() == item);
		var searchMissing = event.getSearchEntries().stream().noneMatch(stack -> stack.getItem() == item);
		if (parentMissing && searchMissing) {
			event.accept(item);
		} else if (parentMissing) {
			event.accept(item, CreativeModeTab.TabVisibility.PARENT_TAB_ONLY);
		} else if (searchMissing) {
			event.accept(item, CreativeModeTab.TabVisibility.SEARCH_TAB_ONLY);
		}
	}

	private static void ensureHologramBlockItems(RegisterEvent event) {
		for (Identifier id : HOLOGRAM_ITEMS) {
			if (BuiltInRegistries.ITEM.getOptional(id).isPresent()) {
				continue;
			}
			BuiltInRegistries.BLOCK.getOptional(id).ifPresent(block -> event.register(
					Registries.ITEM,
					id,
					() -> new BlockItem(block, new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id)))));
		}
	}

	public static boolean isLadder(BlockState blockState, LevelReader world, BlockPos pos) {
		return blockState.isLadder(world, pos, null);
	}

	public static SoundEvent getFenceGateSound(FenceGateBlock block, boolean open) {
		return open ? block.openSound : block.closeSound;
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
		Direction direction = Direction.getApproximateNearest(pPos.x, 0, pPos.z);
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
