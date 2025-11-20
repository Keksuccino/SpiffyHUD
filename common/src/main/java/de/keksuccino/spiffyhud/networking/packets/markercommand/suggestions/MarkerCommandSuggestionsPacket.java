package de.keksuccino.spiffyhud.networking.packets.markercommand.suggestions;

import de.keksuccino.fancymenu.networking.Packet;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MarkerCommandSuggestionsPacket extends Packet {

    public List<String> groupSuggestions;

    @Override
    public boolean processPacket(@Nullable ServerPlayer sender) {
        if (sender == null) {
            return false;
        }
        return ServerSideMarkerCommandSuggestionsPacketLogic.handle(sender, this);
    }

}
