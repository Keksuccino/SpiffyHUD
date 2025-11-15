package de.keksuccino.spiffyhud.customization.actions.marker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import de.keksuccino.spiffyhud.customization.marker.MarkerData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class MarkerActionConfig {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

    public String targetElementIdentifier = "";
    public String lookupMarkerName = "";
    public String displayName = "";
    public String colorHex = "";
    public String texture = "";
    public boolean showAsNeedle = false;
    public double positionX = 0.0D;
    public double positionZ = 0.0D;

    public static @NotNull MarkerActionConfig defaultConfig() {
        MarkerActionConfig config = new MarkerActionConfig();
        config.normalize();
        return config;
    }

    public @NotNull MarkerActionConfig copy() {
        MarkerActionConfig copy = new MarkerActionConfig();
        copy.targetElementIdentifier = this.targetElementIdentifier;
        copy.lookupMarkerName = this.lookupMarkerName;
        copy.displayName = this.displayName;
        copy.colorHex = this.colorHex;
        copy.texture = this.texture;
        copy.showAsNeedle = this.showAsNeedle;
        copy.positionX = this.positionX;
        copy.positionZ = this.positionZ;
        return copy;
    }

    @Nullable
    public static MarkerActionConfig parse(@Nullable String raw) {
        if (raw == null || raw.isBlank()) {
            return defaultConfig();
        }
        try {
            MarkerActionConfig config = GSON.fromJson(raw, MarkerActionConfig.class);
            if (config == null) {
                return defaultConfig();
            }
            config.normalize();
            return config;
        } catch (JsonSyntaxException ex) {
            LOGGER.error("[SPIFFYHUD] Failed to parse marker action config!", ex);
            return null;
        }
    }

    public void normalize() {
        this.targetElementIdentifier = normalize(this.targetElementIdentifier);
        this.lookupMarkerName = normalize(this.lookupMarkerName);
        this.displayName = normalize(this.displayName);
        this.colorHex = normalize(this.colorHex);
        this.texture = normalize(this.texture);
    }

    public @NotNull String serialize() {
        return GSON.toJson(this);
    }

    public boolean hasValidTarget() {
        return !this.targetElementIdentifier.isBlank();
    }

    public boolean hasDisplayName() {
        return !this.displayName.isBlank();
    }

    public @NotNull String getLookupName() {
        return this.lookupMarkerName.isBlank() ? this.displayName : this.lookupMarkerName;
    }

    public @NotNull MarkerData toMarkerData() {
        return new MarkerData(
                this.displayName,
                blankToNull(this.colorHex),
                blankToNull(this.texture),
                this.showAsNeedle,
                this.positionX,
                this.positionZ
        );
    }

    private static String normalize(@Nullable String value) {
        if (value == null) {
            return "";
        }
        String trimmed = value.trim();
        return trimmed.replace('\r', ' ').replace('\n', ' ');
    }

    private static String blankToNull(@Nullable String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
