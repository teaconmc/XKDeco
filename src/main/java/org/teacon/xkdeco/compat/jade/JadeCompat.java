package org.teacon.xkdeco.compat.jade;

import java.util.List;
import java.util.SortedSet;

import org.teacon.xkdeco.XKDeco;
import org.teacon.xkdeco.block.place.PlaceSlot;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;
import snownee.jade.api.config.IPluginConfig;
import snownee.kiwi.loader.Platform;

@WailaPlugin
public class JadeCompat implements IWailaPlugin {
	@Override
	public void registerClient(IWailaClientRegistration registration) {
		if (!Platform.isProduction()) {
			XKDeco.LOGGER.info("Registering debug Jade plugin");
			registration.registerBlockComponent(new DebugProvider(), Block.class);
		}
	}

	public static class DebugProvider implements IBlockComponentProvider {
		public static final ResourceLocation ID = XKDeco.id("debug");

		@Override
		public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
			var side = accessor.getSide();
			List<SortedSet<String>> slots = PlaceSlot.find(accessor.getBlockState(), side).map(PlaceSlot::tags).toList();
			if (slots.isEmpty()) {
				return;
			}
			for (SortedSet<String> slot : slots) {
				tooltip.add(Component.literal(String.join(", ", slot)));
			}
		}

		@Override
		public ResourceLocation getUid() {
			return ID;
		}

		@Override
		public boolean isRequired() {
			return true;
		}
	}
}
