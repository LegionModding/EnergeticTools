package com.legionmodding.energetictools.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public class FillUtil
{
    public static void setDefaultTags(ItemStack stack)
    {
        if (!stack.hasTag())
        {
            CompoundNBT compound = new CompoundNBT();
            stack.setTag(compound);
        }
    }
}
