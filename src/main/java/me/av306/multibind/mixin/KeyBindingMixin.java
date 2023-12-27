package me.av306.multibind.mixin;

import me.av306.multibind.KeybindManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin( value = KeyBinding.class, priority = -5000 )
public abstract class KeyBindingMixin
{
    @Shadow
    private InputUtil.Key boundKey;

    @Inject( method = "setKeyPressed", at = @At( "HEAD" ), cancellable = true )
    private static void setKeyPressed( InputUtil.Key key, boolean pressed, CallbackInfo ci ) throws Exception
    {
        if ( pressed && KeybindManager.handleConflict( key ) )
            ci.cancel();
    }

    @Inject( method = "onKeyPressed", at = @At( "HEAD" ), cancellable = true )
    private static void onKeyPressed( InputUtil.Key key, CallbackInfo ci )
    {
        if ( KeybindManager.hasConflicts( key ) )
        {
            ci.cancel();
            KeybindManager.openConflictMenu( key );
        }
    }

    @Inject( method = "setPressed", at = @At("HEAD"), cancellable = true )
    private void setPressed( boolean pressed, CallbackInfo ci )
    {
        if ( KeybindManager.hasConflicts( this.boundKey ) )
            ci.cancel();
    }
}
