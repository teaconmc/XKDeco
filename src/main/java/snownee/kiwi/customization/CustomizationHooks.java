package snownee.kiwi.customization;

import org.jetbrains.annotations.Nullable;
import snownee.kiwi.customization.block.GlassType;
import snownee.kiwi.customization.block.KBlockSettings;
import org.teacon.xkdeco.util.CommonProxy;

import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class CustomizationHooks {
	private CustomizationHooks() {
	}

	// a custom implementation of the Block.shouldRenderFace
	private static final int CACHE_SIZE = 512;
	private static final ThreadLocal<Object2ByteLinkedOpenHashMap<Block.BlockStatePairKey>> OCCLUSION_CACHE = ThreadLocal.withInitial(() -> {
		Object2ByteLinkedOpenHashMap<Block.BlockStatePairKey> object2bytelinkedopenhashmap = new Object2ByteLinkedOpenHashMap<>(
				CACHE_SIZE,
				0.25F) {
			@Override
			protected void rehash(int needed) {
			}
		};
		object2bytelinkedopenhashmap.defaultReturnValue((byte) 127);
		return object2bytelinkedopenhashmap;
	});

	public static boolean skipGlassRendering(BlockState pState, BlockState pAdjacentBlockState, Direction pDirection) {
		if (KBlockSettings.of(pState.getBlock()) == null && KBlockSettings.of(pAdjacentBlockState.getBlock()) == null) {
			return false;
		}
		GlassType glassType = getGlassType(pState);
		if (glassType == null || !glassType.skipRendering()) {
			return false;
		}
		if (!pState.is(pAdjacentBlockState.getBlock()) && glassType != getGlassType(pAdjacentBlockState)) {
			return false;
		}
		Block.BlockStatePairKey key = new Block.BlockStatePairKey(pState, pAdjacentBlockState, pDirection);
		Object2ByteLinkedOpenHashMap<Block.BlockStatePairKey> map = OCCLUSION_CACHE.get();
		byte b0 = map.getAndMoveToFirst(key);
		if (b0 != 127) {
			return b0 == 0;
		}
		VoxelShape shape1 = KBlockSettings.getGlassFaceShape(pState, pDirection);
		if (shape1.isEmpty()) {
			return true;
		}
		VoxelShape shape2 = KBlockSettings.getGlassFaceShape(pAdjacentBlockState, pDirection.getOpposite());
		boolean flag = Shapes.joinIsNotEmpty(shape1, shape2, BooleanOp.ONLY_FIRST);
		if (map.size() == CACHE_SIZE) {
			map.removeLastByte();
		}
		map.putAndMoveToFirst(key, (byte) (flag ? 1 : 0));
		return !flag;
	}

	@Nullable
	public static GlassType getGlassType(BlockState blockState) {
		KBlockSettings settings = KBlockSettings.of(blockState.getBlock());
		if (settings != null && settings.glassType != null) {
			return settings.glassType;
		}
		if (CommonProxy.isColorlessGlass(blockState)) {
			return GlassType.CLEAR;
		}
		return null;
	}
}
