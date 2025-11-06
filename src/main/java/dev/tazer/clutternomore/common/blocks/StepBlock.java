package dev.tazer.clutternomore.common.blocks;

import com.mojang.serialization.MapCodec;
//? if 1.20.1 {
/*import com.mojang.serialization.codecs.RecordCodecBuilder;
*///?}
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
//? if neoforge {
import net.minecraft.core.registries.BuiltInRegistries;
//?}
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
//? if >1.21.2
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class StepBlock extends HorizontalDirectionalBlock implements SimpleWaterloggedBlock {
    //? if >1.20.1 {
    public static final MapCodec<? extends StepBlock> CODEC = simpleCodec(StepBlock::new);
    //?}

    public static final
    //? if >1.21.2 {
    EnumProperty<Direction>
    //?} else {
    /*DirectionProperty
    *///?}
    FACING = HorizontalDirectionalBlock.FACING;
    public static final EnumProperty<SlabType> SLAB_TYPE = BlockStateProperties.SLAB_TYPE;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public StepBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(SLAB_TYPE, SlabType.BOTTOM).setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false));
    }

    //? if >1.20.1 {
    @Override
    protected MapCodec<? extends StepBlock> codec() {
        return CODEC;
    }
    //?}


    //? if neoforge {
    /*@Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return BuiltInRegistries.ITEM.get(this.builtInRegistryHolder().key().location()).getDefaultInstance();
    }
    *///?}

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SLAB_TYPE).add(FACING).add(WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        Vec3 exactPos = context.getClickLocation();
        Direction direction = context.getHorizontalDirection();
        BlockState replacingBlockState = context.getLevel().getBlockState(pos);
        FluidState replacingFluidState = context.getLevel().getFluidState(pos);

        if (replacingBlockState.is(this)) {
            if (replacingBlockState.getValue(SLAB_TYPE) == SlabType.BOTTOM) {
                replacingBlockState = replacingBlockState.setValue(FACING, replacingBlockState.getValue(FACING).getOpposite());
            }
            return replacingBlockState.setValue(SLAB_TYPE, SlabType.DOUBLE);
        }

        BlockState stateForPlacement = defaultBlockState().setValue(WATERLOGGED, replacingFluidState.getType() == Fluids.WATER);

        switch (direction) {
            case NORTH -> {
                if (exactPos.z - pos.getZ() > 0.5) direction = direction.getOpposite();
            }
            case SOUTH -> {
                if (exactPos.z - pos.getZ() < 0.5) direction = direction.getOpposite();
            }
            case EAST -> {
                if (exactPos.x - pos.getX() < 0.5) direction = direction.getOpposite();
            }
            case WEST -> {
                if (exactPos.x - pos.getX() > 0.5) direction = direction.getOpposite();
            }
        }


        if (exactPos.y - pos.getY() > 0.5) {
            stateForPlacement = stateForPlacement.setValue(SLAB_TYPE, SlabType.TOP);
        }

        return stateForPlacement.setValue(FACING, direction);
    }

    @Override
    //? if >1.20.1 {
    protected
    //?} else {
    /*public
    *///?}
    boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        ItemStack itemStack = context.getItemInHand();
        if (state.getValue(SLAB_TYPE) == SlabType.DOUBLE || !(itemStack.is(asItem())) ) {
            return false;
        }

        if (!context.replacingClickedOnBlock()) {
            return true;
        }

        Direction direction = context.getClickedFace();

        return direction == (state.getValue(SLAB_TYPE) == SlabType.BOTTOM ? Direction.UP : Direction.DOWN) || direction == state.getValue(FACING).getOpposite();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction direction = state.getValue(FACING);
        if (state.getValue(SLAB_TYPE) == SlabType.DOUBLE) {
            return Shapes.or(createShape(direction, 0.5), createShape(direction.getOpposite(), 0));
        }

        double y = state.getValue(SLAB_TYPE) == SlabType.TOP ? 0.5 : 0;
        return createShape(direction, y);
    }

    public static VoxelShape createShape(Direction direction, double y) {
        return switch (direction) {
            case NORTH -> Shapes.create(0, y, 0, 1, y + 0.5, 0.5);
            case EAST -> Shapes.create(0.5, y, 0, 1, y + 0.5, 1);
            case SOUTH -> Shapes.create(0, y, 0.5, 1, y + 0.5, 1);
            case WEST -> Shapes.create(0, y, 0, 0.5, y + 0.5, 1);
            default -> Shapes.block();
        };
    }

    @Override
    //? if >1.21.2 {
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess scheduledTickAccess, BlockPos pos, Direction direction, BlockPos blockPos2, BlockState blockState2, RandomSource randomSource) {
        if (state.getValue(WATERLOGGED)) {
            scheduledTickAccess.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        return super.updateShape(state, level, scheduledTickAccess, pos, direction, blockPos2, blockState2, randomSource);
    }
    //?} else {
    /*//? if >1.20.1 {
    protected
    //?} else {
    /^public
    ^///?}
    BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
    if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }
    *///?}

    @Override
    //? if >1.20.1 {
    protected
    //?} else {
    /*public
    *///?}
    boolean useShapeForLightOcclusion(BlockState state) {
        return true;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        if (state.getValue(WATERLOGGED)) {
            return Fluids.WATER.getSource(false);
        }

        return state.getFluidState();
    }
}
