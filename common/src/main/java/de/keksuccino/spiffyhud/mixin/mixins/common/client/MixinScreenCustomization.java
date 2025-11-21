package de.keksuccino.spiffyhud.mixin.mixins.common.client;

import de.keksuccino.fancymenu.customization.ScreenCustomization;
import de.keksuccino.spiffyhud.customization.Shared;
import de.keksuccino.spiffyhud.customization.SpiffyOverlayScreen;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenCustomization.class)
public class MixinScreenCustomization {

    //If HUD gets re-initialized, don't use normal re-init logic
    @Inject(method = "reInitCurrentScreen(ZZ)V", at = @At("HEAD"), remap = false, cancellable = true)
    private static void headReInitCurrentScreen_Spiffy(CallbackInfo info) {
        if ((Minecraft.getInstance().screen instanceof SpiffyOverlayScreen s) && !s.showFancyMenuOverlay) {
            Shared.reInitHudLayouts = true;
            info.cancel();
        }
    }

}
