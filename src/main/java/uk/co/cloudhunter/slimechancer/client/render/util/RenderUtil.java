package uk.co.cloudhunter.slimechancer.client.render.util;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import uk.co.cloudhunter.slimechancer.SlimeChancer;

import java.awt.*;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;

public class RenderUtil
{
    public static Color getAverageColour(TextureAtlasSprite sprite)
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

    public static Color getColorFromState(IBlockState iblockstate) {
        HashMap colors = (HashMap<IBlockState, Color>) SlimeChancer.proxy.getColors();

        Color color = (Color) colors.get(iblockstate);

        if (color == null)
        {
            System.out.println("Finding colour for iblockstate " + iblockstate);
            if (iblockstate.getRenderType() == EnumBlockRenderType.INVISIBLE)
            {
                color = new Color(255, 255, 255, 255);
            }
            else
            {
                Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
                IBakedModel modelForState = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelForState(iblockstate);
                TextureAtlasSprite textureSprite = RenderUtil.getTexture(modelForState, iblockstate, EnumFacing.NORTH);
                color = RenderUtil.getAverageColour(textureSprite);
            }
            colors.put(iblockstate, color);
            System.out.println("Colour found for iblockstate " + iblockstate);
        }

        return color;

    }

    public static TextureAtlasSprite getTexture(IBakedModel ibakedmodel, IBlockState state, EnumFacing facing)
    {
        List<BakedQuad> quadList = ibakedmodel.getQuads(state, facing, 0L);
        TextureAtlasSprite sprite = quadList.isEmpty() ? ibakedmodel.getParticleTexture() : quadList.get(0).getSprite();
        return sprite;
    }
}
