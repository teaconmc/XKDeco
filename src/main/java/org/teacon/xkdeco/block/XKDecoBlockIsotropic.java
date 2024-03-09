package org.teacon.xkdeco.block;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

// isotropic blocks which are directionless or uv locked (stairs, slabs, or pillars)
sealed public interface XKDecoBlockIsotropic permits
		IsotropicCubeBlock, IsotropicHollowBlock, IsotropicPillarBlock, IsotropicSlabBlock, IsotropicStairBlock {
	/**
	 * @return true if the block should be considered as "glass"; false otherwise.
	 */
	boolean isGlass();

	/**
	 * Return the static shape of the block, without knowing the current Level info.
	 *
	 * @param state Current block state
	 * @return The shape of the block under given state
	 */
	VoxelShape getShapeStatic(BlockState state);
}
