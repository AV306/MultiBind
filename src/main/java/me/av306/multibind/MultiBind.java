package me.av306.multibind;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ClientModInitializer;

public class MultiBind implements ClientModInitializer
{
    public static final Logger LOGGER = LoggerFactory.getLogger( "multibind" );
    @Override
    public void onInitializeClient()
    {
        LOGGER.info( "MultiBind beginning initialisation..." );

        // Read configs
        
    }

}
