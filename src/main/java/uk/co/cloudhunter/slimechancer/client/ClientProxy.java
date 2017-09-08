package uk.co.cloudhunter.slimechancer.client;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
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
import java.io.IOException;
import java.util.HashMap;

public class ClientProxy extends CommonProxy implements IResourceManagerReloadListener
{

    private ResourceLocation SLIME_TEXTURE;

    private HashMap<IBlockState, Color> colors;

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
}
