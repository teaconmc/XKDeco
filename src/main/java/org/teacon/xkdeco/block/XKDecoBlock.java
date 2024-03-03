package org.teacon.xkdeco.block;

import java.util.Optional;

import org.teacon.xkdeco.util.IntTriple;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

public sealed interface XKDecoBlock permits
		XKDecoBlock.Basic, XKDecoBlock.Isotropic, XKDecoBlock.Plant, XKDecoBlock.Roof, XKDecoBlock.Special {
	// basic blocks which have directions
	sealed interface Basic extends XKDecoBlock permits
			BasicBlock, BasicCubeBlock, BasicFullDirectionBlock {
		// nothing here
	}

	// isotropic blocks which are directionless or uv locked (stairs, slabs, or pillars)
	sealed interface Isotropic extends XKDecoBlock permits
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

	// roof related blocks which have complex connection logic
	sealed interface Roof extends XKDecoBlock permits
			RoofBlock, RoofEaveBlock, RoofEndBlock, RoofFlatBlock, RoofRidgeBlock {
		Iterable<BlockState> getPlacementChoices(boolean waterlogged, boolean updateSide, Direction... lookingSides);

		Optional<BlockState> getUpdateShapeChoice(BlockState state, Direction side);

		IntTriple getSideHeight(BlockState state, Direction horizontalSide);
	}

	// plant blocks which are related to grass and leaves
	sealed interface Plant extends XKDecoBlock permits
			PlantLeavesBlock, PlantSlabBlock, PlantLeavesShatterBlock {
		// nothing here
	}

	// indexed blocks which have an index respectively
	sealed interface Special extends XKDecoBlock permits
			SpecialBlockDisplayBlock, SpecialCupBlock, SpecialDessertBlock,
			SpecialItemDisplayBlock, SpecialWallBlock, SpecialWardrobeBlock, SpecialLightBar, SpecialConsole {
		// nothing here
	}
}
