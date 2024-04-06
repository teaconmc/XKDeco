package org.teacon.xkdeco.block.command;

import java.util.concurrent.atomic.AtomicInteger;

import org.teacon.xkdeco.block.loader.KBlockDefinition;
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
		AtomicInteger slotsCounter = new AtomicInteger();
		AtomicInteger choicesCounter = new AtomicInteger();
		BuiltInRegistries.BLOCK.holders().forEach(holder -> {
			KBlockDefinition definition = fundamentals.blocks().get(holder.key().location());
			if (definition == null) {
				return;
			}
			if (fundamentals.slotProviders().attachSlots(holder.value(), definition)) {
				slotsCounter.incrementAndGet();
			}
			if (fundamentals.placeChoices().attachChoices(holder.value(), definition)) {
				choicesCounter.incrementAndGet();
			}
		});
		source.sendSuccess(() -> Component.literal("Slots in %d blocks have been reloaded, using %d providers".formatted(
				slotsCounter.get(),
				fundamentals.slotProviders().providers().size())), true);
		source.sendSuccess(() -> Component.literal("Place choices in %d blocks have been reloaded, using %d choices".formatted(
				choicesCounter.get(),
				fundamentals.placeChoices().choices().size())), true);
		return 1;
	}

}
