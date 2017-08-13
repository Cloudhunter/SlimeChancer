package uk.co.cloudhunter.slimechancer.client;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.oredict.OreDictionary;
import uk.co.cloudhunter.slimechancer.client.render.SlimeRenderFactory;
import uk.co.cloudhunter.slimechancer.client.render.texture.GreyscaleTexture;
import uk.co.cloudhunter.slimechancer.common.CommonProxy;
import uk.co.cloudhunter.slimechancer.common.entities.EntityMySlime;

import java.io.IOException;
import java.util.ArrayList;

public class ClientProxy extends CommonProxy implements IResourceManagerReloadListener {

    public ResourceLocation SLIME_TEXTURE;

    public ClientProxy() {
        super();
        SLIME_TEXTURE = new ResourceLocation("slimechancer", "assets/slimetexture");
    }

    @Override
    public void init() {
        super.init();
        IReloadableResourceManager manager = (IReloadableResourceManager)Minecraft.getMinecraft().getResourceManager();
        manager.registerReloadListener(this);
        onResourceManagerReload(manager);
    }

    @Override
    public void preInit() {
        super.preInit();
        RenderingRegistry.registerEntityRenderingHandler(EntityMySlime.class, new SlimeRenderFactory());
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        ITextureObject tex = new GreyscaleTexture(getSlimeTexture(), new ResourceLocation("textures/entity/slime/slime.png"));
        try {
            tex.loadTexture(resourceManager);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Minecraft mc = Minecraft.getMinecraft();
        TextureManager manager = mc.getTextureManager();
        manager.loadTexture(getSlimeTexture(), tex);
    }

    public ResourceLocation getSlimeTexture() {
        return SLIME_TEXTURE;
    }
}
