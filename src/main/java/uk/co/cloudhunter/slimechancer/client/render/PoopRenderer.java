package uk.co.cloudhunter.slimechancer.client.render;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import uk.co.cloudhunter.slimechancer.common.entities.EntityPoop;

import javax.annotation.Nullable;

public class PoopRenderer extends Render<EntityPoop>
{

    public PoopRenderer(RenderManager renderManager)
    {
        super(renderManager);
        System.out.println("test");
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     *
     * @param entity
     */
    @Nullable
    @Override
    protected ResourceLocation getEntityTexture(EntityPoop entity)
    {
        return null;
    }

    @Override
    public void doRender(EntityPoop entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();

        GlStateManager.pushMatrix();

        GlStateManager.translate(x, y, z);

        float f3 = (((float)entity.ticksExisted + partialTicks) / 20.0F + 0) * (180F / (float)Math.PI);

        GlStateManager.rotate(f3, 0.0F, 1.0F, 0.0F);

        GlStateManager.pushMatrix();

        bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        renderPyramid(buffer, 0 , 1, 0, 0.25, 0.5, false);
        renderPyramid(buffer, 0, 0, 0, 0.25, 0.5, true);

        GlStateManager.popMatrix();
        GlStateManager.popMatrix();

        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    public static void drawBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, double x, double y, double z) {
        GlStateManager.pushMatrix();
        //GlStateManager.glLineWidth(2F);
        //GlStateManager.disableTexture2D();

        GlStateManager.disableLighting();
        BufferBuilder buffer = Tessellator.getInstance().getBuffer();

        buffer.begin(7, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);

        buffer.pos(minX, minY, minZ).tex(0, 0).lightmap(240, 240).color(1, 0, 0, 0F).endVertex();
        buffer.pos(maxX, minY, minZ).tex(0, 0).lightmap(240, 240).color(1, 0, 0, 1F).endVertex();
        buffer.pos(maxX, minY, maxZ).tex(0, 0).lightmap(240, 240).color(1, 0, 0, 1F).endVertex();
        buffer.pos(maxX, minY, maxZ).tex(0, 0).lightmap(240, 240).color(1, 0, 0, 1F).endVertex();


        buffer.pos(minX, maxY, minZ).tex(0, 0).lightmap(240, 240).color(1, 0, 0, 1F).endVertex();
        buffer.pos(minX, maxY, maxZ).tex(0, 0).lightmap(240, 240).color(1, 0, 0, 1F).endVertex();
        buffer.pos(maxX, maxY, maxZ).tex(0, 0).lightmap(240, 240).color(1, 0, 0, 1F).endVertex();
        buffer.pos(maxX, maxY, maxZ).tex(0, 0).lightmap(240, 240).color(1, 0, 0, 1F).endVertex();


        buffer.pos(minX, minY, minZ).tex(0, 0).lightmap(240, 240).color(1, 0, 0, 1F).endVertex();
        buffer.pos(minX, maxY, minZ).tex(0, 0).lightmap(240, 240).color(1, 0, 0, 1F).endVertex();
        buffer.pos(maxX, maxY, minZ).tex(0, 0).lightmap(240, 240).color(1, 0, 0, 1F).endVertex();
        buffer.pos(maxX, maxY, minZ).tex(0, 0).lightmap(240, 240).color(1, 0, 0, 1F).endVertex();


        buffer.pos(minX, minY, maxZ).tex(0, 0).lightmap(240, 240).color(1, 0, 0, 1F).endVertex();
        buffer.pos(maxX, minY, maxZ).tex(0, 0).lightmap(240, 240).color(1, 0, 0, 1F).endVertex();
        buffer.pos(maxX, maxY, maxZ).tex(0, 0).lightmap(240, 240).color(1, 0, 0, 1F).endVertex();
        buffer.pos(maxX, maxY, maxZ).tex(0, 0).lightmap(240, 240).color(1, 0, 0, 1F).endVertex();


        buffer.pos(maxX, minY, minZ).tex(0, 0).lightmap(240, 240).color(1, 0, 0, 1F).endVertex();
        buffer.pos(maxX, maxY, minZ).tex(0, 0).lightmap(240, 240).color(1, 0, 0, 1F).endVertex();
        buffer.pos(maxX, maxY, maxZ).tex(0, 0).lightmap(240, 240).color(1, 0, 0, 1F).endVertex();
        buffer.pos(maxX, maxY, maxZ).tex(0, 0).lightmap(240, 240).color(1, 0, 0, 1F).endVertex();


        buffer.pos(minX, minY, minZ).tex(0, 0).lightmap(240, 240).color(1, 0, 0, 1F).endVertex();
        buffer.pos(minX, minY, maxZ).tex(0, 0).lightmap(240, 240).color(1, 0, 0, 1F).endVertex();
        buffer.pos(minX, maxY, maxZ).tex(0, 0).lightmap(240, 240).color(1, 0, 0, 1F).endVertex();
        buffer.pos(minX, maxY, maxZ).tex(0, 0).lightmap(240, 240).color(1, 0, 0, 1F).endVertex();


        Tessellator.getInstance().draw();
        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }


    public static void renderPyramid(BufferBuilder buffer, double pointX, double pointY, double pointZ, double width, double height, boolean inverse) {
        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        buffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);

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


        buffer.pos(pointX, pointY, pointZ).tex(0, 0).color(1, 0, 0, 0F).endVertex();
        buffer.pos(pointX, pointY, pointZ).tex(0, 0).color(1, 0, 0, 0F).endVertex();
        if (!inverse)
        {
            buffer.pos(bottomLX, bottomLY, bottomLZ).tex(0, 0).color(1, 0, 0, 1F).endVertex();
            buffer.pos(bottomLX, bottomLY, bottomRZ).tex(0, 0).color(1, 0, 0, 1F).endVertex();
        }
        else
        {
            buffer.pos(bottomLX, bottomLY, bottomRZ).tex(0, 0).color(1, 0, 0, 1F).endVertex();
            buffer.pos(bottomLX, bottomLY, bottomLZ).tex(0, 0).color(1, 0, 0, 1F).endVertex();
        }

        buffer.pos(pointX, pointY, pointZ).tex(0, 0).color(0, 1, 0, 0F).endVertex();
        buffer.pos(pointX, pointY, pointZ).tex(0, 0).color(0, 1, 0, 0F).endVertex();
        if (!inverse)
        {
            buffer.pos(bottomRX, bottomRY, bottomRZ).tex(0, 0).color(0, 1, 0, 1F).endVertex();
            buffer.pos(bottomRX, bottomRY, bottomLZ).tex(0, 0).color(0, 1, 0, 1F).endVertex();
        }
        else
        {
            buffer.pos(bottomRX, bottomRY, bottomLZ).tex(0, 0).color(0, 1, 0, 1F).endVertex();
            buffer.pos(bottomRX, bottomRY, bottomRZ).tex(0, 0).color(0, 1, 0, 1F).endVertex();
        }


        buffer.pos(pointX, pointY, pointZ).tex(0, 0).color(0, 0, 1, 0F).endVertex();
        buffer.pos(pointX, pointY, pointZ).tex(0, 0).color(0, 0, 1, 0F).endVertex();
        if (!inverse)
        {
            buffer.pos(bottomRX, bottomRY, bottomLZ).tex(0, 0).color(0, 0, 1, 1F).endVertex();
            buffer.pos(bottomLX, bottomLY, bottomLZ).tex(0, 0).color(0, 0, 1, 1F).endVertex();
        }
        else
        {
            buffer.pos(bottomLX, bottomLY, bottomLZ).tex(0, 0).color(0, 0, 1, 1F).endVertex();
            buffer.pos(bottomRX, bottomRY, bottomLZ).tex(0, 0).color(0, 0, 1, 1F).endVertex();
        }

        buffer.pos(pointX, pointY, pointZ).tex(0, 0).color(1, 0, 1, 0F).endVertex();
        buffer.pos(pointX, pointY, pointZ).tex(0, 0).color(1, 0, 1, 0F).endVertex();

        if (!inverse)
        {
            buffer.pos(bottomLX, bottomLY, bottomRZ).tex(0, 0).color(1, 0, 1, 1F).endVertex();
            buffer.pos(bottomRX, bottomRY, bottomRZ).tex(0, 0).color(1, 0, 1, 1F).endVertex();
        }
        else
        {
            buffer.pos(bottomRX, bottomRY, bottomRZ).tex(0, 0).color(1, 0, 1, 1F).endVertex();
            buffer.pos(bottomLX, bottomLY, bottomRZ).tex(0, 0).color(1, 0, 1, 1F).endVertex();
        }


        Tessellator.getInstance().draw();
        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }


}
