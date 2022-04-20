package org.teacon.xkdeco.block;

public sealed interface XKDecoBlock permits
        BasicBlock, BasicCubeBlock, BasicFullDirectionBlock,
        IsotropicCubeBlock, IsotropicHollowCubeBlock, IsotropicPillarBlock, IsotropicSlabBlock, IsotropicStairBlock,
        PlantLeavesBlock, PlantSlabBlock {
    // nothing here
}
