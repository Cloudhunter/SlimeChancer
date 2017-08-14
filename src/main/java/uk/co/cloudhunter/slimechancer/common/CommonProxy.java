package uk.co.cloudhunter.slimechancer.common;

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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class CommonProxy
{

    private final ArrayList<IBlockState> validOreStates;
    private final ArrayList<String> alreadyProcessed;
    private final String prefix = "ore";

    public CommonProxy()
    {
        validOreStates = new ArrayList<>();
        alreadyProcessed = new ArrayList<>();
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
        if (oreName.length() >= 3 && oreName.substring(0, 3).equals(prefix))
        {
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

            if (state == null) {
                System.out.println("WARNING: Could not find processed block for " + oreName); //TODO: Change to log
            } else {
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
}
