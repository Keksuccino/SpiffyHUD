package de.keksuccino.spiffyhud.util.level;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.TagValueOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Utility class for getting NBT data from entities as strings.
 */
public class EntityNbtUtils {

    /**
     * Gets NBT data from an entity as a string using the specified path.
     * For numeric values, returns just the number without type suffix.
     *
     * @param entity The entity to get data from
     * @param path NBT path (like 'Health' or 'Inventory[0].id')
     * @return The data at the specified path as a string, or null if not found
     */
    @Nullable
    public static String getNbtString(@NotNull Entity entity, @NotNull String path) {

        try {
            // Save entity data to a compound tag
            TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, entity.registryAccess());
            entity.saveWithoutId(output);

            // Parse the NBT path
            NbtPathArgument.NbtPath nbtPath = NbtPathArgument.nbtPath().parse(new StringReader(path));

            // Get the data at the path
            List<Tag> results = nbtPath.get(output.buildResult());

            if (results.isEmpty()) {
                return null;
            }

            Tag tag = results.get(0);

            // Special handling for numeric values to remove the type suffix
            if (tag instanceof NumericTag numericTag) {

                String tagString = tag.asString().orElse(null);
                Double tagDouble = numericTag.asDouble().orElse(null);
                Long tagLong = numericTag.asLong().orElse(null);

                if (tagString != null) {
                    // Handle different numeric types to remove suffixes like 'd', 'f', etc.
                    if (tagString.endsWith("d") ||
                            tagString.endsWith("f") ||
                            tagString.endsWith("b") ||
                            tagString.endsWith("s") ||
                            tagString.endsWith("L")) {

                        // For float/double, just use the numeric value without suffix
                        if (tagString.contains(".") && (tagDouble != null)) {
                            return String.valueOf(tagDouble);
                        } else if (tagLong != null) {
                            // For integers, bytes, shorts, longs
                            return String.valueOf(tagLong);
                        }
                    }
                }

            }

            // Default case - return as string
            return tag.asString().orElse(null);

        } catch (CommandSyntaxException ignore) {}
        return null;
    }

    /**
     * Gets all possible NBT paths in an entity.
     *
     * @param entity The entity to get paths from
     * @return A list of all NBT paths in the entity
     */
    @NotNull
    public static List<String> getAllNbtPaths(@NotNull Entity entity) {
        // Save entity data to a compound tag
        TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, entity.registryAccess());
        entity.saveWithoutId(output);

        // Collect all paths
        List<String> paths = new ArrayList<>();
        collectPaths("", output.buildResult(), paths);

        // Sort for better readability
        Collections.sort(paths);
        return paths;
    }

    /**
     * Recursively collects all paths in an NBT tag.
     */
    private static void collectPaths(String prefix, Tag tag, List<String> paths) {
        // Add the current path
        if (prefix != null && !prefix.isEmpty()) {
            paths.add(prefix);
        }
        // Recursively collect paths for compound tags
        if (tag instanceof CompoundTag) {
            CompoundTag compound = (CompoundTag) tag;

            for (String key : compound.keySet()) {
                String newPrefix = prefix.isEmpty() ? key : prefix + "." + key;
                collectPaths(newPrefix, compound.get(key), paths);
            }
        }
        // Recursively collect paths for list tags
        else if (tag instanceof ListTag) {
            ListTag list = (ListTag) tag;

            for (int i = 0; i < list.size(); i++) {
                String newPrefix = prefix + "[" + i + "]";
                collectPaths(newPrefix, list.get(i), paths);
            }
        }
        // Handle array tags
        else if (tag instanceof ByteArrayTag || tag instanceof IntArrayTag || tag instanceof LongArrayTag) {
            int size = 0;
            if (tag instanceof ByteArrayTag) {
                size = ((ByteArrayTag) tag).getAsByteArray().length;
            } else if (tag instanceof IntArrayTag) {
                size = ((IntArrayTag) tag).getAsIntArray().length;
            } else if (tag instanceof LongArrayTag) {
                size = ((LongArrayTag) tag).getAsLongArray().length;
            }

            for (int i = 0; i < size; i++) {
                paths.add(prefix + "[" + i + "]");
            }
        }
    }

}