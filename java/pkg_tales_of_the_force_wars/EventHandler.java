package pkg_tales_of_the_force_wars;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import pkg_tales_of_the_force_wars.capability.JediDataProvider;
import pkg_tales_of_the_force_wars.network.JediDataSyncPacket;
import pkg_tales_of_the_force_wars.ServerTickHandler;

import java.util.Random;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = PKGTalesoftheForceWars.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EventHandler {

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            event.getOriginal().getCapability(JediDataProvider.JEDI_DATA).ifPresent(oldData -> {
                event.getPlayer().getCapability(JediDataProvider.JEDI_DATA).ifPresent(newData -> {
                    newData.loadNBTData(oldData.saveNBTData());
                });
            });
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getPlayer() instanceof ServerPlayer player) {
            player.getCapability(JediDataProvider.JEDI_DATA).ifPresent(data -> {
                String[] slots = data.getAbilitySlots();
                if (slots == null) slots = new String[4];
                PKGTalesoftheForceWars.CHANNEL.sendTo(
                        new JediDataSyncPacket(data.getStage(), data.getForceValue(), slots, data.getUnlockedAbilities()),
                        player.connection.getConnection(), net.minecraftforge.network.NetworkDirection.PLAY_TO_CLIENT);
            });
        }
    }

    // İlk girişte Force-Sensitive seçimi (%15)
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getPlayer() instanceof ServerPlayer player) {
            player.getCapability(JediDataProvider.JEDI_DATA).ifPresent(data -> {
                if (!data.isForceEvaluated()) {
                    int roll = new Random().nextInt(100); // 0-99
                    String status = (roll < 15) ? "force_sensitive" : "non_force";
                    data.setForceStatus(status);
                    data.setForceEvaluated(true);
                    String[] slots = data.getAbilitySlots();
                    if (slots == null) slots = new String[4];
                    PKGTalesoftheForceWars.CHANNEL.sendTo(
                            new JediDataSyncPacket(data.getStage(), data.getForceValue(), slots, data.getUnlockedAbilities()),
                            player.connection.getConnection(), net.minecraftforge.network.NetworkDirection.PLAY_TO_CLIENT);
                    player.displayClientMessage(new net.minecraft.network.chat.TextComponent(
                            status.equals("force_sensitive") ? "You feel the Force within you." : "You are not attuned to the Force."), false);
                }
            });
        }
    }

    // Enhanced Reflexes: pasif tetikleyici – oyuncu zarar gördüğünde kısa süre buff
    @SubscribeEvent
    public static void onPlayerDamaged(LivingDamageEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if (!(entity instanceof ServerPlayer player)) return;
        player.getCapability(JediDataProvider.JEDI_DATA).ifPresent(data -> {
            if (!data.isAbilityUnlocked("enhanced_reflexes")) return;
            String stage = data.getStage() != null ? data.getStage() : "youngling";
            int duration = switch (stage) {
                case "jedi_master" -> 120;
                case "jedi_knight" -> 100;
                case "padawan" -> 80;
                default -> 60;
            };
            double atkSpeedBoost = switch (stage) {
                case "jedi_master" -> 0.35;
                case "jedi_knight" -> 0.25;
                case "padawan" -> 0.15;
                default -> 0.10;
            };
            UUID REFLEX_ATTACK_SPEED = UUID.fromString("7b3dbb08-8dd8-4b2b-9f11-2b0f8a7d3f51");
            UUID REFLEX_MOVE_SPEED = UUID.fromString("a4a1d2d6-3c6c-4f9a-8d11-5bb1c2e7f9a0");
            UUID REFLEX_KB_RES = UUID.fromString("3f6f2c9a-0b1e-4d4a-9c7e-1a2b3c4d5e6f");
            var atk = player.getAttribute(Attributes.ATTACK_SPEED);
            if (atk != null) {
                atk.removeModifier(REFLEX_ATTACK_SPEED);
                atk.addTransientModifier(new AttributeModifier(REFLEX_ATTACK_SPEED, "enhanced_reflexes", atkSpeedBoost, AttributeModifier.Operation.MULTIPLY_BASE));
                ServerTickHandler.scheduleTask(player, () -> atk.removeModifier(REFLEX_ATTACK_SPEED), duration);
            }
            // İksir yerine attribute: hareket hızı ve knockback direnci
            var mv = player.getAttribute(Attributes.MOVEMENT_SPEED);
            if (mv != null) {
                double mvBoost = switch (stage) {
                    case "jedi_master" -> 0.30;
                    case "jedi_knight" -> 0.22;
                    case "padawan" -> 0.15;
                    default -> 0.10;
                };
                mv.removeModifier(REFLEX_MOVE_SPEED);
                mv.addTransientModifier(new AttributeModifier(REFLEX_MOVE_SPEED, "enhanced_reflexes_speed", mvBoost, AttributeModifier.Operation.MULTIPLY_BASE));
                ServerTickHandler.scheduleTask(player, () -> mv.removeModifier(REFLEX_MOVE_SPEED), duration);
            }
            var kb = player.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
            if (kb != null) {
                kb.removeModifier(REFLEX_KB_RES);
                kb.addTransientModifier(new AttributeModifier(REFLEX_KB_RES, "enhanced_reflexes_kb", 0.25, AttributeModifier.Operation.ADDITION));
                ServerTickHandler.scheduleTask(player, () -> kb.removeModifier(REFLEX_KB_RES), duration);
            }

            // Force Sense alt dalı: sürpriz hasarda kısa auto-jump (cooldown'lı)
            if (data.isAbilityUnlocked("force_sense") && data.isAbilityUnlocked("force_jump")) {
                long now = player.level.getGameTime();
                long cdUntil = player.getPersistentData().getLong("sense_autojump_until");
                if (now >= cdUntil) {
                    // Kısa kaçış zıplaması
                    player.setDeltaMovement(player.getDeltaMovement().x, 0.9D, player.getDeltaMovement().z);
                    player.hurtMarked = true;
                    player.getPersistentData().putLong("sense_autojump_until", now + 200); // 10 sn CD
                }
            }
        });
    }

    // Mind Trick: sahip Jedi'ya zarar verilemesin, moblar hedefi sıfırlasın
    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        if (!(event.getSource().getEntity() instanceof LivingEntity attacker)) return;
        var tag = attacker.getPersistentData();
        if (!tag.hasUUID("mind_trick_owner")) return;
        java.util.UUID ownerId = tag.getUUID("mind_trick_owner");
        long until = tag.getLong("mind_trick_until");
        if (attacker.level.getGameTime() > until) return;
        if (event.getEntityLiving() instanceof ServerPlayer target && target.getUUID().equals(ownerId)) {
            event.setCanceled(true);
        } else if (event.getEntityLiving().getUUID().equals(ownerId)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        LivingEntity le = event.getEntityLiving();
        var tag = le.getPersistentData();
        if (tag.hasUUID("mind_trick_owner")) {
            long until = tag.getLong("mind_trick_until");
            if (le.level.getGameTime() > until) {
                tag.remove("mind_trick_owner");
                tag.remove("mind_trick_until");
            } else if (le instanceof Mob mob) {
                java.util.UUID ownerId = tag.getUUID("mind_trick_owner");
                // Sahibine saldırma / hedef alma
                if (mob.getTarget() != null && ownerId.equals(mob.getTarget().getUUID())) {
                    mob.setTarget(null);
                }
            }
        }
    }
}