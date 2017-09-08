package uk.co.cloudhunter.slimechancer.client.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelSlime;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uk.co.cloudhunter.slimechancer.SlimeChancer;
import uk.co.cloudhunter.slimechancer.client.render.util.RenderUtil;
import uk.co.cloudhunter.slimechancer.common.entities.EntityMySlime;

import java.awt.*;

@SideOnly(Side.CLIENT)
public class MyLayerSlimeGel implements LayerRenderer<EntityMySlime>
{
    private final MySlimeRenderer slimeRenderer;
    private final ModelBase slimeModel = new ModelSlime(0);

    public MyLayerSlimeGel(MySlimeRenderer slimeRendererIn)
    {
        this.slimeRenderer = slimeRendererIn;
    }

    public void doRenderLayer(EntityMySlime entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {

        if (!entitylivingbaseIn.isInvisible())
        {
            Color slimeColour = RenderUtil.getColorFromState(entitylivingbaseIn.getSlimeType());

            slimeColour = slimeColour != null ? slimeColour : new Color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.color(slimeColour.getRed() / 255f, slimeColour.getGreen() / 255f, slimeColour.getBlue() / 255f, 1.0F);
            GlStateManager.enableNormalize();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            this.slimeModel.setModelAttributes(this.slimeRenderer.getMainModel());
            this.slimeModel.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            GlStateManager.disableBlend();
            GlStateManager.disableNormalize();
        }
    }

    public boolean shouldCombineTextures()
    {
        return true;
    }
}