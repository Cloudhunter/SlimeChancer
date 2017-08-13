package uk.co.cloudhunter.slimechancer.client.render;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import uk.co.cloudhunter.slimechancer.SlimeChancer;
import uk.co.cloudhunter.slimechancer.common.SingleBlockWorld;
import uk.co.cloudhunter.slimechancer.common.entities.EntityMySlime;

import java.awt.*;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;

public class MySlimeRenderer extends RenderLiving<EntityMySlime>
{


    private boolean blockRender = false;
    private static final ResourceLocation SLIME_TEXTURES = SlimeChancer.proxy.getSlimeTexture();
    public Color color = null;

    public HashMap<IBlockState, Color> colors = new HashMap<>();

    public MySlimeRenderer(RenderManager p_i47193_1_)
    {
        super(p_i47193_1_, new MyModelSlime(16), 0.25F);
        this.addLayer(new MyLayerSlimeGel(this));
    }

    private Color getAverageColour(TextureAtlasSprite sprite)
    {
        int format = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_COMPONENTS);
        int width = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
        int height = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);
        int channels;
        int byteCount;
        switch (format)
        {
            case GL11.GL_RGB:
                channels = 3;
                break;
            case GL11.GL_RGBA:
            default:
                channels = 4;
                break;
        }
        byteCount = width * height * channels;
        ByteBuffer bytes = BufferUtils.createByteBuffer(byteCount);
        GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, format, GL11.GL_UNSIGNED_BYTE, bytes);
        final String ext = "PNG";
        int xStart = sprite.getOriginX();
        int yStart = sprite.getOriginY();
        int wid = sprite.getIconWidth() + xStart;
        int hei = sprite.getIconHeight() + yStart;
        long sumr = 0, sumg = 0, sumb = 0;
        int num = 0;
        for (int x = xStart; x < wid; x++)
        {
            for (int y = yStart; y < hei; y++)
            {
                int i = (x + (width * y)) * channels;
                int r = bytes.get(i) & 0xFF;
                int g = bytes.get(i + 1) & 0xFF;
                int b = bytes.get(i + 2) & 0xFF;
                sumr += r;
                sumg += g;
                sumb += b;
                num++;
            }
        }
        int finalRed = (int) sumr / num;
        int finalGreen = (int) sumg / num;
        int finalBlue = (int) sumb / num;
        return new Color(finalRed, finalGreen, finalBlue);
    }

    @Override
    public void doRender(EntityMySlime entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        renderBlock(entity, x, y, z, entityYaw, partialTicks);

        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    private TextureAtlasSprite getTexture(IBakedModel ibakedmodel, IBlockState state, EnumFacing facing)
    {
        List<BakedQuad> quadList = ibakedmodel.getQuads(state, facing, 0L);
        TextureAtlasSprite sprite = quadList.isEmpty() ? ibakedmodel.getParticleTexture() : quadList.get(0).getSprite();
        return sprite == null ? null : sprite;
    }

    public void renderBlock(EntityMySlime entity, double x, double y, double z, float yaw, float partialTicks)
    {

        IBlockState iblockstate = entity.getSlimeType();

        if (colors.get(iblockstate) == null)
        {
            if (iblockstate.getRenderType() != EnumBlockRenderType.MODEL)
            {
                colors.put(iblockstate, new Color(255, 255, 255, 255));
            }
            else
            {
                Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
                IBakedModel modelForState = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelForState(iblockstate);
                TextureAtlasSprite textureSprite = getTexture(modelForState, iblockstate, EnumFacing.NORTH);
                colors.put(iblockstate, getAverageColour(textureSprite));
            }

        }


        if (iblockstate != null)
        {
            if (iblockstate.getRenderType() != EnumBlockRenderType.INVISIBLE && iblockstate.getRenderType() != EnumBlockRenderType.ENTITYBLOCK_ANIMATED)
            {
                World world = entity.world;

                this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

                GlStateManager.pushMatrix();
                GlStateManager.translate((float) x, (float) y, (float) z);
                GlStateManager.disableLighting();

                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder bufferbuilder = tessellator.getBuffer();

                if (this.renderOutlines)
                {
                    GlStateManager.enableColorMaterial();
                    GlStateManager.enableOutlineMode(this.getTeamColor(entity));
                }

                bufferbuilder.begin(7, DefaultVertexFormats.BLOCK);
                BlockPos blockpos = new BlockPos(entity.posX, entity.getEntityBoundingBox().maxY, entity.posZ);

                bufferbuilder.setTranslation(-blockpos.getX() - 0.5, -blockpos.getY() + 0.16, -blockpos.getZ() - 0.5);

                float f = this.interpolateRotation(entity.prevRenderYawOffset, entity.renderYawOffset, partialTicks);
                float f8 = this.handleRotationFloat(entity, partialTicks);
                this.applyRotations(entity, f8, f, partialTicks);

                blockRender = true;
                float f4 = this.prepareScale(entity, partialTicks);
                blockRender = false;

                BlockRendererDispatcher blockrendererdispatcher = Minecraft.getMinecraft().getBlockRendererDispatcher();
                GlStateManager.enableAlpha();
                blockrendererdispatcher.getBlockModelRenderer().renderModel(world, blockrendererdispatcher.getModelForState(iblockstate), iblockstate, blockpos, bufferbuilder, false, MathHelper.getPositionRandom(entity.getOrigin()));
                bufferbuilder.setTranslation(0.0D, 0.0D, 0.0D);
                tessellator.draw();

                if (this.renderOutlines)
                {
                    GlStateManager.disableOutlineMode();
                    GlStateManager.disableColorMaterial();
                }

                GlStateManager.enableLighting();
                GlStateManager.popMatrix();

            }

            if (iblockstate.getBlock().hasTileEntity(iblockstate))
            {
                if (iblockstate.getBlock().hasTileEntity(iblockstate))
                {
                    if (entity.singleBlockWorld != null)
                    {
                        TileEntity tileentityIn = entity.singleBlockWorld.getTileEntity(SingleBlockWorld.pos);

                        if (tileentityIn != null)
                        {
                            GlStateManager.pushMatrix();


                            //GlStateManager.translate(0.5, 0.5, 0.5);

                            GlStateManager.translate(x, y, z);

                            float f = this.interpolateRotation(entity.prevRenderYawOffset, entity.renderYawOffset, partialTicks);
                            float f8 = this.handleRotationFloat(entity, partialTicks);
                            this.applyRotations(entity, f8, f, partialTicks);


                            //GlStateManager.scale(2, 2, 2);

                            blockRender = true;
                            float f4 = this.prepareScale(entity, partialTicks);
                            blockRender = false;

                            //GlStateManager.scale(1.05, 1.05, 1.05);

                            GlStateManager.translate(-0.5, 0.16, -0.5);


//                            GlStateManager.translate(-0.10, 0.75, 0.25);
                            tileRender(tileentityIn, 0, 0, 0, partialTicks);

                            GlStateManager.popMatrix();
                        }


                    }
                }
            }
        }
    }

    private void tileRender(TileEntity tileentityIn, double entityX, double entityY, double entityZ, float partialTicks)
    {
        TileEntityRendererDispatcher.instance.render(tileentityIn, entityX, entityY, entityZ, 0.0F, 1.0F);
    }

    @Override
    protected void preRenderCallback(EntityMySlime entitylivingbaseIn, float partialTickTime)
    {
        GlStateManager.scale(0.999F, 0.999F, 0.999F);
        float f1 = (float) entitylivingbaseIn.getSlimeSize();
        if (blockRender) f1 = f1 / 2.65f;
        float f2 = (entitylivingbaseIn.prevSquishFactor + (entitylivingbaseIn.squishFactor - entitylivingbaseIn.prevSquishFactor) * partialTickTime) / (f1 * 0.5F + 1.0F);
        float f3 = 1.0F / (f2 + 1.0F);
        GlStateManager.scale(f3 * f1, 1.0F / f3 * f1, f3 * f1);
    }

    @Override
    public float prepareScale(EntityMySlime entitylivingbaseIn, float partialTicks)
    {
        GlStateManager.enableRescaleNormal();
        if (!blockRender) GlStateManager.scale(-1.0F, -1.0F, 1.0F);
        this.preRenderCallback(entitylivingbaseIn, partialTicks);
        float f = 0.0625F;
        if (!blockRender) GlStateManager.translate(0.0F, -1.501F, 0.0F);
        return 0.0625F;
    }

    protected ResourceLocation getEntityTexture(EntityMySlime entity)
    {
        return SLIME_TEXTURES;
    }
}
