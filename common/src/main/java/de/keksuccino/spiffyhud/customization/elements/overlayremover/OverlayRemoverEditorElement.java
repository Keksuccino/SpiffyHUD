package de.keksuccino.spiffyhud.customization.elements.overlayremover;

import de.keksuccino.fancymenu.customization.element.AbstractElement;
import de.keksuccino.fancymenu.customization.element.editor.AbstractEditorElement;
import de.keksuccino.fancymenu.customization.layout.editor.LayoutEditorScreen;
import de.keksuccino.fancymenu.util.LocalizationUtils;
import de.keksuccino.fancymenu.util.rendering.ui.tooltip.UITooltip;
import org.jetbrains.annotations.NotNull;

public class OverlayRemoverEditorElement extends AbstractEditorElement<OverlayRemoverEditorElement, OverlayRemoverElement> {

    public OverlayRemoverEditorElement(@NotNull OverlayRemoverElement element, @NotNull LayoutEditorScreen editor) {

        super(element, editor);

        this.settings.setInEditorColorSupported(true);
        this.settings.setParallaxAllowed(false);

    }

    @Override
    public void init() {

        super.init();

        this.rightClickMenu.addValueCycleEntry("overlay_type", OverlayRemoverElement.OverlayType.ALL.cycle(this.getElement().overlayType)
                        .addCycleListener(type -> {
                            editor.history.saveSnapshot();
                            this.getElement().overlayType = type;
                        }))
                .setStackable(false)
                .setTooltipSupplier((contextMenu, contextMenuEntry) -> UITooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.elements.overlay_remover.overlay_type.desc")));

    }

    public OverlayRemoverElement getElement() {
        return (OverlayRemoverElement) this.element;
    }

}
