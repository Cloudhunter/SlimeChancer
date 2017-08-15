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
import uk.co.cloudhunter.slimechancer.client.render.util.RenderUtil;
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
    //public Color color = null;

    public HashMap<IBlockState, Color> colors = new HashMap<>();

    public MySlimeRenderer(RenderManager p_i47193_1_)
    {
        super(p_i47193_1_, new MyModelSlime(16), 0.25F);
        this.addLayer(new MyLayerSlimeGel(this));
    }

    @Override
    public void doRender(EntityMySlime entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        renderBlock(entity, x, y, z, entityYaw, partialTicks);

        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @SuppressWarnings("unchecked")
    public void renderBlock(EntityMySlime entity, double x, double y, double z, float yaw, float partialTicks)
    {
        // This may look like I know what I am doing, but I assure you - I do not.

        IBlockState iblockstate = entity.getSlimeType();

        HashMap colors = (HashMap<IBlockState, Color>) SlimeChancer.proxy.getColors();

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
                TextureAtlasSprite textureSprite = RenderUtil.getTexture(modelForState, iblockstate, EnumFacing.NORTH);
                colors.put(iblockstate, RenderUtil.getAverageColour(textureSprite));
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

                            GlStateManager.translate(x, y, z);

                            float f = this.interpolateRotation(entity.prevRenderYawOffset, entity.renderYawOffset, partialTicks);
                            float f8 = this.handleRotationFloat(entity, partialTicks);
                            this.applyRotations(entity, f8, f, partialTicks);

                            blockRender = true;
                            float f4 = this.prepareScale(entity, partialTicks);
                            blockRender = false;

                            GlStateManager.translate(-0.5, 0.16, -0.5);

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
        //TODO: Remove blockRender stuff and just pass as variable with a default override to pass false.
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
        //TODO: Remove blockRender stuff and just pass as variable with a default override to pass false.
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
