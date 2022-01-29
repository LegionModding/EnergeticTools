package com.legionmodding.energetictools.item;


import com.legionmodding.energetictools.handler.registry.ItemRegistry;
import com.legionmodding.energetictools.util.AxeModes;
import com.legionmodding.energetictools.util.FillUtil;
import com.legionmodding.energetictools.util.IToolAxe;
import com.legionmodding.energetictools.util.Reference;
import com.legionmodding.yalm.handler.KeyHandler;
import com.legionmodding.yalm.handler.StringHandler;
import com.legionmodding.yalm.handler.energy.EnergyHandler;
import com.legionmodding.yalm.handler.energy.IEnergyContainer;
import com.legionmodding.yalm.handler.energy.IEnergyItem;
import com.legionmodding.yalm.handler.energy.capability.EnergyItemCapability;
import com.legionmodding.yalm.handler.energy.capability.EnergyStorageCapability;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemBasicEnergeticAxe extends AxeItem implements IEnergyContainer, IEnergyItem, IToolAxe
{
    private static int maxEnergy;
    private final AxeModes mode;

    public ItemBasicEnergeticAxe(AxeModes mode, int maxEnergy)
    {
        super(ItemTier.DIAMOND, 10.0F, 10.0F, new Item.Properties().tab(Reference.CREATIVE_TAB_ITEMS));
        this.mode = mode;
        this.maxEnergy = maxEnergy;
    }

    @Override
    public int receiveEnergy(ItemStack container, int energy, boolean simulate) 
    {
        return EnergyHandler.receiveEnergy(container, energy, simulate);
    }

    @Override
    public int extractEnergy(ItemStack container, int energy, boolean simulate)
    {
        return EnergyHandler.extractEnergy(container, energy, simulate);
    }

    @Override
    public int getEnergyStored(ItemStack container)
    {
        return EnergyHandler.getEnergyStored(container);
    }

    @Override
    public int getMaxEnergyStored(ItemStack container)
    {
        return getMaxEnergy();
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack)
    {
        return true;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack)
    {
        if (stack.getTag() == null)
        {
            EnergyHandler.setDefaultEnergyTag(stack, 0);
        }
        return 1D - ((double) stack.getTag().getInt(EnergyHandler.ENERGY_NBT) / (double) getMaxEnergyStored(stack));
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag)
    {
        //Todo convert this into translation json

        super.appendHoverText(itemStack, world, tooltip, flag);

        AxeModes mode = getCurrentMode(itemStack);
        tooltip.add(new StringTextComponent(TextFormatting.DARK_PURPLE + "Shift right-click to change Axe mode."));
        tooltip.add(new StringTextComponent(TextFormatting.GOLD + "Current Mode: " + mode.getName().toUpperCase()));

        if (mode == AxeModes.MODE_STRIP)
        {
            tooltip.add(new StringTextComponent(TextFormatting.DARK_PURPLE + "Use as a wood stripping/un-stripping tool:"));
            tooltip.add(new StringTextComponent(TextFormatting.WHITE + "Right click to strip.un-strip wood log."));
        }

        else if (mode == AxeModes.MODE_HEAL)
        {
            tooltip.add(new StringTextComponent(TextFormatting.WHITE + "Use as a healing/feeding tool:"));
            tooltip.add(new StringTextComponent(TextFormatting.WHITE + "Right click to gain 1 food, health and saturation point"));
        }

        if (KeyHandler.isShiftKeyDown())
        {
            tooltip.add(new StringTextComponent("Current energy stored:").withStyle(TextFormatting.GOLD));
            tooltip.add(
                    StringHandler.formatNumber(getEnergyStored(itemStack))
                            .append(" / ")
                            .append(StringHandler.formatNumber(getMaxEnergyStored(itemStack)))
                            .append(" " + "FE").withStyle(TextFormatting.GOLD)
            );
        }
    }

    public AxeModes getMode()
    {
        return mode;
    }

    @Override
    public AxeModes getAxeMode(ItemStack itemStack)
    {
        return getCurrentMode(itemStack);
    }

    public static AxeModes getCurrentMode(ItemStack itemStack)
    {
        if (itemStack.getItem() instanceof ItemBasicEnergeticAxe)
        {
            return ((ItemBasicEnergeticAxe) itemStack.getItem()).getMode();
        }

        return AxeModes.MODE_STRIP;
    }

    @Override
    public int getMaxEnergy()
    {
        return maxEnergy;
    }

    @Override
    public void fillItemCategory(ItemGroup itemGroup, NonNullList<ItemStack> items)
    {
        ItemStack empty = new ItemStack(this);
        FillUtil.setDefaultTags(empty);
        items.add(empty);
        ItemStack full = new ItemStack(this);
        FillUtil.setDefaultTags(full);
        EnergyHandler.setDefaultEnergyTag(full, getMaxEnergyStored(full));
        items.add(full);

    }

    @Override
    public boolean isPowered()
    {
        return true;
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT nbt)
    {
        return new EnergyItemCapability<>(new EnergyStorageCapability(this, stack));
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment)
    {
        return false;
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand)
    {
        ItemStack stack = player.getItemInHand(hand);

        if (!world.isClientSide)
        {
            if(mode == AxeModes.MODE_HEAL)
            {
                if(getEnergyStored(stack) >= 600)
                {
                    if(player.isCreative())
                    {
                        return ActionResult.pass(stack);
                    }

                    float food = player.getFoodData().getFoodLevel();
                    float health = player.getHealth();
                    float saturation = player.getFoodData().getSaturationLevel();

                    player.getFoodData().setFoodLevel((int) (food + 1));
                    player.setHealth(health + 1);
                    player.getFoodData().setSaturation(saturation + 1);
                    extractEnergy(stack, 600, false);
                }

                else
                {
                    player.sendMessage(new StringTextComponent("Cannot heal and feed you, you don't have enough RF stored :(").withStyle(TextFormatting.RED), null);
                }
            }

            if(player.isShiftKeyDown())
            {
                AxeModes mode = getCurrentMode(stack);
                CompoundNBT tag = stack.getTag();
                ItemStack newStack;

                if (mode == AxeModes.MODE_STRIP)
                {
                    mode = AxeModes.MODE_HEAL;
                    newStack = new ItemStack(ItemRegistry.BASIC_ENERGETIC_AXE_HEAL.get());
                }

                else
                {
                    mode = AxeModes.MODE_STRIP;
                    newStack = new ItemStack(ItemRegistry.BASIC_ENERGETIC_AXE.get());
                }

                newStack.setTag(tag);
                player.setItemInHand(hand, newStack);
                player.sendMessage(new StringTextComponent("Your axe is now in " + mode.getName() + " mode.").withStyle(TextFormatting.DARK_PURPLE), null);
            }
        }

        return super.use(world, player, hand);
    }

    @Override
    @Nonnull
    public ActionResultType useOn(ItemUseContext context)
    {
        World world = context.getLevel();
        PlayerEntity player = context.getPlayer();
        ItemStack stack = context.getItemInHand();
        BlockPos pos = context.getClickedPos();
        Block block = world.getBlockState(pos).getBlock();

        if (!world.isClientSide)
        {
            if(mode == AxeModes.MODE_STRIP)
            {
                if(getEnergyStored(stack) < 200)
                {
                    player.sendMessage(new StringTextComponent("Cannot strip/un-strip block. You don't have enough RF :(").withStyle(TextFormatting.RED), null);
                    return ActionResultType.FAIL;
                }

                if(player.isCreative())
                {
                    return ActionResultType.SUCCESS;
                }

                if(block == Blocks.ACACIA_LOG)
                {
                    world.setBlockAndUpdate(pos, Blocks.STRIPPED_ACACIA_LOG.defaultBlockState());
                    extractEnergy(stack, 200, false);
                }

                else if(block == Blocks.BIRCH_LOG)
                {
                    world.setBlockAndUpdate(pos, Blocks.STRIPPED_BIRCH_LOG.defaultBlockState());
                    extractEnergy(stack, 200, false);
                }

                else if(block == Blocks.DARK_OAK_LOG)
                {
                    world.setBlockAndUpdate(pos, Blocks.STRIPPED_DARK_OAK_LOG.defaultBlockState());
                    extractEnergy(stack, 200, false);
                }

                else if(block == Blocks.JUNGLE_LOG)
                {
                    world.setBlockAndUpdate(pos, Blocks.STRIPPED_JUNGLE_LOG.defaultBlockState());
                    extractEnergy(stack, 200, false);
                }

                else if(block == Blocks.OAK_LOG)
                {
                    world.setBlockAndUpdate(pos, Blocks.STRIPPED_OAK_LOG.defaultBlockState());
                    extractEnergy(stack, 200, false);
                }

                else if(block == Blocks.SPRUCE_LOG)
                {
                    world.setBlockAndUpdate(pos, Blocks.STRIPPED_SPRUCE_LOG.defaultBlockState());
                    extractEnergy(stack, 200, false);
                }

                else if(block == Blocks.STRIPPED_ACACIA_LOG)
                {
                    world.setBlockAndUpdate(pos, Blocks.ACACIA_LOG.defaultBlockState());
                    extractEnergy(stack, 200, false);
                }

                else if(block == Blocks.STRIPPED_BIRCH_LOG)
                {
                    world.setBlockAndUpdate(pos, Blocks.BIRCH_LOG.defaultBlockState());
                    extractEnergy(stack, 200, false);
                }

                else if(block == Blocks.STRIPPED_DARK_OAK_LOG)
                {
                    world.setBlockAndUpdate(pos, Blocks.DARK_OAK_LOG.defaultBlockState());
                    extractEnergy(stack, 200, false);
                }

                else if(block == Blocks.STRIPPED_JUNGLE_LOG)
                {
                    world.setBlockAndUpdate(pos, Blocks.JUNGLE_LOG.defaultBlockState());
                    extractEnergy(stack, 200, false);
                }

                else if(block == Blocks.STRIPPED_OAK_LOG)
                {
                    world.setBlockAndUpdate(pos, Blocks.OAK_LOG.defaultBlockState());
                    extractEnergy(stack, 200, false);
                }

                else if(block == Blocks.STRIPPED_SPRUCE_LOG)
                {
                    world.setBlockAndUpdate(pos, Blocks.SPRUCE_LOG.defaultBlockState());
                    extractEnergy(stack, 200, false);
                }
            }
        }

        world.playSound(player, pos, SoundEvents.AXE_STRIP, SoundCategory.BLOCKS, 1.0f, 1.0F);

        return ActionResultType.SUCCESS;
    }
}
