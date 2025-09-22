package pkg_tales_of_the_force_wars.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemStack;
import pkg_tales_of_the_force_wars.item.AnakinSkywalkerLightsaber;
import pkg_tales_of_the_force_wars.PKGTalesoftheForceWars.RenderTypes;
import com.mojang.math.Vector3f;

public class LightsaberLazerRender extends BlockEntityWithoutLevelRenderer {
    private static float animationProgress = 0.0f;

    public LightsaberLazerRender() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemTransforms.TransformType transformType, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            return;
        }

        boolean isActive = AnakinSkywalkerLightsaber.isActive(stack);
        ItemRenderer itemRenderer = mc.getItemRenderer();

        // Kabza render’ını item’in kendi modeline bırak (ISTER olarak sadece lazer ekleyeceğiz)
        if (!isActive) {
            itemRenderer.render(stack, transformType, false, poseStack, buffer, light, overlay, null);
            return; // Inaktifse sadece kabza render edilsin
        }

        // Aktifse önce kabza render edilsin
        itemRenderer.render(stack, transformType, false, poseStack, buffer, light, overlay, null);

        // Lazer animasyonu
        if (isActive) {
            if (animationProgress < 1.0f) animationProgress += 0.05f;
            else animationProgress = 1.0f;
        } else {
            if (animationProgress > 0.0f) animationProgress -= 0.05f;
            else animationProgress = 0.0f;
        }

        if (animationProgress > 0.0f) {
            VertexConsumer builder = buffer.getBuffer(RenderTypes.LIGHTSABER); // Custom render type
            poseStack.pushPose();

            // Lazer pozisyonunu kabzanın ucuna hizalayalım
            poseStack.translate(0.0, 0.5, -0.5); // Kabzanın ucundan başlayarak Z ekseninde uzasın
            float scaleY = animationProgress * 1.5f; // Daha kısa lazer (kabzaya uygun)
            poseStack.scale(0.1f, scaleY, 0.1f); // İnce ve uzun lazer

            // Quad render (lazer yüzeyi) - POSITION_COLOR formatına uygun
            builder.vertex(poseStack.last().pose(), -0.5f, 0.0f, 0.0f)
                    .color(255, 0, 0, 255)
                    .endVertex();
            builder.vertex(poseStack.last().pose(), 0.5f, 0.0f, 0.0f)
                    .color(255, 0, 0, 255)
                    .endVertex();
            builder.vertex(poseStack.last().pose(), 0.5f, 1.0f, 0.0f)
                    .color(255, 0, 0, 255)
                    .endVertex();
            builder.vertex(poseStack.last().pose(), -0.5f, 1.0f, 0.0f)
                    .color(255, 0, 0, 255)
                    .endVertex();

            // İkinci üçgen
            builder.vertex(poseStack.last().pose(), -0.5f, 0.0f, 0.0f)
                    .color(255, 0, 0, 255)
                    .endVertex();
            builder.vertex(poseStack.last().pose(), 0.5f, 1.0f, 0.0f)
                    .color(255, 0, 0, 255)
                    .endVertex();
            builder.vertex(poseStack.last().pose(), -0.5f, 1.0f, 0.0f)
                    .color(255, 0, 0, 255)
                    .endVertex();
            builder.vertex(poseStack.last().pose(), 0.5f, 0.0f, 0.0f)
                    .color(255, 0, 0, 255)
                    .endVertex();

            poseStack.popPose();
        }
    }
}