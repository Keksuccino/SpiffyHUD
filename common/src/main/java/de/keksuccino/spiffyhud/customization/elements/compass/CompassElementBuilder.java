package de.keksuccino.spiffyhud.customization.elements.compass;

import de.keksuccino.fancymenu.customization.element.AbstractElement;
import de.keksuccino.fancymenu.customization.element.ElementBuilder;
import de.keksuccino.fancymenu.customization.element.SerializedElement;
import de.keksuccino.fancymenu.customization.layout.editor.LayoutEditorScreen;
import de.keksuccino.fancymenu.util.LocalizationUtils;
import de.keksuccino.fancymenu.util.resource.ResourceSupplier;
import de.keksuccino.fancymenu.util.resource.resources.texture.ITexture;
import de.keksuccino.spiffyhud.customization.SpiffyOverlayScreen;
import de.keksuccino.spiffyhud.customization.marker.MarkerData;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

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
        element.barTexture = deserializeImageResourceSupplier(serialized.getValue("bar_texture"));
        String legacyMajorColor = serialized.getValue("major_tick_color");
        element.cardinalTickColor = Objects.requireNonNullElse(serialized.getValue("cardinal_tick_color"), Objects.requireNonNullElse(legacyMajorColor, element.cardinalTickColor));
        element.degreeTickColor = Objects.requireNonNullElse(serialized.getValue("degree_tick_color"), Objects.requireNonNullElse(legacyMajorColor, element.degreeTickColor));
        element.minorTickColor = Objects.requireNonNullElse(serialized.getValue("minor_tick_color"), element.minorTickColor);
        ResourceSupplier<ITexture> legacyMajorTexture = deserializeImageResourceSupplier(serialized.getValue("major_tick_texture"));
        ResourceSupplier<ITexture> cardinalTexture = deserializeImageResourceSupplier(serialized.getValue("cardinal_tick_texture"));
        element.cardinalTickTexture = (cardinalTexture != null) ? cardinalTexture : legacyMajorTexture;
        ResourceSupplier<ITexture> degreeTexture = deserializeImageResourceSupplier(serialized.getValue("degree_tick_texture"));
        element.degreeTickTexture = (degreeTexture != null) ? degreeTexture : legacyMajorTexture;
        element.minorTickTexture = deserializeImageResourceSupplier(serialized.getValue("minor_tick_texture"));
        element.northCardinalTexture = deserializeImageResourceSupplier(serialized.getValue("cardinal_texture_north"));
        element.eastCardinalTexture = deserializeImageResourceSupplier(serialized.getValue("cardinal_texture_east"));
        element.southCardinalTexture = deserializeImageResourceSupplier(serialized.getValue("cardinal_texture_south"));
        element.westCardinalTexture = deserializeImageResourceSupplier(serialized.getValue("cardinal_texture_west"));
        element.cardinalTextColor = Objects.requireNonNullElse(serialized.getValue("cardinal_text_color"), element.cardinalTextColor);
        element.numberTextColor = Objects.requireNonNullElse(serialized.getValue("number_text_color"), element.numberTextColor);
        element.cardinalTextScale = Objects.requireNonNullElse(serialized.getValue("cardinal_text_scale"), element.cardinalTextScale);
        element.degreeTextScale = Objects.requireNonNullElse(serialized.getValue("degree_text_scale"), element.degreeTextScale);
        element.needleColor = Objects.requireNonNullElse(serialized.getValue("needle_color"), element.needleColor);
        element.deathPointerColor = Objects.requireNonNullElse(serialized.getValue("death_pointer_color"), element.deathPointerColor);
        element.hostileDotsColor = Objects.requireNonNullElse(serialized.getValue("hostile_dots_color"), element.hostileDotsColor);
        element.passiveDotsColor = Objects.requireNonNullElse(serialized.getValue("passive_dots_color"), element.passiveDotsColor);
        element.needleTexture = deserializeImageResourceSupplier(serialized.getValue("needle_texture"));
        element.deathPointerTexture = deserializeImageResourceSupplier(serialized.getValue("death_pointer_texture"));
        element.hostileDotTexture = deserializeImageResourceSupplier(serialized.getValue("hostile_dots_texture"));
        element.passiveDotTexture = deserializeImageResourceSupplier(serialized.getValue("passive_dots_texture"));
        element.hostileDotScale = Objects.requireNonNullElse(serialized.getValue("hostile_dots_scale"), element.hostileDotScale);
        element.passiveDotScale = Objects.requireNonNullElse(serialized.getValue("passive_dots_scale"), element.passiveDotScale);
        element.markerDotScale = Objects.requireNonNullElse(serialized.getValue("marker_dots_scale"), element.markerDotScale);
        element.needleYOffset = Objects.requireNonNullElse(serialized.getValue("needle_y_offset"), element.needleYOffset);
        element.markerDotYOffset = Objects.requireNonNullElse(serialized.getValue("marker_dot_y_offset"), element.markerDotYOffset);
        element.markerNeedleYOffset = Objects.requireNonNullElse(serialized.getValue("marker_needle_y_offset"), element.markerNeedleYOffset);
        element.markerDotLabelXOffset = Objects.requireNonNullElse(serialized.getValue("marker_dot_label_x_offset"), element.markerDotLabelXOffset);
        element.markerDotLabelYOffset = Objects.requireNonNullElse(serialized.getValue("marker_dot_label_y_offset"), element.markerDotLabelYOffset);
        element.markerNeedleLabelXOffset = Objects.requireNonNullElse(serialized.getValue("marker_needle_label_x_offset"), element.markerNeedleLabelXOffset);
        element.markerNeedleLabelYOffset = Objects.requireNonNullElse(serialized.getValue("marker_needle_label_y_offset"), element.markerNeedleLabelYOffset);
        element.deathPointerLabelXOffset = Objects.requireNonNullElse(serialized.getValue("death_pointer_label_x_offset"), element.deathPointerLabelXOffset);
        element.deathPointerLabelYOffset = Objects.requireNonNullElse(serialized.getValue("death_pointer_label_y_offset"), element.deathPointerLabelYOffset);
        element.deathPointerYOffset = Objects.requireNonNullElse(serialized.getValue("death_pointer_y_offset"), element.deathPointerYOffset);
        element.hostileDotsYOffset = Objects.requireNonNullElse(serialized.getValue("hostile_dots_y_offset"), element.hostileDotsYOffset);
        element.passiveDotsYOffset = Objects.requireNonNullElse(serialized.getValue("passive_dots_y_offset"), element.passiveDotsYOffset);
        String legacyTickOffset = serialized.getValue("tick_y_offset");
        String cardinalTickOffset = serialized.getValue("cardinal_tick_y_offset");
        if (cardinalTickOffset != null) {
            element.cardinalTickYOffset = cardinalTickOffset;
        } else if (legacyTickOffset != null) {
            element.cardinalTickYOffset = legacyTickOffset;
        }
        String degreeTickOffset = serialized.getValue("degree_tick_y_offset");
        if (degreeTickOffset != null) {
            element.degreeTickYOffset = degreeTickOffset;
        } else if (legacyTickOffset != null) {
            element.degreeTickYOffset = legacyTickOffset;
        }
        String minorTickOffset = serialized.getValue("minor_tick_y_offset");
        if (minorTickOffset != null) {
            element.minorTickYOffset = minorTickOffset;
        } else if (legacyTickOffset != null) {
            element.minorTickYOffset = legacyTickOffset;
        }
        element.cardinalTextYOffset = Objects.requireNonNullElse(serialized.getValue("cardinal_text_y_offset"), element.cardinalTextYOffset);
        element.degreeTextYOffset = Objects.requireNonNullElse(serialized.getValue("degree_text_y_offset"), element.degreeTextYOffset);
        element.backgroundEnabled = deserializeBoolean(element.backgroundEnabled, serialized.getValue("background_enabled"));
        element.barEnabled = deserializeBoolean(element.barEnabled, serialized.getValue("bar_enabled"));
        element.cardinalTicksEnabled = deserializeBoolean(element.cardinalTicksEnabled, serialized.getValue("cardinal_ticks_enabled"));
        element.degreeTicksEnabled = deserializeBoolean(element.degreeTicksEnabled, serialized.getValue("degree_ticks_enabled"));
        String legacyMajorTicksEnabled = serialized.getValue("major_ticks_enabled");
        if (serialized.getValue("cardinal_ticks_enabled") == null) {
            element.cardinalTicksEnabled = deserializeBoolean(element.cardinalTicksEnabled, legacyMajorTicksEnabled);
        }
        if (serialized.getValue("degree_ticks_enabled") == null) {
            element.degreeTicksEnabled = deserializeBoolean(element.degreeTicksEnabled, legacyMajorTicksEnabled);
        }
        element.minorTicksEnabled = deserializeBoolean(element.minorTicksEnabled, serialized.getValue("minor_ticks_enabled"));
        element.needleEnabled = deserializeBoolean(element.needleEnabled, serialized.getValue("needle_enabled"));
        element.cardinalTextEnabled = deserializeBoolean(element.cardinalTextEnabled, serialized.getValue("cardinal_text_enabled"));
        element.degreeNumbersEnabled = deserializeBoolean(element.degreeNumbersEnabled, serialized.getValue("degree_numbers_enabled"));
        element.cardinalOutlineEnabled = deserializeBoolean(element.cardinalOutlineEnabled, serialized.getValue("cardinal_outline"));
        element.degreeOutlineEnabled = deserializeBoolean(element.degreeOutlineEnabled, serialized.getValue("degree_outline"));
        element.deathPointerEnabled = deserializeBoolean(element.deathPointerEnabled, serialized.getValue("death_pointer_enabled"));
        element.worldMarkersEnabled = deserializeBoolean(element.worldMarkersEnabled, serialized.getValue("world_markers_enabled"));
        element.markerLabelsEnabled = deserializeBoolean(element.markerLabelsEnabled, serialized.getValue("marker_labels_enabled"));
        element.markerLabelOutlineEnabled = deserializeBoolean(element.markerLabelOutlineEnabled, serialized.getValue("marker_label_outline_enabled"));
        element.deathPointerLabelEnabled = deserializeBoolean(element.deathPointerLabelEnabled, serialized.getValue("death_pointer_label_enabled"));
        element.deathPointerLabelOutlineEnabled = deserializeBoolean(element.deathPointerLabelOutlineEnabled, serialized.getValue("death_pointer_label_outline_enabled"));
        element.hostileDotsEnabled = deserializeBoolean(element.hostileDotsEnabled, serialized.getValue("hostile_dots_enabled"));
        element.passiveDotsEnabled = deserializeBoolean(element.passiveDotsEnabled, serialized.getValue("passive_dots_enabled"));
        element.mobDotsMoveUpDown = deserializeBoolean(element.mobDotsMoveUpDown, serialized.getValue("mob_dots_move_up_down"));
        element.hostileDotsRange = deserializeInteger(element.hostileDotsRange, serialized.getValue("hostile_dots_range"));
        element.passiveDotsRange = deserializeInteger(element.passiveDotsRange, serialized.getValue("passive_dots_range"));
        element.hostileDotsShowHeads = deserializeBoolean(element.hostileDotsShowHeads, serialized.getValue("hostile_dots_heads_enabled"));
        element.passiveDotsShowHeads = deserializeBoolean(element.passiveDotsShowHeads, serialized.getValue("passive_dots_heads_enabled"));

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
        serializeTo.putProperty("cardinal_tick_color", element.cardinalTickColor);
        serializeTo.putProperty("degree_tick_color", element.degreeTickColor);
        serializeTo.putProperty("minor_tick_color", element.minorTickColor);
        serializeTo.putProperty("cardinal_text_color", element.cardinalTextColor);
        serializeTo.putProperty("number_text_color", element.numberTextColor);
        serializeTo.putProperty("needle_color", element.needleColor);
        serializeTo.putProperty("death_pointer_color", element.deathPointerColor);
        serializeTo.putProperty("hostile_dots_color", element.hostileDotsColor);
        serializeTo.putProperty("passive_dots_color", element.passiveDotsColor);
        serializeTo.putProperty("hostile_dots_scale", element.hostileDotScale);
        serializeTo.putProperty("passive_dots_scale", element.passiveDotScale);
        serializeTo.putProperty("marker_dots_scale", element.markerDotScale);
        serializeTo.putProperty("needle_y_offset", element.needleYOffset);
        serializeTo.putProperty("marker_dot_y_offset", element.markerDotYOffset);
        serializeTo.putProperty("marker_needle_y_offset", element.markerNeedleYOffset);
        serializeTo.putProperty("marker_dot_label_x_offset", element.markerDotLabelXOffset);
        serializeTo.putProperty("marker_dot_label_y_offset", element.markerDotLabelYOffset);
        serializeTo.putProperty("marker_needle_label_x_offset", element.markerNeedleLabelXOffset);
        serializeTo.putProperty("marker_needle_label_y_offset", element.markerNeedleLabelYOffset);
        serializeTo.putProperty("death_pointer_label_x_offset", element.deathPointerLabelXOffset);
        serializeTo.putProperty("death_pointer_label_y_offset", element.deathPointerLabelYOffset);
        serializeTo.putProperty("death_pointer_y_offset", element.deathPointerYOffset);
        serializeTo.putProperty("hostile_dots_y_offset", element.hostileDotsYOffset);
        serializeTo.putProperty("passive_dots_y_offset", element.passiveDotsYOffset);
        serializeTo.putProperty("cardinal_tick_y_offset", element.cardinalTickYOffset);
        serializeTo.putProperty("degree_tick_y_offset", element.degreeTickYOffset);
        serializeTo.putProperty("minor_tick_y_offset", element.minorTickYOffset);
        serializeTo.putProperty("cardinal_text_y_offset", element.cardinalTextYOffset);
        serializeTo.putProperty("degree_text_y_offset", element.degreeTextYOffset);
        serializeTo.putProperty("cardinal_text_scale", element.cardinalTextScale);
        serializeTo.putProperty("degree_text_scale", element.degreeTextScale);
        ResourceSupplier<ITexture> barTexture = element.barTexture;
        if (barTexture != null) {
            serializeTo.putProperty("bar_texture", barTexture.getSourceWithPrefix());
        }
        ResourceSupplier<ITexture> cardinalTickTexture = element.cardinalTickTexture;
        if (cardinalTickTexture != null) {
            serializeTo.putProperty("cardinal_tick_texture", cardinalTickTexture.getSourceWithPrefix());
        }
        ResourceSupplier<ITexture> degreeTickTexture = element.degreeTickTexture;
        if (degreeTickTexture != null) {
            serializeTo.putProperty("degree_tick_texture", degreeTickTexture.getSourceWithPrefix());
        }
        ResourceSupplier<ITexture> minorTickTexture = element.minorTickTexture;
        if (minorTickTexture != null) {
            serializeTo.putProperty("minor_tick_texture", minorTickTexture.getSourceWithPrefix());
        }
        ResourceSupplier<ITexture> northCardinalTexture = element.northCardinalTexture;
        if (northCardinalTexture != null) {
            serializeTo.putProperty("cardinal_texture_north", northCardinalTexture.getSourceWithPrefix());
        }
        ResourceSupplier<ITexture> eastCardinalTexture = element.eastCardinalTexture;
        if (eastCardinalTexture != null) {
            serializeTo.putProperty("cardinal_texture_east", eastCardinalTexture.getSourceWithPrefix());
        }
        ResourceSupplier<ITexture> southCardinalTexture = element.southCardinalTexture;
        if (southCardinalTexture != null) {
            serializeTo.putProperty("cardinal_texture_south", southCardinalTexture.getSourceWithPrefix());
        }
        ResourceSupplier<ITexture> westCardinalTexture = element.westCardinalTexture;
        if (westCardinalTexture != null) {
            serializeTo.putProperty("cardinal_texture_west", westCardinalTexture.getSourceWithPrefix());
        }
        ResourceSupplier<ITexture> needleTexture = element.needleTexture;
        if (needleTexture != null) {
            serializeTo.putProperty("needle_texture", needleTexture.getSourceWithPrefix());
        }
        ResourceSupplier<ITexture> deathTexture = element.deathPointerTexture;
        if (deathTexture != null) {
            serializeTo.putProperty("death_pointer_texture", deathTexture.getSourceWithPrefix());
        }
        ResourceSupplier<ITexture> hostileDotsTexture = element.hostileDotTexture;
        if (hostileDotsTexture != null) {
            serializeTo.putProperty("hostile_dots_texture", hostileDotsTexture.getSourceWithPrefix());
        }
        ResourceSupplier<ITexture> passiveDotsTexture = element.passiveDotTexture;
        if (passiveDotsTexture != null) {
            serializeTo.putProperty("passive_dots_texture", passiveDotsTexture.getSourceWithPrefix());
        }
        serializeTo.putProperty("background_enabled", "" + element.backgroundEnabled);
        serializeTo.putProperty("bar_enabled", "" + element.barEnabled);
        serializeTo.putProperty("cardinal_ticks_enabled", Boolean.toString(element.cardinalTicksEnabled));
        serializeTo.putProperty("degree_ticks_enabled", Boolean.toString(element.degreeTicksEnabled));
        serializeTo.putProperty("minor_ticks_enabled", "" + element.minorTicksEnabled);
        serializeTo.putProperty("needle_enabled", "" + element.needleEnabled);
        serializeTo.putProperty("cardinal_text_enabled", "" + element.cardinalTextEnabled);
        serializeTo.putProperty("degree_numbers_enabled", "" + element.degreeNumbersEnabled);
        serializeTo.putProperty("cardinal_outline", "" + element.cardinalOutlineEnabled);
        serializeTo.putProperty("degree_outline", "" + element.degreeOutlineEnabled);
        serializeTo.putProperty("death_pointer_enabled", "" + element.deathPointerEnabled);
        serializeTo.putProperty("death_pointer_label_enabled", Boolean.toString(element.deathPointerLabelEnabled));
        serializeTo.putProperty("death_pointer_label_outline_enabled", Boolean.toString(element.deathPointerLabelOutlineEnabled));
        serializeTo.putProperty("world_markers_enabled", Boolean.toString(element.worldMarkersEnabled));
        serializeTo.putProperty("marker_labels_enabled", Boolean.toString(element.markerLabelsEnabled));
        serializeTo.putProperty("marker_label_outline_enabled", Boolean.toString(element.markerLabelOutlineEnabled));
        serializeTo.putProperty("hostile_dots_enabled", "" + element.hostileDotsEnabled);
        serializeTo.putProperty("passive_dots_enabled", "" + element.passiveDotsEnabled);
        serializeTo.putProperty("mob_dots_move_up_down", Boolean.toString(element.mobDotsMoveUpDown));
        serializeTo.putProperty("hostile_dots_range", Integer.toString(element.hostileDotsRange));
        serializeTo.putProperty("passive_dots_range", Integer.toString(element.passiveDotsRange));
        serializeTo.putProperty("hostile_dots_heads_enabled", Boolean.toString(element.hostileDotsShowHeads));
        serializeTo.putProperty("passive_dots_heads_enabled", Boolean.toString(element.passiveDotsShowHeads));
        return serializeTo;
    }

    private int deserializeInteger(int fallback, @Nullable String value) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }

    @Override
    public @NotNull CompassEditorElement wrapIntoEditorElement(@NotNull CompassElement element, @NotNull LayoutEditorScreen editor) {
        return new CompassEditorElement(element, editor);
    }

    @Override
    public @NotNull Component getDisplayName(@Nullable AbstractElement element) {
        return Component.translatable("spiffyhud.elements.compass");
    }

    @Override
    public @Nullable Component[] getDescription(@Nullable AbstractElement element) {
        return LocalizationUtils.splitLocalizedLines("spiffyhud.elements.compass.desc");
    }

    @Override
    public boolean shouldShowUpInEditorElementMenu(@NotNull LayoutEditorScreen editor) {
        return (editor.layoutTargetScreen instanceof SpiffyOverlayScreen);
    }

    public @NotNull List<MarkerData> getMarkers(@NotNull CompassElement element) {
        return element.getMarkers();
    }

    public boolean addMarker(@NotNull CompassElement element, @NotNull MarkerData marker) {
        return element.addMarker(marker);
    }

    public boolean editMarker(@NotNull CompassElement element, @NotNull String markerName, @NotNull Consumer<MarkerData> editor) {
        return element.editMarker(markerName, editor);
    }

    public boolean removeMarker(@NotNull CompassElement element, @NotNull String markerName) {
        return element.removeMarker(markerName);
    }

    public @NotNull String getMarkerGroupKey(@NotNull CompassElement element) {
        return element.getMarkerGroupKey();
    }
}
