package pkg_tales_of_the_force_wars.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import pkg_tales_of_the_force_wars.network.LightsaberTogglePacket;
import pkg_tales_of_the_force_wars.PKGTalesoftheForceWars;
import pkg_tales_of_the_force_wars.item.AnakinSkywalkerLightsaber;
import pkg_tales_of_the_force_wars.capability.JediDataProvider;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.ChatFormatting;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.chat.TextComponent;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraft.sounds.SoundSource;

@Mod.EventBusSubscriber(modid = PKGTalesoftheForceWars.MODID, value = Dist.CLIENT)
public class ClientEventHandler {
    @SubscribeEvent
    public static void onKeyInput(InputEvent.KeyInputEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && KeyBindings.LIGHTSABER_IGNITE.consumeClick()) {
            var stack = mc.player.getMainHandItem();
            if (stack.getItem() instanceof AnakinSkywalkerLightsaber) {
                boolean isActive = !AnakinSkywalkerLightsaber.isActive(stack);
                AnakinSkywalkerLightsaber.setActive(stack, isActive); // Client-side güncelleme
                PKGTalesoftheForceWars.CHANNEL.sendToServer(new LightsaberTogglePacket());
            }
        }
    }

    // Force Cloak sırasında 1. şahıs eldeki el/öğe renderını iptal et (sadece cloak_active iken)
    @SubscribeEvent
    public static void onRenderHand(RenderHandEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.player.getPersistentData().getBoolean("cloak_active")) {
            event.setCanceled(true);
        }
    }

    // Force Cloak sırasında oyuncu modelini (zırh ve eşyalar dahil) çizdirmeyi iptal et
    @SubscribeEvent
    public static void onRenderPlayer(RenderPlayerEvent.Pre event) {
        if (event.getPlayer() == null) return;
        var pl = event.getPlayer();
        if (pl.getPersistentData().getBoolean("cloak_active")) {
            event.setCanceled(true);
            return;
        }
        // Camouflage: yarı saydam görünüm (yaklaşık %25 opak)
        long camoUntil = pl.getPersistentData().getLong("camouflage_until");
        if (camoUntil > 0 && pl.level.getGameTime() <= camoUntil) {
            RenderSystem.enableBlend();
            // %75 görünür (0.75 alfa)
            RenderSystem.setShaderColor(1f, 1f, 1f, 0.75f);
        }
    }

    // Render sonrası shader rengini geri sıfırla
    @SubscribeEvent
    public static void onRenderPlayerPost(RenderPlayerEvent.Post event) {
        if (event.getPlayer() == null) return;
        long camoUntil = event.getPlayer().getPersistentData().getLong("camouflage_until");
        if (camoUntil > 0 && event.getPlayer().level.getGameTime() <= camoUntil) {
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
            RenderSystem.disableBlend();
        }
    }

    // Life Mapping (pasif): Force Vision + Life Mapping UNLOCKED VE Vision aktifken (Night Vision proxy), crosshair'daki canlı için Adı ve HP göster
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        mc.player.getCapability(JediDataProvider.JEDI_DATA).ifPresent(data -> {
            boolean hasVision = data.isAbilityUnlocked("force_vision");
            boolean hasLifeMap = data.isAbilityUnlocked("force_life_mapping");
            if (!hasVision || !hasLifeMap) return; // alt dal koşulu
            // Vision aktif mi? Basit proxy: Night Vision etkisi var mı?
            boolean visionActive = mc.player.hasEffect(MobEffects.NIGHT_VISION);
            if (!visionActive) return;

            HitResult hr = mc.hitResult;
            if (hr instanceof EntityHitResult ehr && ehr.getEntity() instanceof net.minecraft.world.entity.LivingEntity le && le != mc.player) {
                String name = le.getDisplayName().getString();
                int hp = (int)Math.ceil(le.getHealth());
                String msg = ChatFormatting.AQUA + "Adı: " + name + ChatFormatting.RESET + "  " + ChatFormatting.AQUA + "HP: " + hp + ChatFormatting.RESET;
                mc.player.displayClientMessage(new TextComponent(msg), true);
            }
        });
    }

    // Stealth sırasında sadece oyuncunun adım seslerini bastır
    @SubscribeEvent
    public static void onPlaySound(PlaySoundEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;
        var snd = event.getSound();
        if (snd == null) return;
        // Force Stealth aktifken, oyuncudan HİÇBİR ses çıkmasın: tüm PLAYER kaynaklı sesleri bastır
        long until = mc.player.getPersistentData().getLong("stealth_until");
        if (mc.level.getGameTime() <= until && snd.getSource() == SoundSource.PLAYERS) {
            // Yalnızca kendi oyuncumuzun seslerini bastır: ses konumu oyuncuya çok yakınsa (≈0.5 blok)
            double dx = snd.getX() - mc.player.getX();
            double dy = snd.getY() - mc.player.getY();
            double dz = snd.getZ() - mc.player.getZ();
            double dist2 = dx*dx + dy*dy + dz*dz;
            if (dist2 < 9.0) { // ≈3 blok yarıçap
                event.setSound(null);
            }
        }
    }
}