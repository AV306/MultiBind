package me.av306.multibind;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class MultiBind implements ClientModInitializer
{
    public static ConfigManager configManager;

    public static final Logger LOGGER = LoggerFactory.getLogger( "multibind" );
    @Override
    public void onInitializeClient()
    {
        LOGGER.info( "MultiBind beginning initialisation..." );

        // Read configs
        configManager = new ConfigManager( FabricLoader.getInstance()
                .getConfigDir()
                .resolve( "multibind_config.properties" )
                .toString()
        );
    }
}
