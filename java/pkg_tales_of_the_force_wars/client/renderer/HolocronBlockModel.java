package pkg_tales_of_the_force_wars.client.renderer;

import net.minecraft.resources.ResourceLocation;
import pkg_tales_of_the_force_wars.PKGTalesoftheForceWars;
import pkg_tales_of_the_force_wars.block.entity.HolocronBlockEntity;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class HolocronBlockModel extends AnimatedGeoModel<HolocronBlockEntity> {
    @Override
    public ResourceLocation getModelLocation(HolocronBlockEntity object) {
        return new ResourceLocation(PKGTalesoftheForceWars.MODID, "geo/jedi_holocron.geo.json");
    }

    @Override
    public ResourceLocation getTextureLocation(HolocronBlockEntity object) {
        return new ResourceLocation(PKGTalesoftheForceWars.MODID, "textures/block/holocron.png");
    }

    @Override
    public ResourceLocation getAnimationFileLocation(HolocronBlockEntity animatable) {
        return new ResourceLocation(PKGTalesoftheForceWars.MODID, "animations/holocron.animation.json");
    }
}