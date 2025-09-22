package pkg_tales_of_the_force_wars.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderType.CompositeState;

public class PKGTalesoftheForceWarsRenderTypes {
    public static final RenderType LIGHTSABER = createLightsaber();

    private PKGTalesoftheForceWarsRenderTypes() {
        // Private constructor to prevent instantiation
    }

    private static RenderType createLightsaber() {
        CompositeState compositeState = CompositeState.builder()
                .setTextureState(new RenderType.TextureStateShard(null, false, false)) // No texture
                .setTransparencyState(new RenderType.TransparencyStateShard("additive_transparency", () -> {}, () -> {})) // Additive transparency
                .setLightmapState(new RenderType.LightmapStateShard(true)) // Lightmap enabled
                .setOverlayState(new RenderType.OverlayStateShard(true)) // Overlay enabled
                .createCompositeState(true);

        return RenderType.create("tales_of_the_force_wars:lightsaber", DefaultVertexFormat.POSITION_COLOR, Mode.QUADS, 256, false, true, compositeState);
    }
}