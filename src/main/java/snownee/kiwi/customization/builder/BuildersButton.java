package snownee.kiwi.customization.builder;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import snownee.kiwi.customization.CustomizationClient;
import snownee.kiwi.customization.block.family.BlockFamilies;
import snownee.kiwi.customization.block.family.BlockFamily;
import snownee.kiwi.customization.block.loader.KHolder;

public class BuildersButton {
	private static boolean builderMode;

	public static boolean isBuilderModeOn() {
		return builderMode;
	}

	public static boolean onLongPress() {
		builderMode = !builderMode;
		return true;
	}

	public static boolean onDoublePress() {
		return false;
	}

	public static boolean onShortPress() {
		Minecraft mc = Minecraft.getInstance();
		if (mc.player == null) {
			return false;
		}
		Screen screen = mc.screen;
		if (screen instanceof ConvertScreen) {
			screen.onClose();
			return true;
		}
		if (screen instanceof AbstractContainerScreen<?> containerScreen && containerScreen.getMenu().getCarried().isEmpty()) {
			Slot slot = CustomizationClient.getSlotUnderMouse(containerScreen);
			if (slot == null || !slot.hasItem() || !slot.allowModification(mc.player)) {
				return false;
			}
			if (screen instanceof CreativeModeInventoryScreen && slot.container != mc.player.getInventory()) {
				return false;
			}
			List<KHolder<BlockFamily>> families = BlockFamilies.findQuickSwitch(slot.getItem().getItem());
			if (families.isEmpty()) {
				return false;
			}
			CustomizationClient.pushScreen(mc, new ConvertScreen(screen, slot, slot.index, families));
			return true;
		}
		if (screen != null) {
			return false;
		}
		List<KHolder<BlockFamily>> families = BlockFamilies.findQuickSwitch(mc.player.getMainHandItem().getItem());
		if (!families.isEmpty()) {
			mc.setScreen(new ConvertScreen(null, null, mc.player.getInventory().selected, families));
			return true;
		}
		families = BlockFamilies.findQuickSwitch(mc.player.getOffhandItem().getItem());
		if (!families.isEmpty()) {
			mc.setScreen(new ConvertScreen(null, null, Inventory.SLOT_OFFHAND, families));
			return true;
		}
		return false;
	}
}
