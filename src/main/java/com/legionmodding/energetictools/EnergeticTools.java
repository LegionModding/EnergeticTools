package com.legionmodding.energetictools;

import com.legionmodding.energetictools.handler.ConfigHandler;
import com.legionmodding.energetictools.handler.registry.ItemRegistry;
import com.legionmodding.energetictools.util.Reference;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(Reference.MOD_ID)
public class EnergeticTools
{
    public static final Logger LOGGER = LogManager.getLogger();

    public EnergeticTools()
    {
        ItemRegistry.registerItems();

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onCommonSetup);
    }

    private void onClientSetup(FMLClientSetupEvent event)
    {

    }

    private void onCommonSetup(FMLCommonSetupEvent event)
    {

    }
}
