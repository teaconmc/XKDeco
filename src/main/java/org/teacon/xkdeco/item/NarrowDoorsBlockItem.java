package org.teacon.xkdeco.item;

import org.jspecify.annotations.Nullable;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;

public class NarrowDoorsBlockItem extends BlockItem {

	public NarrowDoorsBlockItem(Block pBlock, Properties pProperties) {
		super(pBlock, pProperties);
	}
	
	@Nullable
	@Override
	public BlockPlaceContext updatePlacementContext(BlockPlaceContext pContext) {
		return BlockPlaceContext.at(pContext, pContext.getClickedPos().above(), pContext.getClickedFace());
	}
}