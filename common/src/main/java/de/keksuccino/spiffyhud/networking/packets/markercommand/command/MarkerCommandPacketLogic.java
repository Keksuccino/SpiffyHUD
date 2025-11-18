package de.keksuccino.spiffyhud.networking.packets.markercommand.command;

import de.keksuccino.spiffyhud.customization.actions.marker.MarkerActionConfig;
import de.keksuccino.spiffyhud.customization.actions.marker.MarkerRemovalConfig;
import de.keksuccino.spiffyhud.customization.marker.MarkerStorage;
import de.keksuccino.spiffyhud.networking.packets.markercommand.MarkerCommandEditField;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import java.util.EnumSet;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MarkerCommandPacketLogic {

    private static final Logger LOGGER = LogManager.getLogger();

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
        if (!config.hasValidTarget() || !config.hasValidMarkerName()) {
            LOGGER.error("[SPIFFYHUD] Marker add command missing required fields.");
            return false;
        }
        boolean success = MarkerStorage.addMarker(config.targetElementIdentifier, config.toMarkerData());
        if (success) {
            sendClientFeedback(packet, Component.translatable("spiffyhud.commands.marker.client.add.success", config.uniqueMarkerName, config.targetElementIdentifier), false);
        } else {
            sendClientFeedback(packet, Component.translatable("spiffyhud.commands.marker.client.add.failure", config.uniqueMarkerName, config.targetElementIdentifier), true);
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
        String markerName = config.getMarkerName();
        if (!config.hasValidTarget() || markerName.isBlank() || !config.hasValidMarkerName()) {
            LOGGER.error("[SPIFFYHUD] Marker edit command missing required field: target or marker name invalid");
            return false;
        }
        EnumSet<MarkerCommandEditField> overrides = sanitizeEditFields(packet.editFields);
        boolean success = MarkerStorage.editMarker(config.targetElementIdentifier, markerName, marker -> {
            marker.setName(config.uniqueMarkerName);
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
            sendClientFeedback(packet, Component.translatable("spiffyhud.commands.marker.client.edit.success", markerName, config.targetElementIdentifier), false);
        } else {
            sendClientFeedback(packet, Component.translatable("spiffyhud.commands.marker.client.edit.failure", markerName, config.targetElementIdentifier), true);
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
        boolean success = MarkerStorage.removeMarker(config.targetElementIdentifier, config.uniqueMarkerName);
        if (success) {
            sendClientFeedback(packet, Component.translatable("spiffyhud.commands.marker.client.remove.success", config.uniqueMarkerName, config.targetElementIdentifier), false);
        } else {
            sendClientFeedback(packet, Component.translatable("spiffyhud.commands.marker.client.remove.failure", config.uniqueMarkerName, config.targetElementIdentifier), true);
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

    private static void sendClientFeedback(@NotNull MarkerCommandPacket packet, MutableComponent message, boolean failure) {
        if (!packet.silenceClientFeedback) {
            packet.sendChatFeedback(message, failure);
        }
    }

}
