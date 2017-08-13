package uk.co.cloudhunter.slimechancer.common;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.*;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.SaveHandlerMP;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import uk.co.cloudhunter.slimechancer.common.entities.EntityMySlime;

import javax.annotation.Nullable;
import java.util.UUID;

public class SingleBlockWorld extends World
{
    public static BlockPos pos = new BlockPos(5, 5, 5);
    private IBlockState storedBlockState = Blocks.AIR.getDefaultState();
    private TileEntity storedTileEntity;
    private IChunkProvider storedChunkProvider;
    private EntityMySlime storedSlime = null;
    private Chunk storedChunk;
    private World us = this;

    public SingleBlockWorld(IBlockState blockState)
    {
        this(blockState, null, false);
        //storedBlockState = blockState;
    }

    public SingleBlockWorld(IBlockState blockState, EntityMySlime slime, boolean clientWorld)
    {
        super(new SaveHandlerMP(), new WorldInfo(new WorldSettings(0, GameType.CREATIVE, true, false, WorldType.DEFAULT), "mahfake"),
                new WorldProvider()
                {
                    public DimensionType getDimensionType()
                    {
                        return DimensionType.OVERWORLD;
                    }
                }, new Profiler(), clientWorld);
        this.storedSlime = slime;
        this.chunkProvider = this.createChunkProvider();
        this.setBlockState(SingleBlockWorld.pos, blockState);
    }

