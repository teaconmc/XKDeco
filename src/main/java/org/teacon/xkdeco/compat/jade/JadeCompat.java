package org.teacon.xkdeco.compat.jade;

import org.teacon.xkdeco.XKDeco;
import org.teacon.xkdeco.block.XKDecoBlock;

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
			if (!(accessor.getBlock() instanceof XKDecoBlock.Roof roof)) {
				return;
			}
			var direction = accessor.getPlayer().getDirection().getOpposite();
			var sideHeight = roof.getSideHeight(accessor.getBlockState(), direction);
			tooltip.add(Component.literal("Side Height: %d %d %d".formatted(
					sideHeight.getLeft(),
					sideHeight.getMiddle(),
					sideHeight.getRight())));
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
