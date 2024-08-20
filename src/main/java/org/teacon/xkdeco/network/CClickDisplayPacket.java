package org.teacon.xkdeco.network;

import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import org.teacon.xkdeco.XKDeco;
import org.teacon.xkdeco.block.DisplayBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import snownee.kiwi.network.KPacketSender;
import snownee.kiwi.network.KiwiPacket;
import snownee.kiwi.network.PayloadContext;
import snownee.kiwi.network.PlayPacketHandler;

@KiwiPacket
public record CClickDisplayPacket(Vec3 location, boolean miss, Direction direction, BlockPos pos, boolean inside) implements CustomPacketPayload {

	public static final Type<CClickDisplayPacket> TYPE = new Type<>(XKDeco.id( "click_display"));

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void send(BlockHitResult hit) {
		KPacketSender.sendToServer(new CClickDisplayPacket(hit.getLocation(), hit.getType() == HitResult.Type.MISS, hit.getDirection(), hit.getBlockPos(), hit.isInside()));
	}

	public static final class Handler implements PlayPacketHandler<CClickDisplayPacket> {

		public static final StreamCodec<RegistryFriendlyByteBuf, CClickDisplayPacket> STREAM_CODEC = StreamCodec.composite(
				ByteBufCodecs.fromCodec(Vec3.CODEC), CClickDisplayPacket::location,
				ByteBufCodecs.BOOL, CClickDisplayPacket::miss,
				Direction.STREAM_CODEC, CClickDisplayPacket::direction,
				BlockPos.STREAM_CODEC, CClickDisplayPacket::pos,
				ByteBufCodecs.BOOL, CClickDisplayPacket::inside,
				CClickDisplayPacket::new
		);

		@Override
		public void handle(CClickDisplayPacket cClickDisplayPacket, PayloadContext payloadContext) {
			var player = payloadContext.serverPlayer();
			var pos = cClickDisplayPacket.pos;
			var hit = cClickDisplayPacket.miss ? BlockHitResult.miss(cClickDisplayPacket.location, cClickDisplayPacket.direction, cClickDisplayPacket.pos)
					: new BlockHitResult(cClickDisplayPacket.location,
					cClickDisplayPacket.direction,
					cClickDisplayPacket.pos,
					cClickDisplayPacket.inside);
			if (player == null || pos.distToCenterSqr(player.position()) > 256) {
				return;
			}
			payloadContext.execute(() -> {
				BlockState blockState = player.level().getBlockState(pos);
				if (blockState.getBlock() instanceof DisplayBlock block) {
					block.click(blockState, player.level(), pos, player, hit);
				}
			});
		}

		@Override
		public StreamCodec<RegistryFriendlyByteBuf, CClickDisplayPacket> streamCodec() {
			return STREAM_CODEC;
		}
	}
}