    public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean _default)
    {
        return true;
    }

    @Override
    protected IChunkProvider createChunkProvider()
    {
        if (storedChunkProvider != null)
            return storedChunkProvider;
        else
            return storedChunkProvider = new IChunkProvider()
            {
                @Nullable
                @Override
                public Chunk getLoadedChunk(int x, int z)
                {
                    if (storedChunk == null)
                        storedChunk = new Chunk(us, x, z)
                        {
                            @Nullable
                            public IBlockState setBlockState(BlockPos pos, IBlockState state)
                            {
                                if (storedSlime != null && !us.isRemote)
                                    storedSlime.setSlimeType(state);
                                return storedBlockState = state;
                            }

                            public IBlockState getBlockState(final int x, final int y, final int z)
                            {
                                if (x == 5 && y == 5 && z == 5)
                                    return storedBlockState;
                                if (x == 5 && y == 6 && z == 5 && storedBlockState.getBlock() instanceof BlockChest)
                                    return Blocks.AIR.getDefaultState();
                                return Blocks.DIAMOND_BLOCK.getDefaultState();
                            }

                            public void addTileEntity(BlockPos pos, TileEntity tileEntityIn)
                            {
                                if (!pos.equals(SingleBlockWorld.pos))
                                    return;
                                if (tileEntityIn.getWorld() != us) //Forge don't call unless it's changed, could screw up bad mods.
                                    tileEntityIn.setWorld(us);

                                tileEntityIn.setPos(pos);

                                if (this.getBlockState(pos).getBlock().hasTileEntity(this.getBlockState(pos)))
                                {
                                    if (storedTileEntity != null)
                                    {
                                        storedTileEntity.invalidate();
                                    }

                                    tileEntityIn.validate();
                                    storedTileEntity = tileEntityIn;
                                }
                            }

                            @Override
                            public void removeTileEntity(BlockPos pos)
                            {
                                if (!pos.equals(SingleBlockWorld.pos))
                                    return;
                                TileEntity tileentity = storedTileEntity;

                                storedTileEntity = null;

                                if (tileentity != null)
                                {
                                    tileentity.invalidate();
                                }

                            }

                            @Nullable
                            private TileEntity createNewTileEntity(BlockPos pos)
                            {
                                IBlockState iblockstate = this.getBlockState(pos);
                                Block block = iblockstate.getBlock();
                                return !block.hasTileEntity(iblockstate) ? null : block.createTileEntity(us, iblockstate);
                            }

                            @Nullable
                            public TileEntity getTileEntity(BlockPos pos, Chunk.EnumCreateEntityType p_177424_2_)
                            {
                                TileEntity tileentity = storedTileEntity;

                                if (tileentity != null && tileentity.isInvalid())
                                {
                                    storedTileEntity = null;
                                    tileentity = null;
                                }

                                if (tileentity == null)
                                {
                                    if (p_177424_2_ == Chunk.EnumCreateEntityType.IMMEDIATE)
                                    {
                                        tileentity = this.createNewTileEntity(pos);
                                        this.getWorld().setTileEntity(pos, tileentity);
                                    }
                                    else if (p_177424_2_ == Chunk.EnumCreateEntityType.QUEUED)
                                    {
                                        tileentity = this.createNewTileEntity(pos);
                                        this.getWorld().setTileEntity(pos, tileentity);
                                    }
                                }

                                return storedTileEntity = tileentity;
                            }

                        };
                    return storedChunk;
                }

                @Override
                public Chunk provideChunk(int x, int z)
                {
                    //if (x == 0 && z == 0)
                    return getLoadedChunk(x, z);
                    //return null;
                }

                @Override
                public boolean tick()
                {
                    return false;
                }

                @Override
                public String makeString()
                {
                    return "fakeymcfakeface";
                }

                @Override
                public boolean isChunkGeneratedAt(int x, int z)
                {
                    return true;
                }
            };
    }

    @Override
    protected boolean isChunkLoaded(int x, int z, boolean allowEmpty)
    {
        return true;
    }

    public void playRecord(BlockPos blockPositionIn, @Nullable SoundEvent soundEventIn)
    {
        //TODO: if linked entity do in real world? *shrugs*

    }

    public void spawnParticle(EnumParticleTypes particleType, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int... parameters)
    {
        //TODO: if linked entity do in real world? *shrugs*
    }

    public void playSound(@Nullable EntityPlayer player, double x, double y, double z, SoundEvent soundIn, SoundCategory category, float volume, float pitch)
    {
        //TODO: if linked entity do in real world? *shrugs*

    }

    public void playSound(double x, double y, double z, SoundEvent soundIn, SoundCategory category, float volume, float pitch, boolean distanceDelay)
    {
        //TODO: if linked entity do in real world? *shrugs*
    }

    public boolean canSeeSky(BlockPos pos)
    {
        return true;
    }

    public boolean canBlockSeeSky(BlockPos pos)
    {
        return true;
    }

    public void spawnAlwaysVisibleParticle(int p_190523_1_, double p_190523_2_, double p_190523_4_, double p_190523_6_, double p_190523_8_, double p_190523_10_, double p_190523_12_, int... p_190523_14_)
    {
        //TODO: if linked entity do in real world? *shrugs*
    }

    public boolean addWeatherEffect(Entity entityIn)
    {
        //TODO: if linked entity do in real world? *shrugs*
        return false;
    }

    /**
     * Called when an entity is spawned in the world. This includes players.
     */
    public boolean spawnEntity(Entity entityIn)
    {
        if (storedSlime != null)
        {
            entityIn.setWorld(storedSlime.world);
            entityIn.setPositionAndRotation(storedSlime.posX, storedSlime.posY, storedSlime.posZ, entityIn.rotationYaw, entityIn.rotationPitch);
            return storedSlime.world.spawnEntity(entityIn);
        }
        //TODO: if linked entity do in real world? *shrugs*
        return false;
    }

    public void onEntityAdded(Entity entityIn)
    {
    }

    public void onEntityRemoved(Entity entityIn)
    {
    }

    public void removeEntity(Entity entityIn)
    {
    }

    public void removeEntityDangerously(Entity entityIn)
    {
    }

    public void updateEntities()
    {
        TileEntity tileentity = storedTileEntity;

        if (tileentity == null)
        {
            return;
        }

        if (!tileentity.isInvalid() && tileentity.hasWorld())
        {
            BlockPos blockpos = tileentity.getPos();

            if (this.isBlockLoaded(blockpos, false))
            {
                try
                {
                    ((ITickable) tileentity).update();
                }
                catch (Throwable throwable)
                {
                    CrashReport crashreport2 = CrashReport.makeCrashReport(throwable, "Ticking block entity");
                    CrashReportCategory crashreportcategory2 = crashreport2.makeCategory("Block entity being ticked");
                    tileentity.addInfoToCrashReport(crashreportcategory2);
                    if (true)
                    {
                        net.minecraftforge.fml.common.FMLLog.log.fatal(crashreport2.getCompleteReport());
                        tileentity.invalidate();
                        this.removeTileEntity(tileentity.getPos());
                    }
                    else
                        throw new ReportedException(crashreport2);
                }
            }
        }

        if (tileentity.isInvalid())
        {

            if (this.isBlockLoaded(tileentity.getPos()))
            {
                //Forge: Bugfix: If we set the tile entity it immediately sets it in the chunk, so we could be desyned
                Chunk chunk = this.getChunkFromBlockCoords(tileentity.getPos());
                if (chunk.getTileEntity(tileentity.getPos(), net.minecraft.world.chunk.Chunk.EnumCreateEntityType.CHECK) == tileentity)
                    chunk.removeTileEntity(tileentity.getPos());
            }
            storedTileEntity = null;
        }

        // TODO: See if have to defer to this or similar
        //}

        /*if (!this.addedTileEntityList.isEmpty())
        {
            for (int j1 = 0; j1 < this.addedTileEntityList.size(); ++j1)
            {
                TileEntity tileentity1 = this.addedTileEntityList.get(j1);

                if (!tileentity1.isInvalid())
                {
                    if (!this.loadedTileEntityList.contains(tileentity1))
                    {
                        this.addTileEntity(tileentity1);
                    }

                    if (this.isBlockLoaded(tileentity1.getPos()))
                    {
                        Chunk chunk = this.getChunkFromBlockCoords(tileentity1.getPos());
                        IBlockState iblockstate = chunk.getBlockState(tileentity1.getPos());
                        chunk.addTileEntity(tileentity1.getPos(), tileentity1);
                        this.notifyBlockUpdate(tileentity1.getPos(), iblockstate, iblockstate, 3);
                    }
                }
            }

            this.addedTileEntityList.clear();
        }*/
    }

    @Nullable
    public EntityPlayer getClosestPlayer(double x, double y, double z, double p_190525_7_, Predicate<Entity> p_190525_9_)
    {
        //TODO: if linked entity do in real world? *shrugs*
        return null;
    }

    public boolean isAnyPlayerWithinRangeAt(double x, double y, double z, double range)
    {
        //TODO: if linked entity do in real world? *shrugs*
        return false;
    }

    @Nullable
    public EntityPlayer getNearestAttackablePlayer(double posX, double posY, double posZ, double maxXZDistance, double maxYDistance, @Nullable Function<EntityPlayer, Double> playerToDouble, @Nullable Predicate<EntityPlayer> p_184150_12_)
    {
        //TODO: if linked entity do in real world? *shrugs*
        return null;
    }

    /**
     * Find a player by name in this world.
     */
    @Nullable
    public EntityPlayer getPlayerEntityByName(String name)
    {
        if (storedSlime != null)
            return storedSlime.world.getPlayerEntityByName(name);
        return null;
    }

    @Nullable
    public EntityPlayer getPlayerEntityByUUID(UUID uuid)
    {
        if (storedSlime != null)
            return storedSlime.world.getPlayerEntityByUUID(uuid);
        return null;
    }

    @SideOnly(Side.CLIENT)
    public void joinEntityInSurroundings(Entity entityIn)
    {
        //TODO: if linked entity do in real world? *shrugs*
        // Doesn't appear necessary as real world will handle spawning on client
    }

}
