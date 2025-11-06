package dev.tazer.clutternomore.common.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
//? if neoforge {
import net.minecraft.core.registries.BuiltInRegistries;
//?}
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
//? if >1.21.2 {
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
//?} else {
/*import net.minecraft.world.level.block.state.properties.DirectionProperty;
*///?}
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class VerticalSlabBlock extends HorizontalDirectionalBlock implements SimpleWaterloggedBlock {
    //? if >1.20.1 {
    public static final MapCodec<? extends VerticalSlabBlock> CODEC = simpleCodec(VerticalSlabBlock::new);
    //?}
    public static final BooleanProperty DOUBLE = BooleanProperty.create("double");
    public static final
    //? if >1.21.2 {
    EnumProperty<Direction>
    //?} else {
    /*DirectionProperty
    *///?}
    FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public VerticalSlabBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(DOUBLE, false).setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false));
    }

    //? if >1.20.1 {
    @Override
    protected MapCodec<? extends VerticalSlabBlock> codec() {
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
        builder.add(DOUBLE).add(FACING).add(WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        Vec3 exactPos = context.getClickLocation();
        Direction direction = context.getHorizontalDirection();
        BlockState replacingBlockState = context.getLevel().getBlockState(pos);
        FluidState replacingFluidState = context.getLevel().getFluidState(pos);

        if (replacingBlockState.is(this)) {
            return replacingBlockState.setValue(DOUBLE, true);
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
        if (state.getValue(DOUBLE) || !(itemStack.is(asItem())) ) {
            return false;
        }

        if (!context.replacingClickedOnBlock()) {
            return true;
        }

        double hitposX = context.getClickLocation().x - context.getClickedPos().getX();
        double hitposZ = context.getClickLocation().z - context.getClickedPos().getZ();

        Direction facingDirection = state.getValue(FACING);
        return switch (facingDirection) {
            case NORTH -> hitposZ >= 0.5;
            case EAST -> hitposX <= 0.5;
            case SOUTH -> hitposZ <= 0.5;
            case WEST -> hitposX >= 0.5;
            default -> false;
        };
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if(state.getValue(DOUBLE)) {
            return Shapes.block();
        }
        
        return switch (state.getValue(FACING)) {
            case NORTH -> Shapes.create(0, 0, 0, 1, 1, 0.5F);
            case EAST -> Shapes.create(0.5F, 0, 0, 1, 1, 1);
            case SOUTH -> Shapes.create(0, 0, 0.5F, 1, 1, 1);
            case WEST -> Shapes.create(0, 0, 0, 0.5F, 1, 1);
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
    public boolean useShapeForLightOcclusion(BlockState state) {
        return !state.getValue(DOUBLE);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        if (state.getValue(WATERLOGGED)) {
            return Fluids.WATER.getSource(false);
        }
        
        return state.getFluidState();
    }

    @Override
    public boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluidState) {
        if (!state.getValue(DOUBLE)) {
            return SimpleWaterloggedBlock.super.placeLiquid(level, pos, state, fluidState);
        }

        return false;
    }

    @Override
    //? if >1.21.2 {
    public boolean canPlaceLiquid(@Nullable LivingEntity player, BlockGetter level, BlockPos pos, BlockState state, Fluid fluid) {
    //?} else if 1.21.1 {
    /*public boolean canPlaceLiquid(@Nullable Player player, BlockGetter level, BlockPos pos, BlockState state, Fluid fluid) {
    *///?} else if 1.20.1 {
    /*public boolean canPlaceLiquid(BlockGetter level, BlockPos pos, BlockState state, Fluid fluid) {
    *///?}
        if (!state.getValue(DOUBLE)) {
            //? if >1.20.1 {
            return SimpleWaterloggedBlock.super.canPlaceLiquid(player, level, pos, state, fluid);
            //?} else {
            /*return SimpleWaterloggedBlock.super.canPlaceLiquid(level, pos, state, fluid);
            *///?}
        }
        return false;
    }

    @Override
    //? if >1.20.1 {
    protected
    //?} else {
    /*public
    *///?}
    boolean isPathfindable(
            //? if >1.20.1 {
            BlockState state, PathComputationType type
            //?} else {
            /*BlockState state, BlockGetter level, BlockPos pos, PathComputationType type
            *///?}
    ) {
        return state.getValue(DOUBLE);
    }
}
