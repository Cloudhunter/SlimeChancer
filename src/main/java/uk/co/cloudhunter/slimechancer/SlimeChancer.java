package uk.co.cloudhunter.slimechancer;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import uk.co.cloudhunter.slimechancer.common.CommonProxy;
import uk.co.cloudhunter.slimechancer.common.block.CorallField;
import uk.co.cloudhunter.slimechancer.common.entities.EntityMySlime;

import java.awt.*;
import java.time.temporal.TemporalAmount;

@Mod(modid = SlimeChancer.MODID, version = SlimeChancer.VERSION)
public class SlimeChancer
{
    public static final String MODID = "slimechancer";
    public static final String VERSION = "1.0";

    @SidedProxy(clientSide = "uk.co.cloudhunter.slimechancer.client.ClientProxy", serverSide = "uk.co.cloudhunter.slimechancer.client.CommonProxy")
    public static CommonProxy proxy;
    @Mod.Instance
    public static SlimeChancer instance;

    @EventHandler
    public void preinit(FMLPreInitializationEvent event)
    {
        proxy.preInit();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        proxy.init();
    }

    public static class Blocks {
        public static Block CORRALL_FIELD;

        public static Block[] getBlocks() {
            return new Block[] { CORRALL_FIELD };
        }

        public static void init() {
            CORRALL_FIELD = new CorallField();
        }
    }

    public static class Items {
        public static Item CORRALL_FIELD;

        public static Item[] getItems() {
            return new Item[] { CORRALL_FIELD };
        }

        public static void init() {
            CORRALL_FIELD = new ItemBlock(Blocks.CORRALL_FIELD).setRegistryName(Blocks.CORRALL_FIELD.getRegistryName());
        }
    }
}
