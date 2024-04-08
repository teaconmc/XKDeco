package org.teacon.xkdeco.block.command;

import java.util.concurrent.atomic.AtomicInteger;

import org.teacon.xkdeco.block.loader.KBlockDefinition;
import org.teacon.xkdeco.block.place.PlaceChoices;
import org.teacon.xkdeco.block.place.PlaceSlot;
import org.teacon.xkdeco.util.CommonProxy;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;

public class ReloadSlotsCommand {

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands
				.literal("reloadSlots")
				.requires(source -> source.hasPermission(2))
				.executes(ctx -> reloadSlots(ctx.getSource())));
	}

	private static int reloadSlots(CommandSourceStack source) {
		CommonProxy.BlockFundamentals fundamentals = CommonProxy.BlockFundamentals.reload(CommonProxy.collectKiwiPacks());
		AtomicInteger choicesCounter = new AtomicInteger();
		BuiltInRegistries.BLOCK.holders().forEach(holder -> {
			PlaceChoices.setTo(holder.value(), null);
			KBlockDefinition definition = fundamentals.blocks().get(holder.key().location());
			if (definition == null) {
				return;
			}
			fundamentals.slotProviders().attachSlotsA(holder.value(), definition);
			if (fundamentals.placeChoices().attachChoicesA(holder.value(), definition)) {
				choicesCounter.incrementAndGet();
			}
		});
		fundamentals.slotProviders().attachSlotsB();
		choicesCounter.addAndGet(fundamentals.placeChoices().attachChoicesB());
		source.sendSuccess(() -> Component.literal("Slots in %d blocks have been reloaded, using %d providers".formatted(
				PlaceSlot.blockSize(),
				fundamentals.slotProviders().providers().size())), false);
		source.sendSuccess(() -> Component.literal("Place choices in %d blocks have been reloaded, using %d choices".formatted(
				choicesCounter.get(),
				fundamentals.placeChoices().choices().size())), false);
		return 1;
	}

}
