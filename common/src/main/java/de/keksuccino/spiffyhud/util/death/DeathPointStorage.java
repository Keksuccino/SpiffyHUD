package de.keksuccino.spiffyhud.util.death;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import de.keksuccino.spiffyhud.SpiffyHud;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
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

/**
 * Persists the client's last known death point so the compass can keep showing it
 * until the player returns to that position.
 */
public final class DeathPointStorage {

    private static final Logger LOGGER = LogManager.getLogger("SpiffyHUD-DeathPointStorage");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path STORAGE_PATH = SpiffyHud.INSTANCE_DIR.toPath().resolve("death_point.json");
    private static final double CLEAR_DISTANCE_SQR = 5.0D * 5.0D;

    private static @Nullable StoredDeathPoint cachedPoint;
    private static boolean loaded;

    private DeathPointStorage() {
    }

    public static synchronized void recordDeath(@NotNull Level level, double x, double y, double z) {
        StoredDeathPoint point = new StoredDeathPoint(level.dimension().location().toString(), x, y, z);
        cachedPoint = point;
        loaded = true;
        write(point);
    }

    public static synchronized void clear() {
        dropCachedAndDelete();
    }

    public static synchronized @Nullable StoredDeathPoint get() {
        if (!loaded) {
            cachedPoint = read();
            loaded = true;
        }
        return cachedPoint;
    }

    public static synchronized void tick(@Nullable Player player) {
        if (player == null || player.isDeadOrDying()) {
            return;
        }
        StoredDeathPoint point = get();
        if (point == null) {
            return;
        }
        if (!point.dimensionMatches(player.level())) {
            return;
        }
        if (point.squaredDistanceTo(player.getX(), player.getY(), player.getZ()) <= CLEAR_DISTANCE_SQR) {
            dropCachedAndDelete();
        }
    }

    private static void dropCachedAndDelete() {
        cachedPoint = null;
        loaded = true;
        try {
            Files.deleteIfExists(STORAGE_PATH);
        } catch (IOException ex) {
            LOGGER.warn("Failed to delete stored death point", ex);
        }
    }

    private static void write(@NotNull StoredDeathPoint point) {
        try {
            if (STORAGE_PATH.getParent() != null) {
                Files.createDirectories(STORAGE_PATH.getParent());
            }
            try (Writer writer = Files.newBufferedWriter(STORAGE_PATH, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)) {
                GSON.toJson(point, writer);
            }
        } catch (IOException ex) {
            LOGGER.warn("Failed to store death point", ex);
        }
    }

    private static @Nullable StoredDeathPoint read() {
        if (!Files.exists(STORAGE_PATH)) {
            return null;
        }
        try (Reader reader = Files.newBufferedReader(STORAGE_PATH, StandardCharsets.UTF_8)) {
            StoredDeathPoint data = GSON.fromJson(reader, StoredDeathPoint.class);
            if (data != null && data.isValid()) {
                return data;
            }
        } catch (IOException | JsonParseException ex) {
            LOGGER.warn("Failed to read stored death point", ex);
        }
        return null;
    }

    public static final class StoredDeathPoint {
        private String dimension = "";
        private double x;
        private double y;
        private double z;

        public StoredDeathPoint() {
        }

        public StoredDeathPoint(@NotNull String dimension, double x, double y, double z) {
            this.dimension = dimension;
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public boolean isValid() {
            return this.dimension != null && !this.dimension.isBlank();
        }

        public boolean dimensionMatches(@NotNull Level level) {
            ResourceLocation key = this.dimensionKey();
            return key != null && key.equals(level.dimension().location());
        }

        @Nullable
        public ResourceLocation dimensionKey() {
            return this.dimension == null ? null : ResourceLocation.tryParse(this.dimension);
        }

        public double squaredDistanceTo(double px, double py, double pz) {
            double dx = this.x - px;
            double dy = this.y - py;
            double dz = this.z - pz;
            return dx * dx + dy * dy + dz * dz;
        }

        public double getX() {
            return this.x;
        }

        public double getY() {
            return this.y;
        }

        public double getZ() {
            return this.z;
        }
    }
}
