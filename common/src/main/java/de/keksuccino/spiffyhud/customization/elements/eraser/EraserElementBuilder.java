package de.keksuccino.spiffyhud.customization.elements.eraser;

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

public class EraserElementBuilder extends ElementBuilder<EraserElement, EraserEditorElement> {

    private static final Logger LOGGER = LogManager.getLogger();

    public EraserElementBuilder() {
        super("spiffy_eraser");
    }

    @Override
    public @NotNull EraserElement buildDefaultInstance() {
        EraserElement i = new EraserElement(this);
        i.baseWidth = 100;
        i.baseHeight = 100;
        i.inEditorColor = DrawableColor.of(new Color(157, 19, 93, 179));
        i.stayOnScreen = false;
        return i;
    }

    @Override
    public @Nullable EraserElement deserializeElementInternal(@NotNull SerializedElement serialized) {
        EraserElement e = super.deserializeElementInternal(serialized);
        if (e != null) {
            // Fix "Stay on Screen" resetting itself for element types that have it disabled by default
            e.stayOnScreen = this.deserializeBoolean(e.stayOnScreen, serialized.getValue("stay_on_screen"));
        }
        return e;
    }

    @Override
    public EraserElement deserializeElement(@NotNull SerializedElement serialized) {

        EraserElement element = this.buildDefaultInstance();

        String aggressionLevel = serialized.getValue("aggression_level");
        if (aggressionLevel != null) {
            element.aggressionLevel = Objects.requireNonNullElse(EraserElement.AggressionLevel.getByName(aggressionLevel), EraserElement.AggressionLevel.NORMAL);
        }

        return element;

    }

    @Override
    protected SerializedElement serializeElement(@NotNull EraserElement element, @NotNull SerializedElement serializeTo) {

        serializeTo.putProperty("aggression_level", element.aggressionLevel.getName());

        return serializeTo;

    }

    @Override
    public @NotNull EraserEditorElement wrapIntoEditorElement(@NotNull EraserElement element, @NotNull LayoutEditorScreen editor) {
        return new EraserEditorElement(element, editor);
    }

    @Override
    public @NotNull Component getDisplayName(@Nullable AbstractElement element) {
        return Component.translatable("spiffyhud.elements.hud_eraser");
    }

    @Override
    public @Nullable Component[] getDescription(@Nullable AbstractElement element) {
        return LocalizationUtils.splitLocalizedLines("spiffyhud.elements.hud_eraser.desc");
    }

    @Override
    public boolean shouldShowUpInEditorElementMenu(@NotNull LayoutEditorScreen editor) {
        return (editor.layoutTargetScreen instanceof SpiffyOverlayScreen);
    }

}
