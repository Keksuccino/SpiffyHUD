package de.keksuccino.spiffyhud.customization.marker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import de.keksuccino.spiffyhud.SpiffyHud;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Persists and manages named marker groups which can be shared between compass instances and
 * other overlay features (e.g. a future minimap implementation).
 */
public class MarkerStorage {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path STORAGE_PATH = SpiffyHud.INSTANCE_DIR.toPath().resolve("markers.json");

    private static MarkerStoreData cachedData = new MarkerStoreData();
    private static boolean loaded;

    private MarkerStorage() {
    }

    public static synchronized @NotNull List<MarkerData> getMarkers(@NotNull String groupId) {
        String normalized = normalizeGroupId(groupId);
        ensureLoaded();
        List<MarkerData> stored = cachedData.viewMarkers(normalized);
        if (stored.isEmpty()) {
            return Collections.emptyList();
        }
        List<MarkerData> copies = new ArrayList<>(stored.size());
        for (MarkerData marker : stored) {
            copies.add(marker.copy());
        }
        return Collections.unmodifiableList(copies);
    }

    public static synchronized @NotNull List<String> getAllGroupIds() {
        ensureLoaded();
        return cachedData.listGroupIds();
    }

    public static synchronized @Nullable MarkerData getMarker(@NotNull String groupId, @NotNull String markerName) {
        String normalized = normalizeGroupId(groupId);
        ensureLoaded();
        MarkerData existing = findMarker(cachedData.viewMarkers(normalized), markerName);
        return existing == null ? null : existing.copy();
    }

    public static synchronized boolean addMarker(@NotNull String groupId, @NotNull MarkerData marker) {
        String normalized = normalizeGroupId(groupId);
        Objects.requireNonNull(marker, "marker");
        ensureLoaded();
        if (!marker.hasValidName()) {
            LOGGER.warn("Ignoring marker without a valid name for group '{}'.", normalized);
            return false;
        }
        List<MarkerData> group = cachedData.getOrCreateGroup(normalized);
        if (findMarker(group, marker.getName()) != null) {
            return false;
        }
        group.add(marker.copy());
        persist();
        return true;
    }

    public static synchronized boolean editMarker(@NotNull String groupId, @NotNull String markerName, @NotNull Consumer<MarkerData> editor) {
        String normalized = normalizeGroupId(groupId);
        Objects.requireNonNull(editor, "editor");
        ensureLoaded();
        List<MarkerData> group = cachedData.viewMarkers(normalized);
        MarkerData existing = findMarker(group, markerName);
        if (existing == null) {
            return false;
        }
        MarkerData workingCopy = existing.copy();
        editor.accept(workingCopy);
        if (!workingCopy.hasValidName()) {
            LOGGER.warn("Refusing to apply marker edit for group '{}' because the new name is invalid.", normalized);
            return false;
        }
        MarkerData conflict = findMarker(group, workingCopy.getName());
        if (conflict != null && conflict != existing) {
            return false;
        }
        existing.copyFrom(workingCopy);
        persist();
        return true;
    }

    public static synchronized boolean removeMarker(@NotNull String groupId, @NotNull String markerName) {
        String normalized = normalizeGroupId(groupId);
        ensureLoaded();
        List<MarkerData> group = cachedData.viewMarkers(normalized);
        MarkerData existing = findMarker(group, markerName);
        if (existing == null) {
            return false;
        }
        group.remove(existing);
        cachedData.removeGroupIfEmpty(normalized);
        persist();
        return true;
    }

    public static synchronized void clearGroup(@NotNull String groupId) {
        String normalized = normalizeGroupId(groupId);
        ensureLoaded();
        if (cachedData.dropGroup(normalized)) {
            persist();
        }
    }

    private static void ensureLoaded() {
        if (loaded) {
            return;
        }
        cachedData = readFromDisk();
        loaded = true;
    }

    private static MarkerStoreData readFromDisk() {
        if (!Files.exists(STORAGE_PATH)) {
            return new MarkerStoreData();
        }
        try (Reader reader = Files.newBufferedReader(STORAGE_PATH, StandardCharsets.UTF_8)) {
            MarkerStoreData data = GSON.fromJson(reader, MarkerStoreData.class);
            if (data == null) {
                return new MarkerStoreData();
            }
            data.sanitize();
            return data;
        } catch (IOException | JsonParseException ex) {
            LOGGER.warn("Failed to read stored markers", ex);
            return new MarkerStoreData();
        }
    }

    private static void persist() {
        try {
            if (STORAGE_PATH.getParent() != null) {
                Files.createDirectories(STORAGE_PATH.getParent());
            }
            try (Writer writer = Files.newBufferedWriter(STORAGE_PATH, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)) {
                GSON.toJson(cachedData, writer);
            }
        } catch (IOException ex) {
            LOGGER.warn("Failed to persist marker data", ex);
        }
    }

    @Nullable
    private static MarkerData findMarker(@NotNull List<MarkerData> group, @NotNull String name) {
        Objects.requireNonNull(name, "name");
        for (MarkerData marker : group) {
            if (marker == null) {
                continue;
            }
            if (marker.getName().equalsIgnoreCase(name)) {
                return marker;
            }
        }
        return null;
    }

    @NotNull
    private static String normalizeGroupId(@NotNull String groupId) {
        Objects.requireNonNull(groupId, "groupId");
        String trimmed = groupId.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("groupId cannot be blank");
        }
        return trimmed;
    }

    private static final class MarkerStoreData {

        private Map<String, List<MarkerData>> groups = new HashMap<>();

        private void sanitize() {
            if (this.groups == null) {
                this.groups = new HashMap<>();
                return;
            }
            Map<String, List<MarkerData>> sanitized = new HashMap<>();
            for (Map.Entry<String, List<MarkerData>> entry : this.groups.entrySet()) {
                String key = entry.getKey();
                if (key == null) {
                    continue;
                }
                String normalizedKey = key.trim();
                if (normalizedKey.isEmpty()) {
                    continue;
                }
                List<MarkerData> originalList = entry.getValue();
                List<MarkerData> cleaned = new ArrayList<>();
                if (originalList != null) {
                    for (MarkerData marker : originalList) {
                        if (marker == null || !marker.hasValidName()) {
                            continue;
                        }
                        marker.applyLegacyTextureFallback();
                        cleaned.add(marker);
                    }
                }
                sanitized.put(normalizedKey, cleaned);
            }
            this.groups = sanitized;
        }

        private List<MarkerData> viewMarkers(@NotNull String groupId) {
            List<MarkerData> markers = this.groups.get(groupId);
            if (markers == null) {
                return new ArrayList<>();
            }
            return markers;
        }

        private List<String> listGroupIds() {
            return new ArrayList<>(this.groups.keySet());
        }

        private List<MarkerData> getOrCreateGroup(@NotNull String groupId) {
            return this.groups.computeIfAbsent(groupId, ignored -> new ArrayList<>());
        }

        private void removeGroupIfEmpty(@NotNull String groupId) {
            List<MarkerData> markers = this.groups.get(groupId);
            if (markers != null && markers.isEmpty()) {
                this.groups.remove(groupId);
            }
        }

        private boolean dropGroup(@NotNull String groupId) {
            return this.groups.remove(groupId) != null;
        }
    }
}
