/*
 * This class is modified from the PSI mod created by Vazkii
 * Psi Source Code: https://github.com/Vazkii/Psi
 *
 * Psi is Open Source and distributed under the
 * Psi License: https://psi.vazkii.net/license.php
 *
 * HVB007: IDK What Part This credit refers to, if you want to know contact https://github.com/CaelTheColher as he is the maker of this mod
 * I am just updating it to 1.20.x
 */
package me.av306.multibind;

import com.mojang.blaze3d.systems.RenderSystem;
import me.av306.multibind.mixin.KeyBindingAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.*;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;

public class KeybindSelectorScreen extends Screen
{
    // Configurable variables

    private static float EXPANSION_FACTOR_WHEN_SELECTED = 1.1f;
    private static int PIE_MENU_MARGIN = 20;
    private static float PIE_MENU_SCALE = 0.6f;
    private static float DEADZONE_SCALE = 0.3f;

    private static int CIRCLE_VERTICES = 64;

    private static short PIE_MENU_COLOR = 0x40;
    private static short PIE_MENU_HIGHLIGHT_COLOR = 0xFF;
    private static short PIE_MENU_COLOR_LIGHTEN_FACTOR = 0x19;
    private static short PIE_MENU_ALPHA = 0x66;

    private static final short LABEL_TEXT_INSET = 4;

    // Instance variables
    private int ticksInScreen = 0;
    private int selectedSector = -1;

    private InputUtil.Key conflictedKey = InputUtil.UNKNOWN_KEY;

    private final MinecraftClient mc;

    private int centreX = 0, centreY = 0;

    private float maxRadius = 0;
    private float deadzoneRadius = 0;

    public KeybindSelectorScreen()
    {
        super( NarratorManager.EMPTY );
        this.mc = MinecraftClient.getInstance();
    }

    public KeybindSelectorScreen( InputUtil.Key key )
    {
        this();

        this.setConflictedKey( key );
    }

    private boolean isFirstFrame = true;
    @Override
    public void render( DrawContext context, int mouseX, int mouseY, float delta )
    {
        super.render( context, mouseX, mouseY, delta );

        //this.selectedSlot = -1;

        // Pixel coords of screen centre
        // Only set these on the first frame
        // Side effect: If window is resized when the screen is open, the menu won't update
        if ( this.isFirstFrame )
        {
            // Set centre of screen
            this.centreX = this.width / 2;
            this.centreY = this.height / 2;

            this.maxRadius = Math.min( (this.centreX * PIE_MENU_SCALE) - PIE_MENU_MARGIN, (this.centreY * PIE_MENU_SCALE) - PIE_MENU_MARGIN );
            this.deadzoneRadius = maxRadius * DEADZONE_SCALE;

            this.isFirstFrame = false;
        }

        // Angle of mouse, in radians from +X-axis, centred on the origin
        double mouseAngle = mouseAngle( this.centreX, this.centreY, mouseX, mouseY );

        float mouseDistanceFromCentre = MathHelper.sqrt( (mouseX - this.centreX) * (mouseX - this.centreX) +
                        (mouseY - this.centreY) * (mouseY - this.centreY) );

        // Determines how many segments to make for the pie menu
        int numberOfSectors = KeybindManager.getConflicts( this.conflictedKey ).size();

        // Calculate the angle occupied by each sector
        float sectorAngle = (MathHelper.TAU) / numberOfSectors;

        // Get the exact sector index that is selected
        this.selectedSector = (int) (mouseAngle / sectorAngle);

        // Deselect slot
        if ( mouseDistanceFromCentre <= this.deadzoneRadius )
            this.selectedSector = -1;
        
        this.renderPieMenu( delta, numberOfSectors, sectorAngle );
        this.renderLabelTexts( context, delta, numberOfSectors, sectorAngle );
    }


    private void renderPieMenu( float delta, int numberOfSectors, float sectorAngle )
    {
        // Setup rendering stuff
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();
        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.setShader( GameRenderer::getPositionColorProgram );

        buf.begin( VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR );

        float startAngle = 0;
        int vertices = CIRCLE_VERTICES / numberOfSectors;
        for ( var sectorIndex = 0; sectorIndex < numberOfSectors; sectorIndex++ )
        {
            float outerRadius = calculateRadius( delta, numberOfSectors, sectorIndex );
            float innerRadius = this.deadzoneRadius;
            short color = PIE_MENU_COLOR;

            if ( sectorIndex % 2 == 0 ) color += PIE_MENU_COLOR_LIGHTEN_FACTOR;

            if ( this.selectedSector == sectorIndex )
            {
                //outerRadius *= EXPANSION_FACTOR_WHEN_SELECTED;
                innerRadius *= EXPANSION_FACTOR_WHEN_SELECTED;
                color = PIE_MENU_HIGHLIGHT_COLOR;
            }

            drawSector( buf, startAngle, sectorAngle, vertices, innerRadius, outerRadius, color );

            startAngle += sectorAngle;
        }

        tess.draw();
    }

