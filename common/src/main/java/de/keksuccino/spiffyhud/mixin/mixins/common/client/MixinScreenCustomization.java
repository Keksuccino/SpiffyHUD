package de.keksuccino.spiffyhud.mixin.mixins.common.client;

import de.keksuccino.fancymenu.customization.ScreenCustomization;
import de.keksuccino.spiffyhud.SpiffyHud;
import de.keksuccino.spiffyhud.customization.Shared;
import de.keksuccino.spiffyhud.customization.SpiffyOverlayScreen;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenCustomization.class)
public class MixinScreenCustomization {

    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lde/keksuccino/fancymenu/util/event/acara/EventHandler;registerListenersOf(Ljava/lang/Object;)V", remap = false), remap = false)
    private static void before_registerListenersOf_in_init_Spiffy(CallbackInfo info) {

        // Init Spiffy here to be able to register custom elements, placeholders, etc.
        SpiffyHud.init();

    }

    //If HUD gets re-initialized, don't use normal re-init logic
    @Inject(method = "reInitCurrentScreen(ZZ)V", at = @At("HEAD"), remap = false, cancellable = true)
    private static void headReInitCurrentScreen_Spiffy(CallbackInfo info) {
        if ((Minecraft.getInstance().screen instanceof SpiffyOverlayScreen s) && !s.showFancyMenuOverlay) {
            Shared.reInitHudLayouts = true;
            info.cancel();
        }
    }

}
