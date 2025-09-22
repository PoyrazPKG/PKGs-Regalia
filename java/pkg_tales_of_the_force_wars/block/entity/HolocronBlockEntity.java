package pkg_tales_of_the_force_wars.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import pkg_tales_of_the_force_wars.PKGTalesoftheForceWars;
import pkg_tales_of_the_force_wars.block.HolocronBlock;
import pkg_tales_of_the_force_wars.init.PKGTalesoftheForceWarsBlockEntities;
import pkg_tales_of_the_force_wars.network.HolocronGuiPacket;
import pkg_tales_of_the_force_wars.world.menu.HolocronMenu;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;
import net.minecraftforge.network.PacketDistributor;

public class HolocronBlockEntity extends BlockEntity implements IAnimatable, MenuProvider {
    private final AnimationFactory factory = GeckoLibUtil.createFactory(this);
    private int openersCount = 0;
    private int cooldownTicks = 0;
    private AnimationController<?> controller;
    private boolean isOpen = false;
    private boolean isClosing = false;
    private long animationStartTime = 0;

    public HolocronBlockEntity(BlockPos position, BlockState state) {
        super(PKGTalesoftheForceWarsBlockEntities.HOLOCRON.get(), position, state);
    }

    public boolean isOpen() {
        return isOpen;
    }

    public boolean isClosing() {
        return isClosing;
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        this.openersCount = compound.getInt("OpenersCount");
        this.cooldownTicks = compound.getInt("CooldownTicks");
        this.isOpen = compound.getBoolean("IsOpen");
        this.isClosing = compound.getBoolean("IsClosing");
        this.animationStartTime = compound.getLong("AnimationStartTime");
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        compound.putInt("OpenersCount", this.openersCount);
        compound.putInt("CooldownTicks", this.cooldownTicks);
        compound.putBoolean("IsOpen", this.isOpen);
        compound.putBoolean("IsClosing", this.isClosing);
        compound.putLong("AnimationStartTime", this.animationStartTime);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithFullMetadata();
    }

