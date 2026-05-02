package de.keksuccino.spiffyhud.customization.elements.player;

import de.keksuccino.fancymenu.customization.element.AbstractElement;
import de.keksuccino.fancymenu.customization.element.editor.AbstractEditorElement;
import de.keksuccino.fancymenu.customization.layout.editor.LayoutEditorScreen;
import org.jetbrains.annotations.NotNull;

public class PlayerEditorElement extends AbstractEditorElement<PlayerEditorElement, PlayerElement> {

    public PlayerEditorElement(@NotNull PlayerElement element, @NotNull LayoutEditorScreen editor) {

        super(element, editor);

        this.settings.setParallaxAllowed(false);

    }

    public PlayerElement getElement() {
        return (PlayerElement) this.element;
    }

}
