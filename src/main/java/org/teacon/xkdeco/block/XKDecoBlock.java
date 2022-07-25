package org.teacon.xkdeco.block;

public sealed interface XKDecoBlock permits XKDecoBlock.Basic, XKDecoBlock.Isotropic, XKDecoBlock.Plant, XKDecoBlock.Special {
    // basic blocks which have directions
    sealed interface Basic extends XKDecoBlock permits BasicBlock, BasicCubeBlock, BasicFullDirectionBlock {
        // nothing here
    }

    // isotropic blocks which are directionless or uv locked (stairs, slabs, or pillars)
    sealed interface Isotropic extends XKDecoBlock permits IsotropicCubeBlock, IsotropicHollowBlock, IsotropicPillarBlock, IsotropicRoofBlock, IsotropicSlabBlock, IsotropicStairBlock {
        // nothing here
    }

    // plant blocks which are related to grass and leaves
    sealed interface Plant extends XKDecoBlock permits PlantLeavesBlock, PlantSlabBlock {
        // nothing here
    }

    // indexed blocks which have an index respectively
    sealed interface Special extends XKDecoBlock permits SpecialBlockDisplayBlock, SpecialCupBlock, SpecialDessertBlock,
            SpecialItemDisplayBlock, SpecialRoofRidgeBlock, SpecialWallBlock, SpecialWardrobeBlock {
        // nothing here
    }
}
