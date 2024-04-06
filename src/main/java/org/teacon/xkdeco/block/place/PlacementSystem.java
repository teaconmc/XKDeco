package org.teacon.xkdeco.block.place;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.teacon.xkdeco.XKDeco;
import org.teacon.xkdeco.block.setting.KBlockSettings;
import org.teacon.xkdeco.util.CommonProxy;

import com.google.common.collect.Lists;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
		List<PlaceMatchResult> matchedBlocks = Lists.newArrayList();
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
			int interest = getPlacementInterestAt(possibleState, neighborSlots);
			if (interest > 0) {
				int bonusInterest = 0;
				if (choices != null) {
					bonusInterest = choices.test(blockState, possibleState);
					if (bonusInterest == Integer.MIN_VALUE) {
						continue;
					}
				}
				matchedBlocks.add(new PlaceMatchResult(possibleState, interest + bonusInterest));
			}
		}
		matchedBlocks.sort(null);
		if (debug && !level.isClientSide) {
			mutable.setWithOffset(pos, Direction.UP);
			if (matchedBlocks.isEmpty()) {
				Kiwi.LOGGER.info("No match");
				level.setBlockAndUpdate(mutable.move(Direction.UP), Blocks.BEDROCK.defaultBlockState());
			} else {
				Kiwi.LOGGER.info("Interest: %d".formatted(matchedBlocks.get(0).interest()));
				matchedBlocks.stream().skip(1).forEach($ -> level.setBlockAndUpdate(mutable.move(Direction.UP), $.blockState()));
			}
		}
		return matchedBlocks.isEmpty() ? blockState : matchedBlocks.get(0).blockState();
	}

	//TODO cache based on BlockStates and Direction, see Block.OCCLUSION_CACHE
	public static int getPlacementInterestAt(BlockState blockState, Map<Direction, Collection<PlaceSlot>> neighborSlots) {
		int interest = 0;
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
			for (PlaceSlot ourSlot : ourSlots) {
				for (PlaceSlot theirSlot : theirSlots) {
					int thisInterest = SlotLink.getInterest(ourSlot, theirSlot);
					if (thisInterest != 0) {
						maxInterest = Math.max(maxInterest, thisInterest);
						break;
					}
				}
			}
			interest += maxInterest;
		}
		return interest;
	}
}
