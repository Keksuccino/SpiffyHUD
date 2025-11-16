package de.keksuccino.spiffyhud.networking.packets.markercommand.command;

import de.keksuccino.spiffyhud.customization.actions.marker.MarkerActionConfig;
import de.keksuccino.spiffyhud.customization.actions.marker.MarkerRemovalConfig;
import de.keksuccino.spiffyhud.customization.marker.MarkerStorage;
import de.keksuccino.spiffyhud.networking.packets.markercommand.MarkerCommandEditField;
import de.keksuccino.spiffyhud.networking.packets.markercommand.MarkerCommandOperation;
import net.minecraft.network.chat.Component;
import java.util.EnumSet;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class MarkerCommandPacketLogic {

    private static final Logger LOGGER = LogManager.getLogger();

    private MarkerCommandPacketLogic() {
    }

    protected static boolean handle(@NotNull MarkerCommandPacket packet) {
        if (packet.operation == null) {
            LOGGER.error("[SPIFFYHUD] Received marker command packet without operation.");
            return false;
        }
        return switch (packet.operation) {
            case ADD -> handleAdd(packet);
            case EDIT -> handleEdit(packet);
            case REMOVE -> handleRemove(packet);
        };
    }

    private static boolean handleAdd(@NotNull MarkerCommandPacket packet) {
        MarkerActionConfig config = packet.actionConfig;
        if (config == null) {
            LOGGER.error("[SPIFFYHUD] Marker add command missing config.");
            return false;
        }
        config.normalize();
        if (!config.hasValidTarget() || !config.hasDisplayName()) {
            LOGGER.error("[SPIFFYHUD] Marker add command missing required fields.");
            return false;
        }
        boolean success = MarkerStorage.addMarker(config.targetElementIdentifier, config.toMarkerData());
        if (success) {
            packet.sendChatFeedback(Component.translatable("spiffyhud.commands.marker.client.add.success", config.displayName, config.targetElementIdentifier), false);
        } else {
            packet.sendChatFeedback(Component.translatable("spiffyhud.commands.marker.client.add.failure", config.displayName, config.targetElementIdentifier), true);
        }
        return success;
    }

    private static boolean handleEdit(@NotNull MarkerCommandPacket packet) {
        MarkerActionConfig config = packet.actionConfig;
        if (config == null) {
            LOGGER.error("[SPIFFYHUD] Marker edit command missing config.");
            return false;
        }
        config.normalize();
        String lookup = config.getLookupName();
        if (!config.hasValidTarget() || lookup.isBlank() || !config.hasDisplayName()) {
            LOGGER.error("[SPIFFYHUD] Marker edit command missing required fields.");
            return false;
        }
        EnumSet<MarkerCommandEditField> overrides = sanitizeEditFields(packet.editFields);
        boolean success = MarkerStorage.editMarker(config.targetElementIdentifier, lookup, marker -> {
            marker.setName(config.displayName);
            marker.setMarkerPosX(config.positionX);
            marker.setMarkerPosZ(config.positionZ);
            if (shouldUpdate(overrides, MarkerCommandEditField.COLOR)) {
                marker.setColor(config.colorHex);
            }
            if (shouldUpdate(overrides, MarkerCommandEditField.DOT_TEXTURE)) {
                marker.setDotTexture(config.dotTexture);
            }
            if (shouldUpdate(overrides, MarkerCommandEditField.NEEDLE_TEXTURE)) {
                marker.setNeedleTexture(config.needleTexture);
            }
            if (shouldUpdate(overrides, MarkerCommandEditField.SHOW_AS_NEEDLE)) {
                marker.setShowAsNeedle(config.showAsNeedle);
            }
        });
        if (success) {
            packet.sendChatFeedback(Component.translatable("spiffyhud.commands.marker.client.edit.success", lookup, config.targetElementIdentifier), false);
        } else {
            packet.sendChatFeedback(Component.translatable("spiffyhud.commands.marker.client.edit.failure", lookup, config.targetElementIdentifier), true);
        }
        return success;
    }

    private static boolean handleRemove(@NotNull MarkerCommandPacket packet) {
        MarkerRemovalConfig config = packet.removalConfig;
        if (config == null) {
            LOGGER.error("[SPIFFYHUD] Marker remove command missing config.");
            return false;
        }
        config.normalize();
        if (!config.isValid()) {
            LOGGER.error("[SPIFFYHUD] Marker remove command invalid config.");
            return false;
        }
        boolean success = MarkerStorage.removeMarker(config.targetElementIdentifier, config.markerName);
        if (success) {
            packet.sendChatFeedback(Component.translatable("spiffyhud.commands.marker.client.remove.success", config.markerName, config.targetElementIdentifier), false);
        } else {
            packet.sendChatFeedback(Component.translatable("spiffyhud.commands.marker.client.remove.failure", config.markerName, config.targetElementIdentifier), true);
        }
        return success;
    }

    private static EnumSet<MarkerCommandEditField> sanitizeEditFields(@Nullable Set<MarkerCommandEditField> fields) {
        if (fields == null || fields.isEmpty()) {
            return EnumSet.noneOf(MarkerCommandEditField.class);
        }
        return EnumSet.copyOf(fields);
    }

    private static boolean shouldUpdate(@NotNull EnumSet<MarkerCommandEditField> fields, @NotNull MarkerCommandEditField field) {
        return fields.contains(field);
    }
}
