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

// FIXME: Refactor classname to KeybindSelectorScreen
public class KeybindSelectorScreen extends Screen
{
    private int ticksInScreen = 0;
    private int selectedSector = -1;

    private InputUtil.Key conflictedKey = InputUtil.UNKNOWN_KEY;

    final MinecraftClient mc;

    private int centreX = 0, centreY = 0;


    private static final float EXPANSION_FACTOR_WHEN_SELECTED = 20; // TODO: rename + dynamic calculation
    private static int MAX_RADIUS = 0; // 0 for automatic
    private static int DEADZONE_RADIUS = 0; // 0 for automatic

    private static final short PIE_MENU_COLOR = 0x40;
    private static final short PIE_MENU_COLOR_LIGHTEN_FACTOR = 0x19;
    private static final short PIE_MENU_ALPHA = 0x66;

    // The step to take for each quad drawn
    private static final int VERTICES_PER_SECTOR = 10;
    //private static final float STEP = MathHelper.PI / 18;

    private static final short TEXT_INSET = 4;

    public KeybindSelectorScreen()
    {
        super( NarratorManager.EMPTY );
        this.mc = MinecraftClient.getInstance();

        // Automatic radius calculations

        //if ( MAX_RADIUS == -1 ) MAX_RADIUS = Math.min( this.height - 5, this.width - 5 );
        //if ( DEADZONE_RADIUS == -1 ) DEADZONE_RADIUS = MAX_RADIUS / 5;
        //System.out.println( MAX_RADIUS );
    }

    public KeybindSelectorScreen( InputUtil.Key key )
    {
        this();

        this.setConflictedKey( key );
    }

    @Override
    public void render( DrawContext context, int mouseX, int mouseY, float delta )
    {
        super.render( context, mouseX, mouseY, delta );

        //this.selectedSlot = -1;

        // Pixel coords of screen centre
        this.centreX = this.width / 2;
        this.centreY = this.height / 2;

        if ( MAX_RADIUS <= 0 ) MAX_RADIUS = Math.min( centreX - 30, centreY - 30 );
        if ( DEADZONE_RADIUS <= 0 ) DEADZONE_RADIUS = MAX_RADIUS / 6;

        // Angle of mouse, in radians from +X-axis, centred on the origin
        double mouseAngle = mouseAngle( centreX, centreY, mouseX, mouseY );

        float mouseDistanceFromCentre = MathHelper.sqrt( (mouseX - centreX) * (mouseX - centreX) +
                        (mouseY - centreY) * (mouseY - centreY) );

        // Determines how many segments to make for the pie menu
        int numberOfSectors = KeybindManager.getConflicts( this.conflictedKey ).size();

        // Calculate the angle occupied by each sector
        float sectorAngle = (MathHelper.TAU) / numberOfSectors;

        // Get the exact sector index that is selected
        this.selectedSector = (int) (mouseAngle / sectorAngle);

        // Deselect slot
        if ( mouseDistanceFromCentre <= DEADZONE_RADIUS )
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
        for ( var sectorIndex = 0; sectorIndex < numberOfSectors; sectorIndex++ )
        {
            float outerRadius = calculateRadius( delta, numberOfSectors, sectorIndex );
            float innerRadius = DEADZONE_RADIUS;
            short color = PIE_MENU_COLOR;

            if ( sectorIndex % 2 == 0 ) color += PIE_MENU_COLOR_LIGHTEN_FACTOR;
            if ( this.selectedSector == sectorIndex )
            {
                //outerRadius *= EXPANSION_FACTOR_WHEN_SELECTED;
                innerRadius *= EXPANSION_FACTOR_WHEN_SELECTED;
                color = 0xFF;
            }

            drawSector( buf, startAngle, sectorAngle, innerRadius, outerRadius, color );

            startAngle += sectorAngle;
        }

        tess.draw();
    }

    private void drawSector( BufferBuilder buf, float startAngle, float sectorAngle, float innerRadius, float outerRadius, short color )
    {
        for ( var i = 0; i <= VERTICES_PER_SECTOR; i++ )
        {
            float angle = startAngle + ((float) i / VERTICES_PER_SECTOR) * sectorAngle;

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
        float radius = Math.max( 0F, Math.min( (this.ticksInScreen + delta - sectorIndex * 6F / numberOfSectors) * 40F, MAX_RADIUS ) );

        // Expand the sector if selected
        if ( this.selectedSector == sectorIndex ) radius += EXPANSION_FACTOR_WHEN_SELECTED;
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
            float xPos = centreX + MathHelper.cos( angle ) * radius;
            float yPos = centreY + MathHelper.sin( angle ) * radius;

            KeyBinding action = KeybindManager.getConflicts( conflictedKey ).get( sectorIndex );

            // The biggest nagging bug for me
            // Tells you which control category the action goes in
            String actionName =
                    Text.translatable( action.getCategory() ).getString() + ": " +
                    Text.translatable( action.getTranslationKey() ).getString();

            int textWidth = textRenderer.getWidth( actionName );

            // Which sides of the screen are we on?
            if ( xPos > centreX )
            {
                // Right side
                xPos -= TEXT_INSET;

                // Check text going off-screen
                if ( width - xPos < textWidth )
                    xPos -= textWidth - width + xPos;
            }
            else
            {
                // Left side
                xPos -= textWidth - TEXT_INSET;

                // Check text going off-screen
                if ( xPos < 0 ) xPos = TEXT_INSET;
            }

            // Move it closer to the arc
            yPos -= yPos < centreY ? TEXT_INSET : -TEXT_INSET;


            actionName = (this.selectedSector == sectorIndex ? Formatting.UNDERLINE : Formatting.RESET) + actionName;


            context.drawTextWithShadow( textRenderer, actionName, (int) xPos, (int) yPos, 0xFFFFFF );
        }
    }

    public void setConflictedKey( InputUtil.Key key )
    {
        this.conflictedKey = key;
    }

    // Returns the angle of the line bounded by the given coordinates and the mouse position from the vertical axis
    // (I think)
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
                conflictedKey.getCode()
            )
        )
        {
            mc.setScreen( null );
            if ( selectedSector != -1 )
            {
                KeyBinding bind = KeybindManager.getConflicts( conflictedKey )
                        .get( selectedSector );

                ((KeyBindingAccessor) bind).setPressed( true );
                ((KeyBindingAccessor) bind).setTimesPressed( 1 );
            }
        }

        ticksInScreen++;
    }

    @Override
    // Don't pause the game when this screen is open
    // actually why not
    public boolean shouldPause() {
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
