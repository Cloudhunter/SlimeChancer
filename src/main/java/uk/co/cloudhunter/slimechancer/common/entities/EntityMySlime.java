package uk.co.cloudhunter.slimechancer.common.entities;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import uk.co.cloudhunter.slimechancer.common.SingleBlockWorld;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;

import static uk.co.cloudhunter.slimechancer.common.BlockStateSerializer.BLOCK_STATE;

public class EntityMySlime extends EntitySlime
{

    private BlockPos origin;
    public SingleBlockWorld singleBlockWorld;
//    public IBlockState type;

    private static final DataParameter<IBlockState> SLIME_TYPE = EntityDataManager.<IBlockState>createKey(EntityMySlime.class, BLOCK_STATE);

    static
    {
        DataSerializers.registerSerializer(BLOCK_STATE);
    }

    public EntityMySlime(World worldIn)
    {
        super(worldIn);
        origin = new BlockPos(this);
    }

    @Override
    public boolean canDespawn()
    {
        return false;
    }

    @Override
    public void setDead()
    {
        setSlimeSize(0, false);
        super.setDead();
        if (singleBlockWorld != null)
        {
/*            BlockPos pos = new BlockPos(0, 0, 0)
            TileEntity entity = singleBlockWorld.getTileEntity(pos);
            if (entity != null) {
                IBlockState state = singleBlockWorld.getBlockState(pos);
                state.getBlock().onBlockDestroyedByPlayer(singleBlockWorld, pos, state);

            }*/
            singleBlockWorld.setBlockState(SingleBlockWorld.pos, Blocks.AIR.getDefaultState());
        }
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();

        ItemStack oreDiamond = OreDictionary.getOres("oreDiamond").get(0);
        IBlockState tempState = ((ItemBlock) oreDiamond.getItem()).getBlock().getDefaultState();

        this.dataManager.register(SLIME_TYPE, tempState);
    }

    public void setSlimeType(IBlockState state)
    {
        setSlimeType(state, false);
    }

    private void setSlimeType(IBlockState state, boolean updateWorld)
    {
        if (isDead) return;
        if (Blocks.AIR.getDefaultState() == state)
        {
            this.setDead();
            return;
        }
        if (updateWorld)
        {
            singleBlockWorld.setBlockState(SingleBlockWorld.pos, state);
        }
        System.out.println(state);
        this.dataManager.set(SLIME_TYPE, state);
    }

    public IBlockState getSlimeType()
    {
        return this.dataManager.get(SLIME_TYPE);
    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key)
    {
        if (SLIME_TYPE.equals(key) && world.isRemote)
        {
            IBlockState state = this.getSlimeType();
            System.out.println(state);
            if (singleBlockWorld == null)
            {
                singleBlockWorld = new SingleBlockWorld(state, this, true);
            }
            else
            {
                singleBlockWorld.setBlockState(SingleBlockWorld.pos, state);
            }
        }

        super.notifyDataManagerChange(key);
    }

    @Override
    public void onEntityUpdate()
    {
        super.onEntityUpdate();
        if (singleBlockWorld != null)
        {
            singleBlockWorld.updateEntities();
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);
        IBlockState blockstate = NBTUtil.readBlockState(compound);
        if (blockstate.getBlock().hasTileEntity(blockstate))
        {
            String teClass = compound.getString("teclass");
            if (teClass != null && !teClass.isEmpty())
            {
                TileEntity tileEntity = null;
                try
                {
                    tileEntity = (TileEntity) Class.forName(teClass).getConstructor().newInstance();
                }
                catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e)
                {
                    e.printStackTrace();
                }

                if (tileEntity != null)
                {
                    singleBlockWorld = new SingleBlockWorld(blockstate, this, false);
                    tileEntity.setWorld(singleBlockWorld);
                    NBTTagCompound teCompound = compound.getCompoundTag("tetag");
                    tileEntity.readFromNBT(teCompound);
                    singleBlockWorld.setTileEntity(tileEntity.getPos(), tileEntity);
                    return;
                }
            }
        }
        setSlimeType(blockstate);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound)
    {
        super.writeEntityToNBT(compound);
        IBlockState blockState = getSlimeType();
        NBTUtil.writeBlockState(compound, blockState);
        if (blockState.getBlock().hasTileEntity(blockState))
        {
            if (singleBlockWorld != null)
            {
                NBTTagCompound innerCompound = new NBTTagCompound();
                TileEntity tileEntity = singleBlockWorld.getTileEntity(SingleBlockWorld.pos);
                if (tileEntity != null)
                {
                    compound.setString("teclass", tileEntity.getClass().getCanonicalName());
                    tileEntity.writeToNBT(innerCompound);
                    compound.setTag("tetag", innerCompound);
                }

            }
        }
    }

    public BlockPos getOrigin()
    {
        return origin;
    }

    protected boolean spawnCustomParticles()
    {
        int i = this.getSlimeSize();
        for (int j = 0; j < i * 8; ++j)
        {
            float f = this.rand.nextFloat() * ((float) Math.PI * 2F);
            float f1 = this.rand.nextFloat() * 0.5F + 0.5F;
            float f2 = MathHelper.sin(f) * (float) i * 0.5F * f1;
            float f3 = MathHelper.cos(f) * (float) i * 0.5F * f1;
            World world = this.world;
            double d0 = this.posX + (double) f2;
            double d1 = this.posZ + (double) f3;
            world.spawnParticle(EnumParticleTypes.BLOCK_DUST, d0, this.getEntityBoundingBox().minY, d1, 0.0D, 0.0D, 0.0D, Block.getStateId(getSlimeType()));
        }
        return true;
    }

    @Nullable
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata)
    {
        /*int i = this.rand.nextInt(SlimeChancer.proxy.getOreStates().size());

        this.setSlimeType(SlimeChancer.proxy.getOreStates().get(i));*/
        IBlockState state = Blocks.BEACON.getDefaultState();
        singleBlockWorld = new SingleBlockWorld(state, this, world.isRemote);

        //((TileEntitySkull)singleBlockWorld.getTileEntity(SingleBlockWorld.pos)).setPlayerProfile(new GameProfile(null, "AutismWithSkip"));

        return super.onInitialSpawn(difficulty, livingdata);
    }

    @Override
    @SuppressWarnings("deprecation")
    protected boolean processInteract(EntityPlayer player, EnumHand hand)
    {
        if (hand.equals(EnumHand.MAIN_HAND) && player.isSneaking())
        {
            if (!world.isRemote)
            {
                ItemStack stack = player.getHeldItem(hand);
                if (!stack.isEmpty())
                {
                    if (stack.getItem() instanceof ItemBlock)
                    {
                        System.out.println("Setting " + stack);
                        setSlimeType(((ItemBlock) stack.getItem()).getBlock().getStateFromMeta(stack.getMetadata()), true);
                        return true;
                    }
                }
            }
        }
        if (singleBlockWorld != null && !player.isSneaking())
        {
            //System.out.println(singleBlockWorld);
            IBlockState state = singleBlockWorld.getBlockState(SingleBlockWorld.pos);
            //System.out.println(((TileEntityBeacon)singleBlockWorld.getTileEntity(singleBlockWorld.pos)).getLevels());
            return state.getBlock().onBlockActivated(singleBlockWorld, SingleBlockWorld.pos, state, player, hand, EnumFacing.NORTH, 0, 0, 0);
        }

        return super.processInteract(player, hand);
    }
}
