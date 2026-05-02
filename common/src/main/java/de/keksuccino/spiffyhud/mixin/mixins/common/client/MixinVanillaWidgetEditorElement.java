package de.keksuccino.spiffyhud.mixin.mixins.common.client;

import de.keksuccino.fancymenu.customization.element.elements.button.custombutton.ButtonEditorElement;
import de.keksuccino.fancymenu.customization.element.elements.button.vanillawidget.VanillaWidgetEditorElement;
import de.keksuccino.fancymenu.customization.element.elements.button.vanillawidget.VanillaWidgetElement;
import de.keksuccino.fancymenu.customization.layout.editor.LayoutEditorScreen;
import de.keksuccino.fancymenu.customization.widget.WidgetMeta;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VanillaWidgetEditorElement.class)
public abstract class MixinVanillaWidgetEditorElement extends ButtonEditorElement {

    //unused dummy constructor
    @SuppressWarnings("all")
    private MixinVanillaWidgetEditorElement() {
        super(null, null);
    }

    /**
     * @reason Disable most customization stuff for Spiffy's dummy Vanilla elements.
     */
    @Inject(method = "<init>", at = @At("RETURN"),remap = false)
    private void after_construct_Spiffy(VanillaWidgetElement element, LayoutEditorScreen editor, CallbackInfo info) {

        if (this.isSpiffyDummyElement_Spiffy()) {

            this.settings.setMovable(false);
            this.settings.setResizeable(false);
            this.settings.setAnchorPointChangeable(false);
            this.settings.setAdvancedPositioningSupported(false);
            this.settings.setAdvancedSizingSupported(false);
            this.settings.setDelayable(false);
            this.settings.setLoadingRequirementsEnabled(false);
            this.settings.setFadeable(false);
            this.settings.setStretchable(false);
            this.settings.setAutoSizingAllowed(false);
            this.settings.setOpacityChangeable(false);
            this.settings.setStickyAnchorAllowed(false);

        }

    }

    /**
     * @reason Remove incompatible right-click menu options from Spiffy's dummy Vanilla elements.
     */
    @Inject(method = "init", at = @At("RETURN"), remap = false)
    private void after_init_Spiffy(CallbackInfo info) {

        if (this.isSpiffyDummyElement_Spiffy()) {

            this.rightClickMenu.removeEntry("copy_id");
            this.rightClickMenu.removeEntry("copy_vanilla_widget_locator");
            this.rightClickMenu.removeEntry("edit_label");
            this.rightClickMenu.removeEntry("edit_hover_label");
            this.rightClickMenu.removeEntry("hover_sound");
            this.rightClickMenu.removeEntry("click_sound");
            this.rightClickMenu.removeEntry("edit_tooltip");
            this.rightClickMenu.removeEntry("widget_active_state_controller");
            this.rightClickMenu.removeEntry("stay_on_screen");
            this.rightClickMenu.removeEntry("load_once_per_session");

        }

    }

    @Unique
    private boolean isSpiffyDummyElement_Spiffy() {
        WidgetMeta meta = ((VanillaWidgetElement)this.element).widgetMeta;
        if (meta == null) return false;
        String compId = meta.getUniversalIdentifier();
        return ((compId != null) && compId.startsWith("spiffy_") && compId.endsWith("_dummy"));
    }

}
