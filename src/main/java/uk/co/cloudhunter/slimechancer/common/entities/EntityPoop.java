package uk.co.cloudhunter.slimechancer.common.entities;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntityPoop extends Entity
{
    public EntityPoop(World worldIn)
    {
        super(worldIn);
    }

    @Override
    protected void entityInit()
    {
        //System.out.println("Init");
    }

    @Override
    public void onUpdate()
    {
        //System.out.println("updating");
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     *
     * @param compound
     */
    @Override
    protected void readEntityFromNBT(NBTTagCompound compound)
    {

    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     *
     * @param compound
     */
    @Override
    protected void writeEntityToNBT(NBTTagCompound compound)
    {

    }

}
