package de.keksuccino.spiffyhud.customization.placeholders;

import de.keksuccino.fancymenu.customization.placeholder.DeserializedPlaceholderString;
import de.keksuccino.fancymenu.customization.placeholder.Placeholder;
import de.keksuccino.fancymenu.util.LocalizationUtils;
import de.keksuccino.spiffyhud.customization.marker.MarkerData;
import de.keksuccino.spiffyhud.customization.marker.MarkerStorage;
import net.minecraft.client.resources.language.I18n;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Lists marker display names for a target element identifier.
 */
public class MarkerListPlaceholder extends Placeholder {

    private static final String VALUE_TARGET_ELEMENT = "target_element_identifier";
    private static final String VALUE_SEPARATOR = "separator";

    public MarkerListPlaceholder() {
        super("marker_list");
    }

    @Override
    public String getReplacementFor(DeserializedPlaceholderString dps) {
        Map<String, String> values = dps.values;
        String targetElement = values != null ? values.get(VALUE_TARGET_ELEMENT) : null;
        String separator = values != null ? values.get(VALUE_SEPARATOR) : null;

        String trimmedTarget = targetElement != null ? targetElement.trim() : "";
        if (trimmedTarget.isEmpty()) {
            return "";
        }

        String normalizedSeparator = (separator == null) ? ", " : separator;

        List<MarkerData> markers;
        try {
            markers = MarkerStorage.getMarkers(trimmedTarget);
        } catch (IllegalArgumentException ignored) {
            return "";
        }

        if (markers.isEmpty()) {
            return "";
        }

        return markers.stream()
                .map(MarkerData::getName)
                .filter(name -> !name.isBlank())
                .collect(Collectors.joining(normalizedSeparator));
    }

    @Override
    public @Nullable List<String> getValueNames() {
        return List.of(VALUE_TARGET_ELEMENT, VALUE_SEPARATOR);
    }

    @Override
    public @NotNull String getDisplayName() {
        return I18n.get("spiffyhud.placeholders.marker_list");
    }

    @Override
    public List<String> getDescription() {
        return List.of(LocalizationUtils.splitLocalizedStringLines("spiffyhud.placeholders.marker_list.desc"));
    }

    @Override
    public String getCategory() {
        return I18n.get("fancymenu.placeholders.categories.world");
    }

    @Override
    public @NotNull DeserializedPlaceholderString getDefaultPlaceholderString() {
        LinkedHashMap<String, String> values = new LinkedHashMap<>();
        values.put(VALUE_TARGET_ELEMENT, "element_identifier");
        values.put(VALUE_SEPARATOR, ", ");
        return new DeserializedPlaceholderString(this.getIdentifier(), values, "");
    }

}
