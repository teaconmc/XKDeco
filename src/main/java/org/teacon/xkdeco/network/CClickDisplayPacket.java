package org.teacon.xkdeco.network;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import org.jetbrains.annotations.Nullable;
import org.teacon.xkdeco.block.DisplayBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import snownee.kiwi.network.KiwiPacket;
import snownee.kiwi.network.PacketHandler;

@KiwiPacket(value = "click_display", dir = KiwiPacket.Direction.PLAY_TO_SERVER)
public class CClickDisplayPacket extends PacketHandler {
	public static CClickDisplayPacket I;

	public static void send(BlockHitResult hit) {
		I.sendToServer(buf -> {
			buf.writeBlockHitResult(hit);
		});
	}

	@Override
	public CompletableFuture<FriendlyByteBuf> receive(
			Function<Runnable, CompletableFuture<FriendlyByteBuf>> executor,
			FriendlyByteBuf buf,
			@Nullable ServerPlayer player) {
		BlockHitResult hit = buf.readBlockHitResult();
		BlockPos pos = hit.getBlockPos();
		if (player == null || pos.distToCenterSqr(player.position()) > 256) {
			return null;
		}
		return executor.apply(() -> {
			BlockState blockState = player.level().getBlockState(pos);
			if (blockState.getBlock() instanceof DisplayBlock block) {
				block.click(blockState, player.level(), pos, player, hit);
			}
		});
	}
}
