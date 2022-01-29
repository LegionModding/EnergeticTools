package com.legionmodding.energetictools.util;

import com.legionmodding.energetictools.handler.registry.ItemRegistry;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class Reference
{
    public static final String MOD_ID = "energetictools";

    public static final ItemGroup CREATIVE_TAB_ITEMS = new ItemGroup("creativeTabItems")
    {
        @Override
        public ItemStack makeIcon()
        {
            return new ItemStack(ItemRegistry.BASIC_ENERGETIC_AXE.get());
        }
    };

    public static final String VERSION = "0.0.2";
}
