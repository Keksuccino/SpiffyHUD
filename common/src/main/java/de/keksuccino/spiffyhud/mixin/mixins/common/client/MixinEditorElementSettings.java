package de.keksuccino.spiffyhud.mixin.mixins.common.client;

import de.keksuccino.fancymenu.customization.element.editor.EditorElementSettings;
import de.keksuccino.fancymenu.customization.layout.editor.LayoutEditorScreen;
import de.keksuccino.spiffyhud.customization.SpiffyOverlayScreen;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EditorElementSettings.class)
public class MixinEditorElementSettings {

    /**
     * @reason Disable Parallax for all elements when editing the SpiffyOverlayScreen.
     */
    @Inject(method = "isParallaxAllowed", at = @At("HEAD"), cancellable = true, remap = false)
    private void head_isParallaxAllowed_Spiffy(CallbackInfoReturnable<Boolean> info) {
        if (Minecraft.getInstance().gui.screen() instanceof LayoutEditorScreen e) {
            if (e.layoutTargetScreen instanceof SpiffyOverlayScreen) {
                info.setReturnValue(false);
            }
        }
    }

}
