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
public class KeybindsScreen extends Screen
{
    int ticksInScreen = 0; // TODO: rename to timeInScreen
    int selectedSlot = -1;

    private InputUtil.Key conflictedKey = InputUtil.UNKNOWN_KEY;

    final MinecraftClient mc;

    private final float expansionFactorWhenSelected = 1.075f;
    private final float deadZoneDistance = 20f;

    public KeybindsScreen()
    {
        super( NarratorManager.EMPTY );
        this.mc = MinecraftClient.getInstance();
    }

    public KeybindsScreen( InputUtil.Key key )
    {
        this();

        this.setConflictedKey( key );
    }

    @Override
    // TODO: Split?
    public void render( DrawContext context, int mouseX, int mouseY, float delta )
    {
        super.render( context, mouseX, mouseY, delta );

        this.selectedSlot = -1;

        // Pixel coords of screen centre
        int centreX = width / 2;
        int centreY = height / 2;
        int maxRadius = 80;

        // Angle of mouse, in [angle unit] from [axis]
        // TODO
        double mouseAngle = mouseAngle( centreX, centreY, mouseX, mouseY );

        // The other thing bothering me
        // Adds a "dead zone" so you can cancel quickly
        float mouseDistanceFromCentre = MathHelper.sqrt(
                (mouseX - centreX) * (mouseX - centreX) +
                        (mouseY - centreY) * (mouseY - centreY)
        );

        // Determines how many segments to make for the pie menu
        int numberOfSectors = KeybindsManager.getConflicts( this.conflictedKey ).size();

        // Use Minecraft's value of PI to remove casts
        float step = MathHelper.PI / 180;
        float sectorAngle = (MathHelper.PI * 2) / numberOfSectors; // It's actually radians


        // if cursor is in sector then it highlights
        for ( int sectorIndex = 0; sectorIndex < numberOfSectors; sectorIndex++ )
        {
            // Check if the mouse angle is within the current sector
            boolean mouseInSector = (sectorAngle * sectorIndex) < mouseAngle
                    && mouseAngle < sectorAngle * (sectorIndex + 1)
                    && mouseDistanceFromCentre > this.deadZoneDistance;


            float radius = Math.max( 0F, Math.min( (ticksInScreen + delta - sectorIndex * 6F / numberOfSectors) * 40F, maxRadius ) );

            if ( mouseInSector ) radius *= this.expansionFactorWhenSelected;

            // Render sector labels
            {
                float rad = (sectorIndex + 0.5f) * sectorAngle;
                float xp = centreX + MathHelper.cos( rad ) * radius;
                float yp = centreY + MathHelper.sin( rad ) * radius;

                KeyBinding conflict = KeybindsManager.getConflicts( conflictedKey )
                        .get( sectorIndex );

                // The biggest nagging bug for me
                // Tells you which control category the action goes in
                String actionName = Text.translatable( conflict.getCategory() ).getString() + ": " +
                        Text.translatable( conflict.getTranslationKey() ).getString();

                float xsp = xp - 4;
                float ysp = yp;

                String name = (mouseInSector ? Formatting.UNDERLINE : Formatting.RESET) + actionName;

                int width = textRenderer.getWidth( name );

                if ( xsp < centreX )
                    xsp -= width - 8;

                if ( ysp < centreY )
                    ysp -= 9;

                context.drawTextWithShadow( textRenderer, name, (int) xsp, (int) ysp, 0xFFFFFF );
            }

            // Draw the pie menu :D
            {
                Tessellator tess = Tessellator.getInstance();
                BufferBuilder buf = tess.getBuffer();
                RenderSystem.disableCull();
                RenderSystem.enableBlend();
                RenderSystem.setShader( GameRenderer::getPositionColorProgram );

                buf.begin( VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR );

                int grayscaleColor = 0x40;

                // Darken every other sector
                if ( sectorIndex % 2 == 0 ) grayscaleColor += 0x19;

                int r = grayscaleColor;
                int g = grayscaleColor;
                int b = grayscaleColor;

                int a = 0x66;

                // Set centre vertex of circle
                //if ( sectorIndex == 0 )
                buf.vertex( centreX, centreY, 0 ).color( r, g, b, a ).next();

                // Now make the rest of the sector white
                if ( mouseInSector )
                {
                    this.selectedSlot = sectorIndex;
                    r = g = b = 0xFF; // Woah, I didn't know you could do this
                }

                // Draw an arc!!!
                // TODO: maybe add another arc so the deadzone is visible?
                for ( float i = 0; i < sectorAngle + step / 2; i += step )
                {
                    float rad = i + sectorIndex * sectorAngle;
                    float xp = centreX + MathHelper.cos( rad ) * radius;
                    float yp = centreY + MathHelper.sin( rad ) * radius;

                    if ( i == 0 ) buf.vertex( xp, yp, 0 ).color( r, g, b, a ).next();

                    buf.vertex( xp, yp, 0 ).color( r, g, b, a ).next();
                }

                tess.draw();
            }
        }
    }


    public void setConflictedKey( InputUtil.Key key )
    {
        this.conflictedKey = key;
    }

    // Returns the angle of the line bounded by the given coordinates and the mouse position,
    // from the vertical axis
    // (I think)
    // This is why we study trigo, guys!
    // Who said sin/cos/tan is useless?
    // (I'll admit, outside of computer graphics, it kinda is)
    private static double mouseAngle( int x, int y, int mx, int my )
    {
        return (MathHelper.atan2(my - y, mx - x) + Math.PI * 2) % (Math.PI * 2);
    }

    @Override
    // Checks for Conflicted keys every gametick
    // and waits for input to press selected key once
    public void tick() {
        super.tick();
        if ( !InputUtil.isKeyPressed(
                MinecraftClient.getInstance().getWindow().getHandle(),
                conflictedKey.getCode()
            )
        )
        {
            mc.setScreen( null );
            if ( selectedSlot != -1 )
            {
                KeyBinding bind = KeybindsManager.getConflicts( conflictedKey )
                        .get( selectedSlot );

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
}
