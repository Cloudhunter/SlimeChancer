package uk.co.cloudhunter.slimechancer.common;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;

import java.io.IOException;

public class BlockStateSerializer implements DataSerializer<IBlockState> {

    public static final DataSerializer<IBlockState> BLOCK_STATE = new BlockStateSerializer();

    public void write(PacketBuffer buf, IBlockState value)
    {
        buf.writeVarInt(Block.getStateId(value));
    }
    public IBlockState read(PacketBuffer buf) throws IOException
    {
        int i = buf.readVarInt();
        return Block.getStateById(i);
    }
    public DataParameter<IBlockState> createKey(int id)
    {
        return new DataParameter<>(id, this);
    }
    public IBlockState copyValue(IBlockState value)
    {
        return value;
    }
}
