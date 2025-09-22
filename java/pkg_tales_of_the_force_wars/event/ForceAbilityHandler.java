package pkg_tales_of_the_force_wars.event;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import pkg_tales_of_the_force_wars.PKGTalesoftheForceWars;
import pkg_tales_of_the_force_wars.attributes.AttributeRegistry;

@Mod.EventBusSubscriber(modid = PKGTalesoftheForceWars.MODID)
public class ForceAbilityHandler {
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Player player = event.player instanceof Player ? (Player) event.player : null;
            if (player != null) {
                double forceJumpValue = player.getAttributeValue(AttributeRegistry.FORCE_JUMP.get());
                if (forceJumpValue > 0.0D && player.isOnGround()) {
                    player.jumpFromGround();
                }
            }
        }
    }
}