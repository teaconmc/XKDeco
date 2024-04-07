package org.teacon.xkdeco.block.place;

import java.util.BitSet;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.jetbrains.annotations.Nullable;
import org.teacon.xkdeco.XKDeco;
import org.teacon.xkdeco.block.setting.KBlockSettings;
import org.teacon.xkdeco.util.CommonProxy;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import snownee.kiwi.Kiwi;

public class PlacementSystem {
	private static final Cache<BlockPlaceContext, PlaceMatchResult> RESULT_CONTEXT = CacheBuilder.newBuilder().weakKeys().expireAfterWrite(
			100,
			TimeUnit.MILLISECONDS).build();

	public static boolean isDebugEnabled(Player player) {
		return player != null && player.isCreative() && player.getOffhandItem().is(Items.CHAINMAIL_HELMET);
	}

	public static void removeDebugBlocks(Level level, BlockPos start) {
		BlockPos.MutableBlockPos pos = start.mutable();
		pos.move(Direction.UP, 2);
		while (isBlockToRemove(level.getBlockState(pos))) {
			level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
			pos.move(Direction.UP);
		}
	}

	private static boolean isBlockToRemove(BlockState blockState) {
		if (blockState.is(Blocks.BEDROCK)) {
			return true;
		}
		String namespace = BuiltInRegistries.BLOCK.getKey(blockState.getBlock()).getNamespace();
		return XKDeco.ID.equals(namespace);
	}

	public static BlockState onPlace(BlockState blockState, BlockPlaceContext context) {
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		BlockPos.MutableBlockPos mutable = pos.mutable();
		var neighborSlots = Direction.stream()
				.map($ -> PlaceSlot.find(level.getBlockState(mutable.setWithOffset(pos, $)), $.getOpposite()))
				.filter(Predicate.not(Collection::isEmpty))
				.collect(Collectors.toUnmodifiableMap(
						$ -> $.iterator().next().side().getOpposite(),
						Function.identity()
				));
		if (neighborSlots.isEmpty()) {
			return blockState;
		}
		boolean debug = isDebugEnabled(context.getPlayer());
		List<PlaceMatchResult> results = Lists.newArrayList();
		boolean waterLoggable = blockState.hasProperty(BlockStateProperties.WATERLOGGED);
		boolean hasWater = waterLoggable && blockState.getValue(BlockStateProperties.WATERLOGGED);
		PlaceChoices choices = null;
		KBlockSettings settings = KBlockSettings.of(blockState.getBlock());
		if (settings != null) {
			choices = settings.placeChoices;
		}
		for (BlockState possibleState : blockState.getBlock().getStateDefinition().getPossibleStates()) {
			if (waterLoggable && hasWater != possibleState.getValue(BlockStateProperties.WATERLOGGED)) {
				continue;
			}
			int bonusInterest = 0;
			if (choices != null) {
				bonusInterest = choices.test(blockState, possibleState);
				if (bonusInterest == Integer.MIN_VALUE) {
					continue;
				}
			}
			PlaceMatchResult result = getPlaceMatchResultAt(possibleState, neighborSlots, bonusInterest);
			if (result != null) {
				results.add(result);
			}
		}
		if (results.isEmpty()) {
			if (debug && !level.isClientSide) {
				Kiwi.LOGGER.info("No match");
				level.setBlockAndUpdate(mutable.move(Direction.UP), Blocks.BEDROCK.defaultBlockState());
			}
			return blockState;
		}
		results.sort(null);
		int resultIndex = 0;
		if (results.size() > 1 && context.getPlayer() != null) {
			int maxInterest = results.get(0).interest();
			for (int i = 1; i < results.size(); i++) {
				if (results.get(i).interest() < maxInterest) {
					break;
				}
				resultIndex = i;
			}
			if (resultIndex > 0) {
				//TODO randomize
			}
		}
		PlaceMatchResult result = results.get(resultIndex);
		if (debug && !level.isClientSide) {
			mutable.setWithOffset(pos, Direction.UP);
			Kiwi.LOGGER.info("Interest: %d".formatted(result.interest()));
			results.forEach($ -> {
				if ($ == result) {
					return;
				}
				level.setBlockAndUpdate(mutable.move(Direction.UP), $.blockState());
				Kiwi.LOGGER.info("Alt Interest: %d : %s".formatted($.interest(), $.blockState()));
			});
		}
		blockState = result.blockState();
		for (int i = 0; i < result.links().size(); i++) {
			SlotLink link = result.links().get(i);
			boolean isUpright = result.uprightStatus().get(i);
			SlotLink.ResultAction action = isUpright ? link.onLinkFrom() : link.onLinkTo();
			blockState = action.apply(blockState);
		}
		RESULT_CONTEXT.put(context, result);
		return blockState;
	}

	//TODO cache based on BlockStates and Direction, see Block.OCCLUSION_CACHE
	@Nullable
	public static PlaceMatchResult getPlaceMatchResultAt(
			BlockState blockState,
			Map<Direction, Collection<PlaceSlot>> neighborSlots,
			int bonusInterest) {
		int interest = 0;
		List<SlotLink> links = null;
		List<Vec3i> offsets = null;
		BitSet uprightStatus = null;
		for (Direction side : CommonProxy.DIRECTIONS) {
			Collection<PlaceSlot> theirSlots = neighborSlots.get(side);
			if (theirSlots == null) {
				continue;
			}
			Collection<PlaceSlot> ourSlots = PlaceSlot.find(blockState, side);
			if (ourSlots.isEmpty()) {
				continue;
			}
			int maxInterest = 0;
			SlotLink matchedLink = null;
			Vec3i offset = null;
			boolean isUpright = false;
			for (PlaceSlot ourSlot : ourSlots) {
				for (PlaceSlot theirSlot : theirSlots) {
					SlotLink link = SlotLink.find(ourSlot, theirSlot);
					if (link != null && link.interest() > maxInterest && link.matches(ourSlot, theirSlot)) {
						maxInterest = link.interest();
						matchedLink = link;
						offset = ourSlot.side().getNormal();
						isUpright = SlotLink.isUprightLink(ourSlot, theirSlot);
					}
				}
			}
			interest += maxInterest;
			if (matchedLink != null) {
				if (links == null) {
					links = Lists.newArrayListWithExpectedSize(neighborSlots.size());
					offsets = Lists.newArrayListWithExpectedSize(neighborSlots.size());
					uprightStatus = new BitSet(neighborSlots.size());
				}
				links.add(matchedLink);
				offsets.add(offset);
				uprightStatus.set(links.size() - 1, isUpright);
			}
		}
		if (interest <= 0) {
			return null;
		}
		return new PlaceMatchResult(blockState, interest + bonusInterest, links, offsets, uprightStatus);
	}

	public static void onBlockPlaced(BlockPlaceContext context) {
		PlaceMatchResult result = RESULT_CONTEXT.getIfPresent(context);
		if (result != null) {
			RESULT_CONTEXT.invalidate(context);
			BlockPos.MutableBlockPos mutable = context.getClickedPos().mutable();
			for (int i = 0; i < result.links().size(); i++) {
				SlotLink link = result.links().get(i);
				boolean isUpright = result.uprightStatus().get(i);
				SlotLink.ResultAction action = isUpright ? link.onLinkTo() : link.onLinkFrom();
				BlockPos theirPos = mutable.setWithOffset(context.getClickedPos(), result.offsets().get(i));
				BlockState theirState = context.getLevel().getBlockState(theirPos);
				theirState = action.apply(theirState);
				context.getLevel().setBlock(theirPos, theirState, 11);
			}
		}
	}
}
