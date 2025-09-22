package pkg_tales_of_the_force_wars.client;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import pkg_tales_of_the_force_wars.PKGTalesoftheForceWars;
import pkg_tales_of_the_force_wars.capability.JediDataProvider;
import pkg_tales_of_the_force_wars.network.AbilityActivationPacket;
import pkg_tales_of_the_force_wars.item.AnakinSkywalkerLightsaber;
import pkg_tales_of_the_force_wars.network.LightsaberActivationPacket;

@Mod.EventBusSubscriber(modid = PKGTalesoftheForceWars.MODID, value = Dist.CLIENT)
public class KeyHandler {
    private static final boolean[] keyStates = new boolean[5];

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        checkAbilityKey(KeyBindings.ABILITY_SLOT_1, 1);
        checkAbilityKey(KeyBindings.ABILITY_SLOT_2, 2);
        checkAbilityKey(KeyBindings.ABILITY_SLOT_3, 3);
        checkAbilityKey(KeyBindings.ABILITY_SLOT_4, 4);
        checkLightsaberIgnite();
    }

    private static void checkAbilityKey(KeyMapping key, int slot) {
        boolean isPressed = key.isDown();
        if (isPressed && !keyStates[slot]) {
            Minecraft mc = Minecraft.getInstance();
            mc.player.getCapability(JediDataProvider.JEDI_DATA).ifPresent(data -> {
                String ability = data.getAbilitySlots()[slot - 1];
                if (ability != null && !ability.isEmpty()) {
                    PKGTalesoftheForceWars.CHANNEL.send(PacketDistributor.SERVER.noArg(), new AbilityActivationPacket(ability));
                }
            });
        }
        keyStates[slot] = isPressed;
    }

    private static void checkLightsaberIgnite() {
        boolean isPressed = KeyBindings.LIGHTSABER_IGNITE.isDown();
        if (isPressed && !keyStates[0]) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                ItemStack stack = mc.player.getMainHandItem();
                if (stack.getItem() instanceof AnakinSkywalkerLightsaber) {
                    boolean currentState = AnakinSkywalkerLightsaber.isActive(stack);
                    boolean newState = !currentState;
                    AnakinSkywalkerLightsaber.setActive(stack, newState);
                    PKGTalesoftheForceWars.CHANNEL.sendToServer(new LightsaberActivationPacket(newState));
                    mc.player.level.playSound(mc.player, mc.player.blockPosition(), SoundEvents.BLAZE_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F);
                }
            }
        }
        keyStates[0] = isPressed;
    }

    @SubscribeEvent
    public static void onPlayerLogout(ClientPlayerNetworkEvent.LoggedOutEvent event) {
        for (int i = 0; i < keyStates.length; i++) {
            keyStates[i] = false;
        }
    }
}