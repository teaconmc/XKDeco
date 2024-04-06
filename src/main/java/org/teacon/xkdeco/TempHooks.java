package org.teacon.xkdeco;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.teacon.xkdeco.block.place.PlaceDebugFeature;
import org.teacon.xkdeco.block.place.PlaceSlot;
import org.teacon.xkdeco.block.place.SlotLink;
import org.teacon.xkdeco.util.CommonProxy;

import com.google.common.collect.Lists;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import snownee.kiwi.Kiwi;

public class TempHooks {
	public static BlockState debugPlaceSlotSystem(BlockState blockState, BlockPlaceContext context) {
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
			Kiwi.LOGGER.info("No neighbor slots found at {}", pos);
			return blockState;
		}
		boolean debug = PlaceDebugFeature.isEnabled(context.getPlayer());
		List<BlockState> matchedBlocks = Lists.newArrayList();
		boolean waterLoggable = blockState.hasProperty(BlockStateProperties.WATERLOGGED);
		boolean hasWater = blockState.getValue(BlockStateProperties.WATERLOGGED);
		for (BlockState possibleState : blockState.getBlock().getStateDefinition().getPossibleStates()) {
			if (waterLoggable && hasWater != possibleState.getValue(BlockStateProperties.WATERLOGGED)) {
				continue;
			}
			int interest = getPlacementInterestAt(possibleState, neighborSlots);
			if (interest > 0) {
				matchedBlocks.add(possibleState);
			}
		}
		if (debug) {
			mutable.setWithOffset(pos, Direction.UP);
			if (matchedBlocks.isEmpty()) {
				level.setBlockAndUpdate(mutable.move(Direction.UP), Blocks.BEDROCK.defaultBlockState());
			} else {
				matchedBlocks.stream().skip(1).forEach($ -> level.setBlockAndUpdate(mutable.move(Direction.UP), $));
			}
		}
		return matchedBlocks.isEmpty() ? blockState : matchedBlocks.get(0);
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
