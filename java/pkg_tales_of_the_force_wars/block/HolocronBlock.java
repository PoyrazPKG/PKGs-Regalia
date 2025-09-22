package pkg_tales_of_the_force_wars.block;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;
import net.minecraft.network.chat.TextComponent;
import pkg_tales_of_the_force_wars.capability.JediDataProvider;
import pkg_tales_of_the_force_wars.block.entity.HolocronBlockEntity;
import pkg_tales_of_the_force_wars.init.PKGTalesoftheForceWarsBlockEntities;
import pkg_tales_of_the_force_wars.init.PKGTalesoftheForceWarsBlocks;
import java.util.Collections;
import java.util.List;

public class HolocronBlock extends BaseEntityBlock {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty OPEN = BooleanProperty.create("open");
    public static final IntegerProperty LIGHT_LEVEL = IntegerProperty.create("light_level", 0, 10);

    public HolocronBlock() {
        super(BlockBehaviour.Properties.of(Material.METAL)
                .sound(SoundType.METAL)
                .strength(1f, 10f)
                .noOcclusion()
                .isRedstoneConductor((bs, br, bp) -> false)
                .lightLevel(state -> state.getValue(LIGHT_LEVEL)));
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(OPEN, false)
                .setValue(LIGHT_LEVEL, 0));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HolocronBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
        return true;
    }

    @Override
    public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return 0;
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            default -> Shapes.or(box(5, 0, 5, 11, 6, 11));
            case NORTH, EAST, WEST -> Shapes.or(box(5, 0, 5, 11, 6, 11));
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, OPEN, LIGHT_LEVEL);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(OPEN, false)
                .setValue(LIGHT_LEVEL, 0);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        return state.rotate(mirrorIn.getRotation(state.getValue(FACING)));
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        List<ItemStack> dropsOriginal = super.getDrops(state, builder);
        if (!dropsOriginal.isEmpty()) return dropsOriginal;
        return Collections.singletonList(new ItemStack(this, 1));
    }

    @Override
    public InteractionResult use(BlockState blockstate, Level world, BlockPos pos, Player entity, InteractionHand hand, BlockHitResult hit) {
        if (!world.isClientSide) {
            // Force-Sensitive kontrolü
            if (entity instanceof ServerPlayer sp) {
                boolean allow = sp.getCapability(JediDataProvider.JEDI_DATA).map(data -> {
                    String status = data.getForceStatus();
                    return status != null && status.equals("force_sensitive");
                }).orElse(false);
                if (!allow) {
                    sp.displayClientMessage(new TextComponent("You are not attuned to the Force. The Holocron remains silent."), true);
                    return InteractionResult.PASS;
                }
            }
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof HolocronBlockEntity holocron) {
                NetworkHooks.openGui((ServerPlayer) entity, holocron, pos);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public MenuProvider getMenuProvider(BlockState state, Level worldIn, BlockPos pos) {
        BlockEntity tileEntity = worldIn.getBlockEntity(pos);
        return tileEntity instanceof MenuProvider menuProvider ? menuProvider : null;
    }

    @Override
    public boolean triggerEvent(BlockState state, Level world, BlockPos pos, int eventID, int eventParam) {
        super.triggerEvent(state, world, pos, eventID, eventParam);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        return blockEntity != null && blockEntity.triggerEvent(eventID, eventParam);
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            super.onRemove(state, world, pos, newState, isMoving);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerRenderLayer() {
        ItemBlockRenderTypes.setRenderLayer(PKGTalesoftheForceWarsBlocks.HOLOCRON_BLOCK.get(), RenderType.cutout());
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return !level.isClientSide && type == PKGTalesoftheForceWarsBlockEntities.HOLOCRON.get() ?
                (world, pos, state1, entity) -> HolocronBlockEntity.tick(world, pos, state1, (HolocronBlockEntity) entity) : null;
    }
}