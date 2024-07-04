package org.teacon.xkdeco.item;

import net.minecraft.world.level.block.Block;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;

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