package de.keksuccino.spiffyhud.customization.marker;

import com.google.gson.annotations.SerializedName;
import de.keksuccino.fancymenu.customization.placeholder.PlaceholderParser;
import de.keksuccino.fancymenu.util.SerializationUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Objects;

/**
 * Represents a single compass/minimap marker entry persisted on disk.
 */
public final class MarkerData {

    @NotNull
    private String name = "";
    @Nullable
    private String color;
    @Nullable
    private String dotTexture;
    @Nullable
    private String needleTexture;
    @SerializedName("texture")
    @Nullable
    private String legacyTexture;
    private boolean showAsNeedle;
    @NotNull
    private String markerPosX = "0";
    @NotNull
    private String markerPosZ = "0";

    public MarkerData() {
    }

    public MarkerData(@NotNull String name, @Nullable String color, @Nullable String dotTexture, @Nullable String needleTexture, boolean showAsNeedle, @NotNull String markerPosX, @NotNull String markerPosZ) {
        this.name = normalizeName(name);
        this.color = normalizeString(color);
        this.dotTexture = normalizeString(dotTexture);
        this.needleTexture = normalizeString(needleTexture);
        this.legacyTexture = null;
        this.showAsNeedle = showAsNeedle;
        this.markerPosX = markerPosX;
        this.markerPosZ = markerPosZ;
    }

    public MarkerData(@NotNull MarkerData source) {
        this.name = source.name;
        this.color = source.color;
        this.dotTexture = source.dotTexture;
        this.needleTexture = source.needleTexture;
        this.legacyTexture = null;
        this.showAsNeedle = source.showAsNeedle;
        this.markerPosX = source.markerPosX;
        this.markerPosZ = source.markerPosZ;
    }

    @NotNull
    public String getName() {
        return this.name;
    }

    public void setName(@NotNull String name) {
        this.name = normalizeName(name);
    }

    /**
     * The default fill color for the dot/needle. Can contain raw placeholders.
     */
    @Nullable
    public String getColor() {
        return this.color;
    }

    public void setColor(@Nullable String color) {
        this.color = normalizeString(color);
    }

    /**
     * The resource source for the dot texture. Can contain raw placeholders.
     */
    @Nullable
    public String getDotTexture() {
        return this.dotTexture;
    }

    public void setDotTexture(@Nullable String texture) {
        this.dotTexture = normalizeString(texture);
    }

    /**
     * The resource source for the needle texture. Can contain raw placeholders.
     */
    @Nullable
    public String getNeedleTexture() {
        return this.needleTexture;
    }

    public void setNeedleTexture(@Nullable String texture) {
        this.needleTexture = normalizeString(texture);
    }

    public boolean isShowAsNeedle() {
        return this.showAsNeedle;
    }

    public void setShowAsNeedle(boolean showAsNeedle) {
        this.showAsNeedle = showAsNeedle;
    }

    /**
     * Replaces placeholders in the position and parses it to a number, if possible. Returns 0 if parsing failed.
     */
    public double getResolvedMarkerPosX() {
        return SerializationUtils.deserializeNumber(Double.class, 0D, PlaceholderParser.replacePlaceholders(this.markerPosX));
    }

    /**
     * This returns the raw String value that can contain raw placeholders.
     */
    @NotNull
    public String getMarkerPosX() {
        return markerPosX;
    }

    public void setMarkerPosX(@NotNull String markerPosX) {
        this.markerPosX = markerPosX;
    }

    /**
     * Replaces placeholders in the position and parses it to a number, if possible. Returns 0 if parsing failed.
     */
    public double getResolvedMarkerPosZ() {
        return SerializationUtils.deserializeNumber(Double.class, 0D, PlaceholderParser.replacePlaceholders(this.markerPosZ));
    }

    /**
     * This returns the raw String value that can contain raw placeholders.
     */
    @NotNull
    public String getMarkerPosZ() {
        return markerPosZ;
    }

    public void setMarkerPosZ(@NotNull String markerPosZ) {
        this.markerPosZ = markerPosZ;
    }

    public boolean hasValidName() {
        return !this.name.isBlank();
    }

    public boolean hasTexture() {
        return (this.dotTexture != null && !this.dotTexture.isBlank())
                || (this.needleTexture != null && !this.needleTexture.isBlank());
    }

    @NotNull
    public MarkerData copy() {
        return new MarkerData(this);
    }

    public void copyFrom(@NotNull MarkerData source) {
        this.name = source.name;
        this.color = source.color;
        this.dotTexture = source.dotTexture;
        this.needleTexture = source.needleTexture;
        this.legacyTexture = null;
        this.showAsNeedle = source.showAsNeedle;
        this.markerPosX = source.markerPosX;
        this.markerPosZ = source.markerPosZ;
    }

    void applyLegacyTextureFallback() {
        String normalized = normalizeString(this.legacyTexture);
        if (normalized != null) {
            if (this.dotTexture == null) {
                this.dotTexture = normalized;
            }
            if (this.needleTexture == null) {
                this.needleTexture = normalized;
            }
        }
        this.legacyTexture = null;
    }

    @NotNull
    private static String normalizeName(@NotNull String name) {
        Objects.requireNonNull(name, "name");
        return PlaceholderParser.replacePlaceholders(name.trim());
    }

    @Nullable
    private static String normalizeString(@Nullable String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

}
