package pkg_tales_of_the_force_wars.init;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import pkg_tales_of_the_force_wars.PKGTalesoftheForceWars;
import pkg_tales_of_the_force_wars.client.renderer.HolocronBlockEntityRenderer;

@Mod.EventBusSubscriber(modid = PKGTalesoftheForceWars.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class EntityAnimationFactory {
    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(PKGTalesoftheForceWarsBlockEntities.HOLOCRON.get(), HolocronBlockEntityRenderer::new);
    }
}
