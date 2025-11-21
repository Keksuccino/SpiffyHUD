package de.keksuccino.spiffyhud.customization.elements.singlelinetext;

import de.keksuccino.fancymenu.customization.element.AbstractElement;
import de.keksuccino.fancymenu.customization.element.ElementBuilder;
import de.keksuccino.fancymenu.customization.element.SerializedElement;
import de.keksuccino.fancymenu.customization.layout.editor.LayoutEditorScreen;
import de.keksuccino.fancymenu.util.LocalizationUtils;
import de.keksuccino.spiffyhud.customization.SpiffyOverlayScreen;
import de.keksuccino.spiffyhud.customization.elements.vanillalike.air.VanillaLikePlayerAirElement;
import net.minecraft.network.chat.Component;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class SingleLineTextElementBuilder extends ElementBuilder<SingleLineTextElement, SingleLineTextEditorElement> {

    private static final Logger LOGGER = LogManager.getLogger();

    public SingleLineTextElementBuilder() {
        super("spiffy_single_line_text");
    }

    @Override
    public @NotNull SingleLineTextElement buildDefaultInstance() {
        SingleLineTextElement i = new SingleLineTextElement(this);
        i.baseWidth = 100;
        i.baseHeight = 100;
        i.stickyAnchor = true;
        i.stayOnScreen = false;
        return i;
    }

    @Override
    public SingleLineTextElement deserializeElement(@NotNull SerializedElement serialized) {

        SingleLineTextElement element = this.buildDefaultInstance();

        element.text = serialized.getValue("text");
        element.textScale = Objects.requireNonNullElse(serialized.getValue("text_scale"), element.textScale);

        return element;

    }

    @Override
    public @Nullable SingleLineTextElement deserializeElementInternal(@NotNull SerializedElement serialized) {
        SingleLineTextElement e = super.deserializeElementInternal(serialized);
        if (e != null) {
            // Fix "Stay on Screen" resetting itself for element types that have it disabled by default
            e.stayOnScreen = this.deserializeBoolean(e.stayOnScreen, serialized.getValue("stay_on_screen"));
        }
        return e;
    }

    @Override
    protected SerializedElement serializeElement(@NotNull SingleLineTextElement element, @NotNull SerializedElement serializeTo) {

        if (element.text != null) {
            serializeTo.putProperty("text", element.text);
        }
        serializeTo.putProperty("text_scale", element.textScale);

        return serializeTo;

    }

    @Override
    public @NotNull SingleLineTextEditorElement wrapIntoEditorElement(@NotNull SingleLineTextElement element, @NotNull LayoutEditorScreen editor) {
        return new SingleLineTextEditorElement(element, editor);
    }

    @Override
    public @NotNull Component getDisplayName(@Nullable AbstractElement element) {
        return Component.translatable("spiffyhud.elements.single_line_text");
    }

    @Override
    public @Nullable Component[] getDescription(@Nullable AbstractElement element) {
        return LocalizationUtils.splitLocalizedLines("spiffyhud.elements.single_line_text.desc");
    }

    @Override
    public boolean shouldShowUpInEditorElementMenu(@NotNull LayoutEditorScreen editor) {
        return (editor.layoutTargetScreen instanceof SpiffyOverlayScreen);
    }

}
