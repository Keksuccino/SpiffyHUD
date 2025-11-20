package de.keksuccino.spiffyhud.networking.packets.markercommand.suggestions;

import de.keksuccino.spiffyhud.commands.SpiffyMarkerCommand;
import net.minecraft.server.level.ServerPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ServerSideMarkerCommandSuggestionsPacketLogic {

    private static final Logger LOGGER = LogManager.getLogger();

    protected static boolean handle(@NotNull ServerPlayer sender, @NotNull MarkerCommandSuggestionsPacket packet) {
        try {
            List<String> suggestions = new ArrayList<>(Objects.requireNonNullElse(packet.groupSuggestions, List.of()));
            SpiffyMarkerCommand.cacheSuggestions(sender.getUUID(), suggestions);
            return true;
        } catch (Exception ex) {
            LOGGER.error("[SPIFFYHUD] Failed to record marker command suggestions for {}.", sender.getScoreboardName(), ex);
        }
        return false;
    }

}
