package de.keksuccino.spiffyhud.mixin.mixins.common.client;

import de.keksuccino.fancymenu.customization.element.editor.AbstractEditorElement;
import de.keksuccino.fancymenu.customization.element.elements.button.vanillawidget.VanillaWidgetEditorElement;
import de.keksuccino.fancymenu.customization.layout.editor.AnchorPointOverlay;
import de.keksuccino.spiffyhud.customization.SpiffyOverlayScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AnchorPointOverlay.class)
public class MixinAnchorPointOverlay {

    /**
     * @reason Makes it impossible to hover Spiffy's dummy Vanilla elements to anchor other elements to them.
     */
    @Inject(method = "getTopHoveredNotDraggedElement", at = @At("RETURN"), cancellable = true, remap = false)
    private void after_getTopHoveredNotDraggedElement_Spiffy(CallbackInfoReturnable<AbstractEditorElement> info) {
        if (info.getReturnValue() instanceof VanillaWidgetEditorElement e) {
            if (e.element.widgetMeta.getWidget() instanceof SpiffyOverlayScreen.SpiffyRendererWidget) {
                info.setReturnValue(null);
            }
        }
    }

}
