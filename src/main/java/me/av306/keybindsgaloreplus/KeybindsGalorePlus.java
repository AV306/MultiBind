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
    public static ConfigManager CONFIG_MANAGER;

    public static final Logger LOGGER = LoggerFactory.getLogger( "multibind" );

    private static KeyBinding configreloadKeybind;
    @Override
    public void onInitializeClient()
    {
        LOGGER.info( "KeybindsGalore Plus is initialising..." );

        // Read configs
        CONFIG_MANAGER = new ConfigManager( FabricLoader.getInstance()
                .getConfigDir()
                .resolve("keybindsgalore_plus_config.properties")
                .toString()
        );

        // Config reload key
        configreloadKeybind = KeyBindingHelper.registerKeyBinding( new KeyBinding(
                "key.multibind.reloadconfigs",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                "category.multibind.keybinds"
        ) );

        ClientTickEvents.END_CLIENT_TICK.register( client ->
        {
            while ( configreloadKeybind.wasPressed() )
            {
                CONFIG_MANAGER.readConfigFile();
                client.player.sendMessage( Text.translatable( "text.multibind.configreloaded" ) );
            }

        } );

        LOGGER.info( "KeybindsGalore Plus finished initialisation!");
    }
}
