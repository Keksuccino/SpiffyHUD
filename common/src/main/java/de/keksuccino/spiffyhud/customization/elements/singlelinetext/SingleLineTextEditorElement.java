package de.keksuccino.spiffyhud.customization.elements.singlelinetext;

import de.keksuccino.fancymenu.customization.element.AbstractElement;
import de.keksuccino.fancymenu.customization.element.editor.AbstractEditorElement;
import de.keksuccino.fancymenu.customization.layout.editor.LayoutEditorScreen;
import de.keksuccino.fancymenu.util.LocalizationUtils;
import de.keksuccino.fancymenu.util.rendering.ui.tooltip.UITooltip;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class SingleLineTextEditorElement extends AbstractEditorElement<SingleLineTextEditorElement, SingleLineTextElement> {

    public SingleLineTextEditorElement(@NotNull SingleLineTextElement element, @NotNull LayoutEditorScreen editor) {

        super(element, editor);

        this.settings.setParallaxAllowed(false);
        this.settings.setResizeable(false);
        this.settings.setAdvancedSizingSupported(false);
        this.settings.setStretchable(false);
        this.settings.setAutoSizingAllowed(false);

    }

    @Override
    public void init() {

        super.init();

        this.addStringInputContextMenuEntryTo(this.rightClickMenu, "text", SingleLineTextEditorElement.class,
                chatCustomizerEditorElement -> chatCustomizerEditorElement.getElement().text,
                (chatCustomizerEditorElement, s) -> chatCustomizerEditorElement.getElement().text = s,
                null, false, true, Component.translatable("spiffyhud.elements.single_line_text.text"), true, null, null, null);

        this.addStringInputContextMenuEntryTo(this.rightClickMenu, "text_scale", SingleLineTextEditorElement.class,
                editorElement -> editorElement.getElement().textScale,
                (editorElement, scale) -> editorElement.getElement().textScale = (scale == null || scale.isBlank()) ? SingleLineTextElement.DEFAULT_TEXT_SCALE_STRING : scale,
                null, false, true, Component.translatable("spiffyhud.elements.single_line_text.scale"), true,
                SingleLineTextElement.DEFAULT_TEXT_SCALE_STRING, null, null)
                .setTooltipSupplier((menu, entry) -> UITooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.elements.single_line_text.scale.desc")));

    }

    public SingleLineTextElement getElement() {
        return (SingleLineTextElement) this.element;
    }

}
