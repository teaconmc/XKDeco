package org.teacon.xkdeco.block;

import com.mojang.math.Vector3d;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.teacon.xkdeco.blockentity.ItemDisplayBlockEntity;

public final class SpecialItemDisplayBlock extends BaseEntityBlock implements XKDecoBlock.Special {

    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public SpecialItemDisplayBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(POWERED, false));
    }

    @SuppressWarnings("deprecation")
    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new ItemDisplayBlockEntity(pPos, pState);
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
        // must click on the upper surface
        if (hit.getDirection() != Direction.UP
//                || !containsInclusive(SHAPE_UP.getBoundingBox(),
//                hit.getHitVec().subtract(pos.getX(), pos.getY(), pos.getZ()))
        ) {
            return InteractionResult.PASS;
        }

        if (!worldIn.isClientSide()) {
//        ItemStack heldItem = player.getItemInHand(handIn);
            BlockEntity te = worldIn.getBlockEntity(pos);
            if (te instanceof ItemDisplayBlockEntity tileEntity) {
                ItemStack temp = player.getItemInHand(handIn).copy();
                player.setItemInHand(handIn, tileEntity.getItem().copy());
                tileEntity.setItem(temp);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(POWERED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(POWERED, pContext.getLevel().hasNeighborSignal(pContext.getClickedPos()));
    }

    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pBlock, BlockPos pFromPos, boolean pIsMoving) {
        if (!pLevel.isClientSide) {
            if (pState.getValue(POWERED) != pLevel.hasNeighborSignal(pPos)) {
                pLevel.setBlock(pPos, pState.cycle(POWERED), 2);
            }
        }
    }

    public static boolean containsInclusive(BoundingBox boundingBox, Vector3d vec) {
        return containsInclusive(boundingBox, vec.x, vec.y, vec.z);
    }

    public static boolean containsInclusive(BoundingBox boundingBox, double x, double y, double z) {
        return x >= boundingBox.minX() && x <= boundingBox.maxX()
                && y >= boundingBox.minY() && y <= boundingBox.maxY()
                && z >= boundingBox.minZ() && z <= boundingBox.maxZ();
    }
}
