package org.teacon.xkdeco.blockentity;

import static org.teacon.xkdeco.init.XKDecoObjects.WARDROBE_BLOCK_ENTITY;

import org.teacon.xkdeco.XKDeco;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class WardrobeBlockEntity extends BlockEntity {
	public static final RegistryObject<BlockEntityType<WardrobeBlockEntity>> TYPE =
			RegistryObject.create(new ResourceLocation(XKDeco.ID, WARDROBE_BLOCK_ENTITY), ForgeRegistries.BLOCK_ENTITY_TYPES);

	public WardrobeBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
		super(TYPE.get(), pWorldPosition, pBlockState);
	}
}
