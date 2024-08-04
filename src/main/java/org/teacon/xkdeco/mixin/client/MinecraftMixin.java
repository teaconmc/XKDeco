package org.teacon.xkdeco.mixin.client;

import java.util.Objects;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.teacon.xkdeco.block.DisplayBlock;
import org.teacon.xkdeco.network.CClickDisplayPacket;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

@Mixin(Minecraft.class)
public class MinecraftMixin {
	@Shadow
	@Nullable
	public HitResult hitResult;

	@Shadow
	@Nullable
	public ClientLevel level;

	@Shadow
	@Nullable
	public LocalPlayer player;

	@Shadow
	protected int missTime;

	@Shadow
	@Nullable
	public MultiPlayerGameMode gameMode;

	@Inject(
			method = "startAttack",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;startDestroyBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;)Z"),
			cancellable = true)
	private void xkdeco$startAttack(CallbackInfoReturnable<Boolean> cir) {
		if (player == null || player.isSpectator()) {
			return;
		}
		Objects.requireNonNull(hitResult);
		Objects.requireNonNull(level);
		BlockHitResult hit = (BlockHitResult) hitResult;
		BlockPos pos = hit.getBlockPos();
		BlockState blockState = level.getBlockState(pos);
		if (!(blockState.getBlock() instanceof DisplayBlock block) || block.canBeDestroyed(blockState, level, pos, player, hit)) {
			return;
		}
		CClickDisplayPacket.send(hit);
		if (player.isCreative()) {
			player.swing(InteractionHand.MAIN_HAND);
			missTime = 10;
			Objects.requireNonNull(gameMode).stopDestroyBlock();
			cir.setReturnValue(false);
		}
	}
}
