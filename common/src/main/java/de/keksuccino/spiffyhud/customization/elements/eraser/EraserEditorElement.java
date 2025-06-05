package de.keksuccino.spiffyhud.customization.elements.eraser;

import de.keksuccino.fancymenu.customization.element.AbstractElement;
import de.keksuccino.fancymenu.customization.element.editor.AbstractEditorElement;
import de.keksuccino.fancymenu.customization.layout.editor.LayoutEditorScreen;
import org.jetbrains.annotations.NotNull;

public class EraserEditorElement extends AbstractEditorElement {

    public EraserEditorElement(@NotNull AbstractElement element, @NotNull LayoutEditorScreen editor) {

        super(element, editor);

        this.settings.setInEditorColorSupported(true);

    }

    @Override
    public void init() {

        super.init();

    }

    public EraserElement getElement() {
        return (EraserElement) this.element;
    }

}
