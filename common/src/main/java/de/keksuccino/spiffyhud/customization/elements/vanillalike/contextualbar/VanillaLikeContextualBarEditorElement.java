package de.keksuccino.spiffyhud.customization.elements.vanillalike.contextualbar;

import de.keksuccino.fancymenu.customization.element.AbstractElement;
import de.keksuccino.fancymenu.customization.element.editor.AbstractEditorElement;
import de.keksuccino.fancymenu.customization.layout.editor.LayoutEditorScreen;
import org.jetbrains.annotations.NotNull;

public class VanillaLikeContextualBarEditorElement extends AbstractEditorElement {

    public VanillaLikeContextualBarEditorElement(@NotNull AbstractElement element, @NotNull LayoutEditorScreen editor) {
        super(element, editor);
        this.settings.setStretchable(false);
        this.settings.setAdvancedSizingSupported(false);
        this.settings.setResizeable(false);
        this.settings.setParallaxAllowed(false);
    }

    @Override
    public void init() {

        super.init();
        
        this.addToggleContextMenuEntryTo(this.rightClickMenu, "always_show_locator_bar", VanillaLikeContextualBarEditorElement.class,
                editorElement -> editorElement.getElement().alwaysShowLocatorBar,
                (editorElement, value) -> editorElement.getElement().alwaysShowLocatorBar = value,
                "spiffyhud.elements.contextualbar.always_show_locator");

    }

    public VanillaLikeContextualBarElement getElement() {
        return (VanillaLikeContextualBarElement) this.element;
    }

}
