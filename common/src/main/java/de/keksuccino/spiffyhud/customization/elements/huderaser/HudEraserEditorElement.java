package de.keksuccino.spiffyhud.customization.elements.huderaser;

import de.keksuccino.fancymenu.customization.element.AbstractElement;
import de.keksuccino.fancymenu.customization.element.editor.AbstractEditorElement;
import de.keksuccino.fancymenu.customization.layout.editor.LayoutEditorScreen;
import org.jetbrains.annotations.NotNull;

public class HudEraserEditorElement extends AbstractEditorElement {

    public HudEraserEditorElement(@NotNull AbstractElement element, @NotNull LayoutEditorScreen editor) {

        super(element, editor);

        this.settings.setInEditorColorSupported(true);
        this.settings.setParallaxAllowed(false);

    }

    @Override
    public void init() {

        super.init();

//        this.rightClickMenu.addValueCycleEntry("overlay_type", HudEraserElement.OverlayType.ALL.cycle(this.getElement().overlayType)
//                        .addCycleListener(type -> {
//                            editor.history.saveSnapshot();
//                            this.getElement().overlayType = type;
//                        }))
//                .setStackable(false)
//                .setTooltipSupplier((contextMenu, contextMenuEntry) -> Tooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.elements.overlay_remover.overlay_type.desc")));

    }

    public HudEraserElement getElement() {
        return (HudEraserElement) this.element;
    }

}
