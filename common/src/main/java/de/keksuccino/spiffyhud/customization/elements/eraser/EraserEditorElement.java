package de.keksuccino.spiffyhud.customization.elements.eraser;

import de.keksuccino.fancymenu.customization.element.AbstractElement;
import de.keksuccino.fancymenu.customization.element.editor.AbstractEditorElement;
import de.keksuccino.fancymenu.customization.layout.editor.LayoutEditorScreen;
import de.keksuccino.fancymenu.util.LocalizationUtils;
import de.keksuccino.fancymenu.util.rendering.ui.tooltip.Tooltip;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class EraserEditorElement extends AbstractEditorElement {

    public EraserEditorElement(@NotNull AbstractElement element, @NotNull LayoutEditorScreen editor) {

        super(element, editor);

        this.settings.setInEditorColorSupported(true);

    }

    @Override
    public void init() {

        super.init();

        this.addCycleContextMenuEntryTo(this.rightClickMenu, "aggression_level", Arrays.asList(EraserElement.AggressionLevel.values()), EraserEditorElement.class,
                        eraserEditorElement -> eraserEditorElement.getElement().aggressionLevel,
                        (eraserEditorElement, aggressionLevel) -> eraserEditorElement.getElement().aggressionLevel = aggressionLevel,
                        (contextMenu, clickableContextMenuEntry, aggressionLevel) -> aggressionLevel.getCycleComponent())
                .setTooltipSupplier((contextMenu, contextMenuEntry) -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.aggression_level.desc")));

    }

    public EraserElement getElement() {
        return (EraserElement) this.element;
    }

}
