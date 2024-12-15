package org.teacon.xkdeco.util;

import org.teacon.xkdeco.XKDeco;
import org.teacon.xkdeco.data.XKDDataGen;

import net.fabricmc.fabric.impl.datagen.FabricDataGenHelper;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.data.event.GatherDataEvent;

public class ForgeXKDDataGen {
	public static void init(IEventBus modEventBus) {
		modEventBus.addListener((GatherDataEvent event) -> {
			FabricDataGenHelper.runDatagenForMod(XKDeco.ID, XKDeco.ID, new XKDDataGen(), event);
		});
	}
}
