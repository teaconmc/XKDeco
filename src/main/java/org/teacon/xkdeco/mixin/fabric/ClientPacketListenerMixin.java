package org.teacon.xkdeco.mixin.fabric;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.teacon.xkdeco.blockentity.SingleSlotContainerBlockEntity;

import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {

	@Shadow
	@Final
	private RegistryAccess.Frozen registryAccess;

	@Shadow
	public abstract Connection getConnection();

	@Inject(
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/block/entity/BlockEntity;loadWithComponents(Lnet/minecraft/nbt/CompoundTag;Lnet/minecraft/core/HolderLookup$Provider;)V",
					remap = true
			), method = {"method_38542", "lambda$handleBlockEntityData$5"}, cancellable = true, remap = false
	)
	private void xkdeco$handleBlockEntityData(
			ClientboundBlockEntityDataPacket clientboundBlockEntityDataPacket,
			BlockEntity blockEntity,
			CallbackInfo ci) {
		if (blockEntity instanceof SingleSlotContainerBlockEntity) {
			((SingleSlotContainerBlockEntity) blockEntity).onDataPacket(getConnection(), clientboundBlockEntityDataPacket, registryAccess);
			ci.cancel();
		}
	}

}