package net.hvb007.keybindsgalore;

import com.google.common.collect.Maps;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//logger
//import net.minecraft.client.MinecraftClient;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;


public class KeybindsManager {
//    private static final Logger LOGGER = LogManager.getLogger();

    // To HBV007og:
    // I can't thank you enough for all the comments in the code,
    // I was worried I'd have to actually understand every line in every file
    // to do anything!
    // I hope you have fun on your modding/programming travels! :D
    // - Blender (AV306)

    // Creates an Hashmap that maps conflicting keys to the list of actions they perform
    // allowing us to get an easy list of what each key should do
    private static final Map<InputUtil.Key, List<KeyBinding>> conflictingKeys = Maps.newHashMap();

    // This creates the ArrayList of conflicting actions for us to fill in the above HM
    // I can't claim to understand how this works either (yet)
    public static boolean handleConflict( InputUtil.Key key )
    {
        List<KeyBinding> matches = new ArrayList<>();

        KeyBinding[] allKeys = MinecraftClient.getInstance().options.allKeys;

        // Look for a KeyBinding bound to the key that was just pressed
        // and add it to the running list
        for ( KeyBinding bind : allKeys )
            if ( bind.matchesKey( key.getCode(), -1 ) )
                matches.add( bind );

        // More than one matching KeyBinding, found conflicts!
        if ( matches.size() > 1 )
        {
            KeybindsManager.conflictingKeys.put(key, matches);
            //LOGGER.info("Conflicting key: " + key);

            // Define the array of keys that cannot be multi-bound
            InputUtil.Key[] illegalKeys = {
                    InputUtil.fromTranslationKey( "key.keyboard.tab" ),
                    InputUtil.fromTranslationKey( "key.keyboard.caps.lock" ),
                    InputUtil.fromTranslationKey( "key.keyboard.left.shift" ),
                    InputUtil.fromTranslationKey( "key.keyboard.left.control" ),
                    InputUtil.fromTranslationKey( "key.keyboard.space" ),
                    InputUtil.fromTranslationKey( "key.keyboard.left.alt" ),
                    InputUtil.fromTranslationKey( "key.keyboard.w" ),
                    InputUtil.fromTranslationKey( "key.keyboard.a" ),
                    InputUtil.fromTranslationKey( "key.keyboard.s" ),
                    InputUtil.fromTranslationKey( "key.keyboard.d" )
            };

            // Check if the key is in the array
            boolean illegalKeyIsMultiBound = false;
            for ( InputUtil.Key arrayKey : illegalKeys )
            {
                // Remove the illegal key from the multibinding list
                if ( arrayKey.equals( key)  )
                {
                    illegalKeyIsMultiBound = true;
                    KeybindsManager.conflictingKeys.remove( key );
                    break;
                }
            }

            return !illegalKeyIsMultiBound;

        } else
        {
            // No conflicts, not worth handling
            KeybindsManager.conflictingKeys.remove( key );
            return false;
        }
    }

    // Checks if there is a binding conflict on this key
    public static boolean hasConflicts( InputUtil.Key key ) {
        return conflictingKeys.containsKey(key);
    }

    // Initializes and opens the Circle selector thingy
    public static void openConflictMenu( InputUtil.Key key )
    {
        KeybindsScreen screen = new KeybindsScreen();
        screen.setConflictedKey (key );
        MinecraftClient.getInstance().setScreen( screen );
    }


    // Shortcut method to get conflicts on a key
    public static List<KeyBinding> getConflicts( InputUtil.Key key ) {
        return conflictingKeys.get(key);
    }

}
