package me.av306.keybindsgaloreplus;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class KeybindsGalorePlus implements ClientModInitializer
{
    public static ConfigManager configManager;

    public static final Logger LOGGER = LoggerFactory.getLogger( "keybingsgaloreplus" );

    private static KeyBinding configreloadKeybind;
    @Override
    public void onInitializeClient()
    {
        LOGGER.info( "KeybindsGalore Plus initialising..." );

        // Read configs
        configManager = new ConfigManager( FabricLoader.getInstance()
                .getConfigDir()
                .resolve( "keybindsgaloreplus_config.properties" ) // TODO: put this in a field?
                .toString()
        );

        // Config reload key
        configreloadKeybind = KeyBindingHelper.registerKeyBinding( new KeyBinding(
                "key.keybindsgaloreplus.reloadconfigs",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                "category.keybindsgaloreplus.keybinds"
        ) );

        ClientTickEvents.END_CLIENT_TICK.register( client ->
        {
            while ( configreloadKeybind.wasPressed() )
            {
                configManager.readConfigFile();
                client.player.sendMessage( Text.translatable( "text.keybindsgaloreplus.configreloaded" ) );
            }

        } );
    }
}
