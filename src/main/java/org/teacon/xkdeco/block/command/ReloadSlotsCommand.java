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
		AtomicInteger counter = new AtomicInteger();
		BuiltInRegistries.BLOCK.holders().forEach(holder -> {
			KBlockDefinition definition = fundamentals.blocks().get(holder.key().location());
			if (definition == null) {
				return;
			}
			if (fundamentals.slotProviders().attachSlots(holder.value(), definition)) {
				counter.incrementAndGet();
			}
		});
		source.sendSuccess(() -> Component.literal("Slots in %d blocks have been reloaded".formatted(counter.get())), false);
		return 1;
	}

}
