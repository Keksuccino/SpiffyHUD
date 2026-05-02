package de.keksuccino.spiffyhud.customization.elements.vanillalike.hotbar;

import de.keksuccino.fancymenu.customization.element.AbstractElement;
import de.keksuccino.fancymenu.customization.element.editor.AbstractEditorElement;
import de.keksuccino.fancymenu.customization.layout.editor.LayoutEditorScreen;
import org.jetbrains.annotations.NotNull;

public class VanillaLikeHotbarEditorElement extends AbstractEditorElement<VanillaLikeHotbarEditorElement, VanillaLikeHotbarElement> {

    public VanillaLikeHotbarEditorElement(@NotNull VanillaLikeHotbarElement element, @NotNull LayoutEditorScreen editor) {
        super(element, editor);
        this.settings.setStretchable(false);
        this.settings.setAdvancedSizingSupported(false);
        this.settings.setResizeable(false);
        this.settings.setParallaxAllowed(false);
    }

    @Override
    public void init() {

        super.init();

//        this.addStringInputContextMenuEntryTo(this.rightClickMenu, "bar_color",
//                        VanillaLikeHotbarEditorElement.class,
//                        consumes -> consumes.getElement().color.getHex(),
//                        (vanillaBarEditorElement, s) -> vanillaBarEditorElement.getElement().color = DrawableColor.of(s),
//                        null, false, false, Component.translatable("drippyloadingscreen.elements.vanilla_bar.color"),
//                        true, DrawableColor.WHITE.getHex(), TextValidators.HEX_COLOR_TEXT_VALIDATOR, null)
//                .setStackable(true);

    }

    public VanillaLikeHotbarElement getElement() {
        return (VanillaLikeHotbarElement) this.element;
    }

}
