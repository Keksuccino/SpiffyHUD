package de.keksuccino.spiffyhud.customization.elements.vanillalike.experience;

import de.keksuccino.fancymenu.customization.element.AbstractElement;
import de.keksuccino.fancymenu.customization.element.editor.AbstractEditorElement;
import de.keksuccino.fancymenu.customization.layout.editor.LayoutEditorScreen;
import org.jetbrains.annotations.NotNull;

public class VanillaLikeExperienceEditorElement extends AbstractEditorElement<VanillaLikeExperienceEditorElement, VanillaLikeExperienceElement> {

    public VanillaLikeExperienceEditorElement(@NotNull VanillaLikeExperienceElement element, @NotNull LayoutEditorScreen editor) {
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

    public VanillaLikeExperienceElement getElement() {
        return (VanillaLikeExperienceElement) this.element;
    }

}
