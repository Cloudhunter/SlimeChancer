package uk.co.cloudhunter.slimechancer;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import uk.co.cloudhunter.slimechancer.common.CommonProxy;
import uk.co.cloudhunter.slimechancer.common.entities.EntityMySlime;

import java.awt.*;
import java.time.temporal.TemporalAmount;

@Mod(modid = SlimeChancer.MODID, version = SlimeChancer.VERSION)
public class SlimeChancer
{
    public static final String MODID = "slimechancer";
    public static final String VERSION = "1.0";
    ;

    @SidedProxy(clientSide = "uk.co.cloudhunter.slimechancer.client.ClientProxy", serverSide = "uk.co.cloudhunter.slimechancer.client.CommonProxy")
    public static CommonProxy proxy;

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        proxy.init();
    }

    @EventHandler
    public void preinit(FMLPreInitializationEvent event)
    {
        EntityRegistry.registerModEntity(new ResourceLocation("slimechancer", "slimeymcslimeface"), EntityMySlime.class, "slimeymcslimeface", 0, this, 80, 3, true, 0xFF00FF, 0x00FF00);
        proxy.preInit();
    }
}
