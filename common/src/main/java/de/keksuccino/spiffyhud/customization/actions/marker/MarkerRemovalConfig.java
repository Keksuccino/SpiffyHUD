package de.keksuccino.spiffyhud.customization.actions.marker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class MarkerRemovalConfig {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

    public String targetElementIdentifier = "";
    public String markerName = "";

    public static @NotNull MarkerRemovalConfig defaultConfig() {
        MarkerRemovalConfig config = new MarkerRemovalConfig();
        config.normalize();
        return config;
    }

    @Nullable
    public static MarkerRemovalConfig parse(@Nullable String raw) {
        if (raw == null || raw.isBlank()) {
            return defaultConfig();
        }
        try {
            MarkerRemovalConfig config = GSON.fromJson(raw, MarkerRemovalConfig.class);
            if (config == null) {
                return defaultConfig();
            }
            config.normalize();
            return config;
        } catch (JsonSyntaxException ex) {
            LOGGER.error("[SPIFFYHUD] Failed to parse marker removal config!", ex);
            return null;
        }
    }

    public @NotNull MarkerRemovalConfig copy() {
        MarkerRemovalConfig copy = new MarkerRemovalConfig();
        copy.targetElementIdentifier = this.targetElementIdentifier;
        copy.markerName = this.markerName;
        return copy;
    }

    public @NotNull String serialize() {
        return GSON.toJson(this);
    }

    public void normalize() {
        this.targetElementIdentifier = normalize(this.targetElementIdentifier);
        this.markerName = normalize(this.markerName);
    }

    public boolean isValid() {
        return !this.targetElementIdentifier.isBlank() && !this.markerName.isBlank();
    }

    private static String normalize(@Nullable String value) {
        if (value == null) {
            return "";
        }
        return value.trim();
    }
}
