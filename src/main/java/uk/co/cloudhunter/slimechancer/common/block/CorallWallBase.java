package uk.co.cloudhunter.slimechancer.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;

public class CorallWallBase extends Block {
    public CorallWallBase() {
        super(Material.IRON);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }
}
