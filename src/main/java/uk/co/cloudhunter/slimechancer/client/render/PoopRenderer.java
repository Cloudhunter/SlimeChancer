package uk.co.cloudhunter.slimechancer.client.render;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import uk.co.cloudhunter.slimechancer.SlimeChancer;
import uk.co.cloudhunter.slimechancer.client.render.util.RenderUtil;
import uk.co.cloudhunter.slimechancer.common.entities.EntityPoop;

import javax.annotation.Nullable;
import java.awt.*;

public class PoopRenderer extends Render<EntityPoop>
{

    public PoopRenderer(RenderManager renderManager)
    {
        super(renderManager);
    }

    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityPoop entity)
    {
        return null;
    }

    @Override
    public void doRender(EntityPoop entity, double x, double y, double z, float entityYaw, float partialTicks)
    {

        IBlockState blockState = entity.getSlimeType();

        BufferBuilder buffer = Tessellator.getInstance().getBuffer();

        GlStateManager.pushMatrix();
        {

            GlStateManager.translate(x, y, z);

            float f3 = (((float) entity.ticksExisted + partialTicks) / 20.0F + 0) * (180F / (float) Math.PI);

            GlStateManager.rotate(f3, 0.0F, 1.0F, 0.0F);


            bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

            IBakedModel modelForState = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelForState(blockState);

            renderPyramid(buffer, 0, 0.6, 0, 0.25, 0.25, modelForState, blockState, false);
            renderPyramid(buffer, 0, 0.1, 0, 0.25, 0.25, modelForState, blockState, true);

            renderSlimePyramid(buffer, 0, 0.7, 0, 0.35, 0.35, blockState,false);
            renderSlimePyramid(buffer, 0, 0, 0, 0.35, 0.35, blockState, true);


        }
        GlStateManager.popMatrix();

        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    public static void renderPyramid(BufferBuilder buffer, double pointX, double pointY, double pointZ, double width, double height, IBakedModel modelForState, IBlockState blockState, boolean inverse) {
        GlStateManager.disableLighting();
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        buffer.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX_COLOR);

        double bottomLX, bottomLY, bottomLZ, bottomRX, bottomRY, bottomRZ;

        bottomLX = pointX + (width / 2);
        if (!inverse)
            bottomLY = pointY - (height);
        else
            bottomLY = pointY + (height);
        bottomLZ = pointZ + (width / 2);

        bottomRX = pointX - (width / 2);
        if (!inverse)
            bottomRY = pointY - (height);
        else
            bottomRY = pointY + (height);
        bottomRZ = pointZ - (width / 2);

        float alpha = 0.5F;

        if (!inverse) {
            TextureAtlasSprite textureSprite = RenderUtil.getTexture(modelForState, blockState, EnumFacing.NORTH);

            buffer.pos(pointX, pointY, pointZ).tex(textureSprite.getMinU(), textureSprite.getMaxV()).color(1, 1, 1, alpha).endVertex();
            buffer.pos(bottomLX, bottomLY, bottomLZ).tex(textureSprite.getMaxU(), textureSprite.getMaxV()).color(1, 1, 1, alpha).endVertex();
            buffer.pos(bottomLX, bottomLY, bottomRZ).tex(textureSprite.getMinU(), textureSprite.getMinV()).color(1, 1, 1, alpha).endVertex();

            textureSprite = RenderUtil.getTexture(modelForState, blockState, EnumFacing.EAST);

            buffer.pos(pointX, pointY, pointZ).tex(textureSprite.getMinU(), textureSprite.getMaxV()).color(1, 1, 1, alpha).endVertex();
            buffer.pos(bottomRX, bottomRY, bottomRZ).tex(textureSprite.getMaxU(), textureSprite.getMaxV()).color(1, 1, 1, alpha).endVertex();
            buffer.pos(bottomRX, bottomRY, bottomLZ).tex(textureSprite.getMinU(), textureSprite.getMinV()).color(1, 1, 1, alpha).endVertex();

            textureSprite = RenderUtil.getTexture(modelForState, blockState, EnumFacing.SOUTH);

            buffer.pos(pointX, pointY, pointZ).tex(textureSprite.getMinU(), textureSprite.getMaxV()).color(1, 1, 1, alpha).endVertex();
            buffer.pos(bottomRX, bottomRY, bottomLZ).tex(textureSprite.getMaxU(), textureSprite.getMaxV()).color(1, 1, 1, alpha).endVertex();
            buffer.pos(bottomLX, bottomLY, bottomLZ).tex(textureSprite.getMinU(), textureSprite.getMinV()).color(1, 1, 1, alpha).endVertex();

            textureSprite = RenderUtil.getTexture(modelForState, blockState, EnumFacing.WEST);

            buffer.pos(pointX, pointY, pointZ).tex(textureSprite.getMinU(), textureSprite.getMaxV()).color(1, 1, 1, alpha).endVertex();
            buffer.pos(bottomLX, bottomLY, bottomRZ).tex(textureSprite.getMaxU(), textureSprite.getMaxV()).color(1, 1, 1, alpha).endVertex();
            buffer.pos(bottomRX, bottomRY, bottomRZ).tex(textureSprite.getMinU(), textureSprite.getMinV()).color(1, 1, 1, alpha).endVertex();
        } else {
            TextureAtlasSprite textureSprite = RenderUtil.getTexture(modelForState, blockState, EnumFacing.NORTH);

            buffer.pos(pointX, pointY, pointZ).tex(textureSprite.getMaxU(), textureSprite.getMinV()).color(1, 1, 1, alpha).endVertex();
            buffer.pos(bottomLX, bottomLY, bottomRZ).tex(textureSprite.getMinU(), textureSprite.getMinV()).color(1, 1, 1, alpha).endVertex();
            buffer.pos(bottomLX, bottomLY, bottomLZ).tex(textureSprite.getMaxU(), textureSprite.getMaxV()).color(1, 1, 1, alpha).endVertex();

            textureSprite = RenderUtil.getTexture(modelForState, blockState, EnumFacing.EAST);

            buffer.pos(pointX, pointY, pointZ).tex(textureSprite.getMaxU(), textureSprite.getMinV()).color(1, 1, 1, alpha).endVertex();
            buffer.pos(bottomRX, bottomRY, bottomLZ).tex(textureSprite.getMinU(), textureSprite.getMinV()).color(1, 1, 1, alpha).endVertex();
            buffer.pos(bottomRX, bottomRY, bottomRZ).tex(textureSprite.getMaxU(), textureSprite.getMaxV()).color(1, 1, 1, alpha).endVertex();

            textureSprite = RenderUtil.getTexture(modelForState, blockState, EnumFacing.SOUTH);

            buffer.pos(pointX, pointY, pointZ).tex(textureSprite.getMaxU(), textureSprite.getMinV()).color(1, 1, 1, alpha).endVertex();
            buffer.pos(bottomLX, bottomLY, bottomLZ).tex(textureSprite.getMinU(), textureSprite.getMinV()).color(1, 1, 1, alpha).endVertex();
            buffer.pos(bottomRX, bottomRY, bottomLZ).tex(textureSprite.getMaxU(), textureSprite.getMaxV()).color(1, 1, 1, alpha).endVertex();

            textureSprite = RenderUtil.getTexture(modelForState, blockState, EnumFacing.WEST);

            buffer.pos(pointX, pointY, pointZ).tex(textureSprite.getMaxU(), textureSprite.getMinV()).color(1, 1, 1, alpha).endVertex();
            buffer.pos(bottomRX, bottomRY, bottomRZ).tex(textureSprite.getMinU(), textureSprite.getMinV()).color(1, 1, 1, alpha).endVertex();
            buffer.pos(bottomLX, bottomLY, bottomRZ).tex(textureSprite.getMaxU(), textureSprite.getMaxV()).color(1, 1, 1, alpha).endVertex();
        }


        Tessellator.getInstance().draw();
        GlStateManager.enableLighting();
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
    }

    public static void renderSlimePyramid(BufferBuilder buffer, double pointX, double pointY, double pointZ, double width, double height, IBlockState blockstate, boolean inverse) {
        GlStateManager.disableLighting();
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        buffer.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX_COLOR);

        double bottomLX, bottomLY, bottomLZ, bottomRX, bottomRY, bottomRZ;

        bottomLX = pointX + (width / 2);
        if (!inverse)
            bottomLY = pointY - (height);
        else
            bottomLY = pointY + (height);
        bottomLZ = pointZ + (width / 2);

        bottomRX = pointX - (width / 2);
        if (!inverse)
            bottomRY = pointY - (height);
        else
            bottomRY = pointY + (height);
        bottomRZ = pointZ - (width / 2);

        float alpha = 0.65F;

        TextureAtlasSprite textureSprite = (TextureAtlasSprite) SlimeChancer.proxy.getSlimeBlockTexture();

        Color slimeColour = RenderUtil.getColorFromState(blockstate);

        slimeColour = slimeColour != null ? slimeColour : new Color(1.0F, 1.0F, 1.0F, 1.0F);

        //System.out.println(maxUV);

        float red = slimeColour.getRed() / 255f, green = slimeColour.getGreen() / 255f, blue = slimeColour.getBlue() / 255f;

        if (!inverse) {

            buffer.pos(pointX, pointY, pointZ).tex(textureSprite.getMinU(),       textureSprite.getMaxV()).color(red, green, blue, alpha).endVertex();
            buffer.pos(bottomLX, bottomLY, bottomLZ).tex(textureSprite.getMaxU(), textureSprite.getMaxV()).color(red, green, blue, alpha).endVertex();
            buffer.pos(bottomLX, bottomLY, bottomRZ).tex(textureSprite.getMinU(), textureSprite.getMinV()).color(red, green, blue, alpha).endVertex();

            buffer.pos(pointX, pointY, pointZ).tex(textureSprite.getMinU(),       textureSprite.getMaxV()).color(red, green, blue, alpha).endVertex();
            buffer.pos(bottomRX, bottomRY, bottomRZ).tex(textureSprite.getMaxU(), textureSprite.getMaxV()).color(red, green, blue, alpha).endVertex();
            buffer.pos(bottomRX, bottomRY, bottomLZ).tex(textureSprite.getMinU(), textureSprite.getMinV()).color(red, green, blue, alpha).endVertex();

            buffer.pos(pointX, pointY, pointZ).tex(textureSprite.getMinU(),       textureSprite.getMaxV()).color(red, green, blue, alpha).endVertex();
            buffer.pos(bottomRX, bottomRY, bottomLZ).tex(textureSprite.getMaxU(), textureSprite.getMaxV()).color(red, green, blue, alpha).endVertex();
            buffer.pos(bottomLX, bottomLY, bottomLZ).tex(textureSprite.getMinU(), textureSprite.getMinV()).color(red, green, blue, alpha).endVertex();

            buffer.pos(pointX, pointY, pointZ).tex(textureSprite.getMinU(),       textureSprite.getMaxV()).color(red, green, blue, alpha).endVertex();
            buffer.pos(bottomLX, bottomLY, bottomRZ).tex(textureSprite.getMaxU(), textureSprite.getMaxV()).color(red, green, blue, alpha).endVertex();
            buffer.pos(bottomRX, bottomRY, bottomRZ).tex(textureSprite.getMinU(), textureSprite.getMinV()).color(red, green, blue, alpha).endVertex();
        } else {
            buffer.pos(pointX, pointY, pointZ).tex(textureSprite.getMaxU(),       textureSprite.getMinV()).color(red, green, blue, alpha).endVertex();
            buffer.pos(bottomLX, bottomLY, bottomRZ).tex(textureSprite.getMinU(), textureSprite.getMinV()).color(red, green, blue, alpha).endVertex();
            buffer.pos(bottomLX, bottomLY, bottomLZ).tex(textureSprite.getMaxU(), textureSprite.getMaxV()).color(red, green, blue, alpha).endVertex();

            buffer.pos(pointX, pointY, pointZ).tex(textureSprite.getMaxU(),       textureSprite.getMinV()).color(red, green, blue, alpha).endVertex();
            buffer.pos(bottomRX, bottomRY, bottomLZ).tex(textureSprite.getMinU(), textureSprite.getMinV()).color(red, green, blue, alpha).endVertex();
            buffer.pos(bottomRX, bottomRY, bottomRZ).tex(textureSprite.getMaxU(), textureSprite.getMaxV()).color(red, green, blue, alpha).endVertex();

            buffer.pos(pointX, pointY, pointZ).tex(textureSprite.getMaxU(),       textureSprite.getMinV()).color(red, green, blue, alpha).endVertex();
            buffer.pos(bottomLX, bottomLY, bottomLZ).tex(textureSprite.getMinU(), textureSprite.getMinV()).color(red, green, blue, alpha).endVertex();
            buffer.pos(bottomRX, bottomRY, bottomLZ).tex(textureSprite.getMaxU(), textureSprite.getMaxV()).color(red, green, blue, alpha).endVertex();

            buffer.pos(pointX, pointY, pointZ).tex(textureSprite.getMaxU(),       textureSprite.getMinV()).color(red, green, blue, alpha).endVertex();
            buffer.pos(bottomRX, bottomRY, bottomRZ).tex(textureSprite.getMinU(), textureSprite.getMinV()).color(red, green, blue, alpha).endVertex();
            buffer.pos(bottomLX, bottomLY, bottomRZ).tex(textureSprite.getMaxU(), textureSprite.getMaxV()).color(red, green, blue, alpha).endVertex();
        }


        Tessellator.getInstance().draw();
        GlStateManager.enableLighting();
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
    }


}
