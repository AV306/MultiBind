package me.av306.multibind;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Locale;
import java.lang.reflect.Field;

public class ConfigManager
{
    private final String configFilePath;

    private File configFile;
    
    public ConfigManager( String configFilePath )
    {
        this.configFilePath = configFilePath;
        this.checkConfigFile();
        this.readConfigFile();
    }

    private void checkConfigFile()
    {
        this.configFile = new File( this.configFilePath );
        if ( !this.configFile.exists() )
        {
            try ( FileOutputStream fos = new FileOutputStream( this.configFile ); )
            {
                this.configFile.createNewFile();
                
                MultiBind.LOGGER.warn( "MultiBind config file not found, copying embedded one" );
                fos.write( this.getClass().getResourceAsStream( "/multibind_config.properties" ).readAllBytes() );
            }
            catch ( IOException ioe )
            {
                MultiBind.LOGGER.error( "IOException while copying default configs!" );
                ioe.printStackTrace();
            }
        }
    }

    private void readConfigFile()
    {
        try ( BufferedReader reader = new BufferedReader( new FileReader( this.configFilePath ) ) )
        {
            // Iterate over each line in the file
            for ( String line : reader.lines().toArray( String[]::new ) )
            {
                // Skip comments and blank lines
                if ( line.startsWith( "#" ) || line.isBlank() ) continue;
                
                // Split it by the equals sign (.properties format)
                String[] entry = line.split( "=" );

                try
                {
                    Field f = KeybindSelectorScreen.class.getDeclaredField( entry[0].toUpperCase( Locale.ENGLISH ) );
                    //MultiBind.LOGGER.info( f.getType().getName() );
                    if ( f.getType().isAssignableFrom( short.class ) )
                    {
                        // Short value
                        f.setShort( null, Short.parseShort(
                                entry[1].replace( "0x", "" ),
                                16 )
                        );
                    }
                    else if ( f.getType().isAssignableFrom( int.class ) )
                    {
                        // Integer value
                        f.setInt(
                            null, 
                            Integer.parseInt( entry[1] )
                        );
                    }
                    else if ( f.getType().isAssignableFrom( float.class ) )
                    {
                        f.setFloat(
                            null,
                            Float.parseFloat( entry[1] )
                        );
                    }
                    else
                    {
                        MultiBind.LOGGER.error( "Unrecognised data type for config entry {}", line );
                    }
                }
                catch ( ArrayIndexOutOfBoundsException oobe )
                {
                    MultiBind.LOGGER.warn( "Malformed config entry: {}", line );
                }
                catch ( NoSuchFieldException nsfe )
                {
                    MultiBind.LOGGER.warn( "No matching field found for config entry: {}", line );
                }
                catch ( IllegalAccessException illegal )
                {
                    MultiBind.LOGGER.error( "Could not set field involved in: {}", line );
                    illegal.printStackTrace();
                }
            }
        }
        catch ( IOException ioe )
        {
            MultiBind.LOGGER.error( "IOException while reading config file: {}", ioe.getMessage() );
        }
    }
}