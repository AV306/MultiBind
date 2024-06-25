package me.av306.keybindsgaloreplus;

import com.google.common.collect.Maps;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


public class KeybindManager
{
//    private static final Logger LOGGER = LogManager.getLogger();

    // To HBV007og:
    // I can't thank you enough for all the comments in the code,
    // I was worried I'd have to actually understand every line in every file
    // to do anything!
    // I hope you have fun on your modding/programming travels! :D
    // - Blender (AV306)

    // Array of keys that cannot be multi-bound
    // TODO: make this configurable
    private static final List<InputUtil.Key> ILLEGAL_KEYS = Arrays.asList(
                    InputUtil.fromTranslationKey( "key.keyboard.tab" ),
                    //InputUtil.fromTranslationKey( "key.keyboard.caps.lock" ),
                    InputUtil.fromTranslationKey( "key.keyboard.left.shift" ),
                    InputUtil.fromTranslationKey( "key.keyboard.left.control" ),
                    InputUtil.fromTranslationKey( "key.keyboard.space" ),
                    //InputUtil.fromTranslationKey( "key.keyboard.left.alt" ),
                    InputUtil.fromTranslationKey( "key.keyboard.w" ),
                    InputUtil.fromTranslationKey( "key.keyboard.a" ),
                    InputUtil.fromTranslationKey( "key.keyboard.s" ),
                    InputUtil.fromTranslationKey( "key.keyboard.d" )
    );

    /**
     * Maps keys to a list of bindings they can trigger
     */
    private static final Map<InputUtil.Key, List<KeyBinding>> conflictingKeys = Maps.newHashMap();

    /**
     * Check if a given key has any binding conflicts, and adds any bindings to its list
     * @param key: The key to check
     * @return If any conflicts were found
     */
    public static boolean handleConflict( InputUtil.Key key )
    {
        // Stop if the key is invalid; invalid keys should never end up in the map
        /*for ( InputUtil.Key illegalKey : illegalKeys )
            if ( key.equals( illegalKey ) ) return false;*/
        if ( ILLEGAL_KEYS.contains( key ) ) return false;

        List<KeyBinding> matches = new ArrayList<>();


        // Look for a KeyBinding bound to the key that was just pressed
        // and add it to the running list
        for ( KeyBinding binding : MinecraftClient.getInstance().options.allKeys )
            if ( binding.matchesKey( key.getCode(), -1 ) ) matches.add( binding );

        // More than one matching KeyBinding, found conflicts!
        if ( matches.size() > 1 )
        {
            // Register the key in our map of conflicting keys
            conflictingKeys.put( key, matches );
            //LOGGER.info("Conflicting key: " + key);

            return true;
        }
        else
        {
            // No conflicts, not worth handling
            // Remove it if it's present (means it used to be valid, but has been changed)
            conflictingKeys.remove( key );
            return false;
        }
    }

    /**
     * Checks if there is a binding conflict on this key
     * @param key: The key to check
     */
    public static boolean hasConflicts( InputUtil.Key key )
    {
        return conflictingKeys.containsKey( key );
    }

    /**
     * Initializes and open the pie menu for the given conflicted key
     */
    public static void openConflictMenu( InputUtil.Key key )
    {
        KeybindSelectorScreen screen = new KeybindSelectorScreen( key );   
        MinecraftClient.getInstance().setScreen( screen );
    }

    /**
     * Shortcut method to get conflicts on a key
     */
    public static List<KeyBinding> getConflicts( InputUtil.Key key )
    {
        return conflictingKeys.get( key );
    }
}
