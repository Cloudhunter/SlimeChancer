package uk.co.cloudhunter.slimechancer.common;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;
import uk.co.cloudhunter.slimechancer.SlimeChancer;
import uk.co.cloudhunter.slimechancer.common.block.CorallField;
import uk.co.cloudhunter.slimechancer.common.entities.EntityMySlime;
import uk.co.cloudhunter.slimechancer.common.entities.EntityPoop;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;

@Mod.EventBusSubscriber(modid = SlimeChancer.MODID)
public class CommonProxy
{

    private final ArrayList<IBlockState> validOreStates;
    private final ArrayList<String> alreadyProcessed;
    // TODO: Read these from config, with default
    private final ArrayList<String> prefixes = new ArrayList<String>() {{
        add("ore");
        add("ingot");
    }};
    private final ArrayList<String> invalidStrings = new ArrayList<String>() {{
        add("Brick");
    }};

    public CommonProxy()
    {
        validOreStates = new ArrayList<>();
        alreadyProcessed = new ArrayList<>();
    }

    public HashMap getColors()
    {
        return null;
    }

    public ResourceLocation getSlimeTexture()
    {
        return null;
    }

    public void init()
    {
        DimensionManager.registerDimension(-9654, DimensionType.OVERWORLD);
    }

    public void preInit()
    {
        EntityRegistry.registerModEntity(new ResourceLocation("slimechancer", "slimeymcslimeface"), EntityMySlime.class, "slimeymcslimeface", 0, SlimeChancer.instance, 80, 3, true, 0xFF00FF, 0x00FF00);
        EntityRegistry.registerModEntity(new ResourceLocation("slimechancer", "poop"), EntityPoop.class, "poop", 1, SlimeChancer.instance, 80, 3, true);
        MinecraftForge.EVENT_BUS.register(this);
        String[] oreNames = OreDictionary.getOreNames();
        for (int i = 0; i < oreNames.length; i++)
        {
            checkAndAddOreState(oreNames[i], null);
        }
    }

    @SubscribeEvent
    public void oreDictAdded(OreDictionary.OreRegisterEvent event)
    {
        checkAndAddOreState(event.getName(), event.getOre());
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
        SlimeChancer.Blocks.init();
        event.getRegistry().registerAll(SlimeChancer.Blocks.getBlocks());
        System.out.println("Registered Blocks");
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        SlimeChancer.Items.init();
        event.getRegistry().registerAll(SlimeChancer.Items.getItems());
        System.out.println("Registered Items");
    }

    private final InventoryCrafting inventoryCrafting = new InventoryCrafting(new Container()
    {
        public boolean canInteractWith(EntityPlayer playerIn)
        {
            return false;
        }
    }, 3, 3);

    @SuppressWarnings("deprecation")
    private void checkAndAddOreState(String oreName, @Nullable ItemStack stack)
    {
        if (alreadyProcessed.contains(oreName)) return;
        for(String prefix : prefixes)
            if (oreName.length() >= prefix.length() && oreName.substring(0, prefix.length()).equals(prefix))
            {
                for(String invalid : invalidStrings)
                    if (oreName.contains(invalid))
                        return;

                IBlockState state = null;
                if (stack == null)
                {
                    NonNullList<ItemStack> ores = OreDictionary.getOres(oreName);
                    for (ItemStack tempStack : ores)
                    {
                        state = getLowestState(tempStack);
                        if (state != null) {
                            break;
                        }
                    }


                }
                else
                {
                    state = getLowestState(stack);
                }

                if (state == null)
                {
                    System.out.println("WARNING: Could not find processed block for " + oreName); //TODO: Change to log
                }
                else
                {
                    if (validOreStates.contains(state)) return;
                    System.out.println("Found " + oreName);
                    validOreStates.add(state);
                    alreadyProcessed.add(oreName);
                }
            }
    }

    @SuppressWarnings("deprecation")
    private IBlockState getLowestState(ItemStack stack) {
        World world = new SingleBlockWorld(Blocks.AIR.getDefaultState());
        ItemStack innerStack;
        Item item = stack.getItem();
        IBlockState state = null;
        if (item instanceof ItemBlock)
        {
            state = ((ItemBlock) item).getBlock().getStateFromMeta(stack.getMetadata());
            world.setBlockState(SingleBlockWorld.pos, state);
            NonNullList<ItemStack> dropList = NonNullList.create();
            ((ItemBlock) item).getBlock().getDrops(dropList, world, SingleBlockWorld.pos, state, 0);
            innerStack = dropList.get(0);
        }
        else
        {
            innerStack = stack;
        }

        ItemStack result = getCraftingResult(innerStack, world);

        if (!result.isEmpty() && result.getItem() instanceof ItemBlock)
        {
            state = ((ItemBlock) result.getItem()).getBlock().getStateFromMeta(result.getMetadata());
        }

        result = FurnaceRecipes.instance().getSmeltingResult(innerStack);
        if (!result.isEmpty())
        {
            item = result.getItem();
            if (item instanceof ItemBlock)
            {
                state = ((ItemBlock) item).getBlock().getStateFromMeta(result.getMetadata());
            }
            else
            {
                result = getCraftingResult(result, world);
                if (!result.isEmpty())
                {
                    state = ((ItemBlock) result.getItem()).getBlock().getStateFromMeta(result.getMetadata());
                }
            }
        }
        return state;
    }

    private ItemStack getCraftingResult(ItemStack stack, World world)
    {
        inventoryCrafting.clear();
        for (int i = 0; i < 9; i++)
            inventoryCrafting.setInventorySlotContents(i, stack);
        ItemStack result = CraftingManager.findMatchingResult(inventoryCrafting, world);
        if (!result.isEmpty())
        {
            Item craftItem = result.getItem();
            if (craftItem instanceof ItemBlock)
                return result;
        }
        else
        {
            inventoryCrafting.clear();
            inventoryCrafting.setInventorySlotContents(0, stack);
            inventoryCrafting.setInventorySlotContents(1, stack);
            inventoryCrafting.setInventorySlotContents(3, stack);
            inventoryCrafting.setInventorySlotContents(4, stack);
            result = CraftingManager.findMatchingResult(inventoryCrafting, world);
            if (!result.isEmpty())
            {
                Item craftItem = result.getItem();
                if (craftItem instanceof ItemBlock)
                    return result;
            }
        }
        return ItemStack.EMPTY;
    }

    public ArrayList<IBlockState> getOreStates()
    {
        return validOreStates;
    }

    public Object getSlimeBlockTexture() {
        return null;
    }

    public float getMaxUV() {
        return 16;
    }
}
