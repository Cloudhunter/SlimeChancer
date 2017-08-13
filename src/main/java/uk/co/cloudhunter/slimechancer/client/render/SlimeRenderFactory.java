package uk.co.cloudhunter.slimechancer.client.render;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import uk.co.cloudhunter.slimechancer.common.entities.EntityMySlime;

public class SlimeRenderFactory implements IRenderFactory<EntityMySlime>
{

    @Override
    public Render<? super EntityMySlime> createRenderFor(RenderManager manager)
    {
        return new MySlimeRenderer(manager);
    }
}
