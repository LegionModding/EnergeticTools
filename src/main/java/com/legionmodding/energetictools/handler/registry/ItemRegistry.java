package com.legionmodding.energetictools.handler.registry;

import com.legionmodding.energetictools.item.ItemBasicEnergeticAxe;
import com.legionmodding.energetictools.util.AxeModes;
import com.legionmodding.energetictools.util.Reference;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemRegistry
{
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Reference.MOD_ID);

    /* Axes */

    public static final RegistryObject<Item> BASIC_ENERGETIC_AXE = ITEMS.register("basic_energetic_axe", () -> new ItemBasicEnergeticAxe(AxeModes.MODE_STRIP, 5000));
    public static final RegistryObject<Item> BASIC_ENERGETIC_AXE_HEAL = ITEMS.register("basic_energetic_axe_heal", () -> new ItemBasicEnergeticAxe(AxeModes.MODE_HEAL, 5000));

    public static void registerItems()
    {
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

}
