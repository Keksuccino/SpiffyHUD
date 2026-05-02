package de.keksuccino.spiffyhud.customization.elements.chatcustomizer;

import de.keksuccino.fancymenu.customization.element.AbstractElement;
import de.keksuccino.fancymenu.customization.element.ElementBuilder;
import de.keksuccino.fancymenu.customization.element.SerializedElement;
import de.keksuccino.fancymenu.customization.layout.editor.LayoutEditorScreen;
import de.keksuccino.fancymenu.util.LocalizationUtils;
import de.keksuccino.fancymenu.util.rendering.DrawableColor;
import de.keksuccino.spiffyhud.customization.SpiffyOverlayScreen;
import net.minecraft.network.chat.Component;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.awt.*;
import java.util.Objects;

public class ChatCustomizerElementBuilder extends ElementBuilder<ChatCustomizerElement, ChatCustomizerEditorElement> {

    private static final Logger LOGGER = LogManager.getLogger();

    public ChatCustomizerElementBuilder() {
        super("spiffy_chat_customizer");
    }

    @Override
    public @NotNull ChatCustomizerElement buildDefaultInstance() {
        ChatCustomizerElement i = new ChatCustomizerElement(this);
        i.baseWidth = 100;
        i.baseHeight = 100;
        i.inEditorColor.setDefault(DrawableColor.of(new Color(50, 168, 146)).getHex()).set(DrawableColor.of(new Color(50, 168, 146)).getHex());
        return i;
    }

    @Override
    public ChatCustomizerElement deserializeElement(@NotNull SerializedElement serialized) {

        ChatCustomizerElement element = this.buildDefaultInstance();

        element.chatCorner = Objects.requireNonNullElse(
                ChatCustomizerHandler.ChatCorner.getByName(Objects.requireNonNullElse(serialized.getValue("chat_corner"), ChatCustomizerHandler.ChatCorner.BOTTOM_LEFT.getName())),
                element.chatCorner);

        element.customChatBackgroundColor = serialized.getValue("custom_chat_background_color");
        element.customInputBackgroundColor = serialized.getValue("custom_input_background_color");
        element.customLineSpacing = serialized.getValue("custom_line_spacing");

        return element;

    }

    @Override
    protected SerializedElement serializeElement(@NotNull ChatCustomizerElement element, @NotNull SerializedElement serializeTo) {

        serializeTo.putProperty("chat_corner", element.chatCorner.getName());

        if (element.customChatBackgroundColor != null) {
            serializeTo.putProperty("custom_chat_background_color", element.customChatBackgroundColor);
        }
        if (element.customInputBackgroundColor != null) {
            serializeTo.putProperty("custom_input_background_color", element.customInputBackgroundColor);
        }
        if (element.customLineSpacing != null) {
            serializeTo.putProperty("custom_line_spacing", element.customLineSpacing);
        }

        return serializeTo;

    }

    @Override
    public @NotNull ChatCustomizerEditorElement wrapIntoEditorElement(@NotNull ChatCustomizerElement element, @NotNull LayoutEditorScreen editor) {
        return new ChatCustomizerEditorElement(element, editor);
    }

    @Override
    public @NotNull Component getDisplayName(@Nullable AbstractElement element) {
        return Component.translatable("spiffyhud.elements.chat_customizer");
    }

    @Override
    public @Nullable Component[] getDescription(@Nullable AbstractElement element) {
        return LocalizationUtils.splitLocalizedLines("spiffyhud.elements.chat_customizer.desc");
    }

    @Override
    public boolean shouldShowUpInEditorElementMenu(@NotNull LayoutEditorScreen editor) {
        return (editor.layoutTargetScreen instanceof SpiffyOverlayScreen);
    }

}
