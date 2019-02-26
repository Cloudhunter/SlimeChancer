package uk.co.cloudhunter.slimechancer.client;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import uk.co.cloudhunter.slimechancer.client.render.MySlimeRenderer;
import uk.co.cloudhunter.slimechancer.client.render.PoopRenderer;
import uk.co.cloudhunter.slimechancer.client.render.texture.GreyscaleTexture;
import uk.co.cloudhunter.slimechancer.client.render.util.RenderUtil;
import uk.co.cloudhunter.slimechancer.common.CommonProxy;
import uk.co.cloudhunter.slimechancer.common.entities.EntityMySlime;
import uk.co.cloudhunter.slimechancer.common.entities.EntityPoop;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorConvertOp;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.function.Function;

public class ClientProxy extends CommonProxy implements IResourceManagerReloadListener
{

    private ResourceLocation SLIME_TEXTURE;

    private HashMap<IBlockState, Color> colors;
    private int maxUV = 16;

    public ClientProxy()
    {
        super();
        SLIME_TEXTURE = new ResourceLocation("slimechancer", "assets/slimetexture");
    }

    @Override
    public HashMap<IBlockState, Color> getColors()
    {
        return colors;
    }

    @Override
    public void init()
    {
        super.init();
        IReloadableResourceManager manager = (IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager();
        manager.registerReloadListener(this);
    }

    @Override
    public void preInit()
    {
        super.preInit();
        RenderingRegistry.registerEntityRenderingHandler(EntityMySlime.class, manager -> new MySlimeRenderer(manager));
        RenderingRegistry.registerEntityRenderingHandler(EntityPoop.class, manager -> new PoopRenderer(manager));
    }

    @Override
    public float getMaxUV()
    {
        return maxUV;
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager)
    {
        ITextureObject tex = new GreyscaleTexture(getSlimeTexture(), new ResourceLocation("textures/entity/slime/slime.png"));
        try
        {
            tex.loadTexture(resourceManager);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        Minecraft mc = Minecraft.getMinecraft();
        TextureManager manager = mc.getTextureManager();
        manager.loadTexture(getSlimeTexture(), tex);

        colors = new HashMap();

        for (IBlockState state : getOreStates()) {
            RenderUtil.getColorFromState(state);
        }
    }

    public TextureAtlasSprite SLIME_BLOCK_TEXTURE;

    @SubscribeEvent
    public void texturesStitched(TextureStitchEvent.Pre event)
    {
        TextureMap map = event.getMap();
        map.setTextureEntry(SLIME_BLOCK_TEXTURE = new MyTextureAtlasSprite("slimechancer:slime_block", map.getMipmapLevels()));
    }

    public Object getSlimeBlockTexture()
    {
        return SLIME_BLOCK_TEXTURE;
    }

    public ResourceLocation getSlimeTexture()
    {
        return SLIME_TEXTURE;
    }

    @SubscribeEvent
    public void renderTest(RenderGameOverlayEvent event) {
        switch (event.getType()) {
            case HOTBAR:
        }
    }

    public class MyTextureAtlasSprite extends TextureAtlasSprite
    {
        int mipmaplevels;
        protected MyTextureAtlasSprite(String spriteName, int mipmaplevelsIn) {
            super(spriteName);
            mipmaplevels = mipmaplevelsIn + 1;
        }

        @Override
        public boolean hasCustomLoader(IResourceManager manager, ResourceLocation location) {
            return true;
        }

        @Override
        public boolean load(IResourceManager manager, ResourceLocation location, Function<ResourceLocation, TextureAtlasSprite> textureGetter) {
            try {
                this.framesTextureData.clear();
                IResource resource = manager.getResource(new ResourceLocation("textures/blocks/slime.png"));
                InputStream inputStream = resource.getInputStream();
                BufferedImage bufferedImage = TextureUtil.readBufferedImage(inputStream);
                BufferedImageOp op = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
                BufferedImage image = op.filter(bufferedImage, null);
                int[][] aint = new int[mipmaplevels][];
                aint[0] = new int[image.getWidth() * image.getHeight()];
                image.getRGB(0, 0, image.getWidth(), image.getHeight(), aint[0], 0, image.getWidth());
                height = image.getHeight();
                width = image.getWidth();
                this.framesTextureData.add(aint);
                return false;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return super.load(manager, location, textureGetter);
        }

        public java.util.Collection<ResourceLocation> getDependencies() {
            return com.google.common.collect.ImmutableList.of(new ResourceLocation("blocks/slime"));
        }
    }
}
