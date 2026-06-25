package org.teacon.xkdeco.mixin;

import java.util.List;
import java.util.Set;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

public class MixinPlugin implements IMixinConfigPlugin {
	private static final String REI_CRAFTING_DISPLAY_MIXIN = "org.teacon.xkdeco.mixin.rei.DefaultCraftingDisplayMixin";
	private static final String REI_CRAFTING_DISPLAY = "me.shedaniel.rei.plugin.common.displays.crafting.DefaultCraftingDisplay";

	@Override
	public void onLoad(String mixinPackage) {
	}

	@Override
	public String getRefMapperConfig() {
		return null;
	}

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		if (REI_CRAFTING_DISPLAY_MIXIN.equals(mixinClassName)) {
			return classExists(REI_CRAFTING_DISPLAY);
		}
		return true;
	}

	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

	}

	@Override
	public List<String> getMixins() {
		return null;
	}

	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

	}

	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

	}

	private static boolean classExists(String className) {
		try {
			Class.forName(className, false, MixinPlugin.class.getClassLoader());
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}
}
