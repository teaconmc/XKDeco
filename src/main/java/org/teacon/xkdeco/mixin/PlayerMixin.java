package org.teacon.xkdeco.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.teacon.xkdeco.duck.KPlayer;

import net.minecraft.world.entity.player.Player;

@Mixin(Player.class)
public class PlayerMixin implements KPlayer {
	@Unique
	private int placeCount;

	@Override
	public void kiwi$setPlaceCount(int i) {
		this.placeCount = i;
	}

	@Override
	public int kiwi$getPlaceCount() {
		return placeCount;
	}
}
