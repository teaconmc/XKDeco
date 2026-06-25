package org.teacon.xkdeco.block;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import org.teacon.xkdeco.util.NotNullByDefault;

/**
 * Concrete leaves block for XKDeco's data-driven decorative leaves.
 *
 * <p>In 26.1 vanilla {@link LeavesBlock} became abstract with a {@code (float, Properties)}
 * constructor, so the {@code minecraft:leaves} block template can no longer point at it directly.
 * This subclass provides the {@code (Properties)} constructor Kiwi's {@code type: simple} template
 * instantiates via reflection.
 */
@NotNullByDefault
public class XKDLeavesBlock extends LeavesBlock {
	public static final MapCodec<XKDLeavesBlock> CODEC = simpleCodec(XKDLeavesBlock::new);

	public XKDLeavesBlock(Properties properties) {
		super(0.01F, properties);
	}

	@Override
	protected void spawnFallingLeavesParticle(Level level, BlockPos pos, RandomSource random) {
		ParticleUtils.spawnParticleBelow(level, pos, random, ParticleTypes.CHERRY_LEAVES);
	}

	@Override
	public MapCodec<? extends LeavesBlock> codec() {
		return CODEC;
	}
}
