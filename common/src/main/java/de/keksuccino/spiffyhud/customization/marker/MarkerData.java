package de.keksuccino.spiffyhud.customization.marker;

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
    private String texture;
    private boolean showAsNeedle;
    private double markerPosX;
    private double markerPosZ;

    public MarkerData() {
    }

    public MarkerData(@NotNull String name, @Nullable String color, @Nullable String texture,
                      boolean showAsNeedle, double markerPosX, double markerPosZ) {
        this.name = normalizeName(name);
        this.color = normalizeString(color);
        this.texture = normalizeString(texture);
        this.showAsNeedle = showAsNeedle;
        this.markerPosX = markerPosX;
        this.markerPosZ = markerPosZ;
    }

    public MarkerData(@NotNull MarkerData source) {
        this.name = source.name;
        this.color = source.color;
        this.texture = source.texture;
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

    @Nullable
    public String getColor() {
        return this.color;
    }

    public void setColor(@Nullable String color) {
        this.color = normalizeString(color);
    }

    @Nullable
    public String getTexture() {
        return this.texture;
    }

    public void setTexture(@Nullable String texture) {
        this.texture = normalizeString(texture);
    }

    public boolean isShowAsNeedle() {
        return this.showAsNeedle;
    }

    public void setShowAsNeedle(boolean showAsNeedle) {
        this.showAsNeedle = showAsNeedle;
    }

    public double getMarkerPosX() {
        return this.markerPosX;
    }

    public void setMarkerPosX(double markerPosX) {
        this.markerPosX = markerPosX;
    }

    public double getMarkerPosZ() {
        return this.markerPosZ;
    }

    public void setMarkerPosZ(double markerPosZ) {
        this.markerPosZ = markerPosZ;
    }

    public boolean hasValidName() {
        return !this.name.isBlank();
    }

    public boolean hasTexture() {
        return this.texture != null && !this.texture.isBlank();
    }

    @NotNull
    public MarkerData copy() {
        return new MarkerData(this);
    }

    public void copyFrom(@NotNull MarkerData source) {
        this.name = source.name;
        this.color = source.color;
        this.texture = source.texture;
        this.showAsNeedle = source.showAsNeedle;
        this.markerPosX = source.markerPosX;
        this.markerPosZ = source.markerPosZ;
    }

    @NotNull
    private static String normalizeName(@NotNull String name) {
        Objects.requireNonNull(name, "name");
        return name.trim();
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
