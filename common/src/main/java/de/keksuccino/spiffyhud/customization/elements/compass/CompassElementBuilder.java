package de.keksuccino.spiffyhud.customization.elements.compass;

import de.keksuccino.fancymenu.customization.element.AbstractElement;
import de.keksuccino.fancymenu.customization.element.ElementBuilder;
import de.keksuccino.fancymenu.customization.element.SerializedElement;
import de.keksuccino.fancymenu.customization.layout.editor.LayoutEditorScreen;
import de.keksuccino.fancymenu.util.LocalizationUtils;
import de.keksuccino.spiffyhud.customization.SpiffyOverlayScreen;
import de.keksuccino.spiffyhud.customization.elements.playerairbar.PlayerAirBubbleBarElement;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class CompassElementBuilder extends ElementBuilder<CompassElement, CompassEditorElement> {

    public CompassElementBuilder() {
        super("spiffy_player_compass");
    }

    @Override
    public @NotNull CompassElement buildDefaultInstance() {
        CompassElement element = new CompassElement(this);
        element.baseWidth = 240;
        element.baseHeight = 60;
        return element;
    }

    @Override
    public CompassElement deserializeElement(@NotNull SerializedElement serialized) {
        CompassElement element = this.buildDefaultInstance();

        element.backgroundColor = Objects.requireNonNullElse(serialized.getValue("background_color"), element.backgroundColor);
        element.barColor = Objects.requireNonNullElse(serialized.getValue("bar_color"), element.barColor);
        element.majorTickColor = Objects.requireNonNullElse(serialized.getValue("major_tick_color"), element.majorTickColor);
        element.minorTickColor = Objects.requireNonNullElse(serialized.getValue("minor_tick_color"), element.minorTickColor);
        element.cardinalTextColor = Objects.requireNonNullElse(serialized.getValue("cardinal_text_color"), element.cardinalTextColor);
        element.numberTextColor = Objects.requireNonNullElse(serialized.getValue("number_text_color"), element.numberTextColor);
        element.needleColor = Objects.requireNonNullElse(serialized.getValue("needle_color"), element.needleColor);
        element.deathPointerColor = Objects.requireNonNullElse(serialized.getValue("death_pointer_color"), element.deathPointerColor);
        element.cardinalOutlineEnabled = deserializeBoolean(element.cardinalOutlineEnabled, serialized.getValue("cardinal_outline"));
        element.degreeOutlineEnabled = deserializeBoolean(element.degreeOutlineEnabled, serialized.getValue("degree_outline"));
        element.deathPointerEnabled = deserializeBoolean(element.deathPointerEnabled, serialized.getValue("death_pointer_enabled"));

        return element;
    }

    @Override
    public @Nullable CompassElement deserializeElementInternal(@NotNull SerializedElement serialized) {
        CompassElement element = super.deserializeElementInternal(serialized);
        if (element != null) {
            // Fix for elements that have stay on screen disabled by default
            element.stayOnScreen = this.deserializeBoolean(element.stayOnScreen, serialized.getValue("stay_on_screen"));
        }
        return element;
    }

    @Override
    protected SerializedElement serializeElement(@NotNull CompassElement element, @NotNull SerializedElement serializeTo) {
        serializeTo.putProperty("background_color", element.backgroundColor);
        serializeTo.putProperty("bar_color", element.barColor);
        serializeTo.putProperty("major_tick_color", element.majorTickColor);
        serializeTo.putProperty("minor_tick_color", element.minorTickColor);
        serializeTo.putProperty("cardinal_text_color", element.cardinalTextColor);
        serializeTo.putProperty("number_text_color", element.numberTextColor);
        serializeTo.putProperty("needle_color", element.needleColor);
        serializeTo.putProperty("death_pointer_color", element.deathPointerColor);
        serializeTo.putProperty("cardinal_outline", "" + element.cardinalOutlineEnabled);
        serializeTo.putProperty("degree_outline", "" + element.degreeOutlineEnabled);
        serializeTo.putProperty("death_pointer_enabled", "" + element.deathPointerEnabled);
        return serializeTo;
    }

    @Override
    public @NotNull CompassEditorElement wrapIntoEditorElement(@NotNull CompassElement element, @NotNull LayoutEditorScreen editor) {
        return new CompassEditorElement(element, editor);
    }

    @Override
    public @NotNull Component getDisplayName(@Nullable AbstractElement element) {
        return Component.translatable("spiffyhud.elements.player_compass");
    }

    @Override
    public @Nullable Component[] getDescription(@Nullable AbstractElement element) {
        return LocalizationUtils.splitLocalizedLines("spiffyhud.elements.player_compass.desc");
    }

    @Override
    public boolean shouldShowUpInEditorElementMenu(@NotNull LayoutEditorScreen editor) {
        return (editor.layoutTargetScreen instanceof SpiffyOverlayScreen);
    }
}
