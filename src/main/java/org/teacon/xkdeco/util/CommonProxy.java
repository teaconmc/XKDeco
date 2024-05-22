package org.teacon.xkdeco.util;

import org.teacon.xkdeco.XKDeco;
import org.teacon.xkdeco.block.SpecialSlabBlock;
import org.teacon.xkdeco.block.XKDBlock;
import org.teacon.xkdeco.data.XKDDataGen;
import org.teacon.xkdeco.init.MimicWallsLoader;

import javax.annotation.ParametersAreNonnullByDefault;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import snownee.kiwi.customization.block.loader.BlockCodecs;
import snownee.kiwi.loader.Platform;

@Mod(XKDeco.ID)
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class CommonProxy {

	public CommonProxy() {
		var modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		modEventBus.addListener(EventPriority.LOWEST, MimicWallsLoader::addMimicWallBlocks);
		modEventBus.addListener(EventPriority.LOWEST, MimicWallsLoader::addMimicWallItems);
		modEventBus.addListener(MimicWallsLoader::addMimicWallsToTab);
		modEventBus.addListener((GatherDataEvent event) -> {
			FabricDataGenerator dataGenerator = FabricDataGenerator.create(XKDeco.ID, event);
			new XKDDataGen().onInitializeDataGenerator(dataGenerator);
		});

		if (Platform.isPhysicalClient()) {
			ClientProxy.init();
		}

		BlockCodecs.register(XKDeco.id("special_slab"), SpecialSlabBlock.CODEC);
		BlockCodecs.register(XKDeco.id("block"), BlockCodecs.simpleCodec(XKDBlock::new));
	}

	public static boolean isLadder(BlockState blockState, LevelReader world, BlockPos pos) {
		return blockState.isLadder(world, pos, null);
	}
}
