package de.keksuccino.spiffyhud.customization.elements.vanillalike.jumpmeter;

import de.keksuccino.fancymenu.customization.element.AbstractElement;
import de.keksuccino.fancymenu.customization.element.editor.AbstractEditorElement;
import de.keksuccino.fancymenu.customization.layout.editor.LayoutEditorScreen;
import org.jetbrains.annotations.NotNull;

public class VanillaLikeJumpMeterEditorElement extends AbstractEditorElement<VanillaLikeJumpMeterEditorElement, VanillaLikeJumpMeterElement> {

    public VanillaLikeJumpMeterEditorElement(@NotNull VanillaLikeJumpMeterElement element, @NotNull LayoutEditorScreen editor) {
        super(element, editor);
        this.settings.setStretchable(false);
        this.settings.setAdvancedSizingSupported(false);
        this.settings.setResizeable(false);
        this.settings.setParallaxAllowed(false);
    }

    @Override
    public void init() {

        super.init();

    }

    public VanillaLikeJumpMeterElement getElement() {
        return (VanillaLikeJumpMeterElement) this.element;
    }

}
