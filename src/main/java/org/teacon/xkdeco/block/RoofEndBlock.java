package org.teacon.xkdeco.block;

import java.util.Locale;

import org.teacon.xkdeco.util.RoofUtil;
import org.teacon.xkdeco.util.RoofUtil.RoofShape;

import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import snownee.kiwi.customization.block.StringProperty;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public final class RoofEndBlock extends HorizontalDirectionalBlock implements SimpleWaterloggedBlock {
	private static final StringProperty VARIANT = XKDStateProperties.ROOF_VARIANT;
	private static final StringProperty HALF = XKDStateProperties.HALF;

	public RoofEndBlock(Properties properties) {
		super(properties);
	}

	@Override
	@SuppressWarnings("deprecation")
	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		var facing = pState.getValue(HorizontalDirectionalBlock.FACING);
		var roofHalf = RoofUtil.RoofHalf.valueOf(pState.getValue(HALF).toUpperCase(Locale.ENGLISH));
		var roofVariant = RoofUtil.RoofVariant.valueOf(pState.getValue(VARIANT).toUpperCase(Locale.ENGLISH));
		return RoofUtil.getShape(RoofShape.STRAIGHT, facing, roofHalf, roofVariant);
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {
		return false;
	}
}
