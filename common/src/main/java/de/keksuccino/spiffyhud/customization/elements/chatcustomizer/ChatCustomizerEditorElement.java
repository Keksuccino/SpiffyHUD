package de.keksuccino.spiffyhud.customization.elements.chatcustomizer;

import de.keksuccino.fancymenu.customization.element.AbstractElement;
import de.keksuccino.fancymenu.customization.element.editor.AbstractEditorElement;
import de.keksuccino.fancymenu.customization.layout.editor.LayoutEditorScreen;
import de.keksuccino.fancymenu.util.LocalizationUtils;
import de.keksuccino.fancymenu.util.rendering.ui.tooltip.UITooltip;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class ChatCustomizerEditorElement extends AbstractEditorElement<ChatCustomizerEditorElement, ChatCustomizerElement> {

    public ChatCustomizerEditorElement(@NotNull ChatCustomizerElement element, @NotNull LayoutEditorScreen editor) {

        super(element, editor);

        this.settings.setInEditorColorSupported(true);
        this.settings.setParallaxAllowed(false);

    }

    @Override
    public void init() {

        super.init();

        this.addStringInputContextMenuEntryTo(this.rightClickMenu, "chat_background_color", ChatCustomizerEditorElement.class,
                chatCustomizerEditorElement -> chatCustomizerEditorElement.getElement().customChatBackgroundColor,
                (chatCustomizerEditorElement, s) -> chatCustomizerEditorElement.getElement().customChatBackgroundColor = s,
                null, false, true, Component.translatable("spiffyhud.elements.chat_customizer.chat_background_color"), true, null, null, null);

        this.addStringInputContextMenuEntryTo(this.rightClickMenu, "input_background_color", ChatCustomizerEditorElement.class,
                chatCustomizerEditorElement -> chatCustomizerEditorElement.getElement().customInputBackgroundColor,
                (chatCustomizerEditorElement, s) -> chatCustomizerEditorElement.getElement().customInputBackgroundColor = s,
                null, false, true, Component.translatable("spiffyhud.elements.chat_customizer.input_background_color"), true, null, null, null);

        this.addStringInputContextMenuEntryTo(this.rightClickMenu, "line_spacing", ChatCustomizerEditorElement.class,
                chatCustomizerEditorElement -> chatCustomizerEditorElement.getElement().customLineSpacing,
                (chatCustomizerEditorElement, s) -> chatCustomizerEditorElement.getElement().customLineSpacing = s,
                null, false, true, Component.translatable("spiffyhud.elements.chat_customizer.line_spacing"), true, null, null, null);

        this.rightClickMenu.addValueCycleEntry("screen_corner", ChatCustomizerHandler.ChatCorner.BOTTOM_LEFT.cycle(this.getElement().chatCorner)
                        .addCycleListener(type -> {
                            editor.history.saveSnapshot();
                            this.getElement().chatCorner = type;
                        }))
                .setStackable(false)
                .setTooltipSupplier((contextMenu, contextMenuEntry) -> UITooltip.of(LocalizationUtils.splitLocalizedLines("spiffyhud.elements.chat_customizer.corner.desc")));

    }

    public ChatCustomizerElement getElement() {
        return (ChatCustomizerElement) this.element;
    }

}
