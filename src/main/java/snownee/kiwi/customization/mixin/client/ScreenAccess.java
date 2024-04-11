package snownee.kiwi.customization.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;

@SuppressWarnings("UnusedReturnValue")
@Mixin(Screen.class)
public interface ScreenAccess {
	@Invoker
	<T extends GuiEventListener & Renderable & NarratableEntry> T callAddRenderableWidget(T pWidget);
}