    public void startOpen(Player player) {
        if (this.cooldownTicks > 0 || this.remove || player.isSpectator()) {
            return;
        }
        this.openersCount++;
        this.cooldownTicks = 30;
        if (this.openersCount == 1) {
            this.isOpen = true;
            this.isClosing = false;
            this.animationStartTime = System.currentTimeMillis();
            BlockState state = getBlockState().setValue(HolocronBlock.OPEN, true).setValue(HolocronBlock.LIGHT_LEVEL, 0);
            level.setBlock(worldPosition, state, 3);
            level.sendBlockUpdated(worldPosition, state, state, 3);
            level.playSound(null, worldPosition, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1.0F, 1.0F);
            PKGTalesoftheForceWars.CHANNEL.send(
                    PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(
                            worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), 32, level.dimension())),
                    new HolocronGuiPacket(worldPosition, true)
            );
            if (level.getServer() != null) {
                level.getServer().getPlayerList().broadcastAll(new ClientboundBlockUpdatePacket(worldPosition, state));
            }
        }
    }

    public void stopOpen(Player player) {
        if (!this.remove && !player.isSpectator()) {
            this.openersCount--;
            if (this.openersCount <= 0) {
                this.openersCount = 0;
                this.isOpen = false;
                this.isClosing = true;
                this.animationStartTime = System.currentTimeMillis();
                this.cooldownTicks = 0;
                BlockState state = getBlockState().setValue(HolocronBlock.OPEN, true).setValue(HolocronBlock.LIGHT_LEVEL, 10);
                level.setBlock(worldPosition, state, 3);
                level.sendBlockUpdated(worldPosition, state, state, 3);
                level.playSound(null, worldPosition, SoundEvents.BEACON_DEACTIVATE, SoundSource.BLOCKS, 1.0F, 0.8F);
                PKGTalesoftheForceWars.CHANNEL.send(
                        PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(
                                worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), 32, level.dimension())),
                        new HolocronGuiPacket(worldPosition, false)
                );
                if (level.getServer() != null) {
                    level.getServer().getPlayerList().broadcastAll(new ClientboundBlockUpdatePacket(worldPosition, state));
                }
            }
        }
    }

    public void setOpenState(boolean open) {
        if (this.isOpen != open) {
            this.isOpen = open;
            if (!open && level.isClientSide) {
                this.isClosing = true;
                this.animationStartTime = System.currentTimeMillis();
            }
            setChanged();
            if (level != null) {
                BlockState state = getBlockState().setValue(HolocronBlock.OPEN, isOpen || isClosing);
                if (!isOpen && !isClosing) {
                    state = state.setValue(HolocronBlock.LIGHT_LEVEL, 0);
                }
                level.setBlock(worldPosition, state, 3);
                level.sendBlockUpdated(worldPosition, state, state, 3);
                if (!level.isClientSide && level.getServer() != null) {
                    level.getServer().getPlayerList().broadcastAll(new ClientboundBlockUpdatePacket(worldPosition, state));
                }
            }
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, HolocronBlockEntity entity) {
        if (!level.isClientSide) {
            if (entity.cooldownTicks > 0) {
                entity.cooldownTicks--;
                entity.setChanged();
            }
            boolean shouldBeOpen = entity.isOpen || entity.isClosing;
            int currentLightLevel = state.getValue(HolocronBlock.LIGHT_LEVEL);
            int targetLightLevel = 0;

            if (entity.isOpen) {
                long elapsed = System.currentTimeMillis() - entity.animationStartTime;
                float progress = Math.min(elapsed / 500.0f, 1.0f);
                targetLightLevel = (int) (progress * 10);
            } else if (entity.isClosing) {
                long elapsed = System.currentTimeMillis() - entity.animationStartTime;
                float progress = Math.min(elapsed / 1000.0f, 1.0f);
                targetLightLevel = (int) ((1.0f - progress) * 10);
            }

            if (state.getValue(HolocronBlock.OPEN) != shouldBeOpen || currentLightLevel != targetLightLevel) {
                BlockState newState = state.setValue(HolocronBlock.OPEN, shouldBeOpen).setValue(HolocronBlock.LIGHT_LEVEL, targetLightLevel);
                level.setBlock(pos, newState, 3);
                level.sendBlockUpdated(pos, newState, newState, 3);
                if (level.getServer() != null) {
                    level.getServer().getPlayerList().broadcastAll(new ClientboundBlockUpdatePacket(pos, newState));
                }
            }

            if (entity.isClosing && System.currentTimeMillis() - entity.animationStartTime >= 1000) {
                entity.isClosing = false;
                entity.setChanged();
                BlockState newState = state.setValue(HolocronBlock.OPEN, false).setValue(HolocronBlock.LIGHT_LEVEL, 0);
                level.setBlock(pos, newState, 3);
                level.sendBlockUpdated(pos, newState, newState, 3);
                if (level.getServer() != null) {
                    level.getServer().getPlayerList().broadcastAll(new ClientboundBlockUpdatePacket(pos, newState));
                }
            }
        } else {
            if (entity.isClosing && System.currentTimeMillis() - entity.animationStartTime >= 1000) {
                entity.isClosing = false;
                entity.setChanged();
                BlockState newState = state.setValue(HolocronBlock.OPEN, false).setValue(HolocronBlock.LIGHT_LEVEL, 0);
                level.setBlock(pos, newState, 3);
                level.sendBlockUpdated(pos, newState, newState, 3);
            } else if (entity.isOpen) {
                long elapsed = System.currentTimeMillis() - entity.animationStartTime;
                float progress = Math.min(elapsed / 500.0f, 1.0f);
                int targetLightLevel = (int) (progress * 10);
                if (state.getValue(HolocronBlock.LIGHT_LEVEL) != targetLightLevel) {
                    BlockState newState = state.setValue(HolocronBlock.OPEN, true).setValue(HolocronBlock.LIGHT_LEVEL, targetLightLevel);
                    level.setBlock(pos, newState, 3);
                    level.sendBlockUpdated(pos, newState, newState, 3);
                }
            } else if (entity.isClosing) {
                long elapsed = System.currentTimeMillis() - entity.animationStartTime;
                float progress = Math.min(elapsed / 1000.0f, 1.0f);
                int targetLightLevel = (int) ((1.0f - progress) * 10);
                if (state.getValue(HolocronBlock.LIGHT_LEVEL) != targetLightLevel) {
                    BlockState newState = state.setValue(HolocronBlock.OPEN, true).setValue(HolocronBlock.LIGHT_LEVEL, targetLightLevel);
                    level.setBlock(pos, newState, 3);
                    level.sendBlockUpdated(pos, newState, newState, 3);
                }
            }
        }
    }

    public boolean isInIdle() {
        return controller != null && controller.getCurrentAnimation() != null &&
                controller.getCurrentAnimation().animationName.equals("open_idle");
    }

    @Override
    public void registerControllers(AnimationData data) {
        controller = new AnimationController<>(this, "controller", 10, this::predicate);
        data.addAnimationController(controller);
    }

    private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
        AnimationController<?> controller = event.getController();
        controller.transitionLengthTicks = 10;
        this.controller = controller;

        String currentAnim = controller.getCurrentAnimation() != null ? controller.getCurrentAnimation().animationName : "none";

        if (level.isClientSide) {
            if (isClosing && System.currentTimeMillis() - animationStartTime <= 1000) {
                controller.setAnimation(new AnimationBuilder().addAnimation("close_animation", false));
                return PlayState.CONTINUE;
            } else if (isClosing) {
                isClosing = false;
                setChanged();
                return PlayState.STOP;
            } else if (isOpen) {
                if ("open_animation".equals(currentAnim)) {
                    controller.setAnimation(new AnimationBuilder().addAnimation("open_idle", true));
                } else if (!"open_idle".equals(currentAnim)) {
                    controller.setAnimation(new AnimationBuilder().addAnimation("open_animation", false));
                }
                return PlayState.CONTINUE;
            }
        }
        return PlayState.STOP;
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    @Override
    public Component getDisplayName() {
        return new TextComponent("Holocron");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        this.startOpen(player);
        return new HolocronMenu(id, inventory, this);
    }
}