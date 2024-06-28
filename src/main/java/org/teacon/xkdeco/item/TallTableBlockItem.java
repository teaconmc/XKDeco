package org.teacon.xkdeco.item;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;


public class TallTableBlockItem extends BlockItem {

	public TallTableBlockItem(Block pBlock, Properties pProperties) {
		super(pBlock, pProperties);
	}

	@Nullable
	@Override
	public BlockPlaceContext updatePlacementContext(BlockPlaceContext pContext) {
		return BlockPlaceContext.at(pContext, pContext.getClickedPos().above(), pContext.getClickedFace());
	}
}