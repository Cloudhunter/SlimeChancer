package uk.co.cloudhunter.slimechancer.common.entities;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import uk.co.cloudhunter.slimechancer.SlimeChancer;

import static uk.co.cloudhunter.slimechancer.common.BlockStateSerializer.BLOCK_STATE;

public class EntityPoop extends Entity
{
    public EntityPoop(World worldIn)
    {
        super(worldIn);
    }

    private static final DataParameter<IBlockState> SLIME_TYPE = EntityDataManager.createKey(EntityPoop.class, BLOCK_STATE);

    static
    {
        DataSerializers.registerSerializer(BLOCK_STATE);
    }

    public IBlockState getSlimeType()
    {
        return this.dataManager.get(SLIME_TYPE);
    }


    @Override
    protected void entityInit()
    {
        int i = this.rand.nextInt(SlimeChancer.proxy.getOreStates().size());

        IBlockState state = SlimeChancer.proxy.getOreStates().get(i);

        this.dataManager.register(SLIME_TYPE, state);
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();

        if (!this.hasNoGravity())
        {
            this.motionY -= 0.03999999910593033D;
        }

        this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);

        float f = 0.98F;

        if (this.onGround)
        {
            BlockPos underPos = new BlockPos(MathHelper.floor(this.posX), MathHelper.floor(this.getEntityBoundingBox().minY) - 1, MathHelper.floor(this.posZ));
            net.minecraft.block.state.IBlockState underState = this.world.getBlockState(underPos);
            f = underState.getBlock().getSlipperiness(underState, this.world, underPos, this) * 0.98F;
        }

        this.motionX *= (double)f;
        this.motionY *= 0.9800000190734863D;
        this.motionZ *= (double)f;

        if (this.onGround)
        {
            this.motionY *= -0.5D;
        }
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound)
    {

    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound)
    {

    }

    private void setSlimeType(IBlockState state, boolean updateWorld)
    {
        if (isDead) return;
        if (Blocks.AIR.getDefaultState() == state)
        {
            this.setDead();
            return;
        }

        this.dataManager.set(SLIME_TYPE, state);
    }


}
