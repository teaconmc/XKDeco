package snownee.kiwi.customization.command;

import java.util.concurrent.atomic.AtomicInteger;

import snownee.kiwi.customization.block.loader.KBlockDefinition;
import snownee.kiwi.customization.place.PlaceChoices;
import snownee.kiwi.customization.place.PlaceSlot;
import org.teacon.xkdeco.util.CommonProxy;

import com.google.common.base.Stopwatch;
import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import snownee.kiwi.Kiwi;

public class ReloadSlotsCommand {

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands
				.literal("reloadSlots")
				.requires(source -> source.hasPermission(2))
				.executes(ctx -> reloadSlots(ctx.getSource())));
	}

	private static int reloadSlots(CommandSourceStack source) {
		Stopwatch stopwatch = Stopwatch.createStarted();
		CommonProxy.BlockFundamentals fundamentals = CommonProxy.BlockFundamentals.reload(CommonProxy.collectKiwiPacks(), false);
		long parseTime = stopwatch.elapsed().toMillis();
		stopwatch.reset().start();
		int choicesCount = reloadSlots(fundamentals);
		long attachTime = stopwatch.elapsed().toMillis();
		Kiwi.LOGGER.info("Parse time %dms + Attach time %dms = %dms".formatted(parseTime, attachTime, parseTime + attachTime));
		source.sendSuccess(() -> Component.literal("Slots in %d blocks, %d block states have been reloaded, using %d providers, ".formatted(
				PlaceSlot.blockCount(),
				fundamentals.slotProviders().slots().size(),
				fundamentals.slotProviders().providers().size())), false);
		source.sendSuccess(() -> Component.literal("Place choices in %d blocks have been reloaded, using %d providers".formatted(
				choicesCount,
				fundamentals.placeChoices().choices().size())), false);
		return 1;
	}

	public static int reloadSlots(CommonProxy.BlockFundamentals fundamentals) {
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
		fundamentals.slotLinks().finish();
		return choicesCounter.get();
	}

}
