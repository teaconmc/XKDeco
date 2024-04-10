package snownee.kiwi.customization.command;

import com.google.common.base.Stopwatch;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import snownee.kiwi.Kiwi;
import snownee.kiwi.customization.CustomizationHooks;
import snownee.kiwi.customization.block.family.BlockFamilies;

public class ReloadFamiliesCommand {

	public static void register(LiteralArgumentBuilder<CommandSourceStack> builder) {
		builder.then(Commands
				.literal("families")
				.executes(ctx -> reloadFamilies(ctx.getSource())));
	}

	private static int reloadFamilies(CommandSourceStack source) {
		Stopwatch stopwatch = Stopwatch.createStarted();
		int count = BlockFamilies.reload(CustomizationHooks.collectKiwiPacks());
		long reloadTime = stopwatch.elapsed().toMillis();
		Kiwi.LOGGER.info("Reload time: %dms".formatted(reloadTime));
		source.sendSuccess(() -> Component.literal("%d block families have been reloaded".formatted(count)), false);
		return 1;
	}
}
