package de.keksuccino.spiffyhud.customization.elements.vanillalike.attackindicator;

import de.keksuccino.fancymenu.customization.element.AbstractElement;
import de.keksuccino.fancymenu.customization.element.editor.AbstractEditorElement;
import de.keksuccino.fancymenu.customization.layout.editor.LayoutEditorScreen;
import org.jetbrains.annotations.NotNull;

public class VanillaLikeAttackIndicatorEditorElement extends AbstractEditorElement<VanillaLikeAttackIndicatorEditorElement, VanillaLikeAttackIndicatorElement> {

    public VanillaLikeAttackIndicatorEditorElement(@NotNull VanillaLikeAttackIndicatorElement element, @NotNull LayoutEditorScreen editor) {
        super(element, editor);
        this.settings.setParallaxAllowed(false);
    }

    @Override
    public void init() {

        super.init();

        this.addToggleContextMenuEntryTo(this.rightClickMenu, "is_hotbar", VanillaLikeAttackIndicatorEditorElement.class,
                vanillaLikeAttackIndicatorEditorElement -> vanillaLikeAttackIndicatorEditorElement.getElement().isHotbar,
                (vanillaLikeAttackIndicatorEditorElement, aBoolean) -> vanillaLikeAttackIndicatorEditorElement.getElement().isHotbar = aBoolean,
                "spiffyhud.elements.vanillalike.attack_indicator.is_hotbar");

    }

    public VanillaLikeAttackIndicatorElement getElement() {
        return (VanillaLikeAttackIndicatorElement) this.element;
    }

}
