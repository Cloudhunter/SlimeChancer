package uk.co.cloudhunter.slimechancer.client.render;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import uk.co.cloudhunter.slimechancer.common.entities.EntityPoop;

import javax.annotation.Nullable;

public class PoopRenderer extends Render<EntityPoop>
{

    public PoopRenderer(RenderManager renderManager)
    {
        super(renderManager);
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

}
