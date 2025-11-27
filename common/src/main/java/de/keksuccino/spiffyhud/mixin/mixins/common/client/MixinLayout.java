package de.keksuccino.spiffyhud.mixin.mixins.common.client;

import de.keksuccino.fancymenu.customization.layout.Layout;
import de.keksuccino.fancymenu.customization.screen.identifier.ScreenIdentifierHandler;
import de.keksuccino.spiffyhud.customization.SpiffyOverlayScreen;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Layout.class)
public class MixinLayout {

    @Shadow(remap = false) public boolean renderElementsBehindVanilla;

    /**
     * @reason Set Spiffy layouts to always render behind Vanilla elements.
     */
    @Inject(method = "<init>(Lnet/minecraft/client/gui/screens/Screen;)V", at = @At("RETURN"))
    private void return_initWithScreen_Spiffy(Screen screen, CallbackInfo info) {
        if (screen instanceof SpiffyOverlayScreen) {
            this.renderElementsBehindVanilla = true;
        }
    }

    /**
     * @reason Set Spiffy layouts to always render behind Vanilla elements.
     */
    @Inject(method = "<init>(Ljava/lang/String;)V", at = @At("RETURN"), remap = false)
    private void return_initWithScreenIdentifier_Spiffy(String screenIdentifier, CallbackInfo info) {
        if (screenIdentifier.equals(ScreenIdentifierHandler.getBestIdentifier(SpiffyOverlayScreen.class.getName()))) {
            this.renderElementsBehindVanilla = true;
        }
    }

    /**
     * @reason Set Spiffy layouts to always render behind Vanilla elements.
     */
    @Inject(method = "setScreenIdentifier", at = @At("RETURN"), remap = false)
    private void return_setScreenIdentifier_Spiffy(String screenIdentifier, CallbackInfoReturnable<Layout> info) {
        Layout layout = info.getReturnValue();
        if (layout != null) {
            if (ScreenIdentifierHandler.getBestIdentifier(SpiffyOverlayScreen.class.getName()).equals(screenIdentifier)) {
                layout.renderElementsBehindVanilla = true;
            }
        }
    }

}