    private void drawSector( BufferBuilder buf, float startAngle, float sectorAngle, int vertices, float innerRadius, float outerRadius, short color )
    {
        for ( var i = 0; i <= vertices; i++ )
        {
            float angle = startAngle + ((float) i / vertices) * sectorAngle;

            // Inner vertex
            // FIXME: is the compiler smart enough to optimise the trigo?
            buf.vertex( this.centreX + MathHelper.cos( angle ) * innerRadius, this.centreY + MathHelper.sin( angle ) * innerRadius, 0 )
                    .color( color, color, color, PIE_MENU_ALPHA )
                    .next();

            buf.vertex( this.centreX + MathHelper.cos( angle ) * outerRadius, this.centreY + MathHelper.sin( angle ) * outerRadius, 0 )
                    .color( color, color, color, PIE_MENU_ALPHA )
                    .next();
        }
    }

    private float calculateRadius( float delta, int numberOfSectors, int sectorIndex )
    {
        float radius = Math.max( 0F, Math.min( (this.ticksInScreen + delta - sectorIndex * 6F / numberOfSectors) * 40F, this.maxRadius ) );

        // Expand the sector if selected
        if ( this.selectedSector == sectorIndex ) radius *= EXPANSION_FACTOR_WHEN_SELECTED;
        return radius;
    }

    private void renderLabelTexts(
            DrawContext context,
            float delta,
            int numberOfSectors, float sectorAngle
    )
    {
        for ( var sectorIndex = 0; sectorIndex < numberOfSectors; sectorIndex++ )
        {
            float radius = calculateRadius( delta, numberOfSectors, sectorIndex );
            
            float angle = (sectorIndex + 0.5f) * sectorAngle;

            // Position in the middle of the arc
            float xPos = this.centreX + MathHelper.cos( angle ) * radius;
            float yPos = this.centreY + MathHelper.sin( angle ) * radius;

            KeyBinding action = KeybindManager.getConflicts( this.conflictedKey ).get( sectorIndex );

            // The biggest nagging bug for me
            // Tells you which control category the action goes in
            String actionName =
                    Text.translatable( action.getCategory() ).getString() + ": " +
                    Text.translatable( action.getTranslationKey() ).getString();

            int textWidth = this.textRenderer.getWidth( actionName );

            // Which sides of the screen are we on?
            if ( xPos > this.centreX )
            {
                // Right side
                xPos -= LABEL_TEXT_INSET;

                // Check text going off-screen
                if ( this.width - xPos < textWidth )
                    xPos -= textWidth - width + xPos;
            }
            else
            {
                // Left side
                xPos -= textWidth - LABEL_TEXT_INSET;

                // Check text going off-screen
                if ( xPos < 0 ) xPos = LABEL_TEXT_INSET;
            }

            // Move it closer to the arc
            yPos -= yPos < this.centreY ? LABEL_TEXT_INSET : -LABEL_TEXT_INSET;


            actionName = (this.selectedSector == sectorIndex ? Formatting.UNDERLINE : Formatting.RESET) + actionName;


            context.drawTextWithShadow( textRenderer, actionName, (int) xPos, (int) yPos, 0xFFFFFF );
        }
    }

    public void setConflictedKey( InputUtil.Key key )
    {
        this.conflictedKey = key;
    }

    // Returns the angle of the line bounded by the given coordinates and the mouse position from the vertical axis
    // This is why we study trigo, guys
    private static double mouseAngle( int x, int y, int mx, int my )
    {
        return (MathHelper.atan2(my - y, mx - x) + Math.PI * 2) % (Math.PI * 2);
    }

    @Override
    // Wait for input to press selected key onc
    public void tick()
    {
        super.tick();
        if ( !InputUtil.isKeyPressed(
                MinecraftClient.getInstance().getWindow().getHandle(),
                this.conflictedKey.getCode()
            )
        )
        {
            this.mc.setScreen( null );
            if ( this.selectedSector != -1 )
            {
                KeyBinding bind = KeybindManager.getConflicts( conflictedKey )
                        .get( this.selectedSector );

                ((KeyBindingAccessor) bind).setPressed( true );
                ((KeyBindingAccessor) bind).setTimesPressed( 1 );
            }
        }

        this.ticksInScreen++;
    }

    @Override
    // Don't pause the game when this screen is open
    // actually why not
    public boolean shouldPause()
    {
        return false;
    }

    // 1.20.2 onwards
    @Override
    public void renderBackground( DrawContext context, int mouseX, int mouseY, float delta )
    {
        // Remove the darkened background
        super.renderBackground( context, mouseX, mouseY, delta );
    }
}
